<template>
  <div class="report-container">
    <div v-if="isLoading" class="loading-state">Lade Auswertungen...</div>
    <div v-else-if="error" class="error-box">{{ error }}</div>
    <div v-else-if="hasLoadedData" class="dashboard-grid">
      <UserReportCard :userData="report"/>
    </div>
  </div>
</template>

<script setup>
import UserReportCard from '@/components/report/UserReportCard.vue'
import { ref, computed, onMounted } from 'vue'
import { fetchReport } from '@/api/user/reports'

const selectedMonth = ref('')
const report = ref(null)
const isLoading = ref(false)
const error = ref(null)

onMounted(() => {
  loadReportData();
})

const hasLoadedData = computed(() => {
  return report.value != null;
  })

const loadReportData = async () => {
  isLoading.value = true
  error.value = null

  try {
    const response = await fetchReport(selectedMonth.value || null)
    report.value = response.data
  } catch (err) {
    error.value = err.response?.data?.message || 'Fehler beim Laden des Reports'
  } finally {
    isLoading.value = false
  }
}
</script>
