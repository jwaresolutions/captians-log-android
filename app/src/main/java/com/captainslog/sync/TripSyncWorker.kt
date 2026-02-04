package com.captainslog.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.network.models.CreateGpsPointRequest
import com.captainslog.network.models.CreateTripRequest
import com.captainslog.network.models.ManualData
import com.captainslog.repository.TripRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * WorkManager worker for syncing trips to the backend API.
 * Handles offline storage, sync queue, and conflict resolution.
 */
class TripSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val database = AppDatabase.getInstance(context)
    private val tripRepository = TripRepository(database, context)
    private val connectionManager = ConnectionManager.getInstance(context)
    private val conflictLogger = ConflictLogger(context)
    private val syncStatusManager = SyncStatusManager.getInstance(context)

    companion object {
        const val TAG = "TripSyncWorker"
        const val WORK_NAME = "trip_sync_work"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting trip sync...")
            
            // Report sync started
            syncStatusManager.reportSyncStarted()

            // Check if we have internet connection
            if (!connectionManager.hasInternetConnection()) {
                Log.d(TAG, "No internet connection, will retry later")
                syncStatusManager.reportSyncFailure("No internet connection")
                return@withContext Result.retry()
            }

            // Get all unsynced trips
            val unsyncedTrips = tripRepository.getUnsyncedTrips()
            Log.d(TAG, "Found ${unsyncedTrips.size} unsynced trips")

            if (unsyncedTrips.isEmpty()) {
                Log.d(TAG, "No trips to sync")
                syncStatusManager.reportSyncSuccess(0)
                return@withContext Result.success()
            }

            // Initialize connection manager
            connectionManager.initialize()
            val apiService = connectionManager.getApiService()

            var successCount = 0
            var failureCount = 0

            // Sync each trip
            for (trip in unsyncedTrips) {
                try {
                    Log.d(TAG, "Syncing trip ${trip.id}...")

                    // Get GPS points for this trip
                    val gpsPoints = tripRepository.getGpsPointsForTripSync(trip.id)
                    Log.d(TAG, "Trip has ${gpsPoints.size} GPS points")

                    // Convert GPS points to API format
                    val gpsPointRequests = gpsPoints.map { point ->
                        CreateGpsPointRequest(
                            latitude = point.latitude,
                            longitude = point.longitude,
                            altitude = point.altitude,
                            accuracy = point.accuracy,
                            speed = point.speed,
                            heading = point.heading,
                            timestamp = formatDate(point.timestamp)
                        )
                    }

                    // Create manual data if present
                    val manualData = if (trip.engineHours != null || 
                                         trip.fuelConsumed != null || 
                                         trip.weatherConditions != null || 
                                         trip.numberOfPassengers != null || 
                                         trip.destination != null) {
                        ManualData(
                            engineHours = trip.engineHours,
                            fuelConsumed = trip.fuelConsumed,
                            weatherConditions = trip.weatherConditions,
                            numberOfPassengers = trip.numberOfPassengers,
                            destination = trip.destination
                        )
                    } else {
                        null
                    }

                    // Create trip request
                    val tripRequest = CreateTripRequest(
                        boatId = trip.boatId,
                        startTime = formatDate(trip.startTime),
                        endTime = trip.endTime?.let { formatDate(it) },
                        waterType = trip.waterType,
                        role = trip.role,
                        gpsPoints = gpsPointRequests,
                        manualData = manualData
                    )

                    // Try to create trip on server
                    val response = apiService.createTrip(tripRequest)

                    if (response.isSuccessful) {
                        val serverTrip = response.body()?.data
                        Log.d(TAG, "Successfully synced trip ${trip.id}")

                        // Check for conflicts (server has newer data)
                        if (serverTrip != null) {
                            val serverModified = parseDate(serverTrip.updatedAt)
                            val localModified = trip.lastModified

                            if (serverModified != null && serverModified.after(localModified)) {
                                // Server has newer data - conflict detected
                                Log.w(TAG, "Conflict detected for trip ${trip.id}: server data is newer")
                                
                                // Log conflict for user review
                                conflictLogger.logConflict(
                                    tripId = trip.id,
                                    localModified = localModified,
                                    serverModified = serverModified,
                                    resolution = "Server data kept (newer timestamp)"
                                )

                                // Notify user about conflict
                                SyncNotificationHelper(applicationContext).showConflictNotification(
                                    tripId = trip.id,
                                    conflictMessage = "Trip data conflict resolved using server version (newer)"
                                )

                                // Update local trip with server data
                                updateLocalTripFromServer(trip.id, serverTrip)
                            }
                        }

                        // Mark trip as synced
                        tripRepository.markTripAsSynced(trip.id)
                        successCount++
                    } else {
                        Log.e(TAG, "Failed to sync trip ${trip.id}: ${response.code()} - ${response.message()}")
                        
                        // Check if it's a conflict error (409)
                        if (response.code() == 409) {
                            Log.w(TAG, "Conflict detected for trip ${trip.id}")
                            
                            // Try to fetch the server version
                            val getResponse = apiService.getTrip(trip.id)
                            if (getResponse.isSuccessful) {
                                val serverTrip = getResponse.body()?.data
                                if (serverTrip != null) {
                                    val serverModified = parseDate(serverTrip.updatedAt)
                                    val localModified = trip.lastModified

                                    // Use newest timestamp to resolve conflict
                                    if (serverModified != null && serverModified.after(localModified)) {
                                        Log.d(TAG, "Resolving conflict: server data is newer")
                                        
                                        conflictLogger.logConflict(
                                            tripId = trip.id,
                                            localModified = localModified,
                                            serverModified = serverModified,
                                            resolution = "Server data kept (newer timestamp)"
                                        )

                                        SyncNotificationHelper(applicationContext).showConflictNotification(
                                            tripId = trip.id,
                                            conflictMessage = "Trip conflict resolved using server version (newer)"
                                        )

                                        updateLocalTripFromServer(trip.id, serverTrip)
                                        tripRepository.markTripAsSynced(trip.id)
                                        successCount++
                                    } else {
                                        Log.d(TAG, "Resolving conflict: local data is newer")
                                        
                                        conflictLogger.logConflict(
                                            tripId = trip.id,
                                            localModified = localModified,
                                            serverModified = serverModified ?: Date(),
                                            resolution = "Local data kept (newer timestamp)"
                                        )

                                        SyncNotificationHelper(applicationContext).showConflictNotification(
                                            tripId = trip.id,
                                            conflictMessage = "Trip conflict resolved using local version (newer)"
                                        )

                                        // Try to update server with local data
                                        val updateRequest = com.captainslog.network.models.UpdateTripRequest(
                                            waterType = trip.waterType,
                                            role = trip.role,
                                            manualData = manualData
                                        )
                                        val updateResponse = apiService.updateTrip(trip.id, updateRequest)
                                        if (updateResponse.isSuccessful) {
                                            tripRepository.markTripAsSynced(trip.id)
                                            successCount++
                                        } else {
                                            failureCount++
                                        }
                                    }
                                }
                            } else {
                                failureCount++
                            }
                        } else {
                            failureCount++
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error syncing trip ${trip.id}: ${e.message}", e)
                    failureCount++
                }
            }

            Log.d(TAG, "Sync complete: $successCount succeeded, $failureCount failed")

            // Report sync result to status manager
            if (successCount > 0 || failureCount == 0) {
                syncStatusManager.reportSyncSuccess(successCount)
                return@withContext Result.success()
            } else {
                syncStatusManager.reportSyncFailure("Failed to sync $failureCount trip(s)")
                return@withContext Result.retry()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Fatal error during sync: ${e.message}", e)
            syncStatusManager.reportSyncFailure("Sync error: ${e.message}")
            return@withContext Result.retry()
        }
    }

    /**
     * Update local trip with server data
     */
    private suspend fun updateLocalTripFromServer(tripId: String, serverTrip: com.captainslog.network.models.TripResponse) {
        val localTrip = tripRepository.getTripById(tripId) ?: return

        val updatedTrip = localTrip.copy(
            boatId = serverTrip.boatId,
            startTime = parseDate(serverTrip.startTime) ?: localTrip.startTime,
            endTime = serverTrip.endTime?.let { parseDate(it) },
            waterType = serverTrip.waterType,
            role = serverTrip.role,
            engineHours = serverTrip.manualData?.engineHours,
            fuelConsumed = serverTrip.manualData?.fuelConsumed,
            weatherConditions = serverTrip.manualData?.weatherConditions,
            numberOfPassengers = serverTrip.manualData?.numberOfPassengers,
            destination = serverTrip.manualData?.destination,
            synced = true,
            lastModified = parseDate(serverTrip.updatedAt) ?: Date()
        )

        tripRepository.updateTrip(updatedTrip)
    }

    /**
     * Format date to ISO 8601 string
     */
    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(date)
    }

    /**
     * Parse ISO 8601 date string
     */
    private fun parseDate(dateString: String): Date? {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            format.timeZone = TimeZone.getTimeZone("UTC")
            format.parse(dateString)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing date: $dateString", e)
            null
        }
    }
}
