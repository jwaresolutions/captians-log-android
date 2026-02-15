import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { apiService } from '../services/api'
import { TodoList, TodoItem } from '../types/api'

// Query keys - exported for use in components
export const QUERY_KEYS = {
  todoLists: (boatId?: string) => ['todoLists', boatId],
  todoList: (id: string) => ['todoList', id],
}

// Todo Lists hooks
export const useTodoLists = (boatId?: string) => {
  return useQuery({
    queryKey: QUERY_KEYS.todoLists(boatId),
    queryFn: () => apiService.getTodoLists(boatId),
  })
}

export const useTodoList = (id: string) => {
  return useQuery({
    queryKey: QUERY_KEYS.todoList(id),
    queryFn: () => apiService.getTodoList(id),
    enabled: !!id,
  })
}

export const useCreateTodoList = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: {
      title: string
      type: 'general' | 'boat'
      boatId?: string
    }) => apiService.createTodoList(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['todoLists'] })
    },
  })
}

export const useUpdateTodoList = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<TodoList> }) =>
      apiService.updateTodoList(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.todoList(id) })
      queryClient.invalidateQueries({ queryKey: ['todoLists'] })
    },
  })
}

export const useDeleteTodoList = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: string) => apiService.deleteTodoList(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['todoLists'] })
    },
  })
}

// Todo Items hooks
export const useAddTodoItem = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ listId, content }: { listId: string; content: string }) =>
      apiService.addTodoItem(listId, content),
    onMutate: async ({ listId, content }) => {
      await queryClient.cancelQueries({ queryKey: QUERY_KEYS.todoList(listId) })
      const previous = queryClient.getQueryData<TodoList>(QUERY_KEYS.todoList(listId))
      if (previous) {
        const optimisticItem: TodoItem = {
          id: `temp-${Date.now()}`,
          listId,
          content,
          completed: false,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
        }
        queryClient.setQueryData<TodoList>(QUERY_KEYS.todoList(listId), {
          ...previous,
          items: [...previous.items, optimisticItem],
        })
      }
      return { previous }
    },
    onError: (_, { listId }, context) => {
      if (context?.previous) {
        queryClient.setQueryData(QUERY_KEYS.todoList(listId), context.previous)
      }
    },
    onSettled: (_, __, { listId }) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.todoList(listId) })
      queryClient.invalidateQueries({ queryKey: ['todoLists'] })
    },
  })
}

export const useToggleTodoItem = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ itemId }: { itemId: string; listId: string }) =>
      apiService.toggleTodoItem(itemId),
    onMutate: async ({ itemId, listId }) => {
      await queryClient.cancelQueries({ queryKey: QUERY_KEYS.todoList(listId) })
      const previous = queryClient.getQueryData<TodoList>(QUERY_KEYS.todoList(listId))
      if (previous) {
        queryClient.setQueryData<TodoList>(QUERY_KEYS.todoList(listId), {
          ...previous,
          items: previous.items.map(item =>
            item.id === itemId
              ? { ...item, completed: !item.completed, completedAt: !item.completed ? new Date().toISOString() : undefined }
              : item
          ),
        })
      }
      return { previous }
    },
    onError: (_, { listId }, context) => {
      if (context?.previous) {
        queryClient.setQueryData(QUERY_KEYS.todoList(listId), context.previous)
      }
    },
    onSettled: (_, __, { listId }) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.todoList(listId) })
      queryClient.invalidateQueries({ queryKey: ['todoLists'] })
    },
  })
}

export const useUpdateTodoItem = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ itemId, data }: { itemId: string; listId: string; data: { content?: string; completed?: boolean } }) =>
      apiService.updateTodoItem(itemId, data),
    onMutate: async ({ itemId, listId, data }) => {
      await queryClient.cancelQueries({ queryKey: QUERY_KEYS.todoList(listId) })
      const previous = queryClient.getQueryData<TodoList>(QUERY_KEYS.todoList(listId))
      if (previous) {
        queryClient.setQueryData<TodoList>(QUERY_KEYS.todoList(listId), {
          ...previous,
          items: previous.items.map(item =>
            item.id === itemId ? { ...item, ...data } : item
          ),
        })
      }
      return { previous }
    },
    onError: (_, { listId }, context) => {
      if (context?.previous) {
        queryClient.setQueryData(QUERY_KEYS.todoList(listId), context.previous)
      }
    },
    onSettled: (_, __, { listId }) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.todoList(listId) })
      queryClient.invalidateQueries({ queryKey: ['todoLists'] })
    },
  })
}

export const useDeleteTodoItem = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ itemId }: { itemId: string; listId: string }) =>
      apiService.deleteTodoItem(itemId),
    onMutate: async ({ itemId, listId }) => {
      await queryClient.cancelQueries({ queryKey: QUERY_KEYS.todoList(listId) })
      const previous = queryClient.getQueryData<TodoList>(QUERY_KEYS.todoList(listId))
      if (previous) {
        queryClient.setQueryData<TodoList>(QUERY_KEYS.todoList(listId), {
          ...previous,
          items: previous.items.filter(item => item.id !== itemId),
        })
      }
      return { previous }
    },
    onError: (_, { listId }, context) => {
      if (context?.previous) {
        queryClient.setQueryData(QUERY_KEYS.todoList(listId), context.previous)
      }
    },
    onSettled: (_, __, { listId }) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.todoList(listId) })
      queryClient.invalidateQueries({ queryKey: ['todoLists'] })
    },
  })
}
