import { defineStore } from 'pinia';

const LOCAL_STORAGE_KEY = 'materialFilters';

export const useMaterialFilterStore = defineStore('materialFilter', {
	state: () => ({
		sortBy: 'A-Z',
		filterMainTypes: [],
		filterFileTypes: [],
	}),

	actions: {
		loadFromStorage() {
			const data = localStorage.getItem(LOCAL_STORAGE_KEY);
			if (data) {
				try {
					const parsed = JSON.parse(data);
					this.sortBy = parsed.sortBy ?? 'A-Z';
					this.filterMainTypes = parsed.filterMainTypes ?? [];
					this.filterFileTypes = parsed.filterFileTypes ?? [];
				} catch (e) {
					console.error(
						'Failed to parse filter store from localStorage',
						e,
					);
				}
			}
		},

		saveToStorage() {
			localStorage.setItem(
				LOCAL_STORAGE_KEY,
				JSON.stringify({
					sortBy: this.sortBy,
					filterMainTypes: this.filterMainTypes,
					filterFileTypes: this.filterFileTypes,
				}),
			);
		},

		setSortBy(value) {
			this.sortBy = value;
			this.saveToStorage();
		},

		toggleMainType(type) {
			const index = this.filterMainTypes.indexOf(type);
			if (index !== -1) {
				this.filterMainTypes.splice(index, 1);
			} else {
				this.filterMainTypes.push(type);
			}

			// Remove file types if 'file' is deselected
			if (!this.filterMainTypes.includes('file')) {
				this.filterFileTypes = [];
			}

			this.saveToStorage();
		},

		toggleFileType(types) {
			this.filterFileTypes = types;
			this.saveToStorage();
		},

		resetFilters() {
			this.sortBy = 'A-Z';
			this.filterMainTypes = [];
			this.filterFileTypes = [];
			this.saveToStorage();
		},
	},
});
