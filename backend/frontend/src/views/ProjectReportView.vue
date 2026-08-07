<template>
  <div class="report-container">
    <div class="header-actions">
      <h1>Projekt-Auswertungen</h1>
      
      <!-- Filter-Bereich -->
      <div class="filters">
        <select v-model="selectedProject" @change="loadReportData">
          <option value="" disabled>Projekt wählen...</option>
          <option v-for="proj in projectStore.projects" :key="proj.id" :value="proj.id">
            {{ proj.name }}
          </option>
        </select>

        <!-- HTML5 Month-Picker generiert direkt das 'YYYY-MM' Format! -->
        <input type="month" v-model="selectedMonth" @change="loadReportData" />
      </div>
    </div>

    <!-- Status Anzeigen -->
    <div v-if="isLoading" class="loading-state">Lade Auswertungen...</div>
    <div v-else-if="error" class="error-box">{{ error }}</div>
    
    <!-- Wenn kein Projekt ausgewählt ist -->
    <div v-else-if="!selectedProject" class="empty-state">
      Bitte wähle ein Projekt aus, um den Report zu sehen.
    </div>

    <!-- Das Dashboard (Diagramm & Tabelle) -->
    <div v-else class="dashboard-grid">
      
      <!-- Chart.js Bereich -->
      <div class="chart-card">
        <h3>Auswertung: {{ chartMode === 'hours' ? 'Stunden' : 'Umsatz' }}</h3>
        <!-- Der Toggle für Epic 5 -->
          <div class="toggle-group" v-if="currentProject.hourlyRate > 0">
            <button 
              :class="{ active: chartMode === 'hours' }" 
              @click="chartMode = 'hours'">Stunden</button>
            <button 
              :class="{ active: chartMode === 'costs' }" 
              @click="chartMode = 'costs'">Kosten</button>
          </div>
        </div>
        <div class="chart-wrapper">
          <Bar v-if="reportData.length > 0" :data="chartData" :options="chartOptions" />
          <p v-else class="empty-text">Keine Zeiten in diesem Zeitraum gebucht.</p>
        </div>
      </div>

      <!-- Detail-Tabelle Bereich -->
      <div class="table-card">
        <h3>Detail-Übersicht</h3>
        <div class="export-buttons">
            <button @click="handleExport('excel')" class="btn-export excel" :disabled="isExporting">
              {{ isExporting ? 'Exportiert...' : 'Excel' }}
            </button>
            <button @click="handleExport('pdf')" class="btn-export pdf" :disabled="isExporting">
              {{ isExporting ? 'Exportiert...' : 'PDF' }}
            </button>
        </div>
        <table class="report-table" v-if="reportData.length > 0">
          <thead>
            <tr>
              <th>Mitarbeiter</th>
              <th class="text-right">Gesamtstunden</th>
              <th class="text-right" v-if="currentProject.hourlyRate > 0">Kosten</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in reportData" :key="row.userId">
              <td>{{ row.username }}</td>
              <td class="text-right">{{ row.totalHours.toFixed(2) }} h</td>
              <td class="text-right" v-if="currentProject.hourlyRate > 0">
                {{ formatCurrency(row.totalHours * currentProject.hourlyRate) }}
              </td>
            </tr>
          </tbody>
          <tfoot>
            <tr>
              <td><strong>Gesamt</strong></td>
              <td class="text-right"><strong>{{ totalProjectHours.toFixed(2) }} h</strong></td>
              <td class="text-right" v-if="currentProject.hourlyRate > 0">
                <strong>{{ formatCurrency(totalProjectCost) }}</strong>
              </td>
            </tr>
          </tfoot>
        </table>
        <p v-else class="empty-text">Keine Daten verfügbar.</p>
      </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useProjectStore } from '@/stores/projectStore'
import { fetchProjectHoursReportApi } from '@/api/projects'

import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'
import { Bar } from 'vue-chartjs'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

const projectStore = useProjectStore()

const selectedProject = ref('')
const selectedMonth = ref('') // Format: 'YYYY-MM'
const reportData = ref([])
const isLoading = ref(false)
const isExporting = ref(false) 
const error = ref(null)
const chartMode = ref('hours') // Mögliche Werte: 'hours' oder 'costs'

onMounted(() => {
  projectStore.loadProjects()
})

const loadReportData = async () => {
  if (!selectedProject.value) return

  isLoading.value = true
  error.value = null
  
  try {
    const response = await fetchProjectHoursReportApi(selectedProject.value, selectedMonth.value || null)
    reportData.value = response.data
  } catch (err) {
    error.value = err.response?.data?.message || 'Fehler beim Laden des Reports'
  } finally {
    isLoading.value = false
  }
}

const totalProjectHours = computed(() => {
  return reportData.value.reduce((sum, item) => sum + item.totalHours, 0)
})
// --- Epic 5: Finanz-Berechnungen ---

// 1. Holt das aktuell ausgewählte komplette Projekt aus dem Pinia Store
const currentProject = computed(() => {
  return projectStore.projects.find(p => p.id === selectedProject.value) || {}
})

// 2. Hilfsfunktion für saubere Währungsformatierung (z.B. "1.250,00 €")
const formatCurrency = (value) => {
  const currency = currentProject.value.currency || 'EUR'
  return new Intl.NumberFormat('de-DE', { 
    style: 'currency', 
    currency: currency 
  }).format(value)
}

// 3. Berechnet die Gesamtkosten des Projekts für den Footer
const totalProjectCost = computed(() => {
  const rate = currentProject.value.hourlyRate || 0
  return totalProjectHours.value * rate
})

// --- Anpassung des Charts für Epic 5 ---

const chartData = computed(() => {
  const isCostMode = chartMode.value === 'costs'
  const rate = currentProject.value.hourlyRate || 0

  return {
    labels: reportData.value.map(item => item.username),
    datasets: [
      {
        label: isCostMode ? 'Umsatz' : 'Stunden',
        // Farbwechsel: Blau für Stunden, Grün für Geld
        backgroundColor: isCostMode ? '#27ae60' : '#3498db',
        borderRadius: 4,
        data: reportData.value.map(item => {
          return isCostMode ? (item.totalHours * rate) : item.totalHours
        })
      }
    ]
  }
})

const chartOptions = computed(() => {
  const isCostMode = chartMode.value === 'costs'
  
  return {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: {
        callbacks: {
          label: (context) => {
            return isCostMode 
              ? formatCurrency(context.parsed.y) 
              : `${context.parsed.y} Stunden`
          }
        }
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        title: { 
          display: true, 
          text: isCostMode ? 'Umsatz' : 'Stunden (h)' 
        }
      }
    }
  }
})
const handleExport = async (type) => {
  if (!selectedProject.value) return
  isExporting.value = true
  error.value = null

  try {
    let response;
    
    // API Call basierend auf dem Typ
    if (type === 'excel') {
      response = await exportProjectExcelApi(selectedProject.value, selectedMonth.value || null)
    } else {
      response = await exportProjectPdfApi(selectedProject.value, selectedMonth.value || null)
    }

    triggerFileDownload(response)
    
  } catch (err) {
    // Da wir responseType: 'blob' nutzen, ist das Error-Objekt bei einem Backend-Fehler (z.B. 403) auch ein Blob!
    // Wir müssen es erst wieder zu Text konvertieren, um unsere saubere JSON-Fehlermeldung zu lesen.
    if (err.response?.data instanceof Blob) {
      const text = await err.response.data.text()
      const jsonError = JSON.parse(text)
      error.value = jsonError.message || 'Fehler beim Exportieren'
    } else {
      error.value = 'Fehler beim Exportieren'
    }
  } finally {
    isExporting.value = false
  }
}

// Die Hilfsfunktion, um den Blob als Datei zu speichern
const triggerFileDownload = (response) => {
  // 1. Dateinamen aus dem Header extrahieren (falls vorhanden)
  let filename = 'report_download'
  const disposition = response.headers['content-disposition']
  if (disposition && disposition.indexOf('filename=') !== -1) {
    // Regex, um den Namen zwischen den Anführungszeichen zu holen
    const matches = /filename="([^"]*)"/.exec(disposition)
    if (matches != null && matches[1]) {
      filename = matches[1]
    }
  }

  // 2. Blob in eine temporäre URL umwandeln
  const blob = new Blob([response.data], { type: response.headers['content-type'] })
  const url = window.URL.createObjectURL(blob)

  // 3. Unsichtbaren Link erstellen und Klick simulieren
  const link = document.createElement('a')
  link.href = url
  link.setAttribute('download', filename)
  document.body.appendChild(link)
  link.click()

  // 4. Aufräumen (verhindert Memory Leaks im Browser)
  link.parentNode.removeChild(link)
  window.URL.revokeObjectURL(url)
}
</script>

<style scoped>
.report-container { padding: 0; }
.header-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; }
.filters { display: flex; gap: 15px; }
.filters select, .filters input { padding: 8px 12px; border: 1px solid #ccc; border-radius: 4px; font-size: 1rem; }

.dashboard-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 25px;
}
@media (max-width: 900px) {
  .dashboard-grid { grid-template-columns: 1fr; }
}

.chart-card, .table-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}
.chart-wrapper {
  position: relative;
  height: 350px; /* Chart.js braucht eine feste Höhe des Parent-Containers */
  width: 100%;
  margin-top: 20px;
}

/* Tabellen-Styling */
.report-table { width: 100%; border-collapse: collapse; margin-top: 20px; }
.report-table th, .report-table td { padding: 12px; border-bottom: 1px solid #eee; text-align: left; }
.report-table th { background-color: #f8f9fa; font-weight: 600; }
.text-right { text-align: right !important; }
.report-table tfoot td { border-top: 2px solid #ddd; border-bottom: none; }

.empty-state, .empty-text { color: #7f8c8d; font-style: italic; text-align: center; padding: 20px 0; }
.error-box { background: #f8d7da; color: #721c24; padding: 15px; border-radius: 4px; margin-bottom: 20px; }

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}
.export-buttons {
  display: flex;
  gap: 10px;
}
.btn-export {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  font-size: 0.9em;
}
.btn-export.excel {
  background-color: #27ae60; /* Excel Grün */
  color: white;
}
.btn-export.pdf {
  background-color: #e74c3c; /* PDF Rot */
  color: white;
}
.btn-export:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toggle-group {
  display: flex;
  border: 1px solid #ccc;
  border-radius: 4px;
  overflow: hidden;
}

.toggle-group button {
  background: white;
  border: none;
  padding: 6px 12px;
  cursor: pointer;
  font-size: 0.9em;
  transition: all 0.2s;
}

.toggle-group button.active {
  background: #3498db;
  color: white;
  font-weight: bold;
}

/* Verhindert den Hover-Effekt auf dem aktiven Button */
.toggle-group button:not(.active):hover {
  background: #f1f1f1;
}
</style>