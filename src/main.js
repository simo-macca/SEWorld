import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
// import { useNavStore } from "@/stores/navigation.js";

// Bootstrap styles
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue-next/dist/bootstrap-vue-next.css'

// Bootstrap logic
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import { createBootstrap } from 'bootstrap-vue-next'
import { useThemeStore } from '@/stores/isDark.js'

// Markdow
import 'md-editor-v3/lib/style.css'
import 'md-editor-v3/lib/preview.css'

// Global Styles
import '@/assets/style.css'

const app = createApp(App)

const pinia = createPinia()
app.use(pinia)
app.use(createBootstrap())
app.use(router)

const theme = useThemeStore(pinia)
theme.init()

app.mount('#app')
