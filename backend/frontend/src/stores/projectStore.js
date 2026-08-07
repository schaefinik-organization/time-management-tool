import { defineStore } from 'pinia'
import { ref } from 'vue'
import { fetchManagedProjectsApi, createProjectApi } from '@/api/projects'

export const useProjectStore = defineStore('project', () => {
  // --- State ---
  const projects = ref([])
  const isLoading = ref(false)
  const error = ref(null)

  // --- Actions ---
  
  const loadProjects = async () => {
    isLoading.value = true
    error.value = null
    try {
      const response = await fetchManagedProjectsApi()
      projects.value = response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Fehler beim Laden der Projekte.'
    } finally {
      isLoading.value = false
    }
  }

  const addProject = async (projectData) => {
    try {
      const response = await createProjectApi(projectData)
      projects.value.push(response.data) 
      return { success: true }
    } catch (err) {
      return { 
        success: false, 
        message: err.response?.data?.message,
        validationErrors: err.response?.data?.validationErrors || {}
      }
    }
  }

  return { 
    projects, 
    isLoading, 
    error, 
    loadProjects, 
    addProject 
  }
})