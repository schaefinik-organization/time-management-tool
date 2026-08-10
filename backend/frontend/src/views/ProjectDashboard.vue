<template>
  <div>
    <div class="header-actions">
      <h1>Projektverwaltung als Manager</h1>
      <button class="btn-primary" @click="isModalOpen = true">Neues Projekt</button>
    </div>

    <div v-if="projectStore.isLoading">Projekte werden geladen...</div>
    <div v-else-if="projectStore.error" class="error-box">{{ projectStore.error }}</div>

    <div v-else class="project-grid">
      <ProjectCard 
        v-for="project in projectStore.projects" 
        :key="project.id" 
        :project="project" 
        @edit="openChangeModal(project)"
        @archive="archiveProject(project)"
      />
    </div>

    <BaseModal 
      :is-open="isModalOpen" 
      title="Neues Projekt anlegen" 
      @close="closeModal"
    >
      <form id="createProjectForm" @submit.prevent="doAction">
        <div class="form-group">
          <label>Projektname</label>
          <input v-model="formData.name" type="text" required />
          <span class="error-text" v-if="validationErrors.name">{{ validationErrors.name }}</span>
        </div>
        
        <div class="form-group">
          <label>Beschreibung</label>
          <textarea v-model="formData.description" rows="3"></textarea>
        </div>

        <div class="form-row">
          <div class="form-group">
                <label>Interner Stundensatz</label>
                <input v-model="formData.internalHourlyRate" type="number" step="0.01" min="0" required />
                <span class="error-text" v-if="validationErrors.internalHourlyRate">{{ validationErrors.internalHourlyRate }}</span>
          </div>

          <div class="form-group">
            <label>Stundensatz</label>
            <input v-model="formData.hourlyRate" type="number" step="0.01" min="0" required />
            <span class="error-text" v-if="validationErrors.hourlyRate">{{ validationErrors.hourlyRate }}</span>
          </div>
          
          <div class="form-group">
            <label>Währung</label>
            <select v-model="formData.currency">
              <option value="EUR">EUR</option>
              <option value="USD">USD</option>
              <option value="CHF">CHF</option>
            </select>
          </div>
        </div>
       <div class="form-row">
         <div class="form-group">
          <label>Projekt-Team</label>
    
          <UserMultiSelect 
            v-model="formData.assignedUserIds" 
            :available-options="userStore.subordinates"
            placeholder="Namen tippen..."
          />
    
  </div>
      </div>
      </form>

      <!-- Slot: Footer (Die Buttons) -->
      <template #footer>
        <button type="button" class="btn-secondary" @click="closeModal">Abbrechen</button>
        <!-- form="createProjectForm" triggert den Submit des Formulars im Body-Slot -->
        <button type="submit" form="createProjectForm" class="btn-primary" :disabled="isSubmitting">
          {{ isSubmitting ? 'Speichern...' : (isEditMode ? 'Projekt aktualisieren' : 'Projekt speichern') }}
        </button>
      </template>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useProjectStore } from '@/stores/projectStore'
import { useUserStore } from '@/stores/userStore'
import ProjectCard from '@/components/ProjectCard.vue'
import BaseModal from '@/components/BaseModal.vue'
import UserMultiSelect from '@/components/UserMultiSelect.vue'

const projectStore = useProjectStore()
const userStore = useUserStore()

const isModalOpen = ref(false)
const isSubmitting = ref(false)
const isEditMode = ref(false)
const editingProjectId = ref(null)
const validationErrors = ref({})

const formData = reactive({
  name: '',
  description: '',
  hourlyRate: 0.00,
  currency: 'EUR',
  assignedUserIds: []
})

onMounted(() => {
  projectStore.loadManagedProjects()
  userStore.loadMyEmployees()
})

const closeModal = () => {
  isModalOpen.value = false
  isEditMode.value = false
  editingProjectId.value = null
  isSubmitting.value = false
  validationErrors.value = {}
  Object.assign(formData, { name: '', description: '', internalHourlyRate: 0.00, hourlyRate: 0.00, currency: 'EUR' })
}

const openChangeModal = (project) => {
  isModalOpen.value = true
  isEditMode.value = true
  editingProjectId.value = project.id
  Object.assign(formData, { 
    name: project.name, 
    description: project.description,
    internalHourlyRate: project.internalHourlyRate,
    hourlyRate: project.hourlyRate, 
    currency: project.currency,
    assignedUserIds: project.assignedUserIds
  })
}

const doAction = async() => {
  isSubmitting.value = true
  validationErrors.value = {}

  let result
  if (isEditMode.value) {
    result = await projectStore.updateProject(editingProjectId.value, formData)
  } else {
    result = await projectStore.addProject(formData)
  }

  if (result.success) {
    closeModal()
  } else {
    validationErrors.value = result.validationErrors
  }
}

const submitProject = async () => {
  isSubmitting.value = true
  validationErrors.value = {} 

  const result = await projectStore.addProject(formData)

  if (result.success) {
    closeModal()
  } else {
    validationErrors.value = result.validationErrors
  }
  
  isSubmitting.value = false
}

const updateProject = async (projectId, updatedData) => {
  isSubmitting.value = true
  validationErrors.value = {}

  const result = await projectStore.updateProject(projectId, updatedData)

  if (!result.success) {
    validationErrors.value = result.validationErrors
  }
  isSubmitting.value = false
}

const archiveProject = async (project) => {
  const confirmed = confirm('Möchten Sie dieses Projekt wirklich archivieren?')
  if (!confirmed) return

  isSubmitting.value = true
  const result = await projectStore.archiveProject(project.id)

  if (!result.success) {
    alert('Fehler beim Archivieren des Projekts.')
  }
  isSubmitting.value = false
}
</script>

<style scoped>
.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}
.btn-primary {
  background-color: #3498db;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.error-box {
  background-color: #f8d7da;
  color: #721c24;
  padding: 15px;
  border-radius: 4px;
}
.form-group { margin-bottom: 15px; display: flex; flex-direction: column; }
.form-group label { margin-bottom: 5px; font-weight: bold; }
.form-group input, .form-group textarea, .form-group select { padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
.form-row { display: flex; gap: 15px; }
.form-row .form-group { flex: 1; }
.error-text { color: #dc3545; font-size: 0.85em; margin-top: 5px; }
.btn-secondary { background-color: #6c757d; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; }

</style>