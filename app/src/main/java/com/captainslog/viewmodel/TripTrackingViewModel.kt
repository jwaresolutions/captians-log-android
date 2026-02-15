package com.captainslog.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.GpsPointEntity
import com.captainslog.database.entities.TripEntity
import com.captainslog.repository.TripRepository
import com.captainslog.repository.TripStatistics
import com.captainslog.service.GpsTrackingService
import com.captainslog.sync.SyncOrchestrator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel for managing trip tracking and GPS service interaction.
 * Provides UI state and handles communication with the GPS tracking service.
 */
@HiltViewModel
class TripTrackingViewModel @Inject constructor(
    application: Application,
    private val repository: TripRepository,
    private val database: AppDatabase,
    private val syncOrchestrator: SyncOrchestrator
) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "TripTrackingViewModel"
    }

    private var gpsTrackingService: GpsTrackingService? = null
    private var serviceBound = false

    private val _isTracking = MutableStateFlow<Boolean>(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _currentTripId = MutableStateFlow<String?>(null)
    val currentTripId: StateFlow<String?> = _currentTripId.asStateFlow()

    private val _currentTrip = MutableStateFlow<TripEntity?>(null)
    val currentTrip: StateFlow<TripEntity?> = _currentTrip.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as GpsTrackingService.LocalBinder
            gpsTrackingService = binder.getService()
            serviceBound = true

            Log.d(TAG, "Service connected")

            // Update tracking state from service
            updateStateFromService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "Service disconnected")
            gpsTrackingService = null
            serviceBound = false
            _isTracking.value = false
        }
    }

    /**
     * Update ViewModel state from the service
     */
    private fun updateStateFromService() {
        gpsTrackingService?.let { service ->
            val isServiceTracking = service.isTracking()
            val serviceTripId = service.getCurrentTripId()

            Log.d(TAG, "Updating state from service - Tracking: $isServiceTracking, Trip ID: $serviceTripId")

            // Clean up orphaned trips first
            cleanupOrphanedTrips()

            _isTracking.value = isServiceTracking
            _currentTripId.value = serviceTripId

            // Load current trip if tracking
            serviceTripId?.let { tripId ->
                loadTrip(tripId)
            }
        }
    }

    /**
     * Clean up orphaned trips (trips with no endTime but service not running)
     */
    private fun cleanupOrphanedTrips() {
        viewModelScope.launch {
            try {
                // Get all trips without end time (active trips)
                val activeTrips = database.tripDao().getActiveTrips()

                Log.d(TAG, "Found ${activeTrips.size} active trips in database")

                // If service is not tracking but we have active trips, they are orphaned
                val isServiceTracking = gpsTrackingService?.isTracking() ?: false

                if (!isServiceTracking && activeTrips.isNotEmpty()) {
                    Log.w(TAG, "Found ${activeTrips.size} orphaned trips (service not running)")

                    // End all orphaned trips
                    activeTrips.forEach { trip ->
                        Log.d(TAG, "Ending orphaned trip: ${trip.id}")
                        val endedTrip = trip.copy(
                            endTime = Date(),
                            lastModified = Date()
                        )
                        database.tripDao().updateTrip(endedTrip)
                    }

                    Log.d(TAG, "Cleaned up ${activeTrips.size} orphaned trips")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error cleaning up orphaned trips", e)
            }
        }
    }

    /**
     * Force cleanup of all orphaned trips - more aggressive version
     */
    private suspend fun forceCleanupOrphanedTrips() {
        try {
            // Get all trips without end time (active trips)
            val activeTrips = database.tripDao().getActiveTrips()

            Log.d(TAG, "Force cleanup: Found ${activeTrips.size} active trips in database")

            if (activeTrips.isNotEmpty()) {
                Log.w(TAG, "Force cleanup: Ending all ${activeTrips.size} active trips")

                // End all active trips (assume they are orphaned)
                activeTrips.forEach { trip ->
                    Log.d(TAG, "Force cleanup: Ending trip ${trip.id}")
                    val endedTrip = trip.copy(
                        endTime = Date(),
                        lastModified = Date()
                    )
                    database.tripDao().updateTrip(endedTrip)
                }

                Log.d(TAG, "Force cleanup: Cleaned up ${activeTrips.size} trips")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in force cleanup of orphaned trips", e)
        }
    }

    /**
     * Bind to the GPS tracking service
     */
    fun bindToService(context: Context) {
        val intent = Intent(context, GpsTrackingService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    /**
     * Unbind from the GPS tracking service
     */
    fun unbindFromService(context: Context) {
        if (serviceBound) {
            context.unbindService(serviceConnection)
            serviceBound = false
        }
    }

    /**
     * Start a new trip with GPS tracking
     * Validates that the boat exists before starting the trip
     * Prevents starting a new trip if one is already active
     */
    fun startTrip(
        context: Context,
        boatId: String,
        waterType: String = GpsTrackingService.DEFAULT_WATER_TYPE,
        role: String = GpsTrackingService.DEFAULT_ROLE,
        bodyOfWater: String? = null,
        boundaryClassification: String? = null,
        distanceOffshore: Double? = null,
        updateIntervalMs: Long = GpsTrackingService.DEFAULT_UPDATE_INTERVAL_MS
    ) {
        Log.d(TAG, "startTrip() called - boat: $boatId, waterType: $waterType, role: $role")
        Log.d(TAG, "Current state - isTracking: ${_isTracking.value}, currentTripId: ${_currentTripId.value}")

        // Clear any previous error messages
        _errorMessage.value = null

        // Force cleanup of orphaned trips before checking if tracking
        viewModelScope.launch {
            forceCleanupOrphanedTrips()

            // After cleanup, check if service is actually tracking
            val isServiceTracking = gpsTrackingService?.isTracking() ?: false
            Log.d(TAG, "After cleanup - Service tracking: $isServiceTracking")

            // Update our state to match service state
            _isTracking.value = isServiceTracking
            if (!isServiceTracking) {
                _currentTripId.value = null
                _currentTrip.value = null
            }

            // Now check if a trip is already active
            if (_isTracking.value == true) {
                val errorMsg = "A trip is already in progress. Please stop the current trip first."
                Log.e(TAG, "Failed to start trip: $errorMsg")
                _errorMessage.value = errorMsg
                return@launch
            }

            // Continue with trip start logic
            startTripInternal(context, boatId, waterType, role, bodyOfWater, boundaryClassification, distanceOffshore, updateIntervalMs)
        }
    }

    /**
     * Internal method to start trip after validation
     */
    private suspend fun startTripInternal(
        context: Context,
        boatId: String,
        waterType: String,
        role: String,
        bodyOfWater: String?,
        boundaryClassification: String?,
        distanceOffshore: Double?,
        updateIntervalMs: Long
    ) {
        try {
            Log.d(TAG, "Looking up boat in database: $boatId")
            val boat = database.boatDao().getBoatById(boatId)

            if (boat == null) {
                val errorMsg = "Boat not found. Please select a valid boat."
                Log.e(TAG, "Failed to start trip: $errorMsg (boatId: $boatId)")
                _errorMessage.value = errorMsg
                return
            }

            Log.d(TAG, "Found boat: ${boat.name}, enabled: ${boat.enabled}")

            if (!boat.enabled) {
                val errorMsg = "Boat '${boat.name}' is disabled. Please enable it first."
                Log.e(TAG, "Failed to start trip: $errorMsg")
                _errorMessage.value = errorMsg
                return
            }

            Log.d(TAG, "Boat validated: ${boat.name} (${boat.id})")

            // Boat exists and is enabled, start the service
            val intent = Intent(context, GpsTrackingService::class.java).apply {
                action = GpsTrackingService.ACTION_START_TRIP
                putExtra(GpsTrackingService.EXTRA_BOAT_ID, boatId)
                putExtra(GpsTrackingService.EXTRA_WATER_TYPE, waterType)
                putExtra(GpsTrackingService.EXTRA_ROLE, role)
                putExtra(GpsTrackingService.EXTRA_BODY_OF_WATER, bodyOfWater)
                putExtra(GpsTrackingService.EXTRA_BOUNDARY_CLASSIFICATION, boundaryClassification)
                putExtra(GpsTrackingService.EXTRA_DISTANCE_OFFSHORE, distanceOffshore)
                putExtra(GpsTrackingService.EXTRA_UPDATE_INTERVAL, updateIntervalMs)
            }

            Log.d(TAG, "Starting GPS tracking service with intent: $intent")
            Log.d(TAG, "Intent extras - boatId: $boatId, waterType: $waterType, role: $role")

            try {
                context.startForegroundService(intent)
                Log.d(TAG, "startForegroundService() called successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start foreground service", e)
                _errorMessage.value = "Failed to start GPS service: ${e.message}"
                return
            }

            // Give the service a moment to start, then update state multiple times
            viewModelScope.launch {
                // First update after 500ms
                Log.d(TAG, "Waiting 500ms for service to start...")
                kotlinx.coroutines.delay(500)
                Log.d(TAG, "First state update from service")
                updateStateFromService()

                // Second update after 1 second
                kotlinx.coroutines.delay(500)
                Log.d(TAG, "Second state update from service")
                updateStateFromService()

                // Third update after 2 seconds
                kotlinx.coroutines.delay(1000)
                Log.d(TAG, "Third state update from service")
                updateStateFromService()

                // Fourth update after 3 seconds (final check)
                kotlinx.coroutines.delay(1000)
                Log.d(TAG, "Final state update from service")
                updateStateFromService()
            }

            Log.d(TAG, "Trip start requested successfully")

        } catch (e: Exception) {
            val errorMsg = "Failed to start trip: ${e.message}"
            Log.e(TAG, errorMsg, e)
            _errorMessage.value = errorMsg
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Manually refresh state from service (useful for debugging)
     */
    fun refreshState() {
        if (serviceBound) {
            updateStateFromService()
        }
    }

    /**
     * Manually force cleanup of orphaned trips (useful for debugging)
     */
    fun forceCleanup() {
        viewModelScope.launch {
            Log.d(TAG, "Manual force cleanup requested")
            forceCleanupOrphanedTrips()

            // Reset tracking state
            val isServiceTracking = gpsTrackingService?.isTracking() ?: false
            _isTracking.value = isServiceTracking
            if (!isServiceTracking) {
                _currentTripId.value = null
                _currentTrip.value = null
            }

            Log.d(TAG, "Manual cleanup complete - isTracking: ${_isTracking.value}")
        }
    }

    /**
     * Force stop everything - more aggressive than regular stopTrip
     */
    fun forceStopEverything(context: Context) {
        Log.d(TAG, "FORCE STOP EVERYTHING called")

        viewModelScope.launch {
            try {
                // Send force stop command to service
                val forceStopIntent = Intent(context, GpsTrackingService::class.java).apply {
                    action = "FORCE_STOP"
                }
                context.startService(forceStopIntent)

                // Also send regular stop command
                val stopIntent = Intent(context, GpsTrackingService::class.java).apply {
                    action = GpsTrackingService.ACTION_STOP_TRIP
                }
                context.startService(stopIntent)

                // Try to stop the service entirely
                val serviceIntent = Intent(context, GpsTrackingService::class.java)
                context.stopService(serviceIntent)

                // Force cleanup all trips in database
                forceCleanupOrphanedTrips()

                // Reset all state immediately
                _isTracking.value = false
                _currentTripId.value = null
                _currentTrip.value = null

                Log.d(TAG, "Force stop complete - everything should be stopped")

                // Wait a moment then verify service is stopped
                kotlinx.coroutines.delay(1000)

                // If service is still bound, check its state
                gpsTrackingService?.let { service ->
                    val stillTracking = service.isTracking()
                    Log.d(TAG, "After force stop - service still tracking: $stillTracking")
                    if (stillTracking) {
                        Log.w(TAG, "Service still tracking after force stop - calling forceStop directly")
                        service.forceStop()
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error in force stop", e)
            }
        }
    }

    /**
     * Stop the current trip
     */
    fun stopTrip(context: Context) {
        Log.d(TAG, "stopTrip() called")
        Log.d(TAG, "Current state - isTracking: ${_isTracking.value}, currentTripId: ${_currentTripId.value}")

        viewModelScope.launch {
            try {
                // Send stop command to service
                val intent = Intent(context, GpsTrackingService::class.java).apply {
                    action = GpsTrackingService.ACTION_STOP_TRIP
                }

                Log.d(TAG, "Sending stop command to service")
                context.startService(intent)

                // Wait a moment for service to process
                kotlinx.coroutines.delay(500)

                // Check if service actually stopped
                val serviceStillRunning = gpsTrackingService?.isTracking() ?: false
                Log.d(TAG, "After stop command - service still running: $serviceStillRunning")

                if (serviceStillRunning) {
                    Log.w(TAG, "Service didn't stop - trying force stop")
                    forceStopEverything(context)
                } else {
                    // Service stopped properly, clean up database
                    Log.d(TAG, "Service stopped properly - cleaning up database")
                    forceCleanupOrphanedTrips()

                    // Update state
                    _isTracking.value = false
                    _currentTripId.value = null
                    _currentTrip.value = null

                    Log.d(TAG, "Trip stop completed successfully")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error stopping trip", e)
                // Fallback to force stop
                forceStopEverything(context)
            }
        }
    }

    /**
     * Get all trips
     */
    fun getAllTrips(): Flow<List<TripEntity>> {
        return repository.getAllTrips()
    }

    /**
     * Get a specific trip by ID
     */
    fun loadTrip(tripId: String) {
        viewModelScope.launch {
            val trip = repository.getTripById(tripId)
            _currentTrip.value = trip
        }
    }

    /**
     * Get GPS points for a trip
     */
    fun getGpsPointsForTrip(tripId: String): Flow<List<GpsPointEntity>> {
        return repository.getGpsPointsForTrip(tripId)
    }

    /**
     * Calculate statistics for a trip
     */
    suspend fun calculateTripStatistics(tripId: String): TripStatistics {
        return repository.calculateTripStatistics(tripId)
    }

    /**
     * Update trip details (water type, role, manual data)
     */
    fun updateTrip(trip: TripEntity) {
        viewModelScope.launch {
            repository.updateTrip(trip)
            if (trip.id == _currentTripId.value) {
                _currentTrip.value = trip
            }
        }
    }

    /**
     * Update manual data for a trip
     */
    fun updateTripManualData(trip: TripEntity) {
        Log.d(TAG, "Updating manual data for trip ${trip.id}")
        viewModelScope.launch {
            try {
                repository.updateTrip(trip)
                if (trip.id == _currentTripId.value) {
                    _currentTrip.value = trip
                }

                // Trigger sync to upload manual data to backend
                syncOrchestrator.triggerImmediateSync()

                Log.d(TAG, "Manual data updated successfully for trip ${trip.id} and sync triggered")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating manual data for trip ${trip.id}", e)
                _errorMessage.value = "Failed to update manual data: ${e.message}"
            }
        }
    }

    /**
     * Delete a trip
     */
    fun deleteTrip(trip: TripEntity) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Note: Service unbinding should be done in the Activity/Fragment lifecycle
    }
}
