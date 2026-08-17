import api from '@/api/axios'

export function fetchReport(payload) {
    return api.get('/user/report', payload)
}