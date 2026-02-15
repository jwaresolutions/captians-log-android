import React from 'react'
import styled, { css } from 'styled-components'

interface LCARSPanelProps {
  children: React.ReactNode
  title?: string
  variant?: 'primary' | 'secondary' | 'accent' | 'info'
  padding?: 'none' | 'sm' | 'md' | 'lg'
  className?: string
}

const panelVariants = {
  primary: css`
    .panel-header {
      background-color: ${props => props.theme.colors.primary.neonCarrot};
    }

    .panel-content {
      border-color: ${props => props.theme.colors.primary.neonCarrot};
    }
  `,
  secondary: css`
    .panel-header {
      background-color: ${props => props.theme.colors.primary.lilac};
    }

    .panel-content {
      border-color: ${props => props.theme.colors.primary.lilac};
    }
  `,
  accent: css`
    .panel-header {
      background-color: ${props => props.theme.colors.primary.anakiwa};
    }

    .panel-content {
      border-color: ${props => props.theme.colors.primary.anakiwa};
    }
  `,
  info: css`
    .panel-header {
      background-color: ${props => props.theme.colors.primary.mariner};
    }

    .panel-content {
      border-color: ${props => props.theme.colors.primary.mariner};
    }
  `,
}

const paddingVariants = {
  none: css`
    padding: 0;
  `,
  sm: css`
    padding: ${props => props.theme.spacing.sm};
  `,
  md: css`
    padding: ${props => props.theme.spacing.md};
  `,
  lg: css`
    padding: ${props => props.theme.spacing.lg};
  `,
}

const StyledPanel = styled.div<{ variant: keyof typeof panelVariants }>`
  display: flex;
  flex-direction: column;

  ${props => panelVariants[props.variant]}
`

const PanelHeader = styled.div`
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 ${props => props.theme.spacing.md};
  border-radius: ${props => props.theme.lcars.buttonRadius};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${props => props.theme.typography.letterSpacing.normal};
  font-size: ${props => props.theme.typography.fontSize.sm};
  color: ${props => props.theme.colors.text.inverse};
`

const PanelContent = styled.div<{ padding: keyof typeof paddingVariants }>`
  background-color: ${props => props.theme.colors.background};
  border: 1px solid;
  border-top: none;
  flex: 1;

  ${props => paddingVariants[props.padding]}
`

export const LCARSPanel: React.FC<LCARSPanelProps> = ({
  children,
  title,
  variant = 'primary',
  padding = 'md',
  className,
}) => {
  return (
    <StyledPanel variant={variant} className={className}>
      {title && (
        <PanelHeader className="panel-header">
          {title}
        </PanelHeader>
      )}
      <PanelContent padding={padding} className="panel-content">
        {children}
      </PanelContent>
    </StyledPanel>
  )
}
