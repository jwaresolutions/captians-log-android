import { useState, useEffect, useRef } from 'react'
import { useNauticalSettings } from './useNauticalSettings'
import { fetchTideStations, fetchTidePredictions, TideStation, TidePrediction } from '../services/nautical/noaaCoOps'
import { AISStreamService, AISVessel } from '../services/nautical/aisStream'
import { fetchMarineWeather, MarineWeather } from '../services/nautical/openMeteo'
import { fetchWorldTides, WorldTidePrediction } from '../services/nautical/worldTides'
import { fetchStormglassWeather, StormglassWeather } from '../services/nautical/stormglass'
import { fetchMarineTrafficVessels, MarineTrafficVessel } from '../services/nautical/marineTraffic'

export interface TideStationWithPredictions extends TideStation {
  predictions: TidePrediction[]
}

export interface NauticalData {
  vessels: AISVessel[]
  marineTrafficVessels: MarineTrafficVessel[]
  tideStations: TideStationWithPredictions[]
  worldTides: WorldTidePrediction[]
  weather: MarineWeather | null
  stormglassWeather: StormglassWeather | null
  isLoading: boolean
}

export interface MapBounds {
  minLat: number
  minLng: number
  maxLat: number
  maxLng: number
  centerLat: number
  centerLng: number
}

export const useNauticalData = (bounds: MapBounds | null) => {
  const { isEnabled, getProviderConfig } = useNauticalSettings()

  const [vessels, setVessels] = useState<AISVessel[]>([])
  const [marineTrafficVessels, setMarineTrafficVessels] = useState<MarineTrafficVessel[]>([])
  const [tideStations, setTideStations] = useState<TideStationWithPredictions[]>([])
  const [worldTides, setWorldTides] = useState<WorldTidePrediction[]>([])
  const [weather, setWeather] = useState<MarineWeather | null>(null)
  const [stormglassWeather, setStormglassWeather] = useState<StormglassWeather | null>(null)
  const [isLoading] = useState(false)

  const aisServiceRef = useRef<AISStreamService | null>(null)

  // NOAA CO-OPS tides - poll every 30 min
  useEffect(() => {
    if (!bounds || !isEnabled('noaa-coops')) {
      setTideStations([])
      return
    }

    const loadTides = async () => {
      const stations = await fetchTideStations(bounds.minLat, bounds.minLng, bounds.maxLat, bounds.maxLng)
      const stationsWithPredictions = await Promise.all(
        stations.slice(0, 20).map(async (station) => {
          const predictions = await fetchTidePredictions(station.id)
          return { ...station, predictions }
        })
      )
      setTideStations(stationsWithPredictions)
    }

    loadTides()
    const interval = setInterval(loadTides, 30 * 60 * 1000)
    return () => clearInterval(interval)
  }, [bounds?.minLat, bounds?.maxLat, bounds?.minLng, bounds?.maxLng, isEnabled])

  // AISstream WebSocket
  useEffect(() => {
    const aisConfig = getProviderConfig('aisstream')
    if (!bounds || !isEnabled('aisstream') || !aisConfig.apiKey) {
      aisServiceRef.current?.disconnect()
      aisServiceRef.current = null
      setVessels([])
      return
    }

    const service = new AISStreamService()
    aisServiceRef.current = service
    service.connect(
      aisConfig.apiKey,
      [[bounds.minLat, bounds.minLng], [bounds.maxLat, bounds.maxLng]],
      (updatedVessels) => setVessels(updatedVessels)
    )

    return () => {
      service.disconnect()
      aisServiceRef.current = null
    }
  }, [bounds?.minLat, bounds?.maxLat, isEnabled, getProviderConfig])

  // Open-Meteo weather - poll every 15 min
  useEffect(() => {
    if (!bounds || !isEnabled('open-meteo')) {
      setWeather(null)
      return
    }

    const loadWeather = async () => {
      const data = await fetchMarineWeather(bounds.centerLat, bounds.centerLng)
      setWeather(data)
    }

    loadWeather()
    const interval = setInterval(loadWeather, 15 * 60 * 1000)
    return () => clearInterval(interval)
  }, [bounds?.centerLat, bounds?.centerLng, isEnabled])

  // WorldTides - poll every 30 min
  useEffect(() => {
    const config = getProviderConfig('worldtides')
    if (!bounds || !isEnabled('worldtides') || !config.apiKey) {
      setWorldTides([])
      return
    }

    const loadWorldTides = async () => {
      const data = await fetchWorldTides(bounds.centerLat, bounds.centerLng, config.apiKey!)
      setWorldTides(data)
    }

    loadWorldTides()
    const interval = setInterval(loadWorldTides, 30 * 60 * 1000)
    return () => clearInterval(interval)
  }, [bounds?.centerLat, bounds?.centerLng, isEnabled, getProviderConfig])

  // Stormglass - poll every 15 min
  useEffect(() => {
    const config = getProviderConfig('stormglass')
    if (!bounds || !isEnabled('stormglass') || !config.apiKey) {
      setStormglassWeather(null)
      return
    }

    const loadStormglass = async () => {
      const data = await fetchStormglassWeather(bounds.centerLat, bounds.centerLng, config.apiKey!)
      setStormglassWeather(data)
    }

    loadStormglass()
    const interval = setInterval(loadStormglass, 15 * 60 * 1000)
    return () => clearInterval(interval)
  }, [bounds?.centerLat, bounds?.centerLng, isEnabled, getProviderConfig])

  // MarineTraffic - poll every 5 min
  useEffect(() => {
    const config = getProviderConfig('marinetraffic')
    if (!bounds || !isEnabled('marinetraffic') || !config.apiKey) {
      setMarineTrafficVessels([])
      return
    }

    const loadMT = async () => {
      const data = await fetchMarineTrafficVessels(
        bounds.minLat, bounds.minLng, bounds.maxLat, bounds.maxLng, config.apiKey!
      )
      setMarineTrafficVessels(data)
    }

    loadMT()
    const interval = setInterval(loadMT, 5 * 60 * 1000)
    return () => clearInterval(interval)
  }, [bounds?.minLat, bounds?.maxLat, isEnabled, getProviderConfig])

  return {
    vessels,
    marineTrafficVessels,
    tideStations,
    worldTides,
    weather,
    stormglassWeather,
    isLoading,
  } as NauticalData
}
