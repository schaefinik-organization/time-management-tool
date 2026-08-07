<template>
  <div>
    <div class="header-actions">
      <h1>Projektverwaltung</h1>
      <button class="btn-primary" @click="isModalOpen = true">Neues Projekt</button>
    </div>

    <!-- Pinia State anzeigen -->
    <div v-if="projectStore.isLoading">Projekte werden geladen...</div>
    <div v-else-if="projectStore.error" class="error-box">{{ projectStore.error }}</div>

    <div v-else class="project-grid">
      <ProjectCard 
        v-for="project in projectStore.projects" 
        :key="project.id" 
        :project="project" 
      />
    </div>

    <!-- Das wiederverwendbare Modal nutzen -->
    <BaseModal 
      :is-open="isModalOpen" 
      title="Neues Projekt anlegen" 
      @close="closeModal"
    >
      <!-- Slot: Body (Das Formular) -->
      <form id="createProjectForm" @submit.prevent="submitProject">
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
      </form>

      <!-- Slot: Footer (Die Buttons) -->
      <template #footer>
        <button type="button" class="btn-secondary" @click="closeModal">Abbrechen</button>
        <!-- form="createProjectForm" triggert den Submit des Formulars im Body-Slot -->
        <button type="submit" form="createProjectForm" class="btn-primary" :disabled="isSubmitting">
          {{ isSubmitting ? 'Speichern...' : 'Projekt anlegen' }}
        </button>
      </template>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useProjectStore } from '@/stores/projectStore'
import ProjectCard from '@/components/ProjectCard.vue'
import BaseModal from '@/components/BaseModal.vue'

const projectStore = useProjectStore()

const isModalOpen = ref(false)
const isSubmitting = ref(false)
const validationErrors = ref({})

const formData = reactive({
  name: '',
  description: '',
  hourlyRate: 0.00,
  currency: 'EUR'
})

onMounted(() => {
  projectStore.loadProjects()
})

const closeModal = () => {
  isModalOpen.value = false
  validationErrors.value = {}
  Object.assign(formData, { name: '', description: '', hourlyRate: 0.00, currency: 'EUR' })
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