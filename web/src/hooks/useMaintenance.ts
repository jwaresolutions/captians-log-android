import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { apiService as api } from '../services/api'
import { MaintenanceTemplate, RecurrenceSchedule } from '../types/api'

// Template hooks
export function useMaintenanceTemplates(boatId?: string) {
  return useQuery({
    queryKey: ['maintenance-templates', boatId],
    queryFn: () => api.getMaintenanceTemplates(boatId),
  })
}

export function useMaintenanceTemplate(id: string, options?: { enabled?: boolean }) {
  return useQuery({
    queryKey: ['maintenance-template', id],
    queryFn: () => api.getMaintenanceTemplate(id),
    enabled: options?.enabled !== undefined ? options.enabled : !!id,
  })
}

export function useCreateMaintenanceTemplate() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (data: {
      boatId: string
      title: string
      description?: string
      component?: string
      recurrence?: RecurrenceSchedule
      estimatedCost?: number
      estimatedTime?: number
    }) => api.createMaintenanceTemplate(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['maintenance-templates'] })
    },
  })
}

export function useUpdateMaintenanceTemplate() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<MaintenanceTemplate> }) =>
      api.updateMaintenanceTemplate(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: ['maintenance-template', id] })
      queryClient.invalidateQueries({ queryKey: ['maintenance-templates'] })
    },
  })
}

export function useDeleteMaintenanceTemplate() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (id: string) => api.deleteMaintenanceTemplate(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['maintenance-templates'] })
      queryClient.invalidateQueries({ queryKey: ['maintenance-events'] })
    },
  })
}

// Event hooks
export function useUpcomingMaintenanceEvents(boatId?: string) {
  return useQuery({
    queryKey: ['maintenance-events', 'upcoming', boatId],
    queryFn: () => api.getUpcomingMaintenanceEvents(boatId),
  })
}

export function useCompletedMaintenanceEvents(boatId?: string) {
  return useQuery({
    queryKey: ['maintenance-events', 'completed', boatId],
    queryFn: () => api.getCompletedMaintenanceEvents(boatId),
  })
}

export function useMaintenanceEvent(id: string) {
  return useQuery({
    queryKey: ['maintenance-event', id],
    queryFn: () => api.getMaintenanceEvent(id),
    enabled: !!id,
  })
}

export function useCompleteMaintenanceEvent() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: ({ id, data }: { 
      id: string; 
      data: { 
        actualCost?: number; 
        actualTime?: number; 
        notes?: string 
      } 
    }) => api.completeMaintenanceEvent(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['maintenance-events'] })
    },
  })
}