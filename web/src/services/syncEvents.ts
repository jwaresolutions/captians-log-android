import { QueryClient } from '@tanstack/react-query'

let eventSource: EventSource | null = null

const entityTypeToQueryKey: Record<string, string[]> = {
  boats: ['boats'],
  trips: ['trips'],
  notes: ['notes'],
  todos: ['todos'],
  maintenance_templates: ['maintenanceTemplates'],
  maintenance_events: ['maintenanceEvents'],
  locations: ['locations'],
  photos: ['photos'],
  sensors: ['sensors'],
}

export function connectSyncEvents(queryClient: QueryClient): void {
  disconnectSyncEvents()

  const token = localStorage.getItem('auth_token')
  if (!token) return

  const baseUrl = localStorage.getItem('api_base_url') || import.meta.env.VITE_API_BASE_URL || '/api/v1'
  const url = `${baseUrl}/sync/events?token=${encodeURIComponent(token)}`

  eventSource = new EventSource(url)

  eventSource.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      if (data.type === 'connected') return

      const queryKeys = entityTypeToQueryKey[data.type]
      if (queryKeys) {
        queryClient.invalidateQueries({ queryKey: queryKeys })
      }
    } catch {
      // Ignore parse errors (heartbeats, etc.)
    }
  }

  eventSource.onerror = () => {
    // EventSource auto-reconnects on error
  }
}

export function disconnectSyncEvents(): void {
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
}
