package com.captainslog.sync

import android.content.Context
import android.util.Log
import androidx.work.*
import com.captainslog.network.NetworkMonitor
// SyncConflict is in the same package (com.captainslog.sync)
import com.captainslog.sync.handlers.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single entry point for ALL sync operations.
 * Replaces ComprehensiveSyncManager, SyncManager, ImmediateSyncService, and InitialSyncManager.
 */
@Singleton
class SyncOrchestrator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkMonitor: NetworkMonitor,
    private val conflictLogger: ConflictLogger,
    private val boatSyncHandler: BoatSyncHandler,
    private val tripSyncHandler: TripSyncHandler,
    private val noteSyncHandler: NoteSyncHandler,
    private val todoSyncHandler: TodoSyncHandler,
    private val templateSyncHandler: TemplateSyncHandler,
    private val locationSyncHandler: LocationSyncHandler,
    private val photoSyncHandler: PhotoSyncHandler
) {
    companion object {
        private const val TAG = "SyncOrchestrator"

        // WorkManager work names (migrated from SyncManager)
        const val PERIODIC_SYNC_WORK_NAME = "periodic_trip_sync"
        const val ONE_TIME_SYNC_WORK_NAME = "one_time_trip_sync"
        const val PERIODIC_PHOTO_SYNC_WORK_NAME = "periodic_photo_sync"
        const val ONE_TIME_PHOTO_SYNC_WORK_NAME = "one_time_photo_sync"
        const val PERIODIC_TEMPLATE_SYNC_WORK_NAME = "periodic_template_sync"
        const val ONE_TIME_TEMPLATE_SYNC_WORK_NAME = "one_time_template_sync"

        private const val SYNC_INTERVAL_MINUTES = 15L
        private const val PHOTO_SYNC_INTERVAL_MINUTES = 30L
        private const val TEMPLATE_SYNC_INTERVAL_MINUTES = 10L
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val syncMutex = Mutex()

    // Public state flows
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncProgress = MutableStateFlow<SyncProgress?>(null)
    val syncProgress: StateFlow<SyncProgress?> = _syncProgress.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime.asStateFlow()

    // Sync conflict tracking (migrated from ImmediateSyncService)
    private val _syncConflicts = MutableStateFlow<List<SyncConflict>>(emptyList())
    val syncConflicts: StateFlow<List<SyncConflict>> = _syncConflicts.asStateFlow()

    private val handlers: Map<DataType, SyncHandler> by lazy {
        mapOf(
            DataType.BOATS to boatSyncHandler,
            DataType.TRIPS to tripSyncHandler,
            DataType.NOTES to noteSyncHandler,
            DataType.TODOS to todoSyncHandler,
            DataType.TEMPLATES to templateSyncHandler,
            DataType.LOCATIONS to locationSyncHandler,
            DataType.PHOTOS to photoSyncHandler
        )
    }

    /**
     * Full sync of all data types. Replaces ComprehensiveSyncManager.performFullSync()
     */
    fun syncAll() {
        serviceScope.launch {
            syncAllSuspend()
        }
    }

    /**
     * Suspending version of syncAll for use from coroutines
     */
    suspend fun syncAllSuspend() {
        if (!syncMutex.tryLock()) {
            Log.d(TAG, "Sync already in progress, skipping")
            return
        }
        try {
            _isSyncing.value = true
            _syncProgress.value = SyncProgress("Starting comprehensive sync...", 0, 100)
            Log.d(TAG, "Starting comprehensive bidirectional sync")

            val dataTypes = listOf(
                DataType.BOATS to 10,
                DataType.TRIPS to 25,
                DataType.NOTES to 40,
                DataType.TODOS to 50,
                DataType.TEMPLATES to 65,
                DataType.LOCATIONS to 85,
                DataType.PHOTOS to 95
            )

            for ((type, progress) in dataTypes) {
                _syncProgress.value = SyncProgress("Syncing ${type.name.lowercase()}...", progress, 100)
                val handler = handlers[type]
                if (handler != null) {
                    try {
                        handler.sync()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error syncing ${type.name}", e)
                    }
                }
            }

            _syncProgress.value = SyncProgress("Sync completed", 100, 100)
            _lastSyncTime.value = System.currentTimeMillis()
            Log.d(TAG, "Comprehensive sync completed successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Error during comprehensive sync", e)
            _syncProgress.value = SyncProgress("Sync failed: ${e.message}", 0, 100)
        } finally {
            _isSyncing.value = false
            syncMutex.unlock()

            delay(3000)
            _syncProgress.value = null
        }
    }

    /**
     * Sync one data type. Replaces ComprehensiveSyncManager.syncDataType()
     */
    fun syncDataType(dataType: DataType) {
        serviceScope.launch {
            syncDataTypeSuspend(dataType)
        }
    }

    /**
     * Suspending version of syncDataType
     */
    suspend fun syncDataTypeSuspend(dataType: DataType) {
        try {
            Log.d(TAG, "Syncing ${dataType.name}...")
            handlers[dataType]?.sync()
            Log.d(TAG, "${dataType.name} sync completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing ${dataType.name}", e)
        }
    }

    /**
     * Immediate entity sync. Replaces ImmediateSyncService.syncXxx(entityId)
     */
    fun syncEntity(dataType: DataType, entityId: String) {
        serviceScope.launch {
            syncEntitySuspend(dataType, entityId)
        }
    }

    /**
     * Suspending version of syncEntity
     */
    suspend fun syncEntitySuspend(dataType: DataType, entityId: String): HandlerSyncResult {
        return try {
            if (!networkMonitor.canSyncData()) {
                Log.d(TAG, "No connection, ${dataType.name} entity will sync when connected: $entityId")
                return HandlerSyncResult(success = true) // Deferred, not failed
            }
            Log.d(TAG, "Syncing ${dataType.name} entity immediately: $entityId")
            handlers[dataType]?.syncEntity(entityId) ?: HandlerSyncResult(
                success = false, errors = listOf("No handler for $dataType")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing ${dataType.name} entity: $entityId", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }

    /**
     * First-time sync with progress. Replaces InitialSyncManager.performInitialSync()
     */
    suspend fun performInitialSync(): Flow<SyncProgress> = flow {
        try {
            emit(SyncProgress("Starting initial sync...", 0, 100))

            // Upload local data
            emit(SyncProgress("Uploading local data...", 10, 100))
            for (handler in handlers.values) {
                try {
                    handler.syncToServer()
                } catch (e: Exception) {
                    Log.e(TAG, "Error uploading ${handler.dataType.name}", e)
                }
            }

            // Download server data
            emit(SyncProgress("Downloading server data...", 50, 100))
            for (handler in handlers.values) {
                try {
                    handler.syncFromServer()
                } catch (e: Exception) {
                    Log.e(TAG, "Error downloading ${handler.dataType.name}", e)
                }
            }

            emit(SyncProgress("Initial sync completed", 100, 100))
            _lastSyncTime.value = System.currentTimeMillis()
            Log.d(TAG, "Initial sync completed")

        } catch (e: Exception) {
            Log.e(TAG, "Error during initial sync", e)
            emit(SyncProgress("Sync failed: ${e.message}", 0, 100))
        }
    }

    /**
     * Schedule periodic background sync. Replaces SyncManager.schedulePeriodicSync()
     */
    fun schedulePeriodicSync() {
        // Trip sync
        val tripConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val tripSyncRequest = PeriodicWorkRequestBuilder<TripSyncWorker>(
            SYNC_INTERVAL_MINUTES, TimeUnit.MINUTES
        )
            .setConstraints(tripConstraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_SYNC_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, tripSyncRequest
        )

        // Photo sync (WiFi only)
        val photoConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val photoSyncRequest = PeriodicWorkRequestBuilder<PhotoSyncWorker>(
            PHOTO_SYNC_INTERVAL_MINUTES, TimeUnit.MINUTES
        )
            .setConstraints(photoConstraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_PHOTO_SYNC_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, photoSyncRequest
        )

        // Template sync
        val templateConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val templateSyncRequest = PeriodicWorkRequestBuilder<TemplateSyncWorker>(
            TEMPLATE_SYNC_INTERVAL_MINUTES, TimeUnit.MINUTES
        )
            .setConstraints(templateConstraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_TEMPLATE_SYNC_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, templateSyncRequest
        )

        Log.d(TAG, "Scheduled periodic sync: trips=${SYNC_INTERVAL_MINUTES}m, photos=${PHOTO_SYNC_INTERVAL_MINUTES}m (WiFi), templates=${TEMPLATE_SYNC_INTERVAL_MINUTES}m")
    }

    /**
     * Trigger immediate one-time sync. Replaces SyncManager.triggerImmediateSync()
     */
    fun triggerImmediateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<TripSyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_TIME_SYNC_WORK_NAME, ExistingWorkPolicy.REPLACE, syncRequest
        )
    }

    /**
     * Trigger immediate photo sync. Replaces SyncManager.triggerImmediatePhotoSync()
     */
    fun triggerImmediatePhotoSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val photoSyncRequest = OneTimeWorkRequestBuilder<PhotoSyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_TIME_PHOTO_SYNC_WORK_NAME, ExistingWorkPolicy.REPLACE, photoSyncRequest
        )
    }

    /**
     * Trigger immediate template sync. Replaces SyncManager.triggerImmediateTemplateSync()
     */
    fun triggerImmediateTemplateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val templateSyncRequest = OneTimeWorkRequestBuilder<TemplateSyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_TIME_TEMPLATE_SYNC_WORK_NAME, ExistingWorkPolicy.REPLACE, templateSyncRequest
        )
    }

    /**
     * Cancel all sync work. Replaces SyncManager.cancelAllSync()
     */
    fun cancelAllSync() {
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_SYNC_WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(ONE_TIME_SYNC_WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_PHOTO_SYNC_WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(ONE_TIME_PHOTO_SYNC_WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_TEMPLATE_SYNC_WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(ONE_TIME_TEMPLATE_SYNC_WORK_NAME)
        _isSyncing.value = false
        _syncProgress.value = null
        Log.d(TAG, "Cancelled all sync work")
    }

    /**
     * Check if sync is currently running
     */
    fun isSyncInProgress(): Boolean = _isSyncing.value

    /**
     * Handle sync conflict
     */
    fun handleSyncConflict(conflict: SyncConflict) {
        val currentConflicts = _syncConflicts.value.toMutableList()
        currentConflicts.add(conflict)
        _syncConflicts.value = currentConflicts
    }

    /**
     * Resolve sync conflict with user choice
     */
    fun resolveSyncConflict(conflictId: String, useLocal: Boolean) {
        serviceScope.launch {
            val currentConflicts = _syncConflicts.value.toMutableList()
            val conflict = currentConflicts.find { it.id == conflictId }
            if (conflict != null) {
                if (useLocal) {
                    Log.d(TAG, "Resolving conflict with local data: $conflictId")
                } else {
                    Log.d(TAG, "Resolving conflict with server data: $conflictId")
                }
                currentConflicts.remove(conflict)
                _syncConflicts.value = currentConflicts
            }
        }
    }

    /**
     * Check if there are unresolved sync conflicts
     */
    fun hasUnresolvedConflicts(): Boolean = _syncConflicts.value.isNotEmpty()

    /**
     * Get sync work status
     */
    fun getSyncWorkInfo(): androidx.lifecycle.LiveData<List<WorkInfo>> {
        return WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkLiveData(PERIODIC_SYNC_WORK_NAME)
    }

    /**
     * Cleanup resources
     */
    fun cleanup() {
        cancelAllSync()
        serviceScope.cancel()
    }
}
