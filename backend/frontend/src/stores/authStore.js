import {defineStore} from 'pinia'
import api from '@/api/axios'
import {loginApi} from '@/api/auth'

export const useAuthStore = defineStore('authStore', {
    state: () => ({
        token: localStorage.getItem('token') || null,
    }),

    getters: {
        isAuthenticated: (state) => !!state.token,
    },

    actions: {
        async login(credentials) {
            console.log("start of login with creditials:", credentials);

            const response = await loginApi(credentials)

            console.log(response);

            // 2. Token extrahieren (Annahme: Backend schickt { token: '...' })
            const token = response.data.token
            this.token = token

            // 3. Im localStorage speichern
            localStorage.setItem('token', token)
        },

        logout() {
            this.token = null
            localStorage.removeItem('token')
            delete api.defaults.headers.common['Authorization']
        }
    }
})