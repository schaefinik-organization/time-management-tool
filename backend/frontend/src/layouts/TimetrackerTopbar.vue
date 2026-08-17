<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore }  from '@/stores/authStore'
import { useUserStore } from '@/stores/userStore'

const authStore = useAuthStore()
const userStore = useUserStore()
const router = useRouter()

const isAuthenticated = computed(() => authStore.isAuthenticated)

function handleLogout() {
  authStore.logout();
  userStore.clearCurrentUser();
  router.push('/');
}

onMounted(() => {
  userStore.loadCurrentUser()
})
</script>

<template>
  <div class="topbar" v-if="isAuthenticated">
    <div class="flex items-center gap-4">
      <span class="font-semibold">Angemeldet als:</span>
      <span class="font-bold">{{ userStore.currentUser?.username }}</span>
    </div>

    <div>
      <button @click="handleLogout" class="btn-logout">
        Logout
      </button>
    </div>
  </div>
</template>