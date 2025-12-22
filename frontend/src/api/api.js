import axios from 'axios'
import { handleError } from '@/api/errorHandler.js'
import router from '@/router'
import { useUsersStore } from '@/stores/usersStore.js'

const api = axios.create({
  baseURL: '/api/auth',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response && error.response.status === 401) {
      const userStore = useUsersStore()

      userStore.loggedIn = false
      userStore.user = null

      await router.push('/')
    }

    handleError(error)
    return Promise.reject(error)
  },
)

export default api
