<script setup>
defineProps({
  searchQuery: { type: String, required: true },
  numSearched: { type: Number, required: true },
  numElem: { type: Number, required: true },
  clearSearch: { type: Function, required: true },
})
</script>

<template>
  <div v-if="searchQuery && numSearched" class="mb-4">
    <small class="text-gray-500 dark:text-gray-400">
      Showing {{ numSearched }} of {{ numElem }} results for "{{ searchQuery }}"
      <button
        class="ml-2 text-sm font-medium text-blue-500 hover:underline focus:outline-none"
        @click="clearSearch"
      >
        Show all
      </button>
    </small>
  </div>
</template>
