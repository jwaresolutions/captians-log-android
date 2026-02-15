import React from 'react'
import styled, { css } from 'styled-components'

interface LCARSHeaderProps {
  children: React.ReactNode
  level?: 1 | 2 | 3 | 4 | 5 | 6
  color?: 'neonCarrot' | 'tanoi' | 'lilac' | 'anakiwa' | 'mariner'
  align?: 'left' | 'center' | 'right'
  withBar?: boolean
  barColor?: 'neonCarrot' | 'tanoi' | 'lilac' | 'anakiwa'
  className?: string
}

const headerLevels = {
  1: css`
    font-size: ${props => props.theme.typography.fontSize.xxxl};
  `,
  2: css`
    font-size: ${props => props.theme.typography.fontSize.xxl};
  `,
  3: css`
    font-size: ${props => props.theme.typography.fontSize.xl};
  `,
  4: css`
    font-size: ${props => props.theme.typography.fontSize.lg};
  `,
  5: css`
    font-size: ${props => props.theme.typography.fontSize.md};
  `,
  6: css`
    font-size: ${props => props.theme.typography.fontSize.md};
  `,
}

const headerColors = {
  neonCarrot: css`
    color: ${props => props.theme.colors.primary.neonCarrot};
  `,
  tanoi: css`
    color: ${props => props.theme.colors.primary.tanoi};
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
}

const headerAlignments = {
  left: css`
    text-align: left;
  `,
  center: css`
    text-align: center;
  `,
  right: css`
    text-align: right;
  `,
}

const barColors = {
  neonCarrot: '#FF9933',
  tanoi: '#FFCC99',
  lilac: '#CC99CC',
  anakiwa: '#99CCFF',
}

const HeaderContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.sm};
`

const StyledHeader = styled.div<{
  level: keyof typeof headerLevels
  color: keyof typeof headerColors
  align: keyof typeof headerAlignments
}>`
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 2px;
  line-height: ${props => props.theme.typography.lineHeight.tight};
  margin: 0;

  ${props => headerLevels[props.level]}
  ${props => headerColors[props.color]}
  ${props => headerAlignments[props.align]}
`

const DecorativeBar = styled.div<{ color: string }>`
  width: 100%;
  height: 4px;
  background-color: ${props => props.color};
  border-radius: 0;
`

export const LCARSHeader: React.FC<LCARSHeaderProps> = ({
  children,
  level = 1,
  color = 'neonCarrot',
  align = 'left',
  withBar = false,
  barColor = 'neonCarrot',
  className,
}) => {
  const HeaderTag = `h${level}` as keyof JSX.IntrinsicElements

  const header = (
    <StyledHeader
      as={HeaderTag}
      level={level}
      color={color}
      align={align}
      className={className}
    >
      {children}
    </StyledHeader>
  )

  if (withBar) {
    return (
      <HeaderContainer>
        {header}
        <DecorativeBar color={barColors[barColor]} />
      </HeaderContainer>
    )
  }

  return header
}
