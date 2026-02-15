import React from 'react'
import styled, { css } from 'styled-components'

interface LCARSEstimateDisplayProps {
  title: string
  estimatedDate?: string
  daysRemaining?: number
  isComplete?: boolean
  color?: 'neonCarrot' | 'lilac' | 'anakiwa' | 'success'
  size?: 'sm' | 'md' | 'lg'
  className?: string
}

const displayColors = {
  neonCarrot: css`
    .estimate-value {
      color: ${props => props.theme.colors.primary.neonCarrot};
    }
    .estimate-border {
      border-color: ${props => props.theme.colors.primary.neonCarrot};
    }
  `,
  lilac: css`
    .estimate-value {
      color: ${props => props.theme.colors.primary.lilac};
    }
    .estimate-border {
      border-color: ${props => props.theme.colors.primary.lilac};
    }
  `,
  anakiwa: css`
    .estimate-value {
      color: ${props => props.theme.colors.primary.anakiwa};
    }
    .estimate-border {
      border-color: ${props => props.theme.colors.primary.anakiwa};
    }
  `,
  success: css`
    .estimate-value {
      color: ${props => props.theme.colors.status.success};
    }
    .estimate-border {
      border-color: ${props => props.theme.colors.status.success};
    }
  `,
}

const displaySizes = {
  sm: css`
    .estimate-title {
      font-size: ${props => props.theme.typography.fontSize.xs};
    }
    .estimate-value {
      font-size: ${props => props.theme.typography.fontSize.md};
    }
    .estimate-subtitle {
      font-size: ${props => props.theme.typography.fontSize.xs};
    }
    padding: ${props => props.theme.spacing.sm};
  `,
  md: css`
    .estimate-title {
      font-size: ${props => props.theme.typography.fontSize.sm};
    }
    .estimate-value {
      font-size: ${props => props.theme.typography.fontSize.lg};
    }
    .estimate-subtitle {
      font-size: ${props => props.theme.typography.fontSize.sm};
    }
    padding: ${props => props.theme.spacing.md};
  `,
  lg: css`
    .estimate-title {
      font-size: ${props => props.theme.typography.fontSize.md};
    }
    .estimate-value {
      font-size: ${props => props.theme.typography.fontSize.xl};
    }
    .estimate-subtitle {
      font-size: ${props => props.theme.typography.fontSize.md};
    }
    padding: ${props => props.theme.spacing.lg};
  `,
}

const StyledEstimateDisplay = styled.div<{
  color: keyof typeof displayColors
  size: keyof typeof displaySizes
  isComplete: boolean
}>`
  background-color: ${props => props.theme.colors.surface.dark};
  border: 2px solid;
  border-radius: ${props => props.theme.borderRadius.lg};
  text-align: center;
  position: relative;

  ${props => displayColors[props.color]}
  ${props => displaySizes[props.size]}

  ${props => props.isComplete && css`
    .estimate-value {
      color: ${props => props.theme.colors.status.success};
    }
    .estimate-border {
      border-color: ${props => props.theme.colors.status.success};
    }

    &::after {
      content: '✓ COMPLETE';
      position: absolute;
      top: 8px;
      right: 8px;
      font-size: ${props => props.theme.typography.fontSize.xs};
      color: ${props => props.theme.colors.status.success};
      font-weight: ${props => props.theme.typography.fontWeight.bold};
      text-transform: uppercase;
      letter-spacing: 1px;
    }
  `}
`

const EstimateTitle = styled.div`
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
  color: ${props => props.theme.colors.text.secondary};
  margin-bottom: ${props => props.theme.spacing.sm};
`

const EstimateValue = styled.div`
  font-family: ${props => props.theme.typography.fontFamily.monospace};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  line-height: ${props => props.theme.typography.lineHeight.tight};
  margin-bottom: ${props => props.theme.spacing.xs};
`

const EstimateSubtitle = styled.div`
  font-family: ${props => props.theme.typography.fontFamily.primary};
  color: ${props => props.theme.colors.text.muted};
  text-transform: uppercase;
  letter-spacing: 0.5px;
`

export const LCARSEstimateDisplay: React.FC<LCARSEstimateDisplayProps> = ({
  title,
  estimatedDate,
  daysRemaining,
  isComplete = false,
  color = 'neonCarrot',
  size = 'md',
  className,
}) => {
  const formatDate = (dateString: string) => {
    try {
      const date = new Date(dateString)
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
    } catch {
      return 'Unknown'
    }
  }

  const formatDaysRemaining = (days: number) => {
    if (days <= 0) return 'Goal Achieved'
    if (days === 1) return '1 Day'
    if (days < 30) return `${days} Days`
    if (days < 365) {
      const months = Math.round(days / 30)
      return months === 1 ? '1 Month' : `${months} Months`
    }
    const years = Math.round(days / 365)
    return years === 1 ? '1 Year' : `${years} Years`
  }

  return (
    <StyledEstimateDisplay
      color={color}
      size={size}
      isComplete={isComplete}
      className={`estimate-border ${className || ''}`}
    >
      <EstimateTitle className="estimate-title">
        {title}
      </EstimateTitle>

      {isComplete ? (
        <>
          <EstimateValue className="estimate-value">
            ACHIEVED
          </EstimateValue>
          <EstimateSubtitle className="estimate-subtitle">
            Goal Complete
          </EstimateSubtitle>
        </>
      ) : (
        <>
          {estimatedDate && (
            <>
              <EstimateValue className="estimate-value">
                {formatDate(estimatedDate)}
              </EstimateValue>
              <EstimateSubtitle className="estimate-subtitle">
                Estimated Completion
              </EstimateSubtitle>
            </>
          )}

          {daysRemaining !== undefined && (
            <>
              <EstimateValue className="estimate-value">
                {formatDaysRemaining(daysRemaining)}
              </EstimateValue>
              <EstimateSubtitle className="estimate-subtitle">
                Remaining
              </EstimateSubtitle>
            </>
          )}
        </>
      )}
    </StyledEstimateDisplay>
  )
}
