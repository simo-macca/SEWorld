<script>
import GradesGraphVue from '@/components/user/profile/components/GradesGraph.vue';
import GradesStatsCardVue from '@/components/user/profile/components/GradesStatsCard.vue';

import { useStatisticsStore } from '@/stores/statistics';

export default {
	name: 'UserGradesCard',

	components: {
		GradesGraphVue,
		GradesStatsCardVue,
	},

	props: {
		renderAsInstructor: {
			type: Boolean,
			required: false,
			default: false,
		},
	},

	computed: {
		statisticsStore() {
			return useStatisticsStore();
		},
		getStats() {
			return this.statisticsStore.getTopicsStatsStatistic;
		},
	},
};
</script>

<template>
	<div
		class="w-100 h-100 d-flex flex-column align-items-center justify-content-center gap-2 gap-sm-4 statistics-wrapper"
	>
		<!-- Grades Graph -->
		<div class="rounded p-1 w-100 graph-wrapper" style="height: 400px">
			<GradesGraphVue
				:key="$route.fullPath"
				:renderAsInstructor="renderAsInstructor"
			/>
		</div>

		<!-- User stats: Minimum, Average and Maximum grades -->
		<div
			v-if="this.getStats"
			class="d-flex justify-content-center text-center flex-wrap w-100 gap-2 gap-sm-3 gap-lg-5"
		>
			<!-- For instructor we calculate max, average, min out of all averages -->
			<GradesStatsCardVue
				:cardName="'Minimum'"
				:value="getStats.min.toString()"
			/>
			<GradesStatsCardVue
				:cardName="'Average'"
				:value="getStats.average.toString()"
			/>
			<GradesStatsCardVue
				:cardName="'Maximum'"
				:value="getStats.max.toString()"
			/>
		</div>
	</div>
</template>

<style scoped>
.statistics-wrapper {
	max-height: 675px;
}

.graph-wrapper {
	flex-grow: 1;

	max-height: 545px;
}
</style>