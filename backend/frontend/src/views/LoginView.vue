<template>
  <section class="mx-auto max-w-xl space-y-6">
    <div class="space-y-2 text-center">
      <h1 class="text-3xl font-semibold">Login</h1>
      <p class="text-muted">
        Die Authentifizierung wird im nächsten Ausbauschritt ergänzt.
      </p>
    </div>

    <BaseCard>
      <div class="space-y-5">
        <div class="space-y-2">
          <label class="text-sm font-medium">E-Mail</label>
          <BaseInput
            v-model="form.email"
            type="email"
            placeholder="name@beispiel.de"
          />
        </div>

        <div class="space-y-2">
          <label class="text-sm font-medium">Passwort</label>
          <BaseInput
            v-model="form.password"
            type="password"
            placeholder="••••••••"
          />
        </div>

        <div
          class="rounded-xl border px-4 py-3 text-sm"
          :style="{ borderColor: 'var(--border)', background: 'var(--surface-soft)', color: 'var(--text-muted)' }"
        >
          Aktuell ist dies eine vorbereitete Login-Seite ohne echte Authentifizierung.
          Zielroute nach späterem Login:
          <span class="font-medium">{{ redirectTarget }}</span>
        </div>

        <div class="flex flex-wrap gap-3">
          <BaseButton @click="simulateLogin">
            Demo-Login
          </BaseButton>

          <RouterLink :to="{ name: 'home' }" class="btn-secondary no-underline">
            Zurück zur Startseite
          </RouterLink>
        </div>
      </div>
    </BaseCard>
  </section>
</template>

<script setup>
import { computed, reactive } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import BaseButton from '../components/ui/BaseButton.vue'
import BaseCard from '../components/ui/BaseCard.vue'
import BaseInput from '../components/ui/BaseInput.vue'

const route = useRoute()
const router = useRouter()

const form = reactive({
  email: '',
  password: ''
})

const redirectTarget = computed(() => route.query.redirect || '/')

function simulateLogin() {
  router.push(String(redirectTarget.value || '/'))
}
</script>
