import React, { useState, useRef, useEffect } from 'react'
import styled from 'styled-components'
import { TodoItem } from '../../types/api'

interface TodoItemRowProps {
  item: TodoItem
  onToggle: (itemId: string) => void
  onUpdate: (itemId: string, content: string) => void
  onDelete: (itemId: string) => void
}

const RowContainer = styled.div<{ $completed: boolean; $isEditing: boolean }>`
  display: flex;
  align-items: center;
  gap: 12px;
  padding: ${props => props.theme.spacing.md};
  background: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props =>
    props.$isEditing
      ? props.theme.colors.primary.neonCarrot
      : props.$completed
        ? props.theme.colors.status.success
        : props.theme.colors.primary.anakiwa
  };
  border-radius: ${props => props.theme.borderRadius.md};
  transition: all ${props => props.theme.animation.normal} ease;
  cursor: pointer;
  animation: slideIn ${props => props.theme.animation.normal} ease;

  &:hover {
    border-color: ${props =>
      props.$isEditing
        ? props.theme.colors.primary.neonCarrot
        : props.theme.colors.primary.neonCarrot
    };
    box-shadow: 0 0 12px ${props => props.theme.colors.primary.neonCarrot}30;
  }

  @keyframes slideIn {
    from {
      opacity: 0;
      transform: translateX(-8px);
    }
    to {
      opacity: 1;
      transform: translateX(0);
    }
  }
`

const Checkbox = styled.button<{ $completed: boolean }>`
  width: 24px;
  height: 24px;
  min-width: 24px;
  border-radius: 50%;
  border: 2px solid ${props =>
    props.$completed
      ? props.theme.colors.status.success
      : props.theme.colors.primary.anakiwa
  };
  background: ${props =>
    props.$completed
      ? props.theme.colors.status.success
      : 'transparent'
  };
  color: ${props => props.theme.colors.background};
  font-size: 14px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all ${props => props.theme.animation.fast} ease;
  padding: 0;

  &:hover {
    transform: scale(1.1);
    box-shadow: 0 0 8px ${props =>
      props.$completed
        ? props.theme.colors.status.success
        : props.theme.colors.primary.anakiwa
    };
  }

  &:active {
    transform: scale(0.95);
  }
`

const Content = styled.div<{ $completed: boolean }>`
  flex: 1;
  font-family: ${props => props.theme.typography.fontFamily};
  font-size: 14px;
  color: ${props =>
    props.$completed
      ? props.theme.colors.text.muted
      : props.theme.colors.text.light
  };
  text-transform: uppercase;
  letter-spacing: ${props => props.theme.typography.letterSpacing};
  text-decoration: ${props => props.$completed ? 'line-through' : 'none'};
  user-select: none;
`

const EditInput = styled.input`
  flex: 1;
  font-family: ${props => props.theme.typography.fontFamily};
  font-size: 14px;
  color: ${props => props.theme.colors.text.light};
  text-transform: uppercase;
  letter-spacing: ${props => props.theme.typography.letterSpacing};
  background: ${props => props.theme.colors.surface.medium};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
  border-radius: ${props => props.theme.borderRadius.md};
  padding: 8px 12px;
  outline: none;

  &:focus {
    border-color: ${props => props.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 12px ${props => props.theme.colors.primary.neonCarrot}40;
  }
`

const DeleteButton = styled.button<{ $visible: boolean }>`
  width: 24px;
  height: 24px;
  min-width: 24px;
  border-radius: 50%;
  border: 2px solid ${props => props.theme.colors.status.error};
  background: transparent;
  color: ${props => props.theme.colors.status.error};
  font-size: 12px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: ${props => props.$visible ? 1 : 0};
  transition: all ${props => props.theme.animation.fast} ease;
  padding: 0;

  &:hover {
    background: ${props => props.theme.colors.status.error};
    color: ${props => props.theme.colors.background};
    transform: scale(1.1);
    box-shadow: 0 0 8px ${props => props.theme.colors.status.error};
  }

  &:active {
    transform: scale(0.95);
  }
`

export const TodoItemRow: React.FC<TodoItemRowProps> = ({ item, onToggle, onUpdate, onDelete }) => {
  const [isEditing, setIsEditing] = useState(false)
  const [editValue, setEditValue] = useState(item.content)
  const [isHovered, setIsHovered] = useState(false)
  const inputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    if (isEditing && inputRef.current) {
      inputRef.current.focus()
      inputRef.current.select()
    }
  }, [isEditing])

  const handleContentClick = (e: React.MouseEvent) => {
    e.stopPropagation()
    if (!item.completed) {
      setIsEditing(true)
    }
  }

  const handleSave = () => {
    const trimmedValue = editValue.trim()
    if (trimmedValue && trimmedValue !== item.content) {
      onUpdate(item.id, trimmedValue)
    }
    setIsEditing(false)
  }

  const handleCancel = () => {
    setEditValue(item.content)
    setIsEditing(false)
  }

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSave()
    } else if (e.key === 'Escape') {
      handleCancel()
    }
  }

  const handleCheckboxClick = (e: React.MouseEvent) => {
    e.stopPropagation()
    onToggle(item.id)
  }

  const handleDeleteClick = (e: React.MouseEvent) => {
    e.stopPropagation()
    onDelete(item.id)
  }

  return (
    <RowContainer
      $completed={item.completed}
      $isEditing={isEditing}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <Checkbox
        $completed={item.completed}
        onClick={handleCheckboxClick}
        role="checkbox"
        aria-label={item.completed ? 'Mark incomplete' : 'Mark complete'}
        aria-checked={item.completed}
      >
        {item.completed && '✓'}
      </Checkbox>

      {isEditing ? (
        <EditInput
          ref={inputRef}
          value={editValue}
          onChange={(e) => setEditValue(e.target.value)}
          onKeyDown={handleKeyDown}
          onBlur={handleSave}
        />
      ) : (
        <Content
          $completed={item.completed}
          onClick={handleContentClick}
        >
          {item.content}
        </Content>
      )}

      <DeleteButton
        $visible={isHovered}
        onClick={handleDeleteClick}
        aria-label="Delete task"
      >
        ×
      </DeleteButton>
    </RowContainer>
  )
}
