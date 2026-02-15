export interface WorldTidePrediction {
  date: string
  height: number
  type: 'High' | 'Low'
}

export const fetchWorldTides = async (
  lat: number,
  lng: number,
  apiKey: string
): Promise<WorldTidePrediction[]> => {
  try {
    const response = await fetch(
      `https://www.worldtides.info/api/v3?extremes&lat=${lat}&lon=${lng}&key=${apiKey}`
    )
    if (!response.ok) return []
    const data = await response.json()
    return (data.extremes || []).map((e: any) => ({
      date: e.date,
      height: e.height,
      type: e.type === 'High' ? 'High' : 'Low',
    }))
  } catch {
    return []
  }
}
