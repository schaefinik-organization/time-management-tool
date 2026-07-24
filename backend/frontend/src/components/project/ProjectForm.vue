<template>
  <form @submit.prevent="saveProject">
    <div>
      <input v-model="form.name" type="text" placeholder="Projektname" />
      <!-- Fehlermeldung für den Namen anzeigen -->
      <span v-if="errors.name" class="error-text">{{ errors.name }}</span>
    </div>

    <button type="submit">Speichern</button>
    
    <p v-if="generalError" class="error-text">{{ generalError }}</p>
  </form>
</template>

<script setup>
import { ref } from 'vue';
import api from '@/api/axios';

const form = ref({ name: '' });
const errors = ref({}); // Hier landen die Feld-Fehler
const generalError = ref('');

const saveProject = async () => {
  errors.value = {}; // Reset vor dem Absenden
  generalError.value = '';

  try {
    await api.post('/projects', form.value);
    alert('Projekt erfolgreich erstellt!');
  } catch (error) {
    if (error.response && error.response.status === 400) {
      // 1. Validierungsfehler vom Backend holen
      const apiData = error.response.data;
      if (apiData.validationErrors) {
        errors.value = apiData.validationErrors; // { name: "darf nicht leer sein" }
      }
      generalError.value = apiData.message;
    } else {
      generalError.value = "Ein unerwarteter Fehler ist aufgetreten.";
    }
  }
};
</script>

<style>
.error-text { color: red; font-size: 0.8rem; }
</style>