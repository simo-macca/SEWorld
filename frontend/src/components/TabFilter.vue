<script setup>
defineProps({
  tabs: {
    type: Array,
    required: true,
  },
  activeTab: {
    type: String,
    required: true,
  },
})

const emit = defineEmits(['update:activeTab'])

const selectTab = (value) => {
  emit('update:activeTab', value)
}
</script>

<template>
  <div
    class="flex w-full border-b border-gray-200 sm:gap-2 sm:rounded-xl sm:border-none sm:bg-gray-100 sm:p-1.5 dark:border-gray-700 dark:sm:bg-gray-800"
  >
    <button
      v-for="tab in tabs"
      :key="tab.value"
      @click="selectTab(tab.value)"
      class="relative flex-1 px-3 py-3 text-sm font-medium transition-all focus:outline-none sm:rounded-lg sm:px-4"
      :class="[
        activeTab === tab.value
          ? 'border-b-2 border-blue-500 text-blue-600 sm:border-0 sm:bg-white sm:shadow-sm dark:text-blue-400 dark:sm:bg-gray-700'
          : 'border-b-2 border-transparent text-gray-500 hover:text-gray-700 sm:border-0 sm:text-gray-600 sm:hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-200',
      ]"
    >
      <span class="flex flex-col items-center justify-center gap-1 sm:flex-row sm:gap-2">
        <span class="text-xl sm:text-lg">{{ tab.icon }}</span>

        <span class="hidden sm:block">{{ tab.text }}</span>

        <span
          v-if="tab.count !== undefined"
          class="rounded-full px-2 py-0.5 text-xs font-semibold"
          :class="[
            activeTab === tab.value
              ? 'bg-blue-100 text-blue-600 dark:bg-blue-900/50 dark:text-blue-300'
              : 'bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-400',
          ]"
        >
          {{ tab.count }}
        </span>
      </span>
    </button>
  </div>
</template>
