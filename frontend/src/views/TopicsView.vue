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

const searchFields = ['topicTitle']
const {
  searchQuery,
  filteredItems: filteredTopics,
  setSearchQuery,
  clearSearch,
} = useSearch(topics, searchFields)

const onHeaderSearch = (e) => setSearchQuery(e.detail.query)

onMounted(async () => {
  window.addEventListener('header-search', onHeaderSearch)
  loading.value = true
  try {
    await topicsStore.getTopics()
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  window.removeEventListener('header-search', onHeaderSearch)
})

function navigateTo(slug, view) {
  topicsStore.currentTopic = slug
  router.push({ name: view, params: { topicSlug: slug } })
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
  <main class="relative min-h-full">
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
      :numSearched="filteredTopics.length"
      :numElem="topics.length"
      :clearSearch="clearSearch"
    />

    <!-- Loading Spinner -->
    <Loader v-if="loading" class="mt-10 flex h-full items-center justify-center" />

    <!-- Topics List -->
    <div v-else>
      <!-- Empty Search Result -->
      <div
        v-if="searchQuery && filteredTopics.length === 0"
        class="mt-20 flex flex-col items-center justify-center text-xl text-gray-500 sm:text-3xl"
      >
        <p>No topics found for:</p>
        <p class="font-bold">"{{ searchQuery }}"</p>
      </div>

      <!-- Single Topic Card -->
      <div v-else class="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
        <div v-for="topic in filteredTopics" :key="topic.topicDid" class="min-w-[20em]">
          <TopicCard
            :topic="topic"
            :progressClass="getProgressColor(topic.topicProgress)"
            :navigateFunction="navigateTo"
            @show-modal="isUpdating = true"
            @show-confirm="isDeleting = true"
          />
        </div>
      </div>
    </div>

    <TopicModal
      v-if="isUpdating && topicStore.currentTopic"
      :is-open="isUpdating"
      :current-topic="topicStore.currentTopic"
      :isUpdating="true"
      @close="isUpdating = false"
    />

    <TopicModal
      v-if="isCreating"
      :is-open="isCreating"
      :isCreating="true"
      @close="isCreating = false"
    />

    <ConfirmModal
      v-if="isDeleting && topicStore.currentTopic"
      v-model="isDeleting"
      title="Delete Topic"
      :message="`Are you sure you want to delete ${topicStore.currentTopic.topicTitle}?`"
      ok-title="Delete"
      ok-variant="outline-danger"
      @confirm="deleteTopic(topicStore.currentTopic.topicSlug)"
    />

    <!-- Add Topic Button -->
    <AddButton v-if="userStore.isInstructor" :route="`/topics`" @click="isCreating = true" />
  </main>
</template>
