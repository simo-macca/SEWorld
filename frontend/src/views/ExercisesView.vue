<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import SearchBar from '@/components/SearchComponents/SearchBar.vue'
import ResultSearchBar from '@/components/SearchComponents/ResultSearchBar.vue'
import ExerciseComponent from '@/components/ExerciseComponent.vue'
import AddButton from '@/components/UtilsComponents/AddButton.vue'
import Loader from '@/components/UtilsComponents/Loader.vue'
import { useCollapseStore } from '@/stores/isCollapse.js'
import { useTopicsStore } from '@/stores/topicsStore.js'
import { useSearch } from '@/stores/useSearch.js'
import { useUsersStore } from '@/stores/usersStore.js'
import { useExercisesStore } from '@/stores/exerciseStore.js'

const collapse = useCollapseStore()
const topicStore = useTopicsStore()
const userStore = useUsersStore()
const exercisesStore = useExercisesStore()
const exercises = computed(() => exercisesStore.exercises)
const isInstructor = computed(() => userStore.isInstructor)
const loading = ref(false)

const props = defineProps({ topicSlug: { type: String, required: true } })

const topicName = computed(() => {
  let name = topicStore.findCurrentTopic(props.topicSlug) || props.topicSlug
  return name.charAt(0).toUpperCase() + name.slice(1).split('-').join(' ')
})

const activeInstructorTab = ref('ALL')
const activeStudentTab = ref('ALL')

const instructorTabs = [
  { text: 'ALL', value: 'ALL' },
  { text: 'NOT DRAFT', value: 'NOT_DRAFT' },
  { text: 'DRAFT', value: 'DRAFT' },
]

const studentTabs = [
  { text: 'ALL', value: 'ALL' },
  { text: 'NOT COMPLETED', value: 'NOT_COMPLETED' },
  { text: 'COMPLETED', value: 'COMPLETED' },
]

const filterInstructorMap = {
  ALL: () => true,
  NOT_DRAFT: (ex) => !ex['exerciseIsDraft'],
  DRAFT: (ex) => ex['exerciseIsDraft'],
}
const exercisesInstructorFiltered = computed(() =>
  exercises.value.filter(filterInstructorMap[activeInstructorTab.value] || filterInstructorMap.ALL),
)

const filterStudentMap = {
  ALL: () => true,
  NOT_COMPLETED: (ex) => !ex['exerciseIsCompleted'],
  COMPLETED: (ex) => ex['exerciseIsCompleted'],
}
const exercisesStudentFiltered = computed(() =>
  exercises.value.filter(filterStudentMap[activeStudentTab.value] || filterStudentMap.ALL),
)

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
  loading.value = true
  try {
    await exercisesStore.getExercises()
  } finally {
    loading.value = false
  }
})
onUnmounted(() => window.removeEventListener('header-search', onHeaderSearch))
</script>

<template>
  <main class="relative min-h-screen">
    <SearchBar v-if="collapse.isCollapse" class="mb-4 lg:hidden" />
    <ResultSearchBar
      :searchQuery="searchQuery"
      :numSearched="exercisesSearched.length"
      :numElem="exercises.length"
      :clearSearch="clearSearch"
    />

    <div v-if="loading" class="mt-10 flex justify-center"><Loader /></div>

    <div
      v-else-if="searchQuery && exercisesSearched.length === 0"
      class="mt-20 flex flex-col items-center justify-center text-xl text-gray-500"
    >
      <p>
        No exercises found for: <b>"{{ searchQuery }}"</b>
      </p>
    </div>

    <div v-else>
      <h1 class="mt-4 mb-6 text-3xl font-bold text-gray-800 dark:text-gray-100">
        Exercise {{ topicName }}
      </h1>

      <div v-if="!searchQuery" class="mb-6 flex border-b border-gray-200 dark:border-gray-700">
        <template v-if="isInstructor">
          <button
            v-for="tab in instructorTabs"
            :key="tab.value"
            @click="activeInstructorTab = tab.value"
            class="flex-1 border-b-2 px-4 py-2 text-center font-medium transition-colors focus:outline-none"
            :class="
              activeInstructorTab === tab.value
                ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                : 'border-transparent text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200'
            "
          >
            {{ tab.text }}
          </button>
        </template>
        <template v-else>
          <button
            v-for="tab in studentTabs"
            :key="tab.value"
            @click="activeStudentTab = tab.value"
            class="flex-1 border-b-2 px-4 py-2 text-center font-medium transition-colors focus:outline-none"
            :class="
              activeStudentTab === tab.value
                ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                : 'border-transparent text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200'
            "
          >
            {{ tab.text }}
          </button>
        </template>
      </div>

      <div class="space-y-4">
        <div v-if="searchQuery">
          <div v-for="ex in exercisesSearched" :key="ex['exerciseDid']">
            <ExerciseComponent :exercise="ex" />
          </div>
        </div>
        <div v-else-if="isInstructor">
          <div v-for="ex in exercisesInstructorFiltered" :key="ex['exerciseDid']">
            <ExerciseComponent :exercise="ex" />
          </div>
        </div>
        <div v-else>
          <div v-for="ex in exercisesStudentFiltered" :key="ex['exerciseDid']">
            <ExerciseComponent :exercise="ex" />
          </div>
        </div>
      </div>
    </div>
    <AddButton v-if="isInstructor" :route="`/topics`" />
  </main>
</template>
