import api from '@/api/axios'

export function loginApi(credentials) {
  return api.post('/auth/login', credentials)
}