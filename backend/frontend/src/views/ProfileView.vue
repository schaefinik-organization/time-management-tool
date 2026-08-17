<template>
  <section class="mx-auto max-w-2xl space-y-6">
    <!-- Header -->
    <div class="space-y-2">
      <h1 class="text-2xl font-semibold">Mein Profil</h1>
      <p class="text-muted">Verwalte deine persönlichen Informationen und Sicherheitseinstellungen.</p>
    </div>

    <!-- Sektion 1: Benutzerinformationen -->
    <BaseCard>
      <div class="space-y-4">
        <div class="flex items-center gap-4">
          <div class="flex h-16 w-16 items-center justify-center rounded-full bg-primary text-xl font-bold text-white">
            {{ userInitials }}
          </div>
          <div>
            <h2 class="text-lg font-semibold">{{ authStore.user?.username }}</h2>
            <p class="text-sm text-muted">{{ authStore.user?.email }}</p>
          </div>
        </div>
      </div>
    </BaseCard>

    <div class="grid gap-6 md:grid-cols-1">
      <!-- Sektion 2: Profil bearbeiten -->
      <BaseCard>
        <div class="space-y-5">
          <h2 class="text-lg font-semibold">Persönliche Daten</h2>
          
          <form class="space-y-4" @submit.prevent="updateProfile">
            <div class="space-y-2">
              <label class="text-sm font-medium">Anzeigename / Voller Name</label>
              <BaseInput v-model="profileForm.username" type="text" :placeholder="authStore.user?.username || ''" />
            </div>

            <div class="space-y-2">
              <label class="text-sm font-medium">E-Mail Adresse</label>
              <BaseInput v-model="profileForm.email" type="email" :placeholder="authStore.user?.email || ''" />
            </div>

            <AppError :message="profileError" :errors="profileValidation" />

            <div v-if="profileSuccess" class="text-sm text-green-600 font-medium">
              Profil wurde erfolgreich aktualisiert.
            </div>

            <BaseButton type="submit" :disabled="savingProfile">
              {{ savingProfile ? 'Speichert...' : 'Profil aktualisieren' }}
            </BaseButton>
          </form>
        </div>
      </BaseCard>

      <!-- Sektion 3: Passwort ändern -->
      <BaseCard>
        <div class="space-y-5">
          <h2 class="text-lg font-semibold">Passwort Sicherheit</h2>
          
          <form class="space-y-4" @submit.prevent="submitPasswordChange">
            <div class="space-y-2">
              <label class="text-sm font-medium">Aktuelles Passwort</label>
              <BaseInput v-model="pwForm.currentPassword" type="password" required />
            </div>

            <div class="grid gap-4 sm:grid-cols-2">
              <div class="space-y-2">
                <label class="text-sm font-medium">Neues Passwort</label>
                <BaseInput v-model="pwForm.newPassword" type="password" required />
              </div>
              <div class="space-y-2">
                <label class="text-sm font-medium">Bestätigung</label>
                <BaseInput v-model="pwForm.confirmPassword" type="password" required />
              </div>
            </div>

            <AppError :message="pwError" :errors="pwValidation" />
            
            <div v-if="pwSuccess" class="text-sm text-green-600 font-medium">
              Passwort wurde erfolgreich geändert.
            </div>

            <BaseButton type="submit" variant="secondary" :disabled="changingPw">
              {{ changingPw ? 'Wird geändert...' : 'Passwort ändern' }}
            </BaseButton>
          </form>
        </div>
      </BaseCard>

      <!-- Sektion 4: Gefahrbereich -->
      <BaseCard class="border-red-100 bg-red-50/30">
        <div class="flex items-center justify-between">
          <div>
            <h2 class="text-lg font-semibold text-red-800">Abmelden</h2>
            <p class="text-sm text-red-600/80">Sitzung beenden und vom Tool abmelden.</p>
          </div>
          <BaseButton variant="danger" @click="handleLogout">Logout</BaseButton>
        </div>
      </BaseCard>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useFormHandler } from '@/composables/useFormHandler'
import { updateProfileApi, changePasswordApi } from '@/api/profile'
import BaseCard from '@/components/ui/BaseCard.vue'
import BaseInput from '@/components/ui/BaseInput.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import AppError from '@/components/ui/AppError.vue'

const authStore = useAuthStore()
const router = useRouter()

// Form Handler für Profil-Update
const { 
  loading: savingProfile, 
  errorMessage: profileError, 
  validationErrors: profileValidation, 
  handleAction: runProfileUpdate 
} = useFormHandler()

// Form Handler für Passwort-Wechsel
const { 
  loading: changingPw, 
  errorMessage: pwError, 
  validationErrors: pwValidation, 
  handleAction: runPwUpdate 
} = useFormHandler()

const profileSuccess = ref(false)
const pwSuccess = ref(false)

const profileForm = reactive({
  username: authStore.user?.username || '',
  email: authStore.user?.email || ''
})

const pwForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const userInitials = computed(() => {
  return authStore.user?.username?.substring(0, 2).toUpperCase() || 'U'
})

async function updateProfile() {
  await runProfileUpdate(
     () => updateProfileApi({
      username: profileForm.username,
      email: profileForm.email
    }),
    () => {
      authStore.fetchCurrentUser() // Aktualisiere den Benutzer im Store
      profileSuccess.value = true
      profileForm.username = authStore.user?.username || ''
      profileForm.email = authStore.user?.email || ''
    }
  )
}

async function submitPasswordChange() {
  pwSuccess.value = false
  
  if (pwForm.newPassword !== pwForm.confirmPassword) {
    // Hier nutzen wir das Error-Handling des Handlers manuell
    alert("Passwörter stimmen nicht überein!")
    return
  }

  await runPwUpdate(
    () => changePasswordApi({
      currentPassword: pwForm.currentPassword,
      newPassword: pwForm.newPassword
    }),
    () => {
      pwSuccess.value = true
      pwForm.currentPassword = ''
      pwForm.newPassword = ''
      pwForm.confirmPassword = ''
    }
  )
}

function handleLogout() {
  authStore.logout()
  router.push('/') 
}

onMounted(async () => {
  if (!authStore.user) {
    await authStore.fetchCurrentUser()
  }
  profileForm.username = authStore.user?.username || ''
  profileForm.email = authStore.user?.email || ''
})
</script>