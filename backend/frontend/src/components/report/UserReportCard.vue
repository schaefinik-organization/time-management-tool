<template>
  <div class="user-report-card" v-if="userData && userData.user">
    <div class="user-report-card__header">
      👤 Zeiterfassung - Übersicht für: {{ userData.user.username }}
    </div>
    
    <div class="user-report-card__overview">
      Gesamtstunden: <strong>{{ userData.totalHoursOverall }}h</strong> |  
      Abrechenbar: <strong>{{ userData.billableHoursOverall }}h</strong> |  
      Nicht abrechenbar: <strong>{{ nonBillableTotal }}h</strong>
    </div>

    <div class="user-report-card__chart">
      <div class="user-report-card__chart__header">
        STUNDENVERTEILUNG NACH PROJEKT
      </div>
      
      <div class="user-report-card__chart__wrapper">
        <div class="user-report-card__chart__canvas">
          <canvas ref="chartCanvas"></canvas>
        </div>
        
        <div class="user-report-card__chart__data">
          <strong>[Legende]</strong><br>
          <div 
            v-for="project in calculatedProjects" 
            :key="project.projectId"
            class="legend-item"
          >
            <span :style="{ color: project.color }">■</span> 
            {{ project.projectName }} ({{ project.totalHours }}h - {{ project.percentage }}%)
          </div>
        </div>
      </div>
    </div>

    <div class="user-report-card__project">
      <div class="user-report-card__project__header">
        Projektdetails
      </div>
      <div class="user-report-card__project__table">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Projektname</th>
              <th>Stunden</th>
              <th>Abrechenbar</th>
              <th>Einträge</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="project in userData.projects" :key="project.projectId">
              <td>{{ project.projectId }}</td>
              <td>{{ project.projectName }}</td>
              <td>{{ project.totalHours }}</td>
              <td>{{ project.billableHours }}</td>
              <td>{{ project.entryCount }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div class="user-report-card__action">
        <button @click="downloadPdf">📄 Bericht als PDF laden</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, onBeforeUnmount } from 'vue';
import Chart from 'chart.js/auto';

const props = defineProps({
  userData: {
    type: Object,
    required: true
  }
});

const chartCanvas = ref(null);
let chartInstance = null;

const chartColors = ['#36A2EB', '#FF6384', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'];

const nonBillableTotal = computed(() => {
  if (!props.userData) return 0;
  return props.userData.totalHoursOverall - props.userData.billableHoursOverall;
});

const calculatedProjects = computed(() => {
  if (!props.userData || !props.userData.projects) return [];
  const total = props.userData.totalHoursOverall || 1;
  
  return props.userData.projects.map((project, index) => ({
    ...project,
    percentage: ((project.totalHours / total) * 100).toFixed(1),
    color: chartColors[index % chartColors.length]
  }));
});

const initChart = () => {
  if (!chartCanvas.value || !props.userData) return;

  if (chartInstance) {
    chartInstance.destroy();
  }

  const ctx = chartCanvas.value.getContext('2d');
  
  const labels = calculatedProjects.value.map(p => p.projectName);
  const data = calculatedProjects.value.map(p => p.totalHours);
  const backgroundColors = calculatedProjects.value.map(p => p.color);

  chartInstance = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: labels,
      datasets: [{
        data: data,
        backgroundColor: backgroundColors,
        hoverOffset: 6
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          callbacks: {
            label: function(context) {
              return ` ${context.label}: ${context.raw} Stunden`;
            }
          }
        }
      }
    }
  });
};

watch(() => props.userData, () => {
  initChart();
}, { deep: true });

onMounted(() => {
  initChart();
});

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.destroy();
  }
});

const downloadPdf = () => {
  alert(`PDF Generierung für ${props.userData.user.username} wird gestartet...`);
};
</script>

<style scoped>
.user-report-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 24px;
  max-width: 800px;
  background-color: #ffffff;
  font-family: Arial, sans-serif;
  color: #333;
}

.user-report-card__header {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 12px;
  color: #2c3e50;
}

.user-report-card__overview {
  background: #f8f9fa;
  padding: 12px 16px;
  border-radius: 6px;
  margin-bottom: 24px;
  border-left: 4px solid #36A2EB;
}

.user-report-card__chart__header,
.user-report-card__project__header {
  font-size: 1.1rem;
  font-weight: bold;
  margin-bottom: 16px;
  text-transform: uppercase;
  color: #666;
}

.user-report-card__chart__wrapper {
  display: flex;
  align-items: center;
  gap: 40px;
  margin-bottom: 32px;
}

.user-report-card__chart__canvas {
  width: 200px;
  height: 200px;
  position: relative;
}

.legend-item {
  margin-bottom: 4px;
}

.user-report-card__project__table table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 24px;
}

.user-report-card__project__table th {
  background-color: #f1f3f5;
  text-align: left;
  padding: 10px;
  border-bottom: 2px solid #dee2e6;
}

.user-report-card__project__table td {
  padding: 10px;
  border-bottom: 1px solid #e9ecef;
}

.user-report-card__action button {
  background-color: #343a40;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s ease;
}

.user-report-card__action button:hover {
  background-color: #495057;
}
</style>