<script setup>
import { FileText, ExternalLink, Download, File, UserRound } from 'lucide-vue-next'
import { computed, ref, onMounted, onUnmounted } from 'vue'
import SearchBar from '@/components/SearchComponents/SearchBar.vue'
import ResultSearchBar from '@/components/SearchComponents/ResultSearchBar.vue'
import Loader from '@/components/UtilsComponents/Loader.vue'
import MaterialComponent from '@/components/MaterialComponent.vue'
import { useCollapseStore } from '@/stores/isCollapse.js'
import { useTopicsStore } from '@/stores/topicsStore.js'
import { useMaterialsStore } from '@/stores/materialsStore.js'
import { useSearch } from '@/stores/useSearch.js'

const loading = ref(false)
const collapse = useCollapseStore()
const topicStore = useTopicsStore()
const materialsStore = useMaterialsStore()

const props = defineProps({ topicSlug: { type: String, default: '' } })

const topicName = computed(() => {
  if (history.state?.topicTitle) return history.state.topicTitle

  const topic = topicStore.findTopic(props.topicSlug)
  if (topic) return topic.topicTitle

  const text = props.topicSlug || ''
  return text.charAt(0).toUpperCase() + text.slice(1).split('-').join(' ')
})

const materials = [
  {
    id: 1,
    title: 'Introduzione alla Programmazione',
    description: 'Guida completa per iniziare con Python',
    type: 'markdown',
    content: `
# Benvenuto in MD‑Editor‑V3

Questo è un paragrafo di **prova**.

> Citazione in blocco

- Voce 1
- Voce 2
- Voce 3

\`\`\`js
// Blocco di codice
console.log('Ciao Markdown!')
\`\`\`

[Visita Vue.js](https://vuejs.org)  `,
    author: 'Prof. Rossi',
    date: '2024-01-15',
    tags: ['python', 'programmazione', 'base'],
  },
  {
    id: 2,
    title: 'Esercizi di Matematica',
    description: 'Raccolta di esercizi per il primo semestre',
    type: 'file',
    content: 'esercizi-matematica.pdf',
    author: 'Prof. Bianchi',
    date: '2024-02-01',
    tags: ['matematica', 'esercizi'],
  },
  {
    id: 3,
    title: 'Corso Online di Storia',
    description: 'Video lezioni complete sul Rinascimento',
    type: 'link',
    content: 'https://example.com/corso-storia',
    author: 'Prof. Verdi',
    date: '2024-01-20',
    tags: ['storia', 'rinascimento', 'video'],
  },
  {
    id: 4,
    title: 'Appunti di Chimica',
    description: 'Note dettagliate sulle reazioni chimiche',
    type: 'markdown',
    content: '## Reazioni Chimiche\n\nLe reazioni chimiche sono processi...',
    author: 'Prof. Neri',
    date: '2024-02-10',
    tags: ['chimica', 'reazioni'],
  },
  {
    id: 5,
    title: 'Slides Presentazione',
    description: 'Presentazione PowerPoint sulla Seconda Guerra Mondiale',
    type: 'Other',
    content: `Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla in sapien tellus. Nulla nec mauris egestas, eleifend dui euismod, fermentum dolor. Mauris in tempus quam. Nullam bibendum et sapien nec faucibus. Pellentesque eu bibendum libero. Nam efficitur nulla ligula, vitae ullamcorper nulla fringilla id. Donec imperdiet suscipit congue. Nullam a suscipit velit, dignissim vehicula enim. Phasellus venenatis justo in neque maximus porta. Ut vitae luctus dolor, quis faucibus sem. Pellentesque pellentesque odio enim, vel semper sem egestas ut. Sed tempor nibh ex. Curabitur maximus lacus eu erat consectetur accumsan. Vivamus commodo dui lacus, non efficitur mauris egestas id. Sed ut sodales quam, at auctor quam.

Vestibulum quam urna, iaculis vel pretium sit amet, aliquet a metus. In egestas mauris rutrum lorem ultrices, vitae fermentum augue lobortis. Ut porta, mi sed pulvinar feugiat, magna mauris tincidunt tortor, at ultricies mi erat id dui. Donec efficitur sodales neque, sed aliquam ligula rhoncus ac. Ut et velit mattis justo accumsan sodales eu vitae arcu. Nam nec aliquet tellus. Pellentesque in placerat dolor. Nunc egestas ullamcorper sapien, in bibendum ipsum placerat eu. Donec arcu nisl, tristique sit amet commodo vel, laoreet placerat mauris. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas euismod iaculis tellus, at blandit lorem bibendum a.

Sed sollicitudin eleifend lacus, ut imperdiet tortor porttitor condimentum. In laoreet rutrum ligula, vitae suscipit arcu ultrices a. Mauris lobortis velit massa, vel vulputate nunc ultrices id. Donec ultricies odio eu consequat vehicula. Quisque venenatis mauris ut lacus tincidunt, ut varius risus euismod. Etiam vitae pulvinar eros. Duis fermentum sem tellus, sit amet condimentum massa tincidunt et.

Aliquam tempus euismod lectus quis vestibulum. Pellentesque eget pharetra est. Curabitur vulputate metus velit, a placerat lorem ultrices et. Vestibulum est dui, sollicitudin vitae tellus id, faucibus aliquam dolor. Aliquam at commodo magna. Pellentesque nisi nulla, ornare a sem ac, luctus sollicitudin ligula. Phasellus in neque ac leo aliquam aliquet. Ut nec lobortis quam, rutrum feugiat arcu. Praesent et posuere lectus. Morbi sed pretium ex. Duis vehicula semper ultrices. Duis quis lacus sed elit vehicula lobortis. Etiam id odio in velit mattis eleifend. Morbi luctus hendrerit purus et elementum. Cras vestibulum cursus commodo.

Cras tempor, mi vel dapibus molestie, eros mauris tempus nisl, ut convallis ex justo in ante. Nunc viverra ligula id odio volutpat fermentum. Nullam a hendrerit nulla. Mauris sodales lectus eget augue vehicula, ut faucibus augue maximus. Nunc aliquet sed lacus sit amet euismod. Sed tortor quam, venenatis ac ligula at, aliquam dapibus lectus. Duis quis dolor ac orci luctus venenatis. Nulla non mi vel dolor cursus dignissim. Proin in est eget lorem posuere varius. Ut turpis libero, laoreet a tristique eu, aliquam sit amet mauris. Nullam venenatis turpis id tortor dignissim, eget eleifend dui hendrerit. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut non sapien rutrum, hendrerit ante non, elementum eros. Donec bibendum vestibulum ligula eu pharetra. Curabitur tristique varius tellus, sit amet condimentum felis cursus in. Sed tincidunt nisi lacus, at venenatis magna posuere non.`,
    author: 'Prof. Ferrari',
    date: '2024-02-05',
    tags: ['storia', 'guerra mondiale'],
  },
]

const searchFields = ['title']
const {
  searchQuery,
  filteredItems: materialsSearched,
  setSearchQuery,
  clearSearch,
} = useSearch(materials, searchFields) // Ensure 'materials' is defined

const onHeaderSearch = (e) => setSearchQuery(e.detail.query)

onMounted(async () => {
  window.addEventListener('header-search', onHeaderSearch)
  loading.value = true
  try {
    await materialsStore.getMaterial()
  } finally {
    loading.value = false
  }
})
onUnmounted(() => window.removeEventListener('header-search', onHeaderSearch))

// Tailwind Color mappings for icons
const typeStyles = {
  markdown: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200',
  file: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
  link: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200',
  default: 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200',
}
function getTypeStyle(type) {
  return typeStyles[type.toLowerCase()] || typeStyles.default
}
</script>

<template>
  <main class="relative min-h-screen">
    <SearchBar v-if="collapse.isCollapse" class="mb-4 lg:hidden" />
    <ResultSearchBar
      :searchQuery="searchQuery"
      :numSearched="materialsSearched.length"
      :numElem="materials.length"
      :clearSearch="clearSearch"
    />

    <div v-if="loading" class="mt-10 flex justify-center"><Loader /></div>
    <div
      v-else-if="searchQuery && materialsSearched.length === 0"
      class="mt-20 flex flex-col items-center justify-center text-xl text-gray-500"
    >
      <p>
        No material found for: <b>"{{ searchQuery }}"</b>
      </p>
    </div>

    <div v-else>
      <h1 class="mt-4 mb-6 text-3xl font-bold text-gray-800 dark:text-gray-100">
        Materials {{ topicName }}
      </h1>
      <div class="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <div v-for="obj in materials" :key="obj.id" class="min-w-[20em]">
          <div
            class="flex h-full flex-col rounded-lg border border-gray-200 bg-white p-4 shadow-sm dark:border-gray-700 dark:bg-gray-800"
          >
            <div class="mb-3 flex items-center gap-3">
              <div :class="[getTypeStyle(obj.type), 'rounded-lg p-2']">
                <component :is="getIconComponent(obj.type)" class="h-6 w-6" />
              </div>
              <span
                :class="[
                  getTypeStyle(obj.type),
                  'rounded-full px-3 py-1 text-xs font-bold tracking-wide uppercase',
                ]"
              >
                {{ obj.type }}
              </span>
            </div>

            <h3 class="mb-2 text-xl font-semibold text-gray-900 dark:text-white">
              {{ obj.title }}
            </h3>

            <div class="flex-grow">
              <MaterialComponent :material="obj" />
            </div>

            <div
              class="mt-4 flex items-center justify-between border-t border-gray-100 pt-2 text-sm text-gray-500 dark:border-gray-700 dark:text-gray-400"
            >
              <div class="flex items-center gap-1">
                <UserRound class="h-4 w-4" />
                <span>{{ obj.author }}</span>
              </div>
              <span>{{ obj.date }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>
