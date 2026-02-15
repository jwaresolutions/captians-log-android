import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { apiService } from '../services/api';

const OfflineContainer = styled.div<{ $show: boolean }>`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10000;
  transform: translateY(${props => props.$show ? '0' : '-100%'});
  transition: transform 0.3s ease-in-out;
`;

const OfflineBanner = styled.div`
  background: ${props => props.theme.colors.status.warning};
  color: ${props => props.theme.colors.background};
  padding: ${props => props.theme.spacing.sm} ${props => props.theme.spacing.md};
  text-align: center;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: ${props => props.theme.spacing.md};
`;

const RetryButton = styled.button`
  background: transparent;
  border: 1px solid ${props => props.theme.colors.background};
  color: ${props => props.theme.colors.background};
  padding: 4px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;

  &:hover {
    background: ${props => props.theme.colors.background}20;
  }
`;

const ConnectionStatus = styled.div<{ $isOnline: boolean }>`
  position: static;
  z-index: auto;
  padding: 4px 12px;
  border-radius: 9999px;
  font-size: 11px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 8px;
  background: ${props => props.$isOnline
    ? props.theme.colors.status.success
    : props.theme.colors.status.error};
  color: white;

  &::before {
    content: '';
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: currentColor;
    animation: ${props => props.$isOnline ? 'none' : 'pulse 2s infinite'};
  }

  @keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
  }
`;

interface OfflineIndicatorProps {
  showConnectionStatus?: boolean;
}

export const OfflineIndicator: React.FC<OfflineIndicatorProps> = ({
  showConnectionStatus = true
}) => {
  const [isOnline, setIsOnline] = useState(navigator.onLine);
  const [showOfflineBanner, setShowOfflineBanner] = useState(false);
  const [isRetrying, setIsRetrying] = useState(false);

  useEffect(() => {
    const handleOnline = () => {
      setIsOnline(true);
      setShowOfflineBanner(false);
      
      // Check if we can actually reach the API
      checkApiConnectivity();
    };

    const handleOffline = () => {
      setIsOnline(false);
      setShowOfflineBanner(true);
    };

    // Check API connectivity periodically when offline
    const checkApiConnectivity = async () => {
      try {
        const canConnect = await apiService.checkConnectivity();
        if (!canConnect && navigator.onLine) {
          // Browser thinks we're online but API is unreachable
          setIsOnline(false);
          setShowOfflineBanner(true);
        }
      } catch {
        // API check failed
        if (navigator.onLine) {
          setIsOnline(false);
          setShowOfflineBanner(true);
        }
      }
    };

    // Set up event listeners
    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    // Initial connectivity check
    if (navigator.onLine) {
      checkApiConnectivity();
    } else {
      setShowOfflineBanner(true);
    }

    // Periodic connectivity check when offline
    const connectivityInterval = setInterval(() => {
      if (!isOnline) {
        checkApiConnectivity();
      }
    }, 30000); // Check every 30 seconds

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
      clearInterval(connectivityInterval);
    };
  }, [isOnline]);

  const handleRetry = async () => {
    setIsRetrying(true);
    
    try {
      const canConnect = await apiService.checkConnectivity();
      if (canConnect) {
        setIsOnline(true);
        setShowOfflineBanner(false);
      }
    } catch {
      // Still can't connect
    } finally {
      setIsRetrying(false);
    }
  };

  return (
    <>
      {/* Offline Banner */}
      <OfflineContainer $show={showOfflineBanner}>
        <OfflineBanner>
          <span>⚠ You are currently offline</span>
          <RetryButton 
            onClick={handleRetry} 
            disabled={isRetrying}
          >
            {isRetrying ? 'Checking...' : 'Retry'}
          </RetryButton>
        </OfflineBanner>
      </OfflineContainer>

      {/* Connection Status Indicator */}
      {showConnectionStatus && (
        <ConnectionStatus $isOnline={isOnline}>
          {isOnline ? 'Online' : 'Offline'}
        </ConnectionStatus>
      )}
    </>
  );
};

/**
 * Hook to monitor online/offline status
 */
export const useOnlineStatus = () => {
  const [isOnline, setIsOnline] = useState(navigator.onLine);
  const [isApiReachable, setIsApiReachable] = useState(true);

  useEffect(() => {
    const handleOnline = () => setIsOnline(true);
    const handleOffline = () => setIsOnline(false);

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    // Check API reachability periodically
    const checkApi = async () => {
      try {
        const reachable = await apiService.checkConnectivity();
        setIsApiReachable(reachable);
      } catch {
        setIsApiReachable(false);
      }
    };

    const apiCheckInterval = setInterval(checkApi, 60000); // Check every minute
    checkApi(); // Initial check

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
      clearInterval(apiCheckInterval);
    };
  }, []);

  return {
    isOnline: isOnline && isApiReachable,
    isBrowserOnline: isOnline,
    isApiReachable
  };
};