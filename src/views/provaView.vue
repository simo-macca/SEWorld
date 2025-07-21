<template>
  <div class="container py-4">
    <b-tabs card>
      <b-tab title="Da completare" active>
        <div class="row">
          <div class="col-md-6 mb-4" v-for="exercise in incompleteExercises" :key="exercise.id">
            <b-card class="h-100">
              <h5>{{ exercise.title }}</h5>
              <p>{{ truncate(exercise.description) }}</p>
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <lucide-repeat aria-label="Tentativi" />
                  <small>{{ exercise.attempts.length }} tentativi</small>
                </div>
                <div>
                  <b-button variant="outline-primary" size="sm" @click="goToLast(exercise.id)">
                    <lucide-rotate-ccw /> Ultimo tentativo
                  </b-button>
                  <b-button
                    variant="outline-secondary"
                    size="sm"
                    class="ms-2"
                    @click="goToAll(exercise.id)"
                  >
                    <lucide-list /> Tutti
                  </b-button>
                </div>
              </div>
            </b-card>
          </div>
        </div>
      </b-tab>

      <b-tab title="Completati">
        <div class="row">
          <div class="col-md-6 mb-4" v-for="exercise in completedExercises" :key="exercise.id">
            <b-card class="h-100 border-success">
              <h5>
                <lucide-check-circle class="text-success me-1" />
                {{ exercise.title }}
              </h5>
              <p>{{ truncate(exercise.description) }}</p>
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <lucide-repeat aria-label="Tentativi" />
                  <small>{{ exercise.attempts.length }} tentativi</small>
                </div>
                <div>
                  <b-button variant="outline-primary" size="sm" @click="goToLast(exercise.id)">
                    <lucide-rotate-ccw /> Ultimo tentativo
                  </b-button>
                  <b-button
                    variant="outline-secondary"
                    size="sm"
                    class="ms-2"
                    @click="goToAll(exercise.id)"
                  >
                    <lucide-list /> Tutti
                  </b-button>
                </div>
              </div>
            </b-card>
          </div>
        </div>
      </b-tab>
    </b-tabs>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { BCard, BTabs, BTab, BButton } from 'bootstrap-vue-next'
import {
  List as LucideList,
  Repeat as LucideRepeat,
  RotateCcw as LucideRotateCcw,
  CheckCircle as LucideCheckCircle,
} from 'lucide-vue-next'

// Dati di esempio; sostituisci con API reale
const exercises = ref([
  {
    id: 1,
    title: 'Esercizio HTML & CSS',
    description: 'Creare una pagina web responsiva utilizzando Flexbox e Grid.',
    attempts: [
      { id: 101, score: 4, date: '2025-07-18' },
      { id: 102, score: 6, date: '2025-07-19' },
    ],
  },
  {
    id: 2,
    title: 'JavaScript Base',
    description: 'Implementare funzioni di manipolazione degli array.',
    attempts: [{ id: 201, score: 8, date: '2025-07-17' }],
  },
  {
    id: 3,
    title: 'Vue.js Componenti',
    description: 'Costruire un componente di input personalizzato.',
    attempts: [],
  },
])

const passingScore = 6

const bestScore = (attempts) => (attempts.length ? Math.max(...attempts.map((a) => a.score)) : 0)

const incompleteExercises = computed(() =>
  exercises.value.filter((ex) => bestScore(ex.attempts) < passingScore),
)
const completedExercises = computed(() =>
  exercises.value.filter((ex) => bestScore(ex.attempts) >= passingScore),
)

const truncate = (text, length = 100) => (text.length > length ? text.slice(0, length) + '…' : text)

function goToLast(exerciseId) {
  // router.push a pagina ultimo tentativo
  console.log("Vai all'ultimo tentativo di", exerciseId)
}

function goToAll(exerciseId) {
  // router.push a pagina lista tentativi
  console.log('Vai a tutti i tentativi di', exerciseId)
}
</script>

<style scoped>
.container {
  max-width: 960px;
}
</style>
