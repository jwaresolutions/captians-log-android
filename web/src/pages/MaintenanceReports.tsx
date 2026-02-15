import React, { useMemo } from 'react'
import styled from 'styled-components'
import { 
  LCARSPanel, 
  LCARSDataDisplay, 
  LCARSProgressChart,
  LCARSHeader,
  LCARSAlert,
  LCARSButton
} from '../components/lcars'
import { useMaintenanceTemplates, useUpcomingMaintenanceEvents, useCompletedMaintenanceEvents } from '../hooks/useMaintenance'
import { useBoats } from '../hooks/useBoats'
// Removed unused imports

const PageContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.lg};
  max-width: 1200px;
  margin: 0 auto;
`

const StatsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${props => props.theme.spacing.md};
  margin-bottom: ${props => props.theme.spacing.lg};
`

const ChartsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: ${props => props.theme.spacing.lg};
  margin-bottom: ${props => props.theme.spacing.lg};
`

const FilterContainer = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.md};
  margin-bottom: ${props => props.theme.spacing.lg};
  flex-wrap: wrap;
`

const LoadingContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
`

const ErrorContainer = styled.div`
  margin-bottom: ${props => props.theme.spacing.lg};
`

const ReportTable = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.sm};
`

const TableRow = styled.div`
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr;
  gap: ${props => props.theme.spacing.md};
  padding: ${props => props.theme.spacing.sm};
  background-color: ${props => props.theme.colors.surface.dark};
  border: 1px solid ${props => props.theme.colors.surface.light};
  border-radius: ${props => props.theme.borderRadius.sm};
  align-items: center;

  &.header {
    background-color: ${props => props.theme.colors.primary.neonCarrot};
    color: ${props => props.theme.colors.text.inverse};
    font-weight: ${props => props.theme.typography.fontWeight.bold};
    text-transform: uppercase;
    letter-spacing: 1px;
    font-size: ${props => props.theme.typography.fontSize.sm};
  }
  
  &.overdue {
    border-color: ${props => props.theme.colors.status.error};
    background-color: rgba(255, 102, 102, 0.1);
  }
  
  &.due-soon {
    border-color: ${props => props.theme.colors.status.warning};
    background-color: rgba(255, 255, 102, 0.1);
  }
`

const TableCell = styled.div`
  font-family: ${props => props.theme.typography.fontFamily.monospace};
  font-size: ${props => props.theme.typography.fontSize.sm};
  
  &.text {
    font-family: ${props => props.theme.typography.fontFamily.primary};
  }
  
  &.status {
    font-weight: ${props => props.theme.typography.fontWeight.bold};
    text-transform: uppercase;
  }
`

interface MaintenanceReportsProps {}

export const MaintenanceReports: React.FC<MaintenanceReportsProps> = () => {
  const [selectedBoatId, setSelectedBoatId] = React.useState<string>('')
  
  const { data: boats, isLoading: boatsLoading } = useBoats()
  const { data: templates, isLoading: templatesLoading, error: templatesError } = useMaintenanceTemplates(selectedBoatId || undefined)
  const { data: upcomingEvents, isLoading: upcomingLoading, error: upcomingError } = useUpcomingMaintenanceEvents(selectedBoatId || undefined)
  const { data: completedEvents, isLoading: completedLoading, error: completedError } = useCompletedMaintenanceEvents(selectedBoatId || undefined)

  const isLoading = boatsLoading || templatesLoading || upcomingLoading || completedLoading
  const hasError = templatesError || upcomingError || completedError

  // Calculate statistics
  const stats = useMemo(() => {
    if (!templates || !upcomingEvents || !completedEvents) {
      return {
        totalTemplates: 0,
        activeTemplates: 0,
        upcomingCount: 0,
        overdueCount: 0,
        completedThisMonth: 0,
        totalCostThisMonth: 0,
        averageCost: 0,
        completionRate: 0
      }
    }

    const now = new Date()
    const thisMonth = new Date(now.getFullYear(), now.getMonth(), 1)
    
    const overdueCount = upcomingEvents.filter(event => new Date(event.dueDate) < now).length
    const completedThisMonth = completedEvents.filter(event => 
      event.completedAt && new Date(event.completedAt) >= thisMonth
    )
    
    const totalCostThisMonth = completedThisMonth.reduce((sum, event) => 
      sum + (event.actualCost || 0), 0
    )
    
    const completedWithCost = completedEvents.filter(event => event.actualCost && event.actualCost > 0)
    const averageCost = completedWithCost.length > 0 
      ? completedWithCost.reduce((sum, event) => sum + (event.actualCost || 0), 0) / completedWithCost.length
      : 0

    const totalScheduled = upcomingEvents.length + completedEvents.length
    const completionRate = totalScheduled > 0 ? (completedEvents.length / totalScheduled) * 100 : 0

    return {
      totalTemplates: templates.length,
      activeTemplates: templates.filter(t => t.isActive).length,
      upcomingCount: upcomingEvents.length,
      overdueCount,
      completedThisMonth: completedThisMonth.length,
      totalCostThisMonth,
      averageCost,
      completionRate
    }
  }, [templates, upcomingEvents, completedEvents])

  // Format upcoming events with status
  const upcomingEventsWithStatus = useMemo(() => {
    if (!upcomingEvents) return []
    
    const now = new Date()
    const oneWeek = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000)
    
    return upcomingEvents.map(event => {
      const dueDate = new Date(event.dueDate)
      let status = 'upcoming'
      let statusText = 'Upcoming'
      
      if (dueDate < now) {
        status = 'overdue'
        statusText = 'Overdue'
      } else if (dueDate <= oneWeek) {
        status = 'due-soon'
        statusText = 'Due Soon'
      }
      
      return {
        ...event,
        status,
        statusText,
        daysUntilDue: Math.ceil((dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
      }
    }).sort((a, b) => new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime())
  }, [upcomingEvents])

  if (isLoading) {
    return (
      <PageContainer>
        <LCARSHeader>Maintenance Reports</LCARSHeader>
        <LoadingContainer>
          <LCARSDataDisplay 
            label="System Status" 
            value="Loading Maintenance Data..." 
            valueColor="neonCarrot" 
            size="lg"
          />
        </LoadingContainer>
      </PageContainer>
    )
  }

  if (hasError) {
    return (
      <PageContainer>
        <LCARSHeader>Maintenance Reports</LCARSHeader>
        <ErrorContainer>
          <LCARSAlert type="error">
            Error loading maintenance data. Please check your connection and try again.
          </LCARSAlert>
        </ErrorContainer>
      </PageContainer>
    )
  }

  return (
    <PageContainer>
      <LCARSHeader>Maintenance Reports</LCARSHeader>

      {/* Boat Filter */}
      <FilterContainer>
        <LCARSButton
          variant={selectedBoatId === '' ? 'primary' : 'secondary'}
          onClick={() => setSelectedBoatId('')}
        >
          All Boats
        </LCARSButton>
        {boats?.map(boat => (
          <LCARSButton
            key={boat.id}
            variant={selectedBoatId === boat.id ? 'primary' : 'secondary'}
            onClick={() => setSelectedBoatId(boat.id)}
          >
            {boat.name}
          </LCARSButton>
        ))}
      </FilterContainer>

      {/* Overview Statistics */}
      <LCARSPanel title="Maintenance Overview" variant="primary">
        <StatsGrid>
          <LCARSDataDisplay
            label="Active Templates"
            value={stats.activeTemplates}
            valueColor="neonCarrot"
            size="lg"
          />
          <LCARSDataDisplay
            label="Upcoming Tasks"
            value={stats.upcomingCount}
            valueColor="anakiwa"
            size="lg"
          />
          <LCARSDataDisplay
            label="Overdue Tasks"
            value={stats.overdueCount}
            valueColor={stats.overdueCount > 0 ? 'neonCarrot' : 'success'}
            size="lg"
          />
          <LCARSDataDisplay
            label="Completed This Month"
            value={stats.completedThisMonth}
            valueColor="success"
            size="lg"
          />
        </StatsGrid>
      </LCARSPanel>

      {/* Cost Analysis */}
      <LCARSPanel title="Cost Analysis" variant="secondary">
        <StatsGrid>
          <LCARSDataDisplay
            label="Cost This Month"
            value={`$${stats.totalCostThisMonth.toFixed(2)}`}
            valueColor="lilac"
            size="lg"
          />
          <LCARSDataDisplay
            label="Average Cost Per Task"
            value={`$${stats.averageCost.toFixed(2)}`}
            valueColor="lilac"
            size="lg"
          />
          <LCARSDataDisplay
            label="Completion Rate"
            value={`${stats.completionRate.toFixed(1)}%`}
            valueColor="anakiwa"
            size="lg"
          />
        </StatsGrid>
      </LCARSPanel>

      {/* Progress Charts */}
      <ChartsGrid>
        <LCARSPanel title="Template Status" variant="primary">
          <LCARSProgressChart
            title="Active Templates"
            current={stats.activeTemplates}
            target={stats.totalTemplates}
            unit="templates"
            color="neonCarrot"
            size="md"
            showPercentage={true}
          />
        </LCARSPanel>

        <LCARSPanel title="Task Completion" variant="secondary">
          <LCARSProgressChart
            title="Completion Rate"
            current={stats.completionRate}
            target={100}
            unit="%"
            color="lilac"
            size="md"
            showPercentage={false}
          />
        </LCARSPanel>
      </ChartsGrid>

      {/* Upcoming Tasks Table */}
      {upcomingEventsWithStatus.length > 0 && (
        <LCARSPanel title="Upcoming Maintenance Tasks" variant="accent">
          <ReportTable>
            <TableRow className="header">
              <TableCell>Task</TableCell>
              <TableCell>Boat</TableCell>
              <TableCell>Due Date</TableCell>
              <TableCell>Days Until Due</TableCell>
              <TableCell>Status</TableCell>
            </TableRow>
            
            {upcomingEventsWithStatus.map(event => (
              <TableRow 
                key={event.id}
                className={event.status}
              >
                <TableCell className="text">
                  {event.template?.title || 'Unknown Task'}
                  {event.template?.component && (
                    <div style={{ fontSize: '0.8em', color: '#999' }}>
                      {event.template.component}
                    </div>
                  )}
                </TableCell>
                <TableCell className="text">
                  {event.template?.boat?.name || 'Unknown'}
                </TableCell>
                <TableCell>
                  {new Date(event.dueDate).toLocaleDateString()}
                </TableCell>
                <TableCell>
                  {event.daysUntilDue > 0 ? `${event.daysUntilDue} days` : `${Math.abs(event.daysUntilDue)} days ago`}
                </TableCell>
                <TableCell className="status">
                  {event.statusText}
                </TableCell>
              </TableRow>
            ))}
          </ReportTable>
        </LCARSPanel>
      )}

      {/* Recent Completions */}
      {completedEvents && completedEvents.length > 0 && (
        <LCARSPanel title="Recent Completions" variant="secondary">
          <ReportTable>
            <TableRow className="header">
              <TableCell>Task</TableCell>
              <TableCell>Boat</TableCell>
              <TableCell>Completed</TableCell>
              <TableCell>Cost</TableCell>
              <TableCell>Time</TableCell>
            </TableRow>
            
            {completedEvents.slice(0, 10).map(event => (
              <TableRow key={event.id}>
                <TableCell className="text">
                  {event.template?.title || 'Unknown Task'}
                </TableCell>
                <TableCell className="text">
                  {event.template?.boat?.name || 'Unknown'}
                </TableCell>
                <TableCell>
                  {event.completedAt ? new Date(event.completedAt).toLocaleDateString() : 'N/A'}
                </TableCell>
                <TableCell>
                  {event.actualCost ? `$${event.actualCost.toFixed(2)}` : 'N/A'}
                </TableCell>
                <TableCell>
                  {event.actualTime ? `${event.actualTime}h` : 'N/A'}
                </TableCell>
              </TableRow>
            ))}
          </ReportTable>
        </LCARSPanel>
      )}
    </PageContainer>
  )
}