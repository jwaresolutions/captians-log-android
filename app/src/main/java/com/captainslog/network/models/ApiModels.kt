package com.captainslog.network.models

import com.google.gson.annotations.SerializedName

// Wrapper response models for backend API
data class ApiListResponse<T>(
    val data: List<T>,
    val count: Int,
    val timestamp: String
)

data class ApiSuccessResponse<T>(
    val success: Boolean,
    val data: T
)

// Generic wrapper for single-item responses: { data: T, timestamp: "..." }
data class ApiDataResponse<T>(
    val data: T,
    val timestamp: String? = null
)

// Notifications list response: { notifications: [...], count: N }
data class NotificationsListResponse(
    val notifications: List<NotificationResponse>,
    val count: Int
)

// Boat models
data class BoatResponse(
    val id: String,
    val name: String,
    val enabled: Boolean,
    val isActive: Boolean,
    val metadata: Map<String, Any>?,
    val createdAt: String,
    val updatedAt: String
)

data class CreateBoatRequest(
    val name: String,
    val metadata: Map<String, Any>? = null
)

// Trip models
data class TripResponse(
    val id: String,
    val boatId: String,
    val startTime: String,
    val endTime: String?,
    val waterType: String,
    val role: String,
    val gpsPoints: List<GpsPointResponse>?,
    val statistics: TripStatistics?,
    val manualData: ManualData?,
    val createdAt: String,
    val updatedAt: String
)

data class GpsPointResponse(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double?,
    val accuracy: Float?,
    val speed: Float?,
    val heading: Float?,
    val timestamp: String
)

data class TripStatistics(
    val durationSeconds: Long,
    val distanceMeters: Double,
    val averageSpeedKnots: Double,
    val maxSpeedKnots: Double,
    val stopPoints: List<StopPoint>?
)

data class StopPoint(
    val latitude: Double,
    val longitude: Double,
    val startTime: String,
    val endTime: String,
    val durationSeconds: Long
)

data class ManualData(
    val engineHours: Double?,
    val fuelConsumed: Double?,
    val weatherConditions: String?,
    val numberOfPassengers: Int?,
    val destination: String?
)

data class CreateTripRequest(
    val boatId: String,
    val startTime: String,
    val endTime: String?,
    val waterType: String = "inland",
    val role: String = "master",
    val gpsPoints: List<CreateGpsPointRequest>,
    val manualData: ManualData? = null
)

data class CreateGpsPointRequest(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double?,
    val accuracy: Float?,
    val speed: Float?,
    val heading: Float?,
    val timestamp: String
)

data class UpdateTripRequest(
    val waterType: String?,
    val role: String?,
    val manualData: ManualData?
)

// Authentication models
data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val user: UserResponse,
    val token: String,
    val expiresIn: String
)

data class UserResponse(
    val id: String,
    val username: String,
    val createdAt: String,
    val updatedAt: String
)

data class LogoutResponse(
    val message: String
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

data class ChangePasswordResponse(
    val message: String
)

// Captain's Log models
data class LicenseProgressResponse(
    val totalDays: Int,
    val totalHours: Double,
    val daysInLast3Years: Int,
    val hoursInLast3Years: Double,
    val daysRemaining360: Int,
    val daysRemaining90In3Years: Int,
    val estimatedCompletion360: String?,
    val estimatedCompletion90In3Years: String?,
    val averageDaysPerMonth: Double
)

data class SeaTimeDayResponse(
    val date: String, // YYYY-MM-DD format
    val totalHours: Double,
    val trips: List<SeaTimeDayTripResponse>
)

data class SeaTimeDayTripResponse(
    val id: String,
    val boatId: String,
    val startTime: String, // ISO format
    val endTime: String,   // ISO format
    val durationHours: Double
)

data class SeaTimeBreakdownResponse(
    val month: String, // YYYY-MM format
    val days: Int,
    val hours: Double
)

data class SeaTimeDayCheckResponse(
    val date: String,
    val isSeaTimeDay: Boolean
)

// Note models
data class NoteResponse(
    val id: String,
    val content: String,
    val type: String,
    val boatId: String?,
    val tripId: String?,
    val tags: List<String>,
    val createdAt: String,
    val updatedAt: String,
    val boat: BoatResponse?,
    val trip: TripResponse?
)

data class CreateNoteRequest(
    val content: String,
    val type: String,
    val boatId: String? = null,
    val tripId: String? = null,
    val tags: List<String> = emptyList()
)

data class UpdateNoteRequest(
    val content: String?,
    val tags: List<String>?
)

data class TagsResponse(
    val data: List<String>,
    val count: Int
)

// Todo models
data class TodoListResponse(
    val id: String,
    val title: String,
    val boatId: String?,
    val createdAt: String,
    val updatedAt: String,
    val items: List<TodoItemResponse>,
    val boat: BoatResponse?
)

data class TodoItemResponse(
    val id: String,
    val todoListId: String,
    val content: String,
    val completed: Boolean,
    val completedAt: String?,
    val createdAt: String,
    val updatedAt: String
)

data class CreateTodoListRequest(
    val title: String,
    val boatId: String? = null
)

data class UpdateTodoListRequest(
    val title: String?,
    val boatId: String?
)

data class CreateTodoItemRequest(
    val content: String
)

data class UpdateTodoItemRequest(
    val content: String?,
    val completed: Boolean?
)

// Maintenance models
data class MaintenanceTaskResponse(
    val id: String,
    val boatId: String,
    val title: String,
    val description: String?,
    val component: String?,
    val dueDate: String,
    val recurrence: RecurrenceSchedule?,
    val createdAt: String,
    val updatedAt: String,
    val boat: BoatResponse,
    val completions: List<MaintenanceCompletionResponse>
)

data class MaintenanceCompletionResponse(
    val id: String,
    val maintenanceTaskId: String,
    val completedAt: String,
    val cost: Double?,
    val notes: String?,
    val createdAt: String
)

data class RecurrenceSchedule(
    val type: String, // 'days', 'weeks', 'months', 'years', 'engine_hours'
    val interval: Int
)

data class CreateMaintenanceTaskRequest(
    val boatId: String,
    val title: String,
    val description: String? = null,
    val component: String? = null,
    val dueDate: String,
    val recurrence: RecurrenceSchedule? = null
)

data class UpdateMaintenanceTaskRequest(
    val title: String? = null,
    val description: String? = null,
    val component: String? = null,
    val dueDate: String? = null,
    val recurrence: RecurrenceSchedule? = null
)

data class CompleteMaintenanceTaskRequest(
    val cost: Double? = null,
    val notes: String? = null
)

// Notification models
data class NotificationResponse(
    val id: String,
    val type: String,
    val title: String,
    val message: String,
    val entityType: String?,
    val entityId: String?,
    val read: Boolean,
    val createdAt: String
)

data class MarkNotificationReadRequest(
    val read: Boolean = true
)

// Marked Location models
data class MarkedLocationResponse(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val notes: String?,
    val tags: List<String>,
    val createdAt: String,
    val updatedAt: String,
    val distanceMeters: Double? = null
)

data class CreateMarkedLocationRequest(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val notes: String? = null,
    val tags: List<String> = emptyList()
)

data class UpdateMarkedLocationRequest(
    val name: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val category: String? = null,
    val notes: String? = null,
    val tags: List<String>? = null
)

// Maintenance Template models (new template-event structure)
data class MaintenanceTemplateResponse(
    val id: String,
    val boatId: String,
    val title: String,
    val description: String,
    val component: String,
    val estimatedCost: Double,
    val estimatedTime: Int, // in minutes
    val isActive: Boolean,
    val recurrence: RecurrenceSchedule,
    val createdAt: String,
    val updatedAt: String,
    val boat: BoatResponse?
)

data class MaintenanceEventResponse(
    val id: String,
    val templateId: String,
    val dueDate: String,
    val completedAt: String?,
    val actualCost: Double?,
    val actualTime: Int?, // in minutes
    val notes: String?,
    val createdAt: String,
    val updatedAt: String,
    val template: MaintenanceTemplateResponse?
)

data class CreateMaintenanceTemplateRequest(
    val boatId: String,
    val title: String,
    val description: String,
    val component: String,
    val recurrence: RecurrenceSchedule,
    val estimatedCost: Double,
    val estimatedTime: Int
)

data class UpdateMaintenanceTemplateRequest(
    val title: String? = null,
    val description: String? = null,
    val component: String? = null,
    val recurrence: RecurrenceSchedule? = null,
    val estimatedCost: Double? = null,
    val estimatedTime: Int? = null,
    val isActive: Boolean? = null
)

data class CompleteMaintenanceEventRequest(
    val actualCost: Double? = null,
    val actualTime: Int? = null,
    val notes: String? = null
)

data class ScheduleChangePreviewRequest(
    val recurrence: RecurrenceSchedule
)

data class ScheduleChangeApplyRequest(
    val recurrence: RecurrenceSchedule,
    val offline: Boolean = false
)

data class TemplateInformationChangeRequest(
    val title: String? = null,
    val description: String? = null,
    val component: String? = null,
    val estimatedCost: Double? = null,
    val estimatedTime: Int? = null
)

data class ScheduleChangePreviewResponse(
    val templateId: String,
    val currentRecurrence: RecurrenceSchedule,
    val newRecurrence: RecurrenceSchedule,
    val eventsAffected: Int,
    val nextDueDate: String?,
    val affectedEvents: List<MaintenanceEventResponse>
)

data class ScheduleChangeApplyResponse(
    val success: Boolean,
    val templateId: String,
    val eventsUpdated: Int,
    val eventsCreated: Int,
    val eventsDeleted: Int,
    val errors: List<String>
)

data class TemplateInformationChangeResponse(
    val templateId: String,
    val eventsUpdated: Int,
    val completedEventsPreserved: Int,
    val errors: List<String>
)

// Offline sync models
data class OfflineChangeResponse(
    val id: String,
    val entityType: String,
    val entityId: String,
    val changeType: String,
    val changeData: Map<String, Any>,
    val timestamp: String,
    val synced: Boolean,
    val syncAttempts: Int,
    val lastSyncAttempt: String?,
    val syncError: String?
)

data class SyncStatusResponse(
    val pendingChanges: Int,
    val failedChanges: Int,
    val lastSyncAttempt: String?
)

data class SyncResultResponse(
    val success: Boolean,
    val changesSynced: Int,
    val errors: List<String>
)

// Generic response wrapper
data class ApiResponse<T>(
    val data: T?,
    val error: ApiError?
)

data class ApiError(
    val code: String,
    val message: String,
    val details: Any?
)
