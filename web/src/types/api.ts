// API Response Types
export interface ApiResponse<T = unknown> {
  data: T
  message?: string
  success: boolean
}

export interface ApiError {
  message: string
  code?: string
  details?: unknown
}

// Authentication Types
export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  user: User
  token: string
}

export interface User {
  id: string
  username: string
  role?: string
  createdAt: string
  updatedAt: string
}

// Boat Types
export interface Boat {
  id: string
  name: string
  enabled: boolean
  isActive: boolean
  metadata?: Record<string, unknown>
  createdAt: string
  updatedAt: string
}

// Trip Types
export interface Trip {
  id: string
  boatId: string
  startTime: string
  endTime: string
  waterType: 'inland' | 'coastal' | 'offshore'
  role: 'captain' | 'crew' | 'observer'
  timezone?: string
  gpsPoints: GPSPoint[]
  statistics?: TripStatistics
  // Flat statistics fields (for backward compatibility)
  durationSeconds?: number
  distanceMeters?: number
  averageSpeedKnots?: number
  maxSpeedKnots?: number
  manualData?: ManualData
  notes: Note[]
  photos: Photo[]
  createdAt: string
  updatedAt: string
  boat?: {
    id: string
    name: string
    enabled: boolean
    isActive: boolean
  }
}

export interface GPSPoint {
  id: string
  tripId: string
  latitude: number
  longitude: number
  altitude?: number
  accuracy?: number
  speed?: number
  heading?: number
  timestamp: string
}

export interface TripStatistics {
  durationSeconds: number
  distanceMeters: number
  averageSpeedKnots: number
  maxSpeedKnots: number
  stopPoints: StopPoint[]
}

export interface StopPoint {
  latitude: number
  longitude: number
  startTime: string
  endTime: string
  durationSeconds: number
}

export interface ManualData {
  engineHours?: number
  fuelConsumed?: number
  weatherConditions?: string
  numberOfPassengers?: number
  destination?: string
}

// Note Types
export interface Note {
  id: string
  content: string
  type: 'general' | 'boat' | 'trip'
  boatId?: string
  tripId?: string
  tags: string[]
  createdAt: string
  updatedAt: string
}

// Todo Types
export interface TodoList {
  id: string
  title: string
  type: 'general' | 'boat'
  boatId?: string
  items: TodoItem[]
  createdAt: string
  updatedAt: string
}

export interface TodoItem {
  id: string
  listId: string
  content: string
  completed: boolean
  completedAt?: string
  createdAt: string
  updatedAt: string
}

// Maintenance Types
export interface MaintenanceTemplate {
  id: string
  boatId: string
  title: string
  description?: string
  component?: string
  recurrence?: RecurrenceSchedule
  estimatedCost?: number
  estimatedTime?: number
  isActive: boolean
  createdAt: string
  updatedAt: string
  boat?: {
    id: string
    name: string
  }
}

export interface MaintenanceEvent {
  id: string
  templateId: string
  dueDate: string
  completedAt?: string
  actualCost?: number
  actualTime?: number
  notes?: string
  createdAt: string
  updatedAt: string
  template?: MaintenanceTemplate & {
    boat: {
      id: string
      name: string
    }
  }
}

export interface RecurrenceSchedule {
  type: 'days' | 'weeks' | 'months' | 'years' | 'engine_hours'
  interval: number
}

// Location Types
export interface MarkedLocation {
  id: string
  name: string
  latitude: number
  longitude: number
  category: 'fishing' | 'marina' | 'anchorage' | 'hazard' | 'other'
  notes?: string
  tags: string[]
  createdAt: string
  updatedAt: string
}

// Photo Types
export interface Photo {
  id: string
  entityType: 'trip' | 'maintenance' | 'note'
  entityId: string
  originalPath: string
  webOptimizedPath: string
  mimeType: string
  sizeBytes: number
  metadata?: {
    width: number
    height: number
    takenAt?: string
  }
  createdAt: string
}

// License Progress Types
export interface LicenseProgress {
  totalDays: number
  daysInLast3Years: number
  totalHours: number
  hoursInLast3Years: number
  daysRemaining360: number
  daysRemaining90In3Years: number
  estimatedCompletion360: string | null
  estimatedCompletion90In3Years: string | null
  averageDaysPerMonth: number
}

// Notification Types
export interface Notification {
  id: string
  type: 'maintenance_due' | 'system'
  title: string
  message: string
  entityType?: string
  entityId?: string
  read: boolean
  createdAt: string
}

export interface ViewerSettings {
  exists: boolean
  enabled: boolean
  username: string
}