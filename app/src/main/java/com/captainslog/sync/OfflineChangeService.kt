package com.captainslog.sync

import android.util.Log
import com.captainslog.database.dao.OfflineChangeDao
import com.captainslog.database.entities.OfflineChangeEntity
import com.captainslog.network.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Service for managing offline changes and synchronization
 */
class OfflineChangeService(
    private val offlineChangeDao: OfflineChangeDao
) {
    companion object {
        private const val TAG = "OfflineChangeService"
        private const val MAX_SYNC_ATTEMPTS = 5
        private val gson = Gson()
    }

    /**
     * Queue a template creation for offline sync
     */
    suspend fun queueTemplateCreation(
        templateData: CreateMaintenanceTemplateRequest
    ): OfflineChangeEntity {
        val changeData = gson.toJson(templateData)
        val change = OfflineChangeEntity(
            entityType = "maintenance_template",
            entityId = UUID.randomUUID().toString(), // Temporary ID for creation
            changeType = "template_create",
            changeData = changeData,
            timestamp = Date()
        )
        
        offlineChangeDao.insertChange(change)
        Log.d(TAG, "Queued template creation for offline sync: ${change.id}")
        return change
    }

    /**
     * Queue a template update for offline sync
     */
    suspend fun queueTemplateUpdate(
        templateId: String,
        updateData: UpdateMaintenanceTemplateRequest
    ): OfflineChangeEntity {
        val changeData = gson.toJson(updateData)
        val change = OfflineChangeEntity(
            entityType = "maintenance_template",
            entityId = templateId,
            changeType = "template_update",
            changeData = changeData,
            timestamp = Date()
        )
        
        offlineChangeDao.insertChange(change)
        Log.d(TAG, "Queued template update for offline sync: $templateId")
        return change
    }

    /**
     * Queue a schedule change for offline sync
     */
    suspend fun queueScheduleChange(
        templateId: String,
        newRecurrence: RecurrenceSchedule
    ): OfflineChangeEntity {
        val changeData = gson.toJson(ScheduleChangeApplyRequest(recurrence = newRecurrence, offline = true))
        val change = OfflineChangeEntity(
            entityType = "maintenance_template",
            entityId = templateId,
            changeType = "schedule_change",
            changeData = changeData,
            timestamp = Date()
        )
        
        offlineChangeDao.insertChange(change)
        Log.d(TAG, "Queued schedule change for offline sync: $templateId")
        return change
    }

    /**
     * Queue an information change for offline sync
     */
    suspend fun queueInformationChange(
        templateId: String,
        changes: TemplateInformationChangeRequest
    ): OfflineChangeEntity {
        val changeData = gson.toJson(changes)
        val change = OfflineChangeEntity(
            entityType = "maintenance_template",
            entityId = templateId,
            changeType = "information_change",
            changeData = changeData,
            timestamp = Date()
        )
        
        offlineChangeDao.insertChange(change)
        Log.d(TAG, "Queued information change for offline sync: $templateId")
        return change
    }

    /**
     * Queue a template deletion for offline sync
     */
    suspend fun queueTemplateDeletion(templateId: String): OfflineChangeEntity {
        val change = OfflineChangeEntity(
            entityType = "maintenance_template",
            entityId = templateId,
            changeType = "template_delete",
            changeData = "{}",
            timestamp = Date()
        )
        
        offlineChangeDao.insertChange(change)
        Log.d(TAG, "Queued template deletion for offline sync: $templateId")
        return change
    }

    /**
     * Get pending changes as a Flow for UI observation
     */
    fun getPendingChanges(): Flow<List<OfflineChangeEntity>> {
        return offlineChangeDao.getPendingChanges()
    }

    /**
     * Get pending change count for UI indicators
     */
    fun getPendingChangeCount(): Flow<Int> {
        return offlineChangeDao.getPendingChangeCount()
    }

    /**
     * Get failed change count for error indicators
     */
    fun getFailedChangeCount(): Flow<Int> {
        return offlineChangeDao.getFailedChangeCount()
    }

    /**
     * Get pending changes for a specific entity
     */
    suspend fun getPendingChangesForEntity(entityType: String, entityId: String): List<OfflineChangeEntity> {
        return offlineChangeDao.getPendingChangesForEntity(entityType, entityId)
    }

    /**
     * Mark a change as successfully synced
     */
    suspend fun markAsSynced(changeId: String) {
        offlineChangeDao.markAsSynced(changeId, Date())
        Log.d(TAG, "Marked change as synced: $changeId")
    }

    /**
     * Increment sync attempts for a failed change
     */
    suspend fun incrementSyncAttempts(changeId: String, error: String) {
        offlineChangeDao.incrementSyncAttempts(changeId, Date(), error)
        Log.w(TAG, "Incremented sync attempts for change: $changeId, error: $error")
    }

    /**
     * Remove a queued change (e.g., if user cancels or resolves conflict)
     */
    suspend fun removeChange(changeId: String) {
        offlineChangeDao.deleteChange(changeId)
        Log.d(TAG, "Removed queued change: $changeId")
    }

    /**
     * Clean up old synced changes
     */
    suspend fun cleanupSyncedChanges(olderThanDays: Int = 7): Int {
        val cutoffDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -olderThanDays)
        }.time
        
        val deletedCount = offlineChangeDao.cleanupSyncedChanges(cutoffDate)
        Log.d(TAG, "Cleaned up $deletedCount synced changes older than $olderThanDays days")
        return deletedCount
    }

    /**
     * Check if there are pending changes for a template
     */
    suspend fun hasPendingChanges(templateId: String): Boolean {
        val changes = getPendingChangesForEntity("maintenance_template", templateId)
        return changes.isNotEmpty()
    }

    /**
     * Get the most recent change timestamp for conflict resolution
     */
    suspend fun getMostRecentChangeTimestamp(templateId: String): Date? {
        val changes = getPendingChangesForEntity("maintenance_template", templateId)
        return changes.maxByOrNull { it.timestamp }?.timestamp
    }

    /**
     * Parse change data based on change type
     */
    fun parseChangeData(change: OfflineChangeEntity): Any? {
        return try {
            when (change.changeType) {
                "template_create" -> {
                    gson.fromJson(change.changeData, CreateMaintenanceTemplateRequest::class.java)
                }
                "template_update" -> {
                    gson.fromJson(change.changeData, UpdateMaintenanceTemplateRequest::class.java)
                }
                "schedule_change" -> {
                    gson.fromJson(change.changeData, ScheduleChangeApplyRequest::class.java)
                }
                "information_change" -> {
                    gson.fromJson(change.changeData, TemplateInformationChangeRequest::class.java)
                }
                "template_delete" -> {
                    null // No data needed for deletion
                }
                else -> {
                    Log.w(TAG, "Unknown change type: ${change.changeType}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse change data for ${change.id}", e)
            null
        }
    }

    /**
     * Get a summary of offline status for UI display
     */
    suspend fun getOfflineStatus(): OfflineStatus {
        val pendingChanges = offlineChangeDao.getPendingChangesSync()
        val failedChanges = pendingChanges.filter { it.syncAttempts >= MAX_SYNC_ATTEMPTS }
        
        return OfflineStatus(
            hasPendingChanges = pendingChanges.isNotEmpty(),
            pendingCount = pendingChanges.size,
            failedCount = failedChanges.size,
            lastChangeTimestamp = pendingChanges.maxByOrNull { it.timestamp }?.timestamp
        )
    }
}

/**
 * Data class representing offline status for UI display
 */
data class OfflineStatus(
    val hasPendingChanges: Boolean,
    val pendingCount: Int,
    val failedCount: Int,
    val lastChangeTimestamp: Date?
)