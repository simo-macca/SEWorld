<script setup>
import { computed, ref } from 'vue'
import { useTopicsStore } from '@/stores/topicsStore.js'
import ExerciseForm from '@/components/ExerciseComponent/ExerciseForm.vue'
import ExerciseQuestion from '@/components/ExerciseComponent/ExerciseQuestion.vue'

const topicStore = useTopicsStore()
const availableTopics = computed(() => topicStore.topics.filter((t) => t.topicTitle != null))

const title = ref('')
const description = ref('')
const selectedTopic = ref('')
const questions = ref([])

const canSubmit = computed(() => {
  return (
    title.value.trim() &&
    description.value.trim() &&
    selectedTopic.value &&
    questions.value.length > 0 &&
    questions.value.every((q) => {
      if (!q.text.trim()) return false
      if (q.type === 'multiple-choice') {
        return q.options.every((opt) => opt.trim()) && q.correctAnswer !== null
      }
      if (q.type === 'true-false') {
        return q.correctAnswer !== null
      }
      return true
    })
  )
})

const handleSubmit = () => {
  if (!canSubmit.value) {
    alert('Please fill in all required fields')
    return
  }

  const exerciseData = {
    title: title.value,
    description: description.value,
    topicId: selectedTopic.value,
    questions: questions.value.map((q) => ({
      type: q.type,
      text: q.text,
      correctAnswer: q.correctAnswer,
      options: q.options,
    })),
  }

  console.log('Exercise Data:', exerciseData)
  alert('Exercise created successfully! Check console for data.')
}
</script>

<template>
  <main class="min-h-screen bg-gray-50 px-4 py-8 transition-colors duration-200 dark:bg-gray-900">
    <div class="mx-auto max-w-4xl">
      <ExerciseForm
        v-model:title="title"
        v-model:description="description"
        v-model:selectedTopic="selectedTopic"
        :topics="availableTopics"
      />

      <ExerciseQuestion :questions="questions" />

      <div class="flex justify-end pt-4">
        <button
          @click="handleSubmit"
          :disabled="!canSubmit"
          :class="[
            'rounded-xl px-8 py-3 text-lg font-bold shadow-lg transition-all duration-200',
            canSubmit
              ? 'bg-emerald-500 text-white hover:bg-emerald-600 hover:shadow-emerald-500/30 dark:bg-emerald-600 dark:hover:bg-emerald-500'
              : 'cursor-not-allowed bg-gray-200 text-gray-400 dark:bg-gray-800 dark:text-gray-600',
          ]"
        >
          Create Exercise
        </button>
      </div>
    </div>
  </main>
</template>
