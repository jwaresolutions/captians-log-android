package com.captainslog.sync.handlers

import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.database.dao.MaintenanceEventDao
import com.captainslog.database.dao.MaintenanceTemplateDao
import com.captainslog.database.dao.OfflineChangeDao
import com.captainslog.database.entities.MaintenanceEventEntity
import com.captainslog.database.entities.MaintenanceTemplateEntity
import com.captainslog.sync.DataType
import com.captainslog.sync.HandlerSyncResult
import com.captainslog.sync.OfflineChangeService
import com.captainslog.sync.SyncHandler
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateSyncHandler @Inject constructor(
    private val templateDao: MaintenanceTemplateDao,
    private val eventDao: MaintenanceEventDao,
    private val offlineChangeDao: OfflineChangeDao,
    private val offlineChangeService: OfflineChangeService,
    private val connectionManager: ConnectionManager,
    private val database: AppDatabase
) : SyncHandler {

    companion object {
        private const val TAG = "TemplateSyncHandler"
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    override val dataType = DataType.TEMPLATES

    override suspend fun syncFromServer(): HandlerSyncResult {
        val errors = mutableListOf<String>()
        var syncedCount = 0

        // Sync templates
        try {
            Log.d(TAG, "Syncing maintenance templates from server...")
            val apiService = connectionManager.getApiService()
            val response = apiService.getMaintenanceTemplates()

            if (response.isSuccessful && response.body() != null) {
                val apiTemplates = response.body()!!.data
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
                        createdAt = dateFormat.parse(t.createdAt) ?: Date(),
                        updatedAt = dateFormat.parse(t.updatedAt) ?: Date()
                    )
                }
                templateDao.insertTemplates(templateEntities)
                syncedCount += templateEntities.size
                Log.d(TAG, "Upserted ${templateEntities.size} maintenance templates")
            } else {
                errors.add("Failed to fetch templates: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing templates from server", e)
            errors.add("Template sync error: ${e.message}")
        }

        // Sync events
        try {
            Log.d(TAG, "Syncing maintenance events from server...")
            val apiService = connectionManager.getApiService()
            val allEvents = mutableListOf<MaintenanceEventEntity>()

            val upcomingResponse = apiService.getUpcomingMaintenanceEvents()
            if (upcomingResponse.isSuccessful && upcomingResponse.body() != null) {
                allEvents.addAll(upcomingResponse.body()!!.data.map { e ->
                    MaintenanceEventEntity(
                        id = e.id,
                        templateId = e.templateId,
                        dueDate = dateFormat.parse(e.dueDate) ?: Date(),
                        completedAt = e.completedAt?.let { dateFormat.parse(it) },
                        actualCost = e.actualCost,
                        actualTime = e.actualTime,
                        notes = e.notes,
                        createdAt = dateFormat.parse(e.createdAt) ?: Date(),
                        updatedAt = dateFormat.parse(e.updatedAt) ?: Date()
                    )
                })
            }

            val completedResponse = apiService.getCompletedMaintenanceEvents()
            if (completedResponse.isSuccessful && completedResponse.body() != null) {
                allEvents.addAll(completedResponse.body()!!.data.map { e ->
                    MaintenanceEventEntity(
                        id = e.id,
                        templateId = e.templateId,
                        dueDate = dateFormat.parse(e.dueDate) ?: Date(),
                        completedAt = e.completedAt?.let { dateFormat.parse(it) },
                        actualCost = e.actualCost,
                        actualTime = e.actualTime,
                        notes = e.notes,
                        createdAt = dateFormat.parse(e.createdAt) ?: Date(),
                        updatedAt = dateFormat.parse(e.updatedAt) ?: Date()
                    )
                })
            }

            if (allEvents.isNotEmpty()) {
                eventDao.insertEvents(allEvents)
                syncedCount += allEvents.size
                Log.d(TAG, "Upserted ${allEvents.size} maintenance events")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing events from server", e)
            errors.add("Event sync error: ${e.message}")
        }

        return HandlerSyncResult(
            success = errors.isEmpty(),
            syncedCount = syncedCount,
            errors = errors
        )
    }

    override suspend fun syncToServer(): HandlerSyncResult {
        return try {
            Log.d(TAG, "Syncing offline template changes to server...")
            val pendingChanges = offlineChangeDao.getPendingChangesSync()
            if (pendingChanges.isEmpty()) {
                Log.d(TAG, "No pending template changes to sync")
                return HandlerSyncResult(success = true)
            }

            val apiService = connectionManager.getApiService()
            var successCount = 0
            val errors = mutableListOf<String>()

            for (change in pendingChanges) {
                try {
                    val changeData = offlineChangeService.parseChangeData(change)
                    val result = when (change.changeType) {
                        "template_create" -> {
                            val data = changeData as? com.captainslog.network.models.CreateMaintenanceTemplateRequest
                                ?: continue
                            apiService.createMaintenanceTemplate(data).isSuccessful
                        }
                        "template_update" -> {
                            val data = changeData as? com.captainslog.network.models.UpdateMaintenanceTemplateRequest
                                ?: continue
                            apiService.updateMaintenanceTemplate(change.entityId, data).isSuccessful
                        }
                        "schedule_change" -> {
                            val data = changeData as? com.captainslog.network.models.ScheduleChangeApplyRequest
                                ?: continue
                            apiService.applyScheduleChange(change.entityId, data).isSuccessful
                        }
                        "information_change" -> {
                            val data = changeData as? com.captainslog.network.models.TemplateInformationChangeRequest
                                ?: continue
                            apiService.applyInformationChange(change.entityId, data).isSuccessful
                        }
                        "template_delete" -> {
                            apiService.deleteMaintenanceTemplate(change.entityId).isSuccessful
                        }
                        else -> false
                    }

                    if (result) {
                        offlineChangeService.markAsSynced(change.id)
                        successCount++
                    } else {
                        offlineChangeService.incrementSyncAttempts(change.id, "API call failed")
                        errors.add("Failed to sync change: ${change.id}")
                    }
                } catch (e: Exception) {
                    offlineChangeService.incrementSyncAttempts(change.id, e.message ?: "Unknown error")
                    errors.add("Error syncing change ${change.id}: ${e.message}")
                }
            }

            offlineChangeService.cleanupSyncedChanges()

            HandlerSyncResult(
                success = errors.isEmpty(),
                syncedCount = successCount,
                errors = errors
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing templates to server", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }

    override suspend fun syncEntity(entityId: String): HandlerSyncResult {
        // For templates, syncing a specific entity means processing its pending offline changes
        return try {
            val pendingChanges = offlineChangeDao.getPendingChangesForEntity("maintenance_template", entityId)
            if (pendingChanges.isEmpty()) {
                Log.d(TAG, "No pending changes for template: $entityId")
                return HandlerSyncResult(success = true)
            }

            val apiService = connectionManager.getApiService()
            var successCount = 0
            val errors = mutableListOf<String>()

            for (change in pendingChanges) {
                try {
                    val changeData = offlineChangeService.parseChangeData(change)
                    val result = when (change.changeType) {
                        "template_create" -> {
                            val data = changeData as? com.captainslog.network.models.CreateMaintenanceTemplateRequest
                                ?: continue
                            apiService.createMaintenanceTemplate(data).isSuccessful
                        }
                        "template_update" -> {
                            val data = changeData as? com.captainslog.network.models.UpdateMaintenanceTemplateRequest
                                ?: continue
                            apiService.updateMaintenanceTemplate(change.entityId, data).isSuccessful
                        }
                        else -> false
                    }

                    if (result) {
                        offlineChangeService.markAsSynced(change.id)
                        successCount++
                    } else {
                        errors.add("Failed to sync change: ${change.id}")
                    }
                } catch (e: Exception) {
                    errors.add("Error: ${e.message}")
                }
            }

            HandlerSyncResult(success = errors.isEmpty(), syncedCount = successCount, errors = errors)
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing template entity: $entityId", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }
}
