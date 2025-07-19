<script setup>
// Imports
// Vue APIs
import { useRouter } from 'vue-router'

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

// Router & Toast
const router = useRouter()

// Navigation Functions
function goToMaterial(did) {
  topicsStore.select(did)
  router.push({ name: 'Material' })
}

// Handles exercise navigation and shows all toast types for demonstration
function goToExercise(did) {
  topicsStore.select(did)
  router.push({ name: 'Exercises' })
}

// Static Data for Display
const obj = [
  {
    title: 'JavaScript Fundamentals',
    description:
      'Master the core concepts of JavaScript including variables, functions, objects, and control structures. Learn about ES6+ features and modern JavaScript development practices.',
    progress: '100',
    owner: 'Alex Johnson',
  },
  {
    title: 'React Component Architecture',
    description:
      'Deep dive into React components, hooks, state management, and component lifecycle. Build scalable applications using modern React patterns and best practices. Learn about component composition, prop drilling solutions, and performance optimization techniques for complex user interfaces.',
    progress: '85',
    owner: 'Maria Rodriguez',
  },
  {
    title: 'Database Design & SQL',
    description:
      'Learn relational database concepts, normalization, and advanced SQL queries. Understand indexing, transactions, and database optimization strategies.',
    progress: '75',
    owner: 'David Chen',
  },
  {
    title: 'Machine Learning Basics',
    description:
      'Introduction to machine learning algorithms, data preprocessing, and model evaluation. Explore supervised and unsupervised learning techniques.',
    progress: '60',
    owner: 'Sarah Wilson',
  },
  {
    title: 'API Development with Node.js',
    description:
      'Build RESTful APIs using Node.js and Express. Learn about middleware, authentication, error handling, and API documentation.',
    progress: '90',
    owner: 'Michael Brown',
  },
  {
    title: 'CSS Grid & Flexbox',
    description:
      'Master modern CSS layout techniques including Grid and Flexbox. Create responsive designs and understand CSS positioning.',
    progress: '95',
    owner: 'Emma Davis',
  },
  {
    title: 'Python Data Analysis',
    description:
      'Use Python libraries like Pandas, NumPy, and Matplotlib for data manipulation, analysis, and visualization.',
    progress: '45',
    owner: 'James Miller',
  },
  {
    title: 'Mobile App Development',
    description:
      'Learn React Native or Flutter to build cross-platform collapse applications. Understand collapse UI/UX principles and device-specific features.',
    progress: '30',
    owner: 'Lisa Anderson',
  },
  {
    title: 'Cloud Computing with AWS',
    description:
      'Explore cloud services, serverless architecture, and deployment strategies using Amazon Web Services.',
    progress: '55',
    owner: 'Robert Taylor',
  },
  {
    title: 'Version Control with Git',
    description:
      'Master Git workflows, branching strategies, and collaborative development practices. Learn about code reviews and repository management.',
    progress: '80',
    owner: 'Jennifer White',
  },
]
</script>

<template>
  <main>
    <SearchBar v-if="collapse.isCollapse" class="mobile" />
    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      <div class="col" v-for="(topic, index) in obj" :key="index" style="min-width: 20em">
        <b-card :header="topic.title" class="mb-3">
          <b-card-text class="m-3" style="min-height: 15em; max-height: 15em; overflow: auto">
            {{ topic.description }}
          </b-card-text>

          <b-progress
            :value="topic.progress"
            max="100"
            animated
            striped
            show-progress
            :variant="
              parseInt(topic.progress) < 60
                ? 'danger'
                : parseInt(topic.progress) >= 80
                  ? 'success'
                  : 'warning'
            "
            class="mt-2 text-center"
          />
          <template #footer>
            <div class="text-end">
              <b-button
                @click="goToExercise(topic.title)"
                class="me-2"
                :variant="theme.isDark ? 'outline-info' : 'info'"
                >Exercise</b-button
              >
              <b-button
                @click="goToMaterial(topic.title)"
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
