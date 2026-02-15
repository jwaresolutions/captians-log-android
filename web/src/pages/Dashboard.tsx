import React from 'react'
import styled from 'styled-components'
import { useNavigate } from 'react-router-dom'
import {
  LCARSPanel,
  LCARSHeader,
  LCARSDataDisplay,
  LCARSButton,
  LCARSAlert
} from '../components/lcars'
import { useBoats } from '../hooks/useBoats'
import { useTrips } from '../hooks/useTrips'
import { useLicenseProgress } from '../hooks/useLicenseProgress'

const DashboardContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.lg};
`

const StatusGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: ${props => props.theme.spacing.md};
  margin-bottom: ${props => props.theme.spacing.lg};

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`

const RecentSection = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: ${props => props.theme.spacing.lg};
  
  @media (max-width: ${props => props.theme.breakpoints.md}) {
    grid-template-columns: 1fr;
  }
`

const TripItem = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: ${props => props.theme.spacing.sm};
  border-bottom: 1px solid ${props => props.theme.colors.surface.light};
  
  &:last-child {
    border-bottom: none;
  }
`

const TripInfo = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.xs};
`

const TripDate = styled.span`
  color: ${props => props.theme.colors.text.secondary};
  font-size: ${props => props.theme.typography.fontSize.sm};
`

const TripDetails = styled.span`
  color: ${props => props.theme.colors.text.primary};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
`

const QuickActions = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.md};
  flex-wrap: wrap;
`

const ProgressBar = styled.div<{ progress: number }>`
  width: 100%;
  height: 8px;
  background-color: ${props => props.theme.colors.surface.light};
  border-radius: ${props => props.theme.borderRadius.pill};
  overflow: hidden;
  margin-top: ${props => props.theme.spacing.sm};

  &::after {
    content: '';
    display: block;
    width: ${props => Math.min(props.progress, 100)}%;
    height: 100%;
    background-color: ${props => props.theme.colors.primary.neonCarrot};
    transition: width ${props => props.theme.animation.normal} ease;
  }
`

const ProgressText = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: ${props => props.theme.spacing.xs};
  font-size: ${props => props.theme.typography.fontSize.sm};
  color: ${props => props.theme.colors.text.secondary};
`

export const Dashboard: React.FC = () => {
  const navigate = useNavigate()
  const { data: boats, isLoading: boatsLoading, error: boatsError } = useBoats()
  const { data: trips, isLoading: tripsLoading, error: tripsError } = useTrips()
  const { data: licenseProgress, isLoading: licenseLoading, error: licenseError } = useLicenseProgress()

  const activeBoats = boats?.filter(boat => boat.enabled) || []
  const recentTrips = trips?.slice(0, 5) || []
  const totalTrips = trips?.length || 0

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    })
  }

  const formatDuration = (seconds: number) => {
    const hours = Math.floor(seconds / 3600)
    const minutes = Math.floor((seconds % 3600) / 60)
    return `${hours}h ${minutes}m`
  }

  const calculateProgress = (current: number, target: number) => {
    return Math.min((current / target) * 100, 100)
  }

  return (
    <DashboardContainer>
      <LCARSHeader level={1}>Command Center</LCARSHeader>

      {(boatsError || tripsError || licenseError) && (
        <LCARSAlert type="error">
          Unable to load dashboard data. Check your connection and try again.
        </LCARSAlert>
      )}

      <StatusGrid>
        <LCARSPanel title="Fleet Status" variant="accent">
          {boatsLoading ? (
            <LCARSDataDisplay label="Loading" value="..." valueColor="anakiwa" />
          ) : (
            <>
              <LCARSDataDisplay
                label="Total Vessels"
                value={boats?.length || 0}
                valueColor="anakiwa"
              />
              <LCARSDataDisplay
                label="Active Vessels"
                value={activeBoats.length}
                valueColor="success"
              />
              <LCARSDataDisplay
                label="Inactive Vessels"
                value={(boats?.length || 0) - activeBoats.length}
                valueColor="neonCarrot"
              />
            </>
          )}
        </LCARSPanel>

        <LCARSPanel title="License Progress" variant="secondary">
          {licenseLoading ? (
            <LCARSDataDisplay label="Loading" value="..." valueColor="lilac" />
          ) : licenseProgress ? (
            <>
              <LCARSDataDisplay
                label="Sea Time Days"
                value={licenseProgress.totalDays}
                valueColor="lilac"
              />
              <LCARSDataDisplay
                label="Days (3 Years)"
                value={licenseProgress.daysInLast3Years}
                valueColor="lilac"
              />
              <div>
                <ProgressBar progress={calculateProgress(licenseProgress.totalDays, 360)} />
                <ProgressText>
                  <span>360 Day Goal</span>
                  <span>{Math.round(calculateProgress(licenseProgress.totalDays, 360))}%</span>
                </ProgressText>
              </div>
            </>
          ) : (
            <LCARSDataDisplay label="Status" value="Disabled" valueColor="neonCarrot" />
          )}
        </LCARSPanel>

        <LCARSPanel title="System Status" variant="primary">
          <LCARSDataDisplay
            label="Interface Status"
            value="ONLINE"
            valueColor="success"
            size="sm"
          />
          <LCARSDataDisplay
            label="Active Boats"
            value={boatsLoading ? "..." : activeBoats.length.toString()}
            valueColor="neonCarrot"
            size="sm"
          />
          <LCARSDataDisplay
            label="Total Trips"
            value={tripsLoading ? "..." : totalTrips.toString()}
            valueColor="anakiwa"
            size="sm"
          />
        </LCARSPanel>
      </StatusGrid>

      <QuickActions>
        <LCARSButton size="sm" variant="primary" onClick={() => navigate('/trips')}>
          View Trips
        </LCARSButton>
        <LCARSButton size="sm" variant="secondary" onClick={() => navigate('/boats/new')}>
          Add Boat
        </LCARSButton>
      </QuickActions>

      <RecentSection>
        <LCARSPanel title="Recent Trips" variant="primary">
          {tripsLoading ? (
            <LCARSDataDisplay label="Loading" value="..." valueColor="neonCarrot" />
          ) : recentTrips.length > 0 ? (
            recentTrips.map((trip) => (
              <TripItem key={trip.id}>
                <TripInfo>
                  <TripDate>{formatDate(trip.startTime)}</TripDate>
                  <TripDetails>
                    {formatDuration(trip.statistics?.durationSeconds || 0)} • {trip.waterType}
                  </TripDetails>
                </TripInfo>
                <LCARSDataDisplay
                  label="Distance"
                  value={Math.round((trip.statistics?.distanceMeters || 0) / 1852)}
                  unit="nm"
                  size="sm"
                  valueColor="neonCarrot"
                />
              </TripItem>
            ))
          ) : (
            <div style={{ textAlign: 'center', padding: '2rem', color: '#999' }}>
              No trips recorded yet
            </div>
          )}
        </LCARSPanel>

        <LCARSPanel title="Upcoming Tasks" variant="accent">
          <div style={{ textAlign: 'center', padding: '2rem', color: '#999' }}>
            No maintenance tasks due
          </div>
        </LCARSPanel>
      </RecentSection>
    </DashboardContainer>
  )
}