import { setActivePinia, createPinia } from 'pinia'
import { describe, it, expect, beforeEach } from 'vitest'
import { useAuthStore } from '@/stores/authStore'
import api from '@/api/axios'

describe('Auth Store', () => {
    beforeEach(() => {
        setActivePinia(createPinia())
        localStorage.clear()
    })

    it('clears token, localStorage and axios header on logout', () => {
        const store = useAuthStore()
        
        // Setup State
        store.token = 'fake-jwt-token'
        localStorage.setItem('token', 'fake-jwt-token')
        api.defaults.headers.common['Authorization'] = 'Bearer fake-jwt-token'

        // Action
        store.logout()

        // Assert
        expect(store.token).toBeNull()
        expect(localStorage.getItem('token')).toBeNull()
        expect(api.defaults.headers.common['Authorization']).toBeUndefined()
    })
})