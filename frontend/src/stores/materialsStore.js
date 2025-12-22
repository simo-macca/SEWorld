import { defineStore } from 'pinia'
import { toast } from 'vue-sonner'

export const useMaterialsStore = defineStore('materials', {
  state: () => ({
    currentDid: null,
    materials: [],
  }),
  actions: {
    async getMaterial(did) {
      try {
        // const res = await fetch(`/api/material/${did}`)
        this.materials = [
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
            tags: ['python', 'programmazione', 'base', 'markdown'],
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
            content: `Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla in sapien tellus. Nulla nec mauris egestas, eleifend dui euismod, fermentum dolor. Mauris in tempus quam. Nullam bibendum et sapien nec faucibus. Pellentesque eu bibendum libero. Nam efficitur nulla ligula, vitae ullamcorper nulla fringilla id. Donec imperdiet suscipit congue. Nullam a suscipit velit, dignissim vehicula enim. Phasellus venenatis justo in neque maximus porta. Ut vitae luctus dolor, quis faucibus sem. Pellentesque pellentesque odio enim, vel semper sem egestas ut. Sed tempor nibh ex. Curabitur maximus lacus eu erat consectetur accumsan. Vivamus commodo dui lacus, non efficitur mauris egestas id. Sed ut sodales quam, at auctor quam.`,
            author: 'Prof. Ferrari',
            date: '2024-02-05',
            tags: ['storia', 'guerra mondiale'],
          },
        ]
        console.log('materials founds')
      } catch (err) {
        console.error(err)
        toast.error(err.response?.data?.message || err.message)
      }
    },
  },
})
