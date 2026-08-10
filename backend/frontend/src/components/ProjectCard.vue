<template>
  <div class="card">
    <div class="card-header">
      <h3>{{ project.name }}</h3>
      <span :class="['status-badge', project.active ? 'active' : 'archived']">
        {{ project.active ? 'Aktiv' : 'Archiviert' }}
      </span>
    </div>
    
    <p class="description">{{ project.description || 'Keine Beschreibung' }}</p>

    <div class="finances" v-if="project.internalHourlyRate">
        Stundensatz: {{ formatCurrency(project.internalHourlyRate, project.currency) }}
    </div>

    <div class="finances" v-if="project.hourlyRate">
        Stundensatz: {{ formatCurrency(project.hourlyRate, project.currency) }}
    </div>

    <div class="actions">
      <button class="btn-secondary" @click="$emit('edit', project)">Bearbeiten</button>
      <router-link class="btn-secondary" :to="`/reports/${project.id}`">Berichte</router-link>
      <button v-if="isActive" class="btn-secondary" @click="$emit('archive', project)">Archivieren</button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  project: {
    type: Object,
    required: true
  }
})

defineEmits(['edit', 'archive'])

const isActive = computed(() => props.project.active)

const formatCurrency = (amount, currency) => {
  return new Intl.NumberFormat('de-DE', { style: 'currency', currency: currency }).format(amount)
}
</script>

<style scoped>
.card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  background: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.card-header h3 {
  margin: 0 0 10px 0;
}
.status-badge {
  font-size: 0.8em;
  padding: 3px 8px;
  border-radius: 12px;
}
.active { background: #d4edda; color: #155724; }
.archived { background: #e2e3e5; color: #383d41; }
.description {
  color: #666;
  font-size: 0.9em;
  min-height: 40px;
}
.actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}
</style>