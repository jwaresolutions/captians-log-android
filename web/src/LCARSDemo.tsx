import React from 'react';
import styled from 'styled-components';

// Authentic LCARS Colors (based on thelcars.com reference)
const LCARSColors = {
  // Primary LCARS colors
  orange: '#FF9900',      // Primary orange
  lightOrange: '#FFCC99', // Light orange/peach
  blue: '#9999FF',        // LCARS blue
  lightBlue: '#CCCCFF',   // Light blue
  purple: '#CC99CC',      // LCARS purple/magenta
  red: '#FF6666',         // Alert red
  yellow: '#FFFF99',      // LCARS yellow
  
  // Background and text
  black: '#000000',       // True black background
  darkGray: '#333333',    // Dark panels
  white: '#FFFFFF',       // White text
  
  // Specific UI colors
  frameOrange: '#FF9966', // Frame elements
  buttonOrange: '#FFAA00', // Interactive elements
  textOrange: '#FFCC66',  // Text highlights
};

// Main LCARS Container
const LCARSContainer = styled.div`
  background-color: ${LCARSColors.black};
  min-height: 100vh;
  padding: 0;
  font-family: 'Courier New', monospace;
  color: ${LCARSColors.lightOrange};
  overflow: hidden;
`;

// LCARS Frame Structure (like thelcars.com)
const LCARSFrame = styled.div`
  display: grid;
  grid-template-columns: 200px 1fr 200px;
  grid-template-rows: 80px 1fr 80px;
  height: 100vh;
  gap: 8px;
  padding: 8px;
`;

// Top Header Bar
const TopBar = styled.div`
  grid-column: 1 / -1;
  background-color: ${LCARSColors.frameOrange};
  border-radius: 0 0 40px 40px;
  display: flex;
  align-items: center;
  padding: 0 40px;
  
  h1 {
    color: ${LCARSColors.black};
    font-size: 2rem;
    font-weight: bold;
    text-transform: uppercase;
    letter-spacing: 3px;
    margin: 0;
  }
`;

// Left Sidebar with characteristic LCARS buttons
const LeftSidebar = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

// Right Sidebar
const RightSidebar = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

// Bottom Bar
const BottomBar = styled.div`
  grid-column: 1 / -1;
  background-color: ${LCARSColors.frameOrange};
  border-radius: 40px 40px 0 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
  
  .status {
    color: ${LCARSColors.black};
    font-weight: bold;
    text-transform: uppercase;
  }
`;

// Main Content Area
const MainContent = styled.div`
  background-color: ${LCARSColors.black};
  padding: 20px;
  overflow-y: auto;
  border: 2px solid ${LCARSColors.frameOrange};
  border-radius: 20px;
`;

// Authentic LCARS Button (tall, rounded, with proper proportions)
const LCARSButton = styled.button<{ 
  variant?: 'primary' | 'secondary' | 'alert' | 'inactive';
  tall?: boolean;
}>`
  background-color: ${props => {
    switch(props.variant) {
      case 'primary': return LCARSColors.orange;
      case 'secondary': return LCARSColors.blue;
      case 'alert': return LCARSColors.red;
      case 'inactive': return LCARSColors.darkGray;
      default: return LCARSColors.orange;
    }
  }};
  
  color: ${LCARSColors.black};
  border: none;
  border-radius: ${props => props.tall ? '30px' : '20px'};
  height: ${props => props.tall ? '60px' : '40px'};
  width: 100%;
  
  font-family: 'Courier New', monospace;
  font-size: ${props => props.tall ? '1.1rem' : '0.9rem'};
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1px;
  
  cursor: pointer;
  transition: all 0.2s ease;
  
  display: flex;
  align-items: center;
  justify-content: center;
  
  &:hover {
    filter: brightness(1.2);
    transform: translateY(-1px);
  }
  
  &:active {
    filter: brightness(0.9);
    transform: translateY(0);
  }
  
  &:disabled {
    opacity: 0.3;
    cursor: not-allowed;
  }
`;

// LCARS Panel (like the data displays on thelcars.com)
const LCARSPanel = styled.div<{ variant?: 'primary' | 'secondary' | 'alert' }>`
  background-color: ${props => {
    switch(props.variant) {
      case 'primary': return LCARSColors.lightOrange;
      case 'secondary': return LCARSColors.lightBlue;
      case 'alert': return LCARSColors.red;
      default: return LCARSColors.lightOrange;
    }
  }};
  
  color: ${LCARSColors.black};
  border-radius: 15px;
  padding: 20px;
  margin: 10px 0;
  
  h3 {
    margin: 0 0 15px 0;
    font-size: 1.2rem;
    font-weight: bold;
    text-transform: uppercase;
    letter-spacing: 2px;
  }
  
  .data-row {
    display: flex;
    justify-content: space-between;
    margin: 8px 0;
    font-family: 'Courier New', monospace;
    
    .label {
      font-weight: bold;
    }
    
    .value {
      font-family: 'Courier New', monospace;
    }
  }
`;

// LCARS Elbow (corner piece like on thelcars.com)
const LCARSElbow = styled.div<{ 
  position: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right';
  color?: string;
}>`
  width: 100%;
  height: 60px;
  background-color: ${props => props.color || LCARSColors.orange};
  
  ${props => {
    switch(props.position) {
      case 'top-left':
        return 'border-radius: 0 0 30px 0;';
      case 'top-right':
        return 'border-radius: 0 0 0 30px;';
      case 'bottom-left':
        return 'border-radius: 0 30px 0 0;';
      case 'bottom-right':
        return 'border-radius: 30px 0 0 0;';
      default:
        return 'border-radius: 0 0 30px 0;';
    }
  }}
`;

// LCARS Bar (horizontal divider)
const LCARSBar = styled.div<{ color?: string; height?: number }>`
  width: 100%;
  height: ${props => props.height || 20}px;
  background-color: ${props => props.color || LCARSColors.orange};
  border-radius: 10px;
  margin: 5px 0;
`;

// Data Display Grid (like the technical readouts)
const DataGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin: 20px 0;
`;

// Status Indicator
const StatusIndicator = styled.div<{ status: 'online' | 'offline' | 'warning' | 'error' }>`
  display: inline-flex;
  align-items: center;
  gap: 8px;
  
  &::before {
    content: '';
    width: 12px;
    height: 12px;
    border-radius: 50%;
    background-color: ${props => {
      switch(props.status) {
        case 'online': return '#00FF00';
        case 'offline': return '#666666';
        case 'warning': return '#FFFF00';
        case 'error': return '#FF0000';
        default: return '#666666';
      }
    }};
    animation: ${props => props.status === 'online' ? 'pulse 2s infinite' : 'none'};
  }
  
  @keyframes pulse {
    0% { opacity: 1; }
    50% { opacity: 0.5; }
    100% { opacity: 1; }
  }
`;

const LCARSDemo: React.FC = () => {
  return (
    <LCARSContainer>
      <LCARSFrame>
        {/* Top Header */}
        <TopBar>
          <h1>Captain's Log</h1>
          <div style={{ marginLeft: 'auto', display: 'flex', gap: '20px', alignItems: 'center' }}>
            <StatusIndicator status="online">ONLINE</StatusIndicator>
            <span style={{ color: LCARSColors.black, fontWeight: 'bold' }}>STARDATE 78945.2</span>
          </div>
        </TopBar>

        {/* Left Sidebar */}
        <LeftSidebar>
          <LCARSElbow position="top-left" />
          <LCARSButton variant="primary" tall onClick={() => alert('Navigation')}>
            NAV
          </LCARSButton>
          <LCARSButton variant="primary" tall onClick={() => alert('Trips')}>
            TRIPS
          </LCARSButton>
          <LCARSButton variant="secondary" tall onClick={() => alert('Boats')}>
            BOATS
          </LCARSButton>
          <LCARSButton variant="secondary" tall onClick={() => alert('Maintenance')}>
            MAINT
          </LCARSButton>
          <LCARSButton variant="primary" tall onClick={() => alert('Reports')}>
            REPORTS
          </LCARSButton>
          <LCARSBar color={LCARSColors.blue} height={30} />
          <LCARSButton variant="alert" onClick={() => alert('Emergency')}>
            EMERGENCY
          </LCARSButton>
          <LCARSElbow position="bottom-left" />
        </LeftSidebar>

        {/* Main Content */}
        <MainContent>
          <div style={{ marginBottom: '30px' }}>
            <h2 style={{ 
              color: LCARSColors.orange, 
              fontSize: '1.8rem', 
              textTransform: 'uppercase',
              letterSpacing: '2px',
              marginBottom: '20px'
            }}>
              Authentic LCARS Interface Demo
            </h2>
            
            <p style={{ color: LCARSColors.lightOrange, lineHeight: '1.6', marginBottom: '20px' }}>
              This implementation is inspired by the authentic LCARS interface from Star Trek TNG/DS9, 
              following the design patterns seen on thelcars.com with proper proportions, colors, and layout.
            </p>
          </div>

          <LCARSBar color={LCARSColors.orange} height={8} />

          <DataGrid>
            <LCARSPanel variant="primary">
              <h3>Current Mission Status</h3>
              <div className="data-row">
                <span className="label">Active Vessels:</span>
                <span className="value">3</span>
              </div>
              <div className="data-row">
                <span className="label">Trips Today:</span>
                <span className="value">7</span>
              </div>
              <div className="data-row">
                <span className="label">Sea Time Hours:</span>
                <span className="value">1,247.5</span>
              </div>
              <div className="data-row">
                <span className="label">Last Sync:</span>
                <span className="value">14:32:15</span>
              </div>
            </LCARSPanel>

            <LCARSPanel variant="secondary">
              <h3>Captain's License Progress</h3>
              <div className="data-row">
                <span className="label">Sea Time Days:</span>
                <span className="value">127 / 360</span>
              </div>
              <div className="data-row">
                <span className="label">90-Day Period:</span>
                <span className="value">23 / 90</span>
              </div>
              <div className="data-row">
                <span className="label">Progress:</span>
                <span className="value">35.3%</span>
              </div>
              <div className="data-row">
                <span className="label">Est. Complete:</span>
                <span className="value">18 MONTHS</span>
              </div>
            </LCARSPanel>
          </DataGrid>

          <LCARSBar color={LCARSColors.blue} height={8} />

          <DataGrid>
            <LCARSPanel variant="primary">
              <h3>Recent Activity Log</h3>
              <div className="data-row">
                <span className="label">14:25</span>
                <span className="value">TRIP #1247 COMPLETED</span>
              </div>
              <div className="data-row">
                <span className="label">13:45</span>
                <span className="value">GPS TRACKING ACTIVE</span>
              </div>
              <div className="data-row">
                <span className="label">12:30</span>
                <span className="value">MAINTENANCE LOGGED</span>
              </div>
              <div className="data-row">
                <span className="label">11:15</span>
                <span className="value">SYNC COMPLETED</span>
              </div>
            </LCARSPanel>

            <LCARSPanel variant="alert">
              <h3>System Alerts</h3>
              <div className="data-row">
                <span className="label">ENGINE OIL:</span>
                <span className="value">DUE IN 3 DAYS</span>
              </div>
              <div className="data-row">
                <span className="label">HULL CLEAN:</span>
                <span className="value">DUE IN 12 DAYS</span>
              </div>
              <div className="data-row">
                <span className="label">SAFETY CHECK:</span>
                <span className="value">DUE IN 28 DAYS</span>
              </div>
            </LCARSPanel>
          </DataGrid>

          <div style={{ marginTop: '30px', textAlign: 'center' }}>
            <p style={{ color: LCARSColors.textOrange, fontSize: '1.1rem', fontWeight: 'bold' }}>
              This is what proper LCARS should look like - authentic TNG/DS9 styling with 
              characteristic curved corners, proper proportions, and the classic orange/blue color scheme.
            </p>
          </div>
        </MainContent>

        {/* Right Sidebar */}
        <RightSidebar>
          <LCARSElbow position="top-right" color={LCARSColors.blue} />
          <LCARSBar color={LCARSColors.blue} height={40} />
          <LCARSBar color={LCARSColors.orange} height={25} />
          <LCARSBar color={LCARSColors.blue} height={40} />
          <LCARSBar color={LCARSColors.purple} height={30} />
          <LCARSBar color={LCARSColors.orange} height={35} />
          <LCARSBar color={LCARSColors.blue} height={25} />
          <LCARSButton variant="inactive">
            OFFLINE
          </LCARSButton>
          <LCARSElbow position="bottom-right" color={LCARSColors.blue} />
        </RightSidebar>

        {/* Bottom Bar */}
        <BottomBar>
          <div className="status">
            <StatusIndicator status="online">All Systems Operational</StatusIndicator>
          </div>
          <div className="status">
            LCARS Interface v2.4.1
          </div>
          <LCARSButton 
            variant="primary" 
            onClick={() => window.history.back()}
            style={{ width: '150px', height: '40px' }}
          >
            Return
          </LCARSButton>
        </BottomBar>
      </LCARSFrame>
    </LCARSContainer>
  );
};

export default LCARSDemo;