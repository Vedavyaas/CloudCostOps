import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

const target = process.env.BACKEND_URL || 'http://13.204.66.166:6000';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: target,
        changeOrigin: true,
        rewrite: (path) => `/AUTHENTICATIONSYSTEM${path}`
      },
      '/admin': {
        target: target,
        changeOrigin: true,
        rewrite: (path) => `/AUTHENTICATIONSYSTEM${path}`
      },
      '/get': {
        target: target,
        changeOrigin: true,
        rewrite: (path) => `/AUTHENTICATIONSYSTEM${path}`
      },
      '/ORCHESTRATIONENGINE': {
        target: target,
        changeOrigin: true
      }
    }
  }
})
