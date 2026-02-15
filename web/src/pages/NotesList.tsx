import React, { useState, useMemo } from 'react'
import { useNavigate } from 'react-router-dom'
import styled from 'styled-components'
import { LCARSHeader } from '../components/lcars/LCARSHeader'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { useNotes, useDeleteNote, useNoteTags } from '../hooks/useNotes'
import { useBoats } from '../hooks/useBoats'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'

const PageContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.lg};
`

const HeaderSection = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${props => props.theme.spacing.md};
`

const FiltersSection = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: ${props => props.theme.spacing.md};
  margin-bottom: ${props => props.theme.spacing.lg};
`

const FilterGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.sm};
`

const FilterLabel = styled.label`
  color: ${props => props.theme.colors.primary.neonCarrot};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  font-size: ${props => props.theme.typography.fontSize.sm};
  letter-spacing: 1px;
`

const FilterSelect = styled.select`
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

  option {
    background-color: ${props => props.theme.colors.surface.dark};
    color: ${props => props.theme.colors.text.primary};
  }
`

const FilterInput = styled.input`
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

const NotesGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: ${props => props.theme.spacing.md};

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`

const NoteCard = styled.div`
  background-color: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.primary.lilac};
  border-radius: ${props => props.theme.borderRadius.lg};
  padding: ${props => props.theme.spacing.md};
  cursor: pointer;
  transition: all ${props => props.theme.animation.normal} ease;

  &:hover {
    border-color: ${props => props.theme.colors.primary.neonCarrot};
    box-shadow: ${props => props.theme.shadows.glow};
  }
`

const NoteHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: ${props => props.theme.spacing.sm};
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
  font-size: ${props => props.theme.typography.fontSize.xs};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
`

const NoteActions = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.xs};
`

const ActionButton = styled.button`
  background: none;
  border: 1px solid ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.primary.anakiwa};
  padding: ${props => props.theme.spacing.xs};
  border-radius: ${props => props.theme.borderRadius.sm};
  cursor: pointer;
  font-size: ${props => props.theme.typography.fontSize.xs};
  transition: all ${props => props.theme.animation.fast} ease;

  &:hover {
    border-color: ${props => props.theme.colors.primary.neonCarrot};
    color: ${props => props.theme.colors.primary.neonCarrot};
  }

  &.danger:hover {
    border-color: ${props => props.theme.colors.status.error};
    color: ${props => props.theme.colors.status.error};
  }
`

const NoteContent = styled.div`
  color: ${props => props.theme.colors.text.primary};
  line-height: ${props => props.theme.typography.lineHeight.normal};
  margin-bottom: ${props => props.theme.spacing.sm};
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
`

const NoteTags = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${props => props.theme.spacing.xs};
  margin-bottom: ${props => props.theme.spacing.sm};
`

const Tag = styled.span`
  background-color: ${props => props.theme.colors.surface.medium};
  color: ${props => props.theme.colors.text.secondary};
  padding: ${props => props.theme.spacing.xs} ${props => props.theme.spacing.sm};
  border-radius: ${props => props.theme.borderRadius.pill};
  font-size: ${props => props.theme.typography.fontSize.xs};
  border: 1px solid ${props => props.theme.colors.primary.anakiwa};
`

const NoteDate = styled.div`
  color: ${props => props.theme.colors.text.muted};
  font-size: ${props => props.theme.typography.fontSize.xs};
  text-align: right;
`

const EmptyState = styled.div`
  text-align: center;
  padding: ${props => props.theme.spacing.xxl};
  color: ${props => props.theme.colors.text.muted};
  
  .empty-icon {
    font-size: 48px;
    margin-bottom: ${props => props.theme.spacing.md};
  }
  
  .empty-title {
    font-size: ${props => props.theme.typography.fontSize.lg};
    font-weight: ${props => props.theme.typography.fontWeight.bold};
    margin-bottom: ${props => props.theme.spacing.sm};
    color: ${props => props.theme.colors.primary.neonCarrot};
  }
`

export const NotesList: React.FC = () => {
  const navigate = useNavigate()
  const [typeFilter, setTypeFilter] = useState<string>('')
  const [boatFilter, setBoatFilter] = useState<string>('')
  const [tagFilter, setTagFilter] = useState<string>('')
  const [searchFilter, setSearchFilter] = useState<string>('')

  const { data: boats } = useBoats()
  const availableTags = useNoteTags()
  
  const filters = useMemo(() => {
    const result: any = {}
    if (typeFilter) result.type = typeFilter
    if (boatFilter) result.boatId = boatFilter
    if (tagFilter) result.tags = [tagFilter]
    return result
  }, [typeFilter, boatFilter, tagFilter])

  const { data: notes, isLoading } = useNotes(filters)
  const deleteNoteMutation = useDeleteNote()

  const filteredNotes = useMemo(() => {
    if (!notes) return []
    
    return notes.filter(note => {
      if (searchFilter) {
        const searchLower = searchFilter.toLowerCase()
        return note.content.toLowerCase().includes(searchLower) ||
               note.tags.some(tag => tag.toLowerCase().includes(searchLower))
      }
      return true
    })
  }, [notes, searchFilter])

  const handleCreateNote = () => {
    navigate('/notes/new')
  }

  const handleEditNote = (noteId: string, event: React.MouseEvent) => {
    event.stopPropagation()
    navigate(`/notes/${noteId}/edit`)
  }

  const handleDeleteNote = async (noteId: string, event: React.MouseEvent) => {
    event.stopPropagation()
    if (window.confirm('Are you sure you want to delete this note?')) {
      try {
        await deleteNoteMutation.mutateAsync(noteId)
      } catch (error) {
        console.error('Failed to delete note:', error)
      }
    }
  }

  const handleViewNote = (noteId: string) => {
    navigate(`/notes/${noteId}`)
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    })
  }

  const getBoatName = (boatId?: string) => {
    if (!boatId || !boats) return null
    const boat = boats.find(b => b.id === boatId)
    return boat?.name
  }

  if (isLoading) {
    return (
      <PageContainer>
        <LCARSHeader level={1}>Notes Database</LCARSHeader>
        <LCARSPanel title="Loading">
          <div style={{ textAlign: 'center', padding: '2rem' }}>
            Loading notes...
          </div>
        </LCARSPanel>
      </PageContainer>
    )
  }

  return (
    <PageContainer>
      <HeaderSection>
        <LCARSHeader level={1}>Notes Database</LCARSHeader>
        <ReadOnlyGuard>
          <LCARSButton onClick={handleCreateNote}>
            Create New Note
          </LCARSButton>
        </ReadOnlyGuard>
      </HeaderSection>

      <LCARSPanel title="Filters" variant="secondary">
        <FiltersSection>
          <FilterGroup>
            <FilterLabel>Note Type</FilterLabel>
            <FilterSelect
              value={typeFilter}
              onChange={(e) => setTypeFilter(e.target.value)}
            >
              <option value="">All Types</option>
              <option value="general">General</option>
              <option value="boat">Boat-Specific</option>
              <option value="trip">Trip</option>
            </FilterSelect>
          </FilterGroup>

          <FilterGroup>
            <FilterLabel>Boat</FilterLabel>
            <FilterSelect
              value={boatFilter}
              onChange={(e) => setBoatFilter(e.target.value)}
              disabled={typeFilter === 'general' || typeFilter === 'trip'}
            >
              <option value="">All Boats</option>
              {boats?.map(boat => (
                <option key={boat.id} value={boat.id}>
                  {boat.name}
                </option>
              ))}
            </FilterSelect>
          </FilterGroup>

          <FilterGroup>
            <FilterLabel>Tag</FilterLabel>
            <FilterSelect
              value={tagFilter}
              onChange={(e) => setTagFilter(e.target.value)}
            >
              <option value="">All Tags</option>
              {availableTags.map(tag => (
                <option key={tag} value={tag}>
                  {tag}
                </option>
              ))}
            </FilterSelect>
          </FilterGroup>

          <FilterGroup>
            <FilterLabel>Search</FilterLabel>
            <FilterInput
              type="text"
              placeholder="Search notes content..."
              value={searchFilter}
              onChange={(e) => setSearchFilter(e.target.value)}
            />
          </FilterGroup>
        </FiltersSection>
      </LCARSPanel>

      {filteredNotes.length === 0 ? (
        <LCARSPanel>
          <EmptyState>
            <div className="empty-icon">📝</div>
            <div className="empty-title">No Notes Found</div>
            <div>
              {notes?.length === 0 
                ? "Create your first note to get started."
                : "Try adjusting your filters to find notes."
              }
            </div>
          </EmptyState>
        </LCARSPanel>
      ) : (
        <NotesGrid>
          {filteredNotes.map(note => (
            <NoteCard key={note.id} onClick={() => handleViewNote(note.id)}>
              <NoteHeader>
                <NoteType type={note.type}>
                  {note.type}
                  {note.type === 'boat' && getBoatName(note.boatId) && ` - ${getBoatName(note.boatId)}`}
                </NoteType>
                <NoteActions>
                  <ReadOnlyGuard>
                    <ActionButton onClick={(e) => handleEditNote(note.id, e)}>
                      Edit
                    </ActionButton>
                  </ReadOnlyGuard>
                  <ReadOnlyGuard>
                    <ActionButton
                      className="danger"
                      onClick={(e) => handleDeleteNote(note.id, e)}
                    >
                      Delete
                    </ActionButton>
                  </ReadOnlyGuard>
                </NoteActions>
              </NoteHeader>
              
              <NoteContent>
                {note.content}
              </NoteContent>
              
              {note.tags.length > 0 && (
                <NoteTags>
                  {note.tags.map(tag => (
                    <Tag key={tag}>{tag}</Tag>
                  ))}
                </NoteTags>
              )}
              
              <NoteDate>
                {formatDate(note.createdAt)}
                {note.updatedAt !== note.createdAt && ' (edited)'}
              </NoteDate>
            </NoteCard>
          ))}
        </NotesGrid>
      )}
    </PageContainer>
  )
}