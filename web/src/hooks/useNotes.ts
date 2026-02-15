import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { apiService } from '../services/api'
import { Note } from '../types/api'

interface NotesFilters {
  type?: 'general' | 'boat' | 'trip'
  boatId?: string
  tripId?: string
  tags?: string[]
}

export const useNotes = (filters?: NotesFilters) => {
  return useQuery({
    queryKey: ['notes', filters],
    queryFn: () => apiService.getNotes(filters),
  })
}

export const useNote = (id: string) => {
  return useQuery({
    queryKey: ['notes', id],
    queryFn: () => apiService.getNote(id),
    enabled: !!id,
  })
}

export const useCreateNote = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (data: {
      content: string
      type: 'general' | 'boat' | 'trip'
      boatId?: string
      tripId?: string
      tags?: string[]
    }) => apiService.createNote(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notes'] })
    },
  })
}

export const useUpdateNote = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<Note> }) => 
      apiService.updateNote(id, data),
    onSuccess: (updatedNote) => {
      queryClient.invalidateQueries({ queryKey: ['notes'] })
      queryClient.setQueryData(['notes', updatedNote.id], updatedNote)
    },
  })
}

export const useDeleteNote = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (id: string) => apiService.deleteNote(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notes'] })
    },
  })
}

// Helper hook to get all unique tags from notes
export const useNoteTags = () => {
  const { data: notes } = useNotes()
  
  const tags = notes?.reduce((acc: string[], note) => {
    note.tags.forEach(tag => {
      if (!acc.includes(tag)) {
        acc.push(tag)
      }
    })
    return acc
  }, []) || []
  
  return tags.sort()
}