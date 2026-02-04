package com.captainslog.sync

import android.content.Context
import android.util.Log
import androidx.work.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import com.captainslog.network.NetworkMonitor

/**
 * Enhanced sync status manager with visual indicators and aggressive retry logic
 * 
 * Status States:
 * - SUCCESS (Green): Last sync successful
 * - IN_PROGRESS (Blue/Animated): Sync currently running
 * - WARNING (Yellow): First sync failure, retrying every 30 seconds
 * - ERROR (Red): Failed for 5+ minutes, still retrying but less frequently
 */
class SyncStatusManager(private val context: Context) {

    companion object {
        const val TAG = "SyncStatusManager"
        const val RETRY_SYNC_WORK_NAME = "retry_sync_work"
        
        // Retry intervals
        private const val FAST_RETRY_INTERVAL_SECONDS = 30L  // 30 seconds for first failures
        private const val FAST_RETRY_DURATION_MINUTES = 5L   // Fast retry for 5 minutes
        private const val SLOW_RETRY_INTERVAL_MINUTES = 2L   // 2 minutes after 5 minutes of failures
        
        @Volatile
        private var INSTANCE: SyncStatusManager? = null

        fun getInstance(context: Context): SyncStatusManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SyncStatusManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Sync status states
     */
    enum class SyncStatus {
        SUCCESS,     // Green - last sync successful
        IN_PROGRESS, // Blue/Animated - sync currently running
        WARNING,     // Yellow - first failure, fast retry
        ERROR        // Red - failed for 5+ minutes, slow retry
    }

    // Sync status tracking
    private val _syncStatus = MutableStateFlow(SyncStatus.SUCCESS)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    // Failure tracking
    private var firstFailureTime: Long? = null
    private var consecutiveFailures = 0
    private var retryJob: Job? = null

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Report sync started
     */
    fun reportSyncStarted() {
        Log.d(TAG, "Sync started")
        _syncStatus.value = SyncStatus.IN_PROGRESS
        _syncMessage.value = "Syncing..."
    }

    /**
     * Report sync success
     */
    fun reportSyncSuccess(syncedCount: Int = 0) {
        Log.d(TAG, "Sync successful: $syncedCount items")
        
        // Reset failure tracking
        firstFailureTime = null
        consecutiveFailures = 0
        cancelRetryWork()
        
        // Update status
        _syncStatus.value = SyncStatus.SUCCESS
        _lastSyncTime.value = System.currentTimeMillis()
        // Sync success proves server is reachable - update NetworkMonitor to dismiss "Cannot Reach Server" banner
        NetworkMonitor.getInstance(context).reportServerReachable()
        _syncMessage.value = if (syncedCount > 0) {
            "Synced $syncedCount item(s)"
        } else {
            "Up to date"
        }
    }

    /**
     * Report sync failure
     */
    fun reportSyncFailure(errorMessage: String) {
        Log.w(TAG, "Sync failed: $errorMessage")
        
        val currentTime = System.currentTimeMillis()
        
        // Track failure timing
        if (firstFailureTime == null) {
            firstFailureTime = currentTime
        }
        consecutiveFailures++
        
        // Determine status based on failure duration
        val failureDurationMinutes = (currentTime - (firstFailureTime ?: currentTime)) / (1000 * 60)
        
        if (failureDurationMinutes >= FAST_RETRY_DURATION_MINUTES) {
            // Failed for 5+ minutes - red status, slow retry
            _syncStatus.value = SyncStatus.ERROR
            _syncMessage.value = "Sync failed (${consecutiveFailures} attempts)"
            scheduleSlowRetry()
        } else {
            // First failure or within 5 minutes - yellow status, fast retry
            _syncStatus.value = SyncStatus.WARNING
            _syncMessage.value = "Sync issue, retrying..."
            scheduleFastRetry()
        }
    }

    /**
     * Schedule fast retry (every 30 seconds)
     */
    private fun scheduleFastRetry() {
        cancelRetryWork()
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val retryRequest = PeriodicWorkRequestBuilder<RetrySyncWorker>(
            FAST_RETRY_INTERVAL_SECONDS,
            TimeUnit.SECONDS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            RETRY_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            retryRequest
        )

        Log.d(TAG, "Scheduled fast retry every $FAST_RETRY_INTERVAL_SECONDS seconds")
    }

    /**
     * Schedule slow retry (every 2 minutes after 5 minutes of failures)
     */
    private fun scheduleSlowRetry() {
        cancelRetryWork()
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val retryRequest = PeriodicWorkRequestBuilder<RetrySyncWorker>(
            SLOW_RETRY_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            RETRY_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            retryRequest
        )

        Log.d(TAG, "Scheduled slow retry every $SLOW_RETRY_INTERVAL_MINUTES minutes")
    }

    /**
     * Cancel retry work
     */
    private fun cancelRetryWork() {
        WorkManager.getInstance(context).cancelUniqueWork(RETRY_SYNC_WORK_NAME)
        retryJob?.cancel()
    }

    /**
     * Force immediate retry
     */
    fun forceRetry() {
        Log.d(TAG, "Force retry requested")
        
        // Trigger immediate sync
        val syncManager = SyncManager.getInstance(context)
        syncManager.triggerImmediateSync()
        syncManager.triggerImmediatePhotoSync()
        syncManager.triggerImmediateTemplateSync()
    }

    /**
     * Reset sync status (for manual refresh)
     */
    fun resetStatus() {
        Log.d(TAG, "Sync status reset")
        
        firstFailureTime = null
        consecutiveFailures = 0
        cancelRetryWork()
        
        _syncStatus.value = SyncStatus.SUCCESS
        _syncMessage.value = "Ready to sync"
    }

    /**
     * Get human-readable status message
     */
    fun getStatusMessage(): String {
        return when (_syncStatus.value) {
            SyncStatus.SUCCESS -> _syncMessage.value ?: "Up to date"
            SyncStatus.IN_PROGRESS -> "Syncing..."
            SyncStatus.WARNING -> "Connection issue, retrying..."
            SyncStatus.ERROR -> "Sync failed, retrying less frequently"
        }
    }

    /**
     * Get status color for UI
     */
    fun getStatusColor(): Int {
        return when (_syncStatus.value) {
            SyncStatus.SUCCESS -> android.graphics.Color.GREEN
            SyncStatus.IN_PROGRESS -> android.graphics.Color.BLUE
            SyncStatus.WARNING -> android.graphics.Color.parseColor("#FFA500") // Orange/Yellow
            SyncStatus.ERROR -> android.graphics.Color.RED
        }
    }

    /**
     * Check if should show animated progress indicator
     */
    fun shouldShowProgress(): Boolean {
        return _syncStatus.value == SyncStatus.IN_PROGRESS
    }

    /**
     * Get failure statistics for debugging
     */
    fun getFailureStats(): String {
        val firstFailure = firstFailureTime
        return if (firstFailure != null) {
            val durationMinutes = (System.currentTimeMillis() - firstFailure) / (1000 * 60)
            "Failed for ${durationMinutes}m, $consecutiveFailures attempts"
        } else {
            "No recent failures"
        }
    }

    /**
     * Cleanup resources
     */
    fun cleanup() {
        cancelRetryWork()
        serviceScope.cancel()
    }
}

/**
 * WorkManager worker for retry sync operations
 */
class RetrySyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "RetrySyncWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Retry sync worker started")
            
            val syncStatusManager = SyncStatusManager.getInstance(applicationContext)
            val syncManager = SyncManager.getInstance(applicationContext)
            
            // Report sync started
            syncStatusManager.reportSyncStarted()
            
            // Attempt sync
            syncManager.triggerImmediateSync()
            
            // Note: The actual success/failure will be reported by the individual sync workers
            // This worker just triggers the retry
            
            return@withContext Result.success()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in retry sync worker", e)
            return@withContext Result.retry()
        }
    }
}