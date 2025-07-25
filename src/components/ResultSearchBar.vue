<script setup>
// Imports

// Props & Emits
const props = defineProps({
  searchQuery: {
    type: String,
    required: true,
  },
  numSearched: {
    type: Number,
    required: true,
  },
  numElem: {
    type: Number,
    required: true,
  },
  clearSearch: {
    type: Function,
    required: true,
  },
})
</script>

<template>
  <div v-if="props.searchQuery && props.numSearched" class="mb-3">
    <small class="text-muted">
      Showing {{ props.numSearched }} of {{ props.numElem }} exercises for "{{ props.searchQuery }}"
      <button class="btn btn-link btn-sm p-0 ms-2" @click="props.clearSearch">Show all</button>
    </small>
  </div>
</template>
