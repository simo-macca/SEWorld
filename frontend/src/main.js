import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useThemeStore } from '@/stores/isDark.js'

const app = createApp(App)

app.use(createPinia())
app.use(router)

const themeStore = useThemeStore()
themeStore.applyTheme()

app.mount('#app')
