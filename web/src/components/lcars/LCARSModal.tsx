import React, { useEffect, useRef, useCallback } from 'react'
import styled, { keyframes } from 'styled-components'
import { createPortal } from 'react-dom'

interface LCARSModalProps {
  isOpen: boolean
  onClose: () => void
  title?: string
  variant?: 'primary' | 'secondary' | 'accent' | 'danger'
  children: React.ReactNode
  width?: string
}

const fadeIn = keyframes`
  from { opacity: 0; }
  to { opacity: 1; }
`

const scaleIn = keyframes`
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
`

const Backdrop = styled.div`
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  z-index: ${props => props.theme.zIndex.modal};
  display: flex;
  align-items: center;
  justify-content: center;
  animation: ${fadeIn} 150ms ease;
`

const ModalContainer = styled.div<{ width?: string }>`
  background: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.primary.lilac};
  border-radius: ${props => props.theme.borderRadius.lg};
  width: ${props => props.width || '480px'};
  max-width: 90vw;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: ${scaleIn} 200ms ease;
`

const variantColors: Record<string, string> = {
  primary: '#FF9933',
  secondary: '#CC99CC',
  accent: '#99CCFF',
  danger: '#FF5555',
}

const ModalHeader = styled.div<{ variant: string }>`
  background: ${props => variantColors[props.variant] || variantColors.primary};
  padding: 0 ${props => props.theme.spacing.md};
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-radius: ${props => props.theme.lcars.buttonRadius};
  margin: ${props => props.theme.spacing.sm};
  margin-bottom: 0;
`

const ModalTitle = styled.span`
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${props => props.theme.typography.letterSpacing.normal};
  font-size: ${props => props.theme.typography.fontSize.sm};
  color: ${props => props.theme.colors.text.inverse};
`

const CloseButton = styled.button`
  background: none;
  border: none;
  color: ${props => props.theme.colors.text.inverse};
  font-size: 18px;
  cursor: pointer;
  padding: 0 4px;
  font-weight: bold;
  line-height: 1;
  opacity: 0.8;

  &:hover {
    opacity: 1;
  }
`

const ModalBody = styled.div`
  padding: ${props => props.theme.spacing.lg};
  overflow-y: auto;
  flex: 1;
`

export const LCARSModal: React.FC<LCARSModalProps> = ({
  isOpen,
  onClose,
  title,
  variant = 'primary',
  children,
  width,
}) => {
  const modalRef = useRef<HTMLDivElement>(null)

  const handleKeyDown = useCallback((e: KeyboardEvent) => {
    if (e.key === 'Escape') onClose()
  }, [onClose])

  useEffect(() => {
    if (isOpen) {
      document.addEventListener('keydown', handleKeyDown)
      document.body.style.overflow = 'hidden'
      return () => {
        document.removeEventListener('keydown', handleKeyDown)
        document.body.style.overflow = ''
      }
    }
  }, [isOpen, handleKeyDown])

  if (!isOpen) return null

  return createPortal(
    <Backdrop onClick={(e) => { if (e.target === e.currentTarget) onClose() }}>
      <ModalContainer ref={modalRef} width={width} role="dialog" aria-modal="true">
        {title && (
          <ModalHeader variant={variant}>
            <ModalTitle>{title}</ModalTitle>
            <CloseButton onClick={onClose} aria-label="Close">&times;</CloseButton>
          </ModalHeader>
        )}
        <ModalBody>{children}</ModalBody>
      </ModalContainer>
    </Backdrop>,
    document.body
  )
}
