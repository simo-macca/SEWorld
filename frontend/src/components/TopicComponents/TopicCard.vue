<script setup>
import ProgressBar from '@/components/TopicComponents/ProgressBar.vue'
import { useThemeStore } from '@/stores/isDark.js'
import { useUsersStore } from '@/stores/usersStore.js'
import { Trash2, SquarePen } from 'lucide-vue-next'
import { useTopicsStore } from '@/stores/topicsStore.js'

defineProps({
  topic: {
    type: Object,
    required: true,
  },
  progressClass: {
    type: String,
    required: true,
  },
  navigateFunction: {
    type: Function,
    required: true,
  },
})

const emit = defineEmits(['show-modal', 'show-confirm'])

const theme = useThemeStore()
const userStore = useUsersStore()
const topicStore = useTopicsStore()

function showDelete(topic) {
  topicStore.currentTopic = topic
  emit('show-confirm')
}

function showUpdate(topic) {
  topicStore.currentTopic = topic
  emit('show-modal')
}
</script>

<template>
  <!-- Topic Card -->
  <div
    class="flex h-full flex-col overflow-hidden rounded-lg border border-gray-200 bg-white shadow-md dark:border-gray-700 dark:bg-gray-800"
  >
    <!-- HeaderDesktop -->
    <div
      class="flex items-center justify-between border-b border-gray-200 bg-gray-50 p-4 dark:border-gray-600 dark:bg-gray-700"
    >
      <h3 class="text-lg font-bold text-gray-900 dark:text-white">
        {{ topic.topicTitle }}
      </h3>
      <div v-if="userStore.isInstructor" class="flex items-center justify-center gap-2">
        <SquarePen
          :size="28"
          class="text-gray-200 hover:text-gray-300"
          @click="showUpdate(topic)"
        />
        <Trash2 :size="28" class="text-red-500 hover:text-red-600" @click="showDelete(topic)" />
      </div>
    </div>

    <!-- Description -->
    <div class="h-[15em] grow overflow-y-auto p-4">
      <p class="text-gray-700 dark:text-gray-300">{{ topic.topicDescription }}</p>
    </div>

    <!-- Progress Bar -->
    <div v-if="!userStore.isInstructor" class="px-4 pb-2">
      <ProgressBar :nameClass="progressClass" :topicProgress="topic.topicProgress" />
    </div>

    <!-- Navigation Buttons -->
    <div
      class="flex justify-end gap-2 border-t border-gray-200 bg-gray-50 p-4 dark:border-gray-600 dark:bg-gray-700"
    >
      <button
        v-for="name in ['Exercises', 'Materials']"
        :key="name"
        @click="navigateFunction(topic.topicSlug, name)"
        class="rounded px-4 py-2 font-medium transition-colors focus:outline-none"
        :class="
          theme.isDark
            ? 'border border-cyan-400 text-cyan-400 hover:bg-gray-600'
            : 'bg-cyan-500 text-white hover:bg-cyan-600'
        "
      >
        {{ name }}
      </button>
    </div>
  </div>
</template>
