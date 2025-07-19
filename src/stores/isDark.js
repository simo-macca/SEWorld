import { defineStore } from 'pinia'

export const useThemeStore = defineStore('theme', {
  state: () => ({
    isDark: localStorage.getItem('theme') === 'dark',
  }),

  actions: {
    applyTheme() {
      const mode = this['isDark'] ? 'dark' : 'light'
      document.documentElement.setAttribute('data-bs-theme', mode)
      localStorage.setItem('theme', mode)
    },

    toggle() {
      this.isDark = !this['isDark']
      this.applyTheme()
    },

    init() {
      this.applyTheme()
    },
  },
})
