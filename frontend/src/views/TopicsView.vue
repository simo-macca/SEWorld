<script setup>
import { onMounted, onUnmounted, computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import SearchBar from '@/components/SearchComponents/SearchBar.vue'
import ResultSearchBar from '@/components/SearchComponents/ResultSearchBar.vue'
import Loader from '@/components/UtilsComponents/Loader.vue'
import AddButton from '@/components/UtilsComponents/AddButton.vue'
import { useCollapseStore } from '@/stores/isCollapse.js'
import { useTopicsStore } from '@/stores/topicsStore.js'
import { useSearch } from '@/stores/useSearch.js'
import { useUsersStore } from '@/stores/usersStore.js'
import TopicCard from '@/components/TopicComponents/TopicCard.vue'
import TopicModal from '@/components/TopicComponents/TopicModal.vue'
import ConfirmModal from '@/components/UtilsComponents/ConfirmModal.vue'
import EmptyState from '@/components/UtilsComponents/EmptyState.vue'

const collapse = useCollapseStore()
const topicsStore = useTopicsStore()
const userStore = useUsersStore()
const router = useRouter()
const loading = ref(false)
const topics = computed(() => topicsStore.topics)
const isUpdating = ref(false)
const isCreating = ref(false)
const isDeleting = ref(false)
const topicStore = useTopicsStore()
const isInstructor = userStore.isInstructor

const searchFields = ['topicTitle']
const {
  searchQuery,
  filteredItems: topicSearched,
  setSearchQuery,
  clearSearch,
} = useSearch(topics, searchFields)

const onHeaderSearch = (e) => setSearchQuery(e.detail.query)

onMounted(async () => {
  window.addEventListener('header-search', onHeaderSearch)
  loading.value = true
  try {
    await topicsStore.getTopics(true)
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  window.removeEventListener('header-search', onHeaderSearch)
})

// Update the function signature to accept the topic object
function navigateTo(topic, view) {
  router.push({
    name: view,
    params: { topicSlug: topic.topicSlug },
    state: { topicTitle: topic.topicTitle },
  })
}

function getProgressColor(progress) {
  if (progress >= 80) return 'bg-green-500'
  if (progress >= 60) return 'bg-yellow-500'
  return 'bg-red-500'
}

async function deleteTopic(topicSlug) {
  try {
    await topicStore.deleteTopic(topicSlug)
  } catch (err) {
    console.error(err)
  }
}
</script>

<template>
  <!-- View of all the topics -->
  <main>
    <!-- Search Bar -->
    <SearchBar
      v-if="collapse.isCollapse && !loading"
      class="mb-4 lg:hidden"
      v-model="searchQuery"
      context="Topics"
      placeholder="Search topics..."
    />

    <!-- Result Search Bar -->
    <ResultSearchBar
      :searchQuery="searchQuery"
      :numSearched="topicSearched.length"
      :numElem="topics.length"
      :clearSearch="clearSearch"
    />

    <!-- Loading Spinner -->
    <div v-if="loading" class="mt-20 flex justify-center">
      <Loader />
    </div>

    <!-- Empty State - No Search Results -->
    <EmptyState
      v-else-if="searchQuery && topicSearched.length === 0"
      icon="🔍"
      title="No topics found"
      :description="`No topics match &quot;${searchQuery}&quot;`"
      actionText="Clear search"
      @action="clearSearch"
    />

    <!-- Empty State - No Topics -->
    <EmptyState
      v-else-if="!searchQuery && topics.length === 0"
      :icon="isInstructor ? '📝' : '📚'"
      :title="isInstructor ? 'No Topic yet' : 'No Topic available'"
      :description="
        isInstructor
          ? 'Create your first topic to get started'
          : 'Your instructor hasn\'t added any topics yet'
      "
    />

    <!-- Topics List -->
    <div v-else class="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
      <!-- Single Topic Card -->
      <div v-for="topic in topicSearched" :key="topic.topicDid" class="min-w-[20em]">
        <TopicCard
          :topic="topic"
          :progressClass="getProgressColor(topic.topicProgress)"
          :navigateFunction="navigateTo"
          @show-update="isUpdating = true"
          @show-delete="isDeleting = true"
        />
      </div>
    </div>

    <!-- Topic Modal for update -->
    <TopicModal
      v-if="topicStore.currentTopic"
      :is-open="isUpdating"
      :current-topic="topicStore.currentTopic"
      :isUpdating="true"
      @close="isUpdating = false"
    />

    <!-- Topic Modal for creation -->
    <TopicModal :is-open="isCreating" :isCreating="true" @close="isCreating = false" />

    <!-- Confirmation Modal for deletion of a topic -->
    <ConfirmModal
      v-if="topicStore.currentTopic"
      :isOpen="isDeleting"
      @close="isDeleting = false"
      title="Delete Topic"
      :message="`Are you sure you want to delete ${topicStore.currentTopic.topicTitle}?`"
      ok-title="Delete"
      ok-variant="outline-danger"
      @confirm="deleteTopic(topicStore.currentTopic.topicSlug)"
    />

    <!-- Add Topic Button -->
    <AddButton v-if="isInstructor" :route="`/topics`" @click="isCreating = true" />
  </main>
</template>
