import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { apiService } from '../services/api'
import { Boat } from '../types/api'

// Query keys
export const boatKeys = {
  all: ['boats'] as const,
  lists: () => [...boatKeys.all, 'list'] as const,
  list: (filters: Record<string, unknown>) => [...boatKeys.lists(), { filters }] as const,
  details: () => [...boatKeys.all, 'detail'] as const,
  detail: (id: string) => [...boatKeys.details(), id] as const,
}

// Hooks
export const useBoats = () => {
  return useQuery({
    queryKey: boatKeys.lists(),
    queryFn: () => apiService.getBoats(),
  })
}

export const useBoat = (id: string) => {
  return useQuery({
    queryKey: boatKeys.detail(id),
    queryFn: () => apiService.getBoat(id),
    enabled: !!id,
  })
}

export const useCreateBoat = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (data: { name: string; metadata?: Record<string, unknown> }) =>
      apiService.createBoat(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: boatKeys.lists() })
    },
  })
}

export const useUpdateBoat = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<Boat> }) =>
      apiService.updateBoat(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: boatKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: boatKeys.lists() })
    },
  })
}

export const useToggleBoatStatus = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: ({ id, enabled }: { id: string; enabled: boolean }) =>
      apiService.toggleBoatStatus(id, enabled),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: boatKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: boatKeys.lists() })
    },
  })
}

export const useSetActiveBoat = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (id: string) => apiService.setActiveBoat(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: boatKeys.lists() })
    },
  })
}