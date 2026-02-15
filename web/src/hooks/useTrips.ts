import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { apiService } from '../services/api'
import { Trip, GPSPoint, ManualData } from '../types/api'

// Query keys
export const tripKeys = {
  all: ['trips'] as const,
  lists: () => [...tripKeys.all, 'list'] as const,
  list: (filters: Record<string, unknown>) => [...tripKeys.lists(), { filters }] as const,
  details: () => [...tripKeys.all, 'detail'] as const,
  detail: (id: string) => [...tripKeys.details(), id] as const,
}

// Hooks
export const useTrips = (filters?: { boatId?: string; startDate?: string; endDate?: string }) => {
  return useQuery({
    queryKey: tripKeys.list(filters || {}),
    queryFn: () => apiService.getTrips(filters),
  })
}

export const useTrip = (id: string) => {
  return useQuery({
    queryKey: tripKeys.detail(id),
    queryFn: () => apiService.getTrip(id),
    enabled: !!id,
  })
}

export const useCreateTrip = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (data: {
      boatId: string
      startTime: string
      endTime: string
      waterType: 'inland' | 'coastal' | 'offshore'
      role: 'captain' | 'crew' | 'observer'
      gpsPoints: Omit<GPSPoint, 'id' | 'tripId'>[]
    }) => apiService.createTrip(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: tripKeys.lists() })
    },
  })
}

export const useUpdateTrip = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<Trip> }) =>
      apiService.updateTrip(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: tripKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: tripKeys.lists() })
    },
  })
}

export const useAddManualData = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: ({ tripId, data }: { tripId: string; data: ManualData }) =>
      apiService.addManualData(tripId, data),
    onSuccess: (_, { tripId }) => {
      queryClient.invalidateQueries({ queryKey: tripKeys.detail(tripId) })
      queryClient.invalidateQueries({ queryKey: tripKeys.lists() })
    },
  })
}