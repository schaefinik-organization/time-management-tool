<template>
  <div class="app-container">
    <AppHeader />
    <main class="flex-1">
      <div class="page-container page-section">
        <slot />
      </div>
    </main>
    <AppFooter />
    <CookieBanner />
  </div>
</template>

<script setup>
import AppHeader from './AppHeader.vue'
import AppFooter from './AppFooter.vue'
import CookieBanner from '../feedback/CookieBanner.vue'
</script>
