import { useState, useCallback } from 'react';
import { useQueryClient } from '@tanstack/react-query';

/**
 * Hook for optimistic updates with rollback capability
 */
export function useOptimisticUpdate<T>(queryKey: string[]) {
  const queryClient = useQueryClient();
  const [isOptimistic, setIsOptimistic] = useState(false);

  const optimisticUpdate = useCallback(
    async <R>(
      updateFn: (oldData: T) => T,
      mutationFn: () => Promise<R>,
      onSuccess?: (result: R) => void,
      onError?: (error: any) => void
    ) => {
      setIsOptimistic(true);
      
      // Store the previous data for rollback
      const previousData = queryClient.getQueryData<T>(queryKey);

      // Optimistically update the cache
      queryClient.setQueryData<T>(queryKey, (oldData) => {
        if (oldData === undefined) return oldData;
        return updateFn(oldData);
      });

      try {
        // Perform the actual mutation
        const result = await mutationFn();
        
        // Invalidate and refetch to ensure consistency
        await queryClient.invalidateQueries({ queryKey });
        
        onSuccess?.(result);
        return result;
      } catch (error) {
        // Rollback on error
        if (previousData !== undefined) {
          queryClient.setQueryData<T>(queryKey, previousData);
        }
        
        onError?.(error);
        throw error;
      } finally {
        setIsOptimistic(false);
      }
    },
    [queryClient, queryKey]
  );

  return { optimisticUpdate, isOptimistic };
}

/**
 * Hook for optimistic list operations (add, remove, update)
 */
export function useOptimisticList<T extends { id: string }>(queryKey: string[]) {
  const { optimisticUpdate, isOptimistic } = useOptimisticUpdate<T[]>(queryKey);

  const optimisticAdd = useCallback(
    (newItem: T, mutationFn: () => Promise<T>) => {
      return optimisticUpdate(
        (oldList = []) => [...oldList, newItem],
        mutationFn
      );
    },
    [optimisticUpdate]
  );

  const optimisticRemove = useCallback(
    (itemId: string, mutationFn: () => Promise<void>) => {
      return optimisticUpdate(
        (oldList = []) => oldList.filter(item => item.id !== itemId),
        mutationFn
      );
    },
    [optimisticUpdate]
  );

  const optimisticUpdate_ = useCallback(
    (itemId: string, updateFn: (item: T) => T, mutationFn: () => Promise<T>) => {
      return optimisticUpdate(
        (oldList = []) => oldList.map(item => 
          item.id === itemId ? updateFn(item) : item
        ),
        mutationFn
      );
    },
    [optimisticUpdate]
  );

  return {
    optimisticAdd,
    optimisticRemove,
    optimisticUpdate: optimisticUpdate_,
    isOptimistic
  };
}

/**
 * Hook for optimistic toggle operations (like completing todos)
 */
export function useOptimisticToggle<T extends { id: string }>(
  queryKey: string[],
  toggleProperty: keyof T
) {
  const { optimisticUpdate } = useOptimisticList<T>(queryKey);

  const optimisticToggle = useCallback(
    (itemId: string, mutationFn: () => Promise<T>) => {
      return optimisticUpdate(
        itemId,
        (item) => ({
          ...item,
          [toggleProperty]: !item[toggleProperty]
        }),
        mutationFn
      );
    },
    [optimisticUpdate, toggleProperty]
  );

  return { optimisticToggle };
}

/**
 * Hook for managing loading states across multiple operations
 */
export function useLoadingStates() {
  const [loadingStates, setLoadingStates] = useState<Record<string, boolean>>({});

  const setLoading = useCallback((key: string, loading: boolean) => {
    setLoadingStates(prev => ({
      ...prev,
      [key]: loading
    }));
  }, []);

  const isLoading = useCallback((key: string) => {
    return loadingStates[key] || false;
  }, [loadingStates]);

  const isAnyLoading = useCallback(() => {
    return Object.values(loadingStates).some(Boolean);
  }, [loadingStates]);

  const withLoading = useCallback(
    async <T>(key: string, asyncFn: () => Promise<T>): Promise<T> => {
      setLoading(key, true);
      try {
        return await asyncFn();
      } finally {
        setLoading(key, false);
      }
    },
    [setLoading]
  );

  return {
    loadingStates,
    setLoading,
    isLoading,
    isAnyLoading,
    withLoading
  };
}

/**
 * Hook for debounced operations with loading state
 */
export function useDebouncedOperation<T extends any[]>(
  operation: (...args: T) => Promise<void>,
  delay: number = 500
) {
  const [isLoading, setIsLoading] = useState(false);
  const [timeoutId, setTimeoutId] = useState<ReturnType<typeof setTimeout> | null>(null);

  const debouncedOperation = useCallback(
    (...args: T) => {
      // Clear existing timeout
      if (timeoutId) {
        clearTimeout(timeoutId);
      }

      // Set loading state immediately for UI feedback
      setIsLoading(true);

      // Set new timeout
      const newTimeoutId = setTimeout(async () => {
        try {
          await operation(...args);
        } finally {
          setIsLoading(false);
        }
      }, delay);

      setTimeoutId(newTimeoutId);
    },
    [operation, delay, timeoutId]
  );

  // Cleanup timeout on unmount
  const cleanup = useCallback(() => {
    if (timeoutId) {
      clearTimeout(timeoutId);
      setIsLoading(false);
    }
  }, [timeoutId]);

  return {
    debouncedOperation,
    isLoading,
    cleanup
  };
}