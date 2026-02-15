export interface NauticalTileSource {
  id: string
  url: string
  attribution: string
  opacity: number
  maxZoom: number
  type: 'xyz' | 'wms'
  wmsLayers?: string
  wmsFormat?: string
}

export const nauticalTileSources: Record<string, NauticalTileSource> = {
  openseamap: {
    id: 'openseamap',
    url: 'https://tiles.openseamap.org/seamark/{z}/{x}/{y}.png',
    attribution: '&copy; <a href="https://www.openseamap.org">OpenSeaMap</a> contributors',
    opacity: 0.7,
    maxZoom: 18,
    type: 'xyz',
  },
  'noaa-charts': {
    id: 'noaa-charts',
    url: 'https://tileservice.charts.noaa.gov/tiles/50000_1/{z}/{x}/{y}.png',
    attribution: '&copy; <a href="https://charts.noaa.gov">NOAA</a>',
    opacity: 0.8,
    maxZoom: 16,
    type: 'xyz',
  },
  gebco: {
    id: 'gebco',
    url: 'https://wms.gebco.net/mapserv?',
    attribution: '&copy; <a href="https://www.gebco.net">GEBCO</a>',
    opacity: 0.5,
    maxZoom: 12,
    type: 'wms',
    wmsLayers: 'GEBCO_LATEST',
    wmsFormat: 'image/png',
  },
  windy: {
    id: 'windy',
    url: 'https://tiles.windy.com/tiles/v10.0/wind/{z}/{x}/{y}.png',
    attribution: '&copy; <a href="https://windy.com">Windy</a>',
    opacity: 0.6,
    maxZoom: 18,
    type: 'xyz',
  },
  navionics: {
    id: 'navionics',
    url: 'https://backend.navionics.com/tile/{z}/{x}/{y}',
    attribution: '&copy; <a href="https://www.navionics.com">Navionics/Garmin</a>',
    opacity: 0.8,
    maxZoom: 18,
    type: 'xyz',
  },
}
