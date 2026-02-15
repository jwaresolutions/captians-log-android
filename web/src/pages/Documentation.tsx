import React, { useState } from 'react'
import styled from 'styled-components'
import { LCARSPanel } from '../components/lcars/LCARSPanel'
import { LCARSHeader } from '../components/lcars/LCARSHeader'

const DocsContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.lg};
  max-width: 1200px;
  margin: 0 auto;
`

const SectionGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: ${props => props.theme.spacing.lg};

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`

const FeatureList = styled.ul`
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: ${props => props.theme.spacing.sm};
`

const FeatureItem = styled.li`
  color: ${props => props.theme.colors.text.primary};
  font-size: ${props => props.theme.typography.fontSize.sm};
  line-height: 1.6;
  padding-left: 1.5rem;
  position: relative;

  &::before {
    content: '●';
    position: absolute;
    left: 0;
    color: ${props => props.theme.colors.primary.neonCarrot};
  }
`

const Description = styled.p`
  color: ${props => props.theme.colors.text.secondary};
  font-size: ${props => props.theme.typography.fontSize.sm};
  line-height: 1.8;
  margin: 0 0 ${props => props.theme.spacing.md} 0;
`

const SubHeader = styled.h4`
  color: ${props => props.theme.colors.primary.anakiwa};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: ${props => props.theme.typography.fontSize.md};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
  margin: ${props => props.theme.spacing.md} 0 ${props => props.theme.spacing.sm} 0;
`

const StatusBadge = styled.span<{ $status: 'working' | 'limited' | 'untested' | 'removed' }>`
  display: inline-block;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: ${props => props.theme.typography.fontSize.xs};
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-left: 8px;
  ${props => {
    switch (props.$status) {
      case 'working': return `background: ${props.theme.colors.status.success}30; color: ${props.theme.colors.status.success}; border: 1px solid ${props.theme.colors.status.success};`
      case 'limited': return `background: ${props.theme.colors.status.warning}30; color: ${props.theme.colors.status.warning}; border: 1px solid ${props.theme.colors.status.warning};`
      case 'untested': return `background: ${props.theme.colors.primary.anakiwa}30; color: ${props.theme.colors.primary.anakiwa}; border: 1px solid ${props.theme.colors.primary.anakiwa};`
      case 'removed': return `background: ${props.theme.colors.status.error}30; color: ${props.theme.colors.status.error}; border: 1px solid ${props.theme.colors.status.error};`
    }
  }}
`

const TabBar = styled.div`
  display: flex;
  gap: ${props => props.theme.spacing.sm};
  flex-wrap: wrap;
  margin-bottom: ${props => props.theme.spacing.md};
`

const Tab = styled.button<{ $active: boolean }>`
  background: ${props => props.$active ? props.theme.colors.primary.neonCarrot : props.theme.colors.surface.dark};
  color: ${props => props.$active ? props.theme.colors.text.inverse : props.theme.colors.primary.neonCarrot};
  border: 2px solid ${props => props.theme.colors.primary.neonCarrot};
  padding: ${props => props.theme.spacing.sm} ${props => props.theme.spacing.md};
  font-family: ${props => props.theme.typography.fontFamily.primary};
  font-size: ${props => props.theme.typography.fontSize.sm};
  font-weight: ${props => props.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: 1px;
  border-radius: 0 16px 16px 0;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    filter: brightness(1.2);
  }
`

type DocTab = 'overview' | 'trips' | 'navigation' | 'nautical' | 'maintenance' | 'other'

export const Documentation: React.FC = () => {
  const [activeTab, setActiveTab] = useState<DocTab>('overview')

  return (
    <DocsContainer>
      <LCARSHeader level={2} color="neonCarrot" withBar barColor="tanoi">
        Ship&apos;s Computer — Technical Manual
      </LCARSHeader>

      <TabBar>
        <Tab $active={activeTab === 'overview'} onClick={() => setActiveTab('overview')}>Overview</Tab>
        <Tab $active={activeTab === 'trips'} onClick={() => setActiveTab('trips')}>Trips & GPS</Tab>
        <Tab $active={activeTab === 'navigation'} onClick={() => setActiveTab('navigation')}>Navigation</Tab>
        <Tab $active={activeTab === 'nautical'} onClick={() => setActiveTab('nautical')}>Nautical Data</Tab>
        <Tab $active={activeTab === 'maintenance'} onClick={() => setActiveTab('maintenance')}>Maintenance</Tab>
        <Tab $active={activeTab === 'other'} onClick={() => setActiveTab('other')}>Other Features</Tab>
      </TabBar>

      {activeTab === 'overview' && (
        <>
          <LCARSPanel title="System Overview" variant="primary">
            <Description>
              Captain&apos;s Log is a comprehensive boat tracking and management application. It provides GPS trip recording,
              nautical chart overlays, marine weather data, vessel maintenance tracking, and more. The system operates
              with dual connection modes — local network and remote via secure tunnel — with automatic failover.
            </Description>
            <SectionGrid>
              <div>
                <SubHeader>Core Systems</SubHeader>
                <FeatureList>
                  <FeatureItem>GPS trip recording with foreground service</FeatureItem>
                  <FeatureItem>Interactive map with nautical chart overlays</FeatureItem>
                  <FeatureItem>Marine weather and tide data</FeatureItem>
                  <FeatureItem>Vessel maintenance scheduling and tracking</FeatureItem>
                  <FeatureItem>Captain&apos;s notes and ship&apos;s log</FeatureItem>
                  <FeatureItem>Photo gallery with geo-tagging</FeatureItem>
                </FeatureList>
              </div>
              <div>
                <SubHeader>Support Systems</SubHeader>
                <FeatureList>
                  <FeatureItem>Dual connection mode (local + remote)</FeatureItem>
                  <FeatureItem>Offline data storage with automatic sync</FeatureItem>
                  <FeatureItem>TLS certificate pinning for security</FeatureItem>
                  <FeatureItem>To-do list management</FeatureItem>
                  <FeatureItem>Calendar integration</FeatureItem>
                  <FeatureItem>Data backup and restore</FeatureItem>
                </FeatureList>
              </div>
            </SectionGrid>
          </LCARSPanel>
        </>
      )}

      {activeTab === 'trips' && (
        <>
          <LCARSPanel title="Trip Recording" variant="primary">
            <Description>
              Record your voyages with continuous GPS tracking. The app runs a foreground service that captures your
              position at configurable intervals (default: every 5 seconds) and plots your route on the map.
            </Description>
            <SubHeader>How to Record a Trip</SubHeader>
            <FeatureList>
              <FeatureItem>Navigate to Trip Log and tap &quot;New Trip&quot;</FeatureItem>
              <FeatureItem>Enter trip details (name, vessel, type)</FeatureItem>
              <FeatureItem>GPS tracking starts automatically via foreground service</FeatureItem>
              <FeatureItem>A persistent notification shows tracking status</FeatureItem>
              <FeatureItem>End the trip when you arrive — route is saved and displayed on the map</FeatureItem>
            </FeatureList>
            <SubHeader>Stop Point Detection</SubHeader>
            <Description>
              The system automatically detects when you&apos;ve stopped by monitoring if you remain within a 45-foot
              radius for more than 5 minutes. Stop points are marked on your trip route.
            </Description>
          </LCARSPanel>

          <LCARSPanel title="Marked Locations" variant="secondary">
            <Description>
              Save important locations on the map — fishing spots, marinas, anchorages, hazards, or custom waypoints.
              Each location can include a name, category, notes, and tags.
            </Description>
            <SubHeader>How to Mark a Location</SubHeader>
            <FeatureList>
              <FeatureItem>Open the Map view</FeatureItem>
              <FeatureItem>Tap the &quot;+&quot; button while at your current location</FeatureItem>
              <FeatureItem>Enter name, select category (fishing, marina, anchorage, hazard, other)</FeatureItem>
              <FeatureItem>Add optional notes and tags</FeatureItem>
              <FeatureItem>Locations appear as category-specific icons on the map</FeatureItem>
            </FeatureList>
          </LCARSPanel>
        </>
      )}

      {activeTab === 'navigation' && (
        <>
          <LCARSPanel title="Map & Navigation" variant="accent">
            <Description>
              The interactive map supports multiple base maps, overlay layers, and data sources.
              Use the layer controls (top-left button) to switch between map modes and toggle data visibility.
            </Description>
            <SubHeader>Base Maps</SubHeader>
            <FeatureList>
              <FeatureItem><strong>Standard (OSM)</strong> — Default OpenStreetMap tiles, always available</FeatureItem>
              <FeatureItem><strong>NOAA Charts</strong> — Official US nautical charts with depth soundings, hazards, and navigation aids. Enable in Settings → Nautical Providers. US coastal waters only.</FeatureItem>
              <FeatureItem><strong>GEBCO Bathymetry</strong> — Global ocean depth visualization. Slow initial load but tiles are cached. Best at zoom levels 3-12.</FeatureItem>
            </FeatureList>
            <SubHeader>Overlays</SubHeader>
            <Description>
              Overlays render on top of the standard base map. They are hidden when using NOAA Charts or GEBCO as the base map.
            </Description>
            <FeatureList>
              <FeatureItem><strong>OpenSeaMap</strong> — Nautical marks, buoys, lights, and seamark symbols. Community-maintained with global coverage. See openseamap.org/legend for symbol meanings.</FeatureItem>
            </FeatureList>
            <SubHeader>Map Controls</SubHeader>
            <FeatureList>
              <FeatureItem><strong>Layer button</strong> (top-left) — Opens the control panel</FeatureItem>
              <FeatureItem><strong>Base Map</strong> — Switch between Standard, NOAA Charts, and GEBCO</FeatureItem>
              <FeatureItem><strong>Overlays</strong> — Toggle tile overlays on/off (Standard base map only)</FeatureItem>
              <FeatureItem><strong>Nautical Data</strong> — Toggle data providers (tides, weather, alerts, etc.)</FeatureItem>
              <FeatureItem><strong>Map Data</strong> — Show/hide trips and marked locations</FeatureItem>
              <FeatureItem><strong>Refresh</strong> — Reload all nautical data for current viewport</FeatureItem>
            </FeatureList>
          </LCARSPanel>
        </>
      )}

      {activeTab === 'nautical' && (
        <>
          <LCARSPanel title="Free Nautical Providers" variant="primary">
            <Description>
              Configure nautical data providers in Settings → Nautical Providers. Each provider can be independently
              enabled/disabled. NOAA providers are grouped together. Once enabled, toggle visibility on the map
              using the layer controls.
            </Description>

            <SubHeader>OpenSeaMap <StatusBadge $status="working">Working</StatusBadge></SubHeader>
            <Description>
              Nautical marks, buoys, lights, and seamark overlays on OpenStreetMap. Community-maintained with global
              coverage. Appears as a tile overlay on the standard base map. Visit openseamap.org/legend to understand
              the symbols.
            </Description>

            <SubHeader>NOAA Charts <StatusBadge $status="working">Working</StatusBadge></SubHeader>
            <Description>
              Official US coastal nautical charts from NOAA showing depths, hazards, channels, and aids to navigation.
              Selectable as a base map. Initial load is slow but tiles are cached for subsequent use. Tiles around
              your GPS location are preloaded on app startup.
            </Description>

            <SubHeader>GEBCO Bathymetry <StatusBadge $status="working">Working</StatusBadge></SubHeader>
            <Description>
              Global bathymetry and ocean depth visualization via WMS. Selectable as a base map. Very slow initial
              load (WMS server limitation) but tiles are cached after first view. Best viewed at zoom levels 3-12;
              becomes pixelated beyond zoom 12.
            </Description>

            <SubHeader>NOAA CO-OPS <StatusBadge $status="working">Working</StatusBadge></SubHeader>
            <Description>
              Real-time and predicted tide data from US stations. When enabled, tide station markers appear on the map
              (clock icons). Tap a station marker to see the station name and current tide prediction. Data covers
              US coastal waters. Best viewed around Chesapeake Bay, San Francisco Bay, or other major US ports.
            </Description>

            <SubHeader>NOAA Weather Alerts <StatusBadge $status="working">Working</StatusBadge></SubHeader>
            <Description>
              Active marine weather alerts from the National Weather Service. When alerts exist at your map location,
              colored zone polygons appear on the map (red for extreme/severe, orange for moderate, yellow for advisory).
              A dismissible banner card shows alert details at the top of the screen. Warning markers are placed at
              the center of each alert zone for easy discovery while browsing. Background polling every 15 minutes
              sends push notifications for new alerts.
            </Description>

            <SubHeader>Open-Meteo Marine <StatusBadge $status="working">Working</StatusBadge></SubHeader>
            <Description>
              Marine weather forecasts including wave height, swell, wind speed, and temperature. When enabled, a
              crosshair appears at the map center showing where data is sampled, and a weather card in the bottom-left
              corner displays current conditions. Global coverage, no API key required.
            </Description>

            <SubHeader>Open-Meteo Ocean <StatusBadge $status="limited">Limited</StatusBadge></SubHeader>
            <Description>
              Ocean current velocity, direction, and sea surface temperature data. The API is functional but returns
              null data for most locations. When data is available, it appears in the weather card. Coverage may
              improve as Open-Meteo expands their marine dataset.
            </Description>

            <SubHeader>AISstream <StatusBadge $status="untested">Not Tested</StatusBadge></SubHeader>
            <Description>
              Real-time AIS vessel tracking via WebSocket. Requires a free API key from aisstream.io. When enabled,
              vessel positions appear as arrow markers on the map showing heading and speed. Service has been reported
              as intermittently unavailable.
            </Description>
          </LCARSPanel>

          <LCARSPanel title="Paid Nautical Providers" variant="info">
            <Description>
              The following providers require paid API keys and have not been tested in the current release.
            </Description>
            <FeatureList>
              <FeatureItem><strong>WorldTides</strong> <StatusBadge $status="untested">Untested</StatusBadge> — Global tide predictions. $10 for 5,000 predictions.</FeatureItem>
              <FeatureItem><strong>Stormglass</strong> <StatusBadge $status="untested">Untested</StatusBadge> — Premium marine weather from multiple sources. Free tier: 10 req/day, paid from $19/month.</FeatureItem>
              <FeatureItem><strong>Windy</strong> <StatusBadge $status="untested">Untested</StatusBadge> — Animated wind/wave/weather overlays. ~$720/year.</FeatureItem>
              <FeatureItem><strong>Navionics/Garmin</strong> <StatusBadge $status="untested">Untested</StatusBadge> — Premium nautical charts with detailed depth data. Contact for pricing.</FeatureItem>
              <FeatureItem><strong>MarineTraffic</strong> <StatusBadge $status="untested">Untested</StatusBadge> — Global vessel tracking with satellite AIS. Credit-based pricing.</FeatureItem>
            </FeatureList>
          </LCARSPanel>

          <LCARSPanel title="Removed Providers" variant="secondary">
            <FeatureList>
              <FeatureItem><strong>USCG NAVCEN / Wrecks & Obstructions</strong> <StatusBadge $status="removed">Removed</StatusBadge> — USCG data source URL returned 404. Replaced with NOAA AWOIS which only had 359 records concentrated off the Carolina coast. Too sparse to be useful.</FeatureItem>
              <FeatureItem><strong>OpenSeaMap Depth</strong> <StatusBadge $status="removed">Removed</StatusBadge> — Depth sounding tile overlay. Tile server returns empty transparent images globally. Service is effectively dead.</FeatureItem>
            </FeatureList>
          </LCARSPanel>
        </>
      )}

      {activeTab === 'maintenance' && (
        <>
          <LCARSPanel title="Maintenance Tracking" variant="primary">
            <Description>
              Track vessel maintenance with templates and scheduled events. Set up recurring maintenance items
              and get notified when service is due.
            </Description>
            <SubHeader>Maintenance Templates</SubHeader>
            <FeatureList>
              <FeatureItem>Create templates for recurring maintenance (oil changes, hull cleaning, etc.)</FeatureItem>
              <FeatureItem>Set intervals (by time or engine hours)</FeatureItem>
              <FeatureItem>Track costs and parts</FeatureItem>
              <FeatureItem>View maintenance history and reports</FeatureItem>
            </FeatureList>
            <SubHeader>Maintenance Events</SubHeader>
            <FeatureList>
              <FeatureItem>Log completed maintenance with date, cost, and notes</FeatureItem>
              <FeatureItem>Attach photos to maintenance records</FeatureItem>
              <FeatureItem>View upcoming and overdue maintenance</FeatureItem>
              <FeatureItem>Generate maintenance cost reports</FeatureItem>
            </FeatureList>
          </LCARSPanel>
        </>
      )}

      {activeTab === 'other' && (
        <>
          <SectionGrid>
            <LCARSPanel title="Captain's Notes" variant="accent">
              <Description>
                Keep a ship&apos;s log with rich text notes. Notes sync across devices and can be searched, filtered,
                and organized.
              </Description>
              <FeatureList>
                <FeatureItem>Create and edit notes with the built-in editor</FeatureItem>
                <FeatureItem>Search notes by content</FeatureItem>
                <FeatureItem>Notes sync automatically when online</FeatureItem>
              </FeatureList>
            </LCARSPanel>

            <LCARSPanel title="To-Do Lists" variant="secondary">
              <Description>
                Manage boat-related tasks with to-do lists. Track completion and organize by priority.
              </Description>
              <FeatureList>
                <FeatureItem>Create tasks with descriptions</FeatureItem>
                <FeatureItem>Mark tasks complete</FeatureItem>
                <FeatureItem>Filter by status</FeatureItem>
              </FeatureList>
            </LCARSPanel>

            <LCARSPanel title="Photo Gallery" variant="primary">
              <Description>
                Capture and organize photos from your voyages. Photos are geo-tagged and can be associated with
                trips.
              </Description>
              <FeatureList>
                <FeatureItem>Take photos directly from the app</FeatureItem>
                <FeatureItem>Automatic geo-tagging with GPS coordinates</FeatureItem>
                <FeatureItem>WiFi-only uploads to save mobile data</FeatureItem>
                <FeatureItem>7-day local retention after upload</FeatureItem>
              </FeatureList>
            </LCARSPanel>

            <LCARSPanel title="Reports & Calendar" variant="info">
              <Description>
                View trip statistics, maintenance reports, and license progress. The calendar view shows
                upcoming maintenance and past trips.
              </Description>
              <FeatureList>
                <FeatureItem>Trip summary and statistics</FeatureItem>
                <FeatureItem>Maintenance cost analysis</FeatureItem>
                <FeatureItem>License/certification progress tracking</FeatureItem>
                <FeatureItem>Calendar view of events and maintenance</FeatureItem>
              </FeatureList>
            </LCARSPanel>

            <LCARSPanel title="Backup & Restore" variant="accent">
              <Description>
                Back up your data and restore from previous backups. Ensures you never lose your ship&apos;s records.
              </Description>
              <FeatureList>
                <FeatureItem>Manual and automatic backups</FeatureItem>
                <FeatureItem>Restore from any backup point</FeatureItem>
                <FeatureItem>Export data for external use</FeatureItem>
              </FeatureList>
            </LCARSPanel>

            <LCARSPanel title="Security" variant="secondary">
              <Description>
                The app uses multiple layers of security to protect your data in transit and at rest.
              </Description>
              <FeatureList>
                <FeatureItem>TLS certificate pinning (SHA-256)</FeatureItem>
                <FeatureItem>Encrypted local storage (EncryptedSharedPreferences)</FeatureItem>
                <FeatureItem>API key authentication</FeatureItem>
                <FeatureItem>HTTPS-only communication</FeatureItem>
              </FeatureList>
            </LCARSPanel>
          </SectionGrid>
        </>
      )}
    </DocsContainer>
  )
}
