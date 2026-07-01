import { defineStore } from 'pinia';

const LOCAL_STORAGE_KEY = 'exercisesStatisticsFilter';

export const useExercisesStatisticsFilterStore = defineStore(
	'exercisesStatisticsFilter',
	{
		state: () => ({
			selectedSortKey: 'title',
		}),

		actions: {
			loadFromStorage() {
				const data = localStorage.getItem(LOCAL_STORAGE_KEY);
				if (data) {
					try {
						const parsed = JSON.parse(data);
						this.selectedSortKey =
							parsed.selectedSortKey ?? 'title';
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
						selectedSortKey: this.selectedSortKey,
					}),
				);
			},

			setSelectedSortKey(value) {
				this.selectedSortKey = value;
				this.saveToStorage();
			},

			resetFilters() {
				this.selectedSortKey = 'title';
				this.saveToStorage();
			},
		},
	},
);
