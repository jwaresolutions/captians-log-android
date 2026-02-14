package com.captainslog.repository

import android.content.Context
import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.GpsPointEntity
import com.captainslog.database.entities.TripEntity
import com.captainslog.sync.DataType
import com.captainslog.sync.SyncOrchestrator
import dagger.Lazy
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Repository for managing trip data and GPS points.
 * Provides a clean API for the UI layer to interact with trip data.
 * Automatically syncs changes when connected to internet.
 */
class TripRepository(
    private val database: AppDatabase,
    private val context: Context,
    private val connectionManager: ConnectionManager,
    private val syncOrchestratorLazy: Lazy<SyncOrchestrator>
) {
    private val syncOrchestrator: SyncOrchestrator get() = syncOrchestratorLazy.get()

    /**
     * Get all trips as a Flow for reactive updates
     */
    fun getAllTrips(): Flow<List<TripEntity>> {
        return database.tripDao().getAllTrips()
    }

    /**
     * Get a specific trip by ID
     */
    suspend fun getTripById(tripId: String): TripEntity? {
        return database.tripDao().getTripById(tripId)
    }

    /**
     * Get all GPS points for a specific trip
     */
    fun getGpsPointsForTrip(tripId: String): Flow<List<GpsPointEntity>> {
        return database.gpsPointDao().getGpsPointsForTrip(tripId)
    }

    /**
     * Get GPS points for a trip synchronously (for calculations)
     */
    suspend fun getGpsPointsForTripSync(tripId: String): List<GpsPointEntity> {
        return database.gpsPointDao().getGpsPointsForTripSync(tripId)
    }

    /**
     * Insert a new trip and sync immediately
     */
    suspend fun insertTrip(trip: TripEntity) {
        database.tripDao().insertTrip(trip)
        // Sync immediately if connected, queue if offline
        syncOrchestrator.syncEntity(DataType.TRIPS,trip.id)
    }

    /**
     * Update an existing trip and sync immediately
     */
    suspend fun updateTrip(trip: TripEntity) {
        database.tripDao().updateTrip(trip)
        // Sync immediately if connected, queue if offline
        syncOrchestrator.syncEntity(DataType.TRIPS,trip.id)
    }

    /**
     * Delete a trip
     */
    suspend fun deleteTrip(trip: TripEntity) {
        database.tripDao().deleteTrip(trip)
    }

    /**
     * Get all unsynced trips for synchronization
     */
    suspend fun getUnsyncedTrips(): List<TripEntity> {
        return database.tripDao().getUnsyncedTrips()
    }

    /**
     * Mark a trip as synced
     */
    suspend fun markTripAsSynced(tripId: String) {
        database.tripDao().markAsSynced(tripId)
    }

    /**
     * Sync trips from API to local database
     */
    suspend fun syncTripsFromApi(): Result<Unit> {
        return try {
            val apiService = connectionManager.getApiService()
            val response = apiService.getTrips()

            if (response.isSuccessful && response.body() != null) {
                val apiTrips = response.body()!!.data
                Log.d("TripRepository", "Received ${apiTrips.size} trips from API")

                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }

                val tripEntities = apiTrips.map { trip ->
                    TripEntity(
                        id = trip.id,
                        boatId = trip.boatId,
                        startTime = dateFormat.parse(trip.startTime) ?: java.util.Date(),
                        endTime = trip.endTime?.let { dateFormat.parse(it) },
                        waterType = trip.waterType,
                        role = trip.role,
                        engineHours = trip.manualData?.engineHours,
                        fuelConsumed = trip.manualData?.fuelConsumed,
                        weatherConditions = trip.manualData?.weatherConditions,
                        numberOfPassengers = trip.manualData?.numberOfPassengers,
                        destination = trip.manualData?.destination,
                        synced = true,
                        lastModified = dateFormat.parse(trip.updatedAt) ?: java.util.Date(),
                        createdAt = dateFormat.parse(trip.createdAt) ?: java.util.Date()
                    )
                }

                database.tripDao().insertTrips(tripEntities)
                Log.d("TripRepository", "Upserted ${tripEntities.size} trips from server")
            } else {
                Log.w("TripRepository", "Failed to fetch trips: ${response.code()}")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TripRepository", "Error syncing trips from API", e)
            Result.failure(e)
        }
    }

    /**
     * Sync unsynced trips to API
     */
    suspend fun syncTripsToApi(): Result<Unit> {
        return try {
            val unsyncedTrips = getUnsyncedTrips()
            for (trip in unsyncedTrips) {
                syncOrchestrator.syncEntity(DataType.TRIPS,trip.id)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Insert a GPS point
     */
    suspend fun insertGpsPoint(gpsPoint: GpsPointEntity) {
        database.gpsPointDao().insertGpsPoint(gpsPoint)
    }

    /**
     * Insert multiple GPS points
     */
    suspend fun insertGpsPoints(gpsPoints: List<GpsPointEntity>) {
        database.gpsPointDao().insertGpsPoints(gpsPoints)
    }

    /**
     * Calculate trip statistics from GPS points
     */
    suspend fun calculateTripStatistics(tripId: String): TripStatistics {
        val gpsPoints = getGpsPointsForTripSync(tripId)
        
        if (gpsPoints.isEmpty()) {
            return TripStatistics(
                durationSeconds = 0,
                distanceMeters = 0.0,
                averageSpeedKnots = 0.0,
                maxSpeedKnots = 0.0
            )
        }

        // Sort by timestamp to ensure correct order for distance calculation
        val sortedPoints = gpsPoints.sortedBy { it.timestamp }

        // Calculate duration
        val startTime = sortedPoints.first().timestamp.time
        val endTime = sortedPoints.last().timestamp.time
        val durationSeconds = (endTime - startTime) / 1000

        // Calculate distance using Haversine formula
        var totalDistance = 0.0
        for (i in 1 until sortedPoints.size) {
            val prev = sortedPoints[i - 1]
            val curr = sortedPoints[i]
            totalDistance += calculateDistance(
                prev.latitude, prev.longitude,
                curr.latitude, curr.longitude
            )
        }

        // Calculate speeds
        val speeds = sortedPoints.mapNotNull { it.speed?.toDouble() }
        val averageSpeed = if (speeds.isNotEmpty()) speeds.average() else 0.0
        val maxSpeed = speeds.maxOrNull() ?: 0.0

        // Convert m/s to knots (1 m/s = 1.94384 knots)
        val averageSpeedKnots = averageSpeed * 1.94384
        val maxSpeedKnots = maxSpeed * 1.94384

        return TripStatistics(
            durationSeconds = durationSeconds,
            distanceMeters = totalDistance,
            averageSpeedKnots = averageSpeedKnots,
            maxSpeedKnots = maxSpeedKnots
        )
    }

    /**
     * Calculate distance between two GPS coordinates using Haversine formula
     * Returns distance in meters
     */
    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371000.0 // meters
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return earthRadius * c
    }
}

/**
 * Data class for trip statistics
 */
data class TripStatistics(
    val durationSeconds: Long,
    val distanceMeters: Double,
    val averageSpeedKnots: Double,
    val maxSpeedKnots: Double
)
