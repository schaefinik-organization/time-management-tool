import { ref, onMounted } from 'vue'

const theme = ref('light')

export function useTheme() {
  function applyTheme(value) {
    theme.value = value
    document.documentElement.setAttribute('data-theme', value)
  }

  function toggleTheme() {
    applyTheme(theme.value === 'dark' ? 'light' : 'dark')
  }

  onMounted(() => {
    applyTheme('light')
  })

  return {
    theme,
    applyTheme,
    toggleTheme
  }
}
