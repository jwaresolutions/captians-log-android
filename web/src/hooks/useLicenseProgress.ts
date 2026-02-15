import { useQuery } from '@tanstack/react-query'
import { apiService } from '../services/api'

// Query keys
export const licenseKeys = {
  all: ['license'] as const,
  progress: () => [...licenseKeys.all, 'progress'] as const,
}

// Hooks
export const useLicenseProgress = () => {
  return useQuery({
    queryKey: licenseKeys.progress(),
    queryFn: () => apiService.getLicenseProgress(),
    staleTime: 5 * 60 * 1000, // 5 minutes
  })
}