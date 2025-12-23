<script setup>
defineProps({
  title: String,
  description: String,
  selectedTopic: [String, Number],
  topics: {
    type: Array,
    default: () => [],
  },
})

defineEmits(['update:title', 'update:description', 'update:selectedTopic'])
</script>

<template>
  <div
    class="mb-6 rounded-xl border border-gray-100 bg-white p-6 shadow-lg shadow-gray-200/50 dark:border-gray-700 dark:bg-gray-800 dark:shadow-none"
  >
    <h1 class="mb-6 text-3xl font-bold text-gray-900 dark:text-white">Create New Exercise</h1>

    <div class="mb-6">
      <label class="mb-2 block text-sm font-semibold text-gray-700 dark:text-gray-300">
        Exercise Title <span class="text-red-500">*</span>
      </label>
      <input
        :value="title"
        @input="$emit('update:title', $event.target.value)"
        type="text"
        placeholder="Enter exercise title"
        class="w-full rounded-lg border border-gray-300 bg-white px-4 py-2.5 text-gray-900 placeholder-gray-400 transition-all outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20 dark:border-gray-600 dark:bg-gray-700 dark:text-white dark:placeholder-gray-500 dark:focus:border-indigo-400"
      />
    </div>

    <div class="mb-6">
      <label class="mb-2 block text-sm font-semibold text-gray-700 dark:text-gray-300">
        Description <span class="text-red-500">*</span>
      </label>
      <textarea
        :value="description"
        @input="$emit('update:description', $event.target.value)"
        rows="4"
        placeholder="Describe the exercise objectives and instructions"
        class="w-full resize-none rounded-lg border border-gray-300 bg-white px-4 py-2.5 text-gray-900 placeholder-gray-400 transition-all outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20 dark:border-gray-600 dark:bg-gray-700 dark:text-white dark:placeholder-gray-500 dark:focus:border-indigo-400"
      ></textarea>
    </div>

    <div class="mb-2">
      <label class="mb-2 block text-sm font-semibold text-gray-700 dark:text-gray-300">
        Topic <span class="text-red-500">*</span>
      </label>
      <div class="relative">
        <select
          :value="selectedTopic"
          @change="$emit('update:selectedTopic', $event.target.value)"
          class="w-full appearance-none rounded-lg border border-gray-300 bg-white px-4 py-2.5 text-gray-900 transition-all outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20 dark:border-gray-600 dark:bg-gray-700 dark:text-white dark:focus:border-indigo-400"
        >
          <option value="" disabled class="text-gray-400">Select a topic</option>
          <option v-for="topic in topics" :key="topic.id" :value="topic.id">
            {{ topic.topicTitle }}
          </option>
        </select>

        <div
          class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3 text-gray-500 dark:text-gray-400"
        >
          <svg class="h-4 w-4 fill-current" viewBox="0 0 20 20">
            <path
              d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z"
            />
          </svg>
        </div>
      </div>
    </div>
  </div>
</template>
