<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import SearchBar from '@/components/SearchComponents/SearchBar.vue'
import ResultSearchBar from '@/components/SearchComponents/ResultSearchBar.vue'
import Loader from '@/components/UtilsComponents/Loader.vue'
import { useCollapseStore } from '@/stores/isCollapse.js'
import { useTopicsStore } from '@/stores/topicsStore.js'
import { useMaterialsStore } from '@/stores/materialsStore.js'
import { useSearch } from '@/stores/useSearch.js'
import EmptyState from '@/components/UtilsComponents/EmptyState.vue'
import { useUsersStore } from '@/stores/usersStore.js'
import MaterialCard from '@/components/MaterialComponents/MaterialCard.vue'

const isLoading = ref(false)
const collapse = useCollapseStore()
const topicStore = useTopicsStore()
const materialsStore = useMaterialsStore()
const userStore = useUsersStore()
const isInstructor = userStore.isInstructor
const materials = computed(() => materialsStore.materials)

const props = defineProps({ topicSlug: { type: String, default: '' } })

const topicName = computed(() => {
  if (history.state?.topicTitle) return history.state.topicTitle

  const topic = topicStore.findTopicBySlug(props.topicSlug)
  if (topic) return topic.topicTitle

  const text = props.topicSlug || 'Materials'
  return text.charAt(0).toUpperCase() + text.slice(1).split('-').join(' ')
})

const currentFilteredMaterials = computed(() => {
  if (searchQuery.value) return materialsSearched.value
  return materials.value
})

const searchFields = ['title']
const {
  searchQuery,
  filteredItems: materialsSearched,
  setSearchQuery,
  clearSearch,
} = useSearch(materials, searchFields)

const onHeaderSearch = (e) => setSearchQuery(e.detail.query)

onMounted(async () => {
  window.addEventListener('header-search', onHeaderSearch)
  isLoading.value = true
  try {
    await materialsStore.getMaterial()
  } catch (err) {
    console.error(err)
  } finally {
    isLoading.value = false
  }
})
onUnmounted(() => window.removeEventListener('header-search', onHeaderSearch))
</script>

<template>
  <main>
    <!-- Mobile Search -->
    <SearchBar v-if="collapse.isCollapse && !isLoading" class="mb-4 lg:hidden" />

    <!-- Search Results Bar -->
    <ResultSearchBar
      :searchQuery="searchQuery"
      :numSearched="materialsSearched.length"
      :numElem="materials.length"
      :clearSearch="clearSearch"
    />

    <!-- Loading State -->
    <div v-if="isLoading" class="mt-20 flex justify-center">
      <Loader />
    </div>

    <div v-else>
      <!-- Enhanced Header -->
      <div class="mt-4 mb-8">
        <h1 class="mb-2 text-4xl font-bold text-gray-900 dark:text-gray-50">
          {{ topicName }}
        </h1>
        <p class="text-gray-600 dark:text-gray-400">
          {{ materials.length }} {{ materials.length === 1 ? 'material' : 'materials' }} available
        </p>
      </div>

      <!-- Empty State - No Search Results -->
      <EmptyState
        v-if="searchQuery && materialsSearched.length === 0"
        icon="🔍"
        title="No materials found"
        :description="`No materials match &quot;${searchQuery}&quot;`"
        actionText="Clear search"
        @action="clearSearch"
      />

      <!-- Empty State - No Materials -->
      <EmptyState
        v-else-if="!searchQuery && materials.length === 0"
        :icon="isInstructor ? '📝' : '📚'"
        :title="isInstructor ? 'No materials yet' : 'No materials available'"
        :description="
          isInstructor
            ? 'Create your first material to get started'
            : 'Your instructor hasn\'t added any materials yet'
        "
      />

      <!-- Enhanced Materials -->
      <div v-else class="w-full space-y-5">
        <div
          v-for="material in currentFilteredMaterials"
          :key="material.id"
          class="group relative transform hover:-translate-y-1"
        >
          <MaterialCard :material="material" />
        </div>
      </div>
    </div>
  </main>
</template>
