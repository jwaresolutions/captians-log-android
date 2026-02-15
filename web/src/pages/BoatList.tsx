import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import styled from 'styled-components'
import {
  LCARSHeader,
  LCARSPanel,
  LCARSButton,
  LCARSDataDisplay
} from '../components/lcars'
import { Skeleton } from '../components/LoadingSpinner'
import { ErrorMessage } from '../components/ErrorMessage'
import { useBoats, useToggleBoatStatus, useSetActiveBoat } from '../hooks/useBoats'
import { useOptimisticList } from '../hooks/useOptimisticUpdates'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'
import { Boat } from '../types/api'

const Container = styled.div`
  padding: 20px;
`

const BoatGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
  margin-top: 20px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`

const BoatCard = styled.div<{ $isActive?: boolean; $isEnabled?: boolean }>`
  padding: 20px;
  border: 2px solid ${props =>
    props.$isActive ? props.theme.colors.primary.neonCarrot :
    props.$isEnabled ? props.theme.colors.primary.anakiwa :
    props.theme.colors.interactive.disabled
  };
  background: ${props =>
    props.$isActive ? `${props.theme.colors.primary.neonCarrot}15` :
    props.$isEnabled ? `${props.theme.colors.primary.anakiwa}10` :
    `${props.theme.colors.interactive.disabled}10`
  };
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: ${props => props.theme.borderRadius.lg};

  &:hover {
    border-color: ${props => props.theme.colors.primary.neonCarrot};
    background: ${props => props.theme.colors.primary.neonCarrot}20;
  }
`

const BoatName = styled.h3`
  color: ${props => props.theme.colors.primary.neonCarrot};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: 1.4rem;
  margin: 0 0 15px 0;
  text-transform: uppercase;
`

const BoatStatus = styled.div`
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
`

const StatusBadge = styled.span<{ $type: 'active' | 'enabled' | 'disabled' }>`
  padding: 4px 12px;
  border-radius: 0;
  font-size: 0.8rem;
  font-weight: bold;
  text-transform: uppercase;
  background: ${props => {
    switch (props.$type) {
      case 'active': return props.theme.colors.primary.neonCarrot
      case 'enabled': return props.theme.colors.primary.anakiwa
      case 'disabled': return props.theme.colors.interactive.disabled
      default: return props.theme.colors.interactive.disabled
    }
  }};
  color: ${props => props.theme.colors.background};
`

const BoatActions = styled.div`
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
`

const ActionButton = styled(LCARSButton)`
  flex: 1;
  min-width: 120px;
`

const HeaderActions = styled.div`
  display: flex;
  gap: 15px;
  align-items: center;
`

const EmptyState = styled.div`
  text-align: center;
  padding: 60px 20px;
  color: ${props => props.theme.colors.text.secondary};
`

const EmptyStateIcon = styled.div`
  font-size: 4rem;
  margin-bottom: 20px;
  color: ${props => props.theme.colors.primary.anakiwa};
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

export const BoatList: React.FC = () => {
  const navigate = useNavigate()
  const { data: boats, isLoading, error } = useBoats()
  const toggleBoatStatus = useToggleBoatStatus()
  const setActiveBoat = useSetActiveBoat()
  const [actionLoading, setActionLoading] = useState<string | null>(null)
  
  // Optimistic updates for better UX
  const { optimisticUpdate } = useOptimisticList<Boat>(['boats'])

  const handleBoatClick = (boat: Boat) => {
    navigate(`/boats/${boat.id}`)
  }

  const handleToggleStatus = async (boat: Boat) => {
    setActionLoading(`toggle-${boat.id}`)
    try {
      // Optimistic update for immediate UI feedback
      await optimisticUpdate(
        boat.id,
        (currentBoat) => ({ ...currentBoat, enabled: !currentBoat.enabled }),
        () => toggleBoatStatus.mutateAsync({ 
          id: boat.id, 
          enabled: !boat.enabled 
        })
      )
    } catch (error) {
      console.error('Failed to toggle boat status:', error)
    } finally {
      setActionLoading(null)
    }
  }

  const handleSetActive = async (boat: Boat) => {
    if (boat.isActive) return // Already active
    
    setActionLoading(`active-${boat.id}`)
    try {
      await setActiveBoat.mutateAsync(boat.id)
    } catch (error) {
      console.error('Failed to set active boat:', error)
    } finally {
      setActionLoading(null)
    }
  }

  const handleCreateBoat = () => {
    navigate('/boats/new')
  }

  if (isLoading) {
    return (
      <Container>
        <HeaderContainer>
          <HeaderTitle>
            <LCARSHeader>BOAT MANAGEMENT</LCARSHeader>
            <Skeleton width="200px" height="20px" />
          </HeaderTitle>
          <HeaderActions>
            <Skeleton width="150px" height="40px" />
            <Skeleton width="180px" height="40px" />
          </HeaderActions>
        </HeaderContainer>
        
        <BoatGrid>
          {Array.from({ length: 3 }, (_, i) => (
            <LCARSPanel key={i}>
              <Skeleton variant="card" />
            </LCARSPanel>
          ))}
        </BoatGrid>
      </Container>
    )
  }

  if (error) {
    return (
      <Container>
        <LCARSHeader>BOAT MANAGEMENT</LCARSHeader>
        <ErrorMessage
          title="Failed to Load Boats"
          message={error.message}
          onRetry={() => window.location.reload()}
        />
      </Container>
    )
  }

  const activeBoat = boats?.find(boat => boat.isActive)
  const enabledBoats = boats?.filter(boat => boat.enabled) || []
  const disabledBoats = boats?.filter(boat => !boat.enabled) || []

  return (
    <Container>
        <HeaderContainer>
          <HeaderTitle>
            <LCARSHeader>BOAT MANAGEMENT</LCARSHeader>
            <LCARSDataDisplay
              label="VESSELS REGISTERED"
              value={boats?.length || 0}
              valueColor="anakiwa"
              size="sm"
            />
          </HeaderTitle>
          <HeaderActions>
            <LCARSDataDisplay
              label="ACTIVE VESSEL"
              value={activeBoat?.name || 'NONE SELECTED'}
              valueColor={activeBoat ? 'neonCarrot' : 'anakiwa'}
            />
            <ReadOnlyGuard>
              <LCARSButton
                variant="primary"
                onClick={handleCreateBoat}
              >
                ADD NEW VESSEL
              </LCARSButton>
            </ReadOnlyGuard>
          </HeaderActions>
        </HeaderContainer>

        {!boats || boats.length === 0 ? (
          <LCARSPanel>
            <EmptyState>
              <EmptyStateIcon>🚤</EmptyStateIcon>
              <h3>NO VESSELS REGISTERED</h3>
              <p>Add your first vessel to begin tracking trips and maintenance.</p>
              <ReadOnlyGuard>
                <LCARSButton
                  variant="primary"
                  onClick={handleCreateBoat}
                >
                  ADD FIRST VESSEL
                </LCARSButton>
              </ReadOnlyGuard>
            </EmptyState>
          </LCARSPanel>
        ) : (
          <BoatGrid>
            {boats.map((boat) => (
              <BoatCard
                key={boat.id}
                $isActive={boat.isActive}
                $isEnabled={boat.enabled}
                onClick={() => handleBoatClick(boat)}
              >
                <BoatName>{boat.name}</BoatName>
                
                <BoatStatus>
                  {boat.isActive && (
                    <StatusBadge $type="active">ACTIVE</StatusBadge>
                  )}
                  <StatusBadge $type={boat.enabled ? 'enabled' : 'disabled'}>
                    {boat.enabled ? 'ENABLED' : 'DISABLED'}
                  </StatusBadge>
                </BoatStatus>

                <LCARSDataDisplay
                  label="VESSEL ID"
                  value={boat.id.slice(0, 8).toUpperCase()}
                  valueColor="anakiwa"
                  size="sm"
                />

                <LCARSDataDisplay
                  label="REGISTERED"
                  value={new Date(boat.createdAt).toLocaleDateString()}
                  valueColor="anakiwa"
                  size="sm"
                />

                <BoatActions>
                  {!boat.isActive && boat.enabled && (
                    <ReadOnlyGuard>
                      <ActionButton
                        variant="secondary"
                        onClick={() => handleSetActive(boat)}
                        disabled={actionLoading === `active-${boat.id}`}
                      >
                        {actionLoading === `active-${boat.id}` ? 'SETTING...' : 'SET ACTIVE'}
                      </ActionButton>
                    </ReadOnlyGuard>
                  )}

                  <ReadOnlyGuard>
                    <ActionButton
                      variant={boat.enabled ? 'danger' : 'accent'}
                      onClick={() => handleToggleStatus(boat)}
                      disabled={actionLoading === `toggle-${boat.id}`}
                    >
                      {actionLoading === `toggle-${boat.id}`
                        ? 'UPDATING...'
                        : boat.enabled
                          ? 'DISABLE'
                          : 'ENABLE'
                      }
                    </ActionButton>
                  </ReadOnlyGuard>
                </BoatActions>
              </BoatCard>
            ))}
          </BoatGrid>
        )}

        {boats && boats.length > 0 && (
          <div style={{ marginTop: '30px', display: 'flex', gap: '20px' }}>
            <LCARSDataDisplay
              label="ENABLED VESSELS"
              value={enabledBoats.length.toString()}
              valueColor="anakiwa"
            />
            <LCARSDataDisplay
              label="DISABLED VESSELS"
              value={disabledBoats.length.toString()}
              valueColor="lilac"
            />
          </div>
        )}
      </Container>
  )
}

export default BoatList