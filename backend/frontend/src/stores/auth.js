import { defineStore } from 'pinia'
import api from '@/api/axios'
import { loginApi, fetchCurrentUserApi}  from '@/api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: localStorage.getItem('token') || null,
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === 'ROLE_ADMIN',
  },

  actions: {
    async login(credentials) {
      // 1. API Call zum Backend (/api/auth/login)
      const response = await loginApi(credentials)
      
      // 2. Token extrahieren (Annahme: Backend schickt { token: '...' })
      const token = response.data.token
      this.token = token
      this.user = response.data.user // Falls das Backend User-Infos mitschickt

      // 3. Im localStorage speichern
      localStorage.setItem('token', token)

      // 4. Axios Header für zukünftige Requests setzen
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`
    },

    async fetchCurrentUser() {
      if (!this.token) return
      try {
        const response = await fetchCurrentUserApi()
        this.user = response.data
      } catch (error) {
        this.logout()
      }
    },

    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('token')
      delete api.defaults.headers.common['Authorization']
    }
  }
})