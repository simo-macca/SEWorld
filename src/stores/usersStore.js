import { defineStore } from 'pinia'
import api from '@/api/api.js'
import { useToast } from 'vue-toastification'

const toast = useToast()

export const useUsersStore = defineStore('users', {
  state: () => ({
    loggedIn: false,
    user: null,
  }),
  actions: {
    async getUser() {
      try {
        const res = await api.get('/me')
        this.loggedIn = true
        console.log(res.data['message'])
        this.user = res.data['body']
      } catch (err) {
        toast.error(err.response.data.message || err.message)
      }
    },
  },
})
