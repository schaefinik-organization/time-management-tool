import api from '@/api/axios'

export function fetchAssignedProjectsApi() {
    return api.get('/projects/assigned')
}

export function fetchProjectByIdApi(id) {
    return api.get(`/projects/${id}`)
}

export function fetchManagedProjectsApi() {
    return api.get('/projects/managed')
}

export function createProjectApi(payload) {
    return api.post('/projects', payload)
}

export function updateProjectApi(id, payload) {
    return api.put(`/projects/${id}`, payload)
}

export function archiveProjectApi(id) {
    return api.delete(`/projects/${id}`)
}

export function assignUsersToProjectApi(id, userIdsArray) {
    return api.patch(`/projects/${id}/users`, {
        userIds: userIdsArray
    })
}

export function fetchProjectHoursReportApi(projectId, month) {
  const params = month ? { month } : {}
  return api.get(`/projects/${projectId}/reports/hours-per-user`, { params })
}

export function exportProjectExcelApi(projectId, month) {
  const params = month ? { month } : {}
  return api.get(`/projects/${projectId}/export/excel`, {
    params,
    responseType: 'blob'
  })
}

export function exportProjectPdfApi(projectId, month) {
  const params = month ? { month } : {}
  return api.get(`/projects/${projectId}/export/pdf`, {
    params,
    responseType: 'blob'
  })
}