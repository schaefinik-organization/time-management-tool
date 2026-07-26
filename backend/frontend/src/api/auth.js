import api from '@/api/axios'

export function loginApi(credentials) {
  return api.post('/auth/login', credentials)
}

export function fetchCurrentUserApi() {
  return api.get('/auth/me')
}