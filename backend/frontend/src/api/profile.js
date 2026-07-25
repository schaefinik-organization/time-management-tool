import api from '@/api/axios'

export function updateProfileApi(payload) {
  return api.put('/user/update-profile', payload)
}

export function changePasswordApi(payload) {
  return api.post('/user/change-password', payload)
}