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
    } catch (error) {
      const apiData = error.response?.data
      errorMessage.value = apiData?.message || 'Ein unerwarteter Fehler ist aufgetreten.'
      validationErrors.value = apiData?.validationErrors || {}
      return { success: false, error }
    } finally {
      loading.value = false
    }
  }

  return { loading, errorMessage, validationErrors, handleAction }
}