import React, { useState, useEffect } from 'react'
import styled from 'styled-components'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { LCARSHeader } from '../components/lcars/LCARSHeader'
import { LCARSDataDisplay } from '../components/lcars/LCARSDataDisplay'
import { apiService } from '../services/api'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'

const BackupContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.lg};
  max-width: 1200px;
  margin: 0 auto;
`

const BackupGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: ${props => props.theme.spacing.lg};
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`

const StatusMessage = styled.div<{ $type: 'success' | 'error' | 'info' }>`
  padding: ${props => props.theme.spacing.sm};
  border-radius: 4px;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: ${props => props.theme.spacing.md};
  
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

const BackupList = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.sm};
`

const BackupItem = styled.div`
  background: ${props => props.theme.colors.surface.dark};
  border: 1px solid ${props => props.theme.colors.primary.anakiwa};
  padding: ${props => props.theme.spacing.md};
  display: flex;
  justify-content: space-between;
  align-items: center;

  &:hover {
    border-color: ${props => props.theme.colors.primary.neonCarrot};
  }
`

const BackupInfo = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.xs};
`

const BackupFilename = styled.div`
  color: ${props => props.theme.colors.text.primary};
  font-weight: bold;
  font-family: ${props => props.theme.typography.fontFamily.monospace};
`

const BackupMeta = styled.div`
  color: ${props => props.theme.colors.text.secondary};
  font-size: ${props => props.theme.typography.fontSize.sm};
  display: flex;
  gap: ${props => props.theme.spacing.md};
`

const BackupActions = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.sm};
`

const EmptyState = styled.div`
  text-align: center;
  padding: ${props => props.theme.spacing.xl};
  color: ${props => props.theme.colors.text.secondary};
  font-style: italic;
`

interface BackupFile {
  id: string
  filename: string
  createdAt: string
  size: number
}

export const BackupManager: React.FC = () => {
  const [backups, setBackups] = useState<BackupFile[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [isCreatingBackup, setIsCreatingBackup] = useState(false)
  const [message, setMessage] = useState<{
    type: 'success' | 'error' | 'info'
    text: string
  } | null>(null)

  useEffect(() => {
    loadBackups()
  }, [])

  const loadBackups = async () => {
    try {
      setIsLoading(true)
      const backupList = await apiService.getBackups()
      setBackups(backupList)
    } catch (error: any) {
      setMessage({
        type: 'error',
        text: error.message || 'Failed to load backups',
      })
    } finally {
      setIsLoading(false)
    }
  }

  const createBackup = async () => {
    if (isCreatingBackup) return

    setIsCreatingBackup(true)
    setMessage({
      type: 'info',
      text: 'Creating backup... This may take a few minutes.',
    })

    try {
      const result = await apiService.createBackup()
      setMessage({
        type: 'success',
        text: `Backup created successfully: ${result.filename}`,
      })
      
      // Reload the backup list
      await loadBackups()
    } catch (error: any) {
      setMessage({
        type: 'error',
        text: error.message || 'Failed to create backup',
      })
    } finally {
      setIsCreatingBackup(false)
    }
  }

  const downloadBackup = async (backup: BackupFile) => {
    try {
      setMessage({
        type: 'info',
        text: `Downloading ${backup.filename}...`,
      })

      const blob = await apiService.downloadBackup(backup.id)
      
      // Create download link
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = backup.filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)

      setMessage({
        type: 'success',
        text: `Download started: ${backup.filename}`,
      })
    } catch (error: any) {
      setMessage({
        type: 'error',
        text: error.message || 'Failed to download backup',
      })
    }
  }

  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes'
    
    const k = 1024
    const sizes = ['Bytes', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  const formatDate = (dateString: string): string => {
    return new Date(dateString).toLocaleString()
  }

  return (
    <BackupContainer>
      <LCARSHeader>Database Backup Manager</LCARSHeader>
      
      {message && (
        <StatusMessage $type={message.type}>
          {message.text}
        </StatusMessage>
      )}

      <BackupGrid>
        {/* Backup Controls */}
        <LCARSPanel title="Backup Operations">
          <div style={{ marginBottom: '20px' }}>
            <div style={{ width: '100%', marginBottom: '10px' }}>
              <ReadOnlyGuard>
                <LCARSButton
                  onClick={createBackup}
                  disabled={isCreatingBackup}
                >
                  {isCreatingBackup ? 'Creating Backup...' : 'Create Manual Backup'}
                </LCARSButton>
              </ReadOnlyGuard>
            </div>
            
            <div style={{ width: '100%' }}>
              <LCARSButton 
                onClick={loadBackups}
                disabled={isLoading}
                variant="secondary"
              >
                {isLoading ? 'Refreshing...' : 'Refresh List'}
              </LCARSButton>
            </div>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            <LCARSDataDisplay
              label="Total Backups"
              value={backups.length.toString()}
              valueColor="anakiwa"
            />
            <LCARSDataDisplay
              label="Total Size"
              value={formatFileSize(backups.reduce((sum, backup) => sum + backup.size, 0))}
              valueColor="lilac"
            />
            <LCARSDataDisplay
              label="Latest Backup"
              value={backups.length > 0 
                ? formatDate(backups[0].createdAt)
                : 'None'
              }
              valueColor="neonCarrot"
            />
          </div>

          <div style={{ marginTop: '20px', padding: '10px', background: 'rgba(255, 153, 102, 0.1)', border: '1px solid #FF9966' }}>
            <strong style={{ color: '#FF9966' }}>Important:</strong>
            <ul style={{ margin: '10px 0', paddingLeft: '20px', color: '#CCCCCC' }}>
              <li>Backups include both database records and uploaded photos</li>
              <li>Large backups may take several minutes to create</li>
              <li>Store backups in a secure location outside the system</li>
              <li>Test backup restoration procedures regularly</li>
            </ul>
          </div>
        </LCARSPanel>

        {/* Backup List */}
        <LCARSPanel title="Available Backups">
          {isLoading ? (
            <div style={{ textAlign: 'center', padding: '40px' }}>
              <div style={{ color: '#6688CC' }}>Loading backups...</div>
            </div>
          ) : backups.length === 0 ? (
            <EmptyState>
              No backups available. Create your first backup to get started.
            </EmptyState>
          ) : (
            <BackupList>
              {backups.map((backup) => (
                <BackupItem key={backup.id}>
                  <BackupInfo>
                    <BackupFilename>{backup.filename}</BackupFilename>
                    <BackupMeta>
                      <span>Created: {formatDate(backup.createdAt)}</span>
                      <span>Size: {formatFileSize(backup.size)}</span>
                    </BackupMeta>
                  </BackupInfo>
                  <BackupActions>
                    <LCARSButton
                      onClick={() => downloadBackup(backup)}
                      variant="secondary"
                      size="sm"
                    >
                      Download
                    </LCARSButton>
                  </BackupActions>
                </BackupItem>
              ))}
            </BackupList>
          )}
        </LCARSPanel>
      </BackupGrid>
    </BackupContainer>
  )
}