<template>
  <section class="page">
    <div class="page-header">
      <div>
        <h2>Zeiteinträge</h2>
        <p>Einträge für den gewählten Tag erfassen und verwalten.</p>
      </div>

      <label class="field">
        <span>Datum</span>
        <input type="date" v-model="selectedDateModel" />
      </label>
    </div>

    <div class="grid">
      <section class="card">
        <h3>{{ isEditMode ? 'Eintrag bearbeiten' : 'Neuen Eintrag anlegen' }}</h3>

        <form class="form" @submit.prevent="submitTimeEntry">
          <label class="field">
            <span>Projekt</span>
            <select v-model="form.projectId" required>
              <option value="">Bitte wählen</option>
              <option
                v-for="project in projects"
                :key="project.id"
                :value="project.id"
              >
                {{ project.name }}
              </option>
            </select>
          </label>

          <label class="field">
            <span>Startzeit</span>
            <input type="time" v-model="form.startTime" required />
          </label>

          <label class="field">
            <span>Endzeit</span>
            <input type="time" v-model="form.endTime" required />
          </label>

          <label class="field">
            <span>Notiz</span>
            <textarea v-model="form.note" rows="4" />
          </label>

          <div class="button-row">
            <button type="submit" :disabled="loading">
              {{ loading ? 'Speichert...' : isEditMode ? 'Eintrag aktualisieren' : 'Eintrag speichern' }}
            </button>

            <button
              v-if="isEditMode"
              type="button"
              class="button-secondary"
              @click="resetForm"
            >
              Abbrechen
            </button>
          </div>
        </form>

        <p v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </p>
      </section>

      <section class="card">
        <h3>Einträge am {{ selectedDate }}</h3>

        <p v-if="entriesLoading">Lade Einträge...</p>
        <p v-else-if="timeEntries.length === 0">Keine Einträge für diesen Tag vorhanden.</p>

        <ul v-else class="entry-list">
          <li v-for="entry in timeEntries" :key="entry.id" class="entry-item">
            <div class="entry-top">
              <div>
                <strong>{{ entry.projectName }}</strong>
                <p>{{ entry.startTime }} - {{ entry.endTime }}</p>
                <p>{{ entry.note || 'Keine Notiz' }}</p>
              </div>

              <div class="button-row">
                <button type="button" class="button-secondary" @click="editTimeEntry(entry)">
                  Bearbeiten
                </button>
                <button type="button" class="button-danger" @click="removeTimeEntry(entry.id)">
                  Löschen
                </button>
              </div>
            </div>
          </li>
        </ul>
      </section>
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
