export interface TideStation {
  id: string
  name: string
  latitude: number
  longitude: number
}

export interface TidePrediction {
  time: string
  value: number
  type: 'H' | 'L'
}

const BASE_URL = 'https://api.tidesandcurrents.noaa.gov/api/prod/datagetter'

export const fetchTideStations = async (
  minLat: number,
  minLng: number,
  maxLat: number,
  maxLng: number
): Promise<TideStation[]> => {
  try {
    const response = await fetch(
      'https://api.tidesandcurrents.noaa.gov/mdapi/prod/webapi/stations.json?type=tidepredictions'
    )
    if (!response.ok) return []
    const data = await response.json()
    const stations: TideStation[] = (data.stations || [])
      .filter((s: any) =>
        s.lat >= minLat && s.lat <= maxLat &&
        s.lng >= minLng && s.lng <= maxLng
      )
      .map((s: any) => ({
        id: s.id,
        name: s.name,
        latitude: s.lat,
        longitude: s.lng,
      }))
    return stations
  } catch {
    return []
  }
}

export const fetchTidePredictions = async (stationId: string): Promise<TidePrediction[]> => {
  try {
    const today = new Date()
    const tomorrow = new Date(today)
    tomorrow.setDate(tomorrow.getDate() + 1)
    const beginDate = today.toISOString().slice(0, 10).replace(/-/g, '')
    const endDate = tomorrow.toISOString().slice(0, 10).replace(/-/g, '')

    const params = new URLSearchParams({
      begin_date: beginDate,
      end_date: endDate,
      station: stationId,
      product: 'predictions',
      datum: 'MLLW',
      time_zone: 'lst_ldt',
      units: 'english',
      format: 'json',
      interval: 'hilo',
    })

    const response = await fetch(`${BASE_URL}?${params}`)
    if (!response.ok) return []
    const data = await response.json()
    return (data.predictions || []).map((p: any) => ({
      time: p.t,
      value: parseFloat(p.v),
      type: p.type,
    }))
  } catch {
    return []
  }
}
