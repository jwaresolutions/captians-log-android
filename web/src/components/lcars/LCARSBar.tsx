import React from 'react'
import styled from 'styled-components'

interface LCARSBarProps {
  width?: string | number
  height?: string | number
  colors?: Array<'neonCarrot' | 'tanoi' | 'goldenTanoi' | 'lilac' | 'anakiwa' | 'mariner' | 'paleCanary'>
  orientation?: 'horizontal' | 'vertical'
  className?: string
}

interface SegmentProps {
  color: string
  flex?: number
}

const colorMap = {
  neonCarrot: '#FF9933',
  tanoi: '#FFCC99',
  goldenTanoi: '#FFCC66',
  lilac: '#CC99CC',
  anakiwa: '#99CCFF',
  mariner: '#3366CC',
  paleCanary: '#FFFF99',
}

const StyledBar = styled.div<{
  width: string | number
  height: string | number
  orientation: 'horizontal' | 'vertical'
  isSegmented: boolean
}>`
  display: flex;
  flex-direction: ${props => props.orientation === 'horizontal' ? 'row' : 'column'};
  flex-shrink: 0;
  width: ${props => typeof props.width === 'number' ? `${props.width}px` : props.width};
  height: ${props => typeof props.height === 'number' ? `${props.height}px` : props.height};
  gap: ${props => props.isSegmented ? props.theme.lcars.gap : '0'};
  border-radius: 0;
  overflow: hidden;
`

const Segment = styled.div<SegmentProps>`
  background-color: ${props => props.color};
  flex: ${props => props.flex || 1};
  border-radius: 0;
`

export const LCARSBar: React.FC<LCARSBarProps> = ({
  width = '100%',
  height = '30px',
  colors = ['neonCarrot'],
  orientation = 'horizontal',
  className,
}) => {
  // Adjust default dimensions based on orientation if not explicitly set
  const finalWidth = orientation === 'vertical' && width === '100%' ? '30px' : width
  const finalHeight = orientation === 'horizontal' && height === '30px' ? '30px' : height

  const isSegmented = colors.length > 1

  return (
    <StyledBar
      width={finalWidth}
      height={finalHeight}
      orientation={orientation}
      isSegmented={isSegmented}
      className={className}
      aria-hidden="true"
    >
      {colors.map((colorKey, index) => (
        <Segment
          key={index}
          color={colorMap[colorKey]}
          flex={1}
        />
      ))}
    </StyledBar>
  )
}
