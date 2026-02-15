import { useParams, Link, useNavigate } from 'react-router-dom'
import styled from 'styled-components'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSHeader } from '../components/lcars/LCARSHeader'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { LCARSDataDisplay } from '../components/lcars/LCARSDataDisplay'
import { LCARSColumn } from '../components/lcars/LCARSColumn'
import { LCARSAlert } from '../components/lcars/LCARSAlert'
import { useMaintenanceTemplate, useDeleteMaintenanceTemplate } from '../hooks/useMaintenance'
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

const StatusBadge = styled.span<{ active: boolean }>`
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  text-transform: uppercase;
  background-color: ${props => props.active ? props.theme.colors.primary.anakiwa : props.theme.colors.text.secondary};
  color: ${props => props.theme.colors.background};
`

const Description = styled.div`
  background-color: ${props => props.theme.colors.background}40;
  padding: 15px;
  border-radius: 4px;
  border-left: 4px solid ${props => props.theme.colors.primary.neonCarrot};
  margin-bottom: 20px;
  line-height: 1.5;
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

export function MaintenanceTemplateDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  
  const { data: template, isLoading, error } = useMaintenanceTemplate(id!)
  const deleteTemplateMutation = useDeleteMaintenanceTemplate()

  const handleDelete = async () => {
    if (!template) return
    
    const confirmed = window.confirm(
      `Are you sure you want to delete the template "${template.title}"? This will also delete all future maintenance events for this template.`
    )
    
    if (confirmed) {
      try {
        await deleteTemplateMutation.mutateAsync(template.id)
        navigate('/maintenance')
      } catch (error) {
        console.error('Failed to delete template:', error)
        alert('Failed to delete template. Please try again.')
      }
    }
  }

  const formatRecurrence = (recurrence?: { type: string; interval: number }) => {
    if (!recurrence) return 'One-time'
    const { type, interval } = recurrence
    const unit = interval === 1 ? type.slice(0, -1) : type
    return `Every ${interval} ${unit}`
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

  if (isLoading) {
    return (
      <Container>
        <LCARSColumn>
          <LCARSDataDisplay label="Status" value="LOADING" />
        </LCARSColumn>
        <MainContent>
          <LCARSHeader>Maintenance Template</LCARSHeader>
          <ContentArea>
            <LoadingContainer>Loading template details...</LoadingContainer>
          </ContentArea>
        </MainContent>
      </Container>
    )
  }

  if (error || !template) {
    return (
      <Container>
        <LCARSColumn>
          <LCARSDataDisplay label="Status" value="ERROR" />
        </LCARSColumn>
        <MainContent>
          <LCARSHeader>Maintenance Template</LCARSHeader>
          <ContentArea>
            <ErrorContainer>
              <LCARSAlert type="error">
                Template not found or failed to load.
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

  return (
    <Container>
      <LCARSColumn>
        <LCARSDataDisplay label="Template Status" value={template.isActive ? "ACTIVE" : "INACTIVE"} />
        <LCARSDataDisplay label="Boat" value={template.boat?.name || "Unknown"} />
        <LCARSDataDisplay label="Component" value={template.component || "General"} />
        <LCARSDataDisplay label="Recurrence" value={formatRecurrence(template.recurrence)} />
      </LCARSColumn>

      <MainContent>
        <LCARSHeader>{template.title}</LCARSHeader>
        
        <ActionBar>
          <Link to="/maintenance">
            <LCARSButton>Back to List</LCARSButton>
          </Link>
          <ReadOnlyGuard>
            <Link to={`/maintenance/templates/${template.id}/edit`}>
              <LCARSButton>Edit Template</LCARSButton>
            </Link>
          </ReadOnlyGuard>
          <ReadOnlyGuard>
            <LCARSButton
              onClick={handleDelete}
              disabled={deleteTemplateMutation.isPending}
              variant="danger"
            >
              {deleteTemplateMutation.isPending ? 'Deleting...' : 'Delete Template'}
            </LCARSButton>
          </ReadOnlyGuard>
        </ActionBar>

        <ContentArea>
          {template.description && (
            <Description>
              <strong>Description:</strong><br />
              {template.description}
            </Description>
          )}

          <InfoGrid>
            <InfoSection>
              <SectionTitle>Basic Information</SectionTitle>
              <InfoRow>
                <Label>Title:</Label>
                <Value>{template.title}</Value>
              </InfoRow>
              <InfoRow>
                <Label>Boat:</Label>
                <Value>{template.boat?.name || 'Unknown'}</Value>
              </InfoRow>
              <InfoRow>
                <Label>Component:</Label>
                <Value>{template.component || 'General'}</Value>
              </InfoRow>
              <InfoRow>
                <Label>Status:</Label>
                <Value>
                  <StatusBadge active={template.isActive}>
                    {template.isActive ? 'Active' : 'Inactive'}
                  </StatusBadge>
                </Value>
              </InfoRow>
            </InfoSection>

            <InfoSection>
              <SectionTitle>Schedule & Estimates</SectionTitle>
              <InfoRow>
                <Label>Recurrence:</Label>
                <Value>{formatRecurrence(template.recurrence)}</Value>
              </InfoRow>
              <InfoRow>
                <Label>Estimated Cost:</Label>
                <Value>{formatCurrency(template.estimatedCost)}</Value>
              </InfoRow>
              <InfoRow>
                <Label>Estimated Time:</Label>
                <Value>{formatTime(template.estimatedTime)}</Value>
              </InfoRow>
            </InfoSection>

            <InfoSection>
              <SectionTitle>Timestamps</SectionTitle>
              <InfoRow>
                <Label>Created:</Label>
                <Value>{new Date(template.createdAt).toLocaleString()}</Value>
              </InfoRow>
              <InfoRow>
                <Label>Updated:</Label>
                <Value>{new Date(template.updatedAt).toLocaleString()}</Value>
              </InfoRow>
            </InfoSection>
          </InfoGrid>

          <div style={{ marginTop: '30px' }}>
            <SectionTitle>Related Events</SectionTitle>
            <p style={{ color: '#ccc', marginBottom: '20px' }}>
              View upcoming and completed maintenance events generated from this template.
            </p>
            <div style={{ display: 'flex', gap: '10px' }}>
              <Link to={`/maintenance?tab=upcoming&template=${template.id}`}>
                <LCARSButton>View Upcoming Events</LCARSButton>
              </Link>
              <Link to={`/maintenance?tab=completed&template=${template.id}`}>
                <LCARSButton>View Completed Events</LCARSButton>
              </Link>
            </div>
          </div>
        </ContentArea>
      </MainContent>
    </Container>
  )
}