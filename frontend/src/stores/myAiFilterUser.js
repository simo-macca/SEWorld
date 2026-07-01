import { defineStore } from 'pinia';

const LOCAL_STORAGE_KEY = 'myAiFilterStoreUser';

export const SORTING_OPTIONS = [
	{ value: 'A-Z', text: 'A-Z' },
	{ value: 'Z-A', text: 'Z-A' },
	{ value: 'BR', text: 'Best Rating' },
	{ value: 'WR', text: 'Worst Rating' },
];

export const TYPE_OPTIONS_USER = [
	{
		text: 'Material',
		value: 'MATERIAL',
	},
	{
		text: 'Exercise',
		value: 'EXERCISE',
	},
];

export const TYPE_VISIBILITY_USER = [
	{
		text: 'Public',
		value: 'PUBLIC',
	},
	{
		text: 'Private',
		value: 'PRIVATE',
	},
];

export const useMyAiFilterStoreUser = defineStore('myAiFilterStoreUser', {
	state: () => ({
		sortBy: 'A-Z',
		filterMainTypes: [],
		filterVisibilityTypes: ['PRIVATE'],
	}),

	actions: {
		loadFromStorage() {
			const data = localStorage.getItem(LOCAL_STORAGE_KEY);
			if (data) {
				try {
					const parsed = JSON.parse(data);

					this.sortBy = parsed.sortBy ?? 'A-Z';
					this.filterMainTypes = parsed.filterMainTypes ?? [];
					this.filterVisibilityTypes =
						parsed.filterVisibilityTypes ?? ['PRIVATE'];
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
					filterVisibilityTypes: this.filterVisibilityTypes,
				}),
			);
		},

		setSortBy(value) {
			if (!SORTING_OPTIONS.some((so) => so.value == value)) {
				return;
			}

			this.sortBy = value;
			this.saveToStorage();
		},

		toggleMainType(type) {
			this.toggleType(TYPE_OPTIONS_USER, this.filterMainTypes, type);
		},

		toggleVisibilityType(type) {
			this.toggleType(
				TYPE_VISIBILITY_USER,
				this.filterVisibilityTypes,
				type,
			);
		},

		toggleType(TYPES, currentType, type) {
			// If type does not exist return
			if (!TYPES.some((to) => to.value == type)) {
				return;
			}

			const index = currentType.indexOf(type);
			if (index !== -1) {
				// Delete it if it is present
				currentType.splice(index, 1);
			} else {
				// Otherwise push it if it is not present
				currentType.push(type);
			}

			this.saveToStorage();
		},

		resetFilters() {
			this.sortBy = 'A-Z';
			this.filterMainTypes = [];
			this.filterVisibilityTypes = [];
			this.saveToStorage();
		},
	},

	getters: {
		isMainTypeSelected: (state) => (type) =>
			state.filterMainTypes.includes(type),
		isVisibiltyTypeSelected: (state) => (type) =>
			state.filterVisibilityTypes.includes(type),

		getMainTypesSelected: (state) =>
			// Adds possibility to add custom default
			state.filterMainTypes.length == 0 ? [] : state.filterMainTypes,
		getVisibilityTypesSelected: (state) =>
			state.filterVisibilityTypes.length == 0
				? []
				: state.filterVisibilityTypes,

		getSortBy: (state) => state.sortBy,
	},
});
