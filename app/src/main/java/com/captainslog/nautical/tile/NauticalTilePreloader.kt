package com.captainslog.nautical.tile

import android.content.Context
import android.util.Log
import com.captainslog.nautical.NauticalSettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object NauticalTilePreloader {

    private const val TAG = "NauticalTilePreloader"

    private const val DEFAULT_LAT = 47.6062
    private const val DEFAULT_LON = -122.3321
    private const val PRELOAD_RADIUS_DEG = 0.5

    private const val MIN_PRELOAD_ZOOM = 8
    private const val MAX_PRELOAD_ZOOM = 12

    fun preload(context: Context, settingsManager: NauticalSettingsManager) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = context.getSharedPreferences("captains_log_prefs", Context.MODE_PRIVATE)
                val lat = prefs.getFloat("last_map_lat", DEFAULT_LAT.toFloat()).toDouble()
                val lon = prefs.getFloat("last_map_lon", DEFAULT_LON.toFloat()).toDouble()

                val cachePath = Configuration.getInstance().osmdroidTileCache

                // Only preload NOAA charts if enabled in settings
                if (settingsManager.isEnabled("noaa-charts")) {
                    preloadSource(NauticalTileSources.noaaCharts, cachePath, lat, lon)
                }

                // Only preload OpenSeaMap if enabled in settings
                if (settingsManager.isEnabled("openseamap")) {
                    preloadSource(NauticalTileSources.openSeaMap, cachePath, lat, lon)
                }

                // Only preload GEBCO bathymetry if enabled in settings
                if (settingsManager.isEnabled("gebco")) {
                    preloadSource(NauticalTileSources.gebcoBathymetry, cachePath, lat, lon)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error preloading tiles", e)
            }
        }
    }

    private fun preloadSource(source: OnlineTileSourceBase, cacheBase: File, lat: Double, lon: Double) {
        val sourceName = source.name()
        var downloaded = 0
        var skipped = 0

        for (zoom in MIN_PRELOAD_ZOOM..MAX_PRELOAD_ZOOM) {
            val minTileX = lonToTileX(lon - PRELOAD_RADIUS_DEG, zoom)
            val maxTileX = lonToTileX(lon + PRELOAD_RADIUS_DEG, zoom)
            val minTileY = latToTileY(lat + PRELOAD_RADIUS_DEG, zoom)
            val maxTileY = latToTileY(lat - PRELOAD_RADIUS_DEG, zoom)

            for (x in minTileX..maxTileX) {
                for (y in minTileY..maxTileY) {
                    val cacheDir = File(cacheBase, sourceName)
                    val cacheFile = File(cacheDir, "$zoom/$x/$y.png")
                    if (cacheFile.exists()) {
                        skipped++
                        continue
                    }

                    try {
                        val tileIndex = MapTileIndex.getTileIndex(zoom, x, y)
                        val urlStr = source.getTileURLString(tileIndex)
                        val conn = URL(urlStr).openConnection() as HttpURLConnection
                        conn.connectTimeout = 10000
                        conn.readTimeout = 10000
                        conn.setRequestProperty("User-Agent", "CaptainsLog")

                        if (conn.responseCode == 200) {
                            cacheFile.parentFile?.mkdirs()
                            conn.inputStream.use { input ->
                                cacheFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            downloaded++
                        }
                        conn.disconnect()
                    } catch (e: Exception) {
                        // Skip failed tiles silently
                    }
                }
            }
        }
        Log.d(TAG, "$sourceName preload: $downloaded downloaded, $skipped cached")
    }

    private fun lonToTileX(lon: Double, zoom: Int): Int {
        return ((lon + 180.0) / 360.0 * (1 shl zoom)).toInt()
    }

    private fun latToTileY(lat: Double, zoom: Int): Int {
        val latRad = Math.toRadians(lat)
        return ((1.0 - Math.log(Math.tan(latRad) + 1.0 / Math.cos(latRad)) / Math.PI) / 2.0 * (1 shl zoom)).toInt()
    }
}
