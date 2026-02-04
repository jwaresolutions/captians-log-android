package com.captainslog.sync

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Manager for scheduling and controlling sync operations
 */
class SyncManager(private val context: Context) {

    companion object {
        const val TAG = "SyncManager"
        const val PERIODIC_SYNC_WORK_NAME = "periodic_trip_sync"
        const val ONE_TIME_SYNC_WORK_NAME = "one_time_trip_sync"
        const val PERIODIC_PHOTO_SYNC_WORK_NAME = "periodic_photo_sync"
        const val ONE_TIME_PHOTO_SYNC_WORK_NAME = "one_time_photo_sync"
        const val PERIODIC_TEMPLATE_SYNC_WORK_NAME = "periodic_template_sync"
        const val ONE_TIME_TEMPLATE_SYNC_WORK_NAME = "one_time_template_sync"
        
        // Sync every 15 minutes when online
        private const val SYNC_INTERVAL_MINUTES = 15L
        // Photo sync every 30 minutes (less frequent due to WiFi requirement)
        private const val PHOTO_SYNC_INTERVAL_MINUTES = 30L
        // Template sync every 10 minutes (more frequent for offline changes)
        private const val TEMPLATE_SYNC_INTERVAL_MINUTES = 10L

        @Volatile
        private var INSTANCE: SyncManager? = null

        fun getInstance(context: Context): SyncManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SyncManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Schedule periodic sync work
     * This will run every 15 minutes when the device has internet connectivity
     */
    fun schedulePeriodicSync() {
        // Schedule trip sync
        val tripConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val tripSyncRequest = PeriodicWorkRequestBuilder<TripSyncWorker>(
            SYNC_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
            .setConstraints(tripConstraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            tripSyncRequest
        )

        // Schedule photo sync (WiFi only)
        val photoConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // WiFi only
            .build()

        val photoSyncRequest = PeriodicWorkRequestBuilder<PhotoSyncWorker>(
            PHOTO_SYNC_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
            .setConstraints(photoConstraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_PHOTO_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            photoSyncRequest
        )

        // Schedule template sync (any network connection)
        val templateConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val templateSyncRequest = PeriodicWorkRequestBuilder<TemplateSyncWorker>(
            TEMPLATE_SYNC_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
            .setConstraints(templateConstraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_TEMPLATE_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            templateSyncRequest
        )

        Log.d(TAG, "Scheduled periodic trip sync every $SYNC_INTERVAL_MINUTES minutes")
        Log.d(TAG, "Scheduled periodic photo sync every $PHOTO_SYNC_INTERVAL_MINUTES minutes (WiFi only)")
        Log.d(TAG, "Scheduled periodic template sync every $TEMPLATE_SYNC_INTERVAL_MINUTES minutes")
    }

    /**
     * Trigger an immediate one-time sync
     * Useful for manual sync or after completing a trip
     */
    fun triggerImmediateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<TripSyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_TIME_SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )

        Log.d(TAG, "Triggered immediate trip sync")
    }

    /**
     * Trigger an immediate photo sync
     * Only works if on WiFi
     */
    fun triggerImmediatePhotoSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // WiFi only
            .build()

        val photoSyncRequest = OneTimeWorkRequestBuilder<PhotoSyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_TIME_PHOTO_SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            photoSyncRequest
        )

        Log.d(TAG, "Triggered immediate photo sync (WiFi only)")
    }

    /**
     * Trigger an immediate template sync
     * Works on any network connection
     */
    fun triggerImmediateTemplateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val templateSyncRequest = OneTimeWorkRequestBuilder<TemplateSyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_TIME_TEMPLATE_SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            templateSyncRequest
        )

        Log.d(TAG, "Triggered immediate template sync")
    }

    /**
     * Cancel all sync work
     */
    fun cancelAllSync() {
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_SYNC_WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(ONE_TIME_SYNC_WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_PHOTO_SYNC_WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(ONE_TIME_PHOTO_SYNC_WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_TEMPLATE_SYNC_WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(ONE_TIME_TEMPLATE_SYNC_WORK_NAME)
        Log.d(TAG, "Cancelled all sync work")
    }

    /**
     * Get sync work status
     */
    fun getSyncWorkInfo(): androidx.lifecycle.LiveData<List<WorkInfo>> {
        return WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkLiveData(PERIODIC_SYNC_WORK_NAME)
    }

    /**
     * Get sync work status
     */
    fun getSyncStatus(): androidx.lifecycle.LiveData<List<WorkInfo>> {
        return WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkLiveData(PERIODIC_SYNC_WORK_NAME)
    }
}
