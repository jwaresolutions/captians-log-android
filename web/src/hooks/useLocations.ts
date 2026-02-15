import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { apiService } from '../services/api'
import { MarkedLocation } from '../types/api'

// Query keys
export const locationKeys = {
  all: ['locations'] as const,
  lists: () => [...locationKeys.all, 'list'] as const,
  list: (filters: Record<string, unknown>) => [...locationKeys.lists(), { filters }] as const,
  details: () => [...locationKeys.all, 'detail'] as const,
  detail: (id: string) => [...locationKeys.details(), id] as const,
  nearby: (lat: number, lng: number, radius: number) => [...locationKeys.all, 'nearby', { lat, lng, radius }] as const,
}

// Hooks
export const useMarkedLocations = (filters?: { category?: string; tags?: string[] }) => {
  return useQuery({
    queryKey: locationKeys.list(filters || {}),
    queryFn: () => apiService.getMarkedLocations(filters),
  })
}

export const useMarkedLocation = (id: string) => {
  return useQuery({
    queryKey: locationKeys.detail(id),
    queryFn: () => apiService.getMarkedLocation(id),
    enabled: !!id,
  })
}

export const useNearbyLocations = (latitude: number, longitude: number, radiusMeters: number) => {
  return useQuery({
    queryKey: locationKeys.nearby(latitude, longitude, radiusMeters),
    queryFn: () => apiService.getNearbyLocations(latitude, longitude, radiusMeters),
    enabled: !!(latitude && longitude && radiusMeters),
  })
}

export const useCreateMarkedLocation = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (data: {
      name: string
      latitude: number
      longitude: number
      category: 'fishing' | 'marina' | 'anchorage' | 'hazard' | 'other'
      notes?: string
      tags?: string[]
    }) => apiService.createMarkedLocation(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: locationKeys.lists() })
    },
  })
}

export const useUpdateMarkedLocation = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<MarkedLocation> }) =>
      apiService.updateMarkedLocation(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: locationKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: locationKeys.lists() })
    },
  })
}

export const useDeleteMarkedLocation = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (id: string) => apiService.deleteMarkedLocation(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: locationKeys.lists() })
    },
  })
}