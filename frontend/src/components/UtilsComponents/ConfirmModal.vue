<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, required: true },
  title: { type: String, default: 'Confirm' },
  message: { type: String, default: 'Are you sure?' },
  okTitle: { type: String, default: 'OK' },
  okVariant: { type: String, default: 'primary' },
})

const emit = defineEmits(['update:modelValue', 'confirm'])

const show = ref(props.modelValue)

watch(
  () => props.modelValue,
  (val) => (show.value = val),
)
watch(show, (val) => emit('update:modelValue', val))

function closeModal() {
  show.value = false
}

function handleOk() {
  closeModal()
  emit('confirm')
}

// Map bootstrap variants to tailwind colors
const btnClasses = {
  primary: 'bg-blue-600 hover:bg-blue-700 text-white',
  'outline-primary':
    'border border-blue-600 text-blue-600 hover:bg-blue-50 dark:text-blue-400 dark:hover:bg-gray-700',
  'outline-danger':
    'border border-red-600 text-red-600 hover:bg-red-50 dark:text-red-400 dark:hover:bg-gray-700',
  'outline-success':
    'border border-green-600 text-green-600 hover:bg-green-50 dark:text-green-400 dark:hover:bg-gray-700',
}
</script>

<template>
  <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center p-4">
    <div class="fixed inset-0 bg-black opacity-50 transition-opacity" @click="closeModal"></div>

    <div
      class="relative w-full max-w-md transform rounded-lg bg-white p-6 shadow-xl transition-all dark:bg-gray-800"
    >
      <div class="mb-4 flex items-center justify-between">
        <h3 class="text-lg font-medium text-gray-900 dark:text-white">{{ title }}</h3>
        <button @click="closeModal" class="text-gray-400 hover:text-gray-500 focus:outline-none">
          <span class="text-2xl">&times;</span>
        </button>
      </div>

      <div class="mt-2">
        <p class="text-sm text-gray-500 dark:text-gray-300">{{ message }}</p>
      </div>

      <div class="mt-6 flex justify-end gap-3">
        <button
          @click="closeModal"
          class="rounded-md bg-gray-100 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-200 focus:outline-none dark:bg-gray-700 dark:text-gray-200 dark:hover:bg-gray-600"
        >
          Cancel
        </button>
        <button
          @click="handleOk"
          :class="[
            'rounded-md px-4 py-2 text-sm font-medium transition-colors focus:outline-none',
            btnClasses[okVariant] || btnClasses.primary,
          ]"
        >
          {{ okTitle }}
        </button>
      </div>
    </div>
  </div>
</template>
