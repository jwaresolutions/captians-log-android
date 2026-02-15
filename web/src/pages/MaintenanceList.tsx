import { useState } from 'react'
import { Link } from 'react-router-dom'
import styled from 'styled-components'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSHeader } from '../components/lcars/LCARSHeader'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { LCARSDataDisplay } from '../components/lcars/LCARSDataDisplay'
import { LCARSColumn } from '../components/lcars/LCARSColumn'
import { useMaintenanceTemplates, useUpcomingMaintenanceEvents, useCompletedMaintenanceEvents } from '../hooks/useMaintenance'
import { useBoats } from '../hooks/useBoats'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'
import { MaintenanceTemplate, MaintenanceEvent } from '../types/api'

const Container = styled.div`
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 20px;
  height: 100vh;
  padding: 20px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`

const MainContent = styled.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: hidden;
`

const TabContainer = styled.div`
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
`

const TabButton = styled(LCARSButton)<{ active: boolean }>`
  background-color: ${props => props.active ? props.theme.colors.primary.neonCarrot : props.theme.colors.primary.lilac};
  opacity: ${props => props.active ? 1 : 0.7};
`

const ContentArea = styled(LCARSPanel)`
  flex: 1;
  overflow-y: auto;
  padding: 20px;
`

const ItemGrid = styled.div`
  display: grid;
  gap: 15px;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`

const ItemCard = styled(LCARSPanel)`
  padding: 15px;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background-color: ${props => props.theme.colors.primary.lilac}20;
  }
`

const ItemHeader = styled.div`
  display: flex;
  justify-content: between;
  align-items: flex-start;
  margin-bottom: 10px;
`

const ItemTitle = styled.h3`
  color: ${props => props.theme.colors.primary.neonCarrot};
  margin: 0;
  font-size: 18px;
  flex: 1;
`

const ItemMeta = styled.div`
  display: flex;
  flex-direction: column;
  gap: 5px;
  font-size: 14px;
  color: ${props => props.theme.colors.text.secondary};
`

const StatusBadge = styled.span<{ status: 'active' | 'inactive' | 'due' | 'overdue' | 'completed' }>`
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  text-transform: uppercase;
  background-color: ${props => {
    switch (props.status) {
      case 'active': return props.theme.colors.primary.anakiwa
      case 'inactive': return props.theme.colors.text.secondary
      case 'due': return props.theme.colors.primary.neonCarrot
      case 'overdue': return '#ff4444'
      case 'completed': return '#44ff44'
      default: return props.theme.colors.text.secondary
    }
  }};
  color: ${props => props.theme.colors.background};
`

const FilterContainer = styled.div`
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
`

const Select = styled.select`
  background-color: ${props => props.theme.colors.background};
  color: ${props => props.theme.colors.text.primary};
  border: 2px solid ${props => props.theme.colors.primary.neonCarrot};
  padding: 8px 12px;
  border-radius: 4px;
  font-family: inherit;
`

type TabType = 'templates' | 'upcoming' | 'completed'

export function MaintenanceList() {
  const [activeTab, setActiveTab] = useState<TabType>('templates')
  const [selectedBoatId, setSelectedBoatId] = useState<string>('')

  const { data: boats = [] } = useBoats()
  const { data: templates = [], isLoading: templatesLoading } = useMaintenanceTemplates(selectedBoatId || undefined)
  const { data: upcomingEvents = [], isLoading: upcomingLoading } = useUpcomingMaintenanceEvents(selectedBoatId || undefined)
  const { data: completedEvents = [], isLoading: completedLoading } = useCompletedMaintenanceEvents(selectedBoatId || undefined)

  const formatRecurrence = (recurrence?: { type: string; interval: number }) => {
    if (!recurrence) return 'One-time'
    const { type, interval } = recurrence
    const unit = interval === 1 ? type.slice(0, -1) : type
    return `Every ${interval} ${unit}`
  }

  const formatCurrency = (amount?: number) => {
    if (!amount) return 'N/A'
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount)
  }

  const formatTime = (minutes?: number) => {
    if (!minutes) return 'N/A'
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    if (hours > 0) {
      return `${hours}h ${mins}m`
    }
    return `${mins}m`
  }

  const getEventStatus = (event: MaintenanceEvent) => {
    if (event.completedAt) return 'completed'
    
    const dueDate = new Date(event.dueDate)
    const now = new Date()
    const daysDiff = Math.ceil((dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
    
    if (daysDiff < 0) return 'overdue'
    if (daysDiff <= 7) return 'due'
    return 'active'
  }

  const renderTemplates = () => (
    <ItemGrid>
      {templates.map((template: MaintenanceTemplate) => (
        <Link key={template.id} to={`/maintenance/templates/${template.id}`} style={{ textDecoration: 'none' }}>
          <ItemCard>
            <ItemHeader>
              <ItemTitle>{template.title}</ItemTitle>
              <StatusBadge status={template.isActive ? 'active' : 'inactive'}>
                {template.isActive ? 'Active' : 'Inactive'}
              </StatusBadge>
            </ItemHeader>
            <ItemMeta>
              <div><strong>Boat:</strong> {template.boat?.name || 'Unknown'}</div>
              {template.component && <div><strong>Component:</strong> {template.component}</div>}
              <div><strong>Recurrence:</strong> {formatRecurrence(template.recurrence)}</div>
              <div><strong>Est. Cost:</strong> {formatCurrency(template.estimatedCost)}</div>
              <div><strong>Est. Time:</strong> {formatTime(template.estimatedTime)}</div>
            </ItemMeta>
            {template.description && (
              <div style={{ marginTop: '10px', fontSize: '14px', color: '#ccc' }}>
                {template.description}
              </div>
            )}
          </ItemCard>
        </Link>
      ))}
    </ItemGrid>
  )

  const renderEvents = (events: MaintenanceEvent[], showCompleted = false) => (
    <ItemGrid>
      {events.map((event: MaintenanceEvent) => (
        <Link key={event.id} to={`/maintenance/events/${event.id}`} style={{ textDecoration: 'none' }}>
          <ItemCard>
            <ItemHeader>
              <ItemTitle>{event.template?.title || 'Unknown Task'}</ItemTitle>
              <StatusBadge status={getEventStatus(event)}>
                {getEventStatus(event)}
              </StatusBadge>
            </ItemHeader>
            <ItemMeta>
              <div><strong>Boat:</strong> {event.template?.boat?.name || 'Unknown'}</div>
              {event.template?.component && <div><strong>Component:</strong> {event.template.component}</div>}
              <div><strong>Due Date:</strong> {new Date(event.dueDate).toLocaleDateString()}</div>
              {showCompleted && event.completedAt && (
                <div><strong>Completed:</strong> {new Date(event.completedAt).toLocaleDateString()}</div>
              )}
              {event.actualCost && (
                <div><strong>Actual Cost:</strong> {formatCurrency(event.actualCost)}</div>
              )}
              {event.actualTime && (
                <div><strong>Actual Time:</strong> {formatTime(event.actualTime)}</div>
              )}
            </ItemMeta>
            {event.notes && (
              <div style={{ marginTop: '10px', fontSize: '14px', color: '#ccc' }}>
                {event.notes}
              </div>
            )}
          </ItemCard>
        </Link>
      ))}
    </ItemGrid>
  )

  const isLoading = templatesLoading || upcomingLoading || completedLoading

  return (
    <Container>
      <LCARSColumn>
        <LCARSDataDisplay label="System Status" value="OPERATIONAL" />
        <LCARSDataDisplay label="Active Templates" value={templates.filter(t => t.isActive).length.toString()} />
        <LCARSDataDisplay label="Upcoming Events" value={upcomingEvents.length.toString()} />
        <LCARSDataDisplay label="Overdue Events" value={upcomingEvents.filter(e => getEventStatus(e) === 'overdue').length.toString()} />
      </LCARSColumn>

      <MainContent>
        <LCARSHeader>Maintenance Management</LCARSHeader>
        
        <FilterContainer>
          <Select
            value={selectedBoatId}
            onChange={(e) => setSelectedBoatId(e.target.value)}
          >
            <option value="">All Boats</option>
            {boats.map(boat => (
              <option key={boat.id} value={boat.id}>{boat.name}</option>
            ))}
          </Select>
          
          <ReadOnlyGuard>
            <Link to="/maintenance/templates/new">
              <LCARSButton>New Template</LCARSButton>
            </Link>
          </ReadOnlyGuard>
        </FilterContainer>

        <TabContainer>
          <TabButton
            active={activeTab === 'templates'}
            onClick={() => setActiveTab('templates')}
          >
            Templates ({templates.length})
          </TabButton>
          <TabButton
            active={activeTab === 'upcoming'}
            onClick={() => setActiveTab('upcoming')}
          >
            Upcoming ({upcomingEvents.length})
          </TabButton>
          <TabButton
            active={activeTab === 'completed'}
            onClick={() => setActiveTab('completed')}
          >
            Completed ({completedEvents.length})
          </TabButton>
        </TabContainer>

        <ContentArea>
          {isLoading ? (
            <div style={{ textAlign: 'center', padding: '40px' }}>
              <div style={{ color: '#ff9966', fontSize: '18px' }}>Loading maintenance data...</div>
            </div>
          ) : (
            <>
              {activeTab === 'templates' && renderTemplates()}
              {activeTab === 'upcoming' && renderEvents(upcomingEvents)}
              {activeTab === 'completed' && renderEvents(completedEvents, true)}
            </>
          )}
        </ContentArea>
      </MainContent>
    </Container>
  )
}