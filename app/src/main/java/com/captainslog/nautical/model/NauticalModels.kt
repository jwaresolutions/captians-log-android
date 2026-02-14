package com.captainslog.nautical.model

data class NauticalProviderMeta(
    val id: String,
    val name: String,
    val tier: ProviderTier,
    val type: ProviderType,
    val description: String,
    val website: String,
    val features: List<String>,
    val warnings: List<String>,
    val requiresApiKey: Boolean,
    val apiKeySignupUrl: String? = null,
    val pricingNote: String? = null,
    val parentId: String? = null,
    val group: String? = null,
    val mapRole: MapRole = MapRole.DATA
)

enum class ProviderTier { FREE, PAID }
enum class ProviderType { TILE, DATA }
enum class MapRole { BASE_MAP, OVERLAY, DATA }

data class NauticalProviderConfig(
    val enabled: Boolean = false,
    val apiKey: String? = null,
    val options: Map<String, String> = emptyMap()
)

typealias NauticalSettings = Map<String, NauticalProviderConfig>

data class MarineAlert(
    val id: String,
    val event: String,
    val headline: String,
    val description: String,
    val severity: String,
    val areaDesc: String,
    val polygon: List<org.osmdroid.util.GeoPoint>?,
    val onset: String?,
    val expires: String?
)

data class NavigationHazard(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val type: String,
    val description: String
)

data class OceanData(
    val currentVelocity: Double?,
    val currentDirection: Double?,
    val seaSurfaceTemp: Double?
)
