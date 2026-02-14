package com.captainslog.nautical.service

import android.util.Log
import com.captainslog.nautical.model.MarineAlert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import java.net.URL

object NoaaWeatherService {

    private const val TAG = "NoaaWeatherService"

    private val MARINE_EVENTS = listOf(
        "Marine", "Small Craft", "Storm", "Hurricane",
        "Coastal", "Surf", "Rip Current", "Tsunami",
        "Gale", "Typhoon", "Waterspout"
    )

    suspend fun fetchAlerts(lat: Double, lon: Double): List<MarineAlert> = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.weather.gov/alerts/active?point=$lat,$lon"
            val connection = URL(url).openConnection().apply {
                setRequestProperty("User-Agent", "CaptainsLog")
                setRequestProperty("Accept", "application/geo+json")
            }
            val json = connection.getInputStream().bufferedReader().readText()
            val data = JSONObject(json)
            val features = data.optJSONArray("features") ?: return@withContext emptyList()

            val alerts = mutableListOf<MarineAlert>()
            for (i in 0 until features.length()) {
                val feature = features.getJSONObject(i)
                val props = feature.getJSONObject("properties")
                val event = props.optString("event", "")

                // Filter for marine-related events
                if (MARINE_EVENTS.none { event.contains(it, ignoreCase = true) }) continue

                val polygon = parsePolygon(feature)

                alerts.add(
                    MarineAlert(
                        id = props.optString("id", ""),
                        event = event,
                        headline = props.optString("headline", ""),
                        description = props.optString("description", ""),
                        severity = props.optString("severity", "Unknown"),
                        areaDesc = props.optString("areaDesc", ""),
                        polygon = polygon,
                        onset = props.optString("onset", null),
                        expires = props.optString("expires", null)
                    )
                )
            }
            alerts
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch weather alerts", e)
            emptyList()
        }
    }

    private fun parsePolygon(feature: JSONObject): List<GeoPoint>? {
        try {
            val geometry = feature.optJSONObject("geometry") ?: return null
            val type = geometry.optString("type")
            if (type != "Polygon") return null
            val coordinates = geometry.optJSONArray("coordinates")?.optJSONArray(0) ?: return null
            val points = mutableListOf<GeoPoint>()
            for (i in 0 until coordinates.length()) {
                val coord = coordinates.getJSONArray(i)
                // GeoJSON is [lon, lat]
                points.add(GeoPoint(coord.getDouble(1), coord.getDouble(0)))
            }
            return points.ifEmpty { null }
        } catch (e: Exception) {
            return null
        }
    }
}
