package com.captainslog.sync

import android.content.Context
import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.security.SecurePreferences
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SSE client that listens for real-time sync events from the backend.
 * When an event arrives, it triggers sync for the relevant data type
 * using ComprehensiveSyncManager.
 */
@Singleton
class SseClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val connectionManager: ConnectionManager,
    private val syncOrchestrator: SyncOrchestrator
) {
    companion object {
        private const val TAG = "SseClient"
        private const val INITIAL_RETRY_DELAY_MS = 1000L
        private const val MAX_RETRY_DELAY_MS = 60000L
    }

    private val securePreferences = SecurePreferences(context)
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var eventSource: EventSource? = null
    private var retryDelay = INITIAL_RETRY_DELAY_MS
    private var isConnecting = false

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS) // No read timeout for SSE
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    fun connect() {
        if (isConnecting) return
        isConnecting = true

        val baseUrl = securePreferences.remoteUrl ?: run {
            Log.w(TAG, "No server URL configured, cannot connect SSE")
            isConnecting = false
            return
        }

        val token = securePreferences.jwtToken ?: run {
            Log.w(TAG, "No auth token, cannot connect SSE")
            isConnecting = false
            return
        }

        // Build SSE URL with token as query param
        val sseUrl = "${baseUrl.trimEnd('/')}/api/v1/sync/events?token=$token"

        val request = Request.Builder()
            .url(sseUrl)
            .header("Accept", "text/event-stream")
            .build()

        val factory = EventSources.createFactory(client)

        eventSource = factory.newEventSource(request, object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                Log.d(TAG, "SSE connection opened")
                isConnecting = false
                retryDelay = INITIAL_RETRY_DELAY_MS
            }

            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                try {
                    val event = gson.fromJson(data, SyncEvent::class.java)
                    if (event.type == "connected") return

                    Log.d(TAG, "SSE event: ${event.type}/${event.action}")
                    handleSyncEvent(event)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to parse SSE event: $data", e)
                }
            }

            override fun onClosed(eventSource: EventSource) {
                Log.d(TAG, "SSE connection closed")
                isConnecting = false
                scheduleReconnect()
            }

            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: Response?
            ) {
                Log.w(TAG, "SSE connection failed: ${t?.message}, status: ${response?.code}")
                isConnecting = false

                // Don't reconnect on auth failure
                if (response?.code == 401) {
                    Log.w(TAG, "SSE auth failed, not reconnecting")
                    return
                }

                scheduleReconnect()
            }
        })
    }

    fun disconnect() {
        eventSource?.cancel()
        eventSource = null
        isConnecting = false
        scope.coroutineContext.cancelChildren()
    }

    private fun handleSyncEvent(event: SyncEvent) {
        val dataType = when (event.type) {
            "boats" -> DataType.BOATS
            "trips" -> DataType.TRIPS
            "notes" -> DataType.NOTES
            "todos" -> DataType.TODOS
            "maintenance_templates" -> DataType.TEMPLATES
            "maintenance_events" -> DataType.TEMPLATES
            "locations" -> DataType.LOCATIONS
            "photos" -> DataType.PHOTOS
            else -> {
                Log.w(TAG, "Unknown event type: ${event.type}")
                return
            }
        }

        syncOrchestrator.syncDataType(dataType)
    }

    private fun scheduleReconnect() {
        scope.launch {
            Log.d(TAG, "Reconnecting in ${retryDelay}ms")
            delay(retryDelay)
            retryDelay = (retryDelay * 2).coerceAtMost(MAX_RETRY_DELAY_MS)

            if (connectionManager.hasInternetConnection()) {
                connect()
            } else {
                Log.d(TAG, "No internet, skipping reconnect")
                retryDelay = INITIAL_RETRY_DELAY_MS
            }
        }
    }

    private data class SyncEvent(
        val type: String,
        val action: String,
        val entityId: String? = null,
        val timestamp: String? = null
    )
}
