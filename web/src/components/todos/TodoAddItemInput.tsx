import React, { useState } from 'react'
import styled from 'styled-components'
import { LCARSButton } from '../lcars/LCARSButton'

interface TodoAddItemInputProps {
  onAdd: (content: string) => void
  isLoading?: boolean
}

const InputContainer = styled.div`
  display: flex;
  gap: 12px;
  align-items: center;
`

const StyledInput = styled.input<{ disabled?: boolean }>`
  flex: 1;
  font-family: ${props => props.theme.typography.fontFamily};
  font-size: 14px;
  color: ${props => props.theme.colors.text.light};
  text-transform: uppercase;
  letter-spacing: ${props => props.theme.typography.letterSpacing};
  background: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.primary.anakiwa};
  border-radius: ${props => props.theme.borderRadius.md};
  padding: 12px 16px;
  outline: none;
  transition: all ${props => props.theme.animation.fast} ease;
  opacity: ${props => props.disabled ? 0.5 : 1};
  cursor: ${props => props.disabled ? 'not-allowed' : 'text'};

  &:focus {
    border-color: ${props => props.theme.colors.primary.neonCarrot};
    box-shadow: 0 0 12px ${props => props.theme.colors.primary.neonCarrot}40;
  }

  &::placeholder {
    color: ${props => props.theme.colors.text.muted};
    opacity: 0.6;
  }

  &:disabled {
    cursor: not-allowed;
  }
`

export const TodoAddItemInput: React.FC<TodoAddItemInputProps> = ({ onAdd, isLoading = false }) => {
  const [value, setValue] = useState('')

  const handleSubmit = () => {
    const trimmedValue = value.trim()
    if (trimmedValue && !isLoading) {
      onAdd(trimmedValue)
      setValue('')
    }
  }

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSubmit()
    }
  }

  return (
    <InputContainer>
      <StyledInput
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onKeyDown={handleKeyDown}
        placeholder="NEW TASK..."
        disabled={isLoading}
      />
      <LCARSButton
        onClick={handleSubmit}
        disabled={!value.trim() || isLoading}
      >
        {isLoading ? 'ADDING...' : 'ADD'}
      </LCARSButton>
    </InputContainer>
  )
}
