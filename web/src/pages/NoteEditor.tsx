import React, { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import styled from 'styled-components'
import { LCARSHeader } from '../components/lcars/LCARSHeader'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { useNote, useCreateNote, useUpdateNote, useNoteTags } from '../hooks/useNotes'
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

const FormSection = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.lg};
`

const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.sm};
`

const FormLabel = styled.label`
  color: ${props => props.theme.colors.primary.neonCarrot};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  font-size: ${props => props.theme.typography.fontSize.sm};
  letter-spacing: 1px;
`

const FormSelect = styled.select`
  background-color: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.text.primary};
  padding: ${props => props.theme.spacing.sm};
  border-radius: ${props => props.theme.borderRadius.md};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: ${props => props.theme.typography.fontSize.md};

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 10px rgba(255, 153, 102, 0.3);
  }

  option {
    background-color: ${props => props.theme.colors.surface.dark};
    color: ${props => props.theme.colors.text.primary};
  }
`

const FormTextarea = styled.textarea`
  background-color: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.text.primary};
  padding: ${props => props.theme.spacing.md};
  border-radius: ${props => props.theme.borderRadius.md};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: ${props => props.theme.typography.fontSize.md};
  line-height: ${props => props.theme.typography.lineHeight.normal};
  min-height: 200px;
  resize: vertical;

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 10px rgba(255, 153, 102, 0.3);
  }

  &::placeholder {
    color: ${props => props.theme.colors.text.muted};
  }
`

const TagsSection = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.sm};
`

const TagInput = styled.input`
  background-color: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.text.primary};
  padding: ${props => props.theme.spacing.sm};
  border-radius: ${props => props.theme.borderRadius.md};
  font-family: ${props => props.theme.typography.fontFamily.primary};

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 10px rgba(255, 153, 102, 0.3);
  }

  &::placeholder {
    color: ${props => props.theme.colors.text.muted};
  }
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
  display: flex;
  align-items: center;
  gap: ${props => props.theme.spacing.xs};

  .remove-tag {
    background: none;
    border: none;
    color: ${props => props.theme.colors.text.primary};
    cursor: pointer;
    font-size: ${props => props.theme.typography.fontSize.sm};
    padding: 0;

    &:hover {
      color: ${props => props.theme.colors.status.error};
    }
  }
`

const SuggestedTags = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${props => props.theme.spacing.xs};
  margin-top: ${props => props.theme.spacing.sm};
`

const SuggestedTag = styled.button`
  background: none;
  border: 1px solid ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.primary.anakiwa};
  padding: ${props => props.theme.spacing.xs} ${props => props.theme.spacing.sm};
  border-radius: ${props => props.theme.borderRadius.pill};
  font-size: ${props => props.theme.typography.fontSize.xs};
  cursor: pointer;
  transition: all ${props => props.theme.animation.fast} ease;

  &:hover {
    border-color: ${props => props.theme.colors.primary.neonCarrot};
    color: ${props => props.theme.colors.primary.neonCarrot};
    background-color: ${props => props.theme.colors.primary.neonCarrot}20;
  }
`

const ActionButtons = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.md};
  justify-content: flex-end;
  margin-top: ${props => props.theme.spacing.lg};
`

const ErrorMessage = styled.div`
  color: ${props => props.theme.colors.status.error};
  background-color: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.status.error};
  padding: ${props => props.theme.spacing.md};
  border-radius: ${props => props.theme.borderRadius.md};
  margin-bottom: ${props => props.theme.spacing.md};
`

export const NoteEditor: React.FC = () => {
  const navigate = useNavigate()
  const { id } = useParams<{ id: string }>()
  const isEditing = !!id && id !== 'new'

  const [noteType, setNoteType] = useState<'general' | 'boat' | 'trip'>('general')
  const [boatId, setBoatId] = useState<string>('')
  const [tripId, setTripId] = useState<string>('')
  const [content, setContent] = useState<string>('')
  const [tags, setTags] = useState<string[]>([])
  const [tagInput, setTagInput] = useState<string>('')
  const [error, setError] = useState<string>('')

  const { data: note, isLoading: noteLoading } = useNote(id || '')
  const { data: boats } = useBoats()
  const { data: trips } = useTrips()
  const availableTags = useNoteTags()
  const createNoteMutation = useCreateNote()
  const updateNoteMutation = useUpdateNote()

  // Load existing note data when editing
  useEffect(() => {
    if (note && isEditing) {
      setNoteType(note.type)
      setBoatId(note.boatId || '')
      setTripId(note.tripId || '')
      setContent(note.content)
      setTags(note.tags)
    }
  }, [note, isEditing])

  const handleAddTag = () => {
    const trimmedTag = tagInput.trim()
    if (trimmedTag && !tags.includes(trimmedTag)) {
      setTags([...tags, trimmedTag])
      setTagInput('')
    }
  }

  const handleRemoveTag = (tagToRemove: string) => {
    setTags(tags.filter(tag => tag !== tagToRemove))
  }

  const handleAddSuggestedTag = (tag: string) => {
    if (!tags.includes(tag)) {
      setTags([...tags, tag])
    }
  }

  const handleTagInputKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      handleAddTag()
    }
  }

  const handleSave = async () => {
    setError('')

    if (!content.trim()) {
      setError('Note content is required')
      return
    }

    if (noteType === 'boat' && !boatId) {
      setError('Please select a boat for boat-specific notes')
      return
    }

    if (noteType === 'trip' && !tripId) {
      setError('Please select a trip for trip notes')
      return
    }

    try {
      const noteData = {
        content: content.trim(),
        type: noteType,
        boatId: noteType === 'boat' ? boatId : undefined,
        tripId: noteType === 'trip' ? tripId : undefined,
        tags,
      }

      if (isEditing) {
        await updateNoteMutation.mutateAsync({ id: id!, data: noteData })
      } else {
        await createNoteMutation.mutateAsync(noteData)
      }

      navigate('/notes')
    } catch (error) {
      console.error('Failed to save note:', error)
      setError('Failed to save note. Please try again.')
    }
  }

  const handleCancel = () => {
    navigate('/notes')
  }

  const suggestedTags = availableTags.filter(tag => !tags.includes(tag))

  if (noteLoading && isEditing) {
    return (
      <PageContainer>
        <LCARSHeader level={1}>Loading Note</LCARSHeader>
        <LCARSPanel title="Loading">
          <div style={{ textAlign: 'center', padding: '2rem' }}>
            Loading note data...
          </div>
        </LCARSPanel>
      </PageContainer>
    )
  }

  return (
    <PageContainer>
      <HeaderSection>
        <LCARSHeader level={1}>
          {isEditing ? 'Edit Note' : 'Create New Note'}
        </LCARSHeader>
      </HeaderSection>

      <LCARSPanel title="Note Details">
        <FormSection>
          {error && <ErrorMessage>{error}</ErrorMessage>}

          <FormGroup>
            <FormLabel>Note Type</FormLabel>
            <FormSelect
              value={noteType}
              onChange={(e) => {
                setNoteType(e.target.value as 'general' | 'boat' | 'trip')
                // Reset associated IDs when type changes
                setBoatId('')
                setTripId('')
              }}
            >
              <option value="general">General Note</option>
              <option value="boat">Boat-Specific Note</option>
              <option value="trip">Trip Note</option>
            </FormSelect>
          </FormGroup>

          {noteType === 'boat' && (
            <FormGroup>
              <FormLabel>Boat</FormLabel>
              <FormSelect
                value={boatId}
                onChange={(e) => setBoatId(e.target.value)}
              >
                <option value="">Select a boat</option>
                {boats?.map(boat => (
                  <option key={boat.id} value={boat.id}>
                    {boat.name}
                  </option>
                ))}
              </FormSelect>
            </FormGroup>
          )}

          {noteType === 'trip' && (
            <FormGroup>
              <FormLabel>Trip</FormLabel>
              <FormSelect
                value={tripId}
                onChange={(e) => setTripId(e.target.value)}
              >
                <option value="">Select a trip</option>
                {trips?.map(trip => (
                  <option key={trip.id} value={trip.id}>
                    {new Date(trip.startTime).toLocaleDateString()} - {
                      boats?.find(b => b.id === trip.boatId)?.name || 'Unknown Boat'
                    }
                  </option>
                ))}
              </FormSelect>
            </FormGroup>
          )}

          <FormGroup>
            <FormLabel>Content</FormLabel>
            <FormTextarea
              value={content}
              onChange={(e) => setContent(e.target.value)}
              placeholder="Enter your note content here..."
            />
          </FormGroup>

          <TagsSection>
            <FormLabel>Tags</FormLabel>
            <TagInput
              type="text"
              value={tagInput}
              onChange={(e) => setTagInput(e.target.value)}
              onKeyPress={handleTagInputKeyPress}
              placeholder="Add a tag and press Enter"
            />
            
            {tags.length > 0 && (
              <TagsList>
                {tags.map(tag => (
                  <Tag key={tag}>
                    {tag}
                    <button
                      className="remove-tag"
                      onClick={() => handleRemoveTag(tag)}
                      type="button"
                    >
                      ×
                    </button>
                  </Tag>
                ))}
              </TagsList>
            )}

            {suggestedTags.length > 0 && (
              <div>
                <FormLabel style={{ fontSize: '12px', marginBottom: '8px' }}>
                  Suggested Tags
                </FormLabel>
                <SuggestedTags>
                  {suggestedTags.slice(0, 10).map(tag => (
                    <SuggestedTag
                      key={tag}
                      onClick={() => handleAddSuggestedTag(tag)}
                      type="button"
                    >
                      {tag}
                    </SuggestedTag>
                  ))}
                </SuggestedTags>
              </div>
            )}
          </TagsSection>

          <ActionButtons>
            <LCARSButton variant="secondary" onClick={handleCancel}>
              Cancel
            </LCARSButton>
            <ReadOnlyGuard>
              <LCARSButton
                onClick={handleSave}
                disabled={createNoteMutation.isPending || updateNoteMutation.isPending}
              >
                {createNoteMutation.isPending || updateNoteMutation.isPending
                  ? 'Saving...'
                  : 'Save Note'
                }
              </LCARSButton>
            </ReadOnlyGuard>
          </ActionButtons>
        </FormSection>
      </LCARSPanel>
    </PageContainer>
  )
}