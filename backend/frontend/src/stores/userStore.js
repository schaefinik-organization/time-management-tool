import { defineStore } from 'pinia'
import { ref } from 'vue'
import { fetchUsersApi, createUserApi, updateUserApi, deleteUserApi } from '@/api/admin/users'
import { fetchSubordinatesApi, createEmployeeApi, updateEmployeeApi } from '@/api/manager/users'
import { fetchCurrentUserApi } from '@/api/user/users'

export const useUserStore = defineStore('user', () => {
    // --- State ---
    const currentUser = ref(null)
    const allUsers = ref([])
    const subordinates = ref([])
    const isLoading = ref(false)
    const error = ref(null)

    // --- Actions ---

    const loadCurrentUser = async () => {
        isLoading.value = true
        error.value = null
        console.log("loading current User")
        try {
            console.log("trying to call current User")
            const response = await fetchCurrentUserApi();
            currentUser.value = response.data
            console.log("loading current User" + response.data)
        } catch (err) {
            error.value = err.response?.data?.message || 'Fehler beim Laden der Benutzerdaten.'
            console.log("error trying to call current User" + error.value)

          } finally {
            isLoading.value = false
        }
    }

    const loadAllUsers = async () => {
        isLoading.value = true
        error.value = null
        try {
            const response = await fetchUsersApi()
            allUsers.value = response.data
        } catch (err) {
            error.value = err.response?.data?.message || 'Fehler beim Laden der Benutzerdaten.'
        } finally {
            isLoading.value = false
        }
    }

    const loadMyEmployees = async () => {
        isLoading.value = true
        error.value = null
        try {
            const response = await fetchSubordinatesApi()
            subordinates.value = response.data
            console.log(subordinates.value)
        } catch (err) {
            error.value = err.response?.data?.message || 'Fehler beim Laden der Untergebenen.'
        } finally {
            isLoading.value = false
        }
    }

    const addUser = async (userData) => {
        try {
            await createUserApi(userData)
            loadAllUsers() // Refresh the list of users after adding a new one
            return { success: true }
        } catch (err) {
            return {
                success: false,
                message: err.response?.data?.message,
                validationErrors: err.response?.data?.validationErrors || {}
            }
        }
    }

    const addEmployee = async (employeeData) => {
        try {
            await createEmployeeApi(employeeData)
            loadSubordinates() // Refresh the list of subordinates after adding a new one
            return { success: true }
        } catch (err) {
            return {
                success: false,
                message: err.response?.data?.message,
                validationErrors: err.response?.data?.validationErrors || {}
            }
        }
    }

    const updateUser = async (userId, updatedData) => {
        try {
            await updateUserApi(userId, updatedData)
            loadSubordinates() // Refresh the list of subordinates after updating
            return { success: true }
        } catch (err) {
            return {
                success: false,
                message: err.response?.data?.message,
                validationErrors: err.response?.data?.validationErrors || {}
            }
        }
    }

    const updateEmployee = async (userId, updatedData) => {
     try {
            await updateEmployeeApi(userId, updatedData)
            loadSubordinates() // Refresh the list of subordinates after updating
            return { success: true }
        } catch (err) {
            return {
                success: false,
                message: err.response?.data?.message,
                validationErrors: err.response?.data?.validationErrors || {}
            }
        }
    }

    const deleteUser = async (userId) => {
        try {
            await deleteUserApi(userId)
            loadAllUsers() // Refresh the list of users after deleting
            return { success: true }
        } catch (err) {
            return {
                success: false,
                message: err.response?.data?.message
            }
        }
    }
    
    return {
      currentUser,
      allUsers,
      subordinates,
      isLoading,
      error,
      loadCurrentUser,
      loadAllUsers,
      loadMyEmployees,
      addUser,
      addEmployee,
      updateUser,
      updateEmployee,
      deleteUser
    }
  })
