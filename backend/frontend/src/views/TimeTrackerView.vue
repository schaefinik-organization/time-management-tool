<template>
  <div class="time-tracker-container">
    
    <section class="form-section">
      <h2>Zeit buchen</h2>
      
      <div v-if="globalError" class="error-alert">{{ globalError }}</div>
      
      <form @submit.prevent="submitTimeEntry" class="tracker-form">
        <div class="form-group">
          <label>Projekt</label>
          <select v-model="formData.projectId" required>
            <option value="" disabled>Bitte wählen...</option>
            <option v-for="proj in projectStore.projects" :key="proj.id" :value="proj.id">
              {{ proj.name }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label>Datum</label>
          <input v-model="formData.date" type="date" required />
        </div>

        <div class="time-row">
          <div class="form-group">
            <label>Startzeit</label>
            <input v-model="formData.startTime" type="time" required />
          </div>
          <div class="form-group">
            <label>Endzeit</label>
            <input v-model="formData.endTime" type="time" required />
          </div>
        </div>
        
        <div v-if="isCrossMidnight" class="info-alert">
          ℹ️ Endzeit liegt am Folgetag.
        </div>

        <div class="form-group">
          <label>Beschreibung (Optional)</label>
          <textarea v-model="formData.description" rows="2"></textarea>
        </div>

        <button type="submit" class="btn-primary" :disabled="isSubmitting">
          {{ isSubmitting ? 'Wird gespeichert...' : 'Zeit speichern' }}
        </button>
      </form>
    </section>

    <section class="list-section">
      <h2>Meine Buchungen</h2>
      
      <div v-if="timeEntryStore.isLoading">Lade Zeiten...</div>
      
      <div v-else class="entries-list">
        <div v-for="entry in timeEntryStore.entries" :key="entry.id" class="entry-card">
          <div class="entry-header">
            <strong>{{ entry.projectName }}</strong>
            <button class="btn-delete" @click="timeEntryStore.removeEntry(entry.id)">&times;</button>
          </div>
          <div class="entry-details">
            <span>{{ formatDateTime(entry.startTime) }} - {{ formatTimeOnly(entry.endTime) }}</span>
            <p v-if="entry.description" class="entry-desc">{{ entry.description }}</p>
          </div>
        </div>
        
        <div v-if="timeEntryStore.entries.length === 0" class="empty-state">
          Noch keine Zeiten gebucht.
        </div>
      </div>
    </section>
    
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useProjectStore } from '@/stores/projectStore'
import { useTimeEntryStore } from '@/stores/timeEntryStore'

const projectStore = useProjectStore()
const timeEntryStore = useTimeEntryStore()

const isSubmitting = ref(false)
const globalError = ref(null)

const today = new Date().toISOString().split('T')[0]

const formData = reactive({
  projectId: '',
  date: today,
  startTime: '08:00',
  endTime: '16:00',
  description: ''
})

onMounted(() => {
  projectStore.loadProjects()
  timeEntryStore.loadMyEntries()
})

const isCrossMidnight = computed(() => {
  if (!formData.startTime || !formData.endTime) return false
  return formData.endTime <= formData.startTime
})

const buildBackendPayload = () => {
  const startDateTime = `${formData.date}T${formData.startTime}:00`
  
  let endDateTime = ''
  if (isCrossMidnight.value) {
    const endDate = new Date(formData.date)
    endDate.setDate(endDate.getDate() + 1)
    
    const yyyy = endDate.getFullYear()
    const mm = String(endDate.getMonth() + 1).padStart(2, '0')
    const dd = String(endDate.getDate()).padStart(2, '0')
    
    endDateTime = `${yyyy}-${mm}-${dd}T${formData.endTime}:00`
  } else {
    endDateTime = `${formData.date}T${formData.endTime}:00`
  }

  return {
    projectId: formData.projectId,
    startTime: startDateTime,
    endTime: endDateTime,
    description: formData.description
  }
}

const submitTimeEntry = async () => {
  isSubmitting.value = true
  globalError.value = null

  const payload = buildBackendPayload()
  
  const result = await timeEntryStore.addEntry(payload)

  if (result.success) {
    formData.startTime = formData.endTime
    formData.description = ''
  } else {
    globalError.value = result.message || 'Ein Fehler ist aufgetreten.'
  }
  
  isSubmitting.value = false
}

const formatDateTime = (isoString) => {
  const date = new Date(isoString)
  return date.toLocaleString('de-DE', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const formatTimeOnly = (isoString) => {
  const date = new Date(isoString)
  return date.toLocaleTimeString('de-DE', { hour: '2-digit', minute: '2-digit' })
}
</script>

<style scoped>
.time-tracker-container {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 30px;
}
.form-section, .list-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}
.time-row {
  display: flex;
  gap: 15px;
}
.time-row .form-group {
  flex: 1;
}
.form-group {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}
.form-group label { margin-bottom: 5px; font-weight: bold; font-size: 0.9em; }
.form-group input, .form-group select, .form-group textarea {
  padding: 8px; border: 1px solid #ccc; border-radius: 4px;
}
.error-alert { background: #f8d7da; color: #721c24; padding: 10px; border-radius: 4px; margin-bottom: 15px; }
.info-alert { background: #e2e3e5; color: #383d41; padding: 10px; border-radius: 4px; margin-bottom: 15px; font-size: 0.9em; }
.btn-primary { background: #3498db; color: white; padding: 10px; border: none; border-radius: 4px; cursor: pointer; width: 100%; }

/* Styling der Liste */
.entries-list { display: flex; flex-direction: column; gap: 10px; }
.entry-card { border-left: 4px solid #3498db; background: #f8f9fa; padding: 12px; border-radius: 4px; }
.entry-header { display: flex; justify-content: space-between; margin-bottom: 5px; }
.btn-delete { background: none; border: none; color: #dc3545; font-size: 1.2rem; cursor: pointer; }
.entry-desc { color: #666; font-size: 0.9em; margin: 5px 0 0 0; font-style: italic; }
.empty-state { text-align: center; color: #999; padding: 20px; }
</style>