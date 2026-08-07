import { defineStore } from 'pinia'
import { ref } from 'vue'
import { fetchMyTimeEntriesApi, createTimeEntryApi, deleteTimeEntryApi } from '@/api/timeEntries'

export const useTimeEntryStore = defineStore('timeEntry', () => {
  const entries = ref([])
  const isLoading = ref(false)
  const error = ref(null)

  const loadMyEntries = async () => {
    isLoading.value = true
    try {
      const response = await fetchMyTimeEntriesApi()
      entries.value = response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Fehler beim Laden der Zeiten'
    } finally {
      isLoading.value = false
    }
  }

  const addEntry = async (payload) => {
    try {
      const response = await createTimeEntryApi(payload)
      entries.value.push(response.data)
      return { success: true }
    } catch (err) {
      return { 
        success: false, 
        message: err.response?.data?.message,
        validationErrors: err.response?.data?.validationErrors || {}
      }
    }
  }

  const removeEntry = async (id) => {
    try {
      await deleteTimeEntryApi(id)
      entries.value = entries.value.filter(entry => entry.id !== id)
    } catch (err) {
      console.error('Fehler beim Löschen', err)
    }
  }

  return { entries, isLoading, error, loadMyEntries, addEntry, removeEntry }
})