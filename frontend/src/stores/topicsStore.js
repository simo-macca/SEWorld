import { defineStore } from 'pinia'
import { toast } from 'vue-sonner'
import api from '@/api/api.js'

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
        toast.error(err.response?.data?.message || err.message)
        throw err
      }
    },

    async createTopic(title, description) {
      try {
        const res = await api.post('/topic', { title: title, description: description })

        const newTopic = res.data.body
        this.topics.unshift(newTopic)

        console.log(res.data.message)
        toast.success(res.data.message)
        return res
      } catch (err) {
        toast.error(err.response?.data?.message || err.message)
        throw err
      }
    },

    async updateTopic(slug, newTitle, newDescription) {
      try {
        const res = await api.patch('/topic/' + slug, {
          title: newTitle,
          description: newDescription,
        })

        const updatedTopic = res.data.body

        const idx = this.topics.findIndex((t) => t.topicSlug === slug)
        if (idx !== -1) {
          this.topics[idx] = updatedTopic
        }

        console.log(res.data.message)
        toast.success(res.data.message)
        return res
      } catch (err) {
        toast.error(err.response?.data?.message || err.message)
        throw err
      }
    },

    async deleteTopic(slug) {
      try {
        const res = await api.delete('/topic/' + slug)
        this.topics = this.topics.filter((topic) => topic.topicSlug !== slug)
        console.log(res.data.message)
        toast.success(res.data.message)
        return res
      } catch (err) {
        toast.error(err.response?.data?.message || err.message)
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
