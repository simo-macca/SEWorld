import { defineStore } from 'pinia'
import { useToast } from 'vue-toastification'

import api from '@/api/api.js'

const toast = useToast()

export const useExercisesStore = defineStore('exercises', {
  state: () => ({
    currentExercises: null,
    exercises: [],
  }),
  actions: {
    async getExercises() {
      try {
        const res = await api.get('/exercise')
        this.exercises = res.data.body.sort(
          (a, b) => new Date(a['exerciseCreatedDate']) - new Date(b['exerciseCreatedDate']),
        )
        console.log(res.data.message)
      } catch (err) {
        toast.error(err.response.data.message || err.message)
      }
    },

    async publishExercise(slug) {
      try {
        const res = await api.patch('/exercise/publish/' + slug)
        console.log(res.data.message)
        const idx = this.exercises.findIndex((e) => e['exerciseSlug'] === slug)
        if (idx !== -1) {
          this.exercises[idx].exerciseIsDraft = false
        } else {
          await this.getExercises()
        }
        return res
      } catch (err) {
        toast.error(err.response.data.message || err.message)
        throw err
      }
    },

    async deleteExercise(slug) {
      const old = [...this.exercises]
      this.exercises = this.exercises.filter((e) => e['exerciseSlug'] !== slug)

      try {
        const res = await api.patch('/exercise/delete/' + slug)
        console.log(res.data.message)
        return res
      } catch (err) {
        this.exercises = old
        toast.error(err.response.data.message || err.message)
        throw err
      }
    },
    topicSelected(topicName) {
      sessionStorage.setItem('currentTopic', JSON.stringify(topicName))
      this.currentTopic = topicName
    },
  },
})
