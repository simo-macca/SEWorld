<script setup>
// Imports
// Vue APIs
import { useRouter } from 'vue-router'
import { onMounted, computed } from 'vue'

// Components
import SearchBar from '@/components/SearchBar.vue'
import { BButton, BCard, BCardText, BProgress } from 'bootstrap-vue-next'

// Stores
import { useThemeStore } from '@/stores/isDark'
import { useCollapseStore } from '@/stores/isCollapse.js'
import { useTopicsStore } from '@/stores/topicsStore.js'

// Store & Plugin Initialization
const theme = useThemeStore()
const collapse = useCollapseStore()
const topicsStore = useTopicsStore()

onMounted(() => {
  useTopicsStore().getTopics()
})

// Router & Toast
const router = useRouter()

// Navigation Functions
function goToMaterial(topicDid, topicTitle) {
  router.push({
    name: 'Material',
    query: {
      topicDid: topicDid,
      topicName: topicTitle,
    },
  })
}

// Handles exercise navigation and shows all toast types for demonstration
function goToExercise(topicDid, topicTitle) {
  router.push({
    name: 'Exercises',
    query: {
      topicDid: topicDid,
      topicName: topicTitle,
    },
  })
}

const topics = computed(() => {
  return topicsStore.topics
})
</script>

<template>
  <main>
    <SearchBar v-if="collapse.isCollapse" class="mobile" />
    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      <div class="col" v-for="topic in topics" :key="topic['topicDid']" style="min-width: 20em">
        <b-card :header="topic['topicTitle']" class="mb-3">
          <b-card-text class="m-3" style="min-height: 15em; max-height: 15em; overflow: auto">
            {{ topic['topicDescription'] }}
          </b-card-text>

          <b-progress
            :value="topic['topicProgress']"
            max="100"
            animated
            striped
            show-progress
            :variant="
              parseInt(topic['topicProgress']) < 60
                ? 'danger'
                : parseInt(topic['topicProgress']) >= 80
                  ? 'success'
                  : 'warning'
            "
            class="mt-2 text-center"
          />
          <template #footer>
            <div class="text-end">
              <b-button
                @click="goToExercise(topic['topicDid'], topic['topicTitle'])"
                class="me-2"
                :variant="theme.isDark ? 'outline-info' : 'info'"
                >Exercise</b-button
              >
              <b-button
                @click="goToMaterial(topic['topicDid'], topic['topicTitle'])"
                class="me-2"
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
