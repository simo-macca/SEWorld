<script setup>
import { ref, watch } from 'vue'
import { X, Edit3, Save, AlertCircle } from 'lucide-vue-next'
import { useTopicsStore } from '@/stores/topicsStore.js'

const props = defineProps({
  isOpen: { type: Boolean, required: true },
  currentTopic: { type: Object, default: null },
  isCreating: { type: Boolean, default: false },
  isUpdating: { type: Boolean, default: false },
})

const emit = defineEmits(['close', 'save'])

const title = ref('')
const description = ref('')
const errors = ref({ title: '', description: '' })
const isSaving = ref(false)
const topicStore = useTopicsStore()

// Watch for modal opening to reset values
watch(
  () => props.isOpen,
  (newVal) => {
    if (newVal) {
      title.value = props.currentTopic.topicTitle || ''
      description.value = props.currentTopic.topicDescription || ''
      errors.value = { title: '', description: '' }
    }
  },
)

function validateForm() {
  errors.value = { title: '', description: '' }

  const titleTrimmed = title.value.trim()
  const descriptionTrimmed = description.value.trim()

  if (props.isUpdating) {
    if (!titleTrimmed && !descriptionTrimmed) {
      const errorMsg = 'At least one field must be filled'
      errors.value.title = errorMsg
      errors.value.description = errorMsg
      return false
    }
    return true
  }

  if (props.isCreating) {
    let isValid = true

    if (!titleTrimmed) {
      errors.value.title = 'Title is required'
      isValid = false
    }
    if (!descriptionTrimmed) {
      errors.value.description = 'Description is required'
      isValid = false
    }

    return isValid
  }

  return true
}

async function handleSave() {
  if (!validateForm()) return

  isSaving.value = true

  if (props.isUpdating) {
    try {
      await topicStore.updateTopic(props.currentTopic.topicSlug, title.value, description.value)
    } catch (err) {
      console.error(err)
    }
  }

  if (props.isCreating) {
    try {
      await topicStore.createTopic(title.value, description.value)
    } catch (err) {
      console.error(err)
    }
  }

  isSaving.value = false
  emit('close')
}

function handleClose() {
  if (!isSaving.value) {
    emit('close')
  }
}
</script>

<template>
  <!-- Backdrop (modal overlay) -->
  <div
    v-if="isOpen"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/60 px-4 backdrop-blur-sm"
    @click.self="handleClose"
  >
    <!-- Modal -->
    <div
      v-if="isOpen"
      class="relative w-full max-w-lg rounded-2xl bg-white shadow-2xl dark:bg-gray-800"
    >
      <!-- Header -->
      <div
        class="flex items-center justify-between border-b border-gray-200 p-6 dark:border-gray-700"
      >
        <!-- Image and Title -->
        <div class="flex items-center gap-5">
          <div
            class="flex h-10 w-10 items-center justify-center rounded-full bg-linear-to-br from-blue-500 to-cyan-500"
          >
            <Edit3 class="h-5 w-5 text-white" />
          </div>
          <div>
            <h2 class="text-xl font-bold text-gray-900 dark:text-white">
              {{ isUpdating ? 'Update' : 'Create' }} Topic
            </h2>
            <p class="text-sm text-gray-500 dark:text-gray-400">
              {{ isUpdating ? 'Modify' : 'Create' }} title or description
            </p>
          </div>
        </div>
        <!-- Close button -->
        <button
          @click="handleClose"
          :disabled="isSaving"
          class="rounded-lg p-2 text-gray-400 hover:bg-gray-100 hover:text-gray-600 disabled:cursor-not-allowed disabled:opacity-50 dark:hover:bg-gray-700 dark:hover:text-gray-300"
          aria-label="Close modal"
        >
          <X class="h-8 w-8" />
        </button>
      </div>

      <!-- Body -->
      <div class="space-y-5 p-6">
        <!-- Title Input -->
        <div>
          <label
            for="topic-title"
            class="mb-2 block font-semibold text-gray-700 dark:text-gray-300"
          >
            Title
          </label>
          <input
            id="topic-title"
            v-model="title"
            type="text"
            :disabled="isSaving"
            :placeholder="currentTopic?.topicTitle || 'Enter topic title...'"
            class="w-full rounded-lg border border-gray-300 bg-white px-4 py-3 text-gray-900 placeholder:text-gray-400 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 focus:outline-none disabled:cursor-not-allowed disabled:bg-gray-50 disabled:opacity-60 dark:border-gray-600 dark:bg-gray-700 dark:text-white dark:placeholder:text-gray-500 dark:focus:border-blue-400"
            :class="{
              'border-red-500 focus:border-red-500 focus:ring-red-500/20': errors.title,
            }"
          />

          <!-- Title Error Message -->
          <div
            v-if="errors.title"
            class="mt-2 flex items-center gap-1.5 text-sm text-red-600 dark:text-red-400"
          >
            <AlertCircle class="h-4 w-4" />
            <span>{{ errors.title }}</span>
          </div>
        </div>

        <!-- Description Textarea -->
        <div>
          <label
            for="topic-description"
            class="mb-2 block text-sm font-semibold text-gray-700 dark:text-gray-300"
          >
            Description
          </label>
          <textarea
            id="topic-description"
            v-model="description"
            rows="4"
            :disabled="isSaving"
            :placeholder="currentTopic?.topicDescription || 'Enter topic description...'"
            class="w-full resize-none rounded-lg border border-gray-300 bg-white px-4 py-3 text-gray-900 placeholder:text-gray-400 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 focus:outline-none disabled:cursor-not-allowed disabled:bg-gray-50 disabled:opacity-60 dark:border-gray-600 dark:bg-gray-700 dark:text-white dark:placeholder:text-gray-500 dark:focus:border-blue-400"
            :class="{
              'border-red-500 focus:border-red-500 focus:ring-red-500/20': errors.description,
            }"
          ></textarea>

          <!-- Description Error Message -->
          <div
            v-if="errors.description"
            class="mt-2 flex items-center gap-1.5 text-sm text-red-600 dark:text-red-400"
          >
            <AlertCircle class="h-4 w-4" />
            <span>{{ errors.description }}</span>
          </div>
        </div>

        <!-- Info message -->
        <div
          v-if="isUpdating"
          class="flex items-start gap-2.5 rounded-lg bg-blue-50 p-3.5 dark:bg-blue-900/20"
        >
          <AlertCircle class="mt-0.5 h-5 w-5 shrink-0 text-blue-600 dark:text-blue-400" />
          <p class="text-sm text-blue-800 dark:text-blue-300">
            You can update either the title, description, or both. Leave a field empty to keep its
            current value.
          </p>
        </div>
      </div>

      <!-- Footer -->
      <div
        class="flex items-center justify-end gap-3 border-t border-gray-200 bg-gray-50 px-6 py-4 dark:border-gray-700 dark:bg-gray-900/50"
      >
        <button
          @click="handleClose"
          :disabled="isSaving"
          class="rounded-lg px-5 py-2.5 font-semibold text-gray-700 hover:bg-gray-200 disabled:cursor-not-allowed disabled:opacity-50 dark:text-gray-300 dark:hover:bg-gray-700"
        >
          Cancel
        </button>
        <button
          @click="handleSave"
          :disabled="isSaving"
          class="flex items-center gap-2 rounded-lg bg-linear-to-r from-blue-600 to-cyan-600 px-5 py-2.5 font-semibold text-white shadow-lg hover:from-blue-700 hover:to-cyan-700 hover:shadow-xl disabled:cursor-not-allowed disabled:opacity-60 disabled:shadow-none"
        >
          <Save class="h-4 w-4" :class="{ 'animate-pulse': isSaving }" />
          <span>{{ isSaving ? 'Saving...' : isUpdating ? 'Save Changes' : 'Create Topic' }}</span>
        </button>
      </div>
    </div>
  </div>
</template>
