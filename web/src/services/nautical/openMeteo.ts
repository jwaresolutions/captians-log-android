export interface MarineWeather {
  latitude: number
  longitude: number
  waveHeight: number | null
  wavePeriod: number | null
  waveDirection: number | null
  windSpeed: number | null
  windDirection: number | null
  swellHeight: number | null
  swellPeriod: number | null
  temperature: number | null
  timestamp: string
}

export const fetchMarineWeather = async (lat: number, lng: number): Promise<MarineWeather | null> => {
  try {
    const params = new URLSearchParams({
      latitude: lat.toString(),
      longitude: lng.toString(),
      current: [
        'wave_height',
        'wave_period',
        'wave_direction',
        'wind_wave_height',
        'wind_wave_period',
        'swell_wave_height',
        'swell_wave_period',
      ].join(','),
      hourly: 'temperature_2m,wind_speed_10m,wind_direction_10m',
      forecast_days: '1',
      timezone: 'auto',
    })

    const response = await fetch(`https://marine-api.open-meteo.com/v1/marine?${params}`)
    if (!response.ok) return null
    const data = await response.json()

    const current = data.current || {}
    const hourly = data.hourly || {}

    return {
      latitude: data.latitude,
      longitude: data.longitude,
      waveHeight: current.wave_height ?? null,
      wavePeriod: current.wave_period ?? null,
      waveDirection: current.wave_direction ?? null,
      windSpeed: hourly.wind_speed_10m?.[0] ?? null,
      windDirection: hourly.wind_direction_10m?.[0] ?? null,
      swellHeight: current.swell_wave_height ?? null,
      swellPeriod: current.swell_wave_period ?? null,
      temperature: hourly.temperature_2m?.[0] ?? null,
      timestamp: current.time || new Date().toISOString(),
    }
  } catch {
    return null
  }
}
