<script setup>
import { ref, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { User, LogOut } from 'lucide-vue-next'
import { useUsersStore } from '@/stores/usersStore.js'

const usersStore = useUsersStore()
const isOpen = ref(false)

const dropdownLinks = [
  { name: 'Profile', to: '/profile', icon: User },
  { name: 'Log out', to: '/', icon: LogOut },
]

// --- Computed ---
const userName = computed(() => {
  const name = usersStore.user?.name
  return name
    ? name
        .split(' ')
        .map((part) => part.charAt(0).toUpperCase())
        .join('')
    : 'NF'
})

// --- Methods ---
function toggleDropdown() {
  isOpen.value = !isOpen.value
}

function closeDropdown() {
  isOpen.value = false
}

// --- Custom Directive for Click Outside ---
const vClickOutside = {
  mounted(el, binding) {
    el.clickOutsideEvent = (event) => {
      if (!(el === event.target || el.contains(event.target))) {
        binding.value(event)
      }
    }
    document.addEventListener('click', el.clickOutsideEvent)
  },
  unmounted(el) {
    document.removeEventListener('click', el.clickOutsideEvent)
  },
}
</script>

<template>
  <div class="relative" v-click-outside="closeDropdown">
    <button
      @click="toggleDropdown"
      class="flex h-12 w-12 items-center justify-center rounded-full bg-linear-to-br from-cyan-400 to-blue-500 font-bold text-white shadow-md transition-transform hover:scale-105 hover:shadow-lg focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:outline-none dark:ring-offset-gray-800"
      aria-label="User menu"
    >
      <span>{{ userName }}</span>
    </button>

    <div
      v-if="isOpen"
      class="absolute right-0 z-50 mt-3 w-56 overflow-hidden rounded-xl border border-gray-200 bg-white shadow-xl dark:border-gray-600 dark:bg-gray-700"
    >
      <div class="border-b border-gray-200 px-4 py-3 dark:border-gray-600">
        <p class="text-sm font-semibold text-gray-900 dark:text-gray-100">
          {{ usersStore.user?.name || 'User' }}
        </p>
        <p class="truncate text-xs text-gray-500 dark:text-gray-400">
          {{ usersStore.user?.email || 'user@example.com' }}
        </p>
      </div>

      <RouterLink
        v-for="link in dropdownLinks"
        :key="link.to"
        :to="link.to"
        class="flex items-center gap-3 px-4 py-3 text-sm font-medium text-gray-700 hover:bg-gray-50 dark:text-gray-200 dark:hover:bg-gray-600"
        @click="closeDropdown"
      >
        <component :is="link.icon" class="h-4 w-4" />
        {{ link.name }}
      </RouterLink>
    </div>
  </div>
</template>
