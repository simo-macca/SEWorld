<script setup>
// Imports
import { RouterLink, useRouter, useRoute } from 'vue-router'
import { computed, onMounted, ref, watch } from 'vue'
import { useCollapseStore } from '@/stores/isCollapse.js'
import { useThemeStore } from '@/stores/isDark.js'
import { useUsersStore } from '@/stores/usersStore.js'
import SearchBar from '@/components/SearchBar.vue'

// Constants
const collapse = useCollapseStore()
const theme = useThemeStore()
const usersStore = useUsersStore()
const router = useRouter()
const route = useRoute()
const headerSearchQuery = ref('')

// Computed: derive user initials and search bar context
const userName = computed(() => {
  const name = usersStore.user?.['name']
  if (!name) return 'NF'
  return name
    .split(' ')
    .map((part) => part.charAt(0).toUpperCase())
    .join('')
})

const searchContext = computed(() => {
  // Determine placeholder/context based on current route
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

// Functions
function goToMaterial() {
  router.push({ name: 'Materials' })
}

function emitHeaderSearch(query) {
  window.dispatchEvent(
    new CustomEvent('header-search', {
      detail: { query, currentRoute: route.name },
    }),
  )
}

// Lifecycle: fetch user on mount
onMounted(() => {
  usersStore.getUser()
})

// Watchers
watch(headerSearchQuery, emitHeaderSearch)
watch(
  () => route.name,
  () => {
    headerSearchQuery.value = ''
  },
)
</script>

<template>
  <!-- Header container with bottom border and rounded corners -->
  <header class="container-fluid border-bottom border-4 rounded-bottom mb-3">
    <nav class="navbar navbar-expand-lg">
      <div class="container-fluid">
        <!-- Brand/logo -->
        <div class="navbar-brand">SEWorld</div>

        <!-- Mobile menu toggle button -->
        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarSupportedContent"
          aria-controls="navbarSupportedContent"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
        </button>

        <!-- Collapsible navigation links -->
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <!-- Static route links -->
            <li class="nav-item">
              <RouterLink class="nav-link" to="/topics">Topic</RouterLink>
            </li>
            <li class="nav-item">
              <!-- Prevent default to use custom navigation logic -->
              <RouterLink class="nav-link" to="/materials" @click.prevent="goToMaterial"
                >Material</RouterLink
              >
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link" to="/about">About</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link" to="/prova">Prova</RouterLink>
            </li>

            <!-- Additional links shown only when collapsed -->
            <li v-if="collapse.isCollapse"><hr class="x-divider" /></li>
            <li v-if="collapse.isCollapse">
              <RouterLink class="nav-link" to="/profile">Profile</RouterLink>
            </li>
            <li v-if="collapse.isCollapse">
              <RouterLink class="nav-link" to="/">Log out</RouterLink>
            </li>
          </ul>

          <!-- Central search bar shown when not collapsed and not hidden by route meta -->
          <SearchBar
            v-if="!collapse.isCollapse && !route.meta['hideSearchBar']"
            v-model="headerSearchQuery"
            class="position-absolute top-50 start-50 translate-middle"
            :context="searchContext.context"
            :placeholder="searchContext.placeholder"
          />

          <!-- User dropdown on the right -->
          <ul v-if="!collapse.isCollapse" class="navbar-nav ms-auto mb-2 mb-lg-0 text-end">
            <li class="nav-item dropdown">
              <!-- User initials button toggles dropdown -->
              <button
                :class="`nav-link rounded-circle d-flex align-items-center justify-content-center bg-info bg-gradient text-dark ${theme.isDark ? 'down-arrow-dark' : 'down-arrow-light'}`"
                style="height: 50px; width: 50px"
                data-bs-toggle="dropdown"
                aria-expanded="false"
              >
                {{ userName }}
              </button>
              <ul class="dropdown-menu dropdown-menu-end">
                <!-- Dropdown items -->
                <li>
                  <RouterLink class="dropdown-item" to="/profile">Profile</RouterLink>
                </li>
                <li><hr class="dropdown-divider" /></li>
                <li>
                  <RouterLink class="dropdown-item" to="/">Log out</RouterLink>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  </header>
</template>

<style scoped>
.down-arrow-light::after {
  margin-left: 1.2em !important;
  color: #323232;
}
.down-arrow-dark::after {
  margin-left: 1.2em !important;
  color: #d3d3d4;
}
</style>
