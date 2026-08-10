import api from '@/api/axios'

export function fetchCurrentUserApi() {
  return api.get('/user/current-user')
}

export function putUpdateProfileApi() {
  return api.put('/user/update-profile')
}

export function postChangePasswordApi() {
  return api.post('/user/change-password')
}