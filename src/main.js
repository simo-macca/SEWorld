import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
// import { useNavStore } from "@/stores/navigation.js";

// Global Styles
import '@/assets/main.css';

// Bootstrap styles
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue-next/dist/bootstrap-vue-next.css';

// Bootsrap logic
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import { createBootstrap } from 'bootstrap-vue-next';

// Markdow
// import 'md-editor-v3/lib/preview.css';


const app = createApp(App)

const pinia = createPinia();
app.use(pinia);
app.use(createBootstrap());
app.use(router)

import('@/stores/isDark').then(({ useThemeStore }) => {
  const theme = useThemeStore(pinia)
  theme.init()
})

app.mount('#app')
