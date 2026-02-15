import axios, { AxiosInstance, AxiosResponse } from 'axios'
import {
  ApiResponse,
  ApiError,
  Boat,
  Trip,
  GPSPoint,
  ManualData,
  LicenseProgress,
  Note,
  TodoList,
  TodoItem,
  MaintenanceTemplate,
  MaintenanceEvent,
  RecurrenceSchedule,
  MarkedLocation,
  Photo,
  Notification,
  ViewerSettings
} from '../types/api'

class ApiService {
  private client: AxiosInstance

  constructor() {
    const baseURL = import.meta.env.VITE_API_BASE_URL || '/api/v1'

    // Clear any stale dynamic URL from localStorage
    localStorage.removeItem('api_base_url')

    this.client = axios.create({
      baseURL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    })

    // Request interceptor to add auth token
    this.client.interceptors.request.use(
      (config) => {
        const token = this.getAuthToken()
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        return config
      },
      (error) => {
        return Promise.reject(error)
      }
    )

    // Response interceptor for error handling
    this.client.interceptors.response.use(
      (response: AxiosResponse<ApiResponse>) => {
        return response
      },
      (error) => {
        // Handle network errors
        if (!error.response) {
          const networkError: ApiError = {
            message: error.code === 'ECONNABORTED' 
              ? 'Request timeout. Please try again.' 
              : 'Network error. Please check your internet connection.',
            code: 'NETWORK_ERROR',
            details: { originalError: error.message }
          }
          return Promise.reject(networkError)
        }

        // Handle HTTP errors
        const apiError: ApiError = {
          message: this.getErrorMessage(error),
          code: error.response.status.toString(),
          details: error.response.data,
        }

        // Handle specific status codes
        switch (error.response.status) {
          case 401:
            // Don't overwrite message for login attempts - they have their own error messages
            if (!error.config?.url?.includes('/auth/login')) {
              this.clearAuthToken()
              apiError.message = 'Your session has expired. Please log in again.'
            }
            // For login, keep the original error message from getErrorMessage()
            break
          case 403:
            apiError.message = 'You don\'t have permission to perform this action.'
            break
          case 404:
            apiError.message = 'The requested resource was not found.'
            break
          case 409:
            apiError.message = error.response.data?.error?.message || 'A conflict occurred. The resource may have been modified.'
            break
          case 422:
            apiError.message = error.response.data?.error?.message || 'Invalid data provided.'
            break
          case 429:
            apiError.message = 'Too many requests. Please wait a moment and try again.'
            break
          case 500:
            apiError.message = 'Server error. Please try again later.'
            break
          case 503:
            apiError.message = 'Service temporarily unavailable. Please try again later.'
            break
        }

        // Log error for debugging
        console.error('API Error:', {
          status: error.response.status,
          message: apiError.message,
          url: error.config?.url,
          method: error.config?.method
        })

        return Promise.reject(apiError)
      }
    )
  }

  // Auth token management
  private getAuthToken(): string | null {
    return localStorage.getItem('auth_token')
  }

  private setAuthToken(token: string): void {
    localStorage.setItem('auth_token', token)
  }

  private clearAuthToken(): void {
    localStorage.removeItem('auth_token')
  }

  private getErrorMessage(error: any): string {
    // Try to extract user-friendly error message
    if (error.response?.data?.error?.message) {
      return error.response.data.error.message
    }
    
    if (error.response?.data?.message) {
      return error.response.data.message
    }
    
    if (error.message) {
      return error.message
    }
    
    return 'An unexpected error occurred'
  }

  // Retry mechanism for failed requests
  async retryRequest<T>(
    requestFn: () => Promise<T>,
    maxRetries: number = 3,
    delay: number = 1000
  ): Promise<T> {
    let lastError: any
    
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        return await requestFn()
      } catch (error: any) {
        lastError = error

        // Don't retry on client errors (4xx) except 408 (timeout) and 429 (rate limit)
        if (error.code && error.code.startsWith('4') &&
            error.code !== '408' && error.code !== '429') {
          throw error
        }

        if (attempt < maxRetries) {
          await new Promise(resolve => setTimeout(resolve, delay * attempt))
        }
      }
    }
    
    throw lastError
  }

  // Update the base URL dynamically
  updateBaseUrl(serverUrl: string): void {
    // If the serverUrl points to the same host we're running on, use relative URL
    // to leverage the Vite dev proxy
    let apiBaseUrl: string

    try {
      const serverOrigin = new URL(serverUrl).origin
      const currentOrigin = window.location.origin

      // If server is on same origin or common local dev setup, use relative path
      if (serverOrigin === currentOrigin ||
          (window.location.hostname === 'localhost' && new URL(serverUrl).hostname === 'localhost')) {
        apiBaseUrl = '/api/v1'
      } else {
        // Different host — use absolute URL
        apiBaseUrl = serverUrl.replace(/\/$/, '')
        if (!apiBaseUrl.endsWith('/api/v1')) {
          apiBaseUrl += '/api/v1'
        }
      }
    } catch {
      // Invalid URL, try using as-is with /api/v1 appended
      apiBaseUrl = serverUrl.replace(/\/$/, '')
      if (!apiBaseUrl.endsWith('/api/v1')) {
        apiBaseUrl += '/api/v1'
      }
    }

    // Update the axios client base URL
    this.client.defaults.baseURL = apiBaseUrl

    console.log('API base URL updated to:', apiBaseUrl)
  }

  // Check if the API is reachable
  async checkConnectivity(): Promise<boolean> {
    try {
      await this.healthCheck()
      return true
    } catch {
      return false
    }
  }

  // Generic request methods
  async get<T>(url: string, params?: Record<string, unknown>): Promise<T> {
    const response = await this.client.get<ApiResponse<T>>(url, { params })
    return response.data.data
  }

  async post<T>(url: string, data?: unknown): Promise<T> {
    const response = await this.client.post<ApiResponse<T>>(url, data)
    return response.data.data
  }

  async put<T>(url: string, data?: unknown): Promise<T> {
    const response = await this.client.put<ApiResponse<T>>(url, data)
    return response.data.data
  }

  async patch<T>(url: string, data?: unknown): Promise<T> {
    const response = await this.client.patch<ApiResponse<T>>(url, data)
    return response.data.data
  }

  async delete<T>(url: string): Promise<T> {
    const response = await this.client.delete<ApiResponse<T>>(url)
    return response.data.data
  }

  // Authentication methods
  async login(username: string, password: string) {
    // Login endpoint returns data directly, not wrapped in ApiResponse
    const response = await this.client.post('/auth/login', {
      username,
      password,
    })
    
    const loginData = response.data
    this.setAuthToken(loginData.token)
    return loginData
  }

  async logout() {
    try {
      await this.post('/auth/logout')
    } finally {
      this.clearAuthToken()
    }
  }

  async changePassword(currentPassword: string, newPassword: string): Promise<void> {
    await this.post('/auth/change-password', {
      currentPassword,
      newPassword,
    })
    // Password change invalidates all tokens, so clear the current one
    this.clearAuthToken()
  }

  // Health check
  async healthCheck(): Promise<{ status: string }> {
    // Health endpoint is proxied through Vite dev server
    const response = await axios.get('/health')
    return response.data
  }

  // Boat API methods
  async getBoats(): Promise<Boat[]> {
    return this.get('/boats')
  }

  async getBoat(id: string): Promise<Boat> {
    return this.get(`/boats/${id}`)
  }

  async createBoat(data: { name: string; metadata?: Record<string, unknown> }): Promise<Boat> {
    return this.post('/boats', data)
  }

  async updateBoat(id: string, data: Partial<Boat>): Promise<Boat> {
    return this.put(`/boats/${id}`, data)
  }

  async toggleBoatStatus(id: string, enabled: boolean): Promise<Boat> {
    return this.patch(`/boats/${id}/status`, { enabled })
  }

  async setActiveBoat(id: string): Promise<void> {
    return this.patch(`/boats/${id}/active`)
  }

  // Trip API methods
  async getTrips(filters?: { boatId?: string; startDate?: string; endDate?: string }): Promise<Trip[]> {
    return this.get('/trips', filters)
  }

  async getTrip(id: string): Promise<Trip> {
    return this.get(`/trips/${id}`)
  }

  async createTrip(data: {
    boatId: string
    startTime: string
    endTime: string
    waterType: 'inland' | 'coastal' | 'offshore'
    role: 'captain' | 'crew' | 'observer'
    gpsPoints: Omit<GPSPoint, 'id' | 'tripId'>[]
  }): Promise<Trip> {
    return this.post('/trips', data)
  }

  async updateTrip(id: string, data: Partial<Trip>): Promise<Trip> {
    return this.put(`/trips/${id}`, data)
  }

  async addManualData(tripId: string, data: ManualData): Promise<Trip> {
    return this.patch(`/trips/${tripId}/manual-data`, data)
  }

  // Captain's Log API methods
  async getLicenseProgress(): Promise<LicenseProgress> {
    return this.get('/captain-log/progress')
  }

  // Notes API methods
  async getNotes(filters?: { type?: string; boatId?: string; tripId?: string; tags?: string[] }): Promise<Note[]> {
    return this.get('/notes', filters)
  }

  async getNote(id: string): Promise<Note> {
    return this.get(`/notes/${id}`)
  }

  async createNote(data: {
    content: string
    type: 'general' | 'boat' | 'trip'
    boatId?: string
    tripId?: string
    tags?: string[]
  }): Promise<Note> {
    return this.post('/notes', data)
  }

  async updateNote(id: string, data: Partial<Note>): Promise<Note> {
    return this.put(`/notes/${id}`, data)
  }

  async deleteNote(id: string): Promise<void> {
    return this.delete(`/notes/${id}`)
  }

  // Todo API methods
  async getTodoLists(boatId?: string): Promise<TodoList[]> {
    return this.get('/todos', boatId ? { boatId } : undefined)
  }

  async getTodoList(id: string): Promise<TodoList> {
    return this.get(`/todos/${id}`)
  }

  async createTodoList(data: {
    title: string
    type: 'general' | 'boat'
    boatId?: string
  }): Promise<TodoList> {
    return this.post('/todos', data)
  }

  async updateTodoList(id: string, data: Partial<TodoList>): Promise<TodoList> {
    return this.put(`/todos/${id}`, data)
  }

  async deleteTodoList(id: string): Promise<void> {
    return this.delete(`/todos/${id}`)
  }

  async addTodoItem(listId: string, content: string): Promise<TodoItem> {
    return this.post(`/todos/${listId}/items`, { content })
  }

  async toggleTodoItem(itemId: string): Promise<TodoItem> {
    return this.patch(`/todos/items/${itemId}/complete`)
  }

  async updateTodoItem(itemId: string, data: { content?: string; completed?: boolean }): Promise<TodoItem> {
    return this.put(`/todos/items/${itemId}`, data)
  }

  async deleteTodoItem(itemId: string): Promise<void> {
    return this.delete(`/todos/items/${itemId}`)
  }

  // Maintenance Template API methods
  async getMaintenanceTemplates(boatId?: string): Promise<MaintenanceTemplate[]> {
    return this.get('/maintenance/templates', boatId ? { boatId } : undefined)
  }

  async getMaintenanceTemplate(id: string): Promise<MaintenanceTemplate> {
    return this.get(`/maintenance/templates/${id}`)
  }

  async createMaintenanceTemplate(data: {
    boatId: string
    title: string
    description?: string
    component?: string
    recurrence?: RecurrenceSchedule
    estimatedCost?: number
    estimatedTime?: number
  }): Promise<MaintenanceTemplate> {
    return this.post('/maintenance/templates', data)
  }

  async updateMaintenanceTemplate(id: string, data: Partial<MaintenanceTemplate>): Promise<MaintenanceTemplate> {
    return this.put(`/maintenance/templates/${id}`, data)
  }

  async deleteMaintenanceTemplate(id: string): Promise<void> {
    return this.delete(`/maintenance/templates/${id}`)
  }

  // Maintenance Event API methods
  async getUpcomingMaintenanceEvents(boatId?: string): Promise<MaintenanceEvent[]> {
    return this.get('/maintenance/events/upcoming', boatId ? { boatId } : undefined)
  }

  async getCompletedMaintenanceEvents(boatId?: string): Promise<MaintenanceEvent[]> {
    return this.get('/maintenance/events/completed', boatId ? { boatId } : undefined)
  }

  async getMaintenanceEvent(id: string): Promise<MaintenanceEvent> {
    return this.get(`/maintenance/events/${id}`)
  }

  async completeMaintenanceEvent(id: string, data: {
    actualCost?: number
    actualTime?: number
    notes?: string
  }): Promise<MaintenanceEvent> {
    return this.post(`/maintenance/events/${id}/complete`, data)
  }

  // Location API methods
  async getMarkedLocations(filters?: { category?: string; tags?: string[] }): Promise<MarkedLocation[]> {
    return this.get('/locations', filters)
  }

  async getMarkedLocation(id: string): Promise<MarkedLocation> {
    return this.get(`/locations/${id}`)
  }

  async createMarkedLocation(data: {
    name: string
    latitude: number
    longitude: number
    category: 'fishing' | 'marina' | 'anchorage' | 'hazard' | 'other'
    notes?: string
    tags?: string[]
  }): Promise<MarkedLocation> {
    return this.post('/locations', data)
  }

  async updateMarkedLocation(id: string, data: Partial<MarkedLocation>): Promise<MarkedLocation> {
    return this.put(`/locations/${id}`, data)
  }

  async deleteMarkedLocation(id: string): Promise<void> {
    return this.delete(`/locations/${id}`)
  }

  async getNearbyLocations(latitude: number, longitude: number, radiusMeters: number): Promise<MarkedLocation[]> {
    return this.get('/locations/nearby', { latitude, longitude, radiusMeters })
  }

  // Photo API methods
  async uploadPhoto(file: File, entityType: string, entityId: string): Promise<Photo> {
    const formData = new FormData()
    formData.append('photo', file)
    formData.append('entityType', entityType)
    formData.append('entityId', entityId)

    const response = await this.client.post<ApiResponse<Photo>>('/photos', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    return response.data.data
  }

  async getPhotos(entityType: string, entityId: string): Promise<Photo[]> {
    return this.get('/photos', { entityType, entityId })
  }

  async deletePhoto(id: string): Promise<void> {
    return this.delete(`/photos/${id}`)
  }

  // Notification API methods
  async getNotifications(): Promise<Notification[]> {
    const response = await this.get<Notification[] | { notifications: Notification[]; count: number }>('/notifications')
    if (Array.isArray(response)) {
      return response
    }
    return response?.notifications || []
  }

  async markNotificationAsRead(id: string): Promise<void> {
    return this.patch(`/notifications/${id}/read`)
  }

  // Backup API methods
  async createBackup(): Promise<{ id: string; filename: string }> {
    return this.post('/backups')
  }

  async getBackups(): Promise<Array<{ id: string; filename: string; createdAt: string; size: number }>> {
    return this.get('/backups')
  }

  async downloadBackup(id: string): Promise<Blob> {
    const response = await this.client.get(`/backups/${id}/download`, {
      responseType: 'blob',
    })
    return response.data
  }

  // Viewer account settings (admin only)
  async getViewerSettings(): Promise<ViewerSettings> {
    return this.get('/settings/viewer')
  }

  async updateViewerSettings(data: { username?: string; password?: string; enabled?: boolean }): Promise<ViewerSettings> {
    return this.put('/settings/viewer', data)
  }
}

export const apiService = new ApiService()
export default apiService