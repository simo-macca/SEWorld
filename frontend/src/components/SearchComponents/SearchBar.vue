<script setup>
import { X } from 'lucide-vue-next'
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: 'Search' },
  context: { type: String, default: 'Search' },
})
const emit = defineEmits(['update:modelValue'])
const searchValue = ref(props.modelValue)

watch(
  () => props.modelValue,
  (newVal) => {
    searchValue.value = newVal
  },
)

function handleInput(e) {
  const val = e.target.value
  searchValue.value = val
  emit('update:modelValue', val)
}

function handleClear() {
  searchValue.value = ''
  emit('update:modelValue', '')
}
</script>

<template>
  <div class="flex w-full items-center">
    <div class="flex w-full items-stretch rounded-md shadow-sm">
      <span
        class="inline-flex items-center rounded-l-md border border-r-0 border-gray-300 bg-gray-50 px-3 text-sm text-gray-500 dark:border-gray-600 dark:bg-gray-700 dark:text-gray-300"
      >
        {{ context }}
      </span>
      <div class="relative grow focus-within:z-10">
        <input
          type="text"
          class="block w-full rounded-none rounded-r-md border border-gray-300 bg-white py-2 pr-10 pl-3 text-gray-900 placeholder-gray-400 transition-shadow focus:border-blue-500 focus:ring-2 focus:ring-blue-500 sm:text-sm dark:border-gray-600 dark:bg-gray-800 dark:text-gray-100"
          :class="{ 'rounded-r-none': searchValue }"
          :value="searchValue"
          @input="handleInput"
          @keydown.esc="handleClear"
          :placeholder="placeholder"
        />
        <button
          v-if="searchValue"
          type="button"
          @click="handleClear"
          class="absolute inset-y-0 right-0 flex cursor-pointer items-center pr-3 text-gray-400 hover:text-gray-600 dark:hover:text-gray-200"
        >
          <X class="h-4 w-4" />
        </button>
      </div>
    </div>
  </div>
</template>
