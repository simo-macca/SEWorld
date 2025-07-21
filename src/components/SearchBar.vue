<script setup>
// Imports
import { BFormInput, BInputGroup, BNavForm } from 'bootstrap-vue-next'
import { X } from 'lucide-vue-next'
import { ref, watch } from 'vue'

import { useCollapseStore } from '@/stores/isCollapse.js'
import { useThemeStore } from '@/stores/isDark.js'

// Props & Emits
const props = defineProps({
  modelValue: {
    type: String,
    default: '',
  },
  placeholder: {
    type: String,
    default: 'Search',
  },
  context: {
    type: String,
    default: 'Search',
  },
})
const emit = defineEmits(['update:modelValue'])

// Component state
const collapse = useCollapseStore()
const theme = useThemeStore()
const searchValue = ref(props.modelValue)

// Watch for external modelValue changes
watch(
  () => props.modelValue,
  (newVal) => {
    searchValue.value = newVal
  },
)

function handleInput(value) {
  searchValue.value = value
  emit('update:modelValue', value)
}

function handleClear() {
  searchValue.value = ''
  emit('update:modelValue', '')
}

function handleKeydown(event) {
  if (event.key === 'Escape') {
    handleClear()
  }
}
</script>

<template>
  <!-- Navigation form with adaptive width -->
  <b-nav-form :style="!collapse.isCollapse ? 'width: 38dvw' : ''">
    <b-input-group :prepend="context">
      <!-- Search input -->
      <b-form-input
        :model-value="searchValue"
        @update:model-value="handleInput"
        @keydown="handleKeydown"
        :placeholder="placeholder"
        :class="`focus-ring focus-ring-${theme.isDark ? 'dark' : 'light'}`"
      />
      <!-- Clear button appears when there's text -->
      <template #append v-if="searchValue">
        <button
          type="button"
          class="btn btn-outline-secondary"
          @click="handleClear"
          aria-label="Clear search"
        >
          <X />
        </button>
      </template>
    </b-input-group>
  </b-nav-form>
</template>
