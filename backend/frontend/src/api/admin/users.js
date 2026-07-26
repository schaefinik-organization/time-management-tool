import api from '@/api/axios'

export function fetchUsersApi() {
  return api.get('/admin/users')
}

export function createUserApi(payload) {
  return api.post('/admin/users', payload)
}

export function updateUserApi(id, payload) {
  return api.put(`/admin/users/${id}`, payload)
}

export function deleteUserApi(id) {
  return api.delete(`/admin/users/${id}`)
}