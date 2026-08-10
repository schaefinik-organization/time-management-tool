import api from '@/api/axios'

export function fetchSubordinatesApi() {
  return api.get('/manager/users')
}

export function createEmployeeApi(payload) {
  return api.post('/manager/users', payload)
}

export function updateEmployeeApi(id, payload) {
  return api.put(`/manager/users/${id}`, payload)
}