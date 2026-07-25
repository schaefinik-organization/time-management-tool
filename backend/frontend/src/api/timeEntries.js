import api from '@/api/axios'

export function fetchTimeEntriesByDateApi(date) {
  return api.get('/time-entries', {
    params: { date }
  })
}

export function createTimeEntryApi(payload) {
  return api.post('/time-entries', payload)
}

export function updateTimeEntryApi(id, payload) {
  return api.put(`/time-entries/${id}`, payload)
}

export function deleteTimeEntryApi(id) {
  return api.delete(`/time-entries/${id}`)
}
