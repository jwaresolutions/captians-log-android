import React, { useState, useCallback, useRef } from 'react'
import styled from 'styled-components'
import { MapContainer, TileLayer, Polyline, Marker, Popup, useMapEvents, WMSTileLayer } from 'react-leaflet'
import { LatLngTuple, Icon, DivIcon } from 'leaflet'
import { useNauticalLayers } from '../hooks/useNauticalLayers'
import { useNauticalData, MapBounds } from '../hooks/useNauticalData'
import { useNauticalSettings } from '../hooks/useNauticalSettings'
import { nauticalProviders } from '../config/nauticalProviders'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { LCARSHeader } from '../components/lcars/LCARSHeader'
import { LCARSDataDisplay } from '../components/lcars/LCARSDataDisplay'
import { useTrips } from '../hooks/useTrips'
import { useMarkedLocations, useCreateMarkedLocation, useDeleteMarkedLocation } from '../hooks/useLocations'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'
import { Trip, MarkedLocation } from '../types/api'
import 'leaflet/dist/leaflet.css'

// Fix for default markers in React-Leaflet
import markerIcon from 'leaflet/dist/images/marker-icon.png'
import markerIcon2x from 'leaflet/dist/images/marker-icon-2x.png'
import markerShadow from 'leaflet/dist/images/marker-shadow.png'

delete (Icon.Default.prototype as any)._getIconUrl
Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
})

const MapPageContainer = styled.div`
  display: flex;
  flex-direction: column;
  height: calc(100vh - 200px); // Account for header and footer
  gap: ${props => props.theme.spacing.md};
`

const MapControlsContainer = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.md};
  margin-bottom: ${props => props.theme.spacing.md};
`

const MapContainer_Styled = styled.div`
  position: relative;
  flex: 1;
  display: flex;
  gap: ${props => props.theme.spacing.md};
  min-height: 600px;
`

const MapPanel = styled(LCARSPanel)`
  flex: 1;
  
  .leaflet-container {
    height: 100%;
    min-height: 500px;
    background-color: ${props => props.theme.colors.surface.dark};
  }
  
  .leaflet-control-container {
    .leaflet-control {
      background-color: ${props => props.theme.colors.surface.medium};
      border: 1px solid ${props => props.theme.colors.primary.neonCarrot};

      a {
        color: ${props => props.theme.colors.text.primary};
        background-color: ${props => props.theme.colors.surface.medium};

        &:hover {
          background-color: ${props => props.theme.colors.primary.neonCarrot};
          color: ${props => props.theme.colors.text.inverse};
        }
      }
    }
  }
`

const SidePanel = styled(LCARSPanel)`
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.md};
`

const LocationList = styled.div`
  max-height: 300px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.sm};
`

const LocationItem = styled.div`
  padding: ${props => props.theme.spacing.sm};
  background-color: ${props => props.theme.colors.surface.medium};
  border-radius: ${props => props.theme.borderRadius.sm};
  border-left: 3px solid ${props => props.theme.colors.primary.anakiwa};

  .location-name {
    font-weight: ${props => props.theme.typography.fontWeight.bold};
    color: ${props => props.theme.colors.primary.anakiwa};
    margin-bottom: ${props => props.theme.spacing.xs};
  }
  
  .location-category {
    font-size: ${props => props.theme.typography.fontSize.sm};
    color: ${props => props.theme.colors.text.secondary};
    text-transform: uppercase;
    margin-bottom: ${props => props.theme.spacing.xs};
  }
  
  .location-notes {
    font-size: ${props => props.theme.typography.fontSize.sm};
    color: ${props => props.theme.colors.text.muted};
  }
  
  .location-actions {
    margin-top: ${props => props.theme.spacing.sm};
    display: flex;
    gap: ${props => props.theme.spacing.sm};
  }
`

const NewLocationForm = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.sm};
  
  input, select, textarea {
    padding: ${props => props.theme.spacing.sm};
    background-color: ${props => props.theme.colors.surface.medium};
    border: 1px solid ${props => props.theme.colors.primary.neonCarrot};
    border-radius: ${props => props.theme.borderRadius.sm};
    color: ${props => props.theme.colors.text.primary};
    font-family: ${props => props.theme.typography.fontFamily.primary};

    &:focus {
      outline: none;
      border-color: ${props => props.theme.colors.primary.tanoi};
      box-shadow: 0 0 0 2px ${props => props.theme.colors.primary.neonCarrot}20;
    }
  }
  
  textarea {
    resize: vertical;
    min-height: 60px;
  }
`

const FilterControls = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.sm};
  flex-wrap: wrap;
  align-items: center;
  
  label {
    color: ${props => props.theme.colors.text.secondary};
    font-size: ${props => props.theme.typography.fontSize.sm};
    text-transform: uppercase;
  }
  
  select {
    padding: ${props => props.theme.spacing.xs} ${props => props.theme.spacing.sm};
    background-color: ${props => props.theme.colors.surface.medium};
    border: 1px solid ${props => props.theme.colors.primary.anakiwa};
    border-radius: ${props => props.theme.borderRadius.sm};
    color: ${props => props.theme.colors.text.primary};
    font-family: ${props => props.theme.typography.fontFamily.primary};
  }
`

// Custom marker icons for different categories
const createCategoryIcon = (category: string) => {
  const colors = {
    fishing: '#66FF66',
    marina: '#6688CC',
    anchorage: '#FFFF66',
    hazard: '#FF6666',
    other: '#CC99CC',
  }
  
  return new DivIcon({
    html: `<div style="
      background-color: ${colors[category as keyof typeof colors] || colors.other};
      width: 20px;
      height: 20px;
      border-radius: 50%;
      border: 2px solid #000;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
      font-size: 10px;
      color: #000;
    ">${category.charAt(0).toUpperCase()}</div>`,
    className: 'custom-marker',
    iconSize: [20, 20],
    iconAnchor: [10, 10],
  })
}

// Component for handling map clicks
const MapClickHandler: React.FC<{ onMapClick: (lat: number, lng: number) => void }> = ({ onMapClick }) => {
  useMapEvents({
    click: (e) => {
      onMapClick(e.latlng.lat, e.latlng.lng)
    },
  })
  return null
}

// Component for tracking map bounds
const MapBoundsTracker: React.FC<{ onBoundsChange: (bounds: MapBounds) => void }> = ({ onBoundsChange }) => {
  const map = useMapEvents({
    moveend: () => {
      const b = map.getBounds()
      const center = map.getCenter()
      onBoundsChange({
        minLat: b.getSouth(),
        minLng: b.getWest(),
        maxLat: b.getNorth(),
        maxLng: b.getEast(),
        centerLat: center.lat,
        centerLng: center.lng,
      })
    },
    zoomend: () => {
      const b = map.getBounds()
      const center = map.getCenter()
      onBoundsChange({
        minLat: b.getSouth(),
        minLng: b.getWest(),
        maxLat: b.getNorth(),
        maxLng: b.getEast(),
        centerLat: center.lat,
        centerLng: center.lng,
      })
    },
  })
  return null
}

export const MapView: React.FC = () => {
  const [showTripRoutes, setShowTripRoutes] = useState(true)
  const [showMarkedLocations, setShowMarkedLocations] = useState(true)
  const [selectedCategory, setSelectedCategory] = useState<string>('')
  const [isAddingLocation, setIsAddingLocation] = useState(false)
  const [newLocationData, setNewLocationData] = useState<{
    name: string
    category: 'fishing' | 'marina' | 'anchorage' | 'hazard' | 'other'
    notes: string
    latitude: number | null
    longitude: number | null
  }>({
    name: '',
    category: 'other',
    notes: '',
    latitude: null,
    longitude: null,
  })
  const [selectedLocation, setSelectedLocation] = useState<MarkedLocation | null>(null)

  const mapRef = useRef<any>(null)

  // Nautical layers
  const { enabledTileLayers } = useNauticalLayers()
  const [mapBounds, setMapBounds] = useState<MapBounds | null>(null)
  const nauticalData = useNauticalData(mapBounds)

  const { isEnabled: isProviderEnabled } = useNauticalSettings()

  // Per-layer visibility toggles (separate from settings enabled - these control map visibility)
  const [hiddenLayers, setHiddenLayers] = useState<Set<string>>(new Set())

  const toggleLayerVisibility = useCallback((id: string) => {
    setHiddenLayers(prev => {
      const next = new Set(prev)
      if (next.has(id)) {
        next.delete(id)
      } else {
        next.add(id)
      }
      return next
    })
  }, [])

  const isLayerVisible = useCallback((id: string) => {
    return isProviderEnabled(id) && !hiddenLayers.has(id)
  }, [isProviderEnabled, hiddenLayers])

  // Get list of currently enabled providers for showing controls
  const enabledProviders = nauticalProviders.filter(p => isProviderEnabled(p.id))
  
  // Data fetching
  const { data: trips = [], isLoading: tripsLoading } = useTrips()
  const { data: locations = [], isLoading: locationsLoading } = useMarkedLocations(
    selectedCategory ? { category: selectedCategory } : undefined
  )
  
  // Mutations
  const createLocationMutation = useCreateMarkedLocation()
  const deleteLocationMutation = useDeleteMarkedLocation()
  
  // Calculate map center from trips or use default
  const mapCenter: LatLngTuple = React.useMemo(() => {
    if (trips.length > 0) {
      const allPoints = trips.flatMap(trip => trip.gpsPoints)
      if (allPoints.length > 0) {
        const avgLat = allPoints.reduce((sum, point) => sum + point.latitude, 0) / allPoints.length
        const avgLng = allPoints.reduce((sum, point) => sum + point.longitude, 0) / allPoints.length
        return [avgLat, avgLng]
      }
    }
    return [37.7749, -122.4194] // Default to San Francisco
  }, [trips])
  
  const handleMapClick = useCallback((lat: number, lng: number) => {
    console.log('Map clicked:', { lat, lng, isAddingLocation })
    if (isAddingLocation) {
      setNewLocationData(prev => ({
        ...prev,
        latitude: lat,
        longitude: lng,
      }))
    }
  }, [isAddingLocation])
  
  const handleCreateLocation = async () => {
    console.log('handleCreateLocation called with:', newLocationData)

    if (!newLocationData.name) {
      console.log('Validation failed: no name')
      alert('Please enter a location name')
      return
    }
    if (newLocationData.latitude === null || newLocationData.longitude === null) {
      console.log('Validation failed: no coordinates')
      alert('Please click on the map to set coordinates')
      return
    }

    console.log('Validation passed, calling mutation...')
    try {
      const result = await createLocationMutation.mutateAsync({
        name: newLocationData.name,
        latitude: newLocationData.latitude,
        longitude: newLocationData.longitude,
        category: newLocationData.category,
        notes: newLocationData.notes || undefined,
      })
      console.log('Location created successfully:', result)

      // Reset form
      setNewLocationData({
        name: '',
        category: 'other',
        notes: '',
        latitude: null,
        longitude: null,
      })
      setIsAddingLocation(false)
    } catch (error) {
      console.error('Failed to create location:', error)
      alert('Failed to save location. Please try again.')
    }
  }
  
  const handleDeleteLocation = async (id: string) => {
    if (window.confirm('Are you sure you want to delete this location?')) {
      try {
        await deleteLocationMutation.mutateAsync(id)
        setSelectedLocation(null)
      } catch (error) {
        console.error('Failed to delete location:', error)
      }
    }
  }
  
  const renderTripRoutes = () => {
    if (!showTripRoutes) return null
    
    return trips.map((trip: Trip) => {
      if (trip.gpsPoints.length < 2) return null
      
      const positions: LatLngTuple[] = trip.gpsPoints.map(point => [point.latitude, point.longitude])
      const startPoint = positions[0]
      const endPoint = positions[positions.length - 1]
      
      return (
        <React.Fragment key={trip.id}>
          {/* Trip route line */}
          <Polyline
            positions={positions}
            color="#FF9966"
            weight={3}
            opacity={0.7}
          />
          
          {/* Start marker */}
          <Marker position={startPoint}>
            <Popup>
              <div>
                <strong>Trip Start</strong><br />
                {new Date(trip.startTime).toLocaleString()}<br />
                Boat: {trip.boatId}
              </div>
            </Popup>
          </Marker>
          
          {/* End marker */}
          <Marker position={endPoint}>
            <Popup>
              <div>
                <strong>Trip End</strong><br />
                {new Date(trip.endTime).toLocaleString()}<br />
                Duration: {Math.round((trip.statistics?.durationSeconds || 0) / 60)} minutes<br />
                Distance: {((trip.statistics?.distanceMeters || 0) / 1000).toFixed(2)} km
              </div>
            </Popup>
          </Marker>
          
          {/* Stop points */}
          {(trip.statistics?.stopPoints || []).map((stopPoint, index) => (
            <Marker
              key={`${trip.id}-stop-${index}`}
              position={[stopPoint.latitude, stopPoint.longitude]}
              icon={new DivIcon({
                html: `<div style="
                  background-color: #FFFF66;
                  width: 16px;
                  height: 16px;
                  border-radius: 50%;
                  border: 2px solid #000;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  font-weight: bold;
                  font-size: 8px;
                  color: #000;
                ">S</div>`,
                className: 'stop-marker',
                iconSize: [16, 16],
                iconAnchor: [8, 8],
              })}
            >
              <Popup>
                <div>
                  <strong>Stop Point</strong><br />
                  Duration: {Math.round(stopPoint.durationSeconds / 60)} minutes<br />
                  From: {new Date(stopPoint.startTime).toLocaleString()}<br />
                  To: {new Date(stopPoint.endTime).toLocaleString()}
                </div>
              </Popup>
            </Marker>
          ))}
        </React.Fragment>
      )
    })
  }
  
  const renderMarkedLocations = () => {
    if (!showMarkedLocations) return null
    
    return locations.map((location: MarkedLocation) => (
      <Marker
        key={location.id}
        position={[location.latitude, location.longitude]}
        icon={createCategoryIcon(location.category)}
        eventHandlers={{
          click: () => setSelectedLocation(location),
        }}
      >
        <Popup>
          <div>
            <strong>{location.name}</strong><br />
            Category: {location.category}<br />
            {location.notes && (
              <>
                Notes: {location.notes}<br />
              </>
            )}
            {location.tags.length > 0 && (
              <>
                Tags: {location.tags.join(', ')}<br />
              </>
            )}
            <small>Created: {new Date(location.createdAt).toLocaleDateString()}</small>
          </div>
        </Popup>
      </Marker>
    ))
  }
  
  return (
    <MapPageContainer>
      <LCARSHeader>Navigation Chart</LCARSHeader>
      
      <MapControlsContainer>
        <FilterControls>
          <label>Display:</label>
          <LCARSButton
            variant={showTripRoutes ? 'primary' : 'secondary'}
            size="sm"
            onClick={() => setShowTripRoutes(!showTripRoutes)}
          >
            Trip Routes
          </LCARSButton>
          <LCARSButton
            variant={showMarkedLocations ? 'primary' : 'secondary'}
            size="sm"
            onClick={() => setShowMarkedLocations(!showMarkedLocations)}
          >
            Locations
          </LCARSButton>

          {enabledProviders.length > 0 && (
            <>
              <label>Overlays:</label>
              {enabledProviders.map(provider => (
                <LCARSButton
                  key={provider.id}
                  variant={!hiddenLayers.has(provider.id) ? 'primary' : 'secondary'}
                  size="sm"
                  onClick={() => toggleLayerVisibility(provider.id)}
                >
                  {provider.name}
                </LCARSButton>
              ))}
            </>
          )}

          <label>Category:</label>
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
          >
            <option value="">All Categories</option>
            <option value="fishing">Fishing</option>
            <option value="marina">Marina</option>
            <option value="anchorage">Anchorage</option>
            <option value="hazard">Hazard</option>
            <option value="other">Other</option>
          </select>
        </FilterControls>
      </MapControlsContainer>
      
      <MapContainer_Styled>
        <MapPanel title="Chart Display" padding="none">
          <MapContainer
            center={mapCenter}
            zoom={10}
            style={{ height: '100%', width: '100%' }}
            ref={mapRef}
          >
            <TileLayer
              attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />

            {/* Nautical tile overlays */}
            {enabledTileLayers.filter(layer => !hiddenLayers.has(layer.id)).map(layer =>
              layer.type === 'wms' ? (
                <WMSTileLayer
                  key={layer.id}
                  url={layer.url}
                  layers={layer.wmsLayers || ''}
                  format={layer.wmsFormat || 'image/png'}
                  transparent={true}
                  opacity={layer.opacity}
                  attribution={layer.attribution}
                />
              ) : (
                <TileLayer
                  key={layer.id}
                  url={layer.url}
                  opacity={layer.opacity}
                  maxZoom={layer.maxZoom}
                  attribution={layer.attribution}
                />
              )
            )}

            <MapBoundsTracker onBoundsChange={setMapBounds} />

            <MapClickHandler onMapClick={handleMapClick} />

            {renderTripRoutes()}
            {renderMarkedLocations()}

            {/* AIS Vessels */}
            {isLayerVisible('aisstream') && nauticalData.vessels.map(vessel => (
              <Marker
                key={`ais-${vessel.mmsi}`}
                position={[vessel.latitude, vessel.longitude]}
                icon={new DivIcon({
                  html: `<div style="
                    width: 0; height: 0;
                    border-left: 6px solid transparent;
                    border-right: 6px solid transparent;
                    border-bottom: 14px solid #00FFFF;
                    transform: rotate(${vessel.heading}deg);
                  "></div>`,
                  className: 'vessel-marker',
                  iconSize: [12, 14],
                  iconAnchor: [6, 7],
                })}
              >
                <Popup>
                  <div>
                    <strong>{vessel.name}</strong><br />
                    MMSI: {vessel.mmsi}<br />
                    Speed: {vessel.speed.toFixed(1)} kts<br />
                    Heading: {vessel.heading}°
                  </div>
                </Popup>
              </Marker>
            ))}

            {/* MarineTraffic Vessels */}
            {isLayerVisible('marinetraffic') && nauticalData.marineTrafficVessels.map(vessel => (
              <Marker
                key={`mt-${vessel.mmsi}`}
                position={[vessel.latitude, vessel.longitude]}
                icon={new DivIcon({
                  html: `<div style="
                    width: 0; height: 0;
                    border-left: 6px solid transparent;
                    border-right: 6px solid transparent;
                    border-bottom: 14px solid #FF00FF;
                    transform: rotate(${vessel.heading}deg);
                  "></div>`,
                  className: 'vessel-marker',
                  iconSize: [12, 14],
                  iconAnchor: [6, 7],
                })}
              >
                <Popup>
                  <div>
                    <strong>{vessel.name}</strong><br />
                    MMSI: {vessel.mmsi}<br />
                    Speed: {vessel.speed.toFixed(1)} kts<br />
                    Destination: {vessel.destination}
                  </div>
                </Popup>
              </Marker>
            ))}

            {/* Tide Stations */}
            {isLayerVisible('noaa-coops') && nauticalData.tideStations.map(station => (
              <Marker
                key={`tide-${station.id}`}
                position={[station.latitude, station.longitude]}
                icon={new DivIcon({
                  html: `<div style="
                    background: #0066FF;
                    color: white;
                    width: 22px;
                    height: 22px;
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 10px;
                    font-weight: bold;
                    border: 2px solid white;
                  ">T</div>`,
                  className: 'tide-marker',
                  iconSize: [22, 22],
                  iconAnchor: [11, 11],
                })}
              >
                <Popup>
                  <div>
                    <strong>{station.name}</strong><br />
                    Station: {station.id}<br />
                    {station.predictions.length > 0 && (
                      <>
                        <strong>Predictions:</strong><br />
                        {station.predictions.slice(0, 6).map((p, i) => (
                          <span key={i}>
                            {p.type === 'H' ? '▲ High' : '▼ Low'}: {p.value.toFixed(1)} ft at {p.time}<br />
                          </span>
                        ))}
                      </>
                    )}
                  </div>
                </Popup>
              </Marker>
            ))}
            
            {/* New location marker */}
            {isAddingLocation && newLocationData.latitude !== null && newLocationData.longitude !== null && (
              <Marker position={[newLocationData.latitude, newLocationData.longitude]}>
                <Popup>
                  <div>
                    <strong>New Location</strong><br />
                    Click "Save Location" to confirm
                  </div>
                </Popup>
              </Marker>
            )}
          </MapContainer>

        {/* Weather overlay */}
        {(isLayerVisible('open-meteo') || isLayerVisible('stormglass')) && nauticalData.weather && (
          <div style={{
            position: 'absolute',
            bottom: '10px',
            left: '10px',
            background: 'rgba(0,0,0,0.85)',
            color: '#99CCFF',
            padding: '8px 12px',
            borderRadius: '4px',
            border: '1px solid #336699',
            fontSize: '12px',
            fontFamily: 'monospace',
            zIndex: 1000,
            lineHeight: '1.5',
          }}>
            <div style={{ fontWeight: 'bold', marginBottom: '4px', color: '#FFCC99' }}>MARINE WEATHER</div>
            {nauticalData.weather.waveHeight != null && <div>Waves: {nauticalData.weather.waveHeight}m</div>}
            {nauticalData.weather.windSpeed != null && <div>Wind: {nauticalData.weather.windSpeed} km/h</div>}
            {nauticalData.weather.swellHeight != null && <div>Swell: {nauticalData.weather.swellHeight}m</div>}
            {nauticalData.weather.temperature != null && <div>Temp: {nauticalData.weather.temperature}°C</div>}
            {isLayerVisible('stormglass') && nauticalData.stormglassWeather && (
              <>
                <div style={{ fontWeight: 'bold', marginTop: '4px', color: '#CC99CC' }}>STORMGLASS</div>
                {nauticalData.stormglassWeather.waveHeight != null && <div>Waves: {nauticalData.stormglassWeather.waveHeight}m</div>}
                {nauticalData.stormglassWeather.visibility != null && <div>Vis: {nauticalData.stormglassWeather.visibility}km</div>}
                {nauticalData.stormglassWeather.waterTemperature != null && <div>Water: {nauticalData.stormglassWeather.waterTemperature}°C</div>}
              </>
            )}
          </div>
        )}
        </MapPanel>

        <SidePanel title="Location Manager" variant="secondary">
          {!isAddingLocation ? (
            <>
              <ReadOnlyGuard>
                <LCARSButton
                  onClick={() => setIsAddingLocation(true)}
                  disabled={createLocationMutation.isPending}
                >
                  Add New Location
                </LCARSButton>
              </ReadOnlyGuard>
              
              {selectedLocation && (
                <div>
                  <h4 style={{ color: '#FF9966', marginBottom: '8px' }}>Selected Location</h4>
                  <div style={{ 
                    padding: '12px', 
                    backgroundColor: '#222222', 
                    borderRadius: '4px',
                    border: '1px solid #333333'
                  }}>
                    <strong>{selectedLocation.name}</strong><br />
                    Category: {selectedLocation.category}<br />
                    Coordinates: {selectedLocation.latitude.toFixed(6)}, {selectedLocation.longitude.toFixed(6)}<br />
                    {selectedLocation.notes && (
                      <>
                        Notes: {selectedLocation.notes}<br />
                      </>
                    )}
                    {selectedLocation.tags.length > 0 && (
                      <>
                        Tags: {selectedLocation.tags.join(', ')}<br />
                      </>
                    )}
                    <div style={{ marginTop: '8px' }}>
                      <ReadOnlyGuard>
                        <LCARSButton
                          size="sm"
                          variant="accent"
                          onClick={() => handleDeleteLocation(selectedLocation.id)}
                          disabled={deleteLocationMutation.isPending}
                        >
                          Delete
                        </LCARSButton>
                      </ReadOnlyGuard>
                    </div>
                  </div>
                </div>
              )}
              
              <LocationList>
                {locations.map((location) => (
                  <LocationItem key={location.id}>
                    <div className="location-name">{location.name}</div>
                    <div className="location-category">{location.category}</div>
                    {location.notes && (
                      <div className="location-notes">{location.notes}</div>
                    )}
                    <div className="location-actions">
                      <LCARSButton
                        size="sm"
                        onClick={() => {
                          setSelectedLocation(location)
                          if (mapRef.current) {
                            mapRef.current.setView([location.latitude, location.longitude], 15)
                          }
                        }}
                      >
                        View
                      </LCARSButton>
                    </div>
                  </LocationItem>
                ))}
              </LocationList>
            </>
          ) : (
            <NewLocationForm>
              <h3>Add New Location</h3>
              <p>Click on the map to set coordinates, then fill in the details below.</p>
              
              <input
                type="text"
                placeholder="Location Name"
                value={newLocationData.name}
                onChange={(e) => setNewLocationData(prev => ({ ...prev, name: e.target.value }))}
              />
              
              <select
                value={newLocationData.category}
                onChange={(e) => setNewLocationData(prev => ({ 
                  ...prev, 
                  category: e.target.value as any 
                }))}
              >
                <option value="fishing">Fishing Spot</option>
                <option value="marina">Marina</option>
                <option value="anchorage">Anchorage</option>
                <option value="hazard">Hazard</option>
                <option value="other">Other</option>
              </select>
              
              <textarea
                placeholder="Notes (optional)"
                value={newLocationData.notes}
                onChange={(e) => setNewLocationData(prev => ({ ...prev, notes: e.target.value }))}
              />
              
              {newLocationData.latitude !== null && newLocationData.longitude !== null && (
                <div>
                  <h4 style={{ color: '#FF9966', marginBottom: '8px' }}>Coordinates</h4>
                  <div style={{
                    padding: '12px',
                    backgroundColor: '#222222',
                    borderRadius: '4px',
                    border: '1px solid #333333',
                    fontFamily: 'monospace'
                  }}>
                    Lat: {newLocationData.latitude.toFixed(6)}<br />
                    Lng: {newLocationData.longitude.toFixed(6)}
                  </div>
                </div>
              )}
              
              {/* Debug info - shows why button might be disabled */}
              <div style={{ fontSize: '12px', color: '#999', marginBottom: '8px' }}>
                Status: {!newLocationData.name ? '❌ Need name' : '✓ Name'} |
                {newLocationData.latitude === null ? ' ❌ Need coords (click map)' : ' ✓ Coords'} |
                {createLocationMutation.isPending ? ' ⏳ Saving...' : ' ✓ Ready'}
              </div>

              <div style={{ display: 'flex', gap: '8px' }}>
                <ReadOnlyGuard>
                  <LCARSButton
                    onClick={handleCreateLocation}
                    disabled={!newLocationData.name || newLocationData.latitude === null || newLocationData.longitude === null || createLocationMutation.isPending}
                  >
                    Save Location
                  </LCARSButton>
                </ReadOnlyGuard>
                <LCARSButton
                  variant="secondary"
                  onClick={() => {
                    setIsAddingLocation(false)
                    setNewLocationData({
                      name: '',
                      category: 'other',
                      notes: '',
                      latitude: null,
                      longitude: null,
                    })
                  }}
                >
                  Cancel
                </LCARSButton>
              </div>
            </NewLocationForm>
          )}
        </SidePanel>
      </MapContainer_Styled>
      
      {(tripsLoading || locationsLoading) && (
        <LCARSDataDisplay 
          label="System Status" 
          value="Loading chart data..." 
          valueColor="anakiwa"
        />
      )}
    </MapPageContainer>
  )
}