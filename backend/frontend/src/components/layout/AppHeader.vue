<template>
  <header
    class="border-b"
    :style="{ borderColor: 'var(--border)', background: 'var(--surface)' }"
  >
    <div class="page-container flex flex-col gap-4 py-4 lg:flex-row lg:items-center lg:justify-between">
      <div class="min-w-0">
        <RouterLink :to="{ name: 'home' }" class="text-xl font-semibold no-underline" :style="{ color: 'var(--text)' }">
          Time Management Tool
        </RouterLink>
        <p class="mt-1 text-sm text-muted">
          Projekte, Zeiteinträge und später nutzerbezogene Auswertung
        </p>
      </div>

      <div class="flex flex-col gap-3 sm:flex-row sm:items-center">
        <nav class="flex flex-wrap items-center gap-2">
          <RouterLink :to="{ name: 'home' }" class="nav-link">
            Home
          </RouterLink>

          <RouterLink :to="{ name: 'projects' }" class="nav-link">
            Projekte
          </RouterLink>

          <RouterLink :to="{ name: 'time-entries' }" class="nav-link">
            Zeiten
          </RouterLink>
        </nav>

        <div class="flex items-center gap-2">
          <button class="btn-secondary" type="button" @click="toggleTheme">
            {{ theme === 'dark' ? 'Light' : 'Dark' }}
          </button>

          <RouterLink :to="{ name: 'login' }" class="btn-primary no-underline">
            Login
          </RouterLink>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { RouterLink } from 'vue-router'
import { useTheme } from '../../composables/useTheme'

const { theme, toggleTheme } = useTheme()
</script>
