<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import SearchBar from '@/components/SearchComponents/SearchBar.vue'
import ResultSearchBar from '@/components/SearchComponents/ResultSearchBar.vue'
import ExerciseComponent from '@/components/ExerciseComponent.vue'
import AddButton from '@/components/UtilsComponents/AddButton.vue'
import Loader from '@/components/UtilsComponents/Loader.vue'
import TabFilter from '@/components/TabFilter.vue'
import EmptyState from '@/components/UtilsComponents/EmptyState.vue'
import { useCollapseStore } from '@/stores/isCollapse.js'
import { useTopicsStore } from '@/stores/topicsStore.js'
import { useSearch } from '@/stores/useSearch.js'
import { useUsersStore } from '@/stores/usersStore.js'
import { useExercisesStore } from '@/stores/exerciseStore.js'
import ConfirmModal from '@/components/UtilsComponents/ConfirmModal.vue'

const collapse = useCollapseStore()
const topicStore = useTopicsStore()
const userStore = useUsersStore()
const exercisesStore = useExercisesStore()
const exercises = computed(() =>
  isInstructor.value
    ? exercisesStore.exercises
    : exercisesStore.exercises.filter((ex) => !ex.exerciseIsDraft),
)
const isInstructor = computed(() => userStore.isInstructor)
const isLoading = ref(false)
const isPublishing = ref(false)
const isDeleting = ref(false)

const props = defineProps({ topicSlug: { type: String, required: true } })

const topicName = computed(() => {
  if (history.state?.topicTitle) return history.state.topicTitle

  const topic = topicStore.findTopic(props.topicSlug)
  if (topic) return topic.topicTitle

  const text = props.topicSlug || ''
  return text.charAt(0).toUpperCase() + text.slice(1).split('-').join(' ')
})

const activeInstructorTab = ref('ALL')
const activeStudentTab = ref('ALL')

const instructorTabs = [
  {
    text: 'All Exercises',
    value: 'ALL',
    icon: '📚',
    count: computed(() => exercises.value.length),
  },
  {
    text: 'Published',
    value: 'NOT_DRAFT',
    icon: '✓',
    count: computed(() => exercises.value.filter((ex) => !ex.exerciseIsDraft).length),
  },
  {
    text: 'Drafts',
    value: 'DRAFT',
    icon: '📝',
    count: computed(() => exercises.value.filter((ex) => ex.exerciseIsDraft).length),
  },
]

const studentTabs = [
  {
    text: 'All Exercises',
    value: 'ALL',
    icon: '📚',
    count: computed(() => exercises.value.length),
  },
  {
    text: 'To Complete',
    value: 'NOT_COMPLETED',
    icon: '⏳',
    count: computed(() => exercises.value.filter((ex) => !ex.exerciseIsCompleted).length),
  },
  {
    text: 'Completed',
    value: 'COMPLETED',
    icon: '✓',
    count: computed(() => exercises.value.filter((ex) => ex.exerciseIsCompleted).length),
  },
]

const filterInstructorMap = {
  ALL: () => true,
  NOT_DRAFT: (ex) => !ex.exerciseIsDraft,
  DRAFT: (ex) => ex.exerciseIsDraft,
}

const exercisesInstructorFiltered = computed(() =>
  exercises.value.filter(filterInstructorMap[activeInstructorTab.value] || filterInstructorMap.ALL),
)

const filterStudentMap = {
  ALL: () => true,
  NOT_COMPLETED: (ex) => !ex.exerciseIsCompleted,
  COMPLETED: (ex) => ex.exerciseIsCompleted,
}

const exercisesStudentFiltered = computed(() =>
  exercises.value.filter(filterStudentMap[activeStudentTab.value] || filterStudentMap.ALL),
)

const currentFilteredExercises = computed(() => {
  if (searchQuery.value) return exercisesSearched.value
  return isInstructor.value ? exercisesInstructorFiltered.value : exercisesStudentFiltered.value
})

const searchFields = ['exerciseTitle']
const {
  searchQuery,
  filteredItems: exercisesSearched,
  setSearchQuery,
  clearSearch,
} = useSearch(exercises, searchFields)

const onHeaderSearch = (e) => setSearchQuery(e.detail.query)

onMounted(async () => {
  window.addEventListener('header-search', onHeaderSearch)
  isLoading.value = true
  try {
    await exercisesStore.getExerciseByTopic(props.topicSlug)
  } finally {
    isLoading.value = false
  }
})

onUnmounted(() => window.removeEventListener('header-search', onHeaderSearch))

async function confirmPublish(slug) {
  try {
    await useExercisesStore().publishExercise(slug)
  } catch (err) {
    console.error(err)
  }
}

async function confirmDelete(slug) {
  try {
    await useExercisesStore().deleteExercise(slug)
  } catch (err) {
    console.error(err)
  }
}
</script>

<template>
  <main>
    <!-- Mobile Search -->
    <SearchBar v-if="collapse.isCollapse && !isLoading" class="mb-4 lg:hidden" />

    <!-- Search Results Bar -->
    <ResultSearchBar
      :searchQuery="searchQuery"
      :numSearched="exercisesSearched.length"
      :numElem="exercises.length"
      :clearSearch="clearSearch"
    />

    <!-- Loading State -->
    <div v-if="isLoading" class="mt-20 flex justify-center">
      <Loader />
    </div>

    <!-- Content -->
    <div v-else>
      <!-- Header Section -->
      <div class="mb-8">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-4xl font-bold text-gray-900 dark:text-white">
              {{ topicName }}
            </h1>
            <p class="mt-2 text-sm text-gray-600 dark:text-gray-400">
              {{ isInstructor ? 'Manage your exercises' : 'Track your progress' }}
            </p>
          </div>
          <div
            v-if="!isLoading && exercises.length > 0"
            class="hidden items-center gap-2 rounded-lg bg-blue-50 px-4 py-2 sm:flex dark:bg-blue-900/20"
          >
            <span class="text-2xl font-bold text-blue-600 dark:text-blue-400">
              {{ exercises.length }}
            </span>
            <span class="text-sm text-gray-600 dark:text-gray-400">
              {{ exercises.length === 1 ? 'Exercise' : 'Exercises' }}
            </span>
          </div>
        </div>
      </div>

      <!-- Tab Filters -->
      <TabFilter
        v-if="!searchQuery && exercises.length > 0"
        :tabs="isInstructor ? instructorTabs : studentTabs"
        :activeTab="isInstructor ? activeInstructorTab : activeStudentTab"
        @update:activeTab="
          isInstructor ? (activeInstructorTab = $event) : (activeStudentTab = $event)
        "
        class="mb-8"
      />

      <!-- Empty State - No Search Results -->
      <EmptyState
        v-if="searchQuery && exercisesSearched.length === 0"
        icon="🔍"
        title="No exercises found"
        :description="`No exercises match &quot;${searchQuery}&quot;`"
        actionText="Clear search"
        @action="clearSearch"
      />

      <!-- Empty State - No Exercises -->
      <EmptyState
        v-else-if="!searchQuery && exercises.length === 0"
        :icon="isInstructor ? '📝' : '📚'"
        :title="isInstructor ? 'No exercises yet' : 'No exercises available'"
        :description="
          isInstructor
            ? 'Create your first exercise to get started'
            : 'Your instructor hasn\'t added any exercises yet'
        "
      />

      <!-- Empty State - Filtered Results -->
      <EmptyState
        v-else-if="!searchQuery && currentFilteredExercises.length === 0"
        icon="📭"
        title="No exercises here"
        :description="
          isInstructor
            ? activeInstructorTab === 'DRAFT'
              ? 'You don\'t have any draft exercises'
              : 'You don\'t have any published exercises'
            : activeStudentTab === 'COMPLETED'
              ? 'You haven\'t completed any exercises yet'
              : 'All exercises are completed! Great job!'
        "
      />

      <!-- Exercise List -->
      <div v-else class="space-y-5">
        <div
          v-for="ex in currentFilteredExercises"
          :key="ex.exerciseDid"
          class="hover:scale-[1.01]"
        >
          <ExerciseComponent
            :exercise="ex"
            @show-publish="isPublishing = true"
            @show-delete="isDeleting = true"
          />
        </div>
      </div>
    </div>

    <!-- Confirmation Modal for publishing of an exercise -->
    <ConfirmModal
      v-if="exercisesStore.currentExercise"
      :isOpen="isPublishing"
      @close="isPublishing = false"
      title="Publish Exercise"
      :message="`Are you sure you want to publish ${exercisesStore.currentExercise.exerciseTitle}?`"
      ok-title="Publish"
      ok-variant="outline-success"
      @confirm="confirmPublish(exercisesStore.currentExercise.exerciseSlug)"
    />

    <!-- Confirmation Modal for deletion of an exercise -->
    <ConfirmModal
      v-if="exercisesStore.currentExercise"
      :isOpen="isDeleting"
      @close="isDeleting = false"
      title="Delete Exercise"
      :message="`Are you sure you want to delete ${exercisesStore.currentExercise.exerciseTitle}? This action cannot be undone.`"
      ok-title="Delete"
      ok-variant="outline-danger"
      @confirm="confirmDelete(exercisesStore.currentExercise.exerciseSlug)"
    />

    <!-- Add Button for Instructors -->
    <AddButton v-if="isInstructor" :route="`/topics`" />
  </main>
</template>
