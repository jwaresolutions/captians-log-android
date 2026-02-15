import React, { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import styled from 'styled-components'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { LCARSHeader } from '../components/lcars/LCARSHeader'
import { useTrip, useUpdateTrip, useAddManualData } from '../hooks/useTrips'
import { useBoats } from '../hooks/useBoats'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'
import { ManualData } from '../types/api'

const TripEditContainer = styled.div`
  padding: ${props => props.theme.spacing.lg};
  max-width: 1000px;
  margin: 0 auto;
`

const BackButton = styled(LCARSButton)`
  margin-bottom: ${props => props.theme.spacing.lg};
`

const FormGrid = styled.div`
  display: grid;
  gap: ${props => props.theme.spacing.lg};
`

const FormSection = styled(LCARSPanel)`
  margin-bottom: ${props => props.theme.spacing.lg};
`

const FormRow = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: ${props => props.theme.spacing.md};
  margin-bottom: ${props => props.theme.spacing.md};
  
  &:last-child {
    margin-bottom: 0;
  }
`

const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.xs};
`

const FormLabel = styled.label`
  font-size: ${props => props.theme.typography.fontSize.sm};
  color: ${props => props.theme.colors.text.secondary};
  text-transform: uppercase;
  letter-spacing: 1px;
  font-weight: ${props => props.theme.typography.fontWeight.bold};
`

const FormInput = styled.input`
  background-color: ${props => props.theme.colors.surface.medium};
  border: 1px solid ${props => props.theme.colors.primary.neonCarrot};
  border-radius: ${props => props.theme.borderRadius.sm};
  color: ${props => props.theme.colors.text.primary};
  padding: ${props => props.theme.spacing.sm};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: ${props => props.theme.typography.fontSize.md};

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.tanoi};
    box-shadow: 0 0 0 2px rgba(255, 153, 102, 0.2);
  }

  &:disabled {
    background-color: ${props => props.theme.colors.surface.dark};
    color: ${props => props.theme.colors.text.muted};
    cursor: not-allowed;
  }
`

const FormSelect = styled.select`
  background-color: ${props => props.theme.colors.surface.medium};
  border: 1px solid ${props => props.theme.colors.primary.neonCarrot};
  border-radius: ${props => props.theme.borderRadius.sm};
  color: ${props => props.theme.colors.text.primary};
  padding: ${props => props.theme.spacing.sm};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: ${props => props.theme.typography.fontSize.md};

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.tanoi};
    box-shadow: 0 0 0 2px rgba(255, 153, 102, 0.2);
  }
`

const FormTextarea = styled.textarea`
  background-color: ${props => props.theme.colors.surface.medium};
  border: 1px solid ${props => props.theme.colors.primary.neonCarrot};
  border-radius: ${props => props.theme.borderRadius.sm};
  color: ${props => props.theme.colors.text.primary};
  padding: ${props => props.theme.spacing.sm};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: ${props => props.theme.typography.fontSize.md};
  resize: vertical;
  min-height: 100px;

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.tanoi};
    box-shadow: 0 0 0 2px rgba(255, 153, 102, 0.2);
  }
`

const ActionButtons = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.md};
  justify-content: flex-end;
  margin-top: ${props => props.theme.spacing.lg};
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

const SuccessMessage = styled.div`
  background-color: rgba(102, 255, 102, 0.1);
  border: 1px solid ${props => props.theme.colors.status.success};
  border-radius: ${props => props.theme.borderRadius.md};
  color: ${props => props.theme.colors.status.success};
  padding: ${props => props.theme.spacing.md};
  margin-bottom: ${props => props.theme.spacing.lg};
  text-align: center;
  font-weight: ${props => props.theme.typography.fontWeight.bold};
`

interface TripFormData {
  waterType: 'inland' | 'coastal' | 'offshore'
  role: 'captain' | 'crew' | 'observer'
  boatId: string
}

interface ManualFormData {
  engineHours?: number
  fuelConsumed?: number
  weatherConditions?: string
  numberOfPassengers?: number
  destination?: string
}

export const TripEdit: React.FC = () => {
  const { id } = useParams<{ id: string }>()

  const { data: trip, isLoading, error } = useTrip(id!)
  const { data: boats } = useBoats()
  const updateTripMutation = useUpdateTrip()
  const addManualDataMutation = useAddManualData()

  const [tripData, setTripData] = useState<TripFormData>({
    waterType: 'inland',
    role: 'captain',
    boatId: ''
  })

  const [manualData, setManualData] = useState<ManualFormData>({})
  const [successMessage, setSuccessMessage] = useState<string>('')

  useEffect(() => {
    if (trip) {
      setTripData({
        waterType: trip.waterType,
        role: trip.role,
        boatId: trip.boatId
      })
      
      if (trip.manualData) {
        // Convert null values to undefined for proper form handling
        setManualData({
          engineHours: trip.manualData.engineHours ?? undefined,
          fuelConsumed: trip.manualData.fuelConsumed ?? undefined,
          weatherConditions: trip.manualData.weatherConditions ?? undefined,
          numberOfPassengers: trip.manualData.numberOfPassengers ?? undefined,
          destination: trip.manualData.destination ?? undefined
        })
      }
    }
  }, [trip])

  const handleTripDataChange = (field: keyof TripFormData, value: string) => {
    setTripData(prev => ({
      ...prev,
      [field]: value
    }))
  }

  const handleManualDataChange = (field: keyof ManualFormData, value: string | number) => {
    setManualData(prev => ({
      ...prev,
      [field]: value === '' ? undefined : value
    }))
  }

  const handleSaveTripData = async () => {
    if (!trip) return

    try {
      await updateTripMutation.mutateAsync({
        id: trip.id,
        data: tripData
      })
      setSuccessMessage('Trip information updated successfully!')
      setTimeout(() => setSuccessMessage(''), 3000)
    } catch (error) {
      console.error('Error updating trip:', error)
    }
  }

  const handleSaveManualData = async () => {
    if (!trip) return

    // Filter out undefined, null, empty string, and NaN values
    const filteredManualData: ManualData = {}
    Object.entries(manualData).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '' &&
          !(typeof value === 'number' && isNaN(value))) {
        filteredManualData[key as keyof ManualData] = value as any
      }
    })

    try {
      await addManualDataMutation.mutateAsync({
        tripId: trip.id,
        data: filteredManualData
      })
      setSuccessMessage('Manual data updated successfully!')
      setTimeout(() => setSuccessMessage(''), 3000)
    } catch (error) {
      console.error('Error updating manual data:', error)
    }
  }

  const formatDateTime = (dateString: string): string => {
    return new Date(dateString).toLocaleString('en-US', {
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
      <TripEditContainer>
        <LoadingState>Loading Trip Data...</LoadingState>
      </TripEditContainer>
    )
  }

  if (error || !trip) {
    return (
      <TripEditContainer>
        <ErrorState>
          {error ? `Error loading trip: ${error.message}` : 'Trip not found'}
        </ErrorState>
      </TripEditContainer>
    )
  }

  return (
    <TripEditContainer>
      <BackButton as={Link} to={`/trips/${trip.id}`} variant="secondary" size="sm">
        ← Back to Trip Details
      </BackButton>
      
      <LCARSHeader>
        Edit Trip Data - {getBoatName(trip.boatId)} - {formatDateTime(trip.startTime)}
      </LCARSHeader>

      {successMessage && (
        <SuccessMessage>{successMessage}</SuccessMessage>
      )}

      <FormGrid>
        <FormSection title="Trip Information" variant="primary">
          <FormRow>
            <FormGroup>
              <FormLabel>Vessel</FormLabel>
              <FormSelect
                value={tripData.boatId}
                onChange={(e) => handleTripDataChange('boatId', e.target.value)}
              >
                {boats?.map(boat => (
                  <option key={boat.id} value={boat.id}>
                    {boat.name}
                  </option>
                ))}
              </FormSelect>
            </FormGroup>
            
            <FormGroup>
              <FormLabel>Water Type</FormLabel>
              <FormSelect
                value={tripData.waterType}
                onChange={(e) => handleTripDataChange('waterType', e.target.value)}
              >
                <option value="inland">Inland</option>
                <option value="coastal">Coastal/Nearshore</option>
                <option value="offshore">Offshore</option>
              </FormSelect>
            </FormGroup>
            
            <FormGroup>
              <FormLabel>Role</FormLabel>
              <FormSelect
                value={tripData.role}
                onChange={(e) => handleTripDataChange('role', e.target.value)}
              >
                <option value="captain">Captain</option>
                <option value="crew">Crew</option>
                <option value="observer">Observer</option>
              </FormSelect>
            </FormGroup>
          </FormRow>

          <FormRow>
            <FormGroup>
              <FormLabel>Start Time</FormLabel>
              <FormInput
                type="text"
                value={formatDateTime(trip.startTime)}
                disabled
              />
            </FormGroup>
            
            <FormGroup>
              <FormLabel>End Time</FormLabel>
              <FormInput
                type="text"
                value={formatDateTime(trip.endTime)}
                disabled
              />
            </FormGroup>
          </FormRow>

          <ActionButtons>
            <ReadOnlyGuard>
              <LCARSButton
                variant="primary"
                onClick={handleSaveTripData}
                disabled={updateTripMutation.isPending}
              >
                {updateTripMutation.isPending ? 'Saving...' : 'Save Trip Information'}
              </LCARSButton>
            </ReadOnlyGuard>
          </ActionButtons>
        </FormSection>

        <FormSection title="Manual Data Entry" variant="secondary">
          <FormRow>
            <FormGroup>
              <FormLabel>Engine Hours</FormLabel>
              <FormInput
                type="number"
                step="0.1"
                min="0"
                placeholder="0.0"
                value={manualData.engineHours || ''}
                onChange={(e) => handleManualDataChange('engineHours', parseFloat(e.target.value))}
              />
            </FormGroup>
            
            <FormGroup>
              <FormLabel>Fuel Consumed (gallons)</FormLabel>
              <FormInput
                type="number"
                step="0.1"
                min="0"
                placeholder="0.0"
                value={manualData.fuelConsumed || ''}
                onChange={(e) => handleManualDataChange('fuelConsumed', parseFloat(e.target.value))}
              />
            </FormGroup>
            
            <FormGroup>
              <FormLabel>Number of Passengers</FormLabel>
              <FormInput
                type="number"
                min="0"
                placeholder="0"
                value={manualData.numberOfPassengers || ''}
                onChange={(e) => handleManualDataChange('numberOfPassengers', parseInt(e.target.value))}
              />
            </FormGroup>
          </FormRow>

          <FormRow>
            <FormGroup>
              <FormLabel>Destination</FormLabel>
              <FormInput
                type="text"
                placeholder="Enter destination"
                value={manualData.destination || ''}
                onChange={(e) => handleManualDataChange('destination', e.target.value)}
              />
            </FormGroup>
          </FormRow>

          <FormRow>
            <FormGroup>
              <FormLabel>Weather Conditions</FormLabel>
              <FormTextarea
                placeholder="Describe weather conditions, sea state, visibility, etc."
                value={manualData.weatherConditions || ''}
                onChange={(e) => handleManualDataChange('weatherConditions', e.target.value)}
              />
            </FormGroup>
          </FormRow>

          <ActionButtons>
            <ReadOnlyGuard>
              <LCARSButton
                variant="secondary"
                onClick={handleSaveManualData}
                disabled={addManualDataMutation.isPending}
              >
                {addManualDataMutation.isPending ? 'Saving...' : 'Save Manual Data'}
              </LCARSButton>
            </ReadOnlyGuard>
          </ActionButtons>
        </FormSection>
      </FormGrid>

      <ActionButtons>
        <Link to={`/trips/${trip.id}`} style={{ textDecoration: 'none' }}>
          <LCARSButton variant="accent">
            View Trip Details
          </LCARSButton>
        </Link>
        <Link to="/trips" style={{ textDecoration: 'none' }}>
          <LCARSButton variant="secondary">
            Back to Trip Log
          </LCARSButton>
        </Link>
      </ActionButtons>
    </TripEditContainer>
  )
}