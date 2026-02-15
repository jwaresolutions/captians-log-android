import React from 'react';
import styled, { keyframes } from 'styled-components';
import { LCARSPanel } from './lcars/LCARSPanel';

const indeterminateAnimation = keyframes`
  0% { left: -100%; }
  100% { left: 100%; }
`;

const ProgressContainer = styled.div<{ variant?: 'inline' | 'modal' }>`
  ${props => props.variant === 'modal' && `
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.8);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 10000;
  `}
`;

const ProgressContent = styled.div<{ variant?: 'inline' | 'modal' }>`
  ${props => props.variant === 'modal' && `
    background: ${props.theme.colors.background};
    padding: ${props.theme.spacing.xl};
    border-radius: 8px;
    min-width: 300px;
    text-align: center;
  `}

  ${props => props.variant === 'inline' && `
    padding: ${props.theme.spacing.md};
  `}
`;

const ProgressBarContainer = styled.div`
  width: 100%;
  height: 20px;
  background: ${props => props.theme.colors.surface.dark};
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  margin: ${props => props.theme.spacing.md} 0;
`;

const ProgressBar = styled.div<{ 
  progress?: number; 
  indeterminate?: boolean;
  color?: 'primary' | 'success' | 'warning' | 'error';
}>`
  height: 100%;
  background: ${props => {
    switch (props.color) {
      case 'success': return props.theme.colors.status.success;
      case 'warning': return props.theme.colors.status.warning;
      case 'error': return props.theme.colors.status.error;
      default: return props.theme.colors.primary.neonCarrot;
    }
  }};
  border-radius: 10px;
  transition: width 0.3s ease;
  
  ${props => props.indeterminate ? `
    position: absolute;
    width: 30%;
    animation: ${indeterminateAnimation} 2s ease-in-out infinite;
  ` : `
    width: ${props.progress || 0}%;
  `}
`;

const ProgressText = styled.div`
  color: ${props => props.theme.colors.text.primary};
  font-size: ${props => props.theme.typography.fontSize.md};
  margin-bottom: ${props => props.theme.spacing.sm};
`;

const ProgressPercentage = styled.div`
  color: ${props => props.theme.colors.text.light};
  font-size: ${props => props.theme.typography.fontSize.sm};
  font-family: monospace;
`;

const StepIndicator = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: ${props => props.theme.spacing.md} 0;
`;

const Step = styled.div<{ active?: boolean; completed?: boolean }>`
  display: flex;
  align-items: center;
  gap: ${props => props.theme.spacing.sm};
  color: ${props => {
    if (props.completed) return props.theme.colors.status.success;
    if (props.active) return props.theme.colors.primary.neonCarrot;
    return props.theme.colors.text.light;
  }};
  
  &::before {
    content: '';
    width: 12px;
    height: 12px;
    border-radius: 50%;
    background: ${props => {
      if (props.completed) return props.theme.colors.status.success;
      if (props.active) return props.theme.colors.primary.neonCarrot;
      return props.theme.colors.surface.dark;
    }};
    border: 2px solid ${props => {
      if (props.completed) return props.theme.colors.status.success;
      if (props.active) return props.theme.colors.primary.neonCarrot;
      return props.theme.colors.text.light;
    }};
  }
`;

const StepConnector = styled.div<{ completed?: boolean }>`
  flex: 1;
  height: 2px;
  background: ${props =>
    props.completed
      ? props.theme.colors.status.success
      : props.theme.colors.surface.dark
  };
  margin: 0 ${props => props.theme.spacing.sm};
`;

interface ProgressIndicatorProps {
  progress?: number; // 0-100 for determinate progress
  indeterminate?: boolean;
  text?: string;
  variant?: 'inline' | 'modal';
  color?: 'primary' | 'success' | 'warning' | 'error';
  showPercentage?: boolean;
  steps?: Array<{
    label: string;
    completed?: boolean;
    active?: boolean;
  }>;
}

export const ProgressIndicator: React.FC<ProgressIndicatorProps> = ({
  progress,
  indeterminate = false,
  text,
  variant = 'inline',
  color = 'primary',
  showPercentage = true,
  steps
}) => {
  const content = (
    <ProgressContent variant={variant}>
      {text && <ProgressText>{text}</ProgressText>}
      
      {steps ? (
        <StepIndicator>
          {steps.map((step, index) => (
            <React.Fragment key={index}>
              <Step active={step.active} completed={step.completed}>
                {step.label}
              </Step>
              {index < steps.length - 1 && (
                <StepConnector completed={step.completed} />
              )}
            </React.Fragment>
          ))}
        </StepIndicator>
      ) : (
        <>
          <ProgressBarContainer>
            <ProgressBar
              progress={progress}
              indeterminate={indeterminate}
              color={color}
            />
          </ProgressBarContainer>
          
          {showPercentage && !indeterminate && progress !== undefined && (
            <ProgressPercentage>
              {Math.round(progress)}%
            </ProgressPercentage>
          )}
        </>
      )}
    </ProgressContent>
  );

  if (variant === 'modal') {
    return (
      <ProgressContainer variant="modal">
        <LCARSPanel>
          {content}
        </LCARSPanel>
      </ProgressContainer>
    );
  }

  return (
    <ProgressContainer variant="inline">
      {content}
    </ProgressContainer>
  );
};

/**
 * Hook for managing multi-step operations with progress
 */
export const useStepProgress = (totalSteps: number) => {
  const [currentStep, setCurrentStep] = React.useState(0);
  const [completedSteps, setCompletedSteps] = React.useState<Set<number>>(new Set());

  const nextStep = React.useCallback(() => {
    setCompletedSteps(prev => new Set([...prev, currentStep]));
    setCurrentStep(prev => Math.min(prev + 1, totalSteps - 1));
  }, [currentStep, totalSteps]);

  const goToStep = React.useCallback((step: number) => {
    if (step >= 0 && step < totalSteps) {
      setCurrentStep(step);
    }
  }, [totalSteps]);

  const reset = React.useCallback(() => {
    setCurrentStep(0);
    setCompletedSteps(new Set());
  }, []);

  const progress = React.useMemo(() => {
    return (completedSteps.size / totalSteps) * 100;
  }, [completedSteps.size, totalSteps]);

  return {
    currentStep,
    completedSteps,
    progress,
    nextStep,
    goToStep,
    reset,
    isCompleted: completedSteps.size === totalSteps
  };
};

/**
 * Upload progress component for file uploads
 */
interface UploadProgressProps {
  files: Array<{
    name: string;
    progress: number;
    status: 'pending' | 'uploading' | 'completed' | 'error';
    error?: string;
  }>;
  onCancel?: () => void;
}

export const UploadProgress: React.FC<UploadProgressProps> = ({
  files,
  onCancel
}) => {
  const totalProgress = files.reduce((sum, file) => sum + file.progress, 0) / files.length;
  const completedFiles = files.filter(f => f.status === 'completed').length;
  const errorFiles = files.filter(f => f.status === 'error').length;

  return (
    <LCARSPanel>
      <ProgressContent>
        <ProgressText>
          Uploading {files.length} file{files.length !== 1 ? 's' : ''}
        </ProgressText>
        
        <ProgressBarContainer>
          <ProgressBar
            progress={totalProgress}
            color={errorFiles > 0 ? 'error' : 'primary'}
          />
        </ProgressBarContainer>
        
        <ProgressPercentage>
          {completedFiles} of {files.length} completed
          {errorFiles > 0 && ` (${errorFiles} failed)`}
        </ProgressPercentage>
        
        <div style={{ marginTop: '16px', maxHeight: '200px', overflowY: 'auto' }}>
          {files.map((file, index) => (
            <div key={index} style={{ 
              display: 'flex', 
              justifyContent: 'space-between', 
              alignItems: 'center',
              padding: '4px 0',
              fontSize: '14px'
            }}>
              <span style={{ 
                color: file.status === 'error' ? '#ff6b6b' : 
                       file.status === 'completed' ? '#51cf66' : '#868e96'
              }}>
                {file.name}
              </span>
              <span style={{ fontFamily: 'monospace', fontSize: '12px' }}>
                {file.status === 'error' ? 'Failed' : `${Math.round(file.progress)}%`}
              </span>
            </div>
          ))}
        </div>
        
        {onCancel && (
          <button
            onClick={onCancel}
            style={{
              marginTop: '16px',
              padding: '8px 16px',
              background: 'transparent',
              border: '1px solid #868e96',
              color: '#868e96',
              cursor: 'pointer'
            }}
          >
            Cancel
          </button>
        )}
      </ProgressContent>
    </LCARSPanel>
  );
};