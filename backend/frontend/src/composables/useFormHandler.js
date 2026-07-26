import { ref } from 'vue'

export function useFormHandler() {
  const loading = ref(false)
  const errorMessage = ref('')
  const validationErrors = ref({})

  async function handleAction(actionFn, successCallback) {
    loading.value = true
    errorMessage.value = ''
    validationErrors.value = {}

    try {
      const response = await actionFn()
      if (successCallback) await successCallback(response)
      return { success: true, data: response?.data }
   // ... im catch-Block
} catch (error) {
  const apiData = error.response?.data;
  // Priorität: 
  // 1. Die spezifische Nachricht vom Backend (z.B. "Ihr Account wurde deaktiviert")
  // 2. Fallback-Nachricht
  errorMessage.value = apiData?.message || 'Verbindung zum Server fehlgeschlagen.';
  validationErrors.value = apiData?.validationErrors || {};
  return { success: false, error };
}finally {
      loading.value = false 
    }
  }

  return { loading, errorMessage, validationErrors, handleAction }
}
