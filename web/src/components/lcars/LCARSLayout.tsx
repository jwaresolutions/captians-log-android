import React, { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import styled, { css, keyframes } from 'styled-components'
import { OfflineIndicator } from '../OfflineIndicator'
import { NotificationPanel } from '../NotificationPanel'

// ---------------------------------------------------------------------------
// LCARS 2357 — Authentic TNG-era Library Computer Access/Retrieval System
// Layout: left sidebar with elbows connecting to horizontal header/footer bars
// ---------------------------------------------------------------------------

const SIDEBAR_W = '200px'
const ELBOW = '60px'
const HEADER_H = '60px'
const FOOTER_H = '40px'
const GAP = '3px'
const BTN_H = '44px'
const MOBILE_BP = '768px'

// -- Animations ------------------------------------------------------------

const fadeIn = keyframes`
  from { opacity: 0; }
  to   { opacity: 1; }
`

// -- Root container --------------------------------------------------------

const LayoutContainer = styled.div`
  min-height: 100vh;
  display: grid;
  background: ${p => p.theme.colors.background};
  grid-template-columns: ${SIDEBAR_W} 1fr;
  grid-template-rows: ${HEADER_H} 1fr ${FOOTER_H};
  grid-template-areas:
    "sidebar header"
    "sidebar content"
    "sidebar footer";
  gap: 0;
  overflow-x: hidden;
  animation: ${fadeIn} 0.6s ease;

  @media (max-width: ${MOBILE_BP}) {
    grid-template-columns: 1fr;
    grid-template-rows: ${HEADER_H} 1fr ${FOOTER_H};
    grid-template-areas:
      "header"
      "content"
      "footer";
  }
`

// -- Sidebar ---------------------------------------------------------------
// The sidebar is a vertical strip composed of: top elbow, nav buttons, bottom elbow.
// There is no background — the colored blocks ARE the sidebar.

const Sidebar = styled.aside`
  grid-area: sidebar;
  display: flex;
  flex-direction: column;
  gap: ${GAP};
  overflow-y: auto;
  overflow-x: hidden;
  animation: ${fadeIn} 0.4s ease;

  @media (max-width: ${MOBILE_BP}) {
    display: none;
  }
`

// -- Elbow pieces ----------------------------------------------------------
// An elbow is an L-shaped connector. It's a colored rectangle with a
// quarter-circle cutout (achieved via an inner pseudo-element with
// border-radius and background: black).

const TopElbow = styled.div`
  width: ${SIDEBAR_W};
  height: ${ELBOW};
  background: ${p => p.theme.colors.primary.tanoi};
  position: relative;
  flex-shrink: 0;
  border-radius: 32px 0 0 0;
`

const BottomElbow = styled.div`
  width: ${SIDEBAR_W};
  height: ${FOOTER_H};
  background: ${p => p.theme.colors.primary.lilac};
  position: relative;
  flex-shrink: 0;
  border-radius: 0 0 0 32px;
  margin-top: auto;
`

// -- Sidebar nav buttons ---------------------------------------------------
// Pill-shaped: flat left edge, rounded right edge.

const NAV_COLORS = [
  'tanoi',
  'anakiwa',
  'lilac',
  'goldenTanoi',
  'neonCarrot',
  'mariner',
  'anakiwa',
  'lilac',
  'tanoi',
  'neonCarrot',
  'goldenTanoi',
  'mariner',
] as const

const SidebarButton = styled.button<{ $color: string; $isActive: boolean }>`
  width: 100%;
  height: ${BTN_H};
  flex-shrink: 0;
  border: none;
  cursor: pointer;
  background: ${p => p.$color};
  color: ${p => p.theme.colors.text.inverse};
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: ${p => p.theme.typography.fontSize.md};
  font-weight: ${p => p.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.wide};
  text-align: right;
  padding: 0 18px 0 0;
  border-radius: 0 24px 24px 0;
  position: relative;
  overflow: hidden;
  z-index: 0;

  /* Left-to-right sweep hover effect */
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.25);
    transform: translateX(-100%);
    transition: transform 0.35s ease;
    z-index: 0;
    border-radius: inherit;
  }

  &:hover:not(:disabled)::after {
    transform: translateX(0);
  }

  &:active:not(:disabled)::after {
    background: rgba(255, 255, 255, 0.35);
  }

  ${p => p.$isActive && css`
    filter: brightness(1.35);
    box-shadow: 0 0 12px currentColor, inset 0 0 8px rgba(255,255,255,0.15);

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 4px;
      bottom: 4px;
      width: 4px;
      background: #fff;
      border-radius: 0 2px 2px 0;
      z-index: 1;
    }
  `}
`

// Thin filler bars between buttons (decorative)
const FillerBar = styled.div<{ $color: string }>`
  width: 60%;
  height: 3px;
  background: ${p => p.$color};
  border-radius: 0 2px 2px 0;
  flex-shrink: 0;
  opacity: 0.6;
`

// -- Header bar ------------------------------------------------------------
// Horizontal bar spanning from after the elbow to the right edge.

const HeaderBar = styled.header`
  grid-area: header;
  background: ${p => p.theme.colors.primary.tanoi};
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 24px 0 16px;
  position: relative;

  @media (max-width: ${MOBILE_BP}) {
    border-radius: 0;
    justify-content: center;
  }

  @media (max-width: 480px) {
    padding: 0 8px;
  }
`

const HeaderTitle = styled.h1`
  color: ${p => p.theme.colors.text.inverse};
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: ${p => p.theme.typography.fontSize.xl};
  font-weight: ${p => p.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.extraWide};
  margin: 0;
  cursor: pointer;
  user-select: none;
  transition: opacity 0.2s;

  &:hover { opacity: 0.8; }

  @media (max-width: ${MOBILE_BP}) {
    font-size: ${p => p.theme.typography.fontSize.lg};
    letter-spacing: ${p => p.theme.typography.letterSpacing.wide};
  }

  @media (max-width: 480px) {
    font-size: ${p => p.theme.typography.fontSize.md};
    letter-spacing: ${p => p.theme.typography.letterSpacing.normal};
  }
`

const HeaderLogo = styled.img`
  height: 40px;
  width: auto;
  cursor: pointer;
  margin-right: 12px;
  filter: drop-shadow(0 0 6px rgba(255, 153, 51, 0.4));
  transition: filter 0.2s;

  &:hover {
    filter: drop-shadow(0 0 10px rgba(255, 153, 51, 0.7));
  }

  @media (max-width: ${MOBILE_BP}) {
    height: 32px;
  }

  @media (max-width: 480px) {
    display: none;
  }
`

const HeaderStardate = styled.span`
  color: ${p => p.theme.colors.text.inverse};
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: ${p => p.theme.typography.fontSize.sm};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.wide};
  margin-right: auto;
  padding-left: 40px;
  opacity: 0.75;

  @media (max-width: ${MOBILE_BP}) {
    display: none;
  }
`

// -- Content area ----------------------------------------------------------

const ContentArea = styled.main`
  grid-area: content;
  background: ${p => p.theme.colors.background};
  overflow-y: auto;
  overflow-x: hidden;
  padding: ${p => p.theme.spacing.lg};

  @media (max-width: ${MOBILE_BP}) {
    padding: ${p => p.theme.spacing.md};
  }

  @media (max-width: 480px) {
    padding: ${p => p.theme.spacing.sm};
  }
`

// -- Footer bar ------------------------------------------------------------

const FooterBar = styled.footer`
  grid-area: footer;
  background: ${p => p.theme.colors.primary.lilac};
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 24px 0 16px;
  position: relative;

  @media (max-width: ${MOBILE_BP}) {
    border-radius: 0;
    justify-content: center;
  }
`

const FooterText = styled.span`
  color: ${p => p.theme.colors.text.inverse};
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: ${p => p.theme.typography.fontSize.sm};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.wide};
  opacity: 0.8;
`

// -- Mobile overlay menu ---------------------------------------------------

const MobileOverlay = styled.div<{ $open: boolean }>`
  display: none;

  @media (max-width: ${MOBILE_BP}) {
    display: ${p => p.$open ? 'flex' : 'none'};
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.92);
    z-index: ${p => p.theme.zIndex.modal};
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 6px;
    padding: 24px;
  }
`

const MobileOverlayBtn = styled.button<{ $color: string; $isActive: boolean }>`
  width: 80%;
  max-width: 320px;
  height: 48px;
  border: none;
  cursor: pointer;
  background: ${p => p.$isActive ? p.$color : `${p.$color}44`};
  color: ${p => p.$isActive ? p.theme.colors.text.inverse : p.$color};
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: ${p => p.theme.typography.fontSize.md};
  font-weight: ${p => p.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  letter-spacing: ${p => p.theme.typography.letterSpacing.wide};
  border-radius: 0 24px 24px 0;
  transition: background 0.15s, transform 0.1s;

  &:hover {
    filter: brightness(1.2);
    transform: translateX(4px);
  }
`

const MobileCloseBtn = styled.button`
  position: absolute;
  top: 16px;
  right: 16px;
  background: ${p => p.theme.colors.primary.neonCarrot};
  color: ${p => p.theme.colors.text.inverse};
  border: none;
  font-family: ${p => p.theme.typography.fontFamily.primary};
  font-size: ${p => p.theme.typography.fontSize.md};
  font-weight: ${p => p.theme.typography.fontWeight.bold};
  text-transform: uppercase;
  padding: 10px 20px;
  border-radius: 24px;
  cursor: pointer;
`

const MobileMenuTrigger = styled.button`
  display: none;
  @media (max-width: ${MOBILE_BP}) {
    display: block;
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    background: none;
    border: 2px solid ${p => p.theme.colors.text.inverse};
    color: ${p => p.theme.colors.text.inverse};
    font-family: ${p => p.theme.typography.fontFamily.primary};
    font-size: ${p => p.theme.typography.fontSize.sm};
    font-weight: ${p => p.theme.typography.fontWeight.bold};
    text-transform: uppercase;
    padding: 6px 12px;
    border-radius: 12px;
    cursor: pointer;
  }

  @media (max-width: 480px) {
    display: block;
    position: absolute;
    left: 4px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 11px;
    padding: 4px 8px;
    border-radius: 8px;
    border: 2px solid currentColor;
    background: none;
    color: inherit;
    cursor: pointer;
    text-transform: uppercase;
    font-weight: bold;
    font-family: inherit;
  }
`

// -- Utilities panel -------------------------------------------------------


// ---------------------------------------------------------------------------
// Navigation data
// ---------------------------------------------------------------------------

interface NavItem {
  label: string
  path: string
}

const NAV_ITEMS: NavItem[] = [
  { label: 'Dashboard', path: '/dashboard' },
  { label: 'Vessels', path: '/boats' },
  { label: 'Trip Log', path: '/trips' },
  { label: 'Notes', path: '/notes' },
  { label: 'To-Do Lists', path: '/todos' },
  { label: 'Maintenance', path: '/maintenance' },
  { label: 'Navigation', path: '/map' },
  { label: 'Reports', path: '/reports' },
  { label: 'Calendar', path: '/calendar' },
  { label: 'Photos', path: '/photos' },
  { label: 'Docs', path: '/docs' },
  { label: 'Settings', path: '/settings' },
]

// Compute a TNG-style stardate
// Formula from wikihow: Stardate = (year - 2323) * 1000 + (day_of_year / days_in_year) * 1000
function getStardate(): string {
  const now = new Date()
  const year = now.getFullYear()
  const startOfYear = new Date(year, 0, 1).getTime()
  const endOfYear = new Date(year + 1, 0, 1).getTime()
  const dayFraction = (now.getTime() - startOfYear) / (endOfYear - startOfYear)
  const stardate = (year - 2323) * 1000 + dayFraction * 1000
  return stardate.toFixed(1)
}

// ---------------------------------------------------------------------------
// Component
// ---------------------------------------------------------------------------

interface LCARSLayoutProps {
  children: React.ReactNode
}

export const LCARSLayout: React.FC<LCARSLayoutProps> = ({ children }) => {
  const navigate = useNavigate()
  const location = useLocation()
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)

  const isActive = (path: string) => {
    if (path === '/') return location.pathname === '/'
    if (path === '/dashboard') return location.pathname === '/dashboard'
    return location.pathname.startsWith(path)
  }

  const go = (path: string) => {
    navigate(path)
    setMobileMenuOpen(false)
  }

  const stardate = getStardate()

  // Pick LCARS palette colors for each nav item
  const FILLER_COLORS = ['#664466', '#3366CC', '#006699', '#CC99CC', '#FFCC66']

  return (
    <LayoutContainer>
      {/* ---- Sidebar (desktop) ---- */}
      <Sidebar>
        <TopElbow />

        {NAV_ITEMS.map((item, i) => {
          const colorKey = NAV_COLORS[i % NAV_COLORS.length]
          // Resolve the hex color from theme at render via a lookup
          const colorMap: Record<string, string> = {
            tanoi: '#FFCC99',
            goldenTanoi: '#FFCC66',
            neonCarrot: '#FF9933',
            lilac: '#CC99CC',
            anakiwa: '#99CCFF',
            mariner: '#3366CC',
            paleCanary: '#FFFF99',
            eggplant: '#664466',
            bahamBlue: '#006699',
          }
          const hex = colorMap[colorKey] || '#FFCC99'

          return (
            <React.Fragment key={item.path}>
              {i > 0 && (
                <FillerBar $color={FILLER_COLORS[i % FILLER_COLORS.length]} />
              )}
              <SidebarButton
                $color={hex}
                $isActive={isActive(item.path)}
                onClick={() => go(item.path)}
                aria-current={isActive(item.path) ? 'page' : undefined}
              >
                {item.label}
              </SidebarButton>
            </React.Fragment>
          )
        })}

        <BottomElbow />
      </Sidebar>

      {/* ---- Header bar ---- */}
      <HeaderBar>
        <MobileMenuTrigger onClick={() => setMobileMenuOpen(true)}>
          Menu
        </MobileMenuTrigger>
        <HeaderStardate>Stardate {stardate} ({new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })})</HeaderStardate>
        <HeaderLogo src="/assets/captains-log-logo.png" alt="Captain's Log" onClick={() => go('/')} />
        <HeaderTitle onClick={() => go('/')}>Captain&apos;s Log</HeaderTitle>
        <div style={{ marginLeft: '8px' }}>
          <NotificationPanel />
        </div>
      </HeaderBar>

      {/* ---- Main content ---- */}
      <ContentArea>
        {children}
      </ContentArea>

      {/* ---- Footer bar ---- */}
      <FooterBar>
        <OfflineIndicator />
        <FooterText style={{ marginLeft: 'auto' }}>LCARS v47.3 &mdash; Library Computer Access/Retrieval System</FooterText>
      </FooterBar>

      {/* ---- Mobile overlay menu ---- */}
      <MobileOverlay $open={mobileMenuOpen}>
        <MobileCloseBtn onClick={() => setMobileMenuOpen(false)}>Close</MobileCloseBtn>
        {NAV_ITEMS.map((item, i) => {
          const colorMap: Record<string, string> = {
            tanoi: '#FFCC99', goldenTanoi: '#FFCC66', neonCarrot: '#FF9933',
            lilac: '#CC99CC', anakiwa: '#99CCFF', mariner: '#3366CC',
            paleCanary: '#FFFF99', eggplant: '#664466', bahamBlue: '#006699',
          }
          const hex = colorMap[NAV_COLORS[i % NAV_COLORS.length]] || '#FFCC99'
          return (
            <MobileOverlayBtn
              key={item.path}
              $color={hex}
              $isActive={isActive(item.path)}
              onClick={() => go(item.path)}
            >
              {item.label}
            </MobileOverlayBtn>
          )
        })}
      </MobileOverlay>
    </LayoutContainer>
  )
}
