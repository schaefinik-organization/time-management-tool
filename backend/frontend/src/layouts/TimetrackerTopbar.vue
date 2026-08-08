<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { onMounted } from 'vue'
import  { useAuthStore }  from '@/stores/authStore'

const authStore = useAuthStore()

const username = computed(() => authStore.user?.username || 'nicht angemeldet')
const isAuthenticated = computed(() => authStore.isAuthenticated)

function handleLogout() {
  authStore.logout()
  router.push({ name: 'login' })
}

onMounted(() => {
  authStore.fetchCurrentUser()
})
</script>

<template>
  <div class="topbar">
    <div class="flex items-center gap-4">
      <span class="font-semibold">Angemeldet als:</span>
      <span class="font-bold">{{ username }}</span>
    </div>

    <div>
      <button v-if="isAuthenticated" @click="handleLogout" class="btn-logout">
        Logout
      </button>
    </div>
  </div>
</template>