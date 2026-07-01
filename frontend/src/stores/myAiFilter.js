import { defineStore } from 'pinia';

const LOCAL_STORAGE_KEY = 'myAiFilters';

export const SORTING_OPTIONS = [
	{ value: 'A-Z', text: 'A-Z' },
	{ value: 'Z-A', text: 'Z-A' },
	{ value: 'BR', text: 'Best Rating' },
	{ value: 'WR', text: 'Worst Rating' },
];

export const TYPE_OPTIONS = [
	{
		text: 'Material',
		value: 'MATERIAL',
	},
	{
		text: 'Exercise',
		value: 'EXERCISE',
	},
];

export const useMyAiFilterStore = defineStore('myAiFilterStore', {
	state: () => ({
		// TODO state type defined
		public: {
			sortBy: 'A-Z',
			filterMainTypes: [],
		},
		private: {
			sortBy: 'A-Z',
			filterMainTypes: [],
		},
	}),

	actions: {
		loadFromStorage() {
			const data = localStorage.getItem(LOCAL_STORAGE_KEY);
			if (data) {
				try {
					const parsed = JSON.parse(data);

					// public
					this.public.sortBy = parsed.public.sortBy ?? 'A-Z';
					this.public.filterMainTypes =
						parsed.public.filterMainTypes ?? [];
					// private
					this.private.sortBy = parsed.private.sortBy ?? 'A-Z';
					this.private.filterMainTypes =
						parsed.private.filterMainTypes ?? [];
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
					public: {
						sortBy: this.public.sortBy,
						filterMainTypes: this.public.filterMainTypes,
					},
					private: {
						sortBy: this.private.sortBy,
						filterMainTypes: this.private.filterMainTypes,
					},
				}),
			);
		},

		setSortBy(isPublic = undefined, value) {
			if (isPublic === undefined) {
				return;
			}

			if (!SORTING_OPTIONS.some((so) => so.value == value)) {
				return;
			}

			if (isPublic) {
				this.public.sortBy = value;
			} else {
				this.private.sortBy = value;
			}

			this.saveToStorage();
		},

		toggleMainType(isPublic = undefined, type) {
			if (isPublic === undefined) {
				return;
			}

			if (!TYPE_OPTIONS.some((to) => to.value == type)) {
				return;
			}

			let key = '';
			if (isPublic) {
				key = 'public';
			} else {
				key = 'private';
			}

			const index = this[key].filterMainTypes.indexOf(type);
			if (index !== -1) {
				this[key].filterMainTypes.splice(index, 1);
			} else {
				this[key].filterMainTypes.push(type);
			}

			this.saveToStorage();
		},

		resetFilters() {
			// public
			this.public.sortBy = 'A-Z';
			this.public.filterMainTypes = [];
			// private
			this.private.sortBy = 'A-Z';
			this.private.filterMainTypes = [];
			this.saveToStorage();
		},
	},

	getters: {
		isMainTypeSelected: (state) => (isPublic, type) =>
			state[isPublic ? 'public' : 'private'].filterMainTypes.includes(
				type,
			),

		getMainTypesSelectedPublic: (state) =>
			state.public.filterMainTypes.length == 0
				? TYPE_OPTIONS.map((to) => to.value)
				: state.public.filterMainTypes,
		getMainTypesSelectedPrivate: (state) =>
			state.private.filterMainTypes.length == 0
				? TYPE_OPTIONS.map((to) => to.value)
				: state.private.filterMainTypes,

		getSortByPublic: (state) => state.public.sortBy,
		getSortByPrivate: (state) => state.private.sortBy,
	},
});
