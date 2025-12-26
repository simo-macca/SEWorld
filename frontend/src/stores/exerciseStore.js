import { defineStore } from 'pinia'
import { toast } from 'vue-sonner'
import api from '@/api/api.js'

export const useExercisesStore = defineStore('exercises', {
  state: () => ({
    currentExercise: null,
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
        toast.error(err.response?.data?.message || 'Failed to retrieve exercise. Please try again.')
        throw err
      }
    },

    async getExerciseByTopic(slug) {
      try {
        const res = await api.get('/exercise/get_by_topic/' + slug)
        this.exercises = res.data.body.sort(
          (a, b) => new Date(a.exerciseCreatedDate) - new Date(b.exerciseCreatedDate),
        )
        console.log(res.data.message)
      } catch (err) {
        toast.error(err.response?.data?.message || 'Failed to retrieve exercise. Please try again.')
        throw err
      }
    },

    async createExercise(slug, body) {
      try {
        const res = await api.post('/exercise/' + slug, body)
        console.log(res.data.message)
        return res.data
      } catch (err) {
        toast.error(err.response?.data?.message || 'Failed to create exercise. Please try again.')
        throw err
      }
    },

    async publishExercise(slug) {
      try {
        const res = await api.patch('/exercise/publish/' + slug)

        const idx = this.exercises.findIndex((exercise) => exercise.exerciseSlug === slug)

        if (idx !== -1) {
          this.exercises[idx].exerciseIsDraft = false
        }

        console.log(res.data.message)
        toast.success(res.data.message)
      } catch (err) {
        toast.error(err.response?.data?.message || 'Failed to update exercise. Please try again.')
        throw err
      }
    },

    async deleteExercise(slug) {
      try {
        const res = await api.delete('/exercise/' + slug)
        this.exercises = this.exercises.filter((exercise) => exercise.exerciseSlug !== slug)
        console.log(res.data.message)
        return res.data
      } catch (err) {
        toast.error(err.response?.data?.message || 'Failed to delete exercise. Please try again.')
        throw err
      }
    },
  },
})
