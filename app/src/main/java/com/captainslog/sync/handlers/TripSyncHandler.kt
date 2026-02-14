package com.captainslog.sync.handlers

import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.network.models.CreateGpsPointRequest
import com.captainslog.network.models.CreateTripRequest
import com.captainslog.network.models.ManualData
import com.captainslog.repository.TripRepository
import com.captainslog.sync.ConflictLogger
import com.captainslog.sync.DataType
import com.captainslog.sync.HandlerSyncResult
import com.captainslog.sync.SyncHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripSyncHandler @Inject constructor(
    private val tripRepository: TripRepository,
    private val connectionManager: ConnectionManager,
    private val conflictLogger: ConflictLogger,
    private val database: AppDatabase
) : SyncHandler {

    companion object {
        private const val TAG = "TripSyncHandler"
    }

    override val dataType = DataType.TRIPS

    override suspend fun syncFromServer(): HandlerSyncResult {
        return try {
            Log.d(TAG, "Syncing trips from server...")
            val result = tripRepository.syncTripsFromApi()
            if (result.isFailure) {
                Log.w(TAG, "Failed to sync trips from server: ${result.exceptionOrNull()?.message}")
                HandlerSyncResult(success = false, errors = listOf(result.exceptionOrNull()?.message ?: "Unknown error"))
            } else {
                Log.d(TAG, "Trip sync from server completed")
                HandlerSyncResult(success = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing trips from server", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }

    override suspend fun syncToServer(): HandlerSyncResult {
        return try {
            Log.d(TAG, "Syncing trips to server...")
            val result = tripRepository.syncTripsToApi()
            if (result.isFailure) {
                Log.w(TAG, "Failed to sync trips to server: ${result.exceptionOrNull()?.message}")
                HandlerSyncResult(success = false, errors = listOf(result.exceptionOrNull()?.message ?: "Unknown error"))
            } else {
                Log.d(TAG, "Trip sync to server completed")
                HandlerSyncResult(success = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing trips to server", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }

    override suspend fun syncEntity(entityId: String): HandlerSyncResult {
        return try {
            val trip = database.tripDao().getTripById(entityId) ?: return HandlerSyncResult(
                success = false, errors = listOf("Trip not found: $entityId")
            )
            val gpsPoints = database.gpsPointDao().getGpsPointsForTripSync(entityId)
            val apiService = connectionManager.getApiService()

            val manualData = ManualData(
                engineHours = trip.engineHours,
                fuelConsumed = trip.fuelConsumed,
                weatherConditions = trip.weatherConditions,
                numberOfPassengers = trip.numberOfPassengers,
                destination = trip.destination
            )

            val tripRequest = CreateTripRequest(
                boatId = trip.boatId,
                startTime = trip.startTime.toInstant().toString(),
                endTime = trip.endTime?.toInstant()?.toString(),
                waterType = trip.waterType,
                role = trip.role,
                manualData = manualData,
                gpsPoints = gpsPoints.map { gps ->
                    CreateGpsPointRequest(
                        latitude = gps.latitude,
                        longitude = gps.longitude,
                        altitude = gps.altitude,
                        accuracy = gps.accuracy,
                        speed = gps.speed,
                        heading = gps.heading,
                        timestamp = gps.timestamp.toInstant().toString()
                    )
                }
            )

            val response = apiService.createTrip(tripRequest)
            if (response.isSuccessful) {
                database.tripDao().markAsSynced(entityId)
                Log.d(TAG, "Trip synced successfully: $entityId")
                HandlerSyncResult(success = true, syncedCount = 1)
            } else {
                Log.e(TAG, "Failed to sync trip: ${response.code()} - ${response.message()}")
                HandlerSyncResult(success = false, errors = listOf("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing trip entity: $entityId", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }
}
