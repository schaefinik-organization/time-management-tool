<template>
  <section class="space-y-6">
    <div class="flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
      <div>
        <h1 class="text-2xl font-semibold">Zeiteinträge</h1>
        <p class="mt-1 text-muted">
          Einträge für den gewählten Tag erfassen und verwalten.
        </p>
      </div>

      <div class="w-full max-w-xs space-y-2">
        <label class="text-sm font-medium">Datum</label>
        <BaseInput v-model="selectedDateModel" type="date" />
      </div>
    </div>

    <div class="grid gap-6 xl:grid-cols-2">
      <BaseCard>
        <div class="space-y-5">
          <div>
            <h2 class="text-lg font-semibold">
              {{ isEditMode ? 'Eintrag bearbeiten' : 'Neuen Eintrag anlegen' }}
            </h2>
            <p class="mt-1 text-sm text-muted">
              Erfasse Arbeitszeiten pro Projekt und Tag.
            </p>
          </div>

          <form class="space-y-4" @submit.prevent="submitTimeEntry">
            <div class="space-y-2">
              <label class="text-sm font-medium">Projekt</label>
              <BaseSelect v-model="form.projectId" required>
                <option value="">Bitte wählen</option>
                <option
                  v-for="project in projects"
                  :key="project.id"
                  :value="project.id"
                >
                  {{ project.name }}
                </option>
              </BaseSelect>
            </div>

            <div class="grid gap-4 sm:grid-cols-2">
              <div class="space-y-2">
                <label class="text-sm font-medium">Startzeit</label>
                <BaseInput v-model="form.startTime" type="time" required />
              </div>

              <div class="space-y-2">
                <label class="text-sm font-medium">Endzeit</label>
                <BaseInput v-model="form.endTime" type="time" required />
              </div>
            </div>

            <div class="space-y-2">
              <label class="text-sm font-medium">Notiz</label>
              <BaseTextarea
                v-model="form.note"
                :rows="4"
                placeholder="Optionaler Kommentar zum Eintrag"
              />
            </div>

            <div class="flex flex-wrap gap-3">
              <BaseButton type="submit" :disabled="loading">
                {{ loading ? 'Speichert...' : isEditMode ? 'Eintrag aktualisieren' : 'Eintrag speichern' }}
              </BaseButton>

              <BaseButton
                v-if="isEditMode"
                type="button"
                variant="secondary"
                @click="resetForm"
              >
                Abbrechen
              </BaseButton>
            </div>
          </form>

          <div
            v-if="errorMessage"
            class="rounded-xl border px-4 py-3 text-sm"
            :style="{ borderColor: 'rgb(248 113 113 / 0.35)', color: 'var(--danger)', background: 'rgb(254 242 242)' }"
          >
            {{ errorMessage }}
          </div>
        </div>
      </BaseCard>

      <BaseCard>
        <div class="space-y-5">
          <div class="flex items-center justify-between gap-3">
            <div>
              <h2 class="text-lg font-semibold">Einträge am {{ selectedDate }}</h2>
              <p class="mt-1 text-sm text-muted">
                Tagesübersicht der gepflegten Zeiten.
              </p>
            </div>

            <span
              class="rounded-full px-3 py-1 text-xs font-medium"
              :style="{ background: 'var(--bg-muted)', color: 'var(--text-muted)' }"
            >
              {{ timeEntries.length }} Einträge
            </span>
          </div>

          <div
            v-if="entriesLoading"
            class="rounded-2xl border p-6 text-sm text-muted"
            :style="{ borderColor: 'var(--border)', background: 'var(--surface-soft)' }"
          >
            Lade Einträge...
          </div>

          <div
            v-else-if="timeEntries.length === 0"
            class="rounded-2xl border border-dashed p-6 text-sm text-muted"
            :style="{ borderColor: 'var(--border)' }"
          >
            Keine Einträge für diesen Tag vorhanden.
          </div>

          <ul v-else class="space-y-3">
            <li
              v-for="entry in timeEntries"
              :key="entry.id"
              class="rounded-2xl border p-4"
              :style="{ borderColor: 'var(--border)', background: 'var(--surface-soft)' }"
            >
              <div class="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
                <div class="space-y-1">
                  <p class="font-semibold">{{ entry.projectName }}</p>
                  <p class="text-sm text-muted">
                    {{ entry.startTime }} - {{ entry.endTime }}
                  </p>
                  <p class="text-sm text-muted">
                    {{ entry.note || 'Keine Notiz' }}
                  </p>
                </div>

                <div class="flex flex-wrap gap-2">
                  <BaseButton variant="secondary" @click="editTimeEntry(entry)">
                    Bearbeiten
                  </BaseButton>
                  <BaseButton variant="danger" @click="removeTimeEntry(entry.id)">
                    Löschen
                  </BaseButton>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </BaseCard>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useAppStore } from '../stores/app'
import { fetchProjects } from '../api/projects'
import {
  createTimeEntry,
  deleteTimeEntry,
  fetchTimeEntriesByDate,
  updateTimeEntry
} from '../api/timeEntries'
import BaseButton from '../components/ui/BaseButton.vue'
import BaseCard from '../components/ui/BaseCard.vue'
import BaseInput from '../components/ui/BaseInput.vue'
import BaseSelect from '../components/ui/BaseSelect.vue'
import BaseTextarea from '../components/ui/BaseTextarea.vue'

const appStore = useAppStore()

const selectedDate = computed(() => appStore.selectedDate)
const editingId = ref(null)

const selectedDateModel = computed({
  get: () => appStore.selectedDate,
  set: (value) => appStore.setSelectedDate(value)
})

const projects = ref([])
const timeEntries = ref([])
const loading = ref(false)
const entriesLoading = ref(false)
const errorMessage = ref('')

const form = reactive({
  projectId: '',
  startTime: '',
  endTime: '',
  note: ''
})

const isEditMode = computed(() => editingId.value !== null)

async function loadProjects() {
  const response = await fetchProjects()
  projects.value = response.data
}

async function loadTimeEntries() {
  entriesLoading.value = true
  try {
    const response = await fetchTimeEntriesByDate(selectedDate.value)
    timeEntries.value = response.data
  } finally {
    entriesLoading.value = false
  }
}

function editTimeEntry(entry) {
  editingId.value = entry.id
  form.projectId = entry.projectId
  form.startTime = entry.startTime?.slice(0, 5) || ''
  form.endTime = entry.endTime?.slice(0, 5) || ''
  form.note = entry.note || ''
}

function resetForm() {
  editingId.value = null
  form.projectId = ''
  form.startTime = ''
  form.endTime = ''
  form.note = ''
  errorMessage.value = ''
}

async function submitTimeEntry() {
  loading.value = true
  errorMessage.value = ''

  try {
    const payload = {
      projectId: Number(form.projectId),
      entryDate: selectedDate.value,
      startTime: form.startTime,
      endTime: form.endTime,
      note: form.note
    }

    if (isEditMode.value) {
      await updateTimeEntry(editingId.value, payload)
    } else {
      await createTimeEntry(payload)
    }

    resetForm()
    await loadTimeEntries()
  } catch (error) {
    errorMessage.value =
      error?.response?.data?.message ||
      'Eintrag konnte nicht gespeichert werden.'
  } finally {
    loading.value = false
  }
}

async function removeTimeEntry(id) {
  const confirmed = window.confirm('Zeiteintrag wirklich löschen?')
  if (!confirmed) return

  try {
    await deleteTimeEntry(id)
    if (editingId.value === id) {
      resetForm()
    }
    await loadTimeEntries()
  } catch (error) {
    errorMessage.value =
      error?.response?.data?.message ||
      'Eintrag konnte nicht gelöscht werden.'
  }
}

watch(selectedDate, () => {
  resetForm()
  loadTimeEntries()
})

onMounted(async () => {
  await loadProjects()
  await loadTimeEntries()
})
</script>
