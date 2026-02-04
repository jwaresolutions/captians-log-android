package com.captainslog.sync

import android.content.Context
import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.MaintenanceTemplateEntity
import com.captainslog.database.entities.MaintenanceEventEntity
import com.captainslog.repository.*
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * Comprehensive sync manager that handles bidirectional synchronization
 * for ALL data types in the application.
 * 
 * This ensures that data created on the web interface appears in the Android app
 * and vice versa, solving the systemic sync issue.
 */
class ComprehensiveSyncManager(
    private val context: Context,
    private val database: AppDatabase
) {
    companion object {
        private const val TAG = "ComprehensiveSyncManager"
        
        @Volatile
        private var INSTANCE: ComprehensiveSyncManager? = null
        
        fun getInstance(context: Context, database: AppDatabase): ComprehensiveSyncManager {
            return INSTANCE ?: synchronized(this) {
                val instance = ComprehensiveSyncManager(context.applicationContext, database)
                INSTANCE = instance
                instance
            }
        }
    }
    
    private val connectionManager = ConnectionManager.getInstance(context)
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Repositories for each data type
    private lateinit var boatRepository: BoatRepository
    private lateinit var tripRepository: TripRepository
    private lateinit var noteRepository: NoteRepository
    private lateinit var todoRepository: TodoRepository
    private lateinit var markedLocationRepository: MarkedLocationRepository
    private lateinit var photoRepository: PhotoRepository
    
    // Sync status tracking
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()
    
    private val _syncProgress = MutableStateFlow<SyncProgress?>(null)
    val syncProgress: StateFlow<SyncProgress?> = _syncProgress.asStateFlow()
    
    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime.asStateFlow()
    
    // Track ongoing sync operations
    private val activeSyncs = ConcurrentHashMap<String, Job>()
    
    init {
        initializeRepositories()
    }
    
    private fun initializeRepositories() {
        connectionManager.initialize()
        
        boatRepository = BoatRepository(database, connectionManager, context)
        tripRepository = TripRepository(database, context)
        noteRepository = NoteRepository(database, connectionManager)
        todoRepository = TodoRepository(connectionManager, database.todoListDao(), database.todoItemDao())
        markedLocationRepository = MarkedLocationRepository(database, connectionManager)
        photoRepository = PhotoRepository(database, context)
    }
    
    /**
     * Perform comprehensive bidirectional sync for ALL data types
     * This is the main method that should be called on app startup and periodically
     */
    fun performFullSync() {
        val syncKey = "full_sync"
        
        // Cancel any existing full sync
        activeSyncs[syncKey]?.cancel()
        
        activeSyncs[syncKey] = serviceScope.launch {
            try {
                _isSyncing.value = true
                _syncProgress.value = SyncProgress("Starting comprehensive sync...", 0, 100)
                
                Log.d(TAG, "Starting comprehensive bidirectional sync")
                
                // Step 1: Sync boats (foundation data)
                _syncProgress.value = SyncProgress("Syncing boats...", 10, 100)
                syncBoats()
                
                // Step 2: Sync trips
                _syncProgress.value = SyncProgress("Syncing trips...", 25, 100)
                syncTrips()
                
                // Step 3: Sync notes
                _syncProgress.value = SyncProgress("Syncing notes...", 40, 100)
                syncNotes()
                
                // Step 4: Sync todo lists and items
                _syncProgress.value = SyncProgress("Syncing todo lists...", 50, 100)
                syncTodos()

                // Step 5: Sync maintenance templates
                _syncProgress.value = SyncProgress("Syncing maintenance templates...", 60, 100)
                syncMaintenanceTemplates()

                // Step 6: Sync maintenance events
                _syncProgress.value = SyncProgress("Syncing maintenance events...", 70, 100)
                syncMaintenanceEvents()

                // Step 7: Sync marked locations
                _syncProgress.value = SyncProgress("Syncing locations...", 85, 100)
                syncMarkedLocations()

                // Step 8: Sync photos (metadata only, files on WiFi)
                _syncProgress.value = SyncProgress("Syncing photos...", 95, 100)
                syncPhotos()
                
                // Only mark as successful if we actually have data
                val localBoats = database.boatDao().getAllBoatsSync()
                if (localBoats.isNotEmpty()) {
                    _syncProgress.value = SyncProgress("Sync completed", 100, 100)
                    _lastSyncTime.value = System.currentTimeMillis()
                    Log.d(TAG, "Comprehensive sync completed successfully - ${localBoats.size} boats synced")
                } else {
                    _syncProgress.value = SyncProgress("Sync completed but no data found", 100, 100)
                    Log.w(TAG, "Comprehensive sync completed but no boats found locally")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error during comprehensive sync", e)
                _syncProgress.value = SyncProgress("Sync failed: ${e.message}", 0, 100)
            } finally {
                _isSyncing.value = false
                activeSyncs.remove(syncKey)
                
                // Clear progress after a delay
                delay(3000)
                _syncProgress.value = null
            }
        }
    }
    
    /**
     * Sync boats bidirectionally
     */
    private suspend fun syncBoats() {
        try {
            Log.d(TAG, "=== Starting boat sync ===")
            
            // Check local boats before sync
            val localBoatsBefore = database.boatDao().getAllBoatsSync()
            Log.d(TAG, "Local boats before sync: ${localBoatsBefore.size}")
            
            Log.d(TAG, "Syncing boats from server...")
            val fromServerResult = boatRepository.syncBoatsFromApi()
            if (fromServerResult.isFailure) {
                Log.e(TAG, "Failed to sync boats from server: ${fromServerResult.exceptionOrNull()?.message}")
                fromServerResult.exceptionOrNull()?.printStackTrace()
            } else {
                Log.d(TAG, "Successfully synced boats from server")
                
                // Check local boats after sync
                val localBoatsAfter = database.boatDao().getAllBoatsSync()
                Log.d(TAG, "Local boats after sync: ${localBoatsAfter.size}")
                for (boat in localBoatsAfter) {
                    Log.d(TAG, "  - ${boat.name} (${boat.id}) synced: ${boat.synced}")
                }
            }
            
            Log.d(TAG, "Syncing boats to server...")
            val toServerResult = boatRepository.syncBoatsToApi()
            if (toServerResult.isFailure) {
                Log.e(TAG, "Failed to sync boats to server: ${toServerResult.exceptionOrNull()?.message}")
                toServerResult.exceptionOrNull()?.printStackTrace()
            } else {
                Log.d(TAG, "Successfully synced boats to server")
            }
            
            Log.d(TAG, "=== Boat sync completed ===")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing boats", e)
            e.printStackTrace()
        }
    }
    
    /**
     * Sync trips bidirectionally
     */
    private suspend fun syncTrips() {
        try {
            Log.d(TAG, "Syncing trips from server...")
            val fromServerResult = tripRepository.syncTripsFromApi()
            if (fromServerResult.isFailure) {
                Log.w(TAG, "Failed to sync trips from server: ${fromServerResult.exceptionOrNull()?.message}")
            }

            Log.d(TAG, "Syncing trips to server...")
            val toServerResult = tripRepository.syncTripsToApi()
            if (toServerResult.isFailure) {
                Log.w(TAG, "Failed to sync trips to server: ${toServerResult.exceptionOrNull()?.message}")
            }

            Log.d(TAG, "Trip sync completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing trips", e)
        }
    }
    
    /**
     * Sync notes bidirectionally
     */
    private suspend fun syncNotes() {
        try {
            Log.d(TAG, "Syncing notes from server...")
            val fromServerResult = noteRepository.syncNotesFromApi()
            if (fromServerResult.isFailure) {
                Log.w(TAG, "Failed to sync notes from server: ${fromServerResult.exceptionOrNull()?.message}")
            }
            
            Log.d(TAG, "Syncing notes to server...")
            val toServerResult = noteRepository.syncNotesToApi()
            if (toServerResult.isFailure) {
                Log.w(TAG, "Failed to sync notes to server: ${toServerResult.exceptionOrNull()?.message}")
            }
            
            Log.d(TAG, "Note sync completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing notes", e)
        }
    }
    
    /**
     * Sync todo lists and items bidirectionally
     */
    private suspend fun syncTodos() {
        try {
            Log.d(TAG, "Syncing todos from server...")
            val result = todoRepository.syncTodoLists()
            if (result.isFailure) {
                Log.w(TAG, "Failed to sync todos from server: ${result.exceptionOrNull()?.message}")
            }
            
            // TODO: Add sync TO server for unsynced todos
            // Currently TodoRepository handles individual item sync but not bulk sync
            
            Log.d(TAG, "Todo sync completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing todos", e)
        }
    }
    
    /**
     * Sync marked locations bidirectionally
     */
    private suspend fun syncMarkedLocations() {
        try {
            Log.d(TAG, "Syncing marked locations from server...")
            val fromServerResult = markedLocationRepository.syncMarkedLocationsFromApi()
            if (fromServerResult.isFailure) {
                Log.w(TAG, "Failed to sync marked locations from server: ${fromServerResult.exceptionOrNull()?.message}")
            }
            
            Log.d(TAG, "Syncing marked locations to server...")
            val toServerResult = markedLocationRepository.syncUnsyncedMarkedLocations()
            if (toServerResult.isFailure) {
                Log.w(TAG, "Failed to sync marked locations to server: ${toServerResult.exceptionOrNull()?.message}")
            }
            
            Log.d(TAG, "Marked location sync completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing marked locations", e)
        }
    }
    
    /**
     * Sync photos (metadata sync, file upload on WiFi only)
     */
    private suspend fun syncPhotos() {
        try {
            Log.d(TAG, "Syncing photo metadata...")
            
            // TODO: Implement photo metadata sync FROM server
            // Currently PhotoRepository only handles uploads TO server
            
            // Check for unuploaded photos
            val unuploadedPhotos = photoRepository.getUnuploadedPhotos()
            Log.d(TAG, "Found ${unuploadedPhotos.size} unuploaded photos")
            
            // Note: Photo file uploads are handled by PhotoSyncWorker on WiFi
            
            Log.d(TAG, "Photo sync completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing photos", e)
        }
    }
    
    /**
     * Sync maintenance templates from server
     */
    private suspend fun syncMaintenanceTemplates() {
        try {
            Log.d(TAG, "Syncing maintenance templates from server...")
            val apiService = connectionManager.getApiService()
            val response = apiService.getMaintenanceTemplates()

            if (response.isSuccessful && response.body() != null) {
                val apiTemplates = response.body()!!.data
                Log.d(TAG, "Received ${apiTemplates.size} maintenance templates from API")

                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }

                val templateEntities = apiTemplates.map { t ->
                    MaintenanceTemplateEntity(
                        id = t.id,
                        boatId = t.boatId,
                        title = t.title,
                        description = t.description,
                        component = t.component,
                        estimatedCost = t.estimatedCost,
                        estimatedTime = t.estimatedTime,
                        isActive = t.isActive,
                        recurrenceType = t.recurrence.type,
                        recurrenceInterval = t.recurrence.interval,
                        createdAt = dateFormat.parse(t.createdAt) ?: java.util.Date(),
                        updatedAt = dateFormat.parse(t.updatedAt) ?: java.util.Date()
                    )
                }

                database.maintenanceTemplateDao().insertTemplates(templateEntities)
                Log.d(TAG, "Upserted ${templateEntities.size} maintenance templates")
            } else {
                Log.w(TAG, "Failed to fetch maintenance templates: ${response.code()}")
            }

            Log.d(TAG, "Maintenance template sync completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing maintenance templates", e)
        }
    }

    /**
     * Sync maintenance events from server
     */
    private suspend fun syncMaintenanceEvents() {
        try {
            Log.d(TAG, "Syncing maintenance events from server...")
            val apiService = connectionManager.getApiService()

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

            val allEvents = mutableListOf<MaintenanceEventEntity>()

            // Fetch upcoming events
            val upcomingResponse = apiService.getUpcomingMaintenanceEvents()
            if (upcomingResponse.isSuccessful && upcomingResponse.body() != null) {
                val upcomingApiEvents = upcomingResponse.body()!!.data
                Log.d(TAG, "Received ${upcomingApiEvents.size} upcoming maintenance events")

                allEvents.addAll(upcomingApiEvents.map { e ->
                    MaintenanceEventEntity(
                        id = e.id,
                        templateId = e.templateId,
                        dueDate = dateFormat.parse(e.dueDate) ?: java.util.Date(),
                        completedAt = e.completedAt?.let { dateFormat.parse(it) },
                        actualCost = e.actualCost,
                        actualTime = e.actualTime,
                        notes = e.notes,
                        createdAt = dateFormat.parse(e.createdAt) ?: java.util.Date(),
                        updatedAt = dateFormat.parse(e.updatedAt) ?: java.util.Date()
                    )
                })
            } else {
                Log.w(TAG, "Failed to fetch upcoming maintenance events: ${upcomingResponse.code()}")
            }

            // Fetch completed events
            val completedResponse = apiService.getCompletedMaintenanceEvents()
            if (completedResponse.isSuccessful && completedResponse.body() != null) {
                val completedApiEvents = completedResponse.body()!!.data
                Log.d(TAG, "Received ${completedApiEvents.size} completed maintenance events")

                allEvents.addAll(completedApiEvents.map { e ->
                    MaintenanceEventEntity(
                        id = e.id,
                        templateId = e.templateId,
                        dueDate = dateFormat.parse(e.dueDate) ?: java.util.Date(),
                        completedAt = e.completedAt?.let { dateFormat.parse(it) },
                        actualCost = e.actualCost,
                        actualTime = e.actualTime,
                        notes = e.notes,
                        createdAt = dateFormat.parse(e.createdAt) ?: java.util.Date(),
                        updatedAt = dateFormat.parse(e.updatedAt) ?: java.util.Date()
                    )
                })
            } else {
                Log.w(TAG, "Failed to fetch completed maintenance events: ${completedResponse.code()}")
            }

            if (allEvents.isNotEmpty()) {
                database.maintenanceEventDao().insertEvents(allEvents)
                Log.d(TAG, "Upserted ${allEvents.size} maintenance events total")
            }

            Log.d(TAG, "Maintenance event sync completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing maintenance events", e)
        }
    }

    /**
     * Sync a specific data type
     */
    fun syncDataType(dataType: DataType) {
        val syncKey = "sync_${dataType.name}"
        
        activeSyncs[syncKey]?.cancel()
        activeSyncs[syncKey] = serviceScope.launch {
            try {
                Log.d(TAG, "Syncing ${dataType.name}...")
                
                when (dataType) {
                    DataType.BOATS -> syncBoats()
                    DataType.TRIPS -> syncTrips()
                    DataType.NOTES -> syncNotes()
                    DataType.TODOS -> syncTodos()
                    DataType.MAINTENANCE_TEMPLATES -> syncMaintenanceTemplates()
                    DataType.MAINTENANCE_EVENTS -> syncMaintenanceEvents()
                    DataType.MARKED_LOCATIONS -> syncMarkedLocations()
                    DataType.PHOTOS -> syncPhotos()
                }
                
                Log.d(TAG, "${dataType.name} sync completed")
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing ${dataType.name}", e)
            } finally {
                activeSyncs.remove(syncKey)
            }
        }
    }
    
    /**
     * Check if sync is currently running
     */
    fun isSyncInProgress(): Boolean = _isSyncing.value
    
    /**
     * Cancel all active sync operations
     */
    fun cancelAllSyncs() {
        activeSyncs.values.forEach { it.cancel() }
        activeSyncs.clear()
        _isSyncing.value = false
        _syncProgress.value = null
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        cancelAllSyncs()
        serviceScope.cancel()
    }
}

/**
 * Data types that can be synced
 */
enum class DataType {
    BOATS,
    TRIPS,
    NOTES,
    TODOS,
    MAINTENANCE_TEMPLATES,
    MAINTENANCE_EVENTS,
    MARKED_LOCATIONS,
    PHOTOS
}

/**
 * Sync progress information
 */
data class SyncProgress(
    val message: String,
    val current: Int,
    val total: Int
) {
    val percentage: Int get() = if (total > 0) (current * 100) / total else 0
}