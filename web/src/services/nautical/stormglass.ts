export interface StormglassWeather {
  waveHeight: number | null
  wavePeriod: number | null
  waveDirection: number | null
  windSpeed: number | null
  windDirection: number | null
  waterTemperature: number | null
  airTemperature: number | null
  visibility: number | null
  timestamp: string
}

export const fetchStormglassWeather = async (
  lat: number,
  lng: number,
  apiKey: string
): Promise<StormglassWeather | null> => {
  try {
    const params = [
      'waveHeight', 'wavePeriod', 'waveDirection',
      'windSpeed', 'windDirection',
      'waterTemperature', 'airTemperature', 'visibility',
    ].join(',')

    const response = await fetch(
      `https://api.stormglass.io/v2/weather/point?lat=${lat}&lng=${lng}&params=${params}`,
      { headers: { Authorization: apiKey } }
    )
    if (!response.ok) return null
    const data = await response.json()
    const hour = data.hours?.[0]
    if (!hour) return null

    const sg = (field: string) => hour[field]?.sg ?? hour[field]?.noaa ?? null

    return {
      waveHeight: sg('waveHeight'),
      wavePeriod: sg('wavePeriod'),
      waveDirection: sg('waveDirection'),
      windSpeed: sg('windSpeed'),
      windDirection: sg('windDirection'),
      waterTemperature: sg('waterTemperature'),
      airTemperature: sg('airTemperature'),
      visibility: sg('visibility'),
      timestamp: hour.time,
    }
  } catch {
    return null
  }
}
