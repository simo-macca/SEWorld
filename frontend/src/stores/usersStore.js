import { defineStore } from 'pinia'
import { toast } from 'vue-sonner'
import api from '@/api/api.js'

export const useUsersStore = defineStore('users', {
  state: () => ({
    loggedIn: false,
    user: null,
    isInstructor: false,
  }),
  actions: {
    async getUser() {
      try {
        const res = await api.get('/me')
        this.loggedIn = true
        console.log(res.data.message)
        this.user = res.data.body
        this.isInstructor = res.data.body.role === 'Instructor'
        return res
      } catch (err) {
        toast.error(err.response?.data?.message || err.message)
        throw err
      }
    },
  },
})
