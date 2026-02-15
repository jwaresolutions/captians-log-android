import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import styled from 'styled-components'
import {
  LCARSHeader,
  LCARSPanel,
  LCARSButton,
  LCARSAlert
} from '../components/lcars'
import { useBoats } from '../hooks/useBoats'
import { useCreateTrip } from '../hooks/useTrips'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'

const Container = styled.div`
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
`

const FormPanel = styled(LCARSPanel)`
  padding: 30px;
  margin-top: 20px;
`

const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: 25px;
`

const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: 10px;
`

const FormRow = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;

  @media (max-width: 600px) {
    grid-template-columns: 1fr;
  }
`

const Label = styled.label`
  color: ${props => props.theme.colors.text.primary};
  font-size: 1rem;
  text-transform: uppercase;
  font-weight: bold;
  font-family: ${props => props.theme.typography.fontFamily.primary};
`

const Input = styled.input`
  padding: 15px 20px;
  background: ${props => props.theme.colors.background};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.text.primary};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: 1.1rem;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 15px ${props => props.theme.colors.primary.neonCarrot}40;
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  /* Style the calendar picker */
  &::-webkit-calendar-picker-indicator {
    filter: invert(1);
    cursor: pointer;
    padding: 5px;
  }
`

const Select = styled.select`
  padding: 15px 20px;
  background: ${props => props.theme.colors.background};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.text.primary};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: 1.1rem;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 15px ${props => props.theme.colors.primary.neonCarrot}40;
  }

  option {
    background: ${props => props.theme.colors.background};
    color: ${props => props.theme.colors.text.primary};
  }
`

const HelpText = styled.p`
  color: ${props => props.theme.colors.text.secondary};
  font-size: 0.9rem;
  margin: 0;
  line-height: 1.4;
`

const FormActions = styled.div`
  display: flex;
  gap: 20px;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 2px solid ${props => props.theme.colors.primary.anakiwa};
`

const HeaderContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
`

const RequiredIndicator = styled.span`
  color: ${props => props.theme.colors.primary.neonCarrot};
  margin-left: 5px;
`

const ValidationError = styled.div`
  color: ${props => props.theme.colors.status.error};
  font-size: 0.9rem;
  margin-top: 5px;
`

interface FormData {
  boatId: string
  startTime: string
  endTime: string
  waterType: 'inland' | 'coastal' | 'offshore'
  role: 'captain' | 'crew' | 'observer'
}

interface FormErrors {
  boatId?: string
  startTime?: string
  endTime?: string
}

export const TripForm: React.FC = () => {
  const navigate = useNavigate()
  const { data: boats, isLoading: boatsLoading } = useBoats()
  const createTrip = useCreateTrip()

  const [formData, setFormData] = useState<FormData>({
    boatId: '',
    startTime: '',
    endTime: '',
    waterType: 'inland',
    role: 'captain'
  })

  const [errors, setErrors] = useState<FormErrors>({})
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleBack = () => {
    navigate('/trips')
  }

  const handleInputChange = (field: keyof FormData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }))

    if (errors[field as keyof FormErrors]) {
      setErrors(prev => ({ ...prev, [field]: undefined }))
    }
  }

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {}

    if (!formData.boatId) {
      newErrors.boatId = 'Please select a vessel'
    }

    if (!formData.startTime) {
      newErrors.startTime = 'Start time is required'
    }

    if (!formData.endTime) {
      newErrors.endTime = 'End time is required'
    }

    if (formData.startTime && formData.endTime) {
      const start = new Date(formData.startTime)
      const end = new Date(formData.endTime)
      if (end <= start) {
        newErrors.endTime = 'End time must be after start time'
      }
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!validateForm()) {
      return
    }

    setIsSubmitting(true)

    try {
      const trip = await createTrip.mutateAsync({
        boatId: formData.boatId,
        startTime: new Date(formData.startTime).toISOString(),
        endTime: new Date(formData.endTime).toISOString(),
        waterType: formData.waterType,
        role: formData.role,
        gpsPoints: [] // Manual entry - no GPS points
      })

      navigate(`/trips/${trip.id}`)
    } catch (error) {
      console.error('Failed to create trip:', error)
    } finally {
      setIsSubmitting(false)
    }
  }

  const activeBoats = boats?.filter(boat => boat.enabled) || []

  return (
    <Container>
      <HeaderContainer>
        <LCARSHeader>ADD MANUAL TRIP</LCARSHeader>
        <LCARSButton variant="secondary" onClick={handleBack}>
          BACK TO TRIPS
        </LCARSButton>
      </HeaderContainer>

      {createTrip.error && (
        <LCARSAlert type="error">
          Failed to create trip: {createTrip.error.message}
        </LCARSAlert>
      )}

      <FormPanel>
        <Form onSubmit={handleSubmit}>
          <FormGroup>
            <Label>
              Vessel
              <RequiredIndicator>*</RequiredIndicator>
            </Label>
            <Select
              value={formData.boatId}
              onChange={(e) => handleInputChange('boatId', e.target.value)}
              disabled={isSubmitting || boatsLoading}
            >
              <option value="">-- Select Vessel --</option>
              {activeBoats.map(boat => (
                <option key={boat.id} value={boat.id}>
                  {boat.name}
                </option>
              ))}
            </Select>
            <HelpText>
              Select the vessel used for this trip.
            </HelpText>
            {errors.boatId && <ValidationError>{errors.boatId}</ValidationError>}
          </FormGroup>

          <FormRow>
            <FormGroup>
              <Label>
                Start Date & Time
                <RequiredIndicator>*</RequiredIndicator>
              </Label>
              <Input
                type="datetime-local"
                value={formData.startTime}
                onChange={(e) => handleInputChange('startTime', e.target.value)}
                disabled={isSubmitting}
              />
              <HelpText>
                When did the trip begin?
              </HelpText>
              {errors.startTime && <ValidationError>{errors.startTime}</ValidationError>}
            </FormGroup>

            <FormGroup>
              <Label>
                End Date & Time
                <RequiredIndicator>*</RequiredIndicator>
              </Label>
              <Input
                type="datetime-local"
                value={formData.endTime}
                onChange={(e) => handleInputChange('endTime', e.target.value)}
                disabled={isSubmitting}
              />
              <HelpText>
                When did the trip end?
              </HelpText>
              {errors.endTime && <ValidationError>{errors.endTime}</ValidationError>}
            </FormGroup>
          </FormRow>

          <FormRow>
            <FormGroup>
              <Label>Water Type</Label>
              <Select
                value={formData.waterType}
                onChange={(e) => handleInputChange('waterType', e.target.value)}
                disabled={isSubmitting}
              >
                <option value="inland">Inland</option>
                <option value="coastal">Coastal / Nearshore</option>
                <option value="offshore">Offshore</option>
              </Select>
              <HelpText>
                The type of waters navigated during this trip.
              </HelpText>
            </FormGroup>

            <FormGroup>
              <Label>Your Role</Label>
              <Select
                value={formData.role}
                onChange={(e) => handleInputChange('role', e.target.value)}
                disabled={isSubmitting}
              >
                <option value="captain">Captain</option>
                <option value="crew">Crew</option>
                <option value="observer">Observer</option>
              </Select>
              <HelpText>
                Your role during this trip.
              </HelpText>
            </FormGroup>
          </FormRow>

          <FormActions>
            <LCARSButton
              type="button"
              variant="secondary"
              onClick={handleBack}
              disabled={isSubmitting}
            >
              CANCEL
            </LCARSButton>
            <ReadOnlyGuard>
              <LCARSButton
                type="submit"
                variant="primary"
                disabled={isSubmitting || !formData.boatId}
              >
                {isSubmitting ? 'CREATING TRIP...' : 'CREATE TRIP'}
              </LCARSButton>
            </ReadOnlyGuard>
          </FormActions>
        </Form>
      </FormPanel>
    </Container>
  )
}

export default TripForm
