package com.captainslog.repository

import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.GpsPointEntity
import com.captainslog.database.entities.TripEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing trip data and GPS points.
 * Provides a clean API for the UI layer to interact with trip data.
 */
class TripRepository(
    private val database: AppDatabase
) {
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
     * Insert a new trip
     */
    suspend fun insertTrip(trip: TripEntity) {
        database.tripDao().insertTrip(trip)
    }

    /**
     * Update an existing trip
     */
    suspend fun updateTrip(trip: TripEntity) {
        database.tripDao().updateTrip(trip)
    }

    /**
     * Delete a trip
     */
    suspend fun deleteTrip(trip: TripEntity) {
        database.tripDao().deleteTrip(trip)
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
