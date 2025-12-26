import { defineStore } from 'pinia'
import { toast } from 'vue-sonner'
import api from '@/api/api.js'

export const useQuestionsStore = defineStore('questions', {
  state: () => ({
    currentQuestion: null,
    questions: [],
  }),
  actions: {
    async getQuestions() {
      try {
        const res = await api.get('/question')
        this.questions = res.data.body
        console.log(res.data.message)
        return res
      } catch (err) {
        toast.error(err.response?.data?.message || 'Failed to retrieve question. Please try again.')
        throw err
      }
    },

    async getQuestionsByExercise(slug) {
      try {
        const res = await api.get('/question/' + slug)
        this.questions = res.data.body
        console.log(res.data.message)
        return res
      } catch (err) {
        toast.error(err.response?.data?.message || 'Failed to retrieve question. Please try again.')
        throw err
      }
    },

    async createQuestion(slug, body) {
      try {
        const res = await api.post('/question/' + slug, body)
        console.log(res.data.message)
      } catch (err) {
        toast.error(err.response?.data?.message || 'Failed to create question. Please try again.')
        throw err
      }
    },

    async deleteQuestion(slug) {
      try {
        const res = await api.delete('/exercise/' + slug)
        this.questions = this.questions.filter((exercise) => exercise.exerciseSlug !== slug)
        console.log(res.data.message)
        toast.success(res.data.message)
        return res
      } catch (err) {
        toast.error(err.response?.data?.message || 'Failed to delete question. Please try again.')
        throw err
      }
    },
  },
})
