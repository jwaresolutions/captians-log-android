package com.captainslog.nautical.tile

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex

object NauticalTileSources {

    private const val OPENSEAMAP_BASE = "https://t1.openseamap.org/seamark/"
    private const val NOAA_MCS_BASE = "https://gis.charttools.noaa.gov/arcgis/rest/services/MCS/NOAAChartDisplay/MapServer/exts/MaritimeChartService/MapServer/export"
    private const val GEBCO_WMS_BASE = "https://wms.gebco.net/mapserv"

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

    val gebcoBathymetry = object : OnlineTileSourceBase(
        "GEBCOBathymetry", 0, 12, 256, ".jpeg",
        arrayOf(GEBCO_WMS_BASE)
    ) {
        override fun getTileURLString(pMapTileIndex: Long): String {
            val zoom = MapTileIndex.getZoom(pMapTileIndex)
            val x = MapTileIndex.getX(pMapTileIndex)
            val y = MapTileIndex.getY(pMapTileIndex)
            val bbox = tileToBbox3857(x, y, zoom)
            return "$GEBCO_WMS_BASE?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&LAYERS=gebco_latest&STYLES=&SRS=EPSG:3857&BBOX=${bbox[0]},${bbox[1]},${bbox[2]},${bbox[3]}&WIDTH=256&HEIGHT=256&FORMAT=image/jpeg"
        }
    }

    fun getSourceById(id: String): OnlineTileSourceBase? = when (id) {
        "openseamap" -> openSeaMap
        "noaa-charts" -> noaaCharts
        "gebco" -> gebcoBathymetry
        else -> null
    }

    val tileProviderIds = listOf("openseamap")
}
