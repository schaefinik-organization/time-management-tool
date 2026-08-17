import api from '@/api/axios'

export function fetchAssignedProjectsApi() {
    return api.get('/projects/assigned')
}

export function fetchManagedProjectsApi() {
    return api.get('/projects/managed')
}

export function fetchAllProjectsApi() {
    return api.get('/projects/all')
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