<script setup>
import { RouterLink } from 'vue-router'

defineProps({
  links: { type: Array, required: true },
  isMobile: { type: Boolean, default: false },
})

const emit = defineEmits(['close', 'customClick'])
</script>

<template>
  <!-- Desktop version - horizontal list -->
  <ul v-if="!isMobile" class="flex items-center gap-1">
    <li v-for="link in links" :key="link.to">
      <a
        v-if="link.isCustom"
        :href="link.to"
        @click.prevent="emit('customClick')"
        class="cursor-pointer rounded-lg px-4 py-2 font-semibold text-gray-600 transition-colors hover:bg-gray-100 hover:text-blue-600 dark:text-gray-300 dark:hover:bg-gray-700 dark:hover:text-blue-400"
      >
        {{ link.name }}
      </a>

      <RouterLink
        v-else
        :to="link.to"
        class="rounded-lg px-4 py-2 font-semibold text-gray-600 transition-colors hover:bg-gray-100 hover:text-blue-600 dark:text-gray-300 dark:hover:bg-gray-700 dark:hover:text-blue-400"
        active-class="bg-blue-50 text-blue-600 dark:bg-gray-700 dark:text-blue-400"
      >
        {{ link.name }}
      </RouterLink>
    </li>
  </ul>

  <!-- Mobile version - vertical stack -->
  <div v-else class="space-y-2">
    <template v-for="link in links" :key="link.to">
      <a
        v-if="link.isCustom"
        :href="link.to"
        @click.prevent="emit('customClick')"
        class="block rounded-lg px-4 py-3 font-semibold text-gray-600 transition-colors hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700"
      >
        {{ link.name }}
      </a>
      <RouterLink
        v-else
        :to="link.to"
        @click="emit('close')"
        class="block rounded-lg px-4 py-3 font-semibold text-gray-600 transition-colors hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700"
        active-class="bg-blue-50 text-blue-600 dark:bg-gray-700 dark:text-blue-400"
      >
        {{ link.name }}
      </RouterLink>
    </template>
  </div>
</template>
