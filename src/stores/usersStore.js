import { defineStore } from 'pinia'
import { useToast } from 'vue-toastification'

import api from '@/api/api.js'

const toast = useToast()

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
        console.log(res.data['message'])
        this.user = res.data['body']
        this.isInstructor = res.data.body.role === 'Instructor'
      } catch (err) {
        toast.error(err.response.data.message || err.message)
      }
    },
  },
})
