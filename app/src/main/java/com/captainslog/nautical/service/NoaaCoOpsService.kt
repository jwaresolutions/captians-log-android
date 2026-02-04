package com.captainslog.nautical.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class TideStation(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double
)

data class TidePrediction(
    val time: String,
    val value: Double,
    val type: String
)

object NoaaCoOpsService {

    suspend fun fetchTideStations(
        minLat: Double, minLng: Double, maxLat: Double, maxLng: Double
    ): List<TideStation> = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.tidesandcurrents.noaa.gov/mdapi/prod/webapi/stations.json?type=tidepredictions"
            val json = URL(url).readText()
            val data = JSONObject(json)
            val stations = data.optJSONArray("stations") ?: return@withContext emptyList()
            (0 until stations.length()).mapNotNull { i ->
                val s = stations.getJSONObject(i)
                val lat = s.optDouble("lat")
                val lng = s.optDouble("lng")
                if (lat in minLat..maxLat && lng in minLng..maxLng) {
                    TideStation(s.getString("id"), s.getString("name"), lat, lng)
                } else null
            }
        } catch (e: Exception) {
            android.util.Log.e("NoaaCoOpsService", "Failed to fetch tide stations", e)
            emptyList()
        }
    }

    suspend fun fetchTidePredictions(stationId: String): List<TidePrediction> = withContext(Dispatchers.IO) {
        try {
            val today = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US).format(java.util.Date())
            val tomorrow = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US).format(
                java.util.Date(System.currentTimeMillis() + 86400000)
            )
            val url = "https://api.tidesandcurrents.noaa.gov/api/prod/datagetter?" +
                "begin_date=$today&end_date=$tomorrow&station=$stationId" +
                "&product=predictions&datum=MLLW&time_zone=lst_ldt&units=english&format=json&interval=hilo"
            val json = URL(url).readText()
            val data = JSONObject(json)
            val predictions = data.optJSONArray("predictions") ?: return@withContext emptyList()
            (0 until predictions.length()).map { i ->
                val p = predictions.getJSONObject(i)
                TidePrediction(p.getString("t"), p.getDouble("v"), p.getString("type"))
            }
        } catch (e: Exception) {
            android.util.Log.e("NoaaCoOpsService", "Failed to fetch tide predictions", e)
            emptyList()
        }
    }
}
