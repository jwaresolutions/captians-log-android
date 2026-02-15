import React, { useState, useEffect, useRef, useMemo } from 'react'
import { useSearchParams } from 'react-router-dom'
import styled, { keyframes, css } from 'styled-components'
import {
  useTodoLists,
  useTodoList,
  useCreateTodoList,
  useUpdateTodoList,
  useDeleteTodoList,
  useAddTodoItem,
  useToggleTodoItem,
  useUpdateTodoItem,
  useDeleteTodoItem,
} from '../hooks/useTodos'
import { useBoats } from '../hooks/useBoats'
import { TodoItemRow } from '../components/todos/TodoItemRow'
import { TodoProgressBar } from '../components/todos/TodoProgressBar'
import { TodoEmptyState } from '../components/todos/TodoEmptyState'
import { LCARSButton } from '../components/lcars/LCARSButton'
import { ReadOnlyGuard } from '../components/ReadOnlyGuard'
import { LCARSModal } from '../components/lcars/LCARSModal'
import { LCARSConfirmModal } from '../components/lcars/LCARSConfirmModal'
import { TodoList } from '../types/api'

// ---------------------------------------------------------------------------
// Animations
// ---------------------------------------------------------------------------

const pulse = keyframes`
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
`

const fadeIn = keyframes`
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
`

const slideInLeft = keyframes`
  from { opacity: 0; transform: translateX(-12px); }
  to { opacity: 1; transform: translateX(0); }
`

// ---------------------------------------------------------------------------
// Layout
// ---------------------------------------------------------------------------

const MOBILE_BP = '768px'
const SIDEBAR_WIDTH = '300px'
const LCARS_GAP = '3px'

const PageContainer = styled.div`
  display: flex;
  min-height: calc(100vh - 140px);
  gap: ${LCARS_GAP};

  @media (max-width: ${MOBILE_BP}) {
    flex-direction: column;
    gap: 0;
  }
`

const SidebarPanel = styled.aside<{ $hidden?: boolean }>`
  width: ${SIDEBAR_WIDTH};
  min-width: ${SIDEBAR_WIDTH};
  background: ${p => p.theme.colors.surface.dark};
  display: flex;
  flex-direction: column;
  overflow: hidden;

  @media (max-width: ${MOBILE_BP}) {
    width: 100%;
    min-width: 100%;
    display: ${p => p.$hidden ? 'none' : 'flex'};
  }
`

const MainPanel = styled.section<{ $hidden?: boolean }>`
  flex: 1;
  background: ${p => p.theme.colors.background};
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: ${fadeIn} 300ms ease;

  @media (max-width: ${MOBILE_BP}) {
    display: ${p => p.$hidden ? 'none' : 'flex'};
  }
`

// ---------------------------------------------------------------------------
// Sidebar internals
// ---------------------------------------------------------------------------

const SidebarHeader = styled.div`
  padding: ${p => p.theme.spacing.lg} ${p => p.theme.spacing.md};
  display: flex;
  flex-direction: column;
  gap: ${p => p.theme.spacing.md};
  flex-shrink: 0;
`

const SidebarTitle = styled.h2`
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: ${p => p.theme.typography.fontSize.lg};
  font-weight: ${p => p.theme.typography.fontWeight.bold};
  color: ${p => p.theme.colors.primary.neonCarrot};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.extraWide};
  margin: 0;
`

const FilterSelect = styled.select`
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: ${p => p.theme.typography.fontSize.sm};
  font-weight: ${p => p.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.normal};
  color: ${p => p.theme.colors.text.light};
  background: ${p => p.theme.colors.surface.dark};
  border: 2px solid ${p => p.theme.colors.primary.anakiwa};
  border-radius: ${p => p.theme.borderRadius.md};
  padding: 6px 10px;
  outline: none;
  cursor: pointer;

  &:focus {
    border-color: ${p => p.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 12px ${p => p.theme.colors.primary.neonCarrot}40;
  }

  option {
    background: ${p => p.theme.colors.surface.dark};
  }
`

const SidebarList = styled.div`
  flex: 1;
  overflow-y: auto;
  padding: 0 ${p => p.theme.spacing.md} ${p => p.theme.spacing.md};
  display: flex;
  flex-direction: column;
  gap: ${p => p.theme.spacing.sm};
`

// ---------------------------------------------------------------------------
// List card (inline — no separate file needed)
// ---------------------------------------------------------------------------

const ListCard = styled.button<{ $active: boolean }>`
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 100%;
  padding: ${p => p.theme.spacing.md};
  background: ${p => p.$active ? p.theme.colors.surface.medium : 'transparent'};
  border: 2px solid ${p => p.$active ? p.theme.colors.primary.neonCarrot : p.theme.colors.surface.medium};
  border-radius: ${p => p.theme.borderRadius.md};
  cursor: pointer;
  text-align: left;
  transition: all 200ms ease;
  animation: ${slideInLeft} 300ms ease both;

  ${p => p.$active && css`
    box-shadow: 0 0 10px ${p.theme.colors.primary.neonCarrot}30;
  `}

  &:hover {
    border-color: ${p => p.theme.colors.primary.neonCarrot};
    background: ${p => p.theme.colors.surface.medium};
  }
`

const ListCardTitle = styled.span`
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: ${p => p.theme.typography.fontSize.md};
  font-weight: ${p => p.theme.typography.fontWeight.bold};
  color: ${p => p.theme.colors.text.light};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.normal};
`

const ListCardMeta = styled.span`
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: 11px;
  color: ${p => p.theme.colors.text.muted};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.normal};
`

const TypePill = styled.span<{ $type: 'general' | 'boat' }>`
  display: inline-block;
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: 10px;
  font-weight: ${p => p.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.wide};
  color: ${p => p.theme.colors.text.inverse};
  background: ${p => p.$type === 'boat' ? p.theme.colors.primary.anakiwa : p.theme.colors.primary.neonCarrot};
  padding: 2px 10px;
  border-radius: ${p => p.theme.borderRadius.pill};
`

// ---------------------------------------------------------------------------
// Main panel header
// ---------------------------------------------------------------------------

const DetailHeader = styled.div`
  padding: ${p => p.theme.spacing.lg};
  display: flex;
  flex-direction: column;
  gap: ${p => p.theme.spacing.md};
  flex-shrink: 0;
  border-bottom: 2px solid ${p => p.theme.colors.surface.medium};
`

const DetailTitleRow = styled.div`
  display: flex;
  align-items: center;
  gap: ${p => p.theme.spacing.md};
  flex-wrap: wrap;
`

const DetailTitle = styled.h2`
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: ${p => p.theme.typography.fontSize.xl};
  font-weight: ${p => p.theme.typography.fontWeight.bold};
  color: ${p => p.theme.colors.text.primary};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.wide};
  margin: 0;
  cursor: pointer;
  transition: color 200ms ease;

  &:hover {
    color: ${p => p.theme.colors.primary.neonCarrot};
  }
`

const TitleEditInput = styled.input`
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: ${p => p.theme.typography.fontSize.xl};
  font-weight: ${p => p.theme.typography.fontWeight.bold};
  color: ${p => p.theme.colors.text.primary};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.wide};
  background: ${p => p.theme.colors.surface.dark};
  border: 2px solid ${p => p.theme.colors.primary.anakiwa};
  border-radius: ${p => p.theme.borderRadius.md};
  padding: 4px 12px;
  outline: none;

  &:focus {
    border-color: ${p => p.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 12px ${p => p.theme.colors.primary.neonCarrot}40;
  }
`

const StatsRow = styled.div`
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: 12px;
  color: ${p => p.theme.colors.text.muted};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.normal};
`

const MobileBackButton = styled.button`
  display: none;

  @media (max-width: ${MOBILE_BP}) {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    background: none;
    border: none;
    color: ${p => p.theme.colors.primary.anakiwa};
    font-family: ${p => p.theme.typography.fontFamily.primary};
    font-size: ${p => p.theme.typography.fontSize.sm};
    font-weight: ${p => p.theme.typography.fontWeight.bold};
    text-transform: uppercase;
    letter-spacing: ${p => p.theme.typography.letterSpacing.normal};
    padding: 0;
    cursor: pointer;
    margin-bottom: ${p => p.theme.spacing.sm};
  }
`

// ---------------------------------------------------------------------------
// Items area
// ---------------------------------------------------------------------------

const ItemsContainer = styled.div`
  flex: 1;
  overflow-y: auto;
  padding: ${p => p.theme.spacing.lg};
  display: flex;
  flex-direction: column;
  gap: ${p => p.theme.spacing.sm};
`

const AddItemRow = styled.form`
  display: flex;
  gap: ${p => p.theme.spacing.sm};
  margin-bottom: ${p => p.theme.spacing.md};
  flex-shrink: 0;
`

const AddItemInput = styled.input`
  flex: 1;
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: 14px;
  color: ${p => p.theme.colors.text.light};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.normal};
  background: ${p => p.theme.colors.surface.dark};
  border: 2px solid ${p => p.theme.colors.primary.anakiwa};
  border-radius: ${p => p.theme.borderRadius.md};
  padding: 10px 14px;
  outline: none;

  &:focus {
    border-color: ${p => p.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 12px ${p => p.theme.colors.primary.neonCarrot}40;
  }

  &::placeholder {
    color: ${p => p.theme.colors.text.muted};
    opacity: 0.6;
  }
`

// ---------------------------------------------------------------------------
// Footer actions
// ---------------------------------------------------------------------------

const FooterActions = styled.div`
  padding: ${p => p.theme.spacing.md} ${p => p.theme.spacing.lg};
  border-top: 2px solid ${p => p.theme.colors.surface.medium};
  display: flex;
  justify-content: flex-end;
  flex-shrink: 0;
`

// ---------------------------------------------------------------------------
// Loading
// ---------------------------------------------------------------------------

const LoadingText = styled.div`
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: ${p => p.theme.typography.fontSize.lg};
  font-weight: ${p => p.theme.typography.fontWeight.bold};
  color: ${p => p.theme.colors.primary.neonCarrot};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.extraWide};
  text-align: center;
  padding: 80px 24px;
  animation: ${pulse} 1.5s ease infinite;
`

// ---------------------------------------------------------------------------
// Create list modal internals
// ---------------------------------------------------------------------------

const ModalField = styled.div`
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: ${p => p.theme.spacing.md};
`

const ModalLabel = styled.label`
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: 11px;
  font-weight: ${p => p.theme.typography.fontWeight.bold};
  color: ${p => p.theme.colors.primary.neonCarrot};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.wide};
`

const ModalInput = styled.input`
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: 14px;
  color: ${p => p.theme.colors.text.light};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.normal};
  background: ${p => p.theme.colors.surface.dark};
  border: 2px solid ${p => p.theme.colors.primary.anakiwa};
  border-radius: ${p => p.theme.borderRadius.md};
  padding: 10px 14px;
  outline: none;

  &:focus {
    border-color: ${p => p.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 12px ${p => p.theme.colors.primary.neonCarrot}40;
  }
`

const ModalSelect = styled.select`
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: 14px;
  font-weight: ${p => p.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.normal};
  color: ${p => p.theme.colors.text.light};
  background: ${p => p.theme.colors.surface.dark};
  border: 2px solid ${p => p.theme.colors.primary.anakiwa};
  border-radius: ${p => p.theme.borderRadius.md};
  padding: 10px 14px;
  outline: none;
  cursor: pointer;

  &:focus {
    border-color: ${p => p.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 12px ${p => p.theme.colors.primary.neonCarrot}40;
  }

  option {
    background: ${p => p.theme.colors.surface.dark};
  }
`

const ModalButtonRow = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: ${p => p.theme.spacing.md};
  margin-top: ${p => p.theme.spacing.md};
`

// ===========================================================================
// Component
// ===========================================================================

export const TodoPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams()
  const selectedListId = searchParams.get('list') || ''

  // --- Data hooks ---
  const { data: lists, isLoading: listsLoading } = useTodoLists()
  const { data: selectedList, isLoading: listLoading } = useTodoList(selectedListId)
  const { data: boats } = useBoats()

  // --- Mutations ---
  const createList = useCreateTodoList()
  const updateList = useUpdateTodoList()
  const deleteList = useDeleteTodoList()
  const addItem = useAddTodoItem()
  const toggleItem = useToggleTodoItem()
  const updateItem = useUpdateTodoItem()
  const deleteItem = useDeleteTodoItem()

  // --- Local state ---
  const [typeFilter, setTypeFilter] = useState<'all' | 'general' | 'boat'>('all')
  const [showCreateModal, setShowCreateModal] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)
  const [isEditingTitle, setIsEditingTitle] = useState(false)
  const [titleEditValue, setTitleEditValue] = useState('')
  const [newItemContent, setNewItemContent] = useState('')

  // Create modal state
  const [createTitle, setCreateTitle] = useState('')
  const [createType, setCreateType] = useState<'general' | 'boat'>('general')
  const [createBoatId, setCreateBoatId] = useState('')

  // Mobile: show main when list selected
  const isMobileDetailView = !!selectedListId

  const titleInputRef = useRef<HTMLInputElement>(null)
  const addItemInputRef = useRef<HTMLInputElement>(null)

  // --- Auto-select first list ---
  useEffect(() => {
    if (!selectedListId && lists && lists.length > 0) {
      setSearchParams({ list: lists[0].id }, { replace: true })
    }
  }, [lists, selectedListId, setSearchParams])

  // --- Focus title input on edit ---
  useEffect(() => {
    if (isEditingTitle && titleInputRef.current) {
      titleInputRef.current.focus()
      titleInputRef.current.select()
    }
  }, [isEditingTitle])

  // --- Filtered lists ---
  const filteredLists = useMemo(() => {
    if (!lists) return []
    if (typeFilter === 'all') return lists
    return lists.filter(l => l.type === typeFilter)
  }, [lists, typeFilter])

  // --- Sorted items (completed at bottom) ---
  const sortedItems = useMemo(() => {
    if (!selectedList?.items) return []
    return [...selectedList.items].sort((a, b) => {
      if (a.completed === b.completed) return 0
      return a.completed ? 1 : -1
    })
  }, [selectedList])

  // --- Stats ---
  const completedCount = selectedList?.items.filter(i => i.completed).length ?? 0
  const totalCount = selectedList?.items.length ?? 0
  const percentage = totalCount > 0 ? (completedCount / totalCount) * 100 : 0

  // --- Boat name lookup ---
  const getBoatName = (boatId?: string) => {
    if (!boatId || !boats) return ''
    const boat = boats.find(b => b.id === boatId)
    return boat?.name ?? ''
  }

  // --- Handlers ---
  const selectList = (id: string) => {
    setSearchParams({ list: id })
  }

  const handleTitleClick = () => {
    if (selectedList) {
      setTitleEditValue(selectedList.title)
      setIsEditingTitle(true)
    }
  }

  const handleTitleSave = () => {
    const trimmed = titleEditValue.trim()
    if (trimmed && trimmed !== selectedList?.title) {
      updateList.mutate({ id: selectedListId, data: { title: trimmed } })
    }
    setIsEditingTitle(false)
  }

  const handleTitleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') handleTitleSave()
    else if (e.key === 'Escape') setIsEditingTitle(false)
  }

  const handleAddItem = (e: React.FormEvent) => {
    e.preventDefault()
    const trimmed = newItemContent.trim()
    if (!trimmed || !selectedListId) return
    addItem.mutate({ listId: selectedListId, content: trimmed })
    setNewItemContent('')
    addItemInputRef.current?.focus()
  }

  const handleToggleItem = (itemId: string) => {
    toggleItem.mutate({ itemId, listId: selectedListId })
  }

  const handleUpdateItem = (itemId: string, content: string) => {
    updateItem.mutate({ itemId, listId: selectedListId, data: { content } })
  }

  const handleDeleteItem = (itemId: string) => {
    deleteItem.mutate({ itemId, listId: selectedListId })
  }

  const handleDeleteList = () => {
    deleteList.mutate(selectedListId, {
      onSuccess: () => {
        setShowDeleteConfirm(false)
        setSearchParams({}, { replace: true })
      },
    })
  }

  const handleCreateList = () => {
    const trimmed = createTitle.trim()
    if (!trimmed) return
    createList.mutate(
      {
        title: trimmed,
        type: createType,
        boatId: createType === 'boat' ? createBoatId || undefined : undefined,
      },
      {
        onSuccess: (newList: TodoList) => {
          setShowCreateModal(false)
          setCreateTitle('')
          setCreateType('general')
          setCreateBoatId('')
          setSearchParams({ list: newList.id })
        },
      }
    )
  }

  const handleMobileBack = () => {
    setSearchParams({}, { replace: true })
  }

  // --- Loading ---
  if (listsLoading) {
    return (
      <PageContainer>
        <LoadingText>Accessing Database...</LoadingText>
      </PageContainer>
    )
  }

  // --- Render ---
  return (
    <PageContainer>
      {/* ================================================================= */}
      {/* LEFT SIDEBAR                                                      */}
      {/* ================================================================= */}
      <SidebarPanel $hidden={isMobileDetailView}>
        <SidebarHeader>
          <SidebarTitle>Task Lists</SidebarTitle>
          <ReadOnlyGuard>
            <LCARSButton variant="secondary" size="sm" onClick={() => setShowCreateModal(true)}>
              New List
            </LCARSButton>
          </ReadOnlyGuard>
          <FilterSelect
            value={typeFilter}
            onChange={e => setTypeFilter(e.target.value as 'all' | 'general' | 'boat')}
            aria-label="Filter by type"
          >
            <option value="all">All Types</option>
            <option value="general">General</option>
            <option value="boat">Boat</option>
          </FilterSelect>
        </SidebarHeader>

        <SidebarList>
          {filteredLists.length === 0 ? (
            <ListCardMeta style={{ textAlign: 'center', padding: '24px 0' }}>
              {lists && lists.length > 0 ? 'No matching lists' : 'No lists yet'}
            </ListCardMeta>
          ) : (
            filteredLists.map((list, i) => {
              const done = list.items.filter(it => it.completed).length
              const total = list.items.length
              return (
                <ListCard
                  key={list.id}
                  $active={list.id === selectedListId}
                  onClick={() => selectList(list.id)}
                  style={{ animationDelay: `${i * 40}ms` }}
                  aria-current={list.id === selectedListId ? 'true' : undefined}
                >
                  <ListCardTitle>{list.title}</ListCardTitle>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <TypePill $type={list.type}>
                      {list.type === 'boat' ? `Boat - ${getBoatName(list.boatId)}` : 'General'}
                    </TypePill>
                    <ListCardMeta>{done}/{total} done</ListCardMeta>
                  </div>
                </ListCard>
              )
            })
          )}
        </SidebarList>
      </SidebarPanel>

      {/* ================================================================= */}
      {/* RIGHT MAIN PANEL                                                  */}
      {/* ================================================================= */}
      <MainPanel $hidden={!isMobileDetailView && !!selectedListId === false && false}>
        {!selectedListId ? (
          <TodoEmptyState
            title={lists && lists.length > 0 ? 'Select a List' : 'Create Your First List'}
            message={
              lists && lists.length > 0
                ? 'Choose a task list from the sidebar to view its items'
                : 'Get started by creating a new task list using the button on the left'
            }
          />
        ) : listLoading ? (
          <LoadingText>Loading List Data...</LoadingText>
        ) : selectedList ? (
          <>
            <DetailHeader>
              <MobileBackButton onClick={handleMobileBack}>
                &larr; Back to Lists
              </MobileBackButton>

              <DetailTitleRow>
                {isEditingTitle ? (
                  <TitleEditInput
                    ref={titleInputRef}
                    value={titleEditValue}
                    onChange={e => setTitleEditValue(e.target.value)}
                    onKeyDown={handleTitleKeyDown}
                    onBlur={handleTitleSave}
                  />
                ) : (
                  <DetailTitle onClick={handleTitleClick}>
                    {selectedList.title}
                  </DetailTitle>
                )}
                <TypePill $type={selectedList.type}>
                  {selectedList.type === 'boat'
                    ? `Boat - ${getBoatName(selectedList.boatId)}`
                    : 'General'}
                </TypePill>
              </DetailTitleRow>

              <StatsRow>
                {completedCount} of {totalCount} completed
                {totalCount > 0 && ` \u2014 ${Math.round(percentage)}%`}
              </StatsRow>

              <TodoProgressBar percentage={percentage} />
            </DetailHeader>

            <ItemsContainer>
              <ReadOnlyGuard fallback={null}>
                <AddItemRow onSubmit={handleAddItem}>
                  <AddItemInput
                    ref={addItemInputRef}
                    value={newItemContent}
                    onChange={e => setNewItemContent(e.target.value)}
                    placeholder="Add new task..."
                    aria-label="New task content"
                  />
                  <LCARSButton variant="primary" size="sm" type="submit" onClick={() => {}}>
                    Add
                  </LCARSButton>
                </AddItemRow>
              </ReadOnlyGuard>

              {sortedItems.length === 0 ? (
                <ListCardMeta style={{ textAlign: 'center', padding: '24px 0' }}>
                  No items yet. Add your first task above.
                </ListCardMeta>
              ) : (
                sortedItems.map(item => (
                  <TodoItemRow
                    key={item.id}
                    item={item}
                    onToggle={handleToggleItem}
                    onUpdate={handleUpdateItem}
                    onDelete={handleDeleteItem}
                  />
                ))
              )}
            </ItemsContainer>

            <FooterActions>
              <ReadOnlyGuard>
                <LCARSButton
                  variant="danger"
                  size="sm"
                  onClick={() => setShowDeleteConfirm(true)}
                >
                  Delete List
                </LCARSButton>
              </ReadOnlyGuard>
            </FooterActions>
          </>
        ) : (
          <TodoEmptyState
            title="List Not Found"
            message="The selected list could not be loaded. It may have been deleted."
          />
        )}
      </MainPanel>

      {/* ================================================================= */}
      {/* CREATE LIST MODAL                                                 */}
      {/* ================================================================= */}
      <LCARSModal
        isOpen={showCreateModal}
        onClose={() => setShowCreateModal(false)}
        title="Create Task List"
      >
        <ModalField>
          <ModalLabel htmlFor="create-title">Title</ModalLabel>
          <ModalInput
            id="create-title"
            value={createTitle}
            onChange={e => setCreateTitle(e.target.value)}
            placeholder="Enter list title..."
            autoFocus
            onKeyDown={e => { if (e.key === 'Enter') handleCreateList() }}
          />
        </ModalField>

        <ModalField>
          <ModalLabel htmlFor="create-type">Type</ModalLabel>
          <ModalSelect
            id="create-type"
            value={createType}
            onChange={e => setCreateType(e.target.value as 'general' | 'boat')}
          >
            <option value="general">General</option>
            <option value="boat">Boat</option>
          </ModalSelect>
        </ModalField>

        {createType === 'boat' && (
          <ModalField>
            <ModalLabel htmlFor="create-boat">Vessel</ModalLabel>
            <ModalSelect
              id="create-boat"
              value={createBoatId}
              onChange={e => setCreateBoatId(e.target.value)}
            >
              <option value="">Select a vessel...</option>
              {boats?.map(boat => (
                <option key={boat.id} value={boat.id}>{boat.name}</option>
              ))}
            </ModalSelect>
          </ModalField>
        )}

        <ModalButtonRow>
          <LCARSButton variant="secondary" size="sm" onClick={() => setShowCreateModal(false)}>
            Cancel
          </LCARSButton>
          <LCARSButton
            variant="primary"
            size="sm"
            onClick={handleCreateList}
            disabled={!createTitle.trim() || (createType === 'boat' && !createBoatId)}
          >
            Create
          </LCARSButton>
        </ModalButtonRow>
      </LCARSModal>

      {/* ================================================================= */}
      {/* DELETE CONFIRM MODAL                                              */}
      {/* ================================================================= */}
      <LCARSConfirmModal
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDeleteList}
        title="Delete Task List"
        message={`Permanently delete "${selectedList?.title ?? ''}" and all its items? This action cannot be undone.`}
        confirmLabel="Delete"
        cancelLabel="Cancel"
        variant="danger"
        isLoading={deleteList.isPending}
      />
    </PageContainer>
  )
}
