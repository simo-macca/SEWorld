import { defineStore } from 'pinia'
import api from '@/api/api.js'
import { useToast } from 'vue-toastification'

const toast = useToast()

export const useExercisesStore = defineStore('exercises', {
  state: () => ({
    currentExercises: null,
    exercises: [],
  }),
  actions: {
    async getTopics() {
      try {
        const res = await api.get('/exercise')
        this.exercises = res.data.body
        console.log(res.data.message)
      } catch (err) {
        toast.error(err.response.data.message || err.message)
      }
    },
    topicSelected(topicName) {
      sessionStorage.setItem('currentTopic', JSON.stringify(topicName))
      this.currentTopic = topicName
    },
  },
})
