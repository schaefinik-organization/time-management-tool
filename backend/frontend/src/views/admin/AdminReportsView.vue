<template>
  <section class="space-y-6">
    <BaseCard>
      <div class="grid gap-4 md:grid-cols-3">
        <BaseInput v-model="filters.start" type="date" label="Von" />
        <BaseInput v-model="filters.end" type="date" label="Bis" />
        <div class="flex items-end">
          <BaseButton @click="fetchReportData" class="w-full">Bericht generieren</BaseButton>
        </div>
      </div>
    </BaseCard>

    <BaseCard v-if="reportData.length">
      <table class="w-full text-sm">
        <thead class="bg-gray-50">
          <tr>
            <th class="p-4 text-left">Benutzer</th>
            <th class="p-4 text-left">Projekt</th>
            <th class="p-4 text-right">Gesamtstunden</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in reportData" :key="row.id" class="border-t">
            <td class="p-4">{{ row.username }}</td>
            <td class="p-4">{{ row.projectName }}</td>
            <td class="p-4 text-right font-mono">{{ formatHours(row.totalMinutes) }} h</td>
          </tr>
        </tbody>
      </table>
    </BaseCard>
  </section>
</template>


<script setup>
import { ref, computed, onMounted, reactive } from 'vue';
import { fetchReportDataApi } from '@/api/admin/reports';
import BaseButton from '@/components/ui/BaseButton.vue';
import BaseCard from '@/components/ui/BaseCard.vue';
import BaseInput from '@/components/ui/BaseInput.vue';
import BaseSelect from '@/components/ui/BaseSelect.vue';
import AppError from '@/components/ui/AppError.vue';

const reportData = ref([]);
const filters = ref({
  start: '',
  end: ''
});
const loading = ref(false);
const globalError = ref('');
const showModal = ref(false);

const fetchReportData = async () => {
  loading.value = true;
  globalError.value = '';
  try {
    const response = await fetchReportDataApi(filters.value.start, filters.value.end);
    reportData.value = response.data;
  } catch (err) {
    globalError.value = 'Bericht konnte nicht geladen werden.';
  } finally {
    loading.value = false;
  }
};

const formatHours = (minutes) => {
  console.log('Formatting minutes:', minutes);
  const hours = (minutes / 60).toFixed(2);
  console.log('Formatted hours:', hours);
  return hours;
  };
</script>