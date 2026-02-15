package com.captainslog.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.captainslog.MainActivity
import com.captainslog.R
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.GpsPointEntity
import com.captainslog.database.entities.TripEntity
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.Date
import javax.inject.Inject

/**
 * Foreground service for continuous GPS tracking during active trips.
 *
 * Features:
 * - Persistent notification while tracking
 * - Configurable GPS update interval (default 5 seconds)
 * - Local storage of GPS points in Room database
 * - Wake lock management for continuous tracking
 * - Start/stop trip functionality
 */
@AndroidEntryPoint
class GpsTrackingService : Service() {

    companion object {
        private const val TAG = "GpsTrackingService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "gps_tracking_channel"
        private const val CHANNEL_NAME = "GPS Tracking"
        
        const val ACTION_START_TRIP = "com.captainslog.START_TRIP"
        const val ACTION_STOP_TRIP = "com.captainslog.STOP_TRIP"
        
        const val EXTRA_BOAT_ID = "boat_id"
        const val EXTRA_WATER_TYPE = "water_type"
        const val EXTRA_ROLE = "role"
        const val EXTRA_BODY_OF_WATER = "body_of_water"
        const val EXTRA_BOUNDARY_CLASSIFICATION = "boundary_classification"
        const val EXTRA_DISTANCE_OFFSHORE = "distance_offshore"
        const val EXTRA_UPDATE_INTERVAL = "update_interval"
        
        const val DEFAULT_UPDATE_INTERVAL_MS = 5000L // 5 seconds
        const val DEFAULT_WATER_TYPE = "inland"
        const val DEFAULT_ROLE = "master"
    }

    @Inject
    lateinit var database: AppDatabase

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var notificationManager: NotificationManager
    
    private var wakeLock: PowerManager.WakeLock? = null
    private var currentTripId: String? = null
    private var isTracking = false
    private var updateIntervalMs = DEFAULT_UPDATE_INTERVAL_MS
    
    inner class LocalBinder : Binder() {
        fun getService(): GpsTrackingService = this@GpsTrackingService
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "Service onCreate")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()
        setupLocationCallback()

        Log.d(TAG, "Service initialized successfully")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: action=${intent?.action}, startId=$startId")
        
        when (intent?.action) {
            ACTION_START_TRIP -> {
                val boatId = intent.getStringExtra(EXTRA_BOAT_ID)
                val waterType = intent.getStringExtra(EXTRA_WATER_TYPE) ?: DEFAULT_WATER_TYPE
                val role = intent.getStringExtra(EXTRA_ROLE) ?: DEFAULT_ROLE
                val bodyOfWater = intent.getStringExtra(EXTRA_BODY_OF_WATER)
                val boundaryClassification = intent.getStringExtra(EXTRA_BOUNDARY_CLASSIFICATION)
                val distanceOffshore = if (intent.hasExtra(EXTRA_DISTANCE_OFFSHORE)) {
                    intent.getDoubleExtra(EXTRA_DISTANCE_OFFSHORE, 0.0)
                } else null
                updateIntervalMs = intent.getLongExtra(EXTRA_UPDATE_INTERVAL, DEFAULT_UPDATE_INTERVAL_MS)

                Log.d(TAG, "START_TRIP: boatId=$boatId, waterType=$waterType, role=$role, interval=$updateIntervalMs")

                if (boatId != null) {
                    startTrip(boatId, waterType, role, bodyOfWater, boundaryClassification, distanceOffshore)
                } else {
                    Log.e(TAG, "Cannot start trip: boatId is null")
                    stopSelf()
                }
            }
            ACTION_STOP_TRIP -> {
                Log.d(TAG, "STOP_TRIP action received")
                stopTrip()
                return START_NOT_STICKY // Don't restart after stopping
            }
            "FORCE_STOP" -> {
                Log.d(TAG, "FORCE_STOP action received")
                forceStop()
                return START_NOT_STICKY
            }
            else -> {
                Log.w(TAG, "Unknown action: ${intent?.action}")
                if (intent?.action == null) {
                    // Service restarted without intent - stop it
                    Log.d(TAG, "Service restarted without intent - stopping")
                    stopSelf()
                    return START_NOT_STICKY
                }
            }
        }
        
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy() called")
        super.onDestroy()
        
        // Force cleanup everything
        try {
            stopTracking()
            releaseWakeLock()
            currentTripId = null
            isTracking = false
            serviceScope.cancel()
            Log.d(TAG, "Service destroyed and cleaned up")
        } catch (e: Exception) {
            Log.e(TAG, "Error in onDestroy", e)
        }
    }
    
    /**
     * Force stop everything - more aggressive than regular stopTrip
     */
    fun forceStop() {
        Log.d(TAG, "forceStop() called - forcing immediate shutdown")
        
        try {
            // Stop location updates immediately
            stopTracking()
            Log.d(TAG, "Location updates stopped")
            
            // Release wake lock
            releaseWakeLock()
            Log.d(TAG, "Wake lock released")
            
            // End any active trip in database
            currentTripId?.let { tripId ->
                serviceScope.launch {
                    try {
                        val trip = database.tripDao().getTripById(tripId)
                        if (trip != null && trip.endTime == null) {
                            val endedTrip = trip.copy(
                                endTime = Date(),
                                lastModified = Date()
                            )
                            database.tripDao().updateTrip(endedTrip)
                            Log.d(TAG, "Force ended trip: $tripId")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error force ending trip", e)
                    }
                }
            }
            
            // Reset state
            currentTripId = null
            isTracking = false
            
            // Stop foreground and service
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            
            Log.d(TAG, "Force stop completed")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in force stop", e)
            
            // Last resort - just stop the service
            try {
                currentTripId = null
                isTracking = false
                stopSelf()
            } catch (lastResortError: Exception) {
                Log.e(TAG, "Error in last resort stop", lastResortError)
            }
        }
    }

    /**
     * Start a new trip and begin GPS tracking
     */
    fun startTrip(
        boatId: String,
        waterType: String = DEFAULT_WATER_TYPE,
        role: String = DEFAULT_ROLE,
        bodyOfWater: String? = null,
        boundaryClassification: String? = null,
        distanceOffshore: Double? = null
    ) {
        if (isTracking) {
            Log.w(TAG, "Already tracking a trip, ignoring start request")
            return // Already tracking
        }
        
        Log.d(TAG, "Starting trip for boat: $boatId")
        
        serviceScope.launch {
            try {
                // Validate boat exists in database
                val boat = database.boatDao().getBoatById(boatId)
                if (boat == null) {
                    Log.e(TAG, "Cannot start trip: Boat not found in database (boatId: $boatId)")
                    stopSelf()
                    return@launch
                }
                
                if (!boat.enabled) {
                    Log.e(TAG, "Cannot start trip: Boat '${boat.name}' is disabled")
                    stopSelf()
                    return@launch
                }
                
                Log.d(TAG, "Boat validated: ${boat.name} (${boat.id})")
                
                // Create new trip in database
                val trip = TripEntity(
                    boatId = boatId,
                    startTime = Date(),
                    waterType = waterType,
                    role = role,
                    bodyOfWater = bodyOfWater,
                    boundaryClassification = boundaryClassification,
                    distanceOffshore = distanceOffshore
                )
                database.tripDao().insertTrip(trip)
                currentTripId = trip.id
                
                Log.d(TAG, "Trip created in database: ${trip.id}")
                
                // Start foreground service with notification
                startForeground(NOTIFICATION_ID, createNotification("Trip in progress for ${boat.name}"))
                Log.d(TAG, "Foreground service started")
                
                // Acquire wake lock to keep CPU running
                acquireWakeLock()
                Log.d(TAG, "Wake lock acquired")
                
                // Start location updates
                startLocationUpdates()
                Log.d(TAG, "Location updates started")
                
                isTracking = true
                Log.d(TAG, "Trip started successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error starting trip", e)
                e.printStackTrace()
                stopSelf()
            }
        }
    }

    /**
     * Stop the current trip and end GPS tracking
     */
    fun stopTrip() {
        Log.d(TAG, "stopTrip() called - isTracking: $isTracking, currentTripId: $currentTripId")
        
        serviceScope.launch {
            try {
                // Update trip with end time if we have one
                currentTripId?.let { tripId ->
                    Log.d(TAG, "Updating trip $tripId with end time")
                    val trip = database.tripDao().getTripById(tripId)
                    if (trip != null) {
                        val updatedTrip = trip.copy(
                            endTime = Date(),
                            lastModified = Date()
                        )
                        database.tripDao().updateTrip(updatedTrip)
                        Log.d(TAG, "Trip updated with end time: ${updatedTrip.endTime}")
                    } else {
                        Log.e(TAG, "Trip not found in database: $tripId")
                    }
                }
                
                // Stop tracking immediately
                Log.d(TAG, "Stopping location updates")
                stopTracking()
                
                // Release wake lock
                Log.d(TAG, "Releasing wake lock")
                releaseWakeLock()
                
                // Reset state
                currentTripId = null
                isTracking = false
                
                Log.d(TAG, "Service state reset - stopping foreground service")
                
                // Stop foreground service and remove notification
                try {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    Log.d(TAG, "Foreground service stopped")
                } catch (e: Exception) {
                    Log.e(TAG, "Error stopping foreground service", e)
                }
                
                // Stop the service entirely
                try {
                    stopSelf()
                    Log.d(TAG, "Service stopped via stopSelf()")
                } catch (e: Exception) {
                    Log.e(TAG, "Error calling stopSelf()", e)
                }
                
                Log.d(TAG, "Trip stop completed successfully")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping trip", e)
                e.printStackTrace()
                
                // Force stop even if there was an error
                try {
                    stopTracking()
                    releaseWakeLock()
                    currentTripId = null
                    isTracking = false
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    Log.d(TAG, "Force stop completed after error")
                } catch (forceStopError: Exception) {
                    Log.e(TAG, "Error in force stop", forceStopError)
                }
            }
        }
    }

    /**
     * Get the current trip ID if tracking is active
     */
    fun getCurrentTripId(): String? = currentTripId

    /**
     * Check if GPS tracking is currently active
     */
    fun isTracking(): Boolean = isTracking

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    onLocationUpdate(location)
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Location permission not granted")
            return
        }

        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                updateIntervalMs
            ).apply {
                setMinUpdateIntervalMillis(updateIntervalMs)
                setWaitForAccurateLocation(false)
            }.build()

            Log.d(TAG, "Requesting location updates with interval: ${updateIntervalMs}ms")
            
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error starting location updates", e)
        }
    }

    private fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun onLocationUpdate(location: Location) {
        val tripId = currentTripId ?: return
        
        Log.d(TAG, "Location update: lat=${location.latitude}, lon=${location.longitude}, " +
                "accuracy=${location.accuracy}, speed=${location.speed}")
        
        serviceScope.launch {
            try {
                val gpsPoint = GpsPointEntity(
                    tripId = tripId,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    altitude = if (location.hasAltitude()) location.altitude else null,
                    accuracy = if (location.hasAccuracy()) location.accuracy else null,
                    speed = if (location.hasSpeed()) location.speed else null,
                    heading = if (location.hasBearing()) location.bearing else null,
                    timestamp = Date(location.time)
                )
                
                database.gpsPointDao().insertGpsPoint(gpsPoint)
                Log.d(TAG, "GPS point saved to database")
                
                // Update notification with current location info
                updateNotification(location)
            } catch (e: Exception) {
                Log.e(TAG, "Error saving GPS point", e)
                e.printStackTrace()
            }
        }
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "BoatTracking::GpsTrackingWakeLock"
        ).apply {
            acquire(10 * 60 * 60 * 1000L) // 10 hours max
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        wakeLock = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notification for GPS tracking during trips"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(contentText: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("GPS Tracking Active")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun updateNotification(location: Location) {
        val contentText = String.format(
            "Lat: %.6f, Lon: %.6f, Speed: %.1f knots",
            location.latitude,
            location.longitude,
            if (location.hasSpeed()) location.speed * 1.94384f else 0f // m/s to knots
        )
        
        notificationManager.notify(NOTIFICATION_ID, createNotification(contentText))
    }
}
