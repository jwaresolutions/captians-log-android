import React from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import styled from 'styled-components'
import { LCARSHeader } from '../components/lcars/LCARSHeader'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { useNote, useDeleteNote } from '../hooks/useNotes'
import { useBoats } from '../hooks/useBoats'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'
import { useTrips } from '../hooks/useTrips'

const PageContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.lg};
  max-width: 800px;
  margin: 0 auto;
`

const HeaderSection = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${props => props.theme.spacing.md};
`

const ActionButtons = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.md};
`

const NoteMetadata = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${props => props.theme.spacing.md};
  margin-bottom: ${props => props.theme.spacing.lg};
`

const MetadataItem = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.xs};
`

const MetadataLabel = styled.span`
  color: ${props => props.theme.colors.primary.neonCarrot};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  font-size: ${props => props.theme.typography.fontSize.sm};
  letter-spacing: 1px;
`

const MetadataValue = styled.span`
  color: ${props => props.theme.colors.text.primary};
  font-size: ${props => props.theme.typography.fontSize.md};
`

const NoteType = styled.span<{ type: string }>`
  background-color: ${props => {
    switch (props.type) {
      case 'boat': return props.theme.colors.primary.anakiwa
      case 'trip': return props.theme.colors.primary.lilac
      default: return props.theme.colors.primary.neonCarrot
    }
  }};
  color: ${props => props.theme.colors.text.inverse};
  padding: ${props => props.theme.spacing.xs} ${props => props.theme.spacing.sm};
  border-radius: ${props => props.theme.borderRadius.pill};
  font-size: ${props => props.theme.typography.fontSize.sm};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
  display: inline-block;
`

const NoteContent = styled.div`
  background-color: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
  border-radius: ${props => props.theme.borderRadius.md};
  padding: ${props => props.theme.spacing.lg};
  color: ${props => props.theme.colors.text.primary};
  line-height: ${props => props.theme.typography.lineHeight.normal};
  white-space: pre-wrap;
  font-size: ${props => props.theme.typography.fontSize.md};
`

const TagsList = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${props => props.theme.spacing.sm};
`

const Tag = styled.span`
  background-color: ${props => props.theme.colors.primary.lilac};
  color: ${props => props.theme.colors.text.primary};
  padding: ${props => props.theme.spacing.xs} ${props => props.theme.spacing.sm};
  border-radius: ${props => props.theme.borderRadius.pill};
  font-size: ${props => props.theme.typography.fontSize.sm};
`

const EmptyTags = styled.span`
  color: ${props => props.theme.colors.text.muted};
  font-style: italic;
`

const LoadingContainer = styled.div`
  text-align: center;
  padding: ${props => props.theme.spacing.xxl};
  color: ${props => props.theme.colors.text.muted};
`

const ErrorContainer = styled.div`
  text-align: center;
  padding: ${props => props.theme.spacing.xxl};
  color: ${props => props.theme.colors.status.error};
`

export const NoteDetail: React.FC = () => {
  const navigate = useNavigate()
  const { id } = useParams<{ id: string }>()
  
  const { data: note, isLoading, error } = useNote(id || '')
  const { data: boats } = useBoats()
  const { data: trips } = useTrips()
  const deleteNoteMutation = useDeleteNote()

  const handleEdit = () => {
    navigate(`/notes/${id}/edit`)
  }

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this note?')) {
      try {
        await deleteNoteMutation.mutateAsync(id!)
        navigate('/notes')
      } catch (error) {
        console.error('Failed to delete note:', error)
      }
    }
  }

  const handleBack = () => {
    navigate('/notes')
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    })
  }

  const getBoatName = (boatId?: string) => {
    if (!boatId || !boats) return 'Unknown Boat'
    const boat = boats.find(b => b.id === boatId)
    return boat?.name || 'Unknown Boat'
  }

  const getTripInfo = (tripId?: string) => {
    if (!tripId || !trips) return 'Unknown Trip'
    const trip = trips.find(t => t.id === tripId)
    if (!trip) return 'Unknown Trip'
    
    const boatName = getBoatName(trip.boatId)
    const date = new Date(trip.startTime).toLocaleDateString()
    return `${date} - ${boatName}`
  }

  if (isLoading) {
    return (
      <PageContainer>
        <LCARSHeader level={1}>Note Details</LCARSHeader>
        <LCARSPanel>
          <LoadingContainer>
            Loading note...
          </LoadingContainer>
        </LCARSPanel>
      </PageContainer>
    )
  }

  if (error || !note) {
    return (
      <PageContainer>
        <LCARSHeader level={1}>Note Details</LCARSHeader>
        <LCARSPanel>
          <ErrorContainer>
            Note not found or failed to load.
            <div style={{ marginTop: '1rem' }}>
              <LCARSButton onClick={handleBack}>
                Back to Notes
              </LCARSButton>
            </div>
          </ErrorContainer>
        </LCARSPanel>
      </PageContainer>
    )
  }

  return (
    <PageContainer>
      <HeaderSection>
        <LCARSHeader level={1}>Note Details</LCARSHeader>
        <ActionButtons>
          <LCARSButton variant="secondary" onClick={handleBack}>
            Back to Notes
          </LCARSButton>
          <ReadOnlyGuard>
            <LCARSButton variant="accent" onClick={handleEdit}>
              Edit Note
            </LCARSButton>
          </ReadOnlyGuard>
          <ReadOnlyGuard>
            <LCARSButton
              variant="danger"
              onClick={handleDelete}
              disabled={deleteNoteMutation.isPending}
            >
              {deleteNoteMutation.isPending ? 'Deleting...' : 'Delete'}
            </LCARSButton>
          </ReadOnlyGuard>
        </ActionButtons>
      </HeaderSection>

      <LCARSPanel title="Note Information">
        <NoteMetadata>
          <MetadataItem>
            <MetadataLabel>Type</MetadataLabel>
            <MetadataValue>
              <NoteType type={note.type}>
                {note.type}
              </NoteType>
            </MetadataValue>
          </MetadataItem>

          {note.type === 'boat' && note.boatId && (
            <MetadataItem>
              <MetadataLabel>Boat</MetadataLabel>
              <MetadataValue>{getBoatName(note.boatId)}</MetadataValue>
            </MetadataItem>
          )}

          {note.type === 'trip' && note.tripId && (
            <MetadataItem>
              <MetadataLabel>Trip</MetadataLabel>
              <MetadataValue>{getTripInfo(note.tripId)}</MetadataValue>
            </MetadataItem>
          )}

          <MetadataItem>
            <MetadataLabel>Created</MetadataLabel>
            <MetadataValue>{formatDate(note.createdAt)}</MetadataValue>
          </MetadataItem>

          {note.updatedAt !== note.createdAt && (
            <MetadataItem>
              <MetadataLabel>Last Modified</MetadataLabel>
              <MetadataValue>{formatDate(note.updatedAt)}</MetadataValue>
            </MetadataItem>
          )}

          <MetadataItem>
            <MetadataLabel>Tags</MetadataLabel>
            <MetadataValue>
              {note.tags.length > 0 ? (
                <TagsList>
                  {note.tags.map(tag => (
                    <Tag key={tag}>{tag}</Tag>
                  ))}
                </TagsList>
              ) : (
                <EmptyTags>No tags</EmptyTags>
              )}
            </MetadataValue>
          </MetadataItem>
        </NoteMetadata>
      </LCARSPanel>

      <LCARSPanel title="Content">
        <NoteContent>
          {note.content}
        </NoteContent>
      </LCARSPanel>
    </PageContainer>
  )
}