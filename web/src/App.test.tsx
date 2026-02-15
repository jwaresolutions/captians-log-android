import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ThemeProvider } from 'styled-components'
import App from './App'
import { lcarsTheme } from './styles/theme'

// Create a test wrapper component
const TestWrapper = ({ children }: { children: React.ReactNode }) => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  })

  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <ThemeProvider theme={lcarsTheme}>
          {children}
        </ThemeProvider>
      </BrowserRouter>
    </QueryClientProvider>
  )
}

describe('App', () => {
  it('renders without crashing', () => {
    render(
      <TestWrapper>
        <App />
      </TestWrapper>
    )
    
    // Should show setup wizard when not authenticated
    expect(screen.getByText(/lcars setup wizard/i)).toBeInTheDocument()
  })

  it('shows setup wizard when not authenticated', async () => {
    render(
      <TestWrapper>
        <App />
      </TestWrapper>
    )
    
    // Wait for loading to complete and setup wizard to appear
    await screen.findByText(/lcars setup wizard/i, {}, { timeout: 2000 })
    expect(screen.getByText(/lcars setup wizard/i)).toBeInTheDocument()
  })
})