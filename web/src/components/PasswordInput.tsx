import React, { useState } from 'react'
import styled from 'styled-components'

const InputWrapper = styled.div`
  position: relative;
  display: flex;
  align-items: center;
`

const StyledInput = styled.input`
  width: 100%;
  background-color: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.primary.neonCarrot};
  border-radius: ${props => props.theme.borderRadius.sm};
  padding: ${props => props.theme.spacing.md};
  padding-right: 48px;
  color: ${props => props.theme.colors.text.primary};
  font-size: ${props => props.theme.typography.fontSize.md};
  font-family: ${props => props.theme.typography.fontFamily.primary};

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary.tanoi};
    box-shadow: ${props => props.theme.shadows.glow};
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`

const ToggleButton = styled.button`
  position: absolute;
  right: 10px;
  background: none;
  border: none;
  color: ${props => props.theme.colors.primary.anakiwa};
  cursor: pointer;
  padding: 6px;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    color: ${props => props.theme.colors.primary.neonCarrot};
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  svg {
    width: 22px;
    height: 22px;
  }
`

const EyeIcon = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
    <circle cx="12" cy="12" r="3" />
  </svg>
)

const EyeOffIcon = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
    <line x1="1" y1="1" x2="23" y2="23" />
  </svg>
)

interface PasswordInputProps {
  id?: string
  name?: string
  value: string
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void
  placeholder?: string
  required?: boolean
  disabled?: boolean
  minLength?: number
  autoComplete?: string
}

export const PasswordInput: React.FC<PasswordInputProps> = ({
  id,
  name,
  value,
  onChange,
  placeholder,
  required,
  disabled,
  minLength,
  autoComplete,
}) => {
  const [showPassword, setShowPassword] = useState(false)

  return (
    <InputWrapper>
      <StyledInput
        type={showPassword ? 'text' : 'password'}
        id={id}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        required={required}
        disabled={disabled}
        minLength={minLength}
        autoComplete={autoComplete}
      />
      <ToggleButton
        type="button"
        onClick={() => setShowPassword(!showPassword)}
        disabled={disabled}
        aria-label={showPassword ? 'Hide password' : 'Show password'}
      >
        {showPassword ? <EyeOffIcon /> : <EyeIcon />}
      </ToggleButton>
    </InputWrapper>
  )
}
