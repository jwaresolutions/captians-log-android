import React from 'react'
import styled from 'styled-components'
import { useAuth } from '../hooks/useAuth'

const DisabledWrapper = styled.div`
  position: relative;
  opacity: 0.4;
  pointer-events: none;
  cursor: not-allowed;

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 1;
  }
`

const TooltipWrapper = styled.div`
  position: relative;
  display: inline-block;

  &:hover > .readonly-tooltip {
    visibility: visible;
    opacity: 1;
  }
`

const Tooltip = styled.div`
  visibility: hidden;
  opacity: 0;
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  padding: 4px 8px;
  background: ${props => props.theme.colors.surface.dark};
  border: 1px solid ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.primary.anakiwa};
  font-size: ${props => props.theme.typography.fontSize.xs};
  text-transform: uppercase;
  letter-spacing: 1px;
  white-space: nowrap;
  z-index: 100;
  transition: opacity 0.2s;
  pointer-events: none;
`

interface ReadOnlyGuardProps {
  children: React.ReactNode
  fallback?: React.ReactNode
}

export const ReadOnlyGuard: React.FC<ReadOnlyGuardProps> = ({ children, fallback }) => {
  const { isReadOnly } = useAuth()

  if (!isReadOnly) {
    return <>{children}</>
  }

  if (fallback !== undefined) {
    return <>{fallback}</>
  }

  return (
    <TooltipWrapper>
      <DisabledWrapper>
        {children}
      </DisabledWrapper>
      <Tooltip className="readonly-tooltip">View Only</Tooltip>
    </TooltipWrapper>
  )
}
