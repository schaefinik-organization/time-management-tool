<template>
  <header
    class="sticky top-0 z-50 border-b shadow-sm"
    :style="{ borderColor: 'var(--border)', background: 'var(--surface)' }"
  >
    <div class="page-container flex items-center justify-between py-3">
      <!-- Logo Bereich -->
      <div class="flex items-center gap-8">

        <!-- Desktop Navigation -->
        <nav class="hidden items-center gap-1 lg:flex">
          <RouterLink v-if="authStore.isAuthenticated" :to="{ name: 'reports' }" class="nav-link">Projekte</RouterLink>
          <RouterLink v-if="authStore.isAuthenticated" :to="{ name: 'tracker' }" class="nav-link">Zeiten</RouterLink>
          
          <!-- Admin Dropdown -->
          <div v-if="authStore.isAdmin" class="relative ml-2" v-click-outside="() => (adminMenuOpen = false)">
            <button 
              @click="adminMenuOpen = !adminMenuOpen"
              class="nav-link flex items-center gap-1"
            >
              Admin
              <ChevronDownIcon class="h-4 w-4 transition-transform" :class="{ 'rotate-180': adminMenuOpen }" />
            </button>
          </div>
        </nav>
      </div>

      <!-- Rechts: Actions & User -->
      <div class="flex items-center gap-3">
        <button class="rounded-lg p-2 hover:bg-muted" @click="toggleTheme" title="Theme umschalten">
          <SunIcon v-if="theme === 'dark'" class="h-5 w-5" />
          <MoonIcon v-else class="h-5 w-5" />
        </button>

        <!-- Eingeloggt: Profil Dropdown -->
        <div v-if="authStore.isAuthenticated" class="relative" v-click-outside="() => (userMenuOpen = false)">
          <button 
            @click="userMenuOpen = !userMenuOpen"
            class="flex items-center gap-2 rounded-full border p-1 pr-3 transition-colors hover:bg-muted"
            :style="{ borderColor: 'var(--border)' }"
          >
            <span class="hidden text-sm font-medium sm:block">{{ authStore.user?.username }}</span>
          </button>

          <div v-if="userMenuOpen" class="dropdown-menu right-0">
            <div class="border-b px-4 py-2 text-xs text-muted sm:hidden">
              Eingeloggt als: <strong>{{ authStore.user?.username }}</strong>
            </div>
            <RouterLink :to="{ name: 'profile' }" class="dropdown-item" @click="userMenuOpen = false">
              Mein Profil
            </RouterLink>
            <button @click="handleLogout" class="dropdown-item w-full text-left text-danger">
              Abmelden
            </button>
          </div>
        </div>

        <!-- Ausgeloggt: Login Button -->
        <RouterLink v-else :to="{ name: 'login' }" class="btn-primary no-underline">
          Login
        </RouterLink>

        <!-- Mobil Menü Toggle -->
        <button class="p-2 lg:hidden" @click="mobileMenuOpen = !mobileMenuOpen">
          <MenuIcon v-if="!mobileMenuOpen" class="h-6 w-6" />
          <XIcon v-else class="h-6 w-6" />
        </button>
      </div>
    </div>

    <!-- Mobil Navigation Overlay -->
    <div v-if="mobileMenuOpen" class="border-t lg:hidden" :style="{ background: 'var(--surface)' }">
      <nav class="flex flex-col p-4">
        <template v-if="authStore.isAuthenticated">
          <RouterLink :to="{ name: 'reports' }" class="p-3 font-medium" @click="mobileMenuOpen = false">Projekte</RouterLink>
          <RouterLink :to="{ name: 'time-entries' }" class="p-3 font-medium" @click="mobileMenuOpen = false">Zeiten</RouterLink>
          <div class="mt-2 border-t pt-2">
            <p class="px-3 py-1 text-xs font-bold uppercase text-muted">Admin</p>
            <RouterLink :to="{ name: 'admin-users' }" class="p-3 block" @click="mobileMenuOpen = false">Benutzerverwaltung</RouterLink>
            <RouterLink :to="{ name: 'admin-reports' }" class="p-3 block" @click="mobileMenuOpen = false">Benutzerreporting
            </RouterLink>
          </div>
        </template>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useTheme } from '../../composables/useTheme'
// Icons (Hier kannst du Lucide-Vue-Next oder einfache SVGs nutzen)
import { ChevronDownIcon, SunIcon, MoonIcon, MenuIcon, XIcon } from 'lucide-vue-next'

const authStore = useAuthStore()
const router = useRouter()
const { theme, toggleTheme } = useTheme()

const adminMenuOpen = ref(false)
const userMenuOpen = ref(false)
const mobileMenuOpen = ref(false)

const userInitials = computed(() => {
  const name = authStore.user?.username || 'U'
  return name.substring(0, 2).toUpperCase()
})

function handleLogout() {
  userMenuOpen.value = false
  authStore.logout()
  router.push({ name: 'login' })
}

// Custom Directive für Click-Outside (sehr nützlich!)
const vClickOutside = {
  mounted(el, binding) {
    el.clickOutsideEvent = (event) => {
      if (!(el === event.target || el.contains(event.target))) {
        binding.value()
      }
    }
    document.addEventListener('click', el.clickOutsideEvent)
  },
  unmounted(el) {
    document.removeEventListener('click', el.clickOutsideEvent)
  }
}
</script>

<style scoped>
.dropdown-menu {
  position: absolute;
  top: 100%;
  margin-top: 0.5rem;
  width: 200px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 0.75rem;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.dropdown-item {
  padding: 0.75rem 1rem;
  font-size: 0.875rem;
  transition: background 0.2s;
  cursor: pointer;
  text-decoration: none;
  color: var(--text);
}

.dropdown-item:hover {
  background: var(--surface-soft);
}

.text-danger {
  color: #ef4444;
}
</style>