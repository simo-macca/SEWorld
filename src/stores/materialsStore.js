import { defineStore } from 'pinia'

export const useMaterialsStore = defineStore('materials', {
  state: () => ({
    currentDid: null,
  }),
  actions: {
    topicSelected(did) {
      this.currentDid = did
    },
  },
})
