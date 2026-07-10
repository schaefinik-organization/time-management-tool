import apiClient from './client'

export function fetchTimeEntriesByDate(date) {
  return apiClient.get('/time-entries', {
    params: { date }
  })
}

export function createTimeEntry(payload) {
  return apiClient.post('/time-entries', payload)
}

export function updateTimeEntry(id, payload) {
  return apiClient.put(`/time-entries/${id}`, payload)
}

export function deleteTimeEntry(id) {
  return apiClient.delete(`/time-entries/${id}`)
}
