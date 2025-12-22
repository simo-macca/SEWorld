<script setup>
import {
  SquareCheckBig,
  RotateCcw,
  List,
  Repeat,
  Square as SquareIcon,
  Upload,
  Trash,
  Edit,
  Calendar,
  User,
  Clock,
  CheckCircle2,
  FileEdit,
} from 'lucide-vue-next'
import { useUsersStore } from '@/stores/usersStore.js'
import { computed } from 'vue'
import { useExercisesStore } from '@/stores/exerciseStore.js'

const props = defineProps({
  exercise: { type: Object, required: true },
})

const userStore = useUsersStore()
const isInstructor = computed(() => userStore.isInstructor)
const exerciseStore = useExercisesStore()

const isCompleted = computed(() => props.exercise.exerciseIsCompleted)
const isDraft = computed(() => props.exercise.exerciseIsDraft)

const emit = defineEmits(['show-publish', 'show-delete'])

function showPublish(exercise) {
  exerciseStore.currentExercise = exercise
  emit('show-publish')
}

function showDelete(topic) {
  exerciseStore.currentExercise = topic
  emit('show-delete')
}
</script>

<template>
  <div
    class="group relative overflow-hidden rounded-xl border hover:shadow-lg"
    :class="[
      isInstructor
        ? isDraft
          ? 'border-amber-200 bg-amber-50/50 dark:border-amber-900/50 dark:bg-amber-950/20'
          : 'border-gray-200 bg-white dark:border-gray-700 dark:bg-gray-800'
        : isCompleted
          ? 'border-green-200 bg-green-50/50 dark:border-green-900/50 dark:bg-green-950/20'
          : 'border-gray-200 bg-white dark:border-gray-700 dark:bg-gray-800',
    ]"
  >
    <!-- Status Indicator Bar -->
    <div
      class="absolute top-0 left-0 h-full w-1 group-hover:w-1.5"
      :class="[
        isInstructor
          ? isDraft
            ? 'bg-amber-500'
            : 'bg-blue-500'
          : isCompleted
            ? 'bg-green-500'
            : 'bg-gray-300 dark:bg-gray-600',
      ]"
    />

    <div class="p-5 pl-6">
      <!-- Header Section -->
      <div class="mb-3 flex items-start justify-between gap-4">
        <div class="min-w-0 flex-1">
          <div class="mb-2 flex items-center gap-2">
            <h3 class="truncate text-xl font-bold text-gray-900 dark:text-white">
              {{ props.exercise.exerciseTitle }}
            </h3>

            <!-- Status Badge for Instructor -->
            <span
              v-if="isInstructor"
              class="inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-xs font-semibold"
              :class="
                isDraft
                  ? 'bg-amber-100 text-amber-700 dark:bg-amber-900/50 dark:text-amber-300'
                  : 'bg-green-100 text-green-700 dark:bg-green-900/50 dark:text-green-300'
              "
            >
              <component :is="isDraft ? FileEdit : CheckCircle2" class="h-3 w-3" />
              {{ isDraft ? 'Draft' : 'Published' }}
            </span>
          </div>

          <p class="line-clamp-2 text-sm text-gray-600 dark:text-gray-300">
            {{ props.exercise.exerciseDescription }}
          </p>
        </div>

        <!-- Student Status Icon -->
        <div v-if="!isInstructor" class="flex flex-col items-center gap-1">
          <component
            :is="isCompleted ? SquareCheckBig : SquareIcon"
            :class="isCompleted ? 'text-green-500' : 'text-gray-400'"
            class="h-8 w-8"
          />
          <span
            class="text-xs font-medium"
            :class="isCompleted ? 'text-green-600 dark:text-green-400' : 'text-gray-500'"
          >
            {{ isCompleted ? 'Done' : 'To Do' }}
          </span>
        </div>
      </div>

      <!-- Meta Information -->
      <div class="mb-4 flex flex-wrap items-center gap-4 text-xs text-gray-500 dark:text-gray-400">
        <div v-if="props.exercise.exerciseCreatedDate" class="flex items-center gap-1.5">
          <Calendar class="h-3.5 w-3.5" />
          <span>{{ props.exercise.exerciseCreatedDate }}</span>
        </div>
        <div v-if="props.exercise.exerciseOwner" class="flex items-center gap-1.5">
          <User class="h-3.5 w-3.5" />
          <span>{{ props.exercise.exerciseOwner }}</span>
        </div>
        <div v-if="!isInstructor" class="flex items-center gap-1.5">
          <Repeat class="h-3.5 w-3.5" />
          <span>10 attempts</span>
        </div>
      </div>

      <!-- Actions Section -->
      <div
        class="flex items-center justify-between gap-3 border-t border-gray-100 pt-4 dark:border-gray-700"
      >
        <!-- Instructor Actions -->
        <div v-if="isInstructor" class="flex gap-2">
          <button
            v-if="isDraft"
            @click="showPublish(exercise)"
            class="-blue-50 inline-flex items-center gap-1.5 rounded-lg border border-blue-500 bg-white px-3 py-2 text-sm font-medium text-blue-600 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:outline-none dark:bg-gray-800 dark:hover:bg-gray-700 dark:focus:ring-offset-gray-800"
          >
            <Upload class="h-4 w-4" />
            Publish
          </button>

          <button
            v-if="isDraft"
            class="-gray-50 inline-flex items-center gap-1.5 rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm font-medium text-gray-700 focus:ring-2 focus:ring-gray-500 focus:ring-offset-2 focus:outline-none dark:border-gray-600 dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-gray-700 dark:focus:ring-offset-gray-800"
          >
            <Edit class="h-4 w-4" />
            Modify
          </button>

          <button
            @click="showDelete(exercise)"
            class="-red-600 inline-flex items-center gap-1.5 rounded-lg bg-red-500 px-3 py-2 text-sm font-medium text-white focus:ring-2 focus:ring-red-500 focus:ring-offset-2 focus:outline-none dark:focus:ring-offset-gray-800"
          >
            <Trash class="h-4 w-4" />
            Delete
          </button>
        </div>

        <!-- Student Actions -->
        <div v-else class="flex flex-wrap gap-2">
          <button
            class="-blue-50 inline-flex items-center gap-1.5 rounded-lg border border-blue-500 bg-white px-3 py-2 text-sm font-medium text-blue-600 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:outline-none dark:bg-gray-800 dark:hover:bg-gray-700 dark:focus:ring-offset-gray-800"
          >
            <RotateCcw class="h-4 w-4" />
            Last Attempt
          </button>

          <button
            class="-gray-50 inline-flex items-center gap-1.5 rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm font-medium text-gray-700 focus:ring-2 focus:ring-gray-500 focus:ring-offset-2 focus:outline-none dark:border-gray-600 dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-gray-700 dark:focus:ring-offset-gray-800"
          >
            <List class="h-4 w-4" />
            All Attempts
          </button>
        </div>

        <!-- Completion Badge (Mobile Friendly) -->
        <div v-if="!isInstructor" class="ml-auto hidden sm:block">
          <span
            v-if="isCompleted"
            class="inline-flex items-center gap-1.5 rounded-full bg-green-100 px-3 py-1 text-xs font-semibold text-green-700 dark:bg-green-900/50 dark:text-green-300"
          >
            <CheckCircle2 class="h-3.5 w-3.5" />
            Completed
          </span>
          <span
            v-else
            class="inline-flex items-center gap-1.5 rounded-full bg-gray-100 px-3 py-1 text-xs font-semibold text-gray-600 dark:bg-gray-700 dark:text-gray-400"
          >
            <Clock class="h-3.5 w-3.5" />
            Pending
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
