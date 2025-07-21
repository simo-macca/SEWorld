<script setup>
// Vue & Router APIs
import { BButton, BCard, BCardText, BProgress } from 'bootstrap-vue-next'
import { onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'

// Composition stores
import SearchBar from '@/components/SearchBar.vue'
import { useCollapseStore } from '@/stores/isCollapse.js'
import { useThemeStore } from '@/stores/isDark'
import { useTopicsStore } from '@/stores/topicsStore.js'
import { useSearch } from '@/stores/useSearch.js'

// UI Components

// Initialize stores
const theme = useThemeStore()
const collapse = useCollapseStore()
const topicsStore = useTopicsStore()
const router = useRouter()

// Reactive list of topics
const topics = computed(() => topicsStore.topics)

// Setup search composable: filters by topicTitle
const searchFields = ['topicTitle']
const {
  searchQuery,
  filteredItems: filteredTopics,
  setSearchQuery,
  clearSearch,
} = useSearch(topics, searchFields)

// Handle events from header search component
const onHeaderSearch = (e) => setSearchQuery(e.detail.query)

// Fetch topics on mount
onMounted(() => {
  topicsStore.getTopics()
})

onMounted(() => {
  window.addEventListener('header-search', onHeaderSearch)
})
onUnmounted(() => {
  window.removeEventListener('header-search', onHeaderSearch)
})

function navigateTo(slug, view) {
  topicsStore.currentTopic = slug
  router.push({ name: view, params: { topicSlug: slug } })
}
</script>

<template>
  <main>
    <!-- Mobile search bar -->
    <SearchBar
      v-if="collapse.isCollapse"
      class="mobile"
      v-model="searchQuery"
      context="Topics"
      placeholder="Search topics..."
    />

    <!-- Search results info -->
    <div v-if="searchQuery && filteredTopics.length" class="mb-3">
      <small class="text-muted">
        Showing {{ filteredTopics.length }} of {{ topics.length }} topics for "{{ searchQuery }}"
        <button class="btn btn-link btn-sm p-0 ms-2" @click="clearSearch">Show all</button>
      </small>
    </div>

    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      <!-- No results found -->
      <div
        v-if="searchQuery && filteredTopics.length === 0"
        class="h1 w-100 text-center position-absolute top-50 start-50 translate-middle"
      >
        No topics found for: <br />
        "{{ searchQuery }}"
      </div>

      <!-- Topic cards -->
      <div
        v-else
        class="col"
        v-for="topic in filteredTopics"
        :key="topic['topicDid']"
        style="min-width: 20em"
      >
        <b-card :header="topic['topicTitle']" class="mb-3">
          <b-card-text class="m-3" style="height: 15em; overflow: auto">
            {{ topic['topicDescription'] }}
          </b-card-text>

          <!-- Progress bar with dynamic variant -->
          <b-progress
            :value="topic['topicProgress']"
            max="100"
            animated
            striped
            show-progress
            :variant="
              {
                default:
                  topic['topicProgress'] >= 60 && topic['topicProgress'] < 80
                    ? 'warning'
                    : topic['topicProgress'] >= 80
                      ? 'success'
                      : 'danger',
              }.default
            "
            class="mt-2 text-center"
          />

          <!-- Action buttons -->
          <template #footer>
            <div class="text-end">
              <b-button
                @click="navigateTo(topic.topicSlug, 'Exercises')"
                class="me-2"
                :variant="theme.isDark ? 'outline-info' : 'info'"
                >Exercise</b-button
              >
              <b-button
                @click="navigateTo(topic.topicSlug, 'Materials')"
                :variant="theme.isDark ? 'outline-info' : 'info'"
                >Material</b-button
              >
            </div>
          </template>
        </b-card>
      </div>
    </div>
  </main>
</template>
