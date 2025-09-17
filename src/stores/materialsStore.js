import { defineStore } from 'pinia'

export const useMaterialsStore = defineStore('materials', {
  state: () => ({
    currentDid: null,
  }),
  actions: {
    async getMaterial(did) {
      this.currentDid = did
    },
  },
})
