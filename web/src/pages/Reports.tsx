import React from 'react'
import { useNavigate } from 'react-router-dom'
import styled from 'styled-components'
import { 
  LCARSPanel, 
  LCARSButton,
  LCARSHeader
} from '../components/lcars'

const PageContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.lg};
  max-width: 800px;
  margin: 0 auto;
`

const ReportsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: ${props => props.theme.spacing.lg};

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`

const ReportCard = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.md};
  padding: ${props => props.theme.spacing.lg};
  background-color: ${props => props.theme.colors.surface.dark};
  border: 2px solid ${props => props.theme.colors.primary.neonCarrot};
  border-radius: ${props => props.theme.borderRadius.lg};
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    border-color: ${props => props.theme.colors.primary.tanoi};
    background-color: ${props => props.theme.colors.surface.medium};
  }

  &.secondary {
    border-color: ${props => props.theme.colors.primary.lilac};

    &:hover {
      border-color: ${props => props.theme.colors.primary.lilac};
    }
  }
`

const ReportTitle = styled.h2`
  color: ${props => props.theme.colors.primary.neonCarrot};
  font-size: ${props => props.theme.typography.fontSize.xl};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 2px;
  margin: 0;

  .secondary & {
    color: ${props => props.theme.colors.primary.lilac};
  }
`

const ReportDescription = styled.p`
  color: ${props => props.theme.colors.text.secondary};
  font-size: ${props => props.theme.typography.fontSize.md};
  line-height: ${props => props.theme.typography.lineHeight.normal};
  margin: 0;
`

const ReportFeatures = styled.ul`
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.xs};
`

const ReportFeature = styled.li`
  color: ${props => props.theme.colors.text.muted};
  font-size: ${props => props.theme.typography.fontSize.sm};

  &::before {
    content: '▶';
    color: ${props => props.theme.colors.primary.neonCarrot};
    margin-right: ${props => props.theme.spacing.sm};
    font-size: 0.8em;
  }

  .secondary &::before {
    color: ${props => props.theme.colors.primary.lilac};
  }
`

export const Reports: React.FC = () => {
  const navigate = useNavigate()

  return (
    <PageContainer>
      <LCARSHeader>System Reports</LCARSHeader>
      
      <LCARSPanel title="Available Reports" variant="primary">
        <ReportsGrid>
          <ReportCard onClick={() => navigate('/reports/license')}>
            <ReportTitle>Captain's License Progress</ReportTitle>
            <ReportDescription>
              Track your progress toward OUPV (6-pack) Captain's License requirements
            </ReportDescription>
            <ReportFeatures>
              <ReportFeature>360-day total sea time tracking</ReportFeature>
              <ReportFeature>90-day recent experience monitoring</ReportFeature>
              <ReportFeature>Progress charts and completion estimates</ReportFeature>
              <ReportFeature>Detailed statistics and requirements</ReportFeature>
            </ReportFeatures>
          </ReportCard>

          <ReportCard className="secondary" onClick={() => navigate('/reports/maintenance')}>
            <ReportTitle>Maintenance Reports</ReportTitle>
            <ReportDescription>
              Comprehensive maintenance tracking and cost analysis for all vessels
            </ReportDescription>
            <ReportFeatures>
              <ReportFeature>Upcoming and overdue task tracking</ReportFeature>
              <ReportFeature>Cost analysis and completion rates</ReportFeature>
              <ReportFeature>Template status and activity monitoring</ReportFeature>
              <ReportFeature>Recent completion history</ReportFeature>
            </ReportFeatures>
          </ReportCard>
        </ReportsGrid>
      </LCARSPanel>

      <LCARSPanel title="Quick Access" variant="accent">
        <div style={{ display: 'flex', gap: '16px', justifyContent: 'center', flexWrap: 'wrap' }}>
          <LCARSButton 
            variant="primary" 
            onClick={() => navigate('/reports/license')}
          >
            License Progress
          </LCARSButton>
          <LCARSButton 
            variant="secondary" 
            onClick={() => navigate('/reports/maintenance')}
          >
            Maintenance Reports
          </LCARSButton>
        </div>
      </LCARSPanel>
    </PageContainer>
  )
}