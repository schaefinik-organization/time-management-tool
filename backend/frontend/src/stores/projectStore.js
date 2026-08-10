import {defineStore} from 'pinia'
import {ref} from 'vue'
import {
    archiveProjectApi,
    createProjectApi,
    fetchAssignedProjectsApi,
    fetchManagedProjectsApi,
    updateProjectApi
} from '@/api/projects'

export const useProjectStore = defineStore('project', () => {
    // --- State ---
    const projects = ref([])
    const isLoading = ref(false)
    const error = ref(null)

    // --- Actions ---

    const loadAssignedProjects = async () => {
        isLoading.value = true
        error.value = null
        try {
            const response = await fetchAssignedProjectsApi()
            projects.value = response.data
        } catch (err) {
            error.value = err.response?.data?.message || 'Fehler beim Laden der Projekte.'
        } finally {
            isLoading.value = false
        }
    }

    const loadManagedProjects = async () => {
        isLoading.value = true
        error.value = null
        try {
            const response = await fetchManagedProjectsApi()
            projects.value = response.data
            console.log(response.data);
        } catch (err) {
            error.value = err.response?.data?.message || 'Fehler beim Laden der Projekte.'
        } finally {
            isLoading.value = false
        }
    }

    const addProject = async (projectData) => {
        try {
            await createProjectApi(projectData)
            return {success: true}
        } catch (err) {
            return {
                success: false,
                message: err.response?.data?.message,
                validationErrors: err.response?.data?.validationErrors || {}
            }
        } finally {
          loadManagedProjects() 
        }
    }

    const updateProject = async (projectId, updatedData) => {
        try {
            await updateProjectApi(projectId, updatedData);
            return {success: true}
        } catch (err) {
            return {
                success: false,
                message: err.response?.data?.message,
                validationErrors: err.response?.data?.validationErrors || {}
            }
        } finally {
          loadManagedProjects() 
        }
    }

    const archiveProject = async (projectId) => {
        try {
            await archiveProjectApi(projectId);
            return {success: true}
        } catch (err) {
            return {
                success: false,
                message: err.response?.data?.message
            }
        } finally {
          loadManagedProjects()
        }
    }

    return {
        projects,
        isLoading,
        error,
        loadAssignedProjects,
        loadManagedProjects,
        addProject,
        updateProject,
        archiveProject
    }
})