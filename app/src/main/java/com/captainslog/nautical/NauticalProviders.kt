package com.captainslog.nautical

import com.captainslog.nautical.model.*

object NauticalProviders {
    val all: List<NauticalProviderMeta> = listOf(
        NauticalProviderMeta(
            id = "openseamap",
            name = "OpenSeaMap",
            tier = ProviderTier.FREE,
            type = ProviderType.TILE,
            description = "Nautical marks, buoys, lights, and other seamark overlays on OpenStreetMap. See openseamap.org/legend for symbol meanings.",
            website = "https://openseamap.org",
            features = listOf("Completely free", "Community maintained", "Global coverage"),
            warnings = listOf("Limited detail in some regions", "Community-dependent updates"),
            requiresApiKey = false,
            mapRole = MapRole.OVERLAY
        ),
        NauticalProviderMeta(
            id = "noaa-charts",
            name = "NOAA Charts",
            tier = ProviderTier.FREE,
            type = ProviderType.TILE,
            description = "Official US coastal nautical charts from NOAA showing depths, hazards, channels, and aids to navigation.",
            website = "https://nauticalcharts.noaa.gov",
            features = listOf("Official government data", "Depth soundings and hazards", "Free to use", "Viewed areas cached for offline use"),
            warnings = listOf("US coastal waters only", "Internet required to load new areas", "Can be slow on first load"),
            requiresApiKey = false,
            group = "NOAA",
            mapRole = MapRole.BASE_MAP
        ),
        NauticalProviderMeta(
            id = "gebco",
            name = "GEBCO Bathymetry",
            tier = ProviderTier.FREE,
            type = ProviderType.TILE,
            description = "Global bathymetry and ocean depth visualization via WMS.",
            website = "https://www.gebco.net",
            features = listOf("Global ocean depth data", "Free to use", "Scientific quality"),
            warnings = listOf("Lower resolution in some areas", "Pixelated when zoomed in past level 12", "First load can be slow (tiles are cached after)"),
            requiresApiKey = false,
            mapRole = MapRole.BASE_MAP
        ),
        NauticalProviderMeta(
            id = "noaa-coops",
            name = "NOAA CO-OPS",
            tier = ProviderTier.FREE,
            type = ProviderType.DATA,
            description = "Real-time and predicted tide and current data from US stations.",
            website = "https://tidesandcurrents.noaa.gov",
            features = listOf("Official NOAA data", "Real-time observations", "Tide predictions shown on map markers", "Works on any base map"),
            warnings = listOf("US stations only", "Rate limited"),
            requiresApiKey = false,
            group = "NOAA"
        ),
        NauticalProviderMeta(
            id = "aisstream",
            name = "AISstream.io",
            tier = ProviderTier.FREE,
            type = ProviderType.DATA,
            description = "Real-time coastal AIS vessel tracking via WebSocket.",
            website = "https://aisstream.io",
            features = listOf("Real-time vessel positions", "WebSocket streaming", "Free tier available"),
            warnings = listOf("Requires free API key", "Coastal coverage only", "Service can be unreliable"),
            requiresApiKey = true,
            apiKeySignupUrl = "https://aisstream.io/authenticate"
        ),
        NauticalProviderMeta(
            id = "open-meteo",
            name = "Open-Meteo Marine",
            tier = ProviderTier.FREE,
            type = ProviderType.DATA,
            description = "Marine weather forecasts including wave height, swell, and wind.",
            website = "https://open-meteo.com",
            features = listOf("Completely free", "No API key needed", "Global coverage"),
            warnings = listOf("Forecast only, no observations", "Less detail than paid alternatives"),
            requiresApiKey = false
        ),
        NauticalProviderMeta(
            id = "noaa-alerts",
            name = "NOAA Weather Alerts",
            tier = ProviderTier.FREE,
            type = ProviderType.DATA,
            description = "Active marine weather alerts from the National Weather Service including storm warnings, small craft advisories, and coastal hazards.",
            website = "https://www.weather.gov",
            features = listOf("Official NWS alerts", "Zone polygon overlays", "Push notifications", "Severity-based coloring"),
            warnings = listOf("US waters only", "Requires internet"),
            requiresApiKey = false,
            group = "NOAA"
        ),
        NauticalProviderMeta(
            id = "open-meteo-ocean",
            name = "Open-Meteo Ocean",
            tier = ProviderTier.FREE,
            type = ProviderType.DATA,
            description = "Ocean current velocity, direction, and sea surface temperature data.",
            website = "https://open-meteo.com",
            features = listOf("Ocean currents", "Sea surface temperature", "No API key needed", "Global coverage"),
            warnings = listOf("Forecast data only", "Limited resolution"),
            requiresApiKey = false
        ),
    )

    val free = all.filter { it.tier == ProviderTier.FREE }
    fun getById(id: String) = all.find { it.id == id }
}
