import api from '@/api/axios'

export function fetchProjectsApi() {
  return api.get('/projects')
}

export function createProjectApi(payload) {
  return api.post('/projects', payload)
}

export function updateProjectApi(id, payload) {
  return api.put(`/projects/${id}`, payload)
}

export function deleteProjectApi(id) {
  return api.delete(`/projects/${id}`)
}
