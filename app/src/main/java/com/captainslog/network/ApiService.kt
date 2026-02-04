package com.captainslog.network

import com.captainslog.network.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Health check endpoint (no auth required)
    @GET("health")
    suspend fun healthCheck(): Response<Unit>

    // Authentication endpoints (no auth required for login)
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<LogoutResponse>

    @POST("api/v1/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ChangePasswordResponse>

    // Boat endpoints
    @POST("api/v1/boats")
    suspend fun createBoat(@Body request: CreateBoatRequest): Response<ApiDataResponse<BoatResponse>>

    @GET("api/v1/boats")
    suspend fun getBoats(): Response<ApiListResponse<BoatResponse>>

    @GET("api/v1/boats/{id}")
    suspend fun getBoat(@Path("id") id: String): Response<ApiDataResponse<BoatResponse>>

    @PUT("api/v1/boats/{id}")
    suspend fun updateBoat(
        @Path("id") id: String,
        @Body request: CreateBoatRequest
    ): Response<ApiDataResponse<BoatResponse>>

    @PATCH("api/v1/boats/{id}/status")
    suspend fun updateBoatStatus(
        @Path("id") id: String,
        @Body request: Map<String, Boolean>
    ): Response<ApiDataResponse<BoatResponse>>

    @PATCH("api/v1/boats/{id}/active")
    suspend fun setActiveBoat(@Path("id") id: String): Response<ApiDataResponse<BoatResponse>>

    // Trip endpoints
    @POST("api/v1/trips")
    suspend fun createTrip(@Body request: CreateTripRequest): Response<ApiDataResponse<TripResponse>>

    @GET("api/v1/trips")
    suspend fun getTrips(
        @Query("boatId") boatId: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<ApiListResponse<TripResponse>>

    @GET("api/v1/trips/{id}")
    suspend fun getTrip(@Path("id") id: String): Response<ApiDataResponse<TripResponse>>

    @PUT("api/v1/trips/{id}")
    suspend fun updateTrip(
        @Path("id") id: String,
        @Body request: UpdateTripRequest
    ): Response<ApiDataResponse<TripResponse>>

    @PATCH("api/v1/trips/{id}/manual-data")
    suspend fun updateTripManualData(
        @Path("id") id: String,
        @Body manualData: ManualData
    ): Response<ApiDataResponse<TripResponse>>

    // Captain's Log endpoints
    @GET("api/v1/captain-log/progress")
    suspend fun getLicenseProgress(): Response<ApiSuccessResponse<LicenseProgressResponse>>

    @GET("api/v1/captain-log/sea-time-days")
    suspend fun getSeaTimeDays(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<ApiSuccessResponse<List<SeaTimeDayResponse>>>

    @GET("api/v1/captain-log/breakdown")
    suspend fun getSeaTimeBreakdown(
        @Query("year") year: Int? = null
    ): Response<ApiSuccessResponse<List<SeaTimeBreakdownResponse>>>

    @GET("api/v1/captain-log/check-day/{date}")
    suspend fun checkSeaTimeDay(@Path("date") date: String): Response<ApiSuccessResponse<SeaTimeDayCheckResponse>>

    // Notes endpoints
    @POST("api/v1/notes")
    suspend fun createNote(@Body request: CreateNoteRequest): Response<ApiDataResponse<NoteResponse>>

    @GET("api/v1/notes")
    suspend fun getNotes(
        @Query("type") type: String? = null,
        @Query("boatId") boatId: String? = null,
        @Query("tripId") tripId: String? = null,
        @Query("tags") tags: List<String>? = null,
        @Query("search") search: String? = null
    ): Response<ApiListResponse<NoteResponse>>

    @GET("api/v1/notes/{id}")
    suspend fun getNote(@Path("id") id: String): Response<ApiDataResponse<NoteResponse>>

    @PUT("api/v1/notes/{id}")
    suspend fun updateNote(
        @Path("id") id: String,
        @Body request: UpdateNoteRequest
    ): Response<ApiDataResponse<NoteResponse>>

    @DELETE("api/v1/notes/{id}")
    suspend fun deleteNote(@Path("id") id: String): Response<Unit>

    @POST("api/v1/notes/{id}/tags")
    suspend fun addNoteTags(
        @Path("id") id: String,
        @Body tags: Map<String, List<String>>
    ): Response<ApiDataResponse<NoteResponse>>

    @DELETE("api/v1/notes/{id}/tags")
    suspend fun removeNoteTags(
        @Path("id") id: String,
        @Body tags: Map<String, List<String>>
    ): Response<ApiDataResponse<NoteResponse>>

    @GET("api/v1/notes/tags/all")
    suspend fun getAllTags(): Response<TagsResponse>

    // Todo endpoints
    @POST("api/v1/todos")
    suspend fun createTodoList(@Body request: CreateTodoListRequest): Response<ApiSuccessResponse<TodoListResponse>>

    @GET("api/v1/todos")
    suspend fun getTodoLists(
        @Query("boatId") boatId: String? = null
    ): Response<ApiListResponse<TodoListResponse>>

    @GET("api/v1/todos/{id}")
    suspend fun getTodoList(@Path("id") id: String): Response<ApiSuccessResponse<TodoListResponse>>

    @PUT("api/v1/todos/{id}")
    suspend fun updateTodoList(
        @Path("id") id: String,
        @Body request: UpdateTodoListRequest
    ): Response<ApiSuccessResponse<TodoListResponse>>

    @DELETE("api/v1/todos/{id}")
    suspend fun deleteTodoList(@Path("id") id: String): Response<Unit>

    @POST("api/v1/todos/{id}/items")
    suspend fun createTodoItem(
        @Path("id") listId: String,
        @Body request: CreateTodoItemRequest
    ): Response<ApiSuccessResponse<TodoItemResponse>>

    @PUT("api/v1/todos/items/{itemId}")
    suspend fun updateTodoItem(
        @Path("itemId") itemId: String,
        @Body request: UpdateTodoItemRequest
    ): Response<ApiSuccessResponse<TodoItemResponse>>

    @PATCH("api/v1/todos/items/{itemId}/complete")
    suspend fun toggleTodoItemCompletion(
        @Path("itemId") itemId: String
    ): Response<ApiSuccessResponse<TodoItemResponse>>

    @DELETE("api/v1/todos/items/{itemId}")
    suspend fun deleteTodoItem(@Path("itemId") itemId: String): Response<Unit>

    // Maintenance endpoints
    @POST("api/v1/maintenance")
    suspend fun createMaintenanceTask(@Body request: CreateMaintenanceTaskRequest): Response<MaintenanceTaskResponse>

    @GET("api/v1/maintenance")
    suspend fun getMaintenanceTasks(
        @Query("boatId") boatId: String? = null
    ): Response<List<MaintenanceTaskResponse>>

    @GET("api/v1/maintenance/upcoming")
    suspend fun getUpcomingMaintenanceTasks(
        @Query("days") days: Int? = null
    ): Response<List<MaintenanceTaskResponse>>

    @GET("api/v1/maintenance/{id}")
    suspend fun getMaintenanceTask(@Path("id") id: String): Response<MaintenanceTaskResponse>

    @PUT("api/v1/maintenance/{id}")
    suspend fun updateMaintenanceTask(
        @Path("id") id: String,
        @Body request: UpdateMaintenanceTaskRequest
    ): Response<MaintenanceTaskResponse>

    @DELETE("api/v1/maintenance/{id}")
    suspend fun deleteMaintenanceTask(@Path("id") id: String): Response<Unit>

    @POST("api/v1/maintenance/{id}/complete")
    suspend fun completeMaintenanceTask(
        @Path("id") id: String,
        @Body request: CompleteMaintenanceTaskRequest
    ): Response<MaintenanceTaskResponse>

    @GET("api/v1/maintenance/{id}/history")
    suspend fun getMaintenanceTaskHistory(@Path("id") id: String): Response<List<MaintenanceCompletionResponse>>

    // Notification endpoints
    @GET("api/v1/notifications")
    suspend fun getNotifications(): Response<NotificationsListResponse>

    @PATCH("api/v1/notifications/{id}/read")
    suspend fun markNotificationAsRead(
        @Path("id") id: String,
        @Body request: MarkNotificationReadRequest
    ): Response<NotificationResponse>

    // Marked Location endpoints
    @GET("api/v1/locations")
    suspend fun getMarkedLocations(
        @Query("category") category: String? = null,
        @Query("tags") tags: String? = null,
        @Query("search") search: String? = null,
        @Query("lat") lat: Double? = null,
        @Query("lon") lon: Double? = null
    ): Response<List<MarkedLocationResponse>>

    @GET("api/v1/locations/nearby")
    suspend fun getNearbyMarkedLocations(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("radius") radius: Double
    ): Response<List<MarkedLocationResponse>>

    @GET("api/v1/locations/{id}")
    suspend fun getMarkedLocation(
        @Path("id") id: String
    ): Response<MarkedLocationResponse>

    @POST("api/v1/locations")
    suspend fun createMarkedLocation(
        @Body request: CreateMarkedLocationRequest
    ): Response<MarkedLocationResponse>

    @PUT("api/v1/locations/{id}")
    suspend fun updateMarkedLocation(
        @Path("id") id: String,
        @Body request: UpdateMarkedLocationRequest
    ): Response<MarkedLocationResponse>

    @DELETE("api/v1/locations/{id}")
    suspend fun deleteMarkedLocation(
        @Path("id") id: String
    ): Response<Unit>

    // Photo endpoints
    @Multipart
    @POST("api/v1/photos")
    suspend fun uploadPhoto(
        @Part("entityType") entityType: okhttp3.RequestBody,
        @Part("entityId") entityId: okhttp3.RequestBody,
        @Part photo: okhttp3.MultipartBody.Part
    ): Response<PhotoResponse>

    @GET("api/v1/photos")
    suspend fun getPhotos(
        @Query("entityType") entityType: String,
        @Query("entityId") entityId: String
    ): Response<PhotoListResponse>

    @GET("api/v1/photos/{id}")
    suspend fun getPhoto(
        @Path("id") id: String
    ): Response<PhotoResponse>

    @DELETE("api/v1/photos/{id}")
    suspend fun deletePhoto(
        @Path("id") id: String
    ): Response<Unit>

    // Sensor endpoints
    @POST("api/v1/sensors/types")
    suspend fun createSensorType(
        @Body request: CreateSensorTypeRequest
    ): Response<SingleSensorTypeResponse>

    @GET("api/v1/sensors/types")
    suspend fun getSensorTypes(): Response<SensorTypeListResponse>

    @GET("api/v1/sensors/types/{id}")
    suspend fun getSensorType(
        @Path("id") id: String
    ): Response<SingleSensorTypeResponse>

    @PUT("api/v1/sensors/types/{id}")
    suspend fun updateSensorType(
        @Path("id") id: String,
        @Body request: UpdateSensorTypeRequest
    ): Response<SingleSensorTypeResponse>

    @POST("api/v1/sensors/readings")
    suspend fun createSensorReading(
        @Body request: CreateSensorReadingRequest
    ): Response<SingleSensorReadingResponse>

    @GET("api/v1/sensors/readings")
    suspend fun getSensorReadings(
        @Query("tripId") tripId: String,
        @Query("sensorType") sensorType: String? = null
    ): Response<SensorReadingListResponse>

    @GET("api/v1/sensors/readings/{tripId}/{sensorTypeId}")
    suspend fun getSensorReadingsByTypeId(
        @Path("tripId") tripId: String,
        @Path("sensorTypeId") sensorTypeId: String
    ): Response<SensorReadingListResponse>

    // Maintenance Template endpoints (new template-event structure)
    @POST("api/v1/maintenance/templates")
    suspend fun createMaintenanceTemplate(
        @Body request: CreateMaintenanceTemplateRequest
    ): Response<ApiSuccessResponse<MaintenanceTemplateResponse>>

    @GET("api/v1/maintenance/templates")
    suspend fun getMaintenanceTemplates(
        @Query("boatId") boatId: String? = null,
        @Query("activeOnly") activeOnly: Boolean? = null
    ): Response<ApiSuccessResponse<List<MaintenanceTemplateResponse>>>

    @GET("api/v1/maintenance/templates/{id}")
    suspend fun getMaintenanceTemplate(
        @Path("id") id: String
    ): Response<ApiSuccessResponse<MaintenanceTemplateResponse>>

    @PUT("api/v1/maintenance/templates/{id}")
    suspend fun updateMaintenanceTemplate(
        @Path("id") id: String,
        @Body request: UpdateMaintenanceTemplateRequest
    ): Response<ApiSuccessResponse<MaintenanceTemplateResponse>>

    @DELETE("api/v1/maintenance/templates/{id}")
    suspend fun deleteMaintenanceTemplate(
        @Path("id") id: String
    ): Response<ApiSuccessResponse<Unit>>

    @POST("api/v1/maintenance/templates/{id}/schedule-change/preview")
    suspend fun previewScheduleChange(
        @Path("id") id: String,
        @Body request: ScheduleChangePreviewRequest
    ): Response<ApiSuccessResponse<ScheduleChangePreviewResponse>>

    @POST("api/v1/maintenance/templates/{id}/schedule-change/apply")
    suspend fun applyScheduleChange(
        @Path("id") id: String,
        @Body request: ScheduleChangeApplyRequest
    ): Response<ApiSuccessResponse<ScheduleChangeApplyResponse>>

    @POST("api/v1/maintenance/templates/{id}/information-change/preview")
    suspend fun previewInformationChange(
        @Path("id") id: String,
        @Body request: TemplateInformationChangeRequest
    ): Response<ApiSuccessResponse<TemplateInformationChangeResponse>>

    @POST("api/v1/maintenance/templates/{id}/information-change/apply")
    suspend fun applyInformationChange(
        @Path("id") id: String,
        @Body request: TemplateInformationChangeRequest
    ): Response<ApiSuccessResponse<TemplateInformationChangeResponse>>

    // Maintenance Event endpoints
    @GET("api/v1/maintenance/events/upcoming")
    suspend fun getUpcomingMaintenanceEvents(
        @Query("boatId") boatId: String? = null,
        @Query("templateId") templateId: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<ApiSuccessResponse<List<MaintenanceEventResponse>>>

    @GET("api/v1/maintenance/events/completed")
    suspend fun getCompletedMaintenanceEvents(
        @Query("boatId") boatId: String? = null,
        @Query("templateId") templateId: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<ApiSuccessResponse<List<MaintenanceEventResponse>>>

    @GET("api/v1/maintenance/events/{id}")
    suspend fun getMaintenanceEvent(
        @Path("id") id: String
    ): Response<ApiSuccessResponse<MaintenanceEventResponse>>

    @POST("api/v1/maintenance/events/{id}/complete")
    suspend fun completeMaintenanceEvent(
        @Path("id") id: String,
        @Body request: CompleteMaintenanceEventRequest
    ): Response<ApiSuccessResponse<MaintenanceEventResponse>>

    // Offline sync endpoints
    @GET("api/v1/offline-sync/status")
    suspend fun getSyncStatus(): Response<ApiSuccessResponse<SyncStatusResponse>>

    @GET("api/v1/offline-sync/pending")
    suspend fun getPendingChanges(): Response<ApiSuccessResponse<List<OfflineChangeResponse>>>

    @POST("api/v1/offline-sync/sync")
    suspend fun syncOfflineChanges(): Response<ApiSuccessResponse<SyncResultResponse>>
}
