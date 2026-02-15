import React from 'react'
import styled from 'styled-components'
import { LCARSModal } from './LCARSModal'
import { LCARSButton } from './LCARSButton'

interface LCARSConfirmModalProps {
  isOpen: boolean
  onClose: () => void
  onConfirm: () => void
  title: string
  message: string
  confirmLabel?: string
  cancelLabel?: string
  variant?: 'primary' | 'danger'
  isLoading?: boolean
}

const Message = styled.p`
  color: ${props => props.theme.colors.text.light};
  font-size: ${props => props.theme.typography.fontSize.md};
  line-height: ${props => props.theme.typography.lineHeight.normal};
  margin: 0 0 ${props => props.theme.spacing.lg} 0;
`

const ButtonRow = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: ${props => props.theme.spacing.md};
`

export const LCARSConfirmModal: React.FC<LCARSConfirmModalProps> = ({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmLabel = 'Confirm',
  cancelLabel = 'Cancel',
  variant = 'primary',
  isLoading = false,
}) => {
  return (
    <LCARSModal isOpen={isOpen} onClose={onClose} title={title} variant={variant === 'danger' ? 'danger' : 'primary'}>
      <Message>{message}</Message>
      <ButtonRow>
        <LCARSButton variant="secondary" onClick={onClose} disabled={isLoading}>
          {cancelLabel}
        </LCARSButton>
        <LCARSButton variant={variant === 'danger' ? 'danger' : 'primary'} onClick={onConfirm} disabled={isLoading}>
          {isLoading ? 'Processing...' : confirmLabel}
        </LCARSButton>
      </ButtonRow>
    </LCARSModal>
  )
}
