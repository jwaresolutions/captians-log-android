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
