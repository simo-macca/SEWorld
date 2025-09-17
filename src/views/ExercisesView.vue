<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { BNav, BNavItem } from 'bootstrap-vue-next'

import SearchBar from '@/components/SearchBar.vue'
import { useCollapseStore } from '@/stores/isCollapse.js'
import { useTopicsStore } from '@/stores/topicsStore.js'
import { useSearch } from '@/stores/useSearch.js'
import ExerciseComponent from '@/components/ExerciseComponent.vue'
import { useUsersStore } from '@/stores/usersStore.js'
import { useExercisesStore } from '@/stores/exerciseStore.js'

const collapse = useCollapseStore()
const topicStore = useTopicsStore()
const userStore = useUsersStore()
const exercisesStore = useExercisesStore()

const exercises = computed(() => exercisesStore.exercises)
const isInstructor = computed(() => userStore.isInstructor)
const loading = ref(false)

const props = defineProps({
  topicSlug: {
    type: String,
    required: true,
  },
})

const topicName = computed(() => {
  let name = topicStore.findCurrentTopic(props.topicSlug) || props.topicSlug
  return name.charAt(0).toUpperCase() + name.slice(1).split('-').join(' ')
})

const activeInstructorTab = ref('ALL')

const instructorTabs = [
  { text: 'ALL', value: 'ALL' },
  { text: 'NOT DRAFT', value: 'NOT_DRAFT' },
  { text: 'DRAFT', value: 'DRAFT' },
]

function selectInstructorTab(val) {
  activeInstructorTab.value = val
}

const filterInstructorMap = {
  ALL: () => true,
  NOT_DRAFT: (ex) => !ex['exerciseIsDraft'],
  DRAFT: (ex) => ex['exerciseIsDraft'],
}

const exercisesInstructorFiltered = computed(() => {
  const predicate = filterInstructorMap[activeInstructorTab.value] || filterInstructorMap.ALL
  return exercises.value.filter(predicate)
})

const activeStudentTab = ref('ALL')

const studentTabs = [
  { text: 'ALL', value: 'ALL' },
  { text: 'NOT COMPLETED', value: 'NOT_COMPLETED' },
  { text: 'COMPLETED', value: 'COMPLETED' },
]

function selectStudentTab(val) {
  activeStudentTab.value = val
}

// qui definisci in un unico oggetto tutte le funzioni di filtro
const filterStudentMap = {
  ALL: () => true,
  NOT_COMPLETED: (ex) => !ex['exerciseIsCompleted'],
  COMPLETED: (ex) => ex['exerciseIsCompleted'],
}

const exercisesStudentFiltered = computed(() => {
  const predicate = filterStudentMap[activeStudentTab.value] || filterStudentMap.ALL
  return exercises.value.filter(predicate)
})

// Setup search composable: filters by topicTitle
const searchFields = ['exerciseTitle']
const {
  searchQuery,
  filteredItems: exercisesSearched,
  setSearchQuery,
  clearSearch,
} = useSearch(exercises, searchFields)

// Handle events from a header search component
const onHeaderSearch = (e) => setSearchQuery(e.detail.query)

onMounted(async () => {
  window.addEventListener('header-search', onHeaderSearch)
  loading.value = true
  try {
    await exercisesStore.getExercises()
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  window.removeEventListener('header-search', onHeaderSearch)
})
</script>

<template>
  <main>
    <!-- Mobile search bar -->
    <SearchBar v-if="collapse.isCollapse" class="mobile" />
    <!-- Search results info -->
    <ResultSearchBar
      :searchQuery="searchQuery"
      :numSearched="exercisesSearched.length"
      :numElem="exercises.length"
      :clearSearch="clearSearch"
    />
    <div v-if="loading">
      <LoaderComponent />
    </div>
    <div
      v-else-if="searchQuery && exercisesSearched.length === 0"
      class="h1 w-100 text-center position-absolute top-50 start-50 translate-middle"
    >
      No exercises found for: <br />
      "{{ searchQuery }}"
    </div>
    <div v-else class="container-fluid">
      <h1 class="mt-4 mb-4">Exercise {{ topicName }}</h1>

      <b-nav tabs justified v-if="!searchQuery">
        <template v-if="isInstructor">
          <b-nav-item
            v-for="tab in instructorTabs"
            :key="tab.value"
            :active="activeInstructorTab === tab.value"
            @click="selectInstructorTab(tab.value)"
            @focusin="selectInstructorTab(tab.value)"
          >
            {{ tab.text }}
          </b-nav-item>
        </template>
        <template v-else>
          <b-nav-item
            v-for="tab in studentTabs"
            :key="tab.value"
            :active="activeStudentTab === tab.value"
            @click="selectStudentTab(tab.value)"
            @focusin="selectStudentTab(tab.value)"
          >
            {{ tab.text }}
          </b-nav-item>
        </template>
      </b-nav>

      <div class="container-fluid pt-2 p-lg-0 pe-0">
        <div v-if="searchQuery">
          <div v-for="ex in exercisesSearched" :key="ex['exerciseDid']" class="mt-4 mb-4">
            <ExerciseComponent :exercise="ex" />
          </div>
        </div>
        <div v-else-if="isInstructor">
          <div v-for="ex in exercisesInstructorFiltered" :key="ex['exerciseDid']" class="mt-4 mb-4">
            <ExerciseComponent :exercise="ex" />
          </div>
        </div>
        <div v-else>
          <div v-for="ex in exercisesStudentFiltered" :key="ex['exerciseDid']" class="mt-4 mb-4">
            <ExerciseComponent :exercise="ex" />
          </div>
        </div>
      </div>
    </div>
    <AddButtonComponent v-if="isInstructor" :route="`/topics`" />
  </main>
</template>
