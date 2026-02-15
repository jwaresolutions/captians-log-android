import React from 'react'
import styled from 'styled-components'

interface TodoProgressBarProps {
  percentage: number
}

const ProgressContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
`

const ProgressBarTrack = styled.div`
  flex: 1;
  height: 10px;
  background: ${props => props.theme.colors.surface.medium};
  border-radius: ${props => props.theme.borderRadius.pill};
  overflow: hidden;
  position: relative;
`

const ProgressBarFill = styled.div<{ $percentage: number }>`
  height: 100%;
  width: ${props => props.$percentage}%;
  background: ${props => props.theme.colors.primary.neonCarrot};
  border-radius: ${props => props.theme.borderRadius.pill};
  transition: width ${props => props.theme.animation.normal} ease;
  box-shadow: 0 0 8px ${props => props.theme.colors.primary.neonCarrot}40;
`

const PercentageText = styled.span`
  font-family: ${props => props.theme.typography.fontFamily};
  font-size: 12px;
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  color: ${props => props.theme.colors.text.primary};
  text-transform: uppercase;
  letter-spacing: ${props => props.theme.typography.letterSpacing};
  min-width: 42px;
  text-align: right;
`

export const TodoProgressBar: React.FC<TodoProgressBarProps> = ({ percentage }) => {
  const clampedPercentage = Math.min(100, Math.max(0, percentage))

  return (
    <ProgressContainer>
      <ProgressBarTrack>
        <ProgressBarFill $percentage={clampedPercentage} />
      </ProgressBarTrack>
      <PercentageText>{Math.round(clampedPercentage)}%</PercentageText>
    </ProgressContainer>
  )
}
