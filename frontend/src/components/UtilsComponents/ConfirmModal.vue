<script setup>
import { watch, onMounted, onUnmounted } from 'vue'
import { X, AlertTriangle, CheckCircle, Info } from 'lucide-vue-next'

const props = defineProps({
  isOpen: { type: Boolean, required: true },
  title: { type: String, default: 'Confirm' },
  message: { type: String, default: 'Are you sure?' },
  okTitle: { type: String, default: 'OK' },
  okVariant: { type: String, default: 'primary' },
})

const emit = defineEmits(['close', 'confirm'])

function closeModal() {
  emit('close')
}

function handleOk() {
  emit('confirm')
  closeModal()
}

// Handle ESC key
function handleEscape(e) {
  if (e.key === 'Escape' && props.isOpen) {
    closeModal()
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleEscape)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleEscape)
})

// Icon and color mapping based on variant
const variantConfig = {
  primary: {
    icon: Info,
    iconBg: 'bg-blue-100 dark:bg-blue-900/30',
    iconColor: 'text-blue-600 dark:text-blue-400',
  },
  'outline-primary': {
    icon: Info,
    iconBg: 'bg-blue-100 dark:bg-blue-900/30',
    iconColor: 'text-blue-600 dark:text-blue-400',
  },
  'outline-danger': {
    icon: AlertTriangle,
    iconBg: 'bg-red-100 dark:bg-red-900/30',
    iconColor: 'text-red-600 dark:text-red-400',
  },
  'outline-success': {
    icon: CheckCircle,
    iconBg: 'bg-green-100 dark:bg-green-900/30',
    iconColor: 'text-green-600 dark:text-green-400',
  },
}

const currentVariant = variantConfig[props.okVariant] || variantConfig.primary

// Button classes
const btnClasses = {
  primary: 'bg-blue-600 hover:bg-blue-700 text-white focus:ring-blue-500',
  'outline-primary':
    'border-2 border-blue-600 text-blue-600 hover:bg-blue-50 dark:text-blue-400 dark:border-blue-500 dark:hover:bg-blue-950/30 focus:ring-blue-500',
  'outline-danger':
    'border-2 border-red-600 text-red-600 hover:bg-red-50 dark:text-red-400 dark:border-red-500 dark:hover:bg-red-950/30 focus:ring-red-500',
  'outline-success':
    'border-2 border-green-600 text-green-600 hover:bg-green-50 dark:text-green-400 dark:border-green-500 dark:hover:bg-green-950/30 focus:ring-green-500',
}
</script>

<template>
  <div
    v-if="isOpen"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4 backdrop-blur-sm"
    @click.self="closeModal"
  >
    <div
      v-if="isOpen"
      class="relative w-full max-w-md transform rounded-2xl bg-white shadow-2xl transition-all dark:bg-gray-800"
      @click.stop
    >
      <!-- Close Button -->
      <button
        @click="closeModal"
        class="absolute top-4 right-4 rounded-lg p-1.5 text-gray-400 transition-colors hover:bg-gray-100 hover:text-gray-600 focus:ring-2 focus:ring-gray-300 focus:outline-none dark:hover:bg-gray-700 dark:hover:text-gray-300 dark:focus:ring-gray-600"
        aria-label="Close"
      >
        <X class="h-5 w-5" />
      </button>

      <!-- Modal Content -->
      <div class="p-6">
        <!-- Icon -->
        <div class="mb-4 flex justify-center">
          <div :class="[currentVariant.iconBg, currentVariant.iconColor]" class="rounded-full p-3">
            <component :is="currentVariant.icon" class="h-8 w-8" />
          </div>
        </div>

        <!-- Title -->
        <h3 class="mb-2 text-center text-xl font-semibold text-gray-900 dark:text-white">
          {{ title }}
        </h3>

        <!-- Message -->
        <p class="text-center text-sm text-gray-600 dark:text-gray-400" v-html="message" />
      </div>

      <!-- Actions -->
      <div
        class="flex gap-3 border-t border-gray-100 bg-gray-50 px-6 py-4 dark:border-gray-700 dark:bg-gray-900/50"
      >
        <button
          @click="closeModal"
          class="flex-1 rounded-lg border-2 border-gray-300 bg-white px-4 py-2.5 text-sm font-medium text-gray-700 transition-all hover:bg-gray-50 focus:ring-2 focus:ring-gray-300 focus:ring-offset-2 focus:outline-none dark:border-gray-600 dark:bg-gray-800 dark:text-gray-200 dark:hover:bg-gray-700 dark:focus:ring-gray-600 dark:focus:ring-offset-gray-800"
        >
          Cancel
        </button>
        <button
          @click="handleOk"
          :class="[
            'flex-1 rounded-lg px-4 py-2.5 text-sm font-medium transition-all focus:ring-2 focus:ring-offset-2 focus:outline-none dark:focus:ring-offset-gray-800',
            btnClasses[okVariant] || btnClasses.primary,
          ]"
        >
          {{ okTitle }}
        </button>
      </div>
    </div>
  </div>
</template>
