package com.captainslog.nautical.model

data class NauticalProviderMeta(
    val id: String,
    val name: String,
    val tier: ProviderTier,
    val type: ProviderType,
    val description: String,
    val website: String,
    val pros: List<String>,
    val cons: List<String>,
    val requiresApiKey: Boolean,
    val apiKeySignupUrl: String? = null,
    val pricingNote: String? = null
)

enum class ProviderTier { FREE, PAID }
enum class ProviderType { TILE, DATA }

data class NauticalProviderConfig(
    val enabled: Boolean = false,
    val apiKey: String? = null,
    val options: Map<String, String> = emptyMap()
)

typealias NauticalSettings = Map<String, NauticalProviderConfig>
