export type NauticalProviderTier = 'free' | 'paid'
export type NauticalProviderType = 'tile' | 'data'

export interface NauticalProviderMeta {
  id: string
  name: string
  tier: NauticalProviderTier
  type: NauticalProviderType
  description: string
  website: string
  pros: string[]
  cons: string[]
  requiresApiKey: boolean
  apiKeySignupUrl?: string
  pricingNote?: string
}

export interface NauticalProviderConfig {
  enabled: boolean
  apiKey?: string
  options?: Record<string, unknown>
}

export type NauticalSettings = Record<string, NauticalProviderConfig>
