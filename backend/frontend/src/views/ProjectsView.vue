<template>
  <section class="page">
    <div class="page-header">
      <div>
        <h2>Projekte</h2>
        <p>Projekte anlegen, bearbeiten und löschen.</p>
      </div>
    </div>

    <div class="grid">
      <section class="card">
        <h3>{{ isEditMode ? 'Projekt bearbeiten' : 'Projekt anlegen' }}</h3>

        <form class="form" @submit.prevent="submitProject">
          <label class="field">
            <span>Name</span>
            <input v-model="form.name" type="text" required />
          </label>

          <label class="field">
            <span>Beschreibung</span>
            <textarea v-model="form.description" rows="4" />
          </label>

          <div class="button-row">
            <button type="submit" :disabled="loading">
              {{ loading ? 'Speichert...' : isEditMode ? 'Projekt aktualisieren' : 'Projekt speichern' }}
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
        <h3>Projektliste</h3>

        <p v-if="projects.length === 0">Noch keine Projekte vorhanden.</p>

        <ul v-else class="entry-list">
          <li v-for="project in projects" :key="project.id" class="entry-item">
            <div class="entry-top">
              <div>
                <strong>{{ project.name }}</strong>
                <p>{{ project.description || 'Keine Beschreibung' }}</p>
              </div>

              <div class="button-row">
                <button type="button" class="button-secondary" @click="editProject(project)">
                  Bearbeiten
                </button>
                <button type="button" class="button-danger" @click="removeProject(project.id)">
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
import { computed, onMounted, reactive, ref } from 'vue'
import { createProject, deleteProject, fetchProjects, updateProject } from '../api/projects'

const projects = ref([])
const loading = ref(false)
const errorMessage = ref('')
const editingId = ref(null)

const form = reactive({
  name: '',
  description: ''
})

const isEditMode = computed(() => editingId.value !== null)

async function loadProjects() {
  const response = await fetchProjects()
  projects.value = response.data
}

function editProject(project) {
  editingId.value = project.id
  form.name = project.name
  form.description = project.description || ''
}

function resetForm() {
  editingId.value = null
  form.name = ''
  form.description = ''
  errorMessage.value = ''
}

async function submitProject() {
  loading.value = true
  errorMessage.value = ''

  try {
    const payload = {
      name: form.name,
      description: form.description
    }

    if (isEditMode.value) {
      await updateProject(editingId.value, payload)
    } else {
      await createProject(payload)
    }

    resetForm()
    await loadProjects()
  } catch (error) {
    errorMessage.value =
      error?.response?.data?.message ||
      'Projekt konnte nicht gespeichert werden.'
  } finally {
    loading.value = false
  }
}

async function removeProject(id) {
  const confirmed = window.confirm('Projekt wirklich löschen?')
  if (!confirmed) return

  try {
    await deleteProject(id)
    if (editingId.value === id) {
      resetForm()
    }
    await loadProjects()
  } catch (error) {
    errorMessage.value =
      error?.response?.data?.message ||
      'Projekt konnte nicht gelöscht werden.'
  }
}

onMounted(async () => {
  await loadProjects()
})
</script>
