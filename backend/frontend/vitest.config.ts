import { defineConfig } from 'vite'
import { fileURLToPath, URL } from 'node:url'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  test: {
    environment: 'jsdom', 
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html', 'lcov'], 
      exclude: ['src/main.ts', 'vite-env.d.ts', '.eslintrc.cjs'] 
    }
  }
})