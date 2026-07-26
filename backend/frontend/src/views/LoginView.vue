<template>
  <section class="mx-auto max-w-xl space-y-6">
    <div class="space-y-2 text-center">
      <h1 class="text-3xl font-semibold">Login</h1>
      <p class="text-muted">Melde dich an, um deine Zeiten zu verwalten.</p>
    </div>

    <BaseCard>
      <form class="space-y-5" @submit.prevent="handleLogin">
        <div class="space-y-2">
          <label class="text-sm font-medium">Benutzername</label>
          <BaseInput
            v-model="form.username"
            type="text"
            placeholder="Dein Benutzername"
            :disabled="loading"
            required
          />
        </div>

        <div class="space-y-2">
          <label class="text-sm font-medium">Passwort</label>
          <BaseInput
            v-model="form.password"
            type="password"
            placeholder="••••••••"
            :disabled="loading"
            required
          />
        </div>

        <!-- Unsere neue globale Fehlerkomponente -->
        <AppError :message="errorMessage" :errors="validationErrors" />

        <div class="flex flex-col gap-3">
          <BaseButton type="submit" :disabled="loading">
            {{ loading ? 'Anmeldung läuft...' : 'Einloggen' }}
          </BaseButton>

          <RouterLink :to="{ name: 'home' }" class="text-center text-sm text-muted hover:underline">
            Zurück zur Startseite
          </RouterLink>
        </div>
      </form>
    </BaseCard>
  </section>
</template>

<script setup>
import { reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useFormHandler } from '../composables/useFormHandler'
import AppError from '../components/ui/AppError.vue'
import BaseButton from '../components/ui/BaseButton.vue'
import BaseCard from '../components/ui/BaseCard.vue'
import BaseInput from '../components/ui/BaseInput.vue'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

// Composable für Loading & Error Logic
const { loading, errorMessage, validationErrors, handleAction } = useFormHandler()

const form = reactive({
  username: '',
  password: ''
})

const redirectTarget = computed(() => route.query.redirect || '/time-entries')

async function handleLogin() {
  await handleAction(
    () => authStore.login({ 
      username: form.username, 
      password: form.password 
    }),
    () => {
      router.push(redirectTarget.value)
    }
  );

  // UX-Verbesserung: Wenn nach dem Aufruf errorMessage gesetzt ist (Fehler trat auf)
  if (errorMessage.value) {
    form.password = ''; // Passwortfeld leeren bei Fehler
  }
}
</script>