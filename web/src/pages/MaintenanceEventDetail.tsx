import { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import styled from 'styled-components'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSHeader } from '../components/lcars/LCARSHeader'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { LCARSDataDisplay } from '../components/lcars/LCARSDataDisplay'
import { LCARSColumn } from '../components/lcars/LCARSColumn'
import { LCARSAlert } from '../components/lcars/LCARSAlert'
import { useMaintenanceEvent, useCompleteMaintenanceEvent } from '../hooks/useMaintenance'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'

const Container = styled.div`
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 20px;
  height: 100vh;
  padding: 20px;
`

const MainContent = styled.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: hidden;
`

const ContentArea = styled(LCARSPanel)`
  flex: 1;
  overflow-y: auto;
  padding: 20px;
`

const ActionBar = styled.div`
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
`

const InfoGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
`

const InfoSection = styled(LCARSPanel)`
  padding: 15px;
`

const SectionTitle = styled.h3`
  color: ${props => props.theme.colors.primary.neonCarrot};
  margin: 0 0 15px 0;
  font-size: 16px;
  text-transform: uppercase;
`

const InfoRow = styled.div`
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  
  &:last-child {
    margin-bottom: 0;
  }
`

const Label = styled.span`
  color: ${props => props.theme.colors.text.secondary};
  font-weight: bold;
`

const Value = styled.span`
  color: ${props => props.theme.colors.text.primary};
`

const StatusBadge = styled.span<{ status: 'due' | 'overdue' | 'completed' | 'upcoming' }>`
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  text-transform: uppercase;
  background-color: ${props => {
    switch (props.status) {
      case 'completed': return '#44ff44'
      case 'overdue': return '#ff4444'
      case 'due': return props.theme.colors.primary.neonCarrot
      case 'upcoming': return props.theme.colors.primary.anakiwa
      default: return props.theme.colors.text.secondary
    }
  }};
  color: ${props => props.theme.colors.background};
`

const CompletionForm = styled.form`
  display: flex;
  flex-direction: column;
  gap: 15px;
  background-color: ${props => props.theme.colors.background}40;
  padding: 20px;
  border-radius: 4px;
  border-left: 4px solid ${props => props.theme.colors.primary.neonCarrot};
`

const FormRow = styled.div`
  display: flex;
  gap: 15px;
  align-items: center;
`

const FormLabel = styled.label`
  color: ${props => props.theme.colors.text.secondary};
  font-weight: bold;
  min-width: 120px;
`

const FormInput = styled.input`
  background-color: ${props => props.theme.colors.background};
  color: ${props => props.theme.colors.text.primary};
  border: 2px solid ${props => props.theme.colors.primary.neonCarrot};
  padding: 8px 12px;
  border-radius: 4px;
  font-family: inherit;
  flex: 1;
`

const FormTextarea = styled.textarea`
  background-color: ${props => props.theme.colors.background};
  color: ${props => props.theme.colors.text.primary};
  border: 2px solid ${props => props.theme.colors.primary.neonCarrot};
  padding: 8px 12px;
  border-radius: 4px;
  font-family: inherit;
  resize: vertical;
  min-height: 80px;
  flex: 1;
`

const LoadingContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: ${props => props.theme.colors.primary.neonCarrot};
  font-size: 18px;
`

const ErrorContainer = styled.div`
  padding: 20px;
  text-align: center;
`

const Description = styled.div`
  background-color: ${props => props.theme.colors.background}40;
  padding: 15px;
  border-radius: 4px;
  border-left: 4px solid ${props => props.theme.colors.primary.lilac};
  margin-bottom: 20px;
  line-height: 1.5;
`

export function MaintenanceEventDetail() {
  const { id } = useParams<{ id: string }>()
  const [showCompletionForm, setShowCompletionForm] = useState(false)
  const [completionData, setCompletionData] = useState({
    actualCost: '',
    actualTime: '',
    notes: ''
  })

  const { data: event, isLoading, error } = useMaintenanceEvent(id!)
  const completeEventMutation = useCompleteMaintenanceEvent()

  const getEventStatus = (event: any) => {
    if (event.completedAt) return 'completed'
    
    const dueDate = new Date(event.dueDate)
    const now = new Date()
    const daysDiff = Math.ceil((dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
    
    if (daysDiff < 0) return 'overdue'
    if (daysDiff <= 7) return 'due'
    return 'upcoming'
  }

  const formatCurrency = (amount?: number) => {
    if (!amount) return 'Not specified'
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount)
  }

  const formatTime = (minutes?: number) => {
    if (!minutes) return 'Not specified'
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    if (hours > 0) {
      return `${hours}h ${mins}m`
    }
    return `${mins}m`
  }

  const formatRecurrence = (recurrence?: { type: string; interval: number }) => {
    if (!recurrence) return 'One-time'
    const { type, interval } = recurrence
    const unit = interval === 1 ? type.slice(0, -1) : type
    return `Every ${interval} ${unit}`
  }

  const handleCompleteEvent = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!event) return

    try {
      const data: any = {}
      if (completionData.actualCost) {
        data.actualCost = parseFloat(completionData.actualCost)
      }
      if (completionData.actualTime) {
        data.actualTime = parseInt(completionData.actualTime)
      }
      if (completionData.notes) {
        data.notes = completionData.notes
      }

      await completeEventMutation.mutateAsync({ id: event.id, data })
      setShowCompletionForm(false)
    } catch (error) {
      console.error('Failed to complete event:', error)
      alert('Failed to complete maintenance event. Please try again.')
    }
  }

  if (isLoading) {
    return (
      <Container>
        <LCARSColumn>
          <LCARSDataDisplay label="Status" value="LOADING" />
        </LCARSColumn>
        <MainContent>
          <LCARSHeader>Maintenance Event</LCARSHeader>
          <ContentArea>
            <LoadingContainer>Loading event details...</LoadingContainer>
          </ContentArea>
        </MainContent>
      </Container>
    )
  }

  if (error || !event) {
    return (
      <Container>
        <LCARSColumn>
          <LCARSDataDisplay label="Status" value="ERROR" />
        </LCARSColumn>
        <MainContent>
          <LCARSHeader>Maintenance Event</LCARSHeader>
          <ContentArea>
            <ErrorContainer>
              <LCARSAlert type="error">
                Event not found or failed to load.
              </LCARSAlert>
              <Link to="/maintenance">
                <LCARSButton>Back to Maintenance</LCARSButton>
              </Link>
            </ErrorContainer>
          </ContentArea>
        </MainContent>
      </Container>
    )
  }

  const status = getEventStatus(event)
  const isCompleted = !!event.completedAt

  return (
    <Container>
      <LCARSColumn>
        <LCARSDataDisplay label="Event Status" value={status.toUpperCase()} />
        <LCARSDataDisplay label="Boat" value={event.template?.boat?.name || "Unknown"} />
        <LCARSDataDisplay label="Due Date" value={new Date(event.dueDate).toLocaleDateString()} />
        {isCompleted && (
          <LCARSDataDisplay label="Completed" value={new Date(event.completedAt!).toLocaleDateString()} />
        )}
      </LCARSColumn>

      <MainContent>
        <LCARSHeader>{event.template?.title || 'Maintenance Event'}</LCARSHeader>
        
        <ActionBar>
          <Link to="/maintenance">
            <LCARSButton>Back to List</LCARSButton>
          </Link>
          {event.template && (
            <Link to={`/maintenance/templates/${event.template.id}`}>
              <LCARSButton>View Template</LCARSButton>
            </Link>
          )}
          {!isCompleted && (
            <ReadOnlyGuard>
              <LCARSButton
                onClick={() => setShowCompletionForm(!showCompletionForm)}
                variant="accent"
              >
                {showCompletionForm ? 'Cancel Completion' : 'Complete Event'}
              </LCARSButton>
            </ReadOnlyGuard>
          )}
        </ActionBar>

        <ContentArea>
          <div style={{ marginBottom: '20px' }}>
            <StatusBadge status={status}>{status.toUpperCase()}</StatusBadge>
          </div>

          {event.template?.description && (
            <Description>
              <strong>Template Description:</strong><br />
              {event.template.description}
            </Description>
          )}

          {showCompletionForm && !isCompleted && (
            <CompletionForm onSubmit={handleCompleteEvent}>
              <SectionTitle>Complete Maintenance Event</SectionTitle>
              <FormRow>
                <FormLabel>Actual Cost ($):</FormLabel>
                <FormInput
                  type="number"
                  step="0.01"
                  value={completionData.actualCost}
                  onChange={(e) => setCompletionData(prev => ({ ...prev, actualCost: e.target.value }))}
                  placeholder="Enter actual cost"
                />
              </FormRow>
              <FormRow>
                <FormLabel>Actual Time (minutes):</FormLabel>
                <FormInput
                  type="number"
                  value={completionData.actualTime}
                  onChange={(e) => setCompletionData(prev => ({ ...prev, actualTime: e.target.value }))}
                  placeholder="Enter time in minutes"
                />
              </FormRow>
              <FormRow>
                <FormLabel>Notes:</FormLabel>
                <FormTextarea
                  value={completionData.notes}
                  onChange={(e) => setCompletionData(prev => ({ ...prev, notes: e.target.value }))}
                  placeholder="Enter completion notes, observations, or issues encountered"
                />
              </FormRow>
              <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
                <LCARSButton type="button" onClick={() => setShowCompletionForm(false)}>
                  Cancel
                </LCARSButton>
                <LCARSButton 
                  type="submit" 
                  disabled={completeEventMutation.isPending}
                  variant="accent"
                >
                  {completeEventMutation.isPending ? 'Completing...' : 'Complete Event'}
                </LCARSButton>
              </div>
            </CompletionForm>
          )}

          <InfoGrid>
            <InfoSection>
              <SectionTitle>Event Information</SectionTitle>
              <InfoRow>
                <Label>Title:</Label>
                <Value>{event.template?.title || 'Unknown'}</Value>
              </InfoRow>
              <InfoRow>
                <Label>Boat:</Label>
                <Value>{event.template?.boat?.name || 'Unknown'}</Value>
              </InfoRow>
              <InfoRow>
                <Label>Component:</Label>
                <Value>{event.template?.component || 'General'}</Value>
              </InfoRow>
              <InfoRow>
                <Label>Due Date:</Label>
                <Value>{new Date(event.dueDate).toLocaleDateString()}</Value>
              </InfoRow>
              {isCompleted && (
                <InfoRow>
                  <Label>Completed:</Label>
                  <Value>{new Date(event.completedAt!).toLocaleDateString()}</Value>
                </InfoRow>
              )}
            </InfoSection>

            <InfoSection>
              <SectionTitle>Template Information</SectionTitle>
              <InfoRow>
                <Label>Recurrence:</Label>
                <Value>{formatRecurrence(event.template?.recurrence)}</Value>
              </InfoRow>
              <InfoRow>
                <Label>Est. Cost:</Label>
                <Value>{formatCurrency(event.template?.estimatedCost)}</Value>
              </InfoRow>
              <InfoRow>
                <Label>Est. Time:</Label>
                <Value>{formatTime(event.template?.estimatedTime)}</Value>
              </InfoRow>
            </InfoSection>

            {isCompleted && (
              <InfoSection>
                <SectionTitle>Completion Details</SectionTitle>
                <InfoRow>
                  <Label>Actual Cost:</Label>
                  <Value>{formatCurrency(event.actualCost)}</Value>
                </InfoRow>
                <InfoRow>
                  <Label>Actual Time:</Label>
                  <Value>{formatTime(event.actualTime)}</Value>
                </InfoRow>
                {event.notes && (
                  <div style={{ marginTop: '15px' }}>
                    <Label style={{ display: 'block', marginBottom: '5px' }}>Notes:</Label>
                    <div style={{ 
                      backgroundColor: '#333', 
                      padding: '10px', 
                      borderRadius: '4px',
                      whiteSpace: 'pre-wrap'
                    }}>
                      {event.notes}
                    </div>
                  </div>
                )}
              </InfoSection>
            )}
          </InfoGrid>
        </ContentArea>
      </MainContent>
    </Container>
  )
}