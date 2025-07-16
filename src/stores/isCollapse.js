// src/stores/collapse.js
import { defineStore } from 'pinia'
import { useWindowSize } from '@vueuse/core'
import { computed } from 'vue'

export const useCollapseStore = defineStore('collapse', () => {
  const { width } = useWindowSize()
  const isCollapse = computed(() => width.value <= 991)

  return { isCollapse }
})
