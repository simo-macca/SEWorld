// src/stores/navigation.js
import { defineStore } from 'pinia';

export const useNavStore = defineStore('nav', {
    state: () => ({
        // Carico al bootstrap lo stato persistito
        previous: sessionStorage.getItem('previousRoute') || "home",
    }),
    actions: {
        setPrevious(routeName) {
            this.previous = routeName;
            sessionStorage.setItem('previousRoute', routeName);
        },
        clearPrevious() {
            this.previous = null;
            sessionStorage.removeItem('previousRoute');
        },
    },
});
