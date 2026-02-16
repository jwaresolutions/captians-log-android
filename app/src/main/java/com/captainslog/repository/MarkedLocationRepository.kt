package com.captainslog.repository

import android.util.Log
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.MarkedLocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.math.*

class MarkedLocationRepository(
    private val database: AppDatabase
) {
    companion object {
        private const val TAG = "MarkedLocationRepository"
        private const val EARTH_RADIUS_METERS = 6371000.0
    }

    /**
     * Get all marked locations from local database
     */
    fun getAllMarkedLocations(): Flow<List<MarkedLocationEntity>> =
        database.markedLocationDao().getAllMarkedLocations()

    /**
     * Get marked locations by category
     */
    fun getMarkedLocationsByCategory(category: String): Flow<List<MarkedLocationEntity>> =
        database.markedLocationDao().getMarkedLocationsByCategory(category)

    /**
     * Search marked locations
     */
    fun searchMarkedLocations(query: String): Flow<List<MarkedLocationEntity>> =
        database.markedLocationDao().searchMarkedLocations(query)

    /**
     * Get marked locations with distance from a reference point
     */
    fun getMarkedLocationsWithDistance(
        referenceLat: Double,
        referenceLon: Double
    ): Flow<List<MarkedLocationWithDistance>> =
        database.markedLocationDao().getAllMarkedLocations().map { locations ->
            locations.map { location ->
                val distance = calculateDistance(
                    referenceLat, referenceLon,
                    location.latitude, location.longitude
                )
                MarkedLocationWithDistance(
                    location = location,
                    distanceMeters = distance
                )
            }.sortedBy { it.distanceMeters }
        }

    /**
     * Get nearby marked locations within radius
     */
    fun getNearbyMarkedLocations(
        centerLat: Double,
        centerLon: Double,
        radiusMeters: Double
    ): Flow<List<MarkedLocationWithDistance>> =
        getMarkedLocationsWithDistance(centerLat, centerLon).map { locations ->
            locations.filter { it.distanceMeters <= radiusMeters }
        }

    /**
     * Create a new marked location
     */
    suspend fun createMarkedLocation(
        name: String,
        latitude: Double,
        longitude: Double,
        category: String,
        notes: String? = null,
        tags: List<String> = emptyList()
    ): Result<MarkedLocationEntity> {
        return try {
            val location = MarkedLocationEntity(
                name = name,
                latitude = latitude,
                longitude = longitude,
                category = category,
                notes = notes,
                tags = tags.joinToString(","),
                synced = false
            )

            // Save locally
            database.markedLocationDao().insertMarkedLocation(location)
            Log.d(TAG, "Marked location saved locally: ${location.name}")
            Result.success(location)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating marked location", e)
            Result.failure(e)
        }
    }

    /**
     * Update a marked location
     */
    suspend fun updateMarkedLocation(
        id: String,
        name: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        category: String? = null,
        notes: String? = null,
        tags: List<String>? = null
    ): Result<MarkedLocationEntity> {
        return try {
            val existingLocation = database.markedLocationDao().getMarkedLocationById(id)
                ?: return Result.failure(Exception("Marked location not found"))

            val updatedLocation = existingLocation.copy(
                name = name ?: existingLocation.name,
                latitude = latitude ?: existingLocation.latitude,
                longitude = longitude ?: existingLocation.longitude,
                category = category ?: existingLocation.category,
                notes = notes ?: existingLocation.notes,
                tags = tags?.joinToString(",") ?: existingLocation.tags,
                synced = false,
                lastModified = Date()
            )

            // Update locally
            database.markedLocationDao().updateMarkedLocation(updatedLocation)
            Log.d(TAG, "Marked location updated locally: $id")
            Result.success(updatedLocation)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating marked location", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a marked location
     */
    suspend fun deleteMarkedLocation(id: String): Result<Unit> {
        return try {
            // Delete locally
            database.markedLocationDao().deleteMarkedLocationById(id)
            Log.d(TAG, "Marked location deleted locally: $id")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting marked location", e)
            Result.failure(e)
        }
    }

    /**
     * Calculate distance between two GPS coordinates using Haversine formula
     */
    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_METERS * c
    }

    /**
     * Get all available categories
     */
    suspend fun getAllCategories(): List<String> {
        return try {
            database.markedLocationDao().getAllCategories()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting categories", e)
            emptyList()
        }
    }

    /**
     * Get all available tags
     */
    suspend fun getAllTags(): List<String> {
        return try {
            val tagStrings = database.markedLocationDao().getAllTags()
            tagStrings.flatMap { tagString ->
                if (tagString.isEmpty()) emptyList() else tagString.split(",")
            }.distinct().sorted()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tags", e)
            emptyList()
        }
    }
}

/**
 * Data class for marked location with distance
 */
data class MarkedLocationWithDistance(
    val location: MarkedLocationEntity,
    val distanceMeters: Double
)
