package com.captainslog.viewmodel

import androidx.lifecycle.viewModelScope
import com.captainslog.database.dao.MaintenanceTemplateDao
import com.captainslog.database.dao.MaintenanceEventDao
import com.captainslog.database.entities.MaintenanceTemplateEntity
import com.captainslog.database.entities.MaintenanceEventEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MaintenanceTemplateViewModel @Inject constructor(
    private val templateDao: MaintenanceTemplateDao,
    private val eventDao: MaintenanceEventDao
) : BaseViewModel() {

    // Templates (Schedule tab)
    val allTemplates: Flow<List<MaintenanceTemplateEntity>> = templateDao.getAllActiveTemplates()

    // Events (Upcoming and Complete tabs)
    val upcomingEvents: Flow<List<MaintenanceEventEntity>> = eventDao.getUpcomingEvents()
    val completedEvents: Flow<List<MaintenanceEventEntity>> = eventDao.getCompletedEvents()

    fun getTemplateById(id: String): Flow<MaintenanceTemplateEntity?> {
        return templateDao.getTemplateById(id)
    }

    fun getEventById(id: String): Flow<MaintenanceEventEntity?> {
        return eventDao.getEventById(id)
    }

    fun getEventsByTemplate(templateId: String): Flow<List<MaintenanceEventEntity>> {
        return eventDao.getEventsByTemplate(templateId)
    }

    fun createTemplate(
        boatId: String,
        title: String,
        description: String,
        component: String,
        estimatedCost: Double?,
        estimatedTime: Int?,
        recurrenceType: String,
        recurrenceInterval: Int
    ) {
        launchWithErrorHandling(
            onSuccess = { setSuccess("Template created successfully") }
        ) {
            val template = MaintenanceTemplateEntity(
                boatId = boatId,
                title = title,
                description = description,
                component = component,
                estimatedCost = estimatedCost,
                estimatedTime = estimatedTime,
                recurrenceType = recurrenceType,
                recurrenceInterval = recurrenceInterval
            )

            // Save locally first
            templateDao.insertTemplate(template)

            // Generate the first recurring event
            generateNextEvent(template)

            // TODO: Sync to backend API
            // connectionManager.createMaintenanceTemplate(template)
        }
    }

    fun updateTemplate(template: MaintenanceTemplateEntity) {
        launchWithErrorHandling(
            onSuccess = { setSuccess("Template updated successfully") }
        ) {
            val updatedTemplate = template.copy(updatedAt = Date())
            templateDao.updateTemplate(updatedTemplate)

            // TODO: Sync to backend API
            // connectionManager.updateMaintenanceTemplate(updatedTemplate)
        }
    }

    fun deleteTemplate(templateId: String) {
        launchWithErrorHandling(
            onSuccess = { setSuccess("Template deleted successfully") }
        ) {
            templateDao.deleteTemplateById(templateId)

            // TODO: Sync to backend API
            // connectionManager.deleteMaintenanceTemplate(templateId)
        }
    }

    fun completeEvent(
        eventId: String,
        actualCost: Double?,
        actualTime: Int?,
        notes: String?
    ) {
        launchWithErrorHandling(
            onSuccess = { setSuccess("Event completed successfully") }
        ) {
            eventDao.completeEvent(eventId, Date(), actualCost, actualTime, notes)

            // Generate next recurring event from template
            val completedEvent = eventDao.getEventByIdSync(eventId)
            if (completedEvent != null) {
                val template = templateDao.getTemplateByIdSync(completedEvent.templateId)
                if (template != null && template.isActive) {
                    generateNextEvent(template, completedEvent.dueDate)
                }
            }

            // TODO: Sync to backend API
            // connectionManager.completeMaintenanceEvent(eventId, actualCost, actualTime, notes)
        }
    }

    fun formatRecurrence(template: MaintenanceTemplateEntity): String {
        val interval = template.recurrenceInterval
        val type = template.recurrenceType

        return when (type) {
            "days" -> if (interval == 1) "Daily" else "Every $interval days"
            "weeks" -> if (interval == 1) "Weekly" else "Every $interval weeks"
            "months" -> if (interval == 1) "Monthly" else "Every $interval months"
            "years" -> if (interval == 1) "Yearly" else "Every $interval years"
            "engine_hours" -> "Every $interval engine hours"
            else -> "Every $interval $type"
        }
    }

    fun getDaysUntilDue(event: MaintenanceEventEntity): Long {
        val now = Date()
        val diffInMillis = event.dueDate.time - now.time
        return diffInMillis / (1000 * 60 * 60 * 24)
    }

    fun getEventColor(event: MaintenanceEventEntity): TaskColor {
        if (event.completedAt != null) return TaskColor.GREEN

        val daysUntilDue = getDaysUntilDue(event)
        return when {
            daysUntilDue < 0 -> TaskColor.RED // Overdue
            daysUntilDue <= 7 -> TaskColor.YELLOW // Due soon
            else -> TaskColor.GRAY // Future
        }
    }

    fun clearMessage() {
        clearSuccess()
    }

    private suspend fun generateNextEvent(template: MaintenanceTemplateEntity, fromDate: Date = Date()) {
        val calendar = Calendar.getInstance()
        calendar.time = fromDate

        when (template.recurrenceType) {
            "days" -> calendar.add(Calendar.DAY_OF_YEAR, template.recurrenceInterval)
            "weeks" -> calendar.add(Calendar.WEEK_OF_YEAR, template.recurrenceInterval)
            "months" -> calendar.add(Calendar.MONTH, template.recurrenceInterval)
            "years" -> calendar.add(Calendar.YEAR, template.recurrenceInterval)
            "engine_hours" -> calendar.add(Calendar.MONTH, template.recurrenceInterval) // fallback for engine hours
        }

        val event = MaintenanceEventEntity(
            templateId = template.id,
            dueDate = calendar.time
        )
        eventDao.insertEvent(event)
    }
}

/**
 * Color coding for maintenance event status
 */
enum class TaskColor {
    RED,      // Overdue
    YELLOW,   // Due soon (within 7 days)
    GREEN,    // Completed
    GRAY      // Future (not due soon)
}
