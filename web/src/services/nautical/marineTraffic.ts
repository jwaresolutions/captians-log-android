export interface MarineTrafficVessel {
  mmsi: number
  name: string
  latitude: number
  longitude: number
  speed: number
  heading: number
  shipType: string
  destination: string
  timestamp: string
}

export const fetchMarineTrafficVessels = async (
  minLat: number,
  minLng: number,
  maxLat: number,
  maxLng: number,
  apiKey: string
): Promise<MarineTrafficVessel[]> => {
  try {
    const response = await fetch(
      `https://services.marinetraffic.com/api/exportvessels/v:8/${apiKey}/MINLAT:${minLat}/MAXLAT:${maxLat}/MINLON:${minLng}/MAXLON:${maxLng}/protocol:jsono`
    )
    if (!response.ok) return []
    const data = await response.json()
    return (Array.isArray(data) ? data : []).map((v: any) => ({
      mmsi: parseInt(v.MMSI),
      name: v.SHIPNAME || `MMSI ${v.MMSI}`,
      latitude: parseFloat(v.LAT),
      longitude: parseFloat(v.LON),
      speed: parseFloat(v.SPEED) / 10,
      heading: parseInt(v.HEADING),
      shipType: v.SHIPTYPE || '',
      destination: v.DESTINATION || '',
      timestamp: v.TIMESTAMP || '',
    }))
  } catch {
    return []
  }
}
