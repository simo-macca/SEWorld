<script setup>
import { RouterLink } from 'vue-router'
import NavLinks from '@/components/HeaderComponents/NavLinks.vue'

defineProps({
  navLinks: { type: Array, required: true },
  isCollapse: { type: Boolean, default: false },
})

const collapsedLinks = [
  { name: 'Profile', to: '/profile', routeName: 'Profile' },
  { name: 'Log out', to: '/', routeName: 'Logout' },
]

const emit = defineEmits(['close', 'customClick'])
</script>

<template>
  <!-- links for mobile devices -->
  <div class="mt-4 space-y-2 border-t border-gray-200 pt-4 lg:hidden dark:border-gray-700">
    <!-- Navigation links -->
    <NavLinks
      :links="navLinks"
      :is-mobile="true"
      @close="emit('close')"
      @custom-click="emit('customClick')"
    />

    <!-- User menu links -->
    <div v-if="isCollapse" class="space-y-2 border-t border-gray-200 pt-3 dark:border-gray-700">
      <RouterLink
        v-for="link in collapsedLinks"
        :key="link.to"
        :to="link.to"
        @click="emit('close')"
        class="block rounded-lg px-4 py-3 font-semibold text-gray-600 transition-colors hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700"
      >
        {{ link.name }}
      </RouterLink>
    </div>
  </div>
</template>
