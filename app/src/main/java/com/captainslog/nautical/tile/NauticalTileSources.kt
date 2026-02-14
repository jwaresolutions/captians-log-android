package com.captainslog.nautical.tile

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex

object NauticalTileSources {

    val openSeaMap = object : OnlineTileSourceBase(
        "OpenSeaMap", 7, 18, 256, ".png",
        arrayOf("https://t1.openseamap.org/seamark/")
    ) {
        override fun getTileURLString(pMapTileIndex: Long): String {
            val zoom = MapTileIndex.getZoom(pMapTileIndex)
            val x = MapTileIndex.getX(pMapTileIndex)
            val y = MapTileIndex.getY(pMapTileIndex)
            return "${baseUrl[0]}$zoom/$x/$y.png"
        }
    }

    val noaaCharts = object : OnlineTileSourceBase(
        "NOAACharts", 1, 16, 256, ".png",
        arrayOf("https://tileservice.charts.noaa.gov/tiles/50000_1/")
    ) {
        override fun getTileURLString(pMapTileIndex: Long): String {
            val zoom = MapTileIndex.getZoom(pMapTileIndex)
            val x = MapTileIndex.getX(pMapTileIndex)
            val y = MapTileIndex.getY(pMapTileIndex)
            return "${baseUrl[0]}$zoom/$x/$y.png"
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
    val tileProviderIds = listOf("openseamap", "noaa-charts")
}
