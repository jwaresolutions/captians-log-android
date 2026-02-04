package com.captainslog.repository

import android.util.Log
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.MarkedLocationEntity
import com.captainslog.connection.ConnectionManager
import com.captainslog.network.ApiService
import com.captainslog.network.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

class MarkedLocationRepository(
    private val database: AppDatabase,
    private val connectionManager: ConnectionManager
) {
    companion object {
        private const val TAG = "MarkedLocationRepository"
        private const val EARTH_RADIUS_METERS = 6371000.0
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
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

            // Save locally first
            database.markedLocationDao().insertMarkedLocation(location)
            Log.d(TAG, "Marked location saved locally: ${location.name}")

            // Try to sync to API
            try {
                val request = CreateMarkedLocationRequest(
                    name = name,
                    latitude = latitude,
                    longitude = longitude,
                    category = category,
                    notes = notes,
                    tags = tags
                )
                
                val apiService = connectionManager.getApiService()
                val response = apiService.createMarkedLocation(request)
                if (response.isSuccessful && response.body() != null) {
                    val apiLocation = response.body()!!
                    val syncedLocation = location.copy(
                        id = apiLocation.id,
                        synced = true
                    )
                    database.markedLocationDao().insertMarkedLocation(syncedLocation)
                    Log.d(TAG, "Marked location synced to API: ${apiLocation.id}")
                    Result.success(syncedLocation)
                } else {
                    Log.w(TAG, "Failed to sync marked location to API: ${response.code()}")
                    Result.success(location)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Network error syncing marked location", e)
                Result.success(location)
            }
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

            // Try to sync to API
            try {
                val request = UpdateMarkedLocationRequest(
                    name = name,
                    latitude = latitude,
                    longitude = longitude,
                    category = category,
                    notes = notes,
                    tags = tags
                )
                
                val apiService = connectionManager.getApiService()
                val response = apiService.updateMarkedLocation(id, request)
                if (response.isSuccessful) {
                    database.markedLocationDao().markAsSynced(id)
                    Log.d(TAG, "Marked location update synced to API: $id")
                } else {
                    Log.w(TAG, "Failed to sync marked location update to API: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Network error syncing marked location update", e)
            }

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
            // Try to delete from API first
            try {
                val apiService = connectionManager.getApiService()
                val response = apiService.deleteMarkedLocation(id)
                if (response.isSuccessful) {
                    Log.d(TAG, "Marked location deleted from API: $id")
                } else {
                    Log.w(TAG, "Failed to delete marked location from API: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Network error deleting marked location from API", e)
            }

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
     * Sync marked locations from API to local database
     */
    suspend fun syncMarkedLocationsFromApi(): Result<Unit> {
        return try {
            val apiService = connectionManager.getApiService()
            val response = apiService.getMarkedLocations()
            if (response.isSuccessful && response.body() != null) {
                val apiLocations = response.body()!!
                val localLocations = apiLocations.map { apiLocation ->
                    MarkedLocationEntity(
                        id = apiLocation.id,
                        name = apiLocation.name,
                        latitude = apiLocation.latitude,
                        longitude = apiLocation.longitude,
                        category = apiLocation.category,
                        notes = apiLocation.notes,
                        tags = apiLocation.tags.joinToString(","),
                        synced = true,
                        createdAt = parseDate(apiLocation.createdAt) ?: Date(),
                        lastModified = parseDate(apiLocation.updatedAt) ?: Date()
                    )
                }
                
                database.markedLocationDao().insertMarkedLocations(localLocations)
                Log.d(TAG, "Synced ${localLocations.size} marked locations from API")
                Result.success(Unit)
            } else {
                Log.e(TAG, "Failed to sync marked locations from API: ${response.code()}")
                Result.failure(Exception("API sync failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing marked locations from API", e)
            Result.failure(e)
        }
    }

    /**
     * Sync unsynced marked locations to API
     */
    suspend fun syncUnsyncedMarkedLocations(): Result<Unit> {
        return try {
            val unsyncedLocations = database.markedLocationDao().getUnsyncedMarkedLocations()
            Log.d(TAG, "Found ${unsyncedLocations.size} unsynced marked locations")

            for (location in unsyncedLocations) {
                try {
                    val request = CreateMarkedLocationRequest(
                        name = location.name,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        category = location.category,
                        notes = location.notes,
                        tags = if (location.tags.isEmpty()) emptyList() else location.tags.split(",")
                    )
                    
                    val apiService = connectionManager.getApiService()
                    val response = apiService.createMarkedLocation(request)
                    if (response.isSuccessful && response.body() != null) {
                        val apiLocation = response.body()!!
                        val syncedLocation = location.copy(
                            id = apiLocation.id,
                            synced = true
                        )
                        database.markedLocationDao().insertMarkedLocation(syncedLocation)
                        Log.d(TAG, "Synced marked location to API: ${apiLocation.id}")
                    } else {
                        Log.w(TAG, "Failed to sync marked location ${location.id} to API: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error syncing marked location ${location.id} to API", e)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing unsynced marked locations", e)
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
     * Parse date string from API
     */
    private fun parseDate(dateString: String): Date? {
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse date: $dateString", e)
            null
        }
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