import React from 'react'
import { useParams, Link } from 'react-router-dom'
import styled from 'styled-components'
import { MapContainer, TileLayer, Polyline, Marker, Popup } from 'react-leaflet'
import { Icon, LatLngTuple } from 'leaflet'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { LCARSHeader } from '../components/lcars/LCARSHeader'

import { useTrip } from '../hooks/useTrips'
import { useBoats } from '../hooks/useBoats'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'
import { GPSPoint, StopPoint } from '../types/api'

// Import Leaflet CSS
import 'leaflet/dist/leaflet.css'

const TripDetailContainer = styled.div`
  padding: ${props => props.theme.spacing.lg};
  max-width: 1400px;
  margin: 0 auto;
`

const BackButton = styled(LCARSButton)`
  margin-bottom: ${props => props.theme.spacing.lg};
`

const ContentGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: ${props => props.theme.spacing.lg};
  margin-bottom: ${props => props.theme.spacing.lg};
  
  @media (max-width: ${props => props.theme.breakpoints.lg}) {
    grid-template-columns: 1fr;
  }
`

const MapPanel = styled(LCARSPanel)`
  grid-column: 1 / -1;
  margin-bottom: ${props => props.theme.spacing.lg};
`

const MapContainer_Styled = styled(MapContainer)`
  height: 400px;
  width: 100%;
  border-radius: ${props => props.theme.borderRadius.md};
  
  .leaflet-control-container {
    .leaflet-top.leaflet-left {
      .leaflet-control-zoom {
        background-color: ${props => props.theme.colors.surface.dark};
        border: 1px solid ${props => props.theme.colors.primary.neonCarrot};
        border-radius: ${props => props.theme.borderRadius.sm};

        a {
          background-color: ${props => props.theme.colors.surface.medium};
          color: ${props => props.theme.colors.text.primary};
          border: none;

          &:hover {
            background-color: ${props => props.theme.colors.primary.neonCarrot};
            color: ${props => props.theme.colors.text.inverse};
          }
        }
      }
    }
  }
`

const StatsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${props => props.theme.spacing.md};
`

const StatCard = styled.div`
  text-align: center;
  background-color: ${props => props.theme.colors.surface.medium};
  border: 1px solid ${props => props.theme.colors.primary.anakiwa};
  border-radius: ${props => props.theme.borderRadius.md};
  padding: ${props => props.theme.spacing.md};
`

const StatValue = styled.div`
  font-size: ${props => props.theme.typography.fontSize.xxl};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  color: ${props => props.theme.colors.primary.anakiwa};
  font-family: ${props => props.theme.typography.fontFamily.monospace};
  margin-bottom: ${props => props.theme.spacing.xs};
`

const StatLabel = styled.div`
  font-size: ${props => props.theme.typography.fontSize.sm};
  color: ${props => props.theme.colors.text.secondary};
  text-transform: uppercase;
  letter-spacing: 1px;
`

const InfoGrid = styled.div`
  display: grid;
  gap: ${props => props.theme.spacing.sm};
`

const InfoRow = styled.div`
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: ${props => props.theme.spacing.md};
  padding: ${props => props.theme.spacing.sm} 0;
  border-bottom: 1px solid ${props => props.theme.colors.surface.light};
  
  &:last-child {
    border-bottom: none;
  }
`

const InfoLabel = styled.div`
  font-size: ${props => props.theme.typography.fontSize.sm};
  color: ${props => props.theme.colors.text.secondary};
  text-transform: uppercase;
  letter-spacing: 1px;
  font-weight: ${props => props.theme.typography.fontWeight.bold};
`

const InfoValue = styled.div`
  font-size: ${props => props.theme.typography.fontSize.md};
  color: ${props => props.theme.colors.text.primary};
  font-family: ${props => props.theme.typography.fontFamily.monospace};
`

const ManualDataGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${props => props.theme.spacing.md};
`

const ManualDataCard = styled.div`
  background-color: ${props => props.theme.colors.surface.medium};
  border: 1px solid ${props => props.theme.colors.primary.lilac};
  border-radius: ${props => props.theme.borderRadius.md};
  padding: ${props => props.theme.spacing.md};
  text-align: center;
`

const ManualDataValue = styled.div`
  font-size: ${props => props.theme.typography.fontSize.lg};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  color: ${props => props.theme.colors.primary.lilac};
  font-family: ${props => props.theme.typography.fontFamily.monospace};
  margin-bottom: ${props => props.theme.spacing.xs};
`

const ManualDataLabel = styled.div`
  font-size: ${props => props.theme.typography.fontSize.sm};
  color: ${props => props.theme.colors.text.secondary};
  text-transform: uppercase;
  letter-spacing: 1px;
`

const StopPointsList = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.sm};
`

const StopPointCard = styled.div`
  background-color: ${props => props.theme.colors.surface.medium};
  border: 1px solid ${props => props.theme.colors.primary.anakiwa};
  border-radius: ${props => props.theme.borderRadius.md};
  padding: ${props => props.theme.spacing.md};
`

const StopPointHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${props => props.theme.spacing.sm};
`

const StopPointTitle = styled.div`
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  color: ${props => props.theme.colors.primary.anakiwa};
  text-transform: uppercase;
  letter-spacing: 1px;
`

const StopPointDuration = styled.div`
  font-family: ${props => props.theme.typography.fontFamily.monospace};
  color: ${props => props.theme.colors.text.secondary};
`

const StopPointCoords = styled.div`
  font-family: ${props => props.theme.typography.fontFamily.monospace};
  font-size: ${props => props.theme.typography.fontSize.sm};
  color: ${props => props.theme.colors.text.muted};
`

const LoadingState = styled.div`
  text-align: center;
  padding: ${props => props.theme.spacing.xxl};
  color: ${props => props.theme.colors.primary.neonCarrot};
  font-size: ${props => props.theme.typography.fontSize.lg};
  text-transform: uppercase;
  letter-spacing: 2px;
`

const ErrorState = styled.div`
  text-align: center;
  padding: ${props => props.theme.spacing.xxl};
  color: ${props => props.theme.colors.status.error};
  font-size: ${props => props.theme.typography.fontSize.lg};
`

const ActionButtons = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.md};
  margin-top: ${props => props.theme.spacing.lg};
`

const ManualDataPanel = styled(LCARSPanel)`
  margin-bottom: ${props => props.theme.spacing.lg};
`

const StopPointsPanel = styled(LCARSPanel)`
  margin-bottom: ${props => props.theme.spacing.lg};
`

// Custom Leaflet icons
const startIcon = new Icon({
  iconUrl: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMTIiIGN5PSIxMiIgcj0iMTAiIGZpbGw9IiM2NkZGNjYiLz4KPHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMTIiIGN5PSIxMiIgcj0iNiIgZmlsbD0iIzAwMDAwMCIvPgo8L3N2Zz4KPC9zdmc+',
  iconSize: [24, 24],
  iconAnchor: [12, 12],
})

const endIcon = new Icon({
  iconUrl: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMTIiIGN5PSIxMiIgcj0iMTAiIGZpbGw9IiNGRjY2NjYiLz4KPHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMTIiIGN5PSIxMiIgcj0iNiIgZmlsbD0iIzAwMDAwMCIvPgo8L3N2Zz4KPC9zdmc+',
  iconSize: [24, 24],
  iconAnchor: [12, 12],
})

const stopIcon = new Icon({
  iconUrl: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMTIiIGN5PSIxMiIgcj0iMTAiIGZpbGw9IiNGRkZGNjYiLz4KPHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMTIiIGN5PSIxMiIgcj0iNiIgZmlsbD0iIzAwMDAwMCIvPgo8L3N2Zz4KPC9zdmc+',
  iconSize: [20, 20],
  iconAnchor: [10, 10],
})

export const TripDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const { data: trip, isLoading, error } = useTrip(id!)
  const { data: boats } = useBoats()

  const formatDuration = (seconds: number): string => {
    const hours = Math.floor(seconds / 3600)
    const minutes = Math.floor((seconds % 3600) / 60)
    return `${hours}h ${minutes}m`
  }

  const formatDistance = (meters: number): string => {
    const nauticalMiles = meters * 0.000539957
    return `${nauticalMiles.toFixed(1)} nm`
  }

  const formatSpeed = (knots: number): string => {
    return `${knots.toFixed(1)} kts`
  }

  const formatDateTime = (dateString: string): string => {
    return new Date(dateString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  }

  const formatCoordinates = (lat: number, lng: number): string => {
    const latDir = lat >= 0 ? 'N' : 'S'
    const lngDir = lng >= 0 ? 'E' : 'W'
    return `${Math.abs(lat).toFixed(6)}°${latDir}, ${Math.abs(lng).toFixed(6)}°${lngDir}`
  }

  const getBoatName = (boatId: string): string => {
    const boat = boats?.find(b => b.id === boatId)
    return boat?.name || 'Unknown Boat'
  }

  const getRouteCoordinates = (gpsPoints: GPSPoint[]): LatLngTuple[] => {
    return gpsPoints.map(point => [point.latitude, point.longitude] as LatLngTuple)
  }

  const getMapCenter = (gpsPoints: GPSPoint[]): LatLngTuple => {
    if (gpsPoints.length === 0) return [0, 0]
    
    const avgLat = gpsPoints.reduce((sum, point) => sum + point.latitude, 0) / gpsPoints.length
    const avgLng = gpsPoints.reduce((sum, point) => sum + point.longitude, 0) / gpsPoints.length
    
    return [avgLat, avgLng]
  }

  if (isLoading) {
    return (
      <TripDetailContainer>
        <LoadingState>Loading Trip Data...</LoadingState>
      </TripDetailContainer>
    )
  }

  if (error || !trip) {
    return (
      <TripDetailContainer>
        <ErrorState>
          {error ? `Error loading trip: ${error.message}` : 'Trip not found'}
        </ErrorState>
      </TripDetailContainer>
    )
  }

  const routeCoordinates = getRouteCoordinates(trip.gpsPoints)
  const mapCenter = getMapCenter(trip.gpsPoints)
  const startPoint = trip.gpsPoints[0]
  const endPoint = trip.gpsPoints[trip.gpsPoints.length - 1]

  return (
    <TripDetailContainer>
      <BackButton as={Link} to="/trips" variant="secondary" size="sm">
        ← Back to Trip Log
      </BackButton>
      
      <LCARSHeader>
        Trip Analysis - {getBoatName(trip.boatId)} - {formatDateTime(trip.startTime)}
      </LCARSHeader>

      {routeCoordinates.length > 0 && (
        <MapPanel title="Navigation Route" variant="accent">
          <MapContainer_Styled
            center={mapCenter}
            zoom={13}
            scrollWheelZoom={true}
          >
            <TileLayer
              attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            
            {/* Route line */}
            <Polyline
              positions={routeCoordinates}
              color="#FF9966"
              weight={3}
              opacity={0.8}
            />
            
            {/* Start marker */}
            {startPoint && (
              <Marker
                position={[startPoint.latitude, startPoint.longitude]}
                icon={startIcon}
              >
                <Popup>
                  <strong>Trip Start</strong><br />
                  {formatDateTime(trip.startTime)}<br />
                  {formatCoordinates(startPoint.latitude, startPoint.longitude)}
                </Popup>
              </Marker>
            )}
            
            {/* End marker */}
            {endPoint && (
              <Marker
                position={[endPoint.latitude, endPoint.longitude]}
                icon={endIcon}
              >
                <Popup>
                  <strong>Trip End</strong><br />
                  {formatDateTime(trip.endTime)}<br />
                  {formatCoordinates(endPoint.latitude, endPoint.longitude)}
                </Popup>
              </Marker>
            )}
            
            {/* Stop points */}
            {(trip.statistics?.stopPoints || []).map((stop: StopPoint, index: number) => (
              <Marker
                key={index}
                position={[stop.latitude, stop.longitude]}
                icon={stopIcon}
              >
                <Popup>
                  <strong>Stop Point {index + 1}</strong><br />
                  Duration: {formatDuration(stop.durationSeconds)}<br />
                  {formatCoordinates(stop.latitude, stop.longitude)}
                </Popup>
              </Marker>
            ))}
          </MapContainer_Styled>
        </MapPanel>
      )}

      <ContentGrid>
        <LCARSPanel title="Trip Statistics" variant="primary">
          <StatsGrid>
            <StatCard>
              <StatValue>{formatDuration(trip.statistics?.durationSeconds || 0)}</StatValue>
              <StatLabel>Duration</StatLabel>
            </StatCard>
            <StatCard>
              <StatValue>{formatDistance(trip.statistics?.distanceMeters || 0)}</StatValue>
              <StatLabel>Distance</StatLabel>
            </StatCard>
            <StatCard>
              <StatValue>{formatSpeed(trip.statistics?.averageSpeedKnots || 0)}</StatValue>
              <StatLabel>Avg Speed</StatLabel>
            </StatCard>
            <StatCard>
              <StatValue>{formatSpeed(trip.statistics?.maxSpeedKnots || 0)}</StatValue>
              <StatLabel>Max Speed</StatLabel>
            </StatCard>
            <StatCard>
              <StatValue>{trip.statistics?.stopPoints?.length || 0}</StatValue>
              <StatLabel>Stop Points</StatLabel>
            </StatCard>
            <StatCard>
              <StatValue>{trip.gpsPoints.length}</StatValue>
              <StatLabel>GPS Points</StatLabel>
            </StatCard>
          </StatsGrid>
        </LCARSPanel>

        <LCARSPanel title="Trip Information" variant="secondary">
          <InfoGrid>
            <InfoRow>
              <InfoLabel>Vessel</InfoLabel>
              <InfoValue>{getBoatName(trip.boatId)}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Start Time</InfoLabel>
              <InfoValue>{formatDateTime(trip.startTime)}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>End Time</InfoLabel>
              <InfoValue>{formatDateTime(trip.endTime)}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Water Type</InfoLabel>
              <InfoValue>{trip.waterType.toUpperCase()}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Role</InfoLabel>
              <InfoValue>{trip.role.toUpperCase()}</InfoValue>
            </InfoRow>
            {startPoint && (
              <InfoRow>
                <InfoLabel>Start Position</InfoLabel>
                <InfoValue>{formatCoordinates(startPoint.latitude, startPoint.longitude)}</InfoValue>
              </InfoRow>
            )}
            {endPoint && (
              <InfoRow>
                <InfoLabel>End Position</InfoLabel>
                <InfoValue>{formatCoordinates(endPoint.latitude, endPoint.longitude)}</InfoValue>
              </InfoRow>
            )}
          </InfoGrid>
        </LCARSPanel>
      </ContentGrid>

      {trip.manualData && (
        <ManualDataPanel title="Manual Data Entry" variant="accent">
          <ManualDataGrid>
            {trip.manualData.engineHours !== undefined && (
              <ManualDataCard>
                <ManualDataValue>{trip.manualData.engineHours}</ManualDataValue>
                <ManualDataLabel>Engine Hours</ManualDataLabel>
              </ManualDataCard>
            )}
            {trip.manualData.fuelConsumed !== undefined && (
              <ManualDataCard>
                <ManualDataValue>{trip.manualData.fuelConsumed}</ManualDataValue>
                <ManualDataLabel>Fuel Consumed</ManualDataLabel>
              </ManualDataCard>
            )}
            {trip.manualData.numberOfPassengers !== undefined && (
              <ManualDataCard>
                <ManualDataValue>{trip.manualData.numberOfPassengers}</ManualDataValue>
                <ManualDataLabel>Passengers</ManualDataLabel>
              </ManualDataCard>
            )}
            {trip.manualData.weatherConditions && (
              <ManualDataCard>
                <ManualDataValue>{trip.manualData.weatherConditions}</ManualDataValue>
                <ManualDataLabel>Weather</ManualDataLabel>
              </ManualDataCard>
            )}
            {trip.manualData.destination && (
              <ManualDataCard>
                <ManualDataValue>{trip.manualData.destination}</ManualDataValue>
                <ManualDataLabel>Destination</ManualDataLabel>
              </ManualDataCard>
            )}
          </ManualDataGrid>
        </ManualDataPanel>
      )}

      {(trip.statistics?.stopPoints || []).length > 0 && (
        <StopPointsPanel title="Stop Points Analysis" variant="primary">
          <StopPointsList>
            {(trip.statistics?.stopPoints || []).map((stop: StopPoint, index: number) => (
              <StopPointCard key={index}>
                <StopPointHeader>
                  <StopPointTitle>Stop Point {index + 1}</StopPointTitle>
                  <StopPointDuration>{formatDuration(stop.durationSeconds)}</StopPointDuration>
                </StopPointHeader>
                <StopPointCoords>
                  {formatCoordinates(stop.latitude, stop.longitude)}
                </StopPointCoords>
                <div style={{ fontSize: '0.8rem', color: '#999', marginTop: '0.5rem' }}>
                  {formatDateTime(stop.startTime)} - {formatDateTime(stop.endTime)}
                </div>
              </StopPointCard>
            ))}
          </StopPointsList>
        </StopPointsPanel>
      )}

      <ActionButtons>
        <ReadOnlyGuard>
          <Link to={`/trips/${trip.id}/edit`} style={{ textDecoration: 'none' }}>
            <LCARSButton variant="primary">
              Edit Trip Data
            </LCARSButton>
          </Link>
        </ReadOnlyGuard>
        <LCARSButton variant="secondary">
          Export Data
        </LCARSButton>
      </ActionButtons>
    </TripDetailContainer>
  )
}