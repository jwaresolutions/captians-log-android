package com.captainslog.nautical.service

import android.util.Log
import com.captainslog.nautical.model.NavigationHazard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object UscgNavcenService {

    private const val TAG = "UscgNavcenService"
    private const val AWOIS_URL = "https://services.arcgis.com/iFBq2AW9XO0jYYF7/arcgis/rest/services/Wrecks_Obstructions_AWOIS_DFG/FeatureServer/0/query"

    suspend fun fetchHazards(
        context: android.content.Context,
        minLat: Double,
        minLng: Double,
        maxLat: Double,
        maxLng: Double
    ): List<NavigationHazard> = withContext(Dispatchers.IO) {
        try {
            val url = "$AWOIS_URL?where=1%3D1" +
                "&geometry=$minLng,$minLat,$maxLng,$maxLat" +
                "&geometryType=esriGeometryEnvelope" +
                "&inSR=4326&spatialRel=esriSpatialRelIntersects" +
                "&outFields=FID,VESSLTERMS,LATDEC,LONDEC,CHART,GP_QUALITY,HISTORY" +
                "&returnGeometry=false" +
                "&f=json&resultRecordCount=50"
            val json = URL(url).readText()
            val data = JSONObject(json)

            if (data.has("error")) {
                Log.e(TAG, "AWOIS API error: ${data.getJSONObject("error").optString("message")}")
                return@withContext emptyList()
            }

            val features = data.optJSONArray("features") ?: return@withContext emptyList()
            val hazards = mutableListOf<NavigationHazard>()

            for (i in 0 until features.length()) {
                val attrs = features.getJSONObject(i).optJSONObject("attributes") ?: continue
                val lat = attrs.optString("LATDEC", "").toDoubleOrNull() ?: continue
                val lon = attrs.optString("LONDEC", "").toDoubleOrNull() ?: continue

                hazards.add(
                    NavigationHazard(
                        id = "awois_${attrs.optInt("FID", i)}",
                        name = attrs.optString("VESSLTERMS", "Navigation Hazard"),
                        latitude = lat,
                        longitude = lon,
                        type = attrs.optString("VESSLTERMS", "Unknown"),
                        description = buildString {
                            attrs.optString("HISTORY", "").takeIf { it.isNotBlank() }?.let { append(it) }
                            attrs.optString("CHART", "").takeIf { it.isNotBlank() }?.let {
                                if (isNotEmpty()) append(" | ")
                                append("Chart: $it")
                            }
                        }
                    )
                )
            }
            hazards
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch navigation hazards", e)
            emptyList()
        }
    }
}
