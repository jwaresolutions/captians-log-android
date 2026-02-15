import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    host: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8585',
        changeOrigin: true,
        secure: false,
      },
      '/health': {
        target: 'http://localhost:8585',
        changeOrigin: true,
        secure: false,
      },
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: true,
    target: ['chrome89', 'firefox88', 'safari14', 'edge89'], // Modern browser support
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'react-dom'],
          router: ['react-router-dom'],
          query: ['@tanstack/react-query'],
          maps: ['leaflet', 'react-leaflet'],
          ui: ['styled-components']
        }
      }
    }
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
  },
  define: {
    // Define environment variables for the client
    __DEV__: JSON.stringify(process.env.NODE_ENV === 'development'),
  },
})