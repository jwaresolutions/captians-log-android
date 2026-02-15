import React from 'react'
import styled, { css } from 'styled-components'

interface LCARSDataDisplayProps {
  label: string
  value: string | number
  unit?: string
  size?: 'sm' | 'md' | 'lg'
  valueColor?: 'neonCarrot' | 'lilac' | 'anakiwa' | 'mariner' | 'success'
  showIndicator?: boolean
  indicatorColor?: 'neonCarrot' | 'lilac' | 'anakiwa' | 'success' | 'error'
  className?: string
}

const displaySizes = {
  sm: css`
    .data-label {
      font-size: ${props => props.theme.typography.fontSize.xs};
    }
    .data-value {
      font-size: ${props => props.theme.typography.fontSize.md};
    }
    .data-unit {
      font-size: ${props => props.theme.typography.fontSize.sm};
    }
  `,
  md: css`
    .data-label {
      font-size: ${props => props.theme.typography.fontSize.sm};
    }
    .data-value {
      font-size: ${props => props.theme.typography.fontSize.lg};
    }
    .data-unit {
      font-size: ${props => props.theme.typography.fontSize.md};
    }
  `,
  lg: css`
    .data-label {
      font-size: ${props => props.theme.typography.fontSize.md};
    }
    .data-value {
      font-size: ${props => props.theme.typography.fontSize.xl};
    }
    .data-unit {
      font-size: ${props => props.theme.typography.fontSize.lg};
    }
  `,
}

const valueColors = {
  neonCarrot: css`
    color: ${props => props.theme.colors.primary.neonCarrot};
  `,
  lilac: css`
    color: ${props => props.theme.colors.primary.lilac};
  `,
  anakiwa: css`
    color: ${props => props.theme.colors.primary.anakiwa};
  `,
  mariner: css`
    color: ${props => props.theme.colors.primary.mariner};
  `,
  success: css`
    color: ${props => props.theme.colors.status.success};
  `,
}

const indicatorColors = {
  neonCarrot: '#FF9933',
  lilac: '#CC99CC',
  anakiwa: '#99CCFF',
  success: '#55FF55',
  error: '#FF5555',
}

const StyledDataDisplay = styled.div<{
  size: keyof typeof displaySizes
}>`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: ${props => props.theme.spacing.xs};
  background-color: transparent;

  ${props => displaySizes[props.size]}
`

const DataLabel = styled.div`
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-weight: ${props => props.theme.typography.fontWeight.normal};
  text-transform: uppercase;
  letter-spacing: ${props => props.theme.typography.letterSpacing.wide};
  color: ${props => props.theme.colors.primary.lilac};
  opacity: 0.8;
`

const DataValueContainer = styled.div`
  display: flex;
  align-items: center;
  gap: ${props => props.theme.spacing.sm};
`

const Indicator = styled.div<{ color: string }>`
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: ${props => props.color};
  box-shadow: 0 0 8px ${props => props.color};
  flex-shrink: 0;
`

const DataValue = styled.div<{ valueColor: keyof typeof valueColors }>`
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  line-height: ${props => props.theme.typography.lineHeight.tight};

  ${props => valueColors[props.valueColor]}
`

const DataUnit = styled.div`
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-weight: ${props => props.theme.typography.fontWeight.normal};
  color: ${props => props.theme.colors.text.muted};
  text-transform: uppercase;
`

export const LCARSDataDisplay: React.FC<LCARSDataDisplayProps> = ({
  label,
  value,
  unit,
  size = 'md',
  valueColor = 'neonCarrot',
  showIndicator = false,
  indicatorColor = 'neonCarrot',
  className,
}) => {
  return (
    <StyledDataDisplay size={size} className={className}>
      <DataLabel className="data-label">
        {label}
      </DataLabel>
      <DataValueContainer>
        {showIndicator && (
          <Indicator color={indicatorColors[indicatorColor]} />
        )}
        <DataValue className="data-value" valueColor={valueColor}>
          {value}
        </DataValue>
        {unit && (
          <DataUnit className="data-unit">
            {unit}
          </DataUnit>
        )}
      </DataValueContainer>
    </StyledDataDisplay>
  )
}
