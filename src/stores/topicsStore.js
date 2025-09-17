import { defineStore } from 'pinia'
import { useToast } from 'vue-toastification'

import api from '@/api/api.js'

const toast = useToast()

export const useTopicsStore = defineStore('topics', {
  state: () => ({
    currentTopic: null,
    topics: [],
  }),
  actions: {
    async getTopics() {
      try {
        const res = await api.get('/topic')
        this.topics = res.data.body
        console.log(res.data.message)
        return res
      } catch (err) {
        toast.error(err.response.data.message || err.message)
        throw err
      }
    },
  },
  getters: {
    findCurrentTopic: (state) => (slug) => {
      return state['currentTopic'] || state['topics'].find((t) => t.topicSlug === slug)
    },
  },
})
