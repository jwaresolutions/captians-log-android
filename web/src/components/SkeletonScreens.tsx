import React from 'react';
import styled from 'styled-components';
import { LCARSPanel } from './lcars/LCARSPanel';
import { Skeleton } from './LoadingSpinner';

const SkeletonContainer = styled.div`
  padding: 20px;
`;

const SkeletonGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
  margin-top: 20px;
`;

const SkeletonHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
`;

const SkeletonHeaderLeft = styled.div`
  display: flex;
  flex-direction: column;
  gap: 10px;
`;

const SkeletonHeaderRight = styled.div`
  display: flex;
  gap: 15px;
  align-items: center;
`;

const SkeletonCard = styled.div`
  padding: 20px;
`;

const SkeletonCardContent = styled.div`
  display: flex;
  flex-direction: column;
  gap: 15px;
`;

const SkeletonActions = styled.div`
  display: flex;
  gap: 10px;
  margin-top: 15px;
`;

/**
 * Skeleton screen for boat list page
 */
export const BoatListSkeleton: React.FC = () => {
  return (
    <SkeletonContainer>
      <SkeletonHeader>
        <SkeletonHeaderLeft>
          <Skeleton width="300px" height="32px" />
          <Skeleton width="150px" height="20px" />
        </SkeletonHeaderLeft>
        <SkeletonHeaderRight>
          <Skeleton width="150px" height="40px" />
          <Skeleton width="180px" height="40px" />
        </SkeletonHeaderRight>
      </SkeletonHeader>
      
      <SkeletonGrid>
        {Array.from({ length: 6 }, (_, i) => (
          <LCARSPanel key={i}>
            <SkeletonCard>
              <SkeletonCardContent>
                <Skeleton width="200px" height="24px" />
                <Skeleton width="120px" height="16px" />
                <Skeleton width="100%" height="16px" />
                <Skeleton width="80%" height="16px" />
                <SkeletonActions>
                  <Skeleton width="100px" height="32px" />
                  <Skeleton width="100px" height="32px" />
                </SkeletonActions>
              </SkeletonCardContent>
            </SkeletonCard>
          </LCARSPanel>
        ))}
      </SkeletonGrid>
    </SkeletonContainer>
  );
};

/**
 * Skeleton screen for trip list page
 */
export const TripListSkeleton: React.FC = () => {
  return (
    <SkeletonContainer>
      <SkeletonHeader>
        <Skeleton width="250px" height="32px" />
        <SkeletonHeaderRight>
          <Skeleton width="120px" height="40px" />
          <Skeleton width="150px" height="40px" />
        </SkeletonHeaderRight>
      </SkeletonHeader>
      
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        {Array.from({ length: 8 }, (_, i) => (
          <LCARSPanel key={i}>
            <SkeletonCard>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', flex: 1 }}>
                  <Skeleton width="180px" height="20px" />
                  <Skeleton width="120px" height="16px" />
                  <Skeleton width="200px" height="16px" />
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', alignItems: 'flex-end' }}>
                  <Skeleton width="80px" height="16px" />
                  <Skeleton width="100px" height="16px" />
                  <Skeleton width="60px" height="16px" />
                </div>
              </div>
            </SkeletonCard>
          </LCARSPanel>
        ))}
      </div>
    </SkeletonContainer>
  );
};

/**
 * Skeleton screen for trip detail page
 */
export const TripDetailSkeleton: React.FC = () => {
  return (
    <SkeletonContainer>
      <SkeletonHeader>
        <Skeleton width="300px" height="32px" />
        <SkeletonActions>
          <Skeleton width="100px" height="40px" />
          <Skeleton width="120px" height="40px" />
        </SkeletonActions>
      </SkeletonHeader>
      
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '20px' }}>
        <LCARSPanel>
          <SkeletonCard>
            <Skeleton width="150px" height="20px" />
            <div style={{ marginTop: '16px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <Skeleton width="100%" height="16px" />
              <Skeleton width="80%" height="16px" />
              <Skeleton width="90%" height="16px" />
              <Skeleton width="70%" height="16px" />
            </div>
          </SkeletonCard>
        </LCARSPanel>
        
        <LCARSPanel>
          <SkeletonCard>
            <Skeleton width="120px" height="20px" />
            <div style={{ marginTop: '16px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <Skeleton width="100%" height="16px" />
              <Skeleton width="85%" height="16px" />
              <Skeleton width="75%" height="16px" />
            </div>
          </SkeletonCard>
        </LCARSPanel>
      </div>
      
      <LCARSPanel>
        <SkeletonCard>
          <Skeleton width="100px" height="20px" />
          <div style={{ marginTop: '16px', height: '300px', background: '#1a1a1a', borderRadius: '4px' }}>
            <Skeleton width="100%" height="100%" />
          </div>
        </SkeletonCard>
      </LCARSPanel>
    </SkeletonContainer>
  );
};

/**
 * Skeleton screen for dashboard page
 */
export const DashboardSkeleton: React.FC = () => {
  return (
    <SkeletonContainer>
      <div style={{ marginBottom: '20px' }}>
        <Skeleton width="200px" height="32px" />
      </div>
      
      {/* Stats cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px', marginBottom: '30px' }}>
        {Array.from({ length: 4 }, (_, i) => (
          <LCARSPanel key={i}>
            <SkeletonCard>
              <Skeleton width="120px" height="16px" />
              <div style={{ marginTop: '8px' }}>
                <Skeleton width="80px" height="24px" />
              </div>
            </SkeletonCard>
          </LCARSPanel>
        ))}
      </div>
      
      {/* Recent activity */}
      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '20px' }}>
        <LCARSPanel>
          <SkeletonCard>
            <Skeleton width="150px" height="20px" />
            <div style={{ marginTop: '16px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {Array.from({ length: 5 }, (_, i) => (
                <div key={i} style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <Skeleton width="200px" height="16px" />
                  <Skeleton width="80px" height="16px" />
                </div>
              ))}
            </div>
          </SkeletonCard>
        </LCARSPanel>
        
        <LCARSPanel>
          <SkeletonCard>
            <Skeleton width="120px" height="20px" />
            <div style={{ marginTop: '16px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {Array.from({ length: 3 }, (_, i) => (
                <Skeleton key={i} width="100%" height="16px" />
              ))}
            </div>
          </SkeletonCard>
        </LCARSPanel>
      </div>
    </SkeletonContainer>
  );
};

/**
 * Skeleton screen for maintenance list page
 */
export const MaintenanceListSkeleton: React.FC = () => {
  return (
    <SkeletonContainer>
      <SkeletonHeader>
        <Skeleton width="250px" height="32px" />
        <Skeleton width="150px" height="40px" />
      </SkeletonHeader>
      
      {/* Tab skeleton */}
      <div style={{ display: 'flex', gap: '16px', marginBottom: '20px' }}>
        {Array.from({ length: 3 }, (_, i) => (
          <Skeleton key={i} width="100px" height="40px" />
        ))}
      </div>
      
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        {Array.from({ length: 6 }, (_, i) => (
          <LCARSPanel key={i}>
            <SkeletonCard>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', flex: 1 }}>
                  <Skeleton width="220px" height="20px" />
                  <Skeleton width="150px" height="16px" />
                  <Skeleton width="180px" height="16px" />
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', alignItems: 'flex-end' }}>
                  <Skeleton width="80px" height="16px" />
                  <Skeleton width="100px" height="32px" />
                </div>
              </div>
            </SkeletonCard>
          </LCARSPanel>
        ))}
      </div>
    </SkeletonContainer>
  );
};