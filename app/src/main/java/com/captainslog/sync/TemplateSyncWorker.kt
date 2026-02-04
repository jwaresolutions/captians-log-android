package com.captainslog.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.OfflineChangeEntity
import com.captainslog.network.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * WorkManager worker for syncing offline template changes
 */
class TemplateSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "TemplateSyncWorker"
        const val WORK_NAME = "template_sync_work"
    }

    private val database = AppDatabase.getDatabase(applicationContext)
    private val offlineChangeDao = database.offlineChangeDao()
    private val offlineChangeService = OfflineChangeService(offlineChangeDao)
    private val connectionManager = ConnectionManager.getInstance(applicationContext)
    private val syncNotificationHelper = SyncNotificationHelper(applicationContext)
    private val conflictLogger = ConflictLogger(applicationContext)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting template sync work")

            // Check if we have network connectivity
            if (!connectionManager.hasInternetConnection()) {
                Log.d(TAG, "No network connectivity, skipping sync")
                return@withContext Result.retry()
            }

            // Get pending changes
            val pendingChanges = offlineChangeDao.getPendingChangesSync()
            if (pendingChanges.isEmpty()) {
                Log.d(TAG, "No pending changes to sync")
                return@withContext Result.success()
            }

            Log.d(TAG, "Found ${pendingChanges.size} pending changes to sync")

            var successCount = 0
            var failureCount = 0
            val conflicts = mutableListOf<ConflictInfo>()

            // Process each change
            for (change in pendingChanges) {
                try {
                    val result = syncSingleChange(change)
                    when (result) {
                        is SyncResult.Success -> {
                            offlineChangeService.markAsSynced(change.id)
                            successCount++
                            Log.d(TAG, "Successfully synced change: ${change.id}")
                        }
                        is SyncResult.Conflict -> {
                            conflicts.add(result.conflictInfo)
                            offlineChangeService.incrementSyncAttempts(change.id, "Conflict detected")
                            Log.w(TAG, "Conflict detected for change: ${change.id}")
                        }
                        is SyncResult.Failure -> {
                            offlineChangeService.incrementSyncAttempts(change.id, result.error)
                            failureCount++
                            Log.e(TAG, "Failed to sync change: ${change.id}, error: ${result.error}")
                        }
                    }
                } catch (e: Exception) {
                    offlineChangeService.incrementSyncAttempts(change.id, e.message ?: "Unknown error")
                    failureCount++
                    Log.e(TAG, "Exception syncing change: ${change.id}", e)
                }
            }

            // Log conflicts for user review
            if (conflicts.isNotEmpty()) {
                conflictLogger.logConflicts(conflicts)
            }

            // Show notification with sync results
            syncNotificationHelper.showSyncResult(
                successCount = successCount,
                failureCount = failureCount,
                conflictCount = conflicts.size
            )

            Log.d(TAG, "Template sync completed: $successCount success, $failureCount failures, ${conflicts.size} conflicts")

            // Clean up old synced changes
            offlineChangeService.cleanupSyncedChanges()

            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Template sync work failed", e)
            Result.retry()
        }
    }

    /**
     * Sync a single offline change
     */
    private suspend fun syncSingleChange(change: OfflineChangeEntity): SyncResult {
        return try {
            val apiService = connectionManager.getApiService()

            when (change.changeType) {
                "template_create" -> {
                    val createData = offlineChangeService.parseChangeData(change) as? CreateMaintenanceTemplateRequest
                        ?: return SyncResult.Failure("Failed to parse create data")

                    val response = apiService.createMaintenanceTemplate(createData)
                    if (response.isSuccessful) {
                        SyncResult.Success
                    } else {
                        SyncResult.Failure("API error: ${response.code()}")
                    }
                }

                "template_update" -> {
                    val updateData = offlineChangeService.parseChangeData(change) as? UpdateMaintenanceTemplateRequest
                        ?: return SyncResult.Failure("Failed to parse update data")

                    // Check for conflicts by comparing timestamps
                    val serverTemplate = apiService.getMaintenanceTemplate(change.entityId)
                    if (serverTemplate.isSuccessful && serverTemplate.body()?.data != null) {
                        val serverUpdatedAt = parseDate(serverTemplate.body()!!.data!!.updatedAt)
                        if (serverUpdatedAt.after(change.timestamp)) {
                            // Server has newer data - conflict detected
                            return SyncResult.Conflict(
                                ConflictInfo(
                                    changeId = change.id,
                                    entityType = change.entityType,
                                    entityId = change.entityId,
                                    localTimestamp = change.timestamp,
                                    serverTimestamp = serverUpdatedAt,
                                    conflictType = "template_update"
                                )
                            )
                        }
                    }

                    val response = apiService.updateMaintenanceTemplate(change.entityId, updateData)
                    if (response.isSuccessful) {
                        SyncResult.Success
                    } else {
                        SyncResult.Failure("API error: ${response.code()}")
                    }
                }

                "schedule_change" -> {
                    val scheduleData = offlineChangeService.parseChangeData(change) as? ScheduleChangeApplyRequest
                        ?: return SyncResult.Failure("Failed to parse schedule data")

                    val response = apiService.applyScheduleChange(change.entityId, scheduleData)
                    if (response.isSuccessful) {
                        SyncResult.Success
                    } else {
                        SyncResult.Failure("API error: ${response.code()}")
                    }
                }

                "information_change" -> {
                    val infoData = offlineChangeService.parseChangeData(change) as? TemplateInformationChangeRequest
                        ?: return SyncResult.Failure("Failed to parse information data")

                    val response = apiService.applyInformationChange(change.entityId, infoData)
                    if (response.isSuccessful) {
                        SyncResult.Success
                    } else {
                        SyncResult.Failure("API error: ${response.code()}")
                    }
                }

                "template_delete" -> {
                    val response = apiService.deleteMaintenanceTemplate(change.entityId)
                    if (response.isSuccessful) {
                        SyncResult.Success
                    } else {
                        SyncResult.Failure("API error: ${response.code()}")
                    }
                }

                else -> {
                    SyncResult.Failure("Unknown change type: ${change.changeType}")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error syncing change ${change.id}", e)
            SyncResult.Failure(e.message ?: "Unknown error")
        }
    }

    /**
     * Parse date string to Date object
     */
    private fun parseDate(dateString: String): Date {
        return try {
            // Assuming ISO format from server
            java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.parse(dateString) ?: Date()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse date: $dateString", e)
            Date()
        }
    }
}

/**
 * Sealed class representing sync results
 */
sealed class SyncResult {
    object Success : SyncResult()
    data class Failure(val error: String) : SyncResult()
    data class Conflict(val conflictInfo: ConflictInfo) : SyncResult()
}

/**
 * Data class representing a sync conflict
 */
data class ConflictInfo(
    val changeId: String,
    val entityType: String,
    val entityId: String,
    val localTimestamp: Date,
    val serverTimestamp: Date,
    val conflictType: String
)