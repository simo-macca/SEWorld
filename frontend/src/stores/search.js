import { defineStore } from 'pinia';

export const useSearchStore = defineStore('search', {
	state: () => ({
		word: '',
	}),

	actions: {
		resetWord() {
			this.word = '';
		},

		writeWord(word) {
			this.word = word;
		},
	},

	getters: {
		getWord: (state) => state.word,
	},
});
