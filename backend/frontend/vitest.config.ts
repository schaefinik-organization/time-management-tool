import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  test: {
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html', 'lcov'], 
      exclude: ['src/main.ts', 'vite-env.d.ts', '.eslintrc.cjs'] 
    }
  }
})