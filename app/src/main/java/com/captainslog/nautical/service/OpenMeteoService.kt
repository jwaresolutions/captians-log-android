package com.captainslog.nautical.service

import com.captainslog.nautical.model.OceanData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class MarineWeather(
    val latitude: Double,
    val longitude: Double,
    val waveHeight: Double?,
    val wavePeriod: Double?,
    val waveDirection: Double?,
    val windSpeed: Double?,
    val windDirection: Double?,
    val swellHeight: Double?,
    val temperature: Double?,
    val timestamp: String
)

object OpenMeteoService {

    suspend fun fetchMarineWeather(lat: Double, lng: Double): MarineWeather? = withContext(Dispatchers.IO) {
        try {
            val params = "latitude=$lat&longitude=$lng" +
                "&current=wave_height,wave_period,wave_direction,swell_wave_height,swell_wave_period" +
                "&hourly=temperature_2m,wind_speed_10m,wind_direction_10m" +
                "&forecast_days=1&timezone=auto"
            val url = "https://marine-api.open-meteo.com/v1/marine?$params"
            val json = URL(url).readText()
            val data = JSONObject(json)
            val current = data.optJSONObject("current")
            val hourly = data.optJSONObject("hourly")

            MarineWeather(
                latitude = data.optDouble("latitude"),
                longitude = data.optDouble("longitude"),
                waveHeight = current?.optDouble("wave_height"),
                wavePeriod = current?.optDouble("wave_period"),
                waveDirection = current?.optDouble("wave_direction"),
                windSpeed = hourly?.optJSONArray("wind_speed_10m")?.optDouble(0),
                windDirection = hourly?.optJSONArray("wind_direction_10m")?.optDouble(0),
                swellHeight = current?.optDouble("swell_wave_height"),
                temperature = hourly?.optJSONArray("temperature_2m")?.optDouble(0),
                timestamp = current?.optString("time") ?: ""
            )
        } catch (e: Exception) {
            android.util.Log.e("OpenMeteoService", "Failed to fetch marine weather", e)
            null
        }
    }

    suspend fun fetchOceanData(lat: Double, lng: Double): OceanData? = withContext(Dispatchers.IO) {
        try {
            val url = "https://marine-api.open-meteo.com/v1/marine?latitude=$lat&longitude=$lng" +
                "&current=ocean_current_velocity,ocean_current_direction,sea_surface_temperature"
            val json = URL(url).readText()
            val data = JSONObject(json)
            val current = data.optJSONObject("current") ?: return@withContext null

            val velocity = current.optDouble("ocean_current_velocity").takeIf { !it.isNaN() }
            val direction = current.optDouble("ocean_current_direction").takeIf { !it.isNaN() }
            val sst = current.optDouble("sea_surface_temperature").takeIf { !it.isNaN() }

            if (velocity == null && direction == null && sst == null) null
            else OceanData(
                currentVelocity = velocity,
                currentDirection = direction,
                seaSurfaceTemp = sst
            )
        } catch (e: Exception) {
            android.util.Log.e("OpenMeteoService", "Failed to fetch ocean data", e)
            null
        }
    }
}
