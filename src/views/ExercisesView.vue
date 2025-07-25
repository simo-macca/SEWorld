<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { BNav, BNavItem } from 'bootstrap-vue-next'

import SearchBar from '@/components/SearchBar.vue'
import { useCollapseStore } from '@/stores/isCollapse.js'
import { useTopicsStore } from '@/stores/topicsStore.js'
import { useSearch } from '@/stores/useSearch.js'
import ExerciseComponent from '@/components/ExerciseComponent.vue'

const collapse = useCollapseStore()
const topicStore = useTopicsStore()

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

const activeTab = ref('ALL')

const tabs = [
  { text: 'ALL', value: 'ALL' },
  { text: 'NOT COMPLETED', value: 'NOT_COMPLETED' },
  { text: 'COMPLETED', value: 'COMPLETED' },
]

function selectTab(val) {
  activeTab.value = val
}

// qui definisci in un unico oggetto tutte le funzioni di filtro
const filterMap = {
  ALL: () => true,
  NOT_COMPLETED: (ex) => !ex.exerciseIsCompleted,
  COMPLETED: (ex) => ex.exerciseIsCompleted,
}

const exercisesFiltered = computed(() => {
  const predicate = filterMap[activeTab.value] || filterMap.ALL
  return exercises.value.filter(predicate)
})

const exercises = ref([
  {
    exerciseDid: '1',
    exerciseTitle: 'Algoritmo di Ordinamento Bubble Sort',
    exerciseDescription:
      "Implementare l'algoritmo di ordinamento Bubble Sort in Python. L'algoritmo deve ordinare un array di numeri interi in ordine crescente e stampare i passaggi intermedi.",
    exerciseCreatedDate: '2024-01-15',
    exerciseIsDraft: false,
    exerciseIsCompleted: true,
  },
  {
    exerciseDid: '2',
    exerciseTitle: 'Gestione Database Studenti',
    exerciseDescription:
      'Creare un sistema di gestione per un database di studenti utilizzando SQL. Includere operazioni CRUD (Create, Read, Update, Delete) e query per filtrare studenti per corso di laurea.',
    exerciseCreatedDate: '2024-01-20',
    exerciseIsDraft: true,
    exerciseIsCompleted: false,
  },
  {
    exerciseDid: '3',
    exerciseTitle: 'Calcolatrice Web con JavaScript',
    exerciseDescription:
      "Sviluppare una calcolatrice web utilizzando HTML, CSS e JavaScript. La calcolatrice deve supportare operazioni base (+, -, *, /) e avere un'interfaccia user-friendly.",
    exerciseCreatedDate: '2024-02-05',
    exerciseIsDraft: false,
    exerciseIsCompleted: true,
  },
  {
    exerciseDid: '4',
    exerciseTitle: 'Strutture Dati: Implementazione Stack',
    exerciseDescription:
      'Implementare una struttura dati Stack (pila) in Java con operazioni push, pop, peek e isEmpty. Includere test cases per verificare il corretto funzionamento.',
    exerciseCreatedDate: '2024-02-12',
    exerciseIsDraft: true,
    exerciseIsCompleted: false,
  },
  {
    exerciseDid: '5',
    exerciseTitle: 'Analisi Complessità Algoritmica',
    exerciseDescription:
      'Analizzare la complessità temporale e spaziale di diversi algoritmi di ricerca (lineare, binaria) e ordinamento (selection sort, merge sort). Fornire grafici comparativi.',
    exerciseCreatedDate: '2024-02-18',
    exerciseIsDraft: false,
    exerciseIsCompleted: true,
  },
  {
    exerciseDid: '6',
    exerciseTitle: 'Programmazione Object-Oriented: Sistema Biblioteca',
    exerciseDescription:
      'Progettare e implementare un sistema di gestione biblioteca utilizzando i principi OOP. Includere classi per Libro, Utente, Prestito con relativi metodi e attributi.',
    exerciseCreatedDate: '2024-03-01',
    exerciseIsDraft: true,
    exerciseIsCompleted: true,
  },
  {
    exerciseDid: '7',
    exerciseTitle: 'Reti e Protocolli: Server HTTP Semplice',
    exerciseDescription:
      'Creare un server HTTP basilare utilizzando Python che possa servire file statici e gestire richieste GET e POST. Implementare logging delle richieste.',
    exerciseCreatedDate: '2024-03-08',
    exerciseIsDraft: false,
    exerciseIsCompleted: false,
  },
  {
    exerciseDid: '8',
    exerciseTitle: 'Machine Learning: Classificazione Iris Dataset',
    exerciseDescription:
      'Implementare un algoritmo di classificazione per il dataset Iris utilizzando Python e scikit-learn. Confrontare diverse tecniche (KNN, Decision Tree, SVM).',
    exerciseCreatedDate: '2024-03-15',
    exerciseIsDraft: true,
    exerciseIsCompleted: false,
  },
  {
    exerciseDid: '9',
    exerciseTitle: 'Sicurezza Informatica: Crittografia Caesar Cipher',
    exerciseDescription:
      "Implementare l'algoritmo di crittografia Caesar Cipher con possibilità di cifrare e decifrare messaggi. Aggiungere funzionalità di brute force per decifrare senza chiave.",
    exerciseCreatedDate: '2024-03-22',
    exerciseIsDraft: false,
    exerciseIsCompleted: true,
  },
  {
    exerciseDid: '10',
    exerciseTitle: 'Sviluppo Mobile: App Gestione Task',
    exerciseDescription:
      "Sviluppare un'applicazione mobile per la gestione di task utilizzando React Native. L'app deve permettere di aggiungere, modificare ed eliminare task con persistenza locale.",
    exerciseCreatedDate: '2024-03-28',
    exerciseIsDraft: true,
    exerciseIsCompleted: false,
  },
  {
    exerciseDid: '11',
    exerciseTitle: 'Algoritmi su Grafi: Percorso Minimo',
    exerciseDescription:
      "Implementare l'algoritmo di Dijkstra per trovare il percorso minimo in un grafo pesato. Visualizzare graficamente il risultato e confrontare con altri algoritmi.",
    exerciseCreatedDate: '2024-04-05',
    exerciseIsDraft: false,
    exerciseIsCompleted: true,
  },
  {
    exerciseDid: '12',
    exerciseTitle: 'Sistemi Operativi: Simulatore Process Scheduler',
    exerciseDescription:
      'Creare un simulatore per algoritmi di scheduling dei processi (FIFO, SJF, Round Robin). Mostrare timeline di esecuzione e calcolare tempi medi di attesa.',
    exerciseCreatedDate: '2024-04-12',
    exerciseIsDraft: true,
    exerciseIsCompleted: false,
  },
])

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

// Fetch topics on mount
onMounted(() => {
  window.addEventListener('header-search', onHeaderSearch)
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
    <div
      v-if="searchQuery && exercisesSearched.length === 0"
      class="h1 w-100 text-center position-absolute top-50 start-50 translate-middle"
    >
      No exercises found for: <br />
      "{{ searchQuery }}"
    </div>
    <div v-else class="container-fluid">
      <h1 class="mt-4 mb-4">Exercise {{ topicName }}</h1>

      <b-nav tabs justified v-if="!searchQuery">
        <b-nav-item
          v-for="tab in tabs"
          :key="tab.value"
          :active="activeTab === tab.value"
          @click="selectTab(tab.value)"
          @focusin="selectTab(tab.value)"
        >
          {{ tab.text }}
        </b-nav-item>
      </b-nav>

      <div class="container-fluid pt-2">
        <div>
          <div v-if="searchQuery">
            <div v-for="ex in exercisesSearched" :key="ex.exerciseDid" class="mt-4 mb-4">
              <ExerciseComponent :exercise="ex" />
            </div>
          </div>
          <div v-else>
            <div v-for="ex in exercisesFiltered" :key="ex.exerciseDid" class="mt-4 mb-4">
              <ExerciseComponent :exercise="ex" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>
