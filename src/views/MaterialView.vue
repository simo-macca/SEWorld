<script setup>
import SearchBar from '@/components/SearchBar.vue'
import { useCollapseStore } from '@/stores/isCollapse.js'

import { MdPreview } from 'md-editor-v3'
import { useThemeStore } from '@/stores/isDark.js'
import { BCard, BCardText, BCardTitle } from 'bootstrap-vue-next'
import { FileText, UserRound } from 'lucide-vue-next'

const collapse = useCollapseStore()
const theme = useThemeStore()

const materials = [
  {
    id: 1,
    title: 'Introduzione alla Programmazione',
    description: 'Guida completa per iniziare con Python',
    type: 'markdown',
    content: '# Introduzione\n\nQuesto è un esempio di contenuto markdown...',
    author: 'Prof. Rossi',
    date: '2024-01-15',
    tags: ['python', 'programmazione', 'base'],
  },
  {
    id: 2,
    title: 'Esercizi di Matematica',
    description: 'Raccolta di esercizi per il primo semestre',
    type: 'file',
    fileUrl: '/files/esercizi-matematica.pdf',
    fileName: 'esercizi-matematica.pdf',
    fileSize: '2.3 MB',
    author: 'Prof. Bianchi',
    date: '2024-02-01',
    tags: ['matematica', 'esercizi'],
  },
  {
    id: 3,
    title: 'Corso Online di Storia',
    description: 'Videolezioni complete sul Rinascimento',
    type: 'link',
    url: 'https://example.com/corso-storia',
    domain: 'example.com',
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
    type: 'file',
    fileUrl: '/files/wwii-presentation.pptx',
    fileName: 'wwii-presentation.pptx',
    fileSize: '15.7 MB',
    author: 'Prof. Ferrari',
    date: '2024-02-05',
    tags: ['storia', 'guerra mondiale'],
  },
]
</script>

<template>
  <main>
    <SearchBar v-if="collapse.isCollapse" class="mobile" />
    <div class="container-fluid">
      <h1 class="mt-4">Test Markdown Preview</h1>
      <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
        <div class="col" v-for="(o, index) in materials" :key="index" style="min-width: 20em">
          <b-card header-bg-variant="transparent" class="mb-3">
            <template #header>
              <div class="d-flex align-items-center justify-content-start column-gap-3">
                <file-text
                  :class="`${theme.isDark ? `bg-info-subtle text-info-emphasis` : `bg-secondary-subtle`} rounded p-1`"
                  size="30"
                  color="rgb(78,128,238"
                ></file-text>
                <span
                  :class="`${theme.isDark ? `bg-primary bg-opacity-10 text-info-emphasis` : `bg-primary-subtle text-primary-emphasis`} rounded-pill p-2 pe-4 ps-4`"
                  >Document</span
                >
              </div>
            </template>
            <b-card-title>
              {{ o.title }}
            </b-card-title>
            <b-card-text
              class="m-3 d-flex flex-column justify-content-between align-items-start"
              style="min-height: 15em; max-height: 15em; overflow: auto"
            >
              <span>{{ o.description }}</span>

              <div class="d-flex flex-wrap gap-2 mt-4">
                <span
                  v-for="t in o.tags"
                  :key="t"
                  :class="`p-2 ${theme.isDark ? `bg-body-tertiary` : `bg-dark-subtle text-dark-emphasis`} rounded-pill`"
                >
                  #{{ t }}
                </span>
              </div>
            </b-card-text>

            <template #footer>
              <div
                class="d-flex align-items-center justify-content-between text-secondary-emphasis"
              >
                <div class="flex flex-wrap gap-1">
                  <UserRound />
                  {{ o.author }}
                </div>
                <span>{{ o.date }}</span>
              </div>
            </template>
          </b-card>
        </div>
      </div>
    </div>

    <MdPreview
      class="bg-transparent"
      :modelValue="text"
      language="en-US"
      :theme="`${theme.isDark ? 'dark' : 'light'}`"
      previewTheme="cyanosis"
      code-theme="qtcreator"
    />
  </main>
</template>
