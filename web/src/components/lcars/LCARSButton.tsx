import React from 'react'
import styled, { css } from 'styled-components'

interface LCARSButtonProps {
  children: React.ReactNode
  variant?: 'primary' | 'secondary' | 'accent' | 'info' | 'warning' | 'danger' | 'sidebar' | 'cap-left' | 'cap-right'
  size?: 'sm' | 'md' | 'lg'
  disabled?: boolean
  onClick?: () => void
  className?: string
  type?: 'button' | 'submit' | 'reset'
}

const buttonVariants = {
  primary: css`
    background-color: ${props => props.theme.colors.primary.neonCarrot};
    color: ${props => props.theme.colors.text.inverse};

    &:hover:not(:disabled) {
      background-color: ${props => props.theme.colors.primary.goldenTanoi};
    }

    &:active:not(:disabled) {
      background-color: ${props => props.theme.colors.primary.tanoi};
      box-shadow: ${props => props.theme.shadows.glowStrong};
    }
  `,
  secondary: css`
    background-color: ${props => props.theme.colors.primary.lilac};
    color: ${props => props.theme.colors.text.inverse};

    &:hover:not(:disabled) {
      background-color: #DDA6DD;
    }

    &:active:not(:disabled) {
      background-color: #EEB3EE;
      box-shadow: 0 0 40px rgba(204, 153, 204, 0.5);
    }
  `,
  accent: css`
    background-color: ${props => props.theme.colors.primary.anakiwa};
    color: ${props => props.theme.colors.text.inverse};

    &:hover:not(:disabled) {
      background-color: #AAD6FF;
    }

    &:active:not(:disabled) {
      background-color: #BBE0FF;
      box-shadow: 0 0 40px rgba(153, 204, 255, 0.5);
    }
  `,
  info: css`
    background-color: ${props => props.theme.colors.primary.mariner};
    color: ${props => props.theme.colors.text.inverse};

    &:hover:not(:disabled) {
      background-color: #4477DD;
    }

    &:active:not(:disabled) {
      background-color: #5588EE;
      box-shadow: 0 0 40px rgba(51, 102, 204, 0.5);
    }
  `,
  warning: css`
    background-color: ${props => props.theme.colors.primary.goldenTanoi};
    color: ${props => props.theme.colors.text.inverse};

    &:hover:not(:disabled) {
      background-color: #FFD677;
    }

    &:active:not(:disabled) {
      background-color: #FFE088;
      box-shadow: 0 0 40px rgba(255, 204, 102, 0.5);
    }
  `,
  danger: css`
    background-color: ${props => props.theme.colors.status.error};
    color: ${props => props.theme.colors.text.inverse};

    &:hover:not(:disabled) {
      background-color: #FF6666;
    }

    &:active:not(:disabled) {
      background-color: #FF7777;
      box-shadow: 0 0 40px rgba(255, 85, 85, 0.5);
    }
  `,
  sidebar: css`
    background-color: ${props => props.theme.colors.primary.neonCarrot};
    color: ${props => props.theme.colors.text.inverse};
    border-radius: 0 9999px 9999px 0;

    &:hover:not(:disabled) {
      background-color: ${props => props.theme.colors.primary.goldenTanoi};
    }

    &:active:not(:disabled) {
      background-color: ${props => props.theme.colors.primary.tanoi};
      box-shadow: ${props => props.theme.shadows.glowStrong};
    }
  `,
  'cap-left': css`
    background-color: ${props => props.theme.colors.primary.neonCarrot};
    color: ${props => props.theme.colors.text.inverse};
    border-radius: 9999px 0 0 9999px;

    &:hover:not(:disabled) {
      background-color: ${props => props.theme.colors.primary.goldenTanoi};
    }

    &:active:not(:disabled) {
      background-color: ${props => props.theme.colors.primary.tanoi};
      box-shadow: ${props => props.theme.shadows.glowStrong};
    }
  `,
  'cap-right': css`
    background-color: ${props => props.theme.colors.primary.neonCarrot};
    color: ${props => props.theme.colors.text.inverse};
    border-radius: 0 9999px 9999px 0;

    &:hover:not(:disabled) {
      background-color: ${props => props.theme.colors.primary.goldenTanoi};
    }

    &:active:not(:disabled) {
      background-color: ${props => props.theme.colors.primary.tanoi};
      box-shadow: ${props => props.theme.shadows.glowStrong};
    }
  `,
}

const buttonSizes = {
  sm: css`
    height: 28px;
    padding: 0 ${props => props.theme.spacing.md};
    font-size: ${props => props.theme.typography.fontSize.sm};
  `,
  md: css`
    height: 40px;
    padding: 0 ${props => props.theme.spacing.lg};
    font-size: ${props => props.theme.typography.fontSize.md};
  `,
  lg: css`
    height: 56px;
    padding: 0 ${props => props.theme.spacing.xl};
    font-size: ${props => props.theme.typography.fontSize.lg};
  `,
}

const StyledButton = styled.button<{ variant: keyof typeof buttonVariants; size: keyof typeof buttonSizes }>`
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${props => props.theme.typography.letterSpacing.normal};
  border: none;
  border-radius: ${props => props.theme.lcars.buttonRadius};
  cursor: pointer;
  transition: all ${props => props.theme.animation.fast} ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: ${props => props.theme.spacing.sm};
  white-space: nowrap;
  box-shadow: none;
  position: relative;
  overflow: hidden;

  /* Left-to-right sweep hover effect */
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.25);
    transform: translateX(-100%);
    transition: transform 0.35s ease;
    border-radius: inherit;
  }

  &:hover:not(:disabled)::after {
    transform: translateX(0);
  }

  &:active:not(:disabled)::after {
    background: rgba(255, 255, 255, 0.35);
  }

  ${props => buttonVariants[props.variant]}
  ${props => buttonSizes[props.size]}

  &:disabled {
    background-color: ${props => props.theme.colors.interactive.disabled};
    color: ${props => props.theme.colors.text.muted};
    cursor: not-allowed;
    box-shadow: none;
  }

  &:focus-visible {
    outline: 2px solid ${props => props.theme.colors.primary.tanoi};
    outline-offset: 2px;
  }
`

export const LCARSButton: React.FC<LCARSButtonProps> = ({
  children,
  variant = 'primary',
  size = 'md',
  disabled = false,
  onClick,
  className,
  type = 'button',
}) => {
  return (
    <StyledButton
      variant={variant}
      size={size}
      disabled={disabled}
      onClick={onClick}
      className={className}
      type={type}
    >
      {children}
    </StyledButton>
  )
}
