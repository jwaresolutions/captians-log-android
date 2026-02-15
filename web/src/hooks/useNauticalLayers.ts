import { useMemo } from 'react'
import { useNauticalSettings } from './useNauticalSettings'
import { nauticalTileSources, NauticalTileSource } from '../config/nauticalTileSources'
import { nauticalProviders } from '../config/nauticalProviders'

export const useNauticalLayers = () => {
  const { isEnabled, getProviderConfig } = useNauticalSettings()

  const enabledTileLayers = useMemo(() => {
    return nauticalProviders
      .filter(p => p.type === 'tile' && isEnabled(p.id))
      .filter(p => {
        // If provider requires API key, check that one is set
        if (p.requiresApiKey) {
          const config = getProviderConfig(p.id)
          return !!config.apiKey
        }
        return true
      })
      .map(p => nauticalTileSources[p.id])
      .filter((source): source is NauticalTileSource => !!source)
  }, [isEnabled, getProviderConfig])

  return { enabledTileLayers }
}
