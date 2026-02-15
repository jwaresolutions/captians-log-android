import React, { useState, useEffect } from 'react'
import styled from 'styled-components'
import { useNavigate } from 'react-router-dom'
import { LCARSPanel, LCARSButton, LCARSHeader, LCARSAlert } from '../components/lcars'
import { PasswordInput } from '../components/PasswordInput'
import { useAuth } from '../hooks/useAuth'
import { apiService } from '../services/api'

const SetupContainer = styled.div`
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: ${props => props.theme.colors.background};
  padding: ${props => props.theme.spacing.lg};
`

const SetupPanel = styled.div`
  max-width: 600px;
  width: 100%;
`

const LogoContainer = styled.div`
  display: flex;
  justify-content: center;
  margin-bottom: ${props => props.theme.spacing.xl};
`

const Logo = styled.img`
  max-width: 200px;
  height: auto;
  filter: drop-shadow(0 0 10px ${props => props.theme.colors.primary.neonCarrot}40);
`

const SetupForm = styled.form`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.lg};
`

const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.sm};
`

const Label = styled.label`
  color: ${props => props.theme.colors.text.primary};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
`

const Input = styled.input`
  background-color: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.primary.neonCarrot};
  border-radius: ${props => props.theme.borderRadius.sm};
  padding: ${props => props.theme.spacing.md};
  color: ${props => props.theme.colors.text.primary};
  font-size: ${props => props.theme.typography.fontSize.md};

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.tanoi};
    box-shadow: ${props => props.theme.shadows.glow};
  }
`

const ButtonContainer = styled.div`
  display: flex;
  justify-content: center;
  margin-top: ${props => props.theme.spacing.lg};
`

export const SetupWizard: React.FC = () => {
  const navigate = useNavigate()
  const { login, isAuthenticated } = useAuth()

  // Redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated) {
      navigate('/')
    }
  }, [isAuthenticated, navigate])
  
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    serverUrl: import.meta.env.VITE_DEFAULT_SERVER_URL || '',
  })
  const [isLoading, setIsLoading] = useState(false)
  const [message, setMessage] = useState<{ type: 'success' | 'error' | 'info'; text: string } | null>(null)
  const [showAdvanced, setShowAdvanced] = useState(false)

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setMessage(null)

    try {
      // Only update base URL if a custom server URL is provided
      if (formData.serverUrl.trim()) {
        apiService.updateBaseUrl(formData.serverUrl)
        console.log('Server URL configured:', formData.serverUrl)
      } else {
        console.log('Using default server URL (proxy)')
      }

      console.log('Attempting login with:', { username: formData.username })

      // Attempt login
      const result = await login(formData.username, formData.password)
      
      console.log('Login result:', result)
      
      if (result.success) {
        setMessage({
          type: 'success',
          text: 'LCARS Interface Initialized Successfully! Redirecting...'
        })
        console.log('Login successful, setting timeout for redirect')
        // Give a small delay to show the success message, then redirect
        setTimeout(() => {
          console.log('Redirecting to dashboard')
          navigate('/')
        }, 1500)
      } else {
        console.log('Login failed:', result.error)
        setMessage({
          type: 'error',
          text: result.error || 'Authentication failed. Please check your credentials.'
        })
      }
    } catch (error: any) {
      console.error('Login error:', error)
      setMessage({
        type: 'error',
        text: error.message || 'Setup failed. Please check your connection and try again.'
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <SetupContainer>
      <SetupPanel>
        <LogoContainer>
          <Logo 
            src="/assets/captains-log-logo.png" 
            alt="Captain's Log" 
          />
        </LogoContainer>
        <LCARSPanel title="System Initialization" padding="lg">
          <LCARSHeader level={2} align="center">
            LCARS Setup Wizard
          </LCARSHeader>
          
          <SetupForm onSubmit={handleSubmit}>
            <FormGroup>
              <Label htmlFor="username">Username</Label>
              <Input
                type="text"
                id="username"
                name="username"
                value={formData.username}
                onChange={handleInputChange}
                placeholder="Enter your username"
                required
                disabled={isLoading}
              />
            </FormGroup>

            <FormGroup>
              <Label htmlFor="password">Password</Label>
              <PasswordInput
                id="password"
                name="password"
                value={formData.password}
                onChange={handleInputChange}
                placeholder="Enter your password"
                required
                disabled={isLoading}
              />
            </FormGroup>

            <div style={{ textAlign: 'right' }}>
              <button
                type="button"
                onClick={() => setShowAdvanced(!showAdvanced)}
                style={{
                  background: 'none',
                  border: 'none',
                  color: '#99CCFF',
                  cursor: 'pointer',
                  fontSize: '12px',
                  textTransform: 'uppercase',
                  letterSpacing: '1px'
                }}
              >
                {showAdvanced ? 'Hide Advanced' : 'Advanced Options'}
              </button>
            </div>

            {showAdvanced && (
              <FormGroup>
                <Label htmlFor="serverUrl">Server URL (Optional)</Label>
                <Input
                  type="url"
                  id="serverUrl"
                  name="serverUrl"
                  value={formData.serverUrl}
                  onChange={handleInputChange}
                  placeholder="Leave empty for default"
                  disabled={isLoading}
                />
              </FormGroup>
            )}

            {message && (
              <LCARSAlert 
                type={message.type === 'success' ? 'success' : message.type === 'error' ? 'error' : 'info'}
              >
                {message.text}
              </LCARSAlert>
            )}

            <ButtonContainer>
              <LCARSButton 
                type="submit" 
                disabled={isLoading}
                size="lg"
              >
                {isLoading ? 'Initializing...' : 'Initialize LCARS'}
              </LCARSButton>
            </ButtonContainer>
          </SetupForm>
        </LCARSPanel>
      </SetupPanel>
    </SetupContainer>
  )
}