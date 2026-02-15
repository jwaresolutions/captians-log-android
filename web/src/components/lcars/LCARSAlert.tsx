import React from 'react'
import styled, { css } from 'styled-components'

interface LCARSAlertProps {
  children: React.ReactNode
  type?: 'info' | 'success' | 'warning' | 'error'
  blink?: boolean
  dismissible?: boolean
  onDismiss?: () => void
  className?: string
}

const alertTypes = {
  info: css`
    background-color: ${props => props.theme.colors.primary.anakiwa};
    border-color: #AAD6FF;
    color: ${props => props.theme.colors.text.inverse};
  `,
  success: css`
    background-color: ${props => props.theme.colors.status.success};
    border-color: #88FF88;
    color: ${props => props.theme.colors.text.inverse};
  `,
  warning: css`
    background-color: ${props => props.theme.colors.status.warning};
    border-color: #FFFF88;
    color: ${props => props.theme.colors.text.inverse};
  `,
  error: css`
    background-color: ${props => props.theme.colors.status.error};
    border-color: #FF8888;
    color: ${props => props.theme.colors.text.inverse};
  `,
}

const StyledAlert = styled.div.withConfig({
  shouldForwardProp: (prop) => !['type', 'blink'].includes(prop),
})<{
  type: keyof typeof alertTypes
  blink: boolean
}>`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: ${props => props.theme.spacing.md};
  border: 2px solid;
  border-radius: ${props => props.theme.borderRadius.lg};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;

  ${props => alertTypes[props.type]}

  ${props => props.blink && css`
    animation: lcars-blink 1s infinite;
  `}
`

const AlertContent = styled.div`
  flex: 1;
  display: flex;
  align-items: center;
  gap: ${props => props.theme.spacing.sm};
`

const AlertIcon = styled.div`
  font-size: ${props => props.theme.typography.fontSize.lg};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
`

const AlertMessage = styled.div`
  flex: 1;
`

const DismissButton = styled.button`
  background: none;
  border: none;
  color: inherit;
  font-size: ${props => props.theme.typography.fontSize.lg};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  cursor: pointer;
  padding: ${props => props.theme.spacing.xs};
  border-radius: ${props => props.theme.borderRadius.sm};
  transition: background-color ${props => props.theme.animation.fast} ease;

  &:hover {
    background-color: rgba(255, 255, 255, 0.1);
  }

  &:focus {
    outline: 2px solid rgba(255, 255, 255, 0.5);
    outline-offset: 2px;
  }
`

const getAlertIcon = (type: string): string => {
  switch (type) {
    case 'info':
      return 'ℹ'
    case 'success':
      return '✓'
    case 'warning':
      return '⚠'
    case 'error':
      return '✗'
    default:
      return 'ℹ'
  }
}

export const LCARSAlert: React.FC<LCARSAlertProps> = ({
  children,
  type = 'info',
  blink = false,
  dismissible = false,
  onDismiss,
  className,
}) => {
  return (
    <StyledAlert type={type} blink={blink} className={className}>
      <AlertContent>
        <AlertIcon>
          {getAlertIcon(type)}
        </AlertIcon>
        <AlertMessage>
          {children}
        </AlertMessage>
      </AlertContent>
      {dismissible && onDismiss && (
        <DismissButton onClick={onDismiss} aria-label="Dismiss alert">
          ×
        </DismissButton>
      )}
    </StyledAlert>
  )
}
