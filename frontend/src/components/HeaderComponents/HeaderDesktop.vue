<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { RouterLink, useRouter, useRoute } from 'vue-router'
import { Menu, X } from 'lucide-vue-next'

import SearchBar from '@/components/SearchComponents/SearchBar.vue'
import MenuMobile from '@/components/HeaderComponents/MenuMobile.vue'
import UserDropdown from '@/components/HeaderComponents/UserDropdown.vue'
import NavLinks from '@/components/HeaderComponents/NavLinks.vue'
import { useCollapseStore } from '@/stores/isCollapse.js'
import { useUsersStore } from '@/stores/usersStore.js'

// --- Stores & Hooks ---
const collapse = useCollapseStore()
const usersStore = useUsersStore()
const router = useRouter()
const route = useRoute()

// --- State ---
const headerSearchQuery = ref('')
const isMobileMenuOpen = ref(false)

// --- Data ---
const navLinks = [
  { name: 'Topic', to: '/topics', routeName: 'Topics', isCustom: false },
  { name: 'Material', to: '/materials', routeName: 'Materials', isCustom: true },
  { name: 'About', to: '/about', routeName: 'About', isCustom: false },
  { name: 'Prova', to: '/prova', routeName: 'Prova', isCustom: false },
]

// --- Computed ---
const searchContext = computed(() => {
  switch (route.name) {
    case 'Topics':
      return { context: 'Topics', placeholder: 'Search topics...' }
    case 'Materials':
      return { context: 'Materials', placeholder: 'Search materials...' }
    case 'Exercises':
      return { context: 'Exercises', placeholder: 'Search exercises...' }
    case 'Profile':
      return { context: 'Profile', placeholder: 'Search profile...' }
    default:
      return { context: 'Search', placeholder: 'Search...' }
  }
})

const logoLink = computed(() => {
  return usersStore.loggedIn ? '/topics' : '/'
})

// --- Methods ---
function goToMaterial() {
  router.push({ name: 'Materials' })
  closeMenus()
}

function closeMenus() {
  isMobileMenuOpen.value = false
}

function toggleMobileMenu() {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

function emitHeaderSearch(query) {
  window.dispatchEvent(
    new CustomEvent('header-search', { detail: { query, currentRoute: route.name } }),
  )
}

// --- Lifecycle & Watchers ---
onMounted(() => {
  if (!usersStore.loggedIn) {
    usersStore.getUser()
  }
})

watch(headerSearchQuery, emitHeaderSearch)

watch(
  () => route.name,
  () => {
    headerSearchQuery.value = ''
    closeMenus()
  },
)
</script>

<template>
  <!-- Header for any devices -->
  <header
    class="sticky top-0 z-40 mb-6 rounded-b-2xl border-b-4 border-gray-200 bg-white/95 shadow-lg backdrop-blur-sm dark:border-gray-700 dark:bg-gray-800/95"
  >
    <nav class="w-full px-4 py-4 lg:px-6">
      <!-- Logo name -->
      <div class="flex items-center justify-between">
        <RouterLink
          :to="logoLink"
          class="bg-linear-to-r from-blue-600 to-cyan-500 bg-clip-text text-2xl font-extrabold text-transparent transition-transform hover:scale-105 dark:from-blue-400 dark:to-cyan-400"
        >
          SEWorld
        </RouterLink>

        <!-- Hamburger menu for mobile devices -->
        <button
          @click="toggleMobileMenu"
          class="rounded-lg p-2.5 text-gray-600 transition-colors hover:bg-gray-100 focus:ring-2 focus:ring-blue-500 focus:outline-none lg:hidden dark:text-gray-300 dark:hover:bg-gray-700"
          aria-label="Toggle menu"
        >
          <component :is="isMobileMenuOpen ? X : Menu" class="h-6 w-6" />
        </button>

        <div class="ml-8 hidden grow items-center justify-between gap-6 lg:flex">
          <!-- Navigation links -->
          <NavLinks :links="navLinks" :is-mobile="false" @custom-click="goToMaterial" />

          <!-- Search bar -->
          <SearchBar
            class="mx-4 max-w-lg grow"
            v-if="!collapse.isCollapse && !route.meta.hideSearchBar"
            v-model="headerSearchQuery"
            :context="searchContext.context"
            :placeholder="searchContext.placeholder"
          />

          <!-- User dropdown -->
          <UserDropdown v-if="!collapse.isCollapse" />
        </div>
      </div>

      <!-- Mobile menu -->
      <MenuMobile
        v-if="isMobileMenuOpen"
        :nav-links="navLinks"
        :is-collapse="collapse.isCollapse"
        @close="closeMenus"
        @custom-click="goToMaterial"
      />
    </nav>
  </header>
</template>
