// Global Styles
import '@/assets/main.css';

// Bootstrap styles
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue-next/dist/bootstrap-vue-next.css';

// Bootsrap logic
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import { createBootstrap } from 'bootstrap-vue-next';

// Markdow
import 'md-editor-v3/lib/preview.css';

// Vue
import { createApp } from 'vue';

// Pinia
import { createPinia } from 'pinia';

// App entry
import App from './App.vue';

// Router
import router from './router';
import {useNavStore} from "@/stores/navigation.js";

const app = createApp(App);

const pinia = createPinia();
app.use(pinia);
app.use(createBootstrap());
app.use(router);

router.isReady().then(() => {
    const nav = useNavStore();

    // Ogni volta che la pagina sta per essere “nascosta” (reload, close, back/forward fuori SPA)
    window.addEventListener('pagehide', () => {
        // router.currentRoute è un Ref che contiene l’oggetto Route attuale
        const current = router.currentRoute.value.name || router.currentRoute.value.fullPath;
        if (current) {
            // salvo su sessionStorage e in Pinia
            nav.setPrevious(current);
        }
    });
});

app.mount('#app');
