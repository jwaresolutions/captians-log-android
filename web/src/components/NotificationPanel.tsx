import React, { useState, useEffect } from 'react';
import styled, { keyframes } from 'styled-components';
import { LCARSPanel } from './lcars/LCARSPanel';
import { LCARSButton } from './lcars/LCARSButton';
import { LCARSHeader } from './lcars/LCARSHeader';
import { apiService } from '../services/api';
import { Notification } from '../types/api';

const slideIn = keyframes`
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
`;

const slideOut = keyframes`
  from {
    transform: translateX(0);
    opacity: 1;
  }
  to {
    transform: translateX(100%);
    opacity: 0;
  }
`;

const NotificationContainer = styled.div<{ show: boolean }>`
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 9999;
  max-width: 400px;
  width: 100%;
  animation: ${props => props.show ? slideIn : slideOut} 0.3s ease-in-out;
  
  @media (max-width: 768px) {
    top: 60px;
    right: 10px;
    left: 10px;
    max-width: none;
  }
`;

const NotificationList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 80vh;
  overflow-y: auto;
`;

const NotificationItem = styled.div<{ 
  type?: 'maintenance' | 'system' | 'warning' | 'error';
  isRead?: boolean;
}>`
  padding: 16px;
  border-left: 4px solid ${props => {
    switch (props.type) {
      case 'maintenance': return props.theme.colors.primary.neonCarrot;
      case 'warning': return props.theme.colors.status.warning;
      case 'error': return props.theme.colors.status.error;
      default: return props.theme.colors.primary.anakiwa;
    }
  }};
  background: ${props => props.isRead
    ? props.theme.colors.surface.dark
    : props.theme.colors.background
  };
  opacity: ${props => props.isRead ? 0.7 : 1};
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: ${props => props.theme.colors.surface.medium};
  }
`;

const NotificationHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
`;

const NotificationTitle = styled.div`
  font-weight: bold;
  color: ${props => props.theme.colors.text.primary};
  font-size: 14px;
`;

const NotificationTime = styled.div`
  font-size: 12px;
  color: ${props => props.theme.colors.text.light};
  white-space: nowrap;
  margin-left: 8px;
`;

const NotificationMessage = styled.div`
  color: ${props => props.theme.colors.text.light};
  font-size: 13px;
  line-height: 1.4;
`;

const NotificationActions = styled.div`
  display: flex;
  gap: 8px;
  margin-top: 12px;
`;

const NotificationBadge = styled.div<{ count: number }>`
  position: absolute;
  top: -8px;
  right: -8px;
  background: ${props => props.theme.colors.status.error};
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: bold;
  min-width: 20px;
  
  ${props => props.count > 99 && `
    border-radius: 10px;
    padding: 0 6px;
    width: auto;
  `}
`;

const NotificationToggle = styled.button<{ $hasUnread: boolean }>`
  position: relative;
  background: ${props => props.theme.colors.primary.anakiwa};
  color: ${props => props.theme.colors.text.inverse};
  border: none;
  border-radius: 9999px;
  padding: 0 16px;
  height: 32px;
  cursor: pointer;
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: 14px;
  font-weight: bold;
  text-transform: uppercase;
  transition: filter 0.2s ease;

  &:hover {
    filter: brightness(1.2);
  }

  ${props => props.$hasUnread && `
    filter: brightness(1.3);
  `}
`;

const EmptyState = styled.div`
  text-align: center;
  padding: 32px 16px;
  color: ${props => props.theme.colors.text.light};
`;

interface NotificationPanelProps {
  className?: string;
}

export const NotificationPanel: React.FC<NotificationPanelProps> = ({ className }) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const unreadCount = (notifications || []).filter(n => !n.read).length;

  useEffect(() => {
    loadNotifications();
    
    // Poll for new notifications every 30 seconds
    const interval = setInterval(loadNotifications, 30000);
    
    return () => clearInterval(interval);
  }, []);

  const loadNotifications = async () => {
    try {
      setIsLoading(true);
      const data = await apiService.getNotifications();
      setNotifications(data);
    } catch (error) {
      console.error('Failed to load notifications:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleTogglePanel = () => {
    setIsOpen(!isOpen);
  };

  const handleMarkAsRead = async (notification: Notification) => {
    if (notification.read) return;

    try {
      await apiService.markNotificationAsRead(notification.id);
      setNotifications(prev => 
        prev.map(n => 
          n.id === notification.id ? { ...n, read: true } : n
        )
      );
    } catch (error) {
      console.error('Failed to mark notification as read:', error);
    }
  };

  const handleMarkAllAsRead = async () => {
    const unreadNotifications = (notifications || []).filter(n => !n.read);
    
    try {
      await Promise.all(
        unreadNotifications.map(n => apiService.markNotificationAsRead(n.id))
      );
      
      setNotifications(prev => 
        prev.map(n => ({ ...n, read: true }))
      );
    } catch (error) {
      console.error('Failed to mark all notifications as read:', error);
    }
  };

  const formatTime = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;
    
    return date.toLocaleDateString();
  };

  const getNotificationIcon = (type: string) => {
    switch (type) {
      case 'maintenance_due': return '🔧';
      case 'system': return 'ℹ️';
      case 'warning': return '⚠️';
      case 'error': return '❌';
      default: return '📢';
    }
  };

  return (
    <div className={className}>
      <NotificationToggle 
        onClick={handleTogglePanel}
        $hasUnread={unreadCount > 0}
      >
        Alerts
        {unreadCount > 0 && (
          <NotificationBadge count={unreadCount}>
            {unreadCount > 99 ? '99+' : unreadCount}
          </NotificationBadge>
        )}
      </NotificationToggle>

      {isOpen && (
        <NotificationContainer show={isOpen}>
          <LCARSPanel>
            <div style={{ padding: '16px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                <LCARSHeader level={3}>System Alerts</LCARSHeader>
                <div style={{ display: 'flex', gap: '8px' }}>
                  {unreadCount > 0 && (
                    <LCARSButton 
                      size="sm" 
                      variant="secondary"
                      onClick={handleMarkAllAsRead}
                    >
                      Mark All Read
                    </LCARSButton>
                  )}
                  <LCARSButton 
                    size="sm" 
                    variant="secondary"
                    onClick={handleTogglePanel}
                  >
                    Close
                  </LCARSButton>
                </div>
              </div>

              {isLoading ? (
                <div style={{ textAlign: 'center', padding: '20px' }}>
                  Loading notifications...
                </div>
              ) : notifications.length === 0 ? (
                <EmptyState>
                  <div style={{ fontSize: '32px', marginBottom: '8px' }}>📭</div>
                  <div>No notifications</div>
                </EmptyState>
              ) : (
                <NotificationList>
                  {notifications.map((notification) => (
                    <NotificationItem
                      key={notification.id}
                      type={notification.type as any}
                      isRead={notification.read}
                      onClick={() => handleMarkAsRead(notification)}
                    >
                      <NotificationHeader>
                        <NotificationTitle>
                          {getNotificationIcon(notification.type)} {notification.title}
                        </NotificationTitle>
                        <NotificationTime>
                          {formatTime(notification.createdAt)}
                        </NotificationTime>
                      </NotificationHeader>
                      
                      <NotificationMessage>
                        {notification.message}
                      </NotificationMessage>

                      {notification.entityType && notification.entityId && (
                        <NotificationActions>
                          <LCARSButton 
                            size="sm" 
                            variant="primary"
                            onClick={() => {
                              // Navigate to the related entity
                              const path = notification.entityType === 'maintenance'
                                ? `/maintenance/events/${notification.entityId}`
                                : `/${notification.entityType}/${notification.entityId}`;
                              window.location.href = path;
                            }}
                          >
                            View Details
                          </LCARSButton>
                        </NotificationActions>
                      )}
                    </NotificationItem>
                  ))}
                </NotificationList>
              )}
            </div>
          </LCARSPanel>
        </NotificationContainer>
      )}
    </div>
  );
};

/**
 * Toast notification component for temporary messages
 */
interface ToastNotificationProps {
  message: string;
  type?: 'success' | 'error' | 'warning' | 'info';
  duration?: number;
  onClose?: () => void;
}

const ToastContainer = styled.div<{ type: string }>`
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 10001;
  padding: 16px 20px;
  border-radius: 8px;
  color: white;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 12px;
  max-width: 400px;
  animation: ${slideIn} 0.3s ease-in-out;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  
  background: ${props => {
    switch (props.type) {
      case 'success': return '#51cf66';
      case 'error': return '#ff6b6b';
      case 'warning': return '#ffd43b';
      case 'info': return '#339af0';
      default: return '#339af0';
    }
  }};
  
  @media (max-width: 768px) {
    bottom: 10px;
    right: 10px;
    left: 10px;
    max-width: none;
  }
`;

const ToastCloseButton = styled.button`
  background: transparent;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 18px;
  padding: 0;
  margin-left: auto;
  
  &:hover {
    opacity: 0.7;
  }
`;

export const ToastNotification: React.FC<ToastNotificationProps> = ({
  message,
  type = 'info',
  duration = 5000,
  onClose
}) => {
  useEffect(() => {
    if (duration > 0) {
      const timer = setTimeout(() => {
        onClose?.();
      }, duration);
      
      return () => clearTimeout(timer);
    }
  }, [duration, onClose]);

  const getIcon = () => {
    switch (type) {
      case 'success': return '✓';
      case 'error': return '✕';
      case 'warning': return '⚠';
      case 'info': return 'ℹ';
      default: return 'ℹ';
    }
  };

  return (
    <ToastContainer type={type}>
      <span>{getIcon()}</span>
      <span>{message}</span>
      <ToastCloseButton onClick={onClose}>
        ×
      </ToastCloseButton>
    </ToastContainer>
  );
};

/**
 * Hook for managing toast notifications
 */
export const useToast = () => {
  const [toasts, setToasts] = useState<Array<{
    id: string;
    message: string;
    type: 'success' | 'error' | 'warning' | 'info';
    duration?: number;
  }>>([]);

  const showToast = (
    message: string, 
    type: 'success' | 'error' | 'warning' | 'info' = 'info',
    duration: number = 5000
  ) => {
    const id = Math.random().toString(36).substr(2, 9);
    setToasts(prev => [...prev, { id, message, type, duration }]);
  };

  const removeToast = (id: string) => {
    setToasts(prev => prev.filter(toast => toast.id !== id));
  };

  const ToastContainer = () => (
    <div style={{ position: 'fixed', bottom: 20, right: 20, zIndex: 10001 }}>
      {toasts.map((toast, index) => (
        <div key={toast.id} style={{ marginBottom: index > 0 ? '8px' : '0' }}>
          <ToastNotification
            message={toast.message}
            type={toast.type}
            duration={toast.duration}
            onClose={() => removeToast(toast.id)}
          />
        </div>
      ))}
    </div>
  );

  return {
    showToast,
    ToastContainer
  };
};