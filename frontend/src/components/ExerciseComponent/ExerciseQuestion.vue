<script setup>
import { X } from 'lucide-vue-next'
const props = defineProps({
  questions: {
    type: Array,
    required: true,
  },
})

const questionTypes = [
  { value: 'true-false', label: 'True/False' },
  { value: 'multiple-choice', label: 'Multiple Choice' },
  { value: 'short-answer', label: 'Short Answer' },
]

const addQuestion = (type) => {
  const newQuestion = {
    id: Date.now(),
    type,
    text: '',
    correctAnswer: null,
    options: type === 'multiple-choice' ? ['', '', '', ''] : [],
  }
  // Mutating the prop array works in Vue because arrays are objects (passed by reference)
  props.questions.push(newQuestion)
}

const removeQuestion = (id) => {
  const index = props.questions.findIndex((q) => q.id === id)
  if (index !== -1) props.questions.splice(index, 1)
}

const addOption = (questionId) => {
  const question = props.questions.find((q) => q.id === questionId)
  if (question) {
    question.options.push('')
  }
}

const removeOption = (questionId, optionIndex) => {
  const question = props.questions.find((q) => q.id === questionId)
  if (question && question.options.length > 2) {
    question.options.splice(optionIndex, 1)
    if (question.correctAnswer === optionIndex) {
      question.correctAnswer = null
    }
  }
}
</script>

<template>
  <div
    class="mb-6 rounded-xl border border-gray-100 bg-white p-6 shadow-lg shadow-gray-200/50 dark:border-gray-700 dark:bg-gray-800 dark:shadow-none"
  >
    <div class="mb-8 flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
      <h2 class="text-2xl font-bold text-gray-900 dark:text-white">Questions</h2>
      <div class="flex flex-wrap gap-2">
        <button
          v-for="type in questionTypes"
          :key="type.value"
          @click="addQuestion(type.value)"
          class="rounded-lg bg-indigo-50 px-4 py-2 text-sm font-medium text-indigo-700 transition-colors hover:bg-indigo-100 dark:bg-indigo-900/30 dark:text-indigo-300 dark:hover:bg-indigo-900/50"
        >
          + {{ type.label }}
        </button>
      </div>
    </div>

    <div
      v-if="questions.length === 0"
      class="flex flex-col items-center justify-center rounded-lg border-2 border-dashed border-gray-200 py-12 text-gray-500 dark:border-gray-700 dark:text-gray-400"
    >
      <p class="text-lg">No questions yet</p>
      <p class="text-sm">Select a type above to add your first question</p>
    </div>

    <div
      v-for="(question, qIndex) in questions"
      :key="question.id"
      class="group mb-6 rounded-xl border border-gray-200 bg-gray-50 p-5 transition-colors hover:border-indigo-200 dark:border-gray-700 dark:bg-gray-800/50 dark:hover:border-indigo-700"
    >
      <div class="mb-4 flex items-start justify-between">
        <div class="flex items-center gap-3">
          <span
            class="flex h-8 w-8 items-center justify-center rounded-full bg-white text-sm font-bold text-gray-700 shadow-sm dark:bg-gray-700 dark:text-gray-200"
          >
            {{ qIndex + 1 }}
          </span>
          <span
            class="rounded-full bg-indigo-100 px-3 py-1 text-xs font-semibold tracking-wide text-indigo-700 uppercase dark:bg-indigo-900/50 dark:text-indigo-300"
          >
            {{ questionTypes.find((t) => t.value === question.type).label }}
          </span>
        </div>
        <button
          @click="removeQuestion(question.id)"
          class="text-sm font-medium text-red-500 transition-colors hover:text-red-700 dark:text-red-400 dark:hover:text-red-300"
        >
          Remove
        </button>
      </div>

      <div class="mb-4">
        <input
          v-model="question.text"
          type="text"
          placeholder="Enter question text"
          class="w-full rounded-lg border border-gray-300 bg-white px-4 py-2 transition-all outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20 dark:border-gray-600 dark:bg-gray-700 dark:text-white dark:placeholder-gray-500 dark:focus:border-indigo-400"
        />
      </div>

      <div v-if="question.type === 'true-false'" class="grid grid-cols-2 gap-4">
        <label
          :class="[
            'flex cursor-pointer items-center justify-center gap-2 rounded-lg border p-3 font-medium transition-all',
            question.correctAnswer === true
              ? 'border-indigo-500 bg-indigo-50 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-300'
              : 'border-gray-300 bg-white hover:bg-gray-50 dark:border-gray-600 dark:bg-gray-700 dark:text-gray-200 dark:hover:bg-gray-600',
          ]"
        >
          <input
            type="radio"
            :name="'tf-' + question.id"
            :value="true"
            v-model="question.correctAnswer"
            class="hidden"
          />
          True
        </label>
        <label
          :class="[
            'flex cursor-pointer items-center justify-center gap-2 rounded-lg border p-3 font-medium transition-all',
            question.correctAnswer === false
              ? 'border-indigo-500 bg-indigo-50 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-300'
              : 'border-gray-300 bg-white hover:bg-gray-50 dark:border-gray-600 dark:bg-gray-700 dark:text-gray-200 dark:hover:bg-gray-600',
          ]"
        >
          <input
            type="radio"
            :name="'tf-' + question.id"
            :value="false"
            v-model="question.correctAnswer"
            class="hidden"
          />
          False
        </label>
      </div>

      <div v-if="question.type === 'multiple-choice'" class="space-y-3">
        <div v-for="optIndex in question.options" :key="optIndex" class="flex items-center gap-3">
          <div class="relative flex items-center">
            <input
              type="radio"
              :name="'mc-' + question.id"
              :value="optIndex"
              v-model="question.correctAnswer"
              class="peer h-5 w-5 cursor-pointer appearance-none rounded-full border border-gray-300 checked:border-indigo-500 checked:bg-indigo-500 dark:border-gray-500 dark:checked:border-indigo-400 dark:checked:bg-indigo-400"
            />
            <div
              class="pointer-events-none absolute top-1/2 left-1/2 hidden h-2.5 w-2.5 -translate-x-1/2 -translate-y-1/2 rounded-full bg-white peer-checked:block"
            ></div>
          </div>
          <input
            v-model="question.options[optIndex]"
            type="text"
            :placeholder="'Option ' + (optIndex + 1)"
            class="flex-1 rounded-lg border border-gray-300 bg-white px-4 py-2 transition-all outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20 dark:border-gray-600 dark:bg-gray-700 dark:text-white dark:placeholder-gray-500 dark:focus:border-indigo-400"
          />
          <button
            v-if="question.options.length > 2"
            @click="removeOption(question.id, optIndex)"
            class="text-gray-400 hover:text-red-500 dark:text-gray-500 dark:hover:text-red-400"
          >
            <X />
          </button>
        </div>
        <button
          @click="addOption(question.id)"
          class="mt-2 text-sm font-semibold text-indigo-600 hover:text-indigo-800 dark:text-indigo-400 dark:hover:text-indigo-300"
        >
          + Add Another Option
        </button>
      </div>

      <div
        v-if="question.type === 'short-answer'"
        class="flex flex-col items-start justify-center gap-y-2 rounded-lg border border-indigo-100 bg-indigo-50 p-4 text-sm text-indigo-800 dark:border-indigo-900/50 dark:bg-indigo-900/20 dark:text-indigo-200"
      >
        <label> Enter a possible answer </label>
        <input
          type="text"
          placeholder="Answer"
          v-model="question.correctAnswer"
          class="w-full flex-1 rounded-lg border border-gray-300 bg-white px-4 py-2 transition-all outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20 dark:border-gray-600 dark:bg-gray-700 dark:text-white dark:placeholder-gray-500 dark:focus:border-indigo-400"
        />
      </div>
    </div>
  </div>
</template>
