import { defineStore } from 'pinia'

export const useMaterialsStore = defineStore('materials', {
  state: () => ({
    currentDid: null,
  }),
  actions: {
    select(did) {
      this.currentDid = did
    },
  },
})
