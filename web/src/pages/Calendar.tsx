import React, { useState, useEffect } from 'react'
import styled from 'styled-components'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { LCARSHeader } from '../components/lcars/LCARSHeader'
import { LCARSDataDisplay } from '../components/lcars/LCARSDataDisplay'
import { useTrips } from '../hooks/useTrips'
import { useUpcomingMaintenanceEvents } from '../hooks/useMaintenance'
import { Trip, MaintenanceEvent } from '../types/api'

const CalendarContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.lg};
  max-width: 1400px;
  margin: 0 auto;
`

const CalendarControls = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${props => props.theme.spacing.md};
`

const MonthNavigation = styled.div`
  display: flex;
  align-items: center;
  gap: ${props => props.theme.spacing.md};
`

const MonthTitle = styled.h2`
  color: ${props => props.theme.colors.primary.neonCarrot};
  font-size: ${props => props.theme.typography.fontSize.xl};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 2px;
  margin: 0;
  min-width: 200px;
  text-align: center;
`

const CalendarGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 1px;
  background-color: ${props => props.theme.colors.primary.anakiwa};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
`

const CalendarHeader = styled.div`
  background-color: ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.text.primary};
  padding: ${props => props.theme.spacing.sm};
  text-align: center;
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
  font-size: ${props => props.theme.typography.fontSize.sm};
`

const CalendarDay = styled.div<{ 
  $isCurrentMonth: boolean
  $isToday: boolean
  $hasEvents: boolean
}>`
  background-color: ${props => props.theme.colors.surface.dark};
  min-height: 120px;
  padding: ${props => props.theme.spacing.xs};
  display: flex;
  flex-direction: column;
  position: relative;
  
  ${props => !props.$isCurrentMonth && `
    background-color: ${props.theme.colors.surface.medium};
    opacity: 0.5;
  `}
  
  ${props => props.$isToday && `
    border: 2px solid ${props.theme.colors.primary.neonCarrot};
    background-color: ${props.theme.colors.primary.neonCarrot}10;
  `}

  ${props => props.$hasEvents && `
    border-left: 4px solid ${props.theme.colors.primary.lilac};
  `}
`

const DayNumber = styled.div<{ $isToday: boolean }>`
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  font-size: ${props => props.theme.typography.fontSize.sm};
  color: ${props => props.$isToday
    ? props.theme.colors.primary.neonCarrot
    : props.theme.colors.text.primary
  };
  margin-bottom: ${props => props.theme.spacing.xs};
`

const EventList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
`

const EventItem = styled.div<{ $type: 'trip' | 'maintenance' }>`
  background-color: ${props => props.$type === 'trip'
    ? props.theme.colors.primary.anakiwa
    : props.theme.colors.primary.lilac
  };
  color: ${props => props.theme.colors.text.primary};
  padding: 2px 4px;
  font-size: 10px;
  border-radius: 2px;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  cursor: pointer;

  &:hover {
    opacity: 0.8;
  }
`

const StatsPanel = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${props => props.theme.spacing.md};
  margin-bottom: ${props => props.theme.spacing.lg};
`

const Legend = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.md};
  align-items: center;
  margin-bottom: ${props => props.theme.spacing.md};
`

const LegendItem = styled.div<{ $color: string }>`
  display: flex;
  align-items: center;
  gap: ${props => props.theme.spacing.xs};
  
  &::before {
    content: '';
    width: 12px;
    height: 12px;
    background-color: ${props => props.$color};
    border-radius: 2px;
  }
`

const DAYS_OF_WEEK = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
const MONTHS = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'
]

interface CalendarEvent {
  id: string
  title: string
  date: Date
  type: 'trip' | 'maintenance'
  data: Trip | MaintenanceEvent
}

export const Calendar: React.FC = () => {
  const [currentDate, setCurrentDate] = useState(new Date())
  const [calendarEvents, setCalendarEvents] = useState<CalendarEvent[]>([])
  
  const { data: trips, isLoading: tripsLoading } = useTrips()
  const { data: maintenanceEvents, isLoading: maintenanceLoading } = useUpcomingMaintenanceEvents()

  useEffect(() => {
    const events: CalendarEvent[] = []
    
    // Add trips to calendar
    if (trips) {
      trips.forEach(trip => {
        events.push({
          id: `trip-${trip.id}`,
          title: `Trip: ${trip.boat?.name || 'Unknown Boat'}`,
          date: new Date(trip.startTime),
          type: 'trip',
          data: trip,
        })
      })
    }
    
    // Add maintenance events to calendar
    if (maintenanceEvents) {
      maintenanceEvents.forEach(event => {
        events.push({
          id: `maintenance-${event.id}`,
          title: `Maintenance: ${event.template?.title || 'Unknown Task'}`,
          date: new Date(event.dueDate),
          type: 'maintenance',
          data: event,
        })
      })
    }
    
    setCalendarEvents(events)
  }, [trips, maintenanceEvents])

  const navigateMonth = (direction: 'prev' | 'next') => {
    setCurrentDate(prev => {
      const newDate = new Date(prev)
      if (direction === 'prev') {
        newDate.setMonth(prev.getMonth() - 1)
      } else {
        newDate.setMonth(prev.getMonth() + 1)
      }
      return newDate
    })
  }

  const goToToday = () => {
    setCurrentDate(new Date())
  }

  const getDaysInMonth = (date: Date) => {
    const year = date.getFullYear()
    const month = date.getMonth()
    const firstDay = new Date(year, month, 1)
    const lastDay = new Date(year, month + 1, 0)
    const daysInMonth = lastDay.getDate()
    const startingDayOfWeek = firstDay.getDay()
    
    const days: Date[] = []
    
    // Add days from previous month
    for (let i = startingDayOfWeek - 1; i >= 0; i--) {
      const prevDate = new Date(year, month, -i)
      days.push(prevDate)
    }
    
    // Add days from current month
    for (let day = 1; day <= daysInMonth; day++) {
      days.push(new Date(year, month, day))
    }
    
    // Add days from next month to fill the grid
    const remainingDays = 42 - days.length // 6 rows × 7 days
    for (let day = 1; day <= remainingDays; day++) {
      days.push(new Date(year, month + 1, day))
    }
    
    return days
  }

  const getEventsForDate = (date: Date): CalendarEvent[] => {
    return calendarEvents.filter(event => {
      const eventDate = new Date(event.date)
      return eventDate.toDateString() === date.toDateString()
    })
  }

  const isToday = (date: Date): boolean => {
    const today = new Date()
    return date.toDateString() === today.toDateString()
  }

  const isCurrentMonth = (date: Date): boolean => {
    return date.getMonth() === currentDate.getMonth()
  }

  const days = getDaysInMonth(currentDate)
  const currentMonthTrips = trips?.filter(trip => {
    const tripDate = new Date(trip.startTime)
    return tripDate.getMonth() === currentDate.getMonth() && 
           tripDate.getFullYear() === currentDate.getFullYear()
  }) || []
  
  const currentMonthMaintenance = maintenanceEvents?.filter(event => {
    const eventDate = new Date(event.dueDate)
    return eventDate.getMonth() === currentDate.getMonth() && 
           eventDate.getFullYear() === currentDate.getFullYear()
  }) || []

  return (
    <CalendarContainer>
      <LCARSHeader>Mission Calendar</LCARSHeader>
      
      {/* Calendar Statistics */}
      <StatsPanel>
        <LCARSDataDisplay
          label="Current Month Trips"
          value={currentMonthTrips.length.toString()}
          valueColor="anakiwa"
        />
        <LCARSDataDisplay
          label="Upcoming Maintenance"
          value={currentMonthMaintenance.length.toString()}
          valueColor="lilac"
        />
        <LCARSDataDisplay
          label="Total Events"
          value={(currentMonthTrips.length + currentMonthMaintenance.length).toString()}
          valueColor="neonCarrot"
        />
      </StatsPanel>

      <LCARSPanel title="Calendar View">
        {/* Calendar Controls */}
        <CalendarControls>
          <MonthNavigation>
            <LCARSButton 
              onClick={() => navigateMonth('prev')}
              variant="secondary"
              size="sm"
            >
              ← Previous
            </LCARSButton>
            <MonthTitle>
              {MONTHS[currentDate.getMonth()]} {currentDate.getFullYear()}
            </MonthTitle>
            <LCARSButton 
              onClick={() => navigateMonth('next')}
              variant="secondary"
              size="sm"
            >
              Next →
            </LCARSButton>
          </MonthNavigation>
          
          <LCARSButton onClick={goToToday} size="sm">
            Today
          </LCARSButton>
        </CalendarControls>

        {/* Legend */}
        <Legend>
          <LegendItem $color="#6688CC">
            Trips
          </LegendItem>
          <LegendItem $color="#CC99CC">
            Maintenance
          </LegendItem>
        </Legend>

        {/* Calendar Grid */}
        <CalendarGrid>
          {/* Day headers */}
          {DAYS_OF_WEEK.map(day => (
            <CalendarHeader key={day}>
              {day}
            </CalendarHeader>
          ))}
          
          {/* Calendar days */}
          {days.map((date, index) => {
            const dayEvents = getEventsForDate(date)
            return (
              <CalendarDay
                key={index}
                $isCurrentMonth={isCurrentMonth(date)}
                $isToday={isToday(date)}
                $hasEvents={dayEvents.length > 0}
              >
                <DayNumber $isToday={isToday(date)}>
                  {date.getDate()}
                </DayNumber>
                <EventList>
                  {dayEvents.slice(0, 3).map(event => (
                    <EventItem
                      key={event.id}
                      $type={event.type}
                      title={event.title}
                    >
                      {event.title}
                    </EventItem>
                  ))}
                  {dayEvents.length > 3 && (
                    <EventItem $type="trip">
                      +{dayEvents.length - 3} more
                    </EventItem>
                  )}
                </EventList>
              </CalendarDay>
            )
          })}
        </CalendarGrid>

        {(tripsLoading || maintenanceLoading) && (
          <div style={{ 
            textAlign: 'center', 
            padding: '20px', 
            color: '#6688CC' 
          }}>
            Loading calendar data...
          </div>
        )}
      </LCARSPanel>
    </CalendarContainer>
  )
}