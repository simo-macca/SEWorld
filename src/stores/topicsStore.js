import { defineStore } from 'pinia'

export const useTopicsStore = defineStore('topics', {
  state: () => ({
    currentDid: null,
  }),
  actions: {
    select(did) {
      this.currentDid = did
    },
  },
})
