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

    fun connect(apiKey: String, minLat: Double, minLng: Double, maxLat: Double, maxLng: Double) {
        lastApiKey = apiKey
        lastMinLat = minLat
        lastMinLng = minLng
        lastMaxLat = maxLat
        lastMaxLng = maxLng

        disconnect()

        val request = Request.Builder()
            .url("wss://stream.aisstream.io/v0/stream")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
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
                    val report = data.optJSONObject("Message")?.optJSONObject("PositionReport") ?: return
                    val meta = data.optJSONObject("MetaData") ?: return
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
                    // Prune stale
                    val cutoff = System.currentTimeMillis() - 600_000
                    val staleKeys = vessels.entries.filter { it.value.timestamp < cutoff }.map { it.key }
                    staleKeys.forEach { vessels.remove(it) }
                    _vesselFlow.value = vessels.values.toList()
                } catch (e: Exception) {
                    android.util.Log.w("AISStreamService", "Failed to parse AIS message", e)
                }
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                android.util.Log.e("AISStreamService", "WebSocket failure", t)
                // Reconnect after delay
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    if (webSocket != null) { // Only reconnect if not deliberately disconnected
                        connect(lastApiKey, lastMinLat, lastMinLng, lastMaxLat, lastMaxLng)
                    }
                }, 5000)
            }
        })
    }

    fun disconnect() {
        val ws = webSocket
        webSocket = null
        ws?.close(1000, "Closing")
        vessels.clear()
        _vesselFlow.value = emptyList()
    }
}
