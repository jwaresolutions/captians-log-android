import React, { useState } from 'react'
import styled from 'styled-components'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { LCARSHeader } from '../components/lcars/LCARSHeader'
import { LCARSDataDisplay } from '../components/lcars/LCARSDataDisplay'
import { PasswordInput } from '../components/PasswordInput'
import { useAuth } from '../hooks/useAuth'
import { apiService } from '../services/api'
import { useNavigate } from 'react-router-dom'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'
import { ViewerSettings } from '../types/api'

const SettingsContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.lg};
  max-width: 1200px;
  margin: 0 auto;
`

const SettingsGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: ${props => props.theme.spacing.lg};
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`

const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.sm};
  margin-bottom: ${props => props.theme.spacing.md};
`

const Label = styled.label`
  color: ${props => props.theme.colors.primary.anakiwa};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  font-size: ${props => props.theme.typography.fontSize.sm};
  letter-spacing: 1px;
`

const Input = styled.input`
  background: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.text.primary};
  padding: ${props => props.theme.spacing.sm};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: ${props => props.theme.typography.fontSize.md};

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.neonCarrot};
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`

const StatusMessage = styled.div<{ $type: 'success' | 'error' | 'info' }>`
  padding: ${props => props.theme.spacing.sm};
  border-radius: 4px;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1px;
  
  ${props => {
    switch (props.$type) {
      case 'success':
        return `
          background: ${props.theme.colors.status.success}20;
          color: ${props.theme.colors.status.success};
          border: 1px solid ${props.theme.colors.status.success};
        `
      case 'error':
        return `
          background: ${props.theme.colors.status.error}20;
          color: ${props.theme.colors.status.error};
          border: 1px solid ${props.theme.colors.status.error};
        `
      case 'info':
        return `
          background: ${props.theme.colors.primary.anakiwa}20;
          color: ${props.theme.colors.primary.anakiwa};
          border: 1px solid ${props.theme.colors.primary.anakiwa};
        `
    }
  }}
`

const UserInfoGrid = styled.div`
  display: grid;
  grid-template-columns: auto 1fr;
  gap: ${props => props.theme.spacing.md};
  align-items: center;
`

const InfoLabel = styled.div`
  color: ${props => props.theme.colors.primary.anakiwa};
  font-weight: bold;
  text-transform: uppercase;
  font-size: ${props => props.theme.typography.fontSize.sm};
`

const InfoValue = styled.div`
  color: ${props => props.theme.colors.text.primary};
  font-family: ${props => props.theme.typography.fontFamily.monospace};
`

const ToggleContainer = styled.div`
  display: flex;
  align-items: center;
  gap: ${props => props.theme.spacing.md};
`

const ToggleSwitch = styled.label`
  position: relative;
  display: inline-block;
  width: 50px;
  height: 26px;
  cursor: pointer;
`

const ToggleSlider = styled.span<{ $checked: boolean }>`
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: ${props => props.$checked ? props.theme.colors.status.success : props.theme.colors.surface.dark};
  border: 2px solid ${props => props.$checked ? props.theme.colors.status.success : props.theme.colors.primary.anakiwa};
  border-radius: 13px;
  transition: 0.3s;

  &::before {
    content: '';
    position: absolute;
    height: 18px;
    width: 18px;
    left: ${props => props.$checked ? '22px' : '2px'};
    bottom: 2px;
    background-color: ${props => props.theme.colors.text.primary};
    border-radius: 50%;
    transition: 0.3s;
  }
`

const ToggleLabel = styled.span<{ $active: boolean }>`
  color: ${props => props.$active ? props.theme.colors.status.success : props.theme.colors.text.secondary};
  font-weight: bold;
  text-transform: uppercase;
  font-size: ${props => props.theme.typography.fontSize.sm};
  letter-spacing: 1px;
`

export const Settings: React.FC = () => {
  const { user, logout, isReadOnly } = useAuth()
  const navigate = useNavigate()
  const [passwordForm, setPasswordForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  })
  const [isChangingPassword, setIsChangingPassword] = useState(false)
  const [passwordMessage, setPasswordMessage] = useState<{
    type: 'success' | 'error' | 'info'
    text: string
  } | null>(null)

  const [viewerSettings, setViewerSettings] = useState<ViewerSettings>({ exists: false, enabled: false, username: '' })
  const [viewerForm, setViewerForm] = useState({ username: '', password: '' })
  const [isLoadingViewer, setIsLoadingViewer] = useState(false)
  const [viewerMessage, setViewerMessage] = useState<{ type: 'success' | 'error' | 'info'; text: string } | null>(null)

  // Load viewer settings on mount (admin only)
  React.useEffect(() => {
    if (!isReadOnly) {
      loadViewerSettings()
    }
  }, [isReadOnly])

  const loadViewerSettings = async () => {
    try {
      const settings = await apiService.getViewerSettings()
      setViewerSettings(settings)
      setViewerForm(prev => ({ ...prev, username: settings.username || '' }))
    } catch (error) {
      // Settings endpoint may not exist yet, ignore
    }
  }

  const handleViewerToggle = async () => {
    setIsLoadingViewer(true)
    try {
      if (!viewerSettings.exists) {
        // Need username and password to create
        if (!viewerForm.username || !viewerForm.password) {
          setViewerMessage({ type: 'error', text: 'Username and password required to create viewer account' })
          setIsLoadingViewer(false)
          return
        }
        const result = await apiService.updateViewerSettings({
          username: viewerForm.username,
          password: viewerForm.password,
          enabled: true,
        })
        setViewerSettings(result)
        setViewerForm(prev => ({ ...prev, password: '' }))
        setViewerMessage({ type: 'success', text: 'Viewer account created and enabled' })
      } else {
        const result = await apiService.updateViewerSettings({ enabled: !viewerSettings.enabled })
        setViewerSettings(result)
        setViewerMessage({ type: 'success', text: `Viewer account ${result.enabled ? 'enabled' : 'disabled'}` })
      }
    } catch (error: any) {
      setViewerMessage({ type: 'error', text: error.message || 'Failed to update viewer settings' })
    } finally {
      setIsLoadingViewer(false)
    }
  }

  const handleViewerSave = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!viewerForm.username) {
      setViewerMessage({ type: 'error', text: 'Username is required' })
      return
    }
    if (!viewerSettings.exists && !viewerForm.password) {
      setViewerMessage({ type: 'error', text: 'Password is required for new viewer account' })
      return
    }
    if (viewerForm.password && viewerForm.password.length < 8) {
      setViewerMessage({ type: 'error', text: 'Password must be at least 8 characters' })
      return
    }

    setIsLoadingViewer(true)
    setViewerMessage({ type: 'info', text: 'Saving...' })
    try {
      const updateData: any = { username: viewerForm.username }
      if (viewerForm.password) updateData.password = viewerForm.password
      // When creating a new viewer, enable by default
      if (!viewerSettings.exists) updateData.enabled = true
      const result = await apiService.updateViewerSettings(updateData)
      setViewerSettings(result)
      setViewerForm(prev => ({ ...prev, password: '' }))
      setViewerMessage({ type: 'success', text: 'Viewer account updated' })
    } catch (error: any) {
      setViewerMessage({ type: 'error', text: error.message || 'Failed to save viewer settings' })
    } finally {
      setIsLoadingViewer(false)
    }
  }

  const handlePasswordChange = (field: keyof typeof passwordForm) => (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setPasswordForm(prev => ({
      ...prev,
      [field]: e.target.value,
    }))
    // Clear message when user starts typing
    if (passwordMessage) {
      setPasswordMessage(null)
    }
  }

  const handlePasswordSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!passwordForm.currentPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
      setPasswordMessage({
        type: 'error',
        text: 'All password fields are required',
      })
      return
    }

    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      setPasswordMessage({
        type: 'error',
        text: 'New passwords do not match',
      })
      return
    }

    if (passwordForm.newPassword.length < 8) {
      setPasswordMessage({
        type: 'error',
        text: 'New password must be at least 8 characters',
      })
      return
    }

    setIsChangingPassword(true)
    setPasswordMessage({
      type: 'info',
      text: 'Changing password...',
    })

    try {
      await apiService.changePassword(passwordForm.currentPassword, passwordForm.newPassword)
      
      setPasswordMessage({
        type: 'success',
        text: 'Password changed successfully. You will be logged out.',
      })
      
      // Clear form
      setPasswordForm({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
      })
      
      // Log out user after successful password change
      setTimeout(() => {
        logout()
      }, 2000)
      
    } catch (error: any) {
      setPasswordMessage({
        type: 'error',
        text: error.message || 'Failed to change password',
      })
    } finally {
      setIsChangingPassword(false)
    }
  }

  const handleLogout = async () => {
    if (window.confirm('Are you sure you want to log out?')) {
      await logout()
    }
  }

  return (
    <SettingsContainer>
      <LCARSHeader>System Settings</LCARSHeader>
      
      <SettingsGrid>
        {/* User Account Information */}
        <LCARSPanel title="User Account">
          <UserInfoGrid>
            <InfoLabel>Username:</InfoLabel>
            <InfoValue>{user?.username || 'Unknown'}</InfoValue>
            
            <InfoLabel>Account Created:</InfoLabel>
            <InfoValue>
              {user?.createdAt 
                ? new Date(user.createdAt).toLocaleDateString()
                : 'Unknown'
              }
            </InfoValue>
            
            <InfoLabel>Last Updated:</InfoLabel>
            <InfoValue>
              {user?.updatedAt 
                ? new Date(user.updatedAt).toLocaleDateString()
                : 'Unknown'
              }
            </InfoValue>
          </UserInfoGrid>
          
          <div style={{ marginTop: '20px' }}>
            <LCARSButton 
              onClick={handleLogout}
              variant="secondary"
            >
              Logout
            </LCARSButton>
          </div>
        </LCARSPanel>

        {/* Password Change */}
        <ReadOnlyGuard fallback={
          <LCARSPanel title="Change Password">
            <div style={{ padding: '20px', color: '#6688CC', textAlign: 'center', textTransform: 'uppercase', letterSpacing: '1px' }}>
              Password changes are not available for viewer accounts.
            </div>
          </LCARSPanel>
        }>
          <LCARSPanel title="Change Password">
            <form onSubmit={handlePasswordSubmit}>
              <FormGroup>
                <Label htmlFor="currentPassword">Current Password</Label>
                <PasswordInput
                  id="currentPassword"
                  value={passwordForm.currentPassword}
                  onChange={handlePasswordChange('currentPassword')}
                  disabled={isChangingPassword}
                  autoComplete="current-password"
                />
              </FormGroup>

              <FormGroup>
                <Label htmlFor="newPassword">New Password</Label>
                <PasswordInput
                  id="newPassword"
                  value={passwordForm.newPassword}
                  onChange={handlePasswordChange('newPassword')}
                  disabled={isChangingPassword}
                  autoComplete="new-password"
                  minLength={8}
                />
              </FormGroup>

              <FormGroup>
                <Label htmlFor="confirmPassword">Confirm New Password</Label>
                <PasswordInput
                  id="confirmPassword"
                  value={passwordForm.confirmPassword}
                  onChange={handlePasswordChange('confirmPassword')}
                  disabled={isChangingPassword}
                  autoComplete="new-password"
                  minLength={8}
                />
              </FormGroup>

              {passwordMessage && (
                <StatusMessage $type={passwordMessage.type}>
                  {passwordMessage.text}
                </StatusMessage>
              )}

              <div style={{ marginTop: '20px' }}>
                <LCARSButton
                  type="submit"
                  disabled={isChangingPassword}
                >
                  {isChangingPassword ? 'Changing Password...' : 'Change Password'}
                </LCARSButton>
              </div>
            </form>
          </LCARSPanel>
        </ReadOnlyGuard>
      </SettingsGrid>

      {!isReadOnly && (
        <LCARSPanel title="Viewer Account">
          <div style={{ marginBottom: '20px' }}>
            <ToggleContainer>
              <ToggleSwitch onClick={handleViewerToggle}>
                <ToggleSlider $checked={viewerSettings.enabled} />
              </ToggleSwitch>
              <ToggleLabel $active={viewerSettings.enabled}>
                {viewerSettings.enabled ? 'Enabled' : 'Disabled'}
              </ToggleLabel>
              {isLoadingViewer && <span style={{ color: '#9999cc', fontSize: '12px' }}>Updating...</span>}
            </ToggleContainer>
          </div>

          <form onSubmit={handleViewerSave}>
            <FormGroup>
              <Label htmlFor="viewerUsername">Viewer Username</Label>
              <Input
                id="viewerUsername"
                type="text"
                value={viewerForm.username}
                onChange={(e) => { setViewerForm(prev => ({ ...prev, username: e.target.value })); setViewerMessage(null) }}
                disabled={isLoadingViewer}
                placeholder="viewer"
              />
            </FormGroup>

            <FormGroup>
              <Label htmlFor="viewerPassword">{viewerSettings.exists ? 'New Password (leave blank to keep)' : 'Password'}</Label>
              <PasswordInput
                id="viewerPassword"
                value={viewerForm.password}
                onChange={(e) => { setViewerForm(prev => ({ ...prev, password: e.target.value })); setViewerMessage(null) }}
                disabled={isLoadingViewer}
                minLength={8}
                placeholder={viewerSettings.exists ? '********' : 'Min 8 characters'}
              />
            </FormGroup>

            {viewerMessage && (
              <StatusMessage $type={viewerMessage.type}>
                {viewerMessage.text}
              </StatusMessage>
            )}

            <div style={{ marginTop: '20px' }}>
              <LCARSButton
                type="submit"
                disabled={isLoadingViewer}
              >
                {viewerSettings.exists ? 'Update Viewer' : 'Create Viewer'}
              </LCARSButton>
            </div>
          </form>
        </LCARSPanel>
      )}

      {/* System Management */}
      <LCARSPanel title="System Management">
        <div style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
          <LCARSButton
            onClick={() => navigate('/settings/backup')}
            variant="secondary"
          >
            Backup Manager
          </LCARSButton>
          <LCARSButton
            onClick={() => navigate('/settings/nautical')}
            variant="secondary"
          >
            Nautical Data
          </LCARSButton>
        </div>
        
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
          <LCARSDataDisplay
            label="Interface Version"
            value="LCARS v1.0"
            valueColor="anakiwa"
          />
          <LCARSDataDisplay
            label="System Status"
            value="Operational"
            valueColor="success"
          />
          <LCARSDataDisplay
            label="API Endpoint"
            value={import.meta.env.VITE_API_BASE_URL || 'http://localhost:8585/api/v1'}
            valueColor="anakiwa"
          />
          <LCARSDataDisplay
            label="Authentication"
            value="JWT Token-based"
            valueColor="lilac"
          />
        </div>
      </LCARSPanel>
    </SettingsContainer>
  )
}