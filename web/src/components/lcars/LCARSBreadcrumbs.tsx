import React from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import styled from 'styled-components'

const BreadcrumbContainer = styled.div`
  display: flex;
  align-items: center;
  gap: ${props => props.theme.spacing.sm};
  padding: ${props => props.theme.spacing.sm} ${props => props.theme.spacing.md};
  background-color: ${props => props.theme.colors.surface.medium};
  border-bottom: 1px solid ${props => props.theme.colors.primary.anakiwa};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: ${props => props.theme.typography.fontSize.sm};

  @media (max-width: 768px) {
    padding: ${props => props.theme.spacing.xs} ${props => props.theme.spacing.sm};
    font-size: ${props => props.theme.typography.fontSize.xs};
  }
`

const BreadcrumbItem = styled.button<{ $isLast?: boolean }>`
  background: none;
  border: none;
  color: ${props => props.$isLast
    ? props.theme.colors.primary.neonCarrot
    : props.theme.colors.primary.anakiwa
  };
  font-family: inherit;
  font-size: inherit;
  font-weight: ${props => props.$isLast ? 'bold' : 'normal'};
  text-transform: uppercase;
  letter-spacing: 1px;
  cursor: ${props => props.$isLast ? 'default' : 'pointer'};
  padding: 0;

  &:hover:not(:disabled) {
    color: ${props => props.theme.colors.primary.goldenTanoi};
  }

  &:disabled {
    cursor: default;
  }
`

const BreadcrumbSeparator = styled.span`
  color: ${props => props.theme.colors.text.secondary};
  font-weight: bold;
`

interface BreadcrumbItem {
  label: string
  path: string
}

const routeLabels: Record<string, string> = {
  '/': 'Dashboard',
  '/dashboard': 'Dashboard',
  '/boats': 'Vessels',
  '/boats/new': 'New Vessel',
  '/trips': 'Trip Log',
  '/notes': 'Notes',
  '/notes/new': 'New Note',
  '/todos': 'To-Do Lists',
  '/todos/new': 'New List',
  '/maintenance': 'Maintenance',
  '/maintenance/templates/new': 'New Template',
  '/map': 'Navigation',
  '/reports': 'Reports',
  '/reports/license': 'License Progress',
  '/reports/maintenance': 'Maintenance Reports',
  '/calendar': 'Calendar',
  '/photos': 'Photo Gallery',
  '/settings': 'Settings',
  '/settings/backup': 'Backup Manager',
}

export const LCARSBreadcrumbs: React.FC = () => {
  const location = useLocation()
  const navigate = useNavigate()

  const generateBreadcrumbs = (): BreadcrumbItem[] => {
    const pathSegments = location.pathname.split('/').filter(Boolean)
    const breadcrumbs: BreadcrumbItem[] = []

    // Always start with Dashboard
    if (location.pathname !== '/') {
      breadcrumbs.push({
        label: 'Dashboard',
        path: '/',
      })
    }

    // Build breadcrumbs from path segments
    let currentPath = ''
    pathSegments.forEach((segment) => {
      currentPath += `/${segment}`

      // Skip if this is the current page and we're at root
      if (currentPath === '/' && location.pathname === '/') {
        return
      }

      const label = routeLabels[currentPath] || segment.charAt(0).toUpperCase() + segment.slice(1)

      breadcrumbs.push({
        label,
        path: currentPath,
      })
    })

    return breadcrumbs
  }

  const breadcrumbs = generateBreadcrumbs()

  // Don't show breadcrumbs if we're on the dashboard
  if (location.pathname === '/' || location.pathname === '/dashboard') {
    return null
  }

  return (
    <BreadcrumbContainer>
      {breadcrumbs.map((crumb, index) => (
        <React.Fragment key={crumb.path}>
          <BreadcrumbItem
            $isLast={index === breadcrumbs.length - 1}
            onClick={() => !crumb.path || index === breadcrumbs.length - 1 ? undefined : navigate(crumb.path)}
            disabled={index === breadcrumbs.length - 1}
          >
            {crumb.label}
          </BreadcrumbItem>
          {index < breadcrumbs.length - 1 && (
            <BreadcrumbSeparator>→</BreadcrumbSeparator>
          )}
        </React.Fragment>
      ))}
    </BreadcrumbContainer>
  )
}
