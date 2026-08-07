import api from '@/api/axios'

export function fetchMyTimeEntriesApi() {
  return api.get('/time-entries')
}

export function createTimeEntryApi(payload) {
  return api.post('/time-entries', payload)
}

export function deleteTimeEntryApi(id) {
  return api.delete(`/time-entries/${id}`)
}