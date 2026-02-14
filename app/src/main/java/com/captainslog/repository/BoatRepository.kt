package com.captainslog.repository

import android.content.Context
import android.util.Log
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.BoatEntity
import com.captainslog.connection.ConnectionManager
import com.captainslog.network.ApiService
import com.captainslog.network.models.CreateBoatRequest
import com.captainslog.sync.DataType
import com.captainslog.sync.SyncOrchestrator
import dagger.Lazy
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository for managing boat data.
 * Handles both local database operations and immediate API synchronization.
 */
class BoatRepository(
    private val database: AppDatabase,
    private val connectionManager: ConnectionManager,
    private val context: Context,
    private val syncOrchestratorLazy: Lazy<SyncOrchestrator>
) {
    private val syncOrchestrator: SyncOrchestrator get() = syncOrchestratorLazy.get()

    /**
     * Get all boats as a Flow for reactive updates
     */
    fun getAllBoats(): Flow<List<BoatEntity>> {
        return database.boatDao().getAllBoats()
    }

    /**
     * Get a specific boat by ID
     */
    suspend fun getBoatById(boatId: String): BoatEntity? {
        return database.boatDao().getBoatById(boatId)
    }

    /**
     * Get the currently active boat
     */
    suspend fun getActiveBoat(): BoatEntity? {
        return database.boatDao().getActiveBoat()
    }

    /**
     * Create a new boat locally and sync immediately
     * Checks for existing boats with same name to avoid duplicates
     */
    suspend fun createBoat(name: String): Result<BoatEntity> {
        return try {
            val trimmedName = name.trim()
            if (trimmedName.isEmpty()) {
                return Result.failure(Exception("Boat name cannot be empty"))
            }
            
            // Check if boat with same name already exists locally
            val existingBoats = database.boatDao().getAllBoatsSync()
            val existingBoat = existingBoats.find { 
                it.name.equals(trimmedName, ignoreCase = true) 
            }
            
            if (existingBoat != null) {
                return Result.failure(Exception("A boat with this name already exists"))
            }
            
            // Create boat locally first
            val boat = BoatEntity(
                name = trimmedName,
                enabled = true,
                isActive = false,
                synced = false,
                lastModified = Date(),
                createdAt = Date()
            )
            
            database.boatDao().insertBoat(boat)
            
            // Sync immediately if connected, queue if offline
            syncOrchestrator.syncEntity(DataType.BOATS,boat.id)
            
            Result.success(boat)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update boat enabled status and sync immediately
     */
    suspend fun updateBoatStatus(boatId: String, enabled: Boolean): Result<Unit> {
        return try {
            val boat = database.boatDao().getBoatById(boatId)
            if (boat != null) {
                // If disabling an active boat, clear its active status
                val updatedBoat = if (!enabled && boat.isActive) {
                    boat.copy(
                        enabled = enabled,
                        isActive = false,
                        synced = false,
                        lastModified = Date()
                    )
                } else {
                    boat.copy(
                        enabled = enabled,
                        synced = false,
                        lastModified = Date()
                    )
                }
                database.boatDao().updateBoat(updatedBoat)
                
                // Sync immediately if connected, queue if offline
                syncOrchestrator.syncEntity(DataType.BOATS,boatId)
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Boat not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Set a boat as the active boat and sync immediately
     */
    suspend fun setActiveBoat(boatId: String): Result<Unit> {
        return try {
            // Clear all active boats first
            database.boatDao().clearActiveBoat()
            
            // Set the new active boat
            database.boatDao().setActiveBoat(boatId)
            
            // Mark as unsynced
            val boat = database.boatDao().getBoatById(boatId)
            if (boat != null) {
                database.boatDao().updateBoat(boat.copy(synced = false, lastModified = Date()))
            }
            
            // Sync immediately if connected, queue if offline
            syncOrchestrator.syncEntity(DataType.BOATS,boatId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sync boats from API to local database
     * Merges server boats with local boats, avoiding duplicates
     */
    suspend fun syncBoatsFromApi(): Result<Unit> {
        return try {
            Log.d("BoatRepository", "Starting syncBoatsFromApi...")
            
            val apiService = connectionManager.getApiService()
            Log.d("BoatRepository", "Got API service, making request...")
            
            val response = apiService.getBoats()
            Log.d("BoatRepository", "API response: ${response.code()}, successful: ${response.isSuccessful}")
            
            if (response.isSuccessful && response.body() != null) {
                val apiBoats = response.body()!!.data
                Log.d("BoatRepository", "Received ${apiBoats.size} boats from API")
                
                val existingLocalBoats = database.boatDao().getAllBoatsSync()
                Log.d("BoatRepository", "Found ${existingLocalBoats.size} existing local boats")
                
                for (apiBoat in apiBoats) {
                    Log.d("BoatRepository", "Processing API boat: ${apiBoat.name} (${apiBoat.id})")
                    
                    // Check if we already have this boat locally (by ID or name)
                    val existingBoat = existingLocalBoats.find { 
                        it.id == apiBoat.id || it.name.equals(apiBoat.name, ignoreCase = true)
                    }
                    
                    if (existingBoat != null) {
                        Log.d("BoatRepository", "Updating existing boat: ${existingBoat.name}")
                        // Update existing boat with server data
                        val updatedBoat = existingBoat.copy(
                            id = apiBoat.id, // Use server ID
                            name = apiBoat.name, // Use server name (preserves case)
                            enabled = apiBoat.enabled,
                            isActive = apiBoat.isActive,
                            synced = true,
                            lastModified = Date()
                        )
                        database.boatDao().updateBoat(updatedBoat)
                    } else {
                        Log.d("BoatRepository", "Inserting new boat: ${apiBoat.name}")
                        // Insert new boat from server
                        val newBoat = BoatEntity(
                            id = apiBoat.id,
                            name = apiBoat.name,
                            enabled = apiBoat.enabled,
                            isActive = apiBoat.isActive,
                            synced = true,
                            lastModified = Date(),
                            createdAt = Date()
                        )
                        database.boatDao().insertBoat(newBoat)
                    }
                }
                
                // Verify boats were inserted
                val finalLocalBoats = database.boatDao().getAllBoatsSync()
                Log.d("BoatRepository", "After sync: ${finalLocalBoats.size} boats in local database")
                for (boat in finalLocalBoats) {
                    Log.d("BoatRepository", "Local boat: ${boat.name} (${boat.id}) - synced: ${boat.synced}")
                }
                
                Log.d("BoatRepository", "syncBoatsFromApi completed successfully")
                Result.success(Unit)
            } else {
                val errorMsg = "Failed to fetch boats from API: ${response.code()} - ${response.message()}"
                Log.e("BoatRepository", errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("BoatRepository", "Exception in syncBoatsFromApi", e)
            Result.failure(e)
        }
    }

    /**
     * Sync unsynced boats to API
     */
    suspend fun syncBoatsToApi(): Result<Unit> {
        return try {
            val apiService = connectionManager.getApiService()
            val unsyncedBoats = database.boatDao().getUnsyncedBoats()
            for (boat in unsyncedBoats) {
                try {
                    // Try to update or create on server
                    val response = apiService.getBoat(boat.id)
                    if (response.isSuccessful) {
                        // Boat exists, update it
                        apiService.updateBoat(boat.id, CreateBoatRequest(name = boat.name))
                        if (boat.isActive) {
                            apiService.setActiveBoat(boat.id)
                        }
                        apiService.updateBoatStatus(boat.id, mapOf("enabled" to boat.enabled))
                    } else {
                        // Boat doesn't exist, create it
                        val createResponse = apiService.createBoat(CreateBoatRequest(name = boat.name))
                        if (createResponse.isSuccessful && createResponse.body() != null) {
                            val apiBoat = createResponse.body()!!.data
                            // Update local boat with server ID
                            val syncedBoat = boat.copy(id = apiBoat.id)
                            database.boatDao().insertBoat(syncedBoat)
                        }
                    }
                    database.boatDao().markAsSynced(boat.id)
                } catch (e: Exception) {
                    // Continue with next boat
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
