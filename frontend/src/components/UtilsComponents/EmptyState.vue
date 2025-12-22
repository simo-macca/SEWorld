<script setup>
defineProps({
  icon: {
    type: String,
    default: '📭',
  },
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    default: '',
  },
  actionText: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['action'])
</script>

<template>
  <div class="flex min-h-100 items-center justify-center py-12">
    <div class="max-w-md px-4 text-center">
      <div
        class="mb-6 inline-flex h-20 w-20 items-center justify-center rounded-full bg-gray-100 dark:bg-gray-800"
      >
        <span class="text-4xl">{{ icon }}</span>
      </div>

      <h3 class="mb-2 text-2xl font-semibold text-gray-900 dark:text-white">
        {{ title }}
      </h3>

      <p v-if="description" class="mb-6 text-gray-600 dark:text-gray-400" v-html="description" />

      <button
        v-if="actionText"
        @click="emit('action')"
        class="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-6 py-3 text-sm font-medium text-white transition-colors hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:outline-none dark:focus:ring-offset-gray-900"
      >
        {{ actionText }}
      </button>
    </div>
  </div>
</template>
