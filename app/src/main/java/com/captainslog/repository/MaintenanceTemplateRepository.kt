package com.captainslog.repository

import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.dao.MaintenanceTemplateDao
import com.captainslog.database.dao.MaintenanceEventDao
import com.captainslog.database.dao.OfflineChangeDao
import com.captainslog.database.entities.MaintenanceTemplateEntity
import com.captainslog.database.entities.MaintenanceEventEntity
import com.captainslog.network.models.*
import com.captainslog.sync.OfflineChangeService
import com.captainslog.sync.SyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository for maintenance templates with offline support
 */
class MaintenanceTemplateRepository(
    private val connectionManager: ConnectionManager,
    private val templateDao: MaintenanceTemplateDao,
    private val eventDao: MaintenanceEventDao,
    private val offlineChangeDao: OfflineChangeDao,
    private val syncManager: SyncManager
) {
    companion object {
        private const val TAG = "MaintenanceTemplateRepository"
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    private val offlineChangeService = OfflineChangeService(offlineChangeDao)

    // Local data access
    fun getAllActiveTemplates(): Flow<List<MaintenanceTemplateEntity>> = 
        templateDao.getAllActiveTemplates()

    fun getTemplatesByBoat(boatId: String): Flow<List<MaintenanceTemplateEntity>> = 
        templateDao.getTemplatesByBoat(boatId)

    fun getTemplateById(id: String): Flow<MaintenanceTemplateEntity?> = 
        templateDao.getTemplateById(id)

    suspend fun getTemplateByIdSync(id: String): MaintenanceTemplateEntity? = 
        templateDao.getTemplateByIdSync(id)

    fun getEventsByTemplate(templateId: String): Flow<List<MaintenanceEventEntity>> = 
        eventDao.getEventsByTemplate(templateId)

    fun getUpcomingEvents(): Flow<List<MaintenanceEventEntity>> = 
        eventDao.getUpcomingEvents()

    fun getCompletedEvents(): Flow<List<MaintenanceEventEntity>> = 
        eventDao.getCompletedEvents()

    // Offline status
    fun getPendingChangeCount(): Flow<Int> = 
        offlineChangeService.getPendingChangeCount()

    fun getFailedChangeCount(): Flow<Int> = 
        offlineChangeService.getFailedChangeCount()

    suspend fun getOfflineStatus() = 
        offlineChangeService.getOfflineStatus()

    /**
     * Create a new maintenance template
     * Supports offline queuing when no connectivity
     */
    suspend fun createTemplate(
        boatId: String,
        title: String,
        description: String,
        component: String,
        recurrence: RecurrenceSchedule,
        estimatedCost: Double,
        estimatedTime: Int
    ): Result<MaintenanceTemplateEntity> {
        return try {
            val request = CreateMaintenanceTemplateRequest(
                boatId = boatId,
                title = title,
                description = description,
                component = component,
                recurrence = recurrence,
                estimatedCost = estimatedCost,
                estimatedTime = estimatedTime
            )

            if (connectionManager.hasInternetConnection()) {
                // Try online creation first
                try {
                    val apiService = connectionManager.getApiService()
                    val response = apiService.createMaintenanceTemplate(request)
                    
                    if (response.isSuccessful && response.body()?.data != null) {
                        val template = response.body()!!.data!!.toEntity()
                        templateDao.insertTemplate(template)
                        Log.d(TAG, "Created template online: ${template.title}")
                        return Result.success(template)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Online creation failed, falling back to offline", e)
                }
            }

            // Offline creation - queue for sync
            val templateEntity = MaintenanceTemplateEntity(
                id = UUID.randomUUID().toString(),
                boatId = boatId,
                title = title,
                description = description,
                component = component,
                estimatedCost = estimatedCost,
                estimatedTime = estimatedTime,
                recurrenceType = recurrence.type,
                recurrenceInterval = recurrence.interval,
                createdAt = Date(),
                updatedAt = Date()
            )

            templateDao.insertTemplate(templateEntity)
            offlineChangeService.queueTemplateCreation(request)
            
            // Trigger sync when connectivity returns
            syncManager.triggerImmediateTemplateSync()
            
            Log.d(TAG, "Created template offline: ${templateEntity.title}")
            Result.success(templateEntity)

        } catch (e: Exception) {
            Log.e(TAG, "Error creating template", e)
            Result.failure(e)
        }
    }

    /**
     * Update a maintenance template
     * Supports offline queuing when no connectivity
     */
    suspend fun updateTemplate(
        templateId: String,
        title: String? = null,
        description: String? = null,
        component: String? = null,
        recurrence: RecurrenceSchedule? = null,
        estimatedCost: Double? = null,
        estimatedTime: Int? = null,
        isActive: Boolean? = null
    ): Result<MaintenanceTemplateEntity> {
        return try {
            val request = UpdateMaintenanceTemplateRequest(
                title = title,
                description = description,
                component = component,
                recurrence = recurrence,
                estimatedCost = estimatedCost,
                estimatedTime = estimatedTime,
                isActive = isActive
            )

            // Update locally first
            val existingTemplate = templateDao.getTemplateByIdSync(templateId)
            if (existingTemplate != null) {
                val updatedTemplate = existingTemplate.copy(
                    title = title ?: existingTemplate.title,
                    description = description ?: existingTemplate.description,
                    component = component ?: existingTemplate.component,
                    estimatedCost = estimatedCost ?: existingTemplate.estimatedCost,
                    estimatedTime = estimatedTime ?: existingTemplate.estimatedTime,
                    isActive = isActive ?: existingTemplate.isActive,
                    recurrenceType = recurrence?.type ?: existingTemplate.recurrenceType,
                    recurrenceInterval = recurrence?.interval ?: existingTemplate.recurrenceInterval,
                    updatedAt = Date()
                )
                
                templateDao.updateTemplate(updatedTemplate)

                if (connectionManager.hasInternetConnection()) {
                    // Try online update
                    try {
                        val apiService = connectionManager.getApiService()
                        val response = apiService.updateMaintenanceTemplate(templateId, request)
                        
                        if (response.isSuccessful) {
                            Log.d(TAG, "Updated template online: $templateId")
                            return Result.success(updatedTemplate)
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Online update failed, queuing for offline sync", e)
                    }
                }

                // Queue for offline sync
                offlineChangeService.queueTemplateUpdate(templateId, request)
                syncManager.triggerImmediateTemplateSync()
                
                Log.d(TAG, "Updated template offline: $templateId")
                Result.success(updatedTemplate)
            } else {
                Result.failure(Exception("Template not found: $templateId"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error updating template", e)
            Result.failure(e)
        }
    }

    /**
     * Apply a schedule change to a template
     * Supports offline queuing when no connectivity
     */
    suspend fun applyScheduleChange(
        templateId: String,
        newRecurrence: RecurrenceSchedule
    ): Result<Unit> {
        return try {
            if (connectionManager.hasInternetConnection()) {
                // Try online schedule change
                try {
                    val apiService = connectionManager.getApiService()
                    val request = ScheduleChangeApplyRequest(recurrence = newRecurrence)
                    val response = apiService.applyScheduleChange(templateId, request)
                    
                    if (response.isSuccessful) {
                        // Update local template
                        val existingTemplate = templateDao.getTemplateByIdSync(templateId)
                        if (existingTemplate != null) {
                            val updatedTemplate = existingTemplate.copy(
                                recurrenceType = newRecurrence.type,
                                recurrenceInterval = newRecurrence.interval,
                                updatedAt = Date()
                            )
                            templateDao.updateTemplate(updatedTemplate)
                        }
                        
                        Log.d(TAG, "Applied schedule change online: $templateId")
                        return Result.success(Unit)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Online schedule change failed, queuing for offline sync", e)
                }
            }

            // Queue for offline sync
            offlineChangeService.queueScheduleChange(templateId, newRecurrence)
            syncManager.triggerImmediateTemplateSync()
            
            Log.d(TAG, "Queued schedule change for offline sync: $templateId")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error applying schedule change", e)
            Result.failure(e)
        }
    }

    /**
     * Apply information changes to a template
     * Supports offline queuing when no connectivity
     */
    suspend fun applyInformationChange(
        templateId: String,
        title: String? = null,
        description: String? = null,
        component: String? = null,
        estimatedCost: Double? = null,
        estimatedTime: Int? = null
    ): Result<Unit> {
        return try {
            val request = TemplateInformationChangeRequest(
                title = title,
                description = description,
                component = component,
                estimatedCost = estimatedCost,
                estimatedTime = estimatedTime
            )

            if (connectionManager.hasInternetConnection()) {
                // Try online information change
                try {
                    val apiService = connectionManager.getApiService()
                    val response = apiService.applyInformationChange(templateId, request)
                    
                    if (response.isSuccessful) {
                        // Update local template
                        val existingTemplate = templateDao.getTemplateByIdSync(templateId)
                        if (existingTemplate != null) {
                            val updatedTemplate = existingTemplate.copy(
                                title = title ?: existingTemplate.title,
                                description = description ?: existingTemplate.description,
                                component = component ?: existingTemplate.component,
                                estimatedCost = estimatedCost ?: existingTemplate.estimatedCost,
                                estimatedTime = estimatedTime ?: existingTemplate.estimatedTime,
                                updatedAt = Date()
                            )
                            templateDao.updateTemplate(updatedTemplate)
                        }
                        
                        Log.d(TAG, "Applied information change online: $templateId")
                        return Result.success(Unit)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Online information change failed, queuing for offline sync", e)
                }
            }

            // Queue for offline sync
            offlineChangeService.queueInformationChange(templateId, request)
            syncManager.triggerImmediateTemplateSync()
            
            Log.d(TAG, "Queued information change for offline sync: $templateId")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error applying information change", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a maintenance template
     * Supports offline queuing when no connectivity
     */
    suspend fun deleteTemplate(templateId: String): Result<Unit> {
        return try {
            if (connectionManager.hasInternetConnection()) {
                // Try online deletion
                try {
                    val apiService = connectionManager.getApiService()
                    val response = apiService.deleteMaintenanceTemplate(templateId)
                    
                    if (response.isSuccessful) {
                        templateDao.deleteTemplateById(templateId)
                        Log.d(TAG, "Deleted template online: $templateId")
                        return Result.success(Unit)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Online deletion failed, queuing for offline sync", e)
                }
            }

            // Mark as inactive locally and queue for deletion
            val existingTemplate = templateDao.getTemplateByIdSync(templateId)
            if (existingTemplate != null) {
                val inactiveTemplate = existingTemplate.copy(
                    isActive = false,
                    updatedAt = Date()
                )
                templateDao.updateTemplate(inactiveTemplate)
            }

            offlineChangeService.queueTemplateDeletion(templateId)
            syncManager.triggerImmediateTemplateSync()
            
            Log.d(TAG, "Queued template deletion for offline sync: $templateId")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error deleting template", e)
            Result.failure(e)
        }
    }

    /**
     * Sync templates from server
     */
    suspend fun syncTemplates(): Result<Unit> {
        return try {
            if (!connectionManager.hasInternetConnection()) {
                return Result.failure(Exception("No network connectivity"))
            }

            val apiService = connectionManager.getApiService()
            val response = apiService.getMaintenanceTemplates()
            
            if (response.isSuccessful && response.body()?.data != null) {
                val serverTemplates = response.body()!!.data!!
                
                // Update local templates
                val templateEntities = serverTemplates.map { it.toEntity() }
                templateDao.insertTemplates(templateEntities)
                
                Log.d(TAG, "Synced ${templateEntities.size} templates from server")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to fetch templates: ${response.code()}"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error syncing templates", e)
            Result.failure(e)
        }
    }

    /**
     * Check if there are pending changes for a template
     */
    suspend fun hasPendingChanges(templateId: String): Boolean {
        return offlineChangeService.hasPendingChanges(templateId)
    }

    // Extension functions to convert between API models and entities
    private fun MaintenanceTemplateResponse.toEntity(): MaintenanceTemplateEntity {
        return MaintenanceTemplateEntity(
            id = id,
            boatId = boatId,
            title = title,
            description = description,
            component = component,
            estimatedCost = estimatedCost,
            estimatedTime = estimatedTime,
            isActive = isActive,
            recurrenceType = recurrence.type,
            recurrenceInterval = recurrence.interval,
            createdAt = parseDate(createdAt),
            updatedAt = parseDate(updatedAt)
        )
    }

    private fun MaintenanceEventResponse.toEntity(): MaintenanceEventEntity {
        return MaintenanceEventEntity(
            id = id,
            templateId = templateId,
            dueDate = parseDate(dueDate),
            completedAt = completedAt?.let { parseDate(it) },
            actualCost = actualCost,
            actualTime = actualTime,
            notes = notes,
            createdAt = parseDate(createdAt),
            updatedAt = parseDate(updatedAt)
        )
    }

    private fun parseDate(dateString: String): Date {
        return try {
            dateFormat.parse(dateString) ?: Date()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse date: $dateString", e)
            Date()
        }
    }
}