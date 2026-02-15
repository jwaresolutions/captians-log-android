// LCARS 2357 (TNG Era) Theme
// Canonical Library Computer Access/Retrieval System color palette
// Reference: Star Trek: The Next Generation production design

export const lcarsTheme = {
  colors: {
    // Canonical LCARS 2357 palette
    primary: {
      paleCanary: '#FFFF99',
      tanoi: '#FFCC99',
      goldenTanoi: '#FFCC66',
      neonCarrot: '#FF9933',
      eggplant: '#664466',
      lilac: '#CC99CC',
      anakiwa: '#99CCFF',
      mariner: '#3366CC',
      bahamBlue: '#006699',
    },

    // Background and surface colors
    background: '#000000',
    surface: {
      dark: '#0A0A0A',
      medium: '#1A1119',
      light: '#2A2233',
    },

    // Text colors
    text: {
      primary: '#FF9933',
      secondary: '#CC99CC',
      muted: '#664466',
      inverse: '#000000',
      light: '#FFCC99',
    },

    // Status colors
    status: {
      success: '#55FF55',
      warning: '#FFFF99',
      error: '#FF5555',
      info: '#99CCFF',
    },

    // Interactive states
    interactive: {
      hover: '#FFCC66',
      active: '#FFCC99',
      disabled: '#664466',
    },
  },

  // Typography
  typography: {
    fontFamily: {
      primary: "'Antonio', 'Helvetica Neue', Arial, sans-serif",
      monospace: "'Courier New', monospace",
    },
    fontSize: {
      xs: '11px',
      sm: '13px',
      md: '15px',
      lg: '18px',
      xl: '24px',
      xxl: '32px',
      xxxl: '48px',
    },
    fontWeight: {
      normal: 400,
      bold: 700,
    },
    lineHeight: {
      tight: 1.1,
      normal: 1.4,
      loose: 1.7,
    },
    letterSpacing: {
      tight: '-0.02em',
      normal: '0.04em',
      wide: '0.1em',
      extraWide: '0.2em',
    },
  },

  // Spacing
  spacing: {
    xs: '4px',
    sm: '8px',
    md: '16px',
    lg: '24px',
    xl: '32px',
    xxl: '48px',
    xxxl: '64px',
  },

  // Border radius — pill shape is the canonical LCARS signature
  borderRadius: {
    none: '0',
    sm: '4px',
    md: '8px',
    lg: '16px',
    xl: '24px',
    pill: '9999px',
  },

  // Shadows — all using the canonical orange glow
  shadows: {
    sm: '0 1px 3px rgba(255, 153, 51, 0.12)',
    md: '0 4px 8px rgba(255, 153, 51, 0.15)',
    lg: '0 10px 20px rgba(255, 153, 51, 0.18)',
    glow: '0 0 20px rgba(255, 153, 51, 0.35)',
    glowStrong: '0 0 40px rgba(255, 153, 51, 0.5)',
    glowSubtle: '0 0 10px rgba(255, 153, 51, 0.15)',
  },

  // Z-index layers
  zIndex: {
    dropdown: 1000,
    sticky: 1020,
    fixed: 1030,
    modal: 1040,
    popover: 1050,
    tooltip: 1060,
  },

  // Breakpoints
  breakpoints: {
    sm: '640px',
    md: '768px',
    lg: '1024px',
    xl: '1280px',
    xxl: '1536px',
  },

  // Animation durations
  animation: {
    fast: '150ms',
    normal: '300ms',
    slow: '500ms',
  },

  // LCARS-specific measurements
  lcars: {
    sidebarWidth: '200px',
    headerHeight: '60px',
    footerHeight: '40px',
    elbowSize: '60px',
    barThickness: '30px',
    buttonHeight: '40px',
    gap: '3px',
    buttonRadius: '9999px',
  },
}

export type LCARSTheme = typeof lcarsTheme

// Styled-components theme type augmentation
declare module 'styled-components' {
  export interface DefaultTheme extends LCARSTheme {}
}
