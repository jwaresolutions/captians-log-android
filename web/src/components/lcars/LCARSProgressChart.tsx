import React from 'react'
import styled, { css } from 'styled-components'

interface LCARSProgressChartProps {
  title: string
  current: number
  target: number
  unit?: string
  color?: 'neonCarrot' | 'lilac' | 'anakiwa' | 'success'
  size?: 'sm' | 'md' | 'lg'
  showPercentage?: boolean
  className?: string
}

const chartColors = {
  neonCarrot: css`
    .progress-fill {
      background: linear-gradient(90deg,
        ${props => props.theme.colors.primary.neonCarrot} 0%,
        ${props => props.theme.colors.primary.goldenTanoi} 100%
      );
    }
    .progress-text {
      color: ${props => props.theme.colors.primary.neonCarrot};
    }
  `,
  lilac: css`
    .progress-fill {
      background: linear-gradient(90deg,
        ${props => props.theme.colors.primary.lilac} 0%,
        #DDA6DD 100%
      );
    }
    .progress-text {
      color: ${props => props.theme.colors.primary.lilac};
    }
  `,
  anakiwa: css`
    .progress-fill {
      background: linear-gradient(90deg,
        ${props => props.theme.colors.primary.anakiwa} 0%,
        #AAD6FF 100%
      );
    }
    .progress-text {
      color: ${props => props.theme.colors.primary.anakiwa};
    }
  `,
  success: css`
    .progress-fill {
      background: linear-gradient(90deg,
        ${props => props.theme.colors.status.success} 0%,
        #88FF88 100%
      );
    }
    .progress-text {
      color: ${props => props.theme.colors.status.success};
    }
  `,
}

const chartSizes = {
  sm: css`
    .chart-title {
      font-size: ${props => props.theme.typography.fontSize.sm};
      margin-bottom: ${props => props.theme.spacing.sm};
    }
    .progress-bar {
      height: 12px;
    }
    .progress-stats {
      font-size: ${props => props.theme.typography.fontSize.xs};
      margin-top: ${props => props.theme.spacing.sm};
    }
  `,
  md: css`
    .chart-title {
      font-size: ${props => props.theme.typography.fontSize.md};
      margin-bottom: ${props => props.theme.spacing.md};
    }
    .progress-bar {
      height: 16px;
    }
    .progress-stats {
      font-size: ${props => props.theme.typography.fontSize.sm};
      margin-top: ${props => props.theme.spacing.md};
    }
  `,
  lg: css`
    .chart-title {
      font-size: ${props => props.theme.typography.fontSize.lg};
      margin-bottom: ${props => props.theme.spacing.lg};
    }
    .progress-bar {
      height: 20px;
    }
    .progress-stats {
      font-size: ${props => props.theme.typography.fontSize.md};
      margin-top: ${props => props.theme.spacing.lg};
    }
  `,
}

const StyledProgressChart = styled.div<{
  color: keyof typeof chartColors
  size: keyof typeof chartSizes
}>`
  ${props => chartColors[props.color]}
  ${props => chartSizes[props.size]}
`

const ChartTitle = styled.div`
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
  color: ${props => props.theme.colors.text.primary};
`

const ProgressBarContainer = styled.div`
  background-color: ${props => props.theme.colors.surface.light};
  border-radius: ${props => props.theme.borderRadius.pill};
  overflow: hidden;
  position: relative;
  border: 1px solid ${props => props.theme.colors.surface.light};
`

const ProgressFill = styled.div<{ progress: number }>`
  height: 100%;
  width: ${props => Math.min(props.progress, 100)}%;
  transition: width 0.5s ease-in-out;
  border-radius: ${props => props.theme.borderRadius.pill};
  position: relative;

  &::after {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    width: 2px;
    background-color: rgba(255, 255, 255, 0.8);
    border-radius: 0 ${props => props.theme.borderRadius.pill} ${props => props.theme.borderRadius.pill} 0;
  }
`

const ProgressStats = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-family: ${props => props.theme.typography.fontFamily.monospace};
  color: ${props => props.theme.colors.text.secondary};
`

const StatValue = styled.span`
  font-weight: ${props => props.theme.typography.fontWeight.bold};
`

export const LCARSProgressChart: React.FC<LCARSProgressChartProps> = ({
  title,
  current,
  target,
  unit = '',
  color = 'neonCarrot',
  size = 'md',
  showPercentage = true,
  className,
}) => {
  const progress = target > 0 ? (current / target) * 100 : 0
  const percentage = Math.round(progress)
  const isComplete = current >= target

  return (
    <StyledProgressChart color={color} size={size} className={className}>
      <ChartTitle className="chart-title">
        {title}
      </ChartTitle>

      <ProgressBarContainer>
        <ProgressFill
          className="progress-fill"
          progress={progress}
        />
      </ProgressBarContainer>

      <ProgressStats className="progress-stats">
        <div>
          <StatValue className="progress-text">
            {current}
          </StatValue>
          {unit && ` ${unit}`} / {target}{unit && ` ${unit}`}
        </div>

        {showPercentage && (
          <div className="progress-text">
            <StatValue>
              {percentage}%
            </StatValue>
            {isComplete && ' ✓'}
          </div>
        )}
      </ProgressStats>
    </StyledProgressChart>
  )
}
