import React from 'react'
import styled, { css } from 'styled-components'

interface LCARSElbowProps {
  position: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
  size?: number
  color?: 'neonCarrot' | 'tanoi' | 'lilac' | 'anakiwa' | 'mariner'
  armWidth?: number
  className?: string
}

const elbowColors = {
  neonCarrot: css`
    background-color: ${props => props.theme.colors.primary.neonCarrot};

    &::before {
      background-color: ${props => props.theme.colors.background};
    }
  `,
  tanoi: css`
    background-color: ${props => props.theme.colors.primary.tanoi};

    &::before {
      background-color: ${props => props.theme.colors.background};
    }
  `,
  lilac: css`
    background-color: ${props => props.theme.colors.primary.lilac};

    &::before {
      background-color: ${props => props.theme.colors.background};
    }
  `,
  anakiwa: css`
    background-color: ${props => props.theme.colors.primary.anakiwa};

    &::before {
      background-color: ${props => props.theme.colors.background};
    }
  `,
  mariner: css`
    background-color: ${props => props.theme.colors.primary.mariner};

    &::before {
      background-color: ${props => props.theme.colors.background};
    }
  `,
}

const StyledElbow = styled.div<{
  position: LCARSElbowProps['position']
  size: number
  armWidth: number
  color: keyof typeof elbowColors
}>`
  position: relative;
  width: ${props => props.size}px;
  height: ${props => props.size}px;
  flex-shrink: 0;

  ${props => elbowColors[props.color]}

  /* Create the quarter-circle cutout using a pseudo-element */
  &::before {
    content: '';
    position: absolute;
    width: ${props => props.size - props.armWidth}px;
    height: ${props => props.size - props.armWidth}px;
  }

  /* Position the cutout based on elbow orientation */
  ${props => {
    switch (props.position) {
      case 'top-left':
        return css`
          &::before {
            bottom: 0;
            right: 0;
            border-radius: 0 0 0 ${props.size - props.armWidth}px;
          }
        `
      case 'top-right':
        return css`
          &::before {
            bottom: 0;
            left: 0;
            border-radius: 0 0 ${props.size - props.armWidth}px 0;
          }
        `
      case 'bottom-left':
        return css`
          &::before {
            top: 0;
            right: 0;
            border-radius: 0 ${props.size - props.armWidth}px 0 0;
          }
        `
      case 'bottom-right':
        return css`
          &::before {
            top: 0;
            left: 0;
            border-radius: ${props.size - props.armWidth}px 0 0 0;
          }
        `
    }
  }}
`

export const LCARSElbow: React.FC<LCARSElbowProps> = ({
  position,
  size = 60,
  color = 'neonCarrot',
  armWidth = 30,
  className,
}) => {
  return (
    <StyledElbow
      position={position}
      size={size}
      armWidth={armWidth}
      color={color}
      className={className}
      aria-hidden="true"
    />
  )
}
