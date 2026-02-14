package com.captainslog.nautical.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

data class AISVessel(
    val mmsi: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val heading: Double,
    val speed: Double,
    val timestamp: Long
)

class AISStreamService {

    companion object {
        private const val TAG = "AISStreamService"
    }

    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    private val vessels = ConcurrentHashMap<Long, AISVessel>()

    private val _vesselFlow = MutableStateFlow<List<AISVessel>>(emptyList())
    val vesselFlow: StateFlow<List<AISVessel>> = _vesselFlow.asStateFlow()

    private var lastApiKey: String = ""
    private var lastMinLat: Double = 0.0
    private var lastMinLng: Double = 0.0
    private var lastMaxLat: Double = 0.0
    private var lastMaxLng: Double = 0.0
    private var reconnectAttempts: Int = 0
    private val maxReconnectAttempts: Int = 3

    fun connect(apiKey: String, minLat: Double, minLng: Double, maxLat: Double, maxLng: Double) {
        lastApiKey = apiKey
        lastMinLat = minLat
        lastMinLng = minLng
        lastMaxLat = maxLat
        lastMaxLng = maxLng

        disconnect()
        reconnectAttempts = 0

        val request = Request.Builder()
            .url("wss://stream.aisstream.io/v0/stream")
            .build()

        android.util.Log.d(TAG, "Connecting to AISstream with bbox [$minLat,$minLng,$maxLat,$maxLng]")

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                android.util.Log.d(TAG, "WebSocket opened, sending subscription")
                reconnectAttempts = 0
                val subscribeMsg = JSONObject().apply {
                    put("APIKey", apiKey)
                    put("BoundingBoxes", org.json.JSONArray().apply {
                        put(org.json.JSONArray().apply {
                            put(org.json.JSONArray().apply { put(minLat); put(minLng) })
                            put(org.json.JSONArray().apply { put(maxLat); put(maxLng) })
                        })
                    })
                }
                ws.send(subscribeMsg.toString())
            }

            override fun onMessage(ws: WebSocket, text: String) {
                try {
                    val data = JSONObject(text)
                    // Check for error response (e.g. invalid API key)
                    val error = data.optString("error", "")
                    if (error.isNotEmpty()) {
                        android.util.Log.e(TAG, "AISstream error: $error")
                        ws.close(1000, "Error received")
                        return
                    }
                    val report = data.optJSONObject("Message")?.optJSONObject("PositionReport") ?: return
                    val meta = data.optJSONObject("Metadata") ?: data.optJSONObject("MetaData") ?: return
                    val mmsi = meta.optLong("MMSI")
                    val vessel = AISVessel(
                        mmsi = mmsi,
                        name = meta.optString("ShipName", "MMSI $mmsi").trim(),
                        latitude = report.optDouble("Latitude"),
                        longitude = report.optDouble("Longitude"),
                        heading = report.optDouble("TrueHeading", report.optDouble("Cog", 0.0)),
                        speed = report.optDouble("Sog", 0.0),
                        timestamp = System.currentTimeMillis()
                    )
                    vessels[mmsi] = vessel
                    android.util.Log.d(TAG, "Vessel: ${vessel.name} at ${vessel.latitude},${vessel.longitude}")
                    // Prune stale
                    val cutoff = System.currentTimeMillis() - 600_000
                    val staleKeys = vessels.entries.filter { it.value.timestamp < cutoff }.map { it.key }
                    staleKeys.forEach { vessels.remove(it) }
                    _vesselFlow.value = vessels.values.toList()
                } catch (e: Exception) {
                    android.util.Log.w(TAG, "Failed to parse AIS message", e)
                }
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                android.util.Log.e(TAG, "WebSocket failure (attempt $reconnectAttempts)", t)
                if (reconnectAttempts < maxReconnectAttempts && webSocket != null) {
                    reconnectAttempts++
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        if (webSocket != null) {
                            connect(lastApiKey, lastMinLat, lastMinLng, lastMaxLat, lastMaxLng)
                        }
                    }, 5000)
                } else {
                    android.util.Log.e(TAG, "Max reconnect attempts reached, giving up")
                }
            }
        })
    }

    /**
     * Test an API key by connecting briefly and checking for errors.
     * Returns true if the key is accepted, false otherwise.
     */
    /**
     * Test an API key by connecting and waiting for a valid AIS message.
     * Returns success only if we receive actual data, failure if the connection drops.
     */
    /**
     * Test an API key by connecting and waiting for actual vessel data.
     * Fails if no data received within timeout or if server returns an error.
     */
    suspend fun testApiKey(apiKey: String): Result<Boolean> {
        return kotlinx.coroutines.suspendCancellableCoroutine { cont ->
            val request = Request.Builder()
                .url("wss://stream.aisstream.io/v0/stream")
                .build()

            val testSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(ws: WebSocket, response: Response) {
                    // Use global bounding box to maximize chance of receiving data
                    val subscribeMsg = JSONObject().apply {
                        put("APIKey", apiKey)
                        put("BoundingBoxes", org.json.JSONArray().apply {
                            put(org.json.JSONArray().apply {
                                put(org.json.JSONArray().apply { put(-90.0); put(-180.0) })
                                put(org.json.JSONArray().apply { put(90.0); put(180.0) })
                            })
                        })
                    }
                    ws.send(subscribeMsg.toString())
                    // Timeout — if no data or error within 15 seconds, service is not responding
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        ws.close(1000, "Test timeout")
                        if (cont.isActive) cont.resumeWith(kotlin.Result.success(
                            Result.failure(Exception("No vessel data received. The AISstream service may be experiencing issues."))
                        ))
                    }, 15000)
                }

                override fun onMessage(ws: WebSocket, text: String) {
                    val data = JSONObject(text)
                    val error = data.optString("error", "")
                    if (error.isNotEmpty()) {
                        ws.close(1000, "Error received")
                        if (cont.isActive) cont.resumeWith(kotlin.Result.success(
                            Result.failure(Exception(error))
                        ))
                        return
                    }
                    // Got actual data — key works and service is operational
                    ws.close(1000, "Test complete")
                    if (cont.isActive) cont.resumeWith(kotlin.Result.success(Result.success(true)))
                }

                override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                    if (cont.isActive) {
                        cont.resumeWith(kotlin.Result.success(
                            Result.failure(Exception("Connection failed: ${t.message}"))
                        ))
                    }
                }

                override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                    if (code != 1000 && cont.isActive) {
                        cont.resumeWith(kotlin.Result.success(
                            Result.failure(Exception("Connection rejected (code $code): $reason"))
                        ))
                    }
                }
            })

            cont.invokeOnCancellation {
                testSocket.close(1000, "Cancelled")
            }
        }
    }

    fun disconnect() {
        val ws = webSocket
        webSocket = null
        ws?.close(1000, "Closing")
        vessels.clear()
        _vesselFlow.value = emptyList()
    }
}
