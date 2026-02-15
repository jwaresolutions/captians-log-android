import React, { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import styled from 'styled-components'
import {
  LCARSHeader,
  LCARSPanel,
  LCARSButton,
  LCARSDataDisplay,
  LCARSAlert
} from '../components/lcars'
import { useBoat, useUpdateBoat, useToggleBoatStatus, useSetActiveBoat } from '../hooks/useBoats'
import { useTrips } from '../hooks/useTrips'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'

const Container = styled.div`
  padding: 20px;
`

const ContentGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-top: 20px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`

const InfoSection = styled(LCARSPanel)`
  padding: 25px;
`

const SectionTitle = styled.h3`
  color: ${props => props.theme.colors.primary.neonCarrot};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: 1.2rem;
  margin: 0 0 20px 0;
  text-transform: uppercase;
  border-bottom: 2px solid ${props => props.theme.colors.primary.neonCarrot};
  padding-bottom: 10px;
`

const StatusGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 25px;
`

const StatusBadge = styled.div<{ $type: 'active' | 'enabled' | 'disabled' }>`
  padding: 15px;
  text-align: center;
  border: 2px solid ${props => {
    switch (props.$type) {
      case 'active': return props.theme.colors.primary.neonCarrot
      case 'enabled': return props.theme.colors.primary.anakiwa
      case 'disabled': return props.theme.colors.interactive.disabled
      default: return props.theme.colors.interactive.disabled
    }
  }};
  background: ${props => {
    switch (props.$type) {
      case 'active': return `${props.theme.colors.primary.neonCarrot}20`
      case 'enabled': return `${props.theme.colors.primary.anakiwa}15`
      case 'disabled': return `${props.theme.colors.interactive.disabled}15`
      default: return `${props.theme.colors.interactive.disabled}15`
    }
  }};
`

const StatusLabel = styled.div`
  font-size: 0.9rem;
  color: ${props => props.theme.colors.text.secondary};
  margin-bottom: 5px;
  text-transform: uppercase;
`

const StatusValue = styled.div`
  font-size: 1.1rem;
  font-weight: bold;
  color: ${props => props.theme.colors.text.primary};
  text-transform: uppercase;
`

const ActionGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-top: 20px;
`

const BackButton = styled(LCARSButton)`
  margin-right: 15px;
`

const EditForm = styled.form`
  display: flex;
  flex-direction: column;
  gap: 20px;
`

const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
`

const Label = styled.label`
  color: ${props => props.theme.colors.text.primary};
  font-size: 0.9rem;
  text-transform: uppercase;
  font-weight: bold;
`

const Input = styled.input`
  padding: 12px 15px;
  background: ${props => props.theme.colors.background};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.text.primary};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: 1rem;

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 10px ${props => props.theme.colors.primary.neonCarrot}40;
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
`

const FormActions = styled.div`
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 20px;
`

const StatsGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
`

const HeaderContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
`

const HeaderTitle = styled.div`
  display: flex;
  flex-direction: column;
  gap: 5px;
`

const StatsSection = styled(LCARSPanel)`
  padding: 25px;
  margin-top: 30px;
`

export const BoatDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { data: boat, isLoading, error } = useBoat(id!)
  const { data: trips } = useTrips({ boatId: id })
  const updateBoat = useUpdateBoat()
  const toggleBoatStatus = useToggleBoatStatus()
  const setActiveBoat = useSetActiveBoat()
  
  const [isEditing, setIsEditing] = useState(false)
  const [editForm, setEditForm] = useState({ name: '' })
  const [actionLoading, setActionLoading] = useState<string | null>(null)

  React.useEffect(() => {
    if (boat) {
      setEditForm({ name: boat.name })
    }
  }, [boat])

  const handleBack = () => {
    navigate('/boats')
  }

  const handleEdit = () => {
    setIsEditing(true)
  }

  const handleCancelEdit = () => {
    setIsEditing(false)
    if (boat) {
      setEditForm({ name: boat.name })
    }
  }

  const handleSaveEdit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!boat || !editForm.name.trim()) return

    setActionLoading('save')
    try {
      await updateBoat.mutateAsync({
        id: boat.id,
        data: { name: editForm.name.trim() }
      })
      setIsEditing(false)
    } catch (error) {
      console.error('Failed to update boat:', error)
    } finally {
      setActionLoading(null)
    }
  }

  const handleToggleStatus = async () => {
    if (!boat) return
    
    setActionLoading('toggle')
    try {
      await toggleBoatStatus.mutateAsync({ 
        id: boat.id, 
        enabled: !boat.enabled 
      })
    } catch (error) {
      console.error('Failed to toggle boat status:', error)
    } finally {
      setActionLoading(null)
    }
  }

  const handleSetActive = async () => {
    if (!boat || boat.isActive) return
    
    setActionLoading('active')
    try {
      await setActiveBoat.mutateAsync(boat.id)
    } catch (error) {
      console.error('Failed to set active boat:', error)
    } finally {
      setActionLoading(null)
    }
  }

  if (isLoading) {
    return (
      <Container>
        <LCARSHeader>VESSEL DETAILS</LCARSHeader>
        <LCARSDataDisplay
          label="STATUS"
          value="LOADING VESSEL DATA..."
          valueColor="anakiwa"
        />
      </Container>
    )
  }

  if (error || !boat) {
    return (
      <Container>
        <LCARSHeader>VESSEL DETAILS</LCARSHeader>
        <LCARSAlert type="error">
          {error?.message || 'Vessel not found'}
        </LCARSAlert>
        <BackButton variant="secondary" onClick={handleBack}>
          BACK TO VESSELS
        </BackButton>
      </Container>
    )
  }

  const tripCount = trips?.length || 0
  const totalHours = trips?.reduce((sum, trip) => {
    return sum + (trip.statistics?.durationSeconds || 0)
  }, 0) || 0
  const totalDistance = trips?.reduce((sum, trip) => {
    return sum + (trip.statistics?.distanceMeters || 0)
  }, 0) || 0

  return (
    <>
      <Container>
        <HeaderContainer>
          <HeaderTitle>
            <LCARSHeader>VESSEL DETAILS</LCARSHeader>
            <LCARSDataDisplay
              label="VESSEL NAME"
              value={boat.name}
              valueColor="neonCarrot"
              size="sm"
            />
          </HeaderTitle>
          <div>
            <BackButton variant="secondary" onClick={handleBack}>
              BACK TO VESSELS
            </BackButton>
            {!isEditing && (
              <ReadOnlyGuard>
                <LCARSButton variant="primary" onClick={handleEdit}>
                  EDIT VESSEL
                </LCARSButton>
              </ReadOnlyGuard>
            )}
          </div>
        </HeaderContainer>

        <ContentGrid>
          <InfoSection>
            <SectionTitle>Vessel Information</SectionTitle>
            
            {isEditing ? (
              <EditForm onSubmit={handleSaveEdit}>
                <FormGroup>
                  <Label>Vessel Name</Label>
                  <Input
                    type="text"
                    value={editForm.name}
                    onChange={(e) => setEditForm({ ...editForm, name: e.target.value })}
                    placeholder="Enter vessel name"
                    required
                    disabled={actionLoading === 'save'}
                  />
                </FormGroup>
                
                <FormActions>
                  <LCARSButton 
                    type="button" 
                    variant="secondary" 
                    onClick={handleCancelEdit}
                    disabled={actionLoading === 'save'}
                  >
                    CANCEL
                  </LCARSButton>
                  <LCARSButton 
                    type="submit" 
                    variant="primary"
                    disabled={actionLoading === 'save' || !editForm.name.trim()}
                  >
                    {actionLoading === 'save' ? 'SAVING...' : 'SAVE CHANGES'}
                  </LCARSButton>
                </FormActions>
              </EditForm>
            ) : (
              <>
                <LCARSDataDisplay
                  label="VESSEL NAME"
                  value={boat.name}
                  valueColor="neonCarrot"
                />

                <LCARSDataDisplay
                  label="VESSEL ID"
                  value={boat.id}
                  valueColor="anakiwa"
                />

                <LCARSDataDisplay
                  label="REGISTERED"
                  value={new Date(boat.createdAt).toLocaleString()}
                  valueColor="anakiwa"
                />

                <LCARSDataDisplay
                  label="LAST UPDATED"
                  value={new Date(boat.updatedAt).toLocaleString()}
                  valueColor="anakiwa"
                />
              </>
            )}
          </InfoSection>

          <InfoSection>
            <SectionTitle>Status & Actions</SectionTitle>
            
            <StatusGrid>
              <StatusBadge $type={boat.isActive ? 'active' : 'disabled'}>
                <StatusLabel>Active Status</StatusLabel>
                <StatusValue>{boat.isActive ? 'ACTIVE' : 'INACTIVE'}</StatusValue>
              </StatusBadge>
              
              <StatusBadge $type={boat.enabled ? 'enabled' : 'disabled'}>
                <StatusLabel>Operational Status</StatusLabel>
                <StatusValue>{boat.enabled ? 'ENABLED' : 'DISABLED'}</StatusValue>
              </StatusBadge>
            </StatusGrid>

            {!isEditing && (
              <ReadOnlyGuard>
                <ActionGrid>
                  {!boat.isActive && boat.enabled && (
                    <LCARSButton
                      variant="primary"
                      onClick={handleSetActive}
                      disabled={actionLoading === 'active'}
                    >
                      {actionLoading === 'active' ? 'SETTING...' : 'SET AS ACTIVE'}
                    </LCARSButton>
                  )}

                  <LCARSButton
                    variant={boat.enabled ? 'danger' : 'accent'}
                    onClick={handleToggleStatus}
                    disabled={actionLoading === 'toggle'}
                  >
                    {actionLoading === 'toggle'
                      ? 'UPDATING...'
                      : boat.enabled
                        ? 'DISABLE VESSEL'
                        : 'ENABLE VESSEL'
                    }
                  </LCARSButton>
                </ActionGrid>
              </ReadOnlyGuard>
            )}
          </InfoSection>
        </ContentGrid>

        <StatsSection>
          <SectionTitle>Usage Statistics</SectionTitle>
          
          <StatsGrid>
            <LCARSDataDisplay
              label="TOTAL TRIPS"
              value={tripCount.toString()}
              valueColor="anakiwa"
            />

            <LCARSDataDisplay
              label="TOTAL HOURS"
              value={`${(totalHours / 3600).toFixed(1)}`}
              unit="hrs"
              valueColor="anakiwa"
            />

            <LCARSDataDisplay
              label="TOTAL DISTANCE"
              value={`${(totalDistance * 0.000539957).toFixed(1)}`}
              unit="nm"
              valueColor="anakiwa"
            />

            <LCARSDataDisplay
              label="LAST TRIP"
              value={trips && trips.length > 0
                ? new Date(trips[0].startTime).toLocaleDateString()
                : 'NO TRIPS'
              }
              valueColor="anakiwa"
            />
          </StatsGrid>
        </StatsSection>
      </Container>
    </>
  )
}

export default BoatDetail