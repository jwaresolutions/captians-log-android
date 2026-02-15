import React from 'react'
import styled from 'styled-components'

interface LCARSColumnProps {
  children: React.ReactNode
  width?: string | number
  gap?: string
  className?: string
}

const StyledColumn = styled.div<{
  width: string | number
  gap: string
}>`
  display: flex;
  flex-direction: column;
  width: ${props => typeof props.width === 'number' ? `${props.width}px` : props.width};
  gap: ${props => props.gap};
  min-height: 100%;

  > * {
    width: 100%;
    flex-shrink: 0;
  }
`

export const LCARSColumn: React.FC<LCARSColumnProps> = ({
  children,
  width = '200px',
  gap = '3px',
  className,
}) => {
  return (
    <StyledColumn
      width={width}
      gap={gap}
      className={className}
    >
      {children}
    </StyledColumn>
  )
}
