<template>
	<b-card class="mb-3 shadow-sm card">
		<div class="d-flex justify-content-between align-items-center">
			<h4 class="mb-0 pe-4 question" :title="questionText">
				{{ questionText }}
			</h4>

			<b-badge pill class="px-3 py-2" :variant="getStatus">
				{{ percentage }}%
			</b-badge>
		</div>

		<b-progress
			:value="percentage"
			max="100"
			height="6px"
			class="mt-2"
			:variant="getStatus"
		/>
	</b-card>
</template>

<script>
import { BBadge, BCard, BProgress } from 'bootstrap-vue-next';

export default {
	name: 'QuestionStat',
	components: { BCard, BProgress, BBadge },
	props: {
		questionText: {
			type: String,
			required: true,
		},
		percentage: {
			type: Number,
			required: true,
		},
	},

	computed: {
		getStatus() {
			const p = this.percentage;
			if (p >= 60) {
				return 'success';
			} else if (p >= 40) {
				return 'warning';
			} else {
				return 'danger';
			}
		},
	},
};
</script>
<style scoped>
.question {
	max-height: 140px;
	overflow: scroll;
	color: white;
}

.card {
	background: var(--primary-bg-color-dark);
}
</style>