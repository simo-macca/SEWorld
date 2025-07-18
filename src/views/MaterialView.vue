<script setup>
import SearchBar from '@/components/SearchBar.vue'
import { useCollapseStore } from '@/stores/isCollapse.js'

import { BCard, BCardTitle } from 'bootstrap-vue-next'
import { FileText, UserRound, ExternalLink, Download, File } from 'lucide-vue-next'

const collapse = useCollapseStore()

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

const iconMap = {
  markdown: FileText,
  link: ExternalLink,
  file: Download,
}

const iconClass = {
  markdown: 'bg-primary-subtle',
  file: 'bg-success-subtle',
  link: 'bg-danger-subtle',
}

const iconColor = {
  markdown: 'rgb(0, 78, 255)',
  file: 'rgb(80, 164, 59)',
  link: 'rgb(255, 51, 66)',
}

const typeClass = {
  markdown: 'bg-primary-subtle text-primary-emphasis',
  file: 'bg-success-subtle text-success-emphasis',
  link: 'bg-danger-subtle text-danger-emphasis',
}

const getIconComponent = (type) => {
  const comp = iconMap[type.toLowerCase()]
  return comp || File
}

const getIconClass = (type) => {
  const comp = iconClass[type.toLowerCase()]
  return comp || 'bg-secondary-subtle'
}

const getIconColor = (type) => {
  const comp = iconColor[type.toLowerCase()]
  return comp || 'rgba(134, 134, 134)'
}

const getTypeClass = (type) => {
  const comp = typeClass[type.toLowerCase()]
  return comp || 'bg-secondary-subtle text-secondary-emphasis'
}
</script>

<template>
  <main>
    <SearchBar v-if="collapse.isCollapse" class="mobile" />
    <div class="container-fluid">
      <h1 class="mt-4">Test Markdown Preview</h1>
      <div class="">
        <div class="col" v-for="obj in materials" :key="obj.id" style="min-width: 20em">
          <b-card header-bg-variant="transparent" class="mb-4 shadow rounded">
            <template #header>
              <div class="d-flex align-items-center justify-content-start column-gap-3">
                <component
                  :is="getIconComponent(obj.type)"
                  :size="30"
                  :color="getIconColor(obj.type)"
                  :class="[getIconClass(obj.type), 'rounded', 'p-1']"
                />
                <span
                  :class="`${getTypeClass(obj.type)} rounded-pill p-2 pe-4 ps-4 text-capitalize`"
                  >{{ obj.type }}</span
                >
              </div>
            </template>
            <b-card-title>
              {{ obj.title }}
            </b-card-title>
            <MaterialComponent :material="obj" />
            <template #footer>
              <div
                class="d-flex align-items-center justify-content-between text-secondary-emphasis"
              >
                <div class="flex flex-wrap gap-1">
                  <UserRound />
                  {{ obj.author }}
                </div>
                <span>{{ obj.date }}</span>
              </div>
            </template>
          </b-card>
        </div>
      </div>
    </div>
  </main>
</template>
