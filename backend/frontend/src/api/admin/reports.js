import api from '@/api/axios'

export function fetchReportDataApi(start, end) {
  return api.get('/admin/reports/summary', {
    params: {
      start,
      end
    }
  })
}