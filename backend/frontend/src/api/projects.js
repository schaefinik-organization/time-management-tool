import apiClient from './client'

export function fetchProjects() {
  return apiClient.get('/projects')
}

export function createProject(payload) {
  return apiClient.post('/projects', payload)
}

export function updateProject(id, payload) {
  return apiClient.put(`/projects/${id}`, payload)
}

export function deleteProject(id) {
  return apiClient.delete(`/projects/${id}`)
}
