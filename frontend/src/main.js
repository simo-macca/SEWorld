import { createBootstrap } from 'bootstrap-vue-next'
import { Check, CircleAlert, TriangleAlert, Info, X } from 'lucide-vue-next'
import { createPinia } from 'pinia'
import { createApp, h } from 'vue'
// Core components
import Toast, { TYPE } from 'vue-toastification'

import App from './App.vue'
import router from './router'

// Stores
import { useThemeStore } from '@/stores/isDark.js'
//import { useNavStore } from '@/stores/navigation.js'

// Styles
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue-next/dist/bootstrap-vue-next.css'
import 'md-editor-v3/lib/style.css'
import 'md-editor-v3/lib/preview.css'
import '@/assets/style.css'

// Bootstrap JS for components
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

// Toastification
import 'vue-toastification/dist/index.css'

const app = createApp(App)

// Store + Pinia
const pinia = createPinia()
app.use(pinia)

// Initialize theme store
const theme = useThemeStore(pinia)
theme.init()

// const navStore = useNavStore(pinia)

// BootstrapVue plugin
app.use(createBootstrap())

// Vue Router plugin
app.use(router)

// Toastification plugin
app.use(Toast, {
  position: 'top-right',
  timeout: 3000,
  draggable: true,
  // Custom close button using the X icon
  closeButton: (props) => h(X, { ...props, size: 34, 'stroke-width': 3 }),
  // Center-align toast content
  toastClassName: 'd-flex align-items-center justify-content-center',
  showCloseButtonOnHover: true,
  // Default icons per toast type
  toastDefaults: {
    [TYPE.SUCCESS]: { icon: Check },
    [TYPE.WARNING]: { icon: CircleAlert },
    [TYPE.ERROR]: { icon: TriangleAlert },
    [TYPE.INFO]: { icon: Info },
  },
})

app.mount('#app')
