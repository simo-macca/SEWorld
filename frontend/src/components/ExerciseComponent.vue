<script setup>
import {
  SquareCheckBig,
  RotateCcw,
  List,
  Repeat,
  Square as SquareIcon,
  Upload,
  Trash,
} from 'lucide-vue-next'
import { useUsersStore } from '@/stores/usersStore.js'
import { computed, ref } from 'vue'
import ConfirmModal from '@/components/UtilsComponents/ConfirmModal.vue'
import { useExercisesStore } from '@/stores/exerciseStore.js'
import { toast } from 'vue-sonner'

const props = defineProps({
  exercise: { type: Object, required: true },
})

const userStore = useUsersStore()
const isInstructor = computed(() => userStore.isInstructor)
const showPublishModal = ref(false)
const showDeleteModal = ref(false)

const statusConfig = {
  true: { class: 'text-green-500', icon: SquareCheckBig },
  false: { class: 'text-gray-400', icon: SquareIcon },
}
const getStatus = (completed) => statusConfig[completed] || { class: '', icon: null }

async function confirmPublish(slug) {
  const updated = await useExercisesStore().publishExercise(slug)
  toast.success(updated.data.message || 'Published successfully')
}

async function confirmDelete(slug) {
  const updated = await useExercisesStore().deleteExercise(slug)
  toast.success(updated.data.message || 'Deleted successfully')
}
</script>

<template>
  <div
    class="flex h-full flex-col justify-between rounded-lg border border-gray-200 bg-white p-4 shadow-sm dark:border-gray-700 dark:bg-gray-800"
  >
    <div>
      <div class="mb-2 flex items-center justify-between">
        <h5 class="text-lg font-semibold text-gray-900 dark:text-white">
          {{ props.exercise.exerciseTitle }}
        </h5>
        <div class="flex items-center gap-2" v-if="!isInstructor">
          <component
            :is="getStatus(props.exercise.exerciseIsCompleted).icon"
            :class="getStatus(props.exercise.exerciseIsCompleted).class"
            class="h-5 w-5"
          />
          <div class="flex items-center text-sm text-gray-500 dark:text-gray-400">
            <Repeat class="mr-1 h-4 w-4" aria-label="Tentativi" />
            <span>10</span>
          </div>
        </div>
      </div>
      <p class="text-sm text-gray-600 dark:text-gray-300">
        {{ props.exercise.exerciseDescription }}
      </p>
    </div>

    <div
      class="mt-2 flex items-center justify-between border-t border-gray-100 pt-4 dark:border-gray-700"
    >
      <div class="flex flex-col text-xs text-gray-400">
        <span v-if="props.exercise.exerciseCreatedDate"
          >Created: {{ props.exercise.exerciseCreatedDate }}</span
        >
        <span v-if="props.exercise.exerciseOwner">By: {{ props.exercise.exerciseOwner }}</span>
      </div>

      <div v-if="isInstructor" class="flex gap-2">
        <button
          v-if="props.exercise.exerciseIsDraft"
          @click="showPublishModal = true"
          class="flex items-center rounded border border-blue-500 px-2 py-1 text-sm text-blue-500 transition hover:bg-blue-50 dark:hover:bg-gray-700"
        >
          <Upload class="mr-1 h-3 w-3" /> Public
        </button>
        <ConfirmModal
          v-model="showPublishModal"
          title="Publish Exercise"
          :message="`Are you sure you want to publish ${props.exercise.exerciseTitle}?`"
          ok-title="Publish"
          ok-variant="outline-success"
          @confirm="confirmPublish(props.exercise.exerciseSlug)"
        />

        <button
          v-if="props.exercise.exerciseIsDraft"
          class="flex items-center rounded border border-gray-400 px-2 py-1 text-sm text-gray-500 transition hover:bg-gray-50 dark:hover:bg-gray-700"
        >
          <List class="mr-1 h-3 w-3" /> Modify
        </button>

        <button
          @click="showDeleteModal = true"
          class="flex items-center rounded bg-red-500 px-2 py-1 text-sm text-white transition hover:bg-red-600"
        >
          <Trash class="mr-1 h-3 w-3" /> Delete
        </button>
        <ConfirmModal
          v-model="showDeleteModal"
          title="Delete Exercise"
          message="This action is irreversible."
          ok-title="Delete"
          ok-variant="outline-danger"
          @confirm="confirmDelete(props.exercise.exerciseSlug)"
        />
      </div>
      <div v-else class="flex gap-2">
        <button
          class="flex items-center rounded border border-blue-500 px-2 py-1 text-sm text-blue-500 hover:bg-blue-50"
        >
          <RotateCcw class="mr-1 h-3 w-3" /> Last attempt
        </button>
        <button
          class="flex items-center rounded border border-gray-400 px-2 py-1 text-sm text-gray-500 hover:bg-gray-50"
        >
          <List class="mr-1 h-3 w-3" /> All attempts
        </button>
      </div>
    </div>
  </div>
</template>
