package com.captainslog.nautical

import com.captainslog.nautical.model.*

object NauticalProviders {
    val all: List<NauticalProviderMeta> = listOf(
        NauticalProviderMeta(
            id = "openseamap",
            name = "OpenSeaMap",
            tier = ProviderTier.FREE,
            type = ProviderType.TILE,
            description = "Nautical marks, buoys, lights, and other seamark overlays on OpenStreetMap.",
            website = "https://openseamap.org",
            pros = listOf("Completely free", "Community maintained", "Global coverage"),
            cons = listOf("Limited detail in some regions", "Community-dependent updates"),
            requiresApiKey = false
        ),
        NauticalProviderMeta(
            id = "noaa-charts",
            name = "NOAA Charts",
            tier = ProviderTier.FREE,
            type = ProviderType.TILE,
            description = "Official US nautical charts from NOAA via WMTS tile service.",
            website = "https://charts.noaa.gov",
            pros = listOf("Official government data", "High accuracy for US waters", "Free to use"),
            cons = listOf("US waters only", "Can be slow to update"),
            requiresApiKey = false
        ),
        NauticalProviderMeta(
            id = "gebco",
            name = "GEBCO Bathymetry",
            tier = ProviderTier.FREE,
            type = ProviderType.TILE,
            description = "Global bathymetry and ocean depth visualization via WMS.",
            website = "https://www.gebco.net",
            pros = listOf("Global ocean depth data", "Free to use", "Scientific quality"),
            cons = listOf("Lower resolution in some areas", "WMS can be slower than tile sources"),
            requiresApiKey = false
        ),
        NauticalProviderMeta(
            id = "noaa-coops",
            name = "NOAA CO-OPS",
            tier = ProviderTier.FREE,
            type = ProviderType.DATA,
            description = "Real-time and predicted tide and current data from US stations.",
            website = "https://tidesandcurrents.noaa.gov",
            pros = listOf("Official NOAA data", "Real-time observations", "Tide predictions"),
            cons = listOf("US stations only", "Rate limited"),
            requiresApiKey = false
        ),
        NauticalProviderMeta(
            id = "aisstream",
            name = "AISstream.io",
            tier = ProviderTier.FREE,
            type = ProviderType.DATA,
            description = "Real-time coastal AIS vessel tracking via WebSocket.",
            website = "https://aisstream.io",
            pros = listOf("Real-time vessel positions", "WebSocket streaming", "Free tier available"),
            cons = listOf("Requires free API key", "Coastal coverage only"),
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
            pros = listOf("Completely free", "No API key needed", "Global coverage"),
            cons = listOf("Forecast only, no observations", "Less detail than paid alternatives"),
            requiresApiKey = false
        ),
        NauticalProviderMeta(
            id = "worldtides",
            name = "WorldTides",
            tier = ProviderTier.PAID,
            type = ProviderType.DATA,
            description = "Global tide predictions and observations with high accuracy.",
            website = "https://www.worldtides.info",
            pros = listOf("Global coverage", "High accuracy", "Detailed predictions"),
            cons = listOf("Paid per request", "Credits expire"),
            requiresApiKey = true,
            apiKeySignupUrl = "https://www.worldtides.info/developer",
            pricingNote = "$10 for 5,000 predictions"
        ),
        NauticalProviderMeta(
            id = "stormglass",
            name = "Stormglass",
            tier = ProviderTier.PAID,
            type = ProviderType.DATA,
            description = "Premium marine weather data from multiple sources.",
            website = "https://stormglass.io",
            pros = listOf("Multiple weather models", "High accuracy", "Free tier (10 req/day)"),
            cons = listOf("Limited free tier", "Can be expensive at scale"),
            requiresApiKey = true,
            apiKeySignupUrl = "https://stormglass.io/register",
            pricingNote = "Free tier: 10 requests/day. Paid plans from \$19/month."
        ),
        NauticalProviderMeta(
            id = "windy",
            name = "Windy",
            tier = ProviderTier.PAID,
            type = ProviderType.TILE,
            description = "Animated wind, wave, and weather tile overlays.",
            website = "https://api.windy.com",
            pros = listOf("Beautiful visualizations", "Animated overlays", "Multiple data layers"),
            cons = listOf("Expensive", "API key required"),
            requiresApiKey = true,
            apiKeySignupUrl = "https://api.windy.com/signup",
            pricingNote = "~\$720/year"
        ),
        NauticalProviderMeta(
            id = "navionics",
            name = "Navionics/Garmin",
            tier = ProviderTier.PAID,
            type = ProviderType.TILE,
            description = "Premium nautical charts with detailed depth contours and marina info.",
            website = "https://www.navionics.com",
            pros = listOf("Industry-leading charts", "Detailed depth data", "Marina information"),
            cons = listOf("Expensive", "Contact for pricing", "Complex integration"),
            requiresApiKey = true,
            apiKeySignupUrl = "https://developer.navionics.com",
            pricingNote = "Contact Garmin/Navionics for pricing"
        ),
        NauticalProviderMeta(
            id = "marinetraffic",
            name = "MarineTraffic",
            tier = ProviderTier.PAID,
            type = ProviderType.DATA,
            description = "Global vessel tracking with satellite AIS coverage.",
            website = "https://www.marinetraffic.com",
            pros = listOf("Global coverage", "Satellite + terrestrial AIS", "Historical data"),
            cons = listOf("Credit-based pricing", "Can be expensive"),
            requiresApiKey = true,
            apiKeySignupUrl = "https://www.marinetraffic.com/en/ais-api-services",
            pricingNote = "Credit-based pricing, varies by endpoint"
        )
    )

    val free = all.filter { it.tier == ProviderTier.FREE }
    val paid = all.filter { it.tier == ProviderTier.PAID }
    fun getById(id: String) = all.find { it.id == id }
}
