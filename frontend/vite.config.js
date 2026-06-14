import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://13.204.66.166',
        changeOrigin: true,
        rewrite: (path) => `/AUTHENTICATIONSYSTEM${path}`
      },
      '/admin': {
        target: 'http://13.204.66.166',
        changeOrigin: true,
        rewrite: (path) => `/AUTHENTICATIONSYSTEM${path}`
      },
      '/get': {
        target: 'http://13.204.66.166',
        changeOrigin: true,
        rewrite: (path) => `/AUTHENTICATIONSYSTEM${path}`
      },
      '/ORCHESTRATIONENGINE': {
        target: 'http://13.204.66.166',
        changeOrigin: true
      }
    }
  }
})
