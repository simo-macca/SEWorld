<script setup>
import { computed, onMounted, ref } from 'vue'
import { useTopicsStore } from '@/stores/topicsStore.js'
import ExerciseForm from '@/components/ExerciseComponent/ExerciseForm.vue'
import ExerciseQuestion from '@/components/ExerciseComponent/ExerciseQuestion.vue'
import { useQuestionsStore } from '@/stores/questionStore.js'
import { useExercisesStore } from '@/stores/exerciseStore.js'
import { toast } from 'vue-sonner'
import router from '@/router/index.js'

const topicStore = useTopicsStore()
onMounted(async () => {
  await topicStore.getTopics()
  // if (props.exerciseSlug) await questionStore.getQuestionsByExercise(props.exerciseSlug)
})
const availableTopics = computed(() => topicStore.topics.filter((t) => t.topicTitle != null))

const exerciseStore = useExercisesStore()
const questionStore = useQuestionsStore()

const props = defineProps({ topicSlug: { type: String, required: true } })
const topicName = computed(() => {
  if (history.state?.topicTitle) return history.state.topicTitle

  const topic = topicStore.findTopicBySlug(props.topicSlug)
  if (topic) return topic.topicTitle

  const text = props.topicSlug || ''
  return text.charAt(0).toUpperCase() + text.slice(1).split('-').join(' ')
})

const exerciseTitle = ref('')
const exerciseDescription = ref('')
const topicTitle = ref(topicName)
const questions = ref(computed(() => questionStore.questions))
const isSubmitting = ref(false)

const canSubmit = computed(() => {
  return (
    exerciseTitle.value.trim() &&
    exerciseDescription.value.trim() &&
    topicTitle.value &&
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

const handleSubmit = async () => {
  if (!canSubmit.value) {
    toast.error('Please fill in all required fields')
    return
  }

  isSubmitting.value = true

  let exerciseSlug
  try {
    const topic = topicStore.findTopicByName(topicTitle.value)
    if (!topic) toast.error('Invalid Topic Selected')
    const topicSlug = topic.topicSlug
    const exerciseDTO = {
      title: exerciseTitle.value,
      description: exerciseDescription.value,
    }
    const data = await exerciseStore.createExercise(topicSlug, exerciseDTO)
    exerciseSlug = data.body.exerciseSlug
    if (!exerciseSlug) {
      toast.error('Failed to retrieve new exercise slug')
      return
    }
    toast.success(data.message)

    const questionDTO = questions.value.map((q) => {
      let newQuestion = { text: q.text }

      switch (q.type) {
        case 'multiple-choice':
          newQuestion = {
            ...newQuestion,
            type: 'MultiChoice',
            options: q.options,
            correctIndex: Number(q.correctAnswer),
          }
          break

        case 'true-false':
          newQuestion = {
            ...newQuestion,
            type: 'TrueFalse',
            correctBoolean: q.correctAnswer,
          }
          break

        case 'short-answer':
          newQuestion = {
            ...newQuestion,
            type: 'ShortAnswer',
            correctText: q.text,
          }
          break

        default:
          toast.warning('Unknown question type:', q.type)
          console.warn('Unknown question type:', q.type)
      }
      return newQuestion
    })

    await questionStore.createQuestion(exerciseSlug, questionDTO)

    toast.success('Exercise and questions created successfully!')
    navigateTo(topic, 'Exercises')
  } catch (err) {
    if (!exerciseSlug) {
      toast.error('Failed to create the exercise.')
      console.error(err)
      return
    }
    try {
      await exerciseStore.deleteExercise(exerciseSlug)
      toast.warning(
        'An error occurred while adding questions. The exercise has been deleted. Please try again.',
      )
    } catch (deleteErr) {
      console.error('CRITICAL: Rollback failed', deleteErr)
      toast.error(
        'An error occurred and we could not revert the changes. Please check the exercise list manually.',
      )
    }
  } finally {
    isSubmitting.value = false
  }
}

function navigateTo(topic, view) {
  router.push({
    name: view,
    params: { topicSlug: topic.topicSlug },
    state: { topicTitle: topic.topicTitle },
  })
}
</script>

<template>
  <main class="min-h-screen bg-gray-50 px-4 py-8 transition-colors duration-200 dark:bg-gray-900">
    <div class="mx-auto max-w-4xl">
      <ExerciseForm
        v-model:title="exerciseTitle"
        v-model:description="exerciseDescription"
        v-model:selectedTopic="topicTitle"
        :topics="availableTopics"
        :currentTopic="topicName"
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
