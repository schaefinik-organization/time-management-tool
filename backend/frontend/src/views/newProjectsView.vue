<template>
  <section class="space-y-6">
    <div class="flex flex-col gap-2">
      <h1 class="text-2xl font-semibold">Projekte</h1>
      <p class="text-muted">Projekte anlegen, bearbeiten und löschen.</p>
    </div>

    <div class="grid gap-6 xl:grid-cols-2">
      <BaseCard>
        <div class="space-y-5">
          <div>
            <h2 class="text-lg font-semibold">
              {{ isEditMode ? 'Projekt bearbeiten' : 'Projekt anlegen' }}
            </h2>
            <p class="mt-1 text-sm text-muted">
              Verwalte deine Projektbasis für spätere Zeiteinträge.
            </p>
          </div>

          <form class="space-y-4" @submit.prevent="submitProject">
            <div class="space-y-2">
              <label class="text-sm font-medium">Name</label>
              <BaseInput
                v-model="form.name"
                type="text"
                placeholder="z. B. Kundenprojekt Alpha"
                required
              />
            </div>

            <div class="space-y-2">
              <label class="text-sm font-medium">Beschreibung</label>
              <BaseTextarea
                v-model="form.description"
                :rows="4"
                placeholder="Optionale Projektbeschreibung"
              />
            </div>

            <div class="flex flex-wrap gap-3">
              <BaseButton type="submit" :disabled="loading">
                {{ loading ? 'Speichert...' : isEditMode ? 'Projekt aktualisieren' : 'Projekt speichern' }}
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
              <h2 class="text-lg font-semibold">Projektliste</h2>
              <p class="mt-1 text-sm text-muted">
                Vorhandene Projekte im System.
              </p>
            </div>

            <span
              class="rounded-full px-3 py-1 text-xs font-medium"
              :style="{ background: 'var(--bg-muted)', color: 'var(--text-muted)' }"
            >
              {{ projects.length }} Projekte
            </span>
          </div>

          <div
            v-if="projects.length === 0"
            class="rounded-2xl border border-dashed p-6 text-sm text-muted"
            :style="{ borderColor: 'var(--border)' }"
          >
            Noch keine Projekte vorhanden.
          </div>

          <ul v-else class="space-y-3">
            <li
              v-for="project in projects"
              :key="project.id"
              class="rounded-2xl border p-4"
              :style="{ borderColor: 'var(--border)', background: 'var(--surface-soft)' }"
            >
              <div class="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
                <div class="space-y-1">
                  <p class="font-semibold">{{ project.name }}</p>
                  <p class="text-sm text-muted">
                    {{ project.description || 'Keine Beschreibung' }}
                  </p>
                </div>

                <div class="flex flex-wrap gap-2">
                  <BaseButton variant="secondary" @click="editProject(project)">
                    Bearbeiten
                  </BaseButton>
                  <BaseButton variant="danger" @click="removeProject(project.id)">
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
import { computed, onMounted, reactive, ref } from 'vue'
import { createProject, deleteProject, fetchProjects, updateProject } from '../api/projects'
import BaseButton from '../components/ui/BaseButton.vue'
import BaseCard from '../components/ui/BaseCard.vue'
import BaseInput from '../components/ui/BaseInput.vue'
import BaseTextarea from '../components/ui/BaseTextarea.vue'

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
