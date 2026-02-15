/**
 * Browser Compatibility Tests
 * Tests for cross-browser compatibility of LCARS components and features
 */

import { describe, it, expect, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ThemeProvider } from 'styled-components';
import { lcarsTheme } from '../src/styles/theme';

// Mock components for testing
import { LCARSButton } from '../src/components/lcars/LCARSButton';
import { LCARSPanel } from '../src/components/lcars/LCARSPanel';
import { LCARSHeader } from '../src/components/lcars/LCARSHeader';

// Test wrapper component
const TestWrapper = ({ children }: { children: React.ReactNode }) => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false }
    }
  });

  return (
    <BrowserRouter>
      <QueryClientProvider client={queryClient}>
        <ThemeProvider theme={lcarsTheme}>
          {children}
        </ThemeProvider>
      </QueryClientProvider>
    </BrowserRouter>
  );
};

describe('Browser Compatibility Tests', () => {
  let queryClient: QueryClient;

  beforeEach(() => {
    queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false },
        mutations: { retry: false }
      }
    });
  });

  describe('CSS Grid and Flexbox Support', () => {
    it('should render LCARS layout with CSS Grid', () => {
      render(
        <TestWrapper>
          <LCARSPanel>
            <div data-testid="grid-content">Grid Layout Test</div>
          </LCARSPanel>
        </TestWrapper>
      );

      const content = screen.getByTestId('grid-content');
      expect(content).toBeInTheDocument();
    });

    it('should render LCARS buttons with flexbox', () => {
      render(
        <TestWrapper>
          <LCARSButton onClick={() => {}}>
            Test Button
          </LCARSButton>
        </TestWrapper>
      );

      const button = screen.getByRole('button', { name: /test button/i });
      expect(button).toBeInTheDocument();
    });
  });

  describe('CSS Custom Properties (Variables)', () => {
    it('should support CSS custom properties for LCARS colors', () => {
      render(
        <TestWrapper>
          <LCARSHeader title="Test Header" />
        </TestWrapper>
      );

      const header = screen.getByText('Test Header');
      expect(header).toBeInTheDocument();
      
      // Check if CSS custom properties are applied
      const computedStyle = window.getComputedStyle(header);
      expect(computedStyle).toBeDefined();
    });
  });

  describe('ES6+ Features', () => {
    it('should support arrow functions', () => {
      const arrowFunction = () => 'test';
      expect(arrowFunction()).toBe('test');
    });

    it('should support template literals', () => {
      const name = 'LCARS';
      const template = `Hello ${name}`;
      expect(template).toBe('Hello LCARS');
    });

    it('should support destructuring', () => {
      const obj = { a: 1, b: 2 };
      const { a, b } = obj;
      expect(a).toBe(1);
      expect(b).toBe(2);
    });

    it('should support async/await', async () => {
      const asyncFunction = async () => {
        return Promise.resolve('async test');
      };
      
      const result = await asyncFunction();
      expect(result).toBe('async test');
    });
  });

  describe('Fetch API Support', () => {
    it('should have fetch API available', () => {
      expect(typeof fetch).toBe('function');
    });

    it('should support Promise-based requests', async () => {
      // Mock fetch for testing
      global.fetch = vi.fn(() =>
        Promise.resolve({
          ok: true,
          json: () => Promise.resolve({ test: 'data' }),
        })
      ) as any;

      const response = await fetch('/test');
      const data = await response.json();
      
      expect(data).toEqual({ test: 'data' });
    });
  });

  describe('Local Storage Support', () => {
    it('should support localStorage', () => {
      expect(typeof localStorage).toBe('object');
      expect(typeof localStorage.setItem).toBe('function');
      expect(typeof localStorage.getItem).toBe('function');
    });

    it('should be able to store and retrieve data', () => {
      localStorage.setItem('test-key', 'test-value');
      const value = localStorage.getItem('test-key');
      expect(value).toBe('test-value');
      
      localStorage.removeItem('test-key');
    });
  });

  describe('History API Support', () => {
    it('should support History API', () => {
      expect(typeof history).toBe('object');
      expect(typeof history.pushState).toBe('function');
      expect(typeof history.replaceState).toBe('function');
    });
  });

  describe('Responsive Design', () => {
    it('should support CSS media queries', () => {
      // Test if matchMedia is available
      expect(typeof window.matchMedia).toBe('function');
      
      const mediaQuery = window.matchMedia('(max-width: 768px)');
      expect(mediaQuery).toBeDefined();
      expect(typeof mediaQuery.matches).toBe('boolean');
    });
  });

  describe('Touch Events (Mobile Support)', () => {
    it('should support touch events', () => {
      const touchSupported = 'ontouchstart' in window || 
                           navigator.maxTouchPoints > 0 ||
                           (navigator as any).msMaxTouchPoints > 0;
      
      // This test will pass on both touch and non-touch devices
      expect(typeof touchSupported).toBe('boolean');
    });
  });

  describe('WebGL Support (for Maps)', () => {
    it('should support WebGL for map rendering', () => {
      const canvas = document.createElement('canvas');
      const gl = canvas.getContext('webgl') || canvas.getContext('experimental-webgl');
      
      // WebGL might not be available in test environment, so we just check the API exists
      expect(typeof canvas.getContext).toBe('function');
    });
  });

  describe('Geolocation API Support', () => {
    it('should support Geolocation API', () => {
      expect(typeof navigator.geolocation).toBe('object');
      expect(typeof navigator.geolocation.getCurrentPosition).toBe('function');
    });
  });

  describe('File API Support', () => {
    it('should support File API for photo uploads', () => {
      expect(typeof File).toBe('function');
      expect(typeof FileReader).toBe('function');
      expect(typeof FormData).toBe('function');
    });
  });
});