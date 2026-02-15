import { useState, useCallback, useEffect } from 'react'
import { NauticalSettings, NauticalProviderConfig } from '../types/nautical'

const STORAGE_KEY = 'nautical_settings'

const getStoredSettings = (): NauticalSettings => {
  try {
    const stored = localStorage.getItem(STORAGE_KEY)
    return stored ? JSON.parse(stored) : {}
  } catch {
    return {}
  }
}

const saveSettings = (settings: NauticalSettings) => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(settings))
}

export const useNauticalSettings = () => {
  const [settings, setSettings] = useState<NauticalSettings>(getStoredSettings)

  useEffect(() => {
    saveSettings(settings)
  }, [settings])

  const getProviderConfig = useCallback((id: string): NauticalProviderConfig => {
    return settings[id] || { enabled: false }
  }, [settings])

  const isEnabled = useCallback((id: string): boolean => {
    return settings[id]?.enabled ?? false
  }, [settings])

  const toggleProvider = useCallback((id: string) => {
    setSettings(prev => {
      const current = prev[id] || { enabled: false }
      return { ...prev, [id]: { ...current, enabled: !current.enabled } }
    })
  }, [])

  const setApiKey = useCallback((id: string, apiKey: string) => {
    setSettings(prev => {
      const current = prev[id] || { enabled: false }
      return { ...prev, [id]: { ...current, apiKey } }
    })
  }, [])

  const setProviderOption = useCallback((id: string, key: string, value: unknown) => {
    setSettings(prev => {
      const current = prev[id] || { enabled: false }
      return {
        ...prev,
        [id]: { ...current, options: { ...current.options, [key]: value } },
      }
    })
  }, [])

  return {
    settings,
    getProviderConfig,
    isEnabled,
    toggleProvider,
    setApiKey,
    setProviderOption,
  }
}
