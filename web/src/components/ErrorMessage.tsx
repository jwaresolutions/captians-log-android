import React from 'react';
import styled from 'styled-components';
import { LCARSPanel } from './lcars/LCARSPanel';
import { LCARSButton } from './lcars/LCARSButton';

const ErrorContainer = styled.div<{ variant?: 'inline' | 'card' | 'banner' }>`
  ${props => {
    switch (props.variant) {
      case 'inline':
        return `
          display: inline-flex;
          align-items: center;
          padding: ${props.theme.spacing.sm};
          background: ${props.theme.colors.status.error}20;
          border: 1px solid ${props.theme.colors.status.error};
          border-radius: 4px;
          color: ${props.theme.colors.status.error};
        `;
      case 'banner':
        return `
          width: 100%;
          padding: ${props.theme.spacing.md};
          background: ${props.theme.colors.status.error}20;
          border-left: 4px solid ${props.theme.colors.status.error};
          color: ${props.theme.colors.status.error};
        `;
      default:
        return `
          padding: ${props.theme.spacing.lg};
          text-align: center;
        `;
    }
  }}
`;

const ErrorIcon = styled.div`
  font-size: 1.2em;
  margin-right: ${props => props.theme.spacing.sm};
  color: ${props => props.theme.colors.status.error};
`;

const ErrorTitle = styled.div`
  font-weight: bold;
  font-size: ${props => props.theme.typography.fontSize.lg};
  color: ${props => props.theme.colors.status.error};
  margin-bottom: ${props => props.theme.spacing.sm};
`;

const ErrorDescription = styled.div`
  color: ${props => props.theme.colors.text.light};
  margin-bottom: ${props => props.theme.spacing.md};
  line-height: 1.5;
`;

const ErrorActions = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.sm};
  justify-content: center;
  margin-top: ${props => props.theme.spacing.md};
`;

const ErrorCode = styled.code`
  background: ${props => props.theme.colors.surface.dark};
  padding: 2px 6px;
  border-radius: 3px;
  font-family: monospace;
  font-size: 0.9em;
  color: ${props => props.theme.colors.status.error};
`;

interface ErrorMessageProps {
  title?: string;
  message: string;
  code?: string;
  variant?: 'inline' | 'card' | 'banner';
  showIcon?: boolean;
  onRetry?: () => void;
  onDismiss?: () => void;
  retryText?: string;
  dismissText?: string;
}

export const ErrorMessage: React.FC<ErrorMessageProps> = ({
  title = 'Error',
  message,
  code,
  variant = 'card',
  showIcon = true,
  onRetry,
  onDismiss,
  retryText = 'Try Again',
  dismissText = 'Dismiss'
}) => {
  const content = (
    <ErrorContainer variant={variant}>
      {showIcon && variant === 'inline' && <ErrorIcon>⚠</ErrorIcon>}
      
      {variant !== 'inline' && (
        <ErrorTitle>
          {showIcon && '⚠ '}{title}
        </ErrorTitle>
      )}
      
      <ErrorDescription>
        {message}
        {code && (
          <>
            <br />
            <small>Error code: <ErrorCode>{code}</ErrorCode></small>
          </>
        )}
      </ErrorDescription>
      
      {(onRetry || onDismiss) && (
        <ErrorActions>
          {onRetry && (
            <LCARSButton onClick={onRetry} variant="primary" size="sm">
              {retryText}
            </LCARSButton>
          )}
          {onDismiss && (
            <LCARSButton onClick={onDismiss} variant="secondary" size="sm">
              {dismissText}
            </LCARSButton>
          )}
        </ErrorActions>
      )}
    </ErrorContainer>
  );

  if (variant === 'card') {
    return <LCARSPanel>{content}</LCARSPanel>;
  }

  return content;
};

/**
 * Network error component for offline states
 */
export const NetworkError: React.FC<{
  onRetry?: () => void;
  isOffline?: boolean;
}> = ({ onRetry, isOffline = false }) => {
  return (
    <ErrorMessage
      title={isOffline ? 'Offline' : 'Network Error'}
      message={
        isOffline
          ? 'You are currently offline. Some features may not be available.'
          : 'Unable to connect to the server. Please check your internet connection.'
      }
      code={isOffline ? 'OFFLINE' : 'NETWORK_ERROR'}
      onRetry={onRetry}
      retryText="Retry Connection"
    />
  );
};

/**
 * Not found error component
 */
export const NotFoundError: React.FC<{
  resource?: string;
  onGoBack?: () => void;
}> = ({ resource = 'Resource', onGoBack }) => {
  return (
    <ErrorMessage
      title="Not Found"
      message={`${resource} not found or may have been deleted.`}
      code="NOT_FOUND"
      onRetry={onGoBack}
      retryText="Go Back"
    />
  );
};

/**
 * Permission error component
 */
export const PermissionError: React.FC<{
  onLogin?: () => void;
}> = ({ onLogin }) => {
  return (
    <ErrorMessage
      title="Access Denied"
      message="You don't have permission to access this resource. Please log in or contact an administrator."
      code="FORBIDDEN"
      onRetry={onLogin}
      retryText="Log In"
    />
  );
};