import api from '@/api/axios'

export function fetchReport(payload) {
    return api.get('/manager/report', payload)
}