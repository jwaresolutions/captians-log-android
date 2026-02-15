import React, { Component, ErrorInfo, ReactNode } from 'react';
import styled from 'styled-components';
import { LCARSPanel } from './lcars/LCARSPanel';
import { LCARSButton } from './lcars/LCARSButton';
import { LCARSHeader } from './lcars/LCARSHeader';

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error?: Error;
  errorInfo?: ErrorInfo;
}

const ErrorContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: ${props => props.theme.spacing.xl};
  text-align: center;
`;

const ErrorMessage = styled.div`
  color: ${props => props.theme.colors.status.error};
  font-size: ${props => props.theme.typography.fontSize.lg};
  margin: ${props => props.theme.spacing.lg} 0;
`;

const ErrorDetails = styled.details`
  margin-top: ${props => props.theme.spacing.lg};
  padding: ${props => props.theme.spacing.md};
  background: ${props => props.theme.colors.surface.dark};
  border-radius: 4px;
  border: 1px solid ${props => props.theme.colors.status.error};
  max-width: 600px;
  
  summary {
    cursor: pointer;
    color: ${props => props.theme.colors.status.error};
    font-weight: bold;
    margin-bottom: ${props => props.theme.spacing.sm};
  }
  
  pre {
    font-size: ${props => props.theme.typography.fontSize.sm};
    color: ${props => props.theme.colors.text.secondary};
    white-space: pre-wrap;
    word-break: break-word;
    margin: 0;
  }
`;

const ButtonContainer = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.md};
  margin-top: ${props => props.theme.spacing.xl};
`;

export class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error): State {
    return {
      hasError: true,
      error
    };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
    
    this.setState({
      error,
      errorInfo
    });

    // Log error to external service in production
    if (import.meta.env.PROD) {
      // TODO: Send to error reporting service
      console.error('Production error:', {
        error: error.message,
        stack: error.stack,
        componentStack: errorInfo.componentStack
      });
    }
  }

  handleReload = () => {
    window.location.reload();
  };

  handleGoHome = () => {
    window.location.href = '/';
  };

  handleRetry = () => {
    this.setState({ hasError: false, error: undefined, errorInfo: undefined });
  };

  render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback;
      }

      return (
        <ErrorContainer>
          <LCARSPanel>
            <LCARSHeader level={1}>System Error</LCARSHeader>
            
            <ErrorMessage>
              An unexpected error has occurred in the application.
            </ErrorMessage>
            
            <p>
              The error has been logged and will be investigated. 
              You can try reloading the page or returning to the dashboard.
            </p>

            <ButtonContainer>
              <LCARSButton onClick={this.handleRetry} variant="primary">
                Try Again
              </LCARSButton>
              <LCARSButton onClick={this.handleReload} variant="secondary">
                Reload Page
              </LCARSButton>
              <LCARSButton onClick={this.handleGoHome} variant="secondary">
                Go to Dashboard
              </LCARSButton>
            </ButtonContainer>

            {import.meta.env.DEV && this.state.error && (
              <ErrorDetails>
                <summary>Error Details (Development)</summary>
                <pre>
                  <strong>Error:</strong> {this.state.error.message}
                  {'\n\n'}
                  <strong>Stack:</strong>
                  {'\n'}
                  {this.state.error.stack}
                  {this.state.errorInfo && (
                    <>
                      {'\n\n'}
                      <strong>Component Stack:</strong>
                      {'\n'}
                      {this.state.errorInfo.componentStack}
                    </>
                  )}
                </pre>
              </ErrorDetails>
            )}
          </LCARSPanel>
        </ErrorContainer>
      );
    }

    return this.props.children;
  }
}

/**
 * Higher-order component to wrap components with error boundary
 */
export function withErrorBoundary<P extends object>(
  Component: React.ComponentType<P>,
  fallback?: ReactNode
) {
  return function WrappedComponent(props: P) {
    return (
      <ErrorBoundary fallback={fallback}>
        <Component {...props} />
      </ErrorBoundary>
    );
  };
}

/**
 * Hook to handle async errors in functional components
 */
export function useErrorHandler() {
  return (error: Error, errorInfo?: string) => {
    console.error('Async error:', error, errorInfo);
    
    // In a real app, you might want to show a toast notification
    // or send the error to an error reporting service
    if (import.meta.env.PROD) {
      // TODO: Send to error reporting service
      console.error('Production async error:', {
        error: error.message,
        stack: error.stack,
        info: errorInfo
      });
    }
    
    // For now, just throw the error to be caught by the nearest error boundary
    throw error;
  };
}