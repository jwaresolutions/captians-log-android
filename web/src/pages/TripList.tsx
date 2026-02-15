import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import styled from 'styled-components'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { LCARSHeader } from '../components/lcars/LCARSHeader'

import { useTrips } from '../hooks/useTrips'
import { useBoats } from '../hooks/useBoats'
import { Trip } from '../types/api'

const TripListContainer = styled.div`
  padding: ${props => props.theme.spacing.lg};
  max-width: 1200px;
  margin: 0 auto;
`

const HeaderRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${props => props.theme.spacing.lg};
`

const FiltersPanel = styled(LCARSPanel)`
  margin-bottom: ${props => props.theme.spacing.lg};
`

const FiltersGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${props => props.theme.spacing.md};
  align-items: end;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`

const FilterGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.xs};
`

const FilterLabel = styled.label`
  font-size: ${props => props.theme.typography.fontSize.sm};
  color: ${props => props.theme.colors.text.secondary};
  text-transform: uppercase;
  letter-spacing: 1px;
`

const FilterSelect = styled.select`
  background-color: ${props => props.theme.colors.surface.medium};
  border: 1px solid ${props => props.theme.colors.primary.neonCarrot};
  border-radius: ${props => props.theme.borderRadius.sm};
  color: ${props => props.theme.colors.text.primary};
  padding: ${props => props.theme.spacing.sm};
  font-family: ${props => props.theme.typography.fontFamily.primary};

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.tanoi};
    box-shadow: 0 0 0 2px rgba(255, 153, 102, 0.2);
  }
`

const FilterInput = styled.input`
  background-color: ${props => props.theme.colors.surface.medium};
  border: 1px solid ${props => props.theme.colors.primary.neonCarrot};
  border-radius: ${props => props.theme.borderRadius.sm};
  color: ${props => props.theme.colors.text.primary};
  padding: ${props => props.theme.spacing.sm};
  font-family: ${props => props.theme.typography.fontFamily.primary};

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.tanoi};
    box-shadow: 0 0 0 2px rgba(255, 153, 102, 0.2);
  }
`

const TripsGrid = styled.div`
  display: grid;
  gap: ${props => props.theme.spacing.md};
`

const TripCard = styled(LCARSPanel)`
  cursor: pointer;
  transition: all ${props => props.theme.animation.normal} ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: ${props => props.theme.shadows.lg};
  }
`

const TripCardContent = styled.div`
  display: grid;
  grid-template-columns: 1fr auto;
  gap: ${props => props.theme.spacing.md};
  align-items: start;
`

const TripInfo = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.sm};
`

const TripTitle = styled.h3`
  margin: 0;
  font-size: ${props => props.theme.typography.fontSize.lg};
  color: ${props => props.theme.colors.primary.neonCarrot};
  text-transform: uppercase;
  letter-spacing: 1px;
`

const TripMeta = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: ${props => props.theme.spacing.sm};
  font-size: ${props => props.theme.typography.fontSize.sm};
  color: ${props => props.theme.colors.text.secondary};

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`

const TripStats = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.xs};
  text-align: right;
`

const StatValue = styled.div`
  font-size: ${props => props.theme.typography.fontSize.lg};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  color: ${props => props.theme.colors.primary.anakiwa};
  font-family: ${props => props.theme.typography.fontFamily.monospace};
`

const StatLabel = styled.div`
  font-size: ${props => props.theme.typography.fontSize.xs};
  color: ${props => props.theme.colors.text.muted};
  text-transform: uppercase;
  letter-spacing: 1px;
`

const EmptyState = styled.div`
  text-align: center;
  padding: ${props => props.theme.spacing.xxl};
  color: ${props => props.theme.colors.text.muted};
  
  .empty-title {
    font-size: ${props => props.theme.typography.fontSize.xl};
    margin-bottom: ${props => props.theme.spacing.md};
    color: ${props => props.theme.colors.primary.neonCarrot};
  }
  
  .empty-message {
    font-size: ${props => props.theme.typography.fontSize.md};
    margin-bottom: ${props => props.theme.spacing.lg};
  }
`

const LoadingState = styled.div`
  text-align: center;
  padding: ${props => props.theme.spacing.xxl};
  color: ${props => props.theme.colors.primary.neonCarrot};
  font-size: ${props => props.theme.typography.fontSize.lg};
  text-transform: uppercase;
  letter-spacing: 2px;
`

interface TripFilters {
  boatId?: string
  startDate?: string
  endDate?: string
}

export const TripList: React.FC = () => {
  const navigate = useNavigate()
  const [filters, setFilters] = useState<TripFilters>({})
  const { data: trips, isLoading, error } = useTrips(filters)
  const { data: boats } = useBoats()

  const handleFilterChange = (key: keyof TripFilters, value: string) => {
    setFilters(prev => ({
      ...prev,
      [key]: value || undefined
    }))
  }

  const clearFilters = () => {
    setFilters({})
  }

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

  const formatDate = (dateString: string): string => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  const getBoatName = (boatId: string): string => {
    const boat = boats?.find(b => b.id === boatId)
    return boat?.name || 'Unknown Boat'
  }

  if (isLoading) {
    return (
      <TripListContainer>
        <LoadingState>Loading Trip Data...</LoadingState>
      </TripListContainer>
    )
  }

  if (error) {
    return (
      <TripListContainer>
        <LCARSPanel variant="accent" title="System Error">
          <div style={{ color: 'red', textAlign: 'center', padding: '2rem' }}>
            Error loading trips: {error.message}
          </div>
        </LCARSPanel>
      </TripListContainer>
    )
  }

  return (
    <TripListContainer>
      <HeaderRow>
        <LCARSHeader>Trip Log Database</LCARSHeader>
        <LCARSButton variant="primary" onClick={() => navigate('/trips/new')}>
          ADD MANUAL TRIP
        </LCARSButton>
      </HeaderRow>

      <FiltersPanel title="Search Parameters" variant="secondary">
        <FiltersGrid>
          <FilterGroup>
            <FilterLabel>Vessel</FilterLabel>
            <FilterSelect
              value={filters.boatId || ''}
              onChange={(e) => handleFilterChange('boatId', e.target.value)}
            >
              <option value="">All Vessels</option>
              {boats?.map(boat => (
                <option key={boat.id} value={boat.id}>
                  {boat.name}
                </option>
              ))}
            </FilterSelect>
          </FilterGroup>
          
          <FilterGroup>
            <FilterLabel>Start Date</FilterLabel>
            <FilterInput
              type="date"
              value={filters.startDate || ''}
              onChange={(e) => handleFilterChange('startDate', e.target.value)}
            />
          </FilterGroup>
          
          <FilterGroup>
            <FilterLabel>End Date</FilterLabel>
            <FilterInput
              type="date"
              value={filters.endDate || ''}
              onChange={(e) => handleFilterChange('endDate', e.target.value)}
            />
          </FilterGroup>
          
          <FilterGroup>
            <LCARSButton
              variant="secondary"
              size="sm"
              onClick={clearFilters}
            >
              Clear Filters
            </LCARSButton>
          </FilterGroup>
        </FiltersGrid>
      </FiltersPanel>

      {!trips || trips.length === 0 ? (
        <EmptyState>
          <div className="empty-title">No Trip Records Found</div>
          <div className="empty-message">
            {Object.keys(filters).length > 0 
              ? 'No trips match the current search parameters.'
              : 'No trips have been recorded yet.'
            }
          </div>
        </EmptyState>
      ) : (
        <TripsGrid>
          {trips.map((trip: Trip) => (
            <Link key={trip.id} to={`/trips/${trip.id}`} style={{ textDecoration: 'none' }}>
              <TripCard variant="primary">
                <TripCardContent>
                  <TripInfo>
                    <TripTitle>
                      {getBoatName(trip.boatId)} - {formatDate(trip.startTime)}
                    </TripTitle>
                    <TripMeta>
                      <div>
                        <strong>Water Type:</strong> {trip.waterType.toUpperCase()}
                      </div>
                      <div>
                        <strong>Role:</strong> {trip.role.toUpperCase()}
                      </div>
                      <div>
                        <strong>Duration:</strong> {formatDuration(trip.statistics?.durationSeconds || 0)}
                      </div>
                      <div>
                        <strong>Distance:</strong> {formatDistance(trip.statistics?.distanceMeters || 0)}
                      </div>
                    </TripMeta>
                  </TripInfo>
                  
                  <TripStats>
                    <div>
                      <StatValue>{formatSpeed(trip.statistics?.averageSpeedKnots || 0)}</StatValue>
                      <StatLabel>Avg Speed</StatLabel>
                    </div>
                    <div>
                      <StatValue>{formatSpeed(trip.statistics?.maxSpeedKnots || 0)}</StatValue>
                      <StatLabel>Max Speed</StatLabel>
                    </div>
                    <div>
                      <StatValue>{trip.statistics?.stopPoints?.length || 0}</StatValue>
                      <StatLabel>Stop Points</StatLabel>
                    </div>
                  </TripStats>
                </TripCardContent>
              </TripCard>
            </Link>
          ))}
        </TripsGrid>
      )}
    </TripListContainer>
  )
}