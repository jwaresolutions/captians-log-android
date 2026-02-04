package com.captainslog.repository

import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.network.ApiService
import com.captainslog.network.models.MarkNotificationReadRequest
import com.captainslog.network.models.NotificationResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationRepository(
    private val connectionManager: ConnectionManager
) {
    companion object {
        private const val TAG = "NotificationRepository"
    }

    private val _notifications = MutableStateFlow<List<NotificationResponse>>(emptyList())
    val notifications: Flow<List<NotificationResponse>> = _notifications.asStateFlow()

    suspend fun fetchNotifications(): Result<List<NotificationResponse>> {
        return try {
            val apiService = try {
                connectionManager.getApiService()
            } catch (e: IllegalStateException) {
                Log.w(TAG, "API service not initialized, cannot fetch notifications: ${e.message}")
                return Result.failure(e)
            }
            
            val response = apiService.getNotifications()
            if (response.isSuccessful && response.body() != null) {
                val notifications = response.body()!!.notifications
                _notifications.value = notifications
                Log.d(TAG, "Fetched ${notifications.size} notifications")
                Result.success(notifications)
            } else {
                val error = "Failed to fetch notifications: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching notifications", e)
            Result.failure(e)
        }
    }

    suspend fun markNotificationAsRead(id: String): Result<NotificationResponse> {
        return try {
            val request = MarkNotificationReadRequest(read = true)
            val apiService = connectionManager.getApiService()
            val response = apiService.markNotificationAsRead(id, request)
            if (response.isSuccessful && response.body() != null) {
                val updatedNotification = response.body()!!
                
                // Update local state
                val currentNotifications = _notifications.value.toMutableList()
                val index = currentNotifications.indexOfFirst { it.id == id }
                if (index != -1) {
                    currentNotifications[index] = updatedNotification
                    _notifications.value = currentNotifications
                }
                
                Log.d(TAG, "Marked notification as read: $id")
                Result.success(updatedNotification)
            } else {
                val error = "Failed to mark notification as read: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error marking notification as read", e)
            Result.failure(e)
        }
    }

    fun getUnreadNotifications(): List<NotificationResponse> {
        return _notifications.value.filter { !it.read }
    }

    fun getMaintenanceNotifications(): List<NotificationResponse> {
        return _notifications.value.filter { it.type == "maintenance_due" }
    }
}