<script setup>
// Vue Router imports
import { RouterLink, useRouter } from 'vue-router'
import { computed, onMounted } from 'vue'

// Custom components and stores
import SearchBar from '@/components/SearchBar.vue'
import { useCollapseStore } from '@/stores/isCollapse.js'
import { useTopicsStore } from '@/stores/topicsStore.js'
import { useThemeStore } from '@/stores/isDark.js'
import { useUsersStore } from '@/stores/usersStore.js'

// Initialize stores and router
const collapse = useCollapseStore()
const topicsStore = useTopicsStore()
const theme = useThemeStore()
const router = useRouter()

// Navigate to "Material" view and select topic ID if provided
function goToMaterial(did = null) {
  topicsStore.select(did)
  router.push({ name: 'Material' })
}

const usersStore = useUsersStore()

// Richiamo l'azione una volta montato
onMounted(() => {
  usersStore.getUser()
})

// Computed legge dallo stato reattivo
const userName = computed(() => {
  // 1) Prendo il nome completo, o null
  const fullName = usersStore.user?.['name']
  if (!fullName) {
    // se non c'è nome, torno il fallback "NF"
    return 'NF'
  }

  return fullName
    .split(' ')
    .map((part) => part.charAt(0).toUpperCase())
    .join('')
})
</script>

<template>
  <header class="container-fluid border-bottom border-4 rounded-bottom mb-3">
    <nav class="navbar navbar-expand-lg">
      <div class="container-fluid">
        <div class="navbar-brand">SEWorld</div>
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
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <RouterLink class="nav-link" to="/topics">Topic</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link" @click="goToMaterial()" to="/materials"
                >Material</RouterLink
              >
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link" to="/about">About</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link" to="/prova">Prova</RouterLink>
            </li>
            <li v-if="collapse.isCollapse"><hr class="x-divider" /></li>
            <li v-if="collapse.isCollapse">
              <RouterLink class="nav-link" to="/profile">Profile</RouterLink>
            </li>
            <li v-if="collapse.isCollapse">
              <RouterLink class="nav-link" to="/">Log out</RouterLink>
            </li>
          </ul>
          <SearchBar
            v-if="!collapse.isCollapse && !$route.meta.hideSearchBar"
            class="position-absolute top-50 start-50 translate-middle"
          />
          <ul v-if="!collapse.isCollapse" class="navbar-nav ms-auto mb-2 mb-lg-0 text-end">
            <li class="nav-item dropdown">
              <button
                :class="`nav-link rounded-circle d-flex align-items-center justify-content-center bg-info bg-gradient text-dark ${theme.isDark ? `down-arrow-dark` : `down-arrow-light`}`"
                style="height: 50px; width: 50px"
                data-bs-toggle="dropdown"
                aria-expanded="false"
              >
                {{ userName }}
              </button>
              <ul class="dropdown-menu" style="left: -80px">
                <li><RouterLink class="dropdown-item" to="/profile">Profile</RouterLink></li>
                <li><hr class="dropdown-divider" /></li>
                <li><RouterLink class="dropdown-item" to="/">Log out</RouterLink></li>
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
