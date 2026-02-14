package com.captainslog.nautical.tile

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex

object NauticalTileSources {

    private const val OPENSEAMAP_BASE = "https://t1.openseamap.org/seamark/"
    private const val NOAA_MCS_BASE = "https://gis.charttools.noaa.gov/arcgis/rest/services/MCS/NOAAChartDisplay/MapServer/exts/MaritimeChartService/MapServer/export"

    /** Web Mercator origin shift (half circumference in meters) */
    private const val ORIGIN_SHIFT = 20037508.342789244

    /** Convert tile X/Y at zoom to Web Mercator (EPSG:3857) bounding box */
    private fun tileToBbox3857(x: Int, y: Int, zoom: Int): DoubleArray {
        val n = (1 shl zoom).toDouble()
        val tileSize = 2.0 * ORIGIN_SHIFT / n
        val minX = -ORIGIN_SHIFT + x * tileSize
        val maxX = minX + tileSize
        val maxY = ORIGIN_SHIFT - y * tileSize
        val minY = maxY - tileSize
        return doubleArrayOf(minX, minY, maxX, maxY)
    }

    val openSeaMap = object : OnlineTileSourceBase(
        "OpenSeaMap", 7, 18, 256, ".png",
        arrayOf(OPENSEAMAP_BASE)
    ) {
        override fun getTileURLString(pMapTileIndex: Long): String {
            val zoom = MapTileIndex.getZoom(pMapTileIndex)
            val x = MapTileIndex.getX(pMapTileIndex)
            val y = MapTileIndex.getY(pMapTileIndex)
            return "$OPENSEAMAP_BASE$zoom/$x/$y.png"
        }
    }

    val noaaCharts = object : OnlineTileSourceBase(
        "NOAACharts", 3, 16, 256, ".png",
        arrayOf(NOAA_MCS_BASE)
    ) {
        override fun getTileURLString(pMapTileIndex: Long): String {
            val zoom = MapTileIndex.getZoom(pMapTileIndex)
            val x = MapTileIndex.getX(pMapTileIndex)
            val y = MapTileIndex.getY(pMapTileIndex)
            val bbox = tileToBbox3857(x, y, zoom)
            return "$NOAA_MCS_BASE?bbox=${bbox[0]},${bbox[1]},${bbox[2]},${bbox[3]}&bboxSR=3857&imageSR=3857&size=256,256&format=png&f=image"
        }
    }

    fun getSourceById(id: String): OnlineTileSourceBase? = when (id) {
        "openseamap" -> openSeaMap
        "noaa-charts" -> noaaCharts
        // GEBCO bathymetry requires WMS support which is not yet implemented for osmdroid
        "gebco" -> null
        else -> null
    }

    // Note: GEBCO is in settings but not yet supported on Android (requires WMS tile support)
    val tileProviderIds = listOf("openseamap")
}
