import React from 'react';
import styled, { keyframes } from 'styled-components';

const spin = keyframes`
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
`;

const pulse = keyframes`
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
`;

const SpinnerContainer = styled.div<{ size?: 'sm' | 'md' | 'lg'; fullScreen?: boolean }>`
  display: flex;
  align-items: center;
  justify-content: center;
  ${props => props.fullScreen && `
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.8);
    z-index: 9999;
  `}
  ${props => !props.fullScreen && `
    padding: ${props.theme.spacing.xl};
  `}
`;

const Spinner = styled.div<{ size?: 'sm' | 'md' | 'lg' }>`
  width: ${props => {
    switch (props.size) {
      case 'sm': return '20px';
      case 'lg': return '60px';
      default: return '40px';
    }
  }};
  height: ${props => {
    switch (props.size) {
      case 'sm': return '20px';
      case 'lg': return '60px';
      default: return '40px';
    }
  }};
  border: 3px solid ${props => props.theme.colors.primary.neonCarrot}40;
  border-top: 3px solid ${props => props.theme.colors.primary.neonCarrot};
  border-radius: 50%;
  animation: ${spin} 1s linear infinite;
`;

const LoadingText = styled.div<{ size?: 'sm' | 'md' | 'lg' }>`
  margin-left: ${props => props.theme.spacing.md};
  color: ${props => props.theme.colors.primary.neonCarrot};
  font-size: ${props => {
    switch (props.size) {
      case 'sm': return props.theme.typography.fontSize.sm;
      case 'lg': return props.theme.typography.fontSize.lg;
      default: return props.theme.typography.fontSize.md;
    }
  }};
  animation: ${pulse} 2s ease-in-out infinite;
`;

const LCARSLoadingBar = styled.div`
  width: 200px;
  height: 20px;
  background: ${props => props.theme.colors.surface.dark};
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(
      90deg,
      transparent,
      ${props => props.theme.colors.primary.neonCarrot},
      transparent
    );
    animation: ${keyframes`
      0% { left: -100%; }
      100% { left: 100%; }
    `} 2s ease-in-out infinite;
  }
`;

interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  text?: string;
  fullScreen?: boolean;
  variant?: 'spinner' | 'bar';
}

export const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
  size = 'md',
  text,
  fullScreen = false,
  variant = 'spinner'
}) => {
  return (
    <SpinnerContainer size={size} fullScreen={fullScreen}>
      {variant === 'spinner' ? (
        <>
          <Spinner size={size} />
          {text && <LoadingText size={size}>{text}</LoadingText>}
        </>
      ) : (
        <div style={{ textAlign: 'center' }}>
          <LCARSLoadingBar />
          {text && <LoadingText size={size} style={{ marginLeft: 0, marginTop: '8px' }}>{text}</LoadingText>}
        </div>
      )}
    </SpinnerContainer>
  );
};

/**
 * Skeleton loading component for content placeholders
 */
const SkeletonBase = styled.div`
  background: linear-gradient(
    90deg,
    ${props => props.theme.colors.surface.dark} 25%,
    ${props => props.theme.colors.surface.medium} 50%,
    ${props => props.theme.colors.surface.dark} 75%
  );
  background-size: 200% 100%;
  animation: ${keyframes`
    0% { background-position: 200% 0; }
    100% { background-position: -200% 0; }
  `} 2s ease-in-out infinite;
  border-radius: 4px;
`;

const SkeletonText = styled(SkeletonBase)<{ width?: string; height?: string }>`
  width: ${props => props.width || '100%'};
  height: ${props => props.height || '1em'};
  margin: 4px 0;
`;

const SkeletonCard = styled(SkeletonBase)`
  width: 100%;
  height: 120px;
  margin: 8px 0;
`;

interface SkeletonProps {
  variant?: 'text' | 'card';
  width?: string;
  height?: string;
  lines?: number;
}

export const Skeleton: React.FC<SkeletonProps> = ({
  variant = 'text',
  width,
  height,
  lines = 1
}) => {
  if (variant === 'card') {
    return <SkeletonCard />;
  }

  if (lines === 1) {
    return <SkeletonText width={width} height={height} />;
  }

  return (
    <div>
      {Array.from({ length: lines }, (_, i) => (
        <SkeletonText
          key={i}
          width={i === lines - 1 ? '60%' : width}
          height={height}
        />
      ))}
    </div>
  );
};