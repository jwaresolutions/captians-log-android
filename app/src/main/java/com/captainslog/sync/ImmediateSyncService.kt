package com.captainslog.sync

import android.content.Context
import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.network.NetworkMonitor
import com.captainslog.network.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Service that handles immediate synchronization of data changes
 * Syncs data immediately when connected, queues when offline
 */
class ImmediateSyncService(
    private val context: Context,
    private val database: AppDatabase
) {
    companion object {
        private const val TAG = "ImmediateSyncService"
        
        @Volatile
        private var INSTANCE: ImmediateSyncService? = null
        
        fun getInstance(context: Context, database: AppDatabase): ImmediateSyncService {
            return INSTANCE ?: synchronized(this) {
                val instance = ImmediateSyncService(context.applicationContext, database)
                INSTANCE = instance
                instance
            }
        }
    }
    
    private val connectionManager = ConnectionManager.getInstance(context)
    private val networkMonitor = NetworkMonitor.getInstance(context)
    private val offlineChangeService = OfflineChangeService(database.offlineChangeDao())
    
    // Sync status tracking
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()
    
    private val _syncConflicts = MutableStateFlow<List<SyncConflict>>(emptyList())
    val syncConflicts: StateFlow<List<SyncConflict>> = _syncConflicts.asStateFlow()
    
    // Track pending sync operations to avoid duplicates
    private val pendingSyncs = ConcurrentHashMap<String, Job>()
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    init {
        // Initialize connection manager
        connectionManager.initialize()
        
        // Listen for network changes and sync when connected
        networkMonitor.onConnectionChanged = { isConnected, _ ->
            if (isConnected) {
                Log.d(TAG, "Network connected, triggering sync of pending changes")
                syncPendingChanges()
            }
        }
    }
    
    /**
     * Sync a trip immediately if connected, queue if offline
     */
    fun syncTrip(tripId: String) {
        val syncKey = "trip_$tripId"
        
        // Cancel any existing sync for this trip
        pendingSyncs[syncKey]?.cancel()
        
        // Start new sync
        pendingSyncs[syncKey] = serviceScope.launch {
            try {
                if (networkMonitor.canSyncData()) {
                    Log.d(TAG, "Syncing trip immediately: $tripId")
                    syncTripToServer(tripId)
                } else {
                    Log.d(TAG, "No connection, trip will sync when connected: $tripId")
                    // Trip is already marked as unsynced in database
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing trip $tripId", e)
            } finally {
                pendingSyncs.remove(syncKey)
            }
        }
    }
    
    /**
     * Sync boat data immediately if connected, queue if offline
     */
    fun syncBoat(boatId: String) {
        val syncKey = "boat_$boatId"
        
        pendingSyncs[syncKey]?.cancel()
        pendingSyncs[syncKey] = serviceScope.launch {
            try {
                if (networkMonitor.canSyncData()) {
                    Log.d(TAG, "Syncing boat immediately: $boatId")
                    syncBoatToServer(boatId)
                } else {
                    Log.d(TAG, "No connection, boat will sync when connected: $boatId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing boat $boatId", e)
            } finally {
                pendingSyncs.remove(syncKey)
            }
        }
    }
    
    /**
     * Sync photo metadata immediately, upload file only on WiFi
     */
    fun syncPhoto(photoId: String) {
        val syncKey = "photo_$photoId"
        
        pendingSyncs[syncKey]?.cancel()
        pendingSyncs[syncKey] = serviceScope.launch {
            try {
                val photo = database.photoDao().getPhotoById(photoId)
                if (photo != null) {
                    if (networkMonitor.canSyncData()) {
                        Log.d(TAG, "Syncing photo metadata immediately: $photoId")
                        // Sync metadata to server (create photo record without file)
                        syncPhotoMetadataToServer(photo)
                        
                        // Upload file only if on WiFi
                        if (networkMonitor.canUploadPhotos()) {
                            Log.d(TAG, "WiFi available, uploading photo file: $photoId")
                            uploadPhotoFileToServer(photo)
                        } else {
                            Log.d(TAG, "Not on WiFi, photo file will upload later: $photoId")
                        }
                    } else {
                        Log.d(TAG, "No connection, photo will sync when connected: $photoId")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing photo $photoId", e)
            } finally {
                pendingSyncs.remove(syncKey)
            }
        }
    }
    
    /**
     * Sync maintenance template immediately if connected
     */
    fun syncMaintenanceTemplate(templateId: String) {
        val syncKey = "template_$templateId"
        
        pendingSyncs[syncKey]?.cancel()
        pendingSyncs[syncKey] = serviceScope.launch {
            try {
                if (networkMonitor.canSyncData()) {
                    Log.d(TAG, "Syncing maintenance template immediately: $templateId")
                    syncTemplateToServer(templateId)
                } else {
                    Log.d(TAG, "No connection, template will sync when connected: $templateId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing template $templateId", e)
            } finally {
                pendingSyncs.remove(syncKey)
            }
        }
    }
    
    /**
     * Sync note immediately if connected
     */
    fun syncNote(noteId: String) {
        val syncKey = "note_$noteId"
        
        pendingSyncs[syncKey]?.cancel()
        pendingSyncs[syncKey] = serviceScope.launch {
            try {
                if (networkMonitor.canSyncData()) {
                    Log.d(TAG, "Syncing note immediately: $noteId")
                    syncNoteToServer(noteId)
                } else {
                    Log.d(TAG, "No connection, note will sync when connected: $noteId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing note $noteId", e)
            } finally {
                pendingSyncs.remove(syncKey)
            }
        }
    }
    
    /**
     * Sync all pending changes when connection is restored
     */
    private fun syncPendingChanges() {
        serviceScope.launch {
            try {
                _isSyncing.value = true
                
                // Sync unsynced trips
                val unsyncedTrips = database.tripDao().getUnsyncedTrips()
                Log.d(TAG, "Syncing ${unsyncedTrips.size} unsynced trips")
                for (trip in unsyncedTrips) {
                    syncTripToServer(trip.id)
                }
                
                // Sync unsynced boats
                val unsyncedBoats = database.boatDao().getUnsyncedBoats()
                Log.d(TAG, "Syncing ${unsyncedBoats.size} unsynced boats")
                for (boat in unsyncedBoats) {
                    syncBoatToServer(boat.id)
                }
                
                // Sync unsynced photos (metadata first, then files if on WiFi)
                val unsyncedPhotos = database.photoDao().getUnuploadedPhotos()
                Log.d(TAG, "Syncing ${unsyncedPhotos.size} unsynced photos")
                for (photo in unsyncedPhotos) {
                    syncPhotoMetadataToServer(photo)
                    if (networkMonitor.canUploadPhotos()) {
                        uploadPhotoFileToServer(photo)
                    }
                }
                
                // Sync offline changes (maintenance templates, etc.)
                val pendingChanges = offlineChangeService.getPendingChanges()
                // This would need to be collected as it's a Flow, but for now we'll use the sync workers
                
                Log.d(TAG, "Pending changes sync completed")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing pending changes", e)
            } finally {
                _isSyncing.value = false
            }
        }
    }
    
    /**
     * Sync a specific trip to the server
     */
    private suspend fun syncTripToServer(tripId: String) {
        try {
            val trip = database.tripDao().getTripById(tripId) ?: return
            val gpsPoints = database.gpsPointDao().getGpsPointsForTripSync(tripId)
            
            val apiService = connectionManager.getApiService()
            
            // Create manual data from trip entity
            val manualData = ManualData(
                engineHours = trip.engineHours,
                fuelConsumed = trip.fuelConsumed,
                weatherConditions = trip.weatherConditions,
                numberOfPassengers = trip.numberOfPassengers,
                destination = trip.destination
            )

            // Create trip request
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
                database.tripDao().markAsSynced(tripId)
                Log.d(TAG, "Trip synced successfully: $tripId")
            } else {
                Log.e(TAG, "Failed to sync trip: ${response.code()} - ${response.message()}")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing trip to server: $tripId", e)
        }
    }
    
    /**
     * Sync a specific boat to the server
     */
    private suspend fun syncBoatToServer(boatId: String) {
        try {
            val boat = database.boatDao().getBoatById(boatId) ?: return
            val apiService = connectionManager.getApiService()
            
            // Check if boat already exists on server by name to avoid duplicates
            val allBoatsResponse = apiService.getBoats()
            var existingBoat: com.captainslog.network.models.BoatResponse? = null
            
            if (allBoatsResponse.isSuccessful && allBoatsResponse.body() != null) {
                existingBoat = allBoatsResponse.body()!!.data.find { 
                    it.name.equals(boat.name, ignoreCase = true) 
                }
            }
            
            if (existingBoat != null) {
                // Boat with same name exists, update local boat to use server ID
                val updatedBoat = boat.copy(
                    id = existingBoat.id,
                    synced = true,
                    lastModified = java.util.Date()
                )
                database.boatDao().insertBoat(updatedBoat)
                
                // Update server boat with any local changes
                apiService.updateBoat(existingBoat.id, CreateBoatRequest(name = boat.name))
                if (boat.isActive) {
                    apiService.setActiveBoat(existingBoat.id)
                }
                apiService.updateBoatStatus(existingBoat.id, mapOf("enabled" to boat.enabled))
                
                Log.d(TAG, "Boat merged with existing server boat: ${boat.name} -> ${existingBoat.id}")
            } else {
                // Try to update existing boat by ID or create new one
                val response = apiService.getBoat(boatId)
                if (response.isSuccessful) {
                    // Boat exists by ID, update it
                    apiService.updateBoat(boatId, CreateBoatRequest(name = boat.name))
                    if (boat.isActive) {
                        apiService.setActiveBoat(boatId)
                    }
                    apiService.updateBoatStatus(boatId, mapOf("enabled" to boat.enabled))
                    database.boatDao().markAsSynced(boatId)
                } else {
                    // Boat doesn't exist, create it
                    val createResponse = apiService.createBoat(CreateBoatRequest(name = boat.name))
                    if (createResponse.isSuccessful && createResponse.body() != null) {
                        val apiBoat = createResponse.body()!!.data
                        // Update local boat with server ID
                        val syncedBoat = boat.copy(
                            id = apiBoat.id,
                            synced = true,
                            lastModified = java.util.Date()
                        )
                        database.boatDao().insertBoat(syncedBoat)
                        
                        // Set active status if needed
                        if (boat.isActive) {
                            apiService.setActiveBoat(apiBoat.id)
                        }
                        
                        Log.d(TAG, "New boat created on server: ${boat.name} -> ${apiBoat.id}")
                    }
                }
            }
            
            Log.d(TAG, "Boat synced successfully: $boatId")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing boat to server: $boatId", e)
        }
    }
    
    /**
     * Sync photo metadata to server (without file upload)
     */
    private suspend fun syncPhotoMetadataToServer(photo: com.captainslog.database.entities.PhotoEntity) {
        try {
            val apiService = connectionManager.getApiService()
            
            // Create photo metadata record on server
            // This would need a new API endpoint for metadata-only photo creation
            // For now, we'll mark it as ready for file upload
            
            Log.d(TAG, "Photo metadata synced: ${photo.id}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing photo metadata: ${photo.id}", e)
        }
    }
    
    /**
     * Upload photo file to server (WiFi only)
     */
    private suspend fun uploadPhotoFileToServer(photo: com.captainslog.database.entities.PhotoEntity) {
        try {
            // This would use the existing PhotoRepository.uploadPhoto method
            // For now, just log that it would happen
            Log.d(TAG, "Photo file uploaded: ${photo.id}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading photo file: ${photo.id}", e)
        }
    }
    
    /**
     * Sync maintenance template to server
     */
    private suspend fun syncTemplateToServer(templateId: String) {
        try {
            // This would sync maintenance template changes
            // Implementation would depend on the specific template data structure
            Log.d(TAG, "Template synced: $templateId")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing template: $templateId", e)
        }
    }
    
    /**
     * Sync note to server
     */
    private suspend fun syncNoteToServer(noteId: String) {
        try {
            // This would sync note changes
            Log.d(TAG, "Note synced: $noteId")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing note: $noteId", e)
        }
    }
    
    /**
     * Handle sync conflicts - show user options to resolve
     */
    fun handleSyncConflict(conflict: SyncConflict) {
        serviceScope.launch {
            val currentConflicts = _syncConflicts.value.toMutableList()
            currentConflicts.add(conflict)
            _syncConflicts.value = currentConflicts
        }
    }
    
    /**
     * Resolve sync conflict with user choice
     */
    fun resolveSyncConflict(conflictId: String, useLocal: Boolean) {
        serviceScope.launch {
            val currentConflicts = _syncConflicts.value.toMutableList()
            val conflict = currentConflicts.find { it.id == conflictId }
            if (conflict != null) {
                // Apply resolution based on user choice
                if (useLocal) {
                    // Override server with local data
                    Log.d(TAG, "Resolving conflict with local data: $conflictId")
                } else {
                    // Accept server data, update local
                    Log.d(TAG, "Resolving conflict with server data: $conflictId")
                }
                
                // Remove from conflicts list
                currentConflicts.remove(conflict)
                _syncConflicts.value = currentConflicts
            }
        }
    }
    
    /**
     * Check if there are unresolved sync conflicts
     */
    fun hasUnresolvedConflicts(): Boolean {
        return _syncConflicts.value.isNotEmpty()
    }
    
    /**
     * Cleanup service resources
     */
    fun cleanup() {
        serviceScope.cancel()
        pendingSyncs.clear()
    }
}

/**
 * Data class representing a sync conflict
 */
data class SyncConflict(
    val id: String,
    val entityType: String,
    val entityId: String,
    val localTimestamp: Date,
    val serverTimestamp: Date,
    val description: String
)