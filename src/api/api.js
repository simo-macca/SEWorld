import axios from 'axios'
import { handleError } from '@/api/errorHandler.js'

// Axios instance configured for API requests.
const api = axios.create({
  baseURL: '/api/auth',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Global error interceptor
api.interceptors.response.use(
  (response) => response,
  (error) => {
    handleError(error)
    return Promise.reject(error)
  },
)

export default api
