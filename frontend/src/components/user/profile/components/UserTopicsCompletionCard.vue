<script>
import TopicCompletionCard from '@/components/user/profile/components/TopicCompletionCard.vue';

import { useStatisticsStore } from '@/stores/statistics';

export default {
	name: 'UserTopicsCompletionCard',

	components: {
		TopicCompletionCard,
	},

	props: {
		renderAsInstructor: {
			type: Boolean,
			required: false,
			default: false,
		},
		userDid: {
			type: String,
			required: false,
			default: undefined,
		},
	},

	computed: {
		statisticStores() {
			return useStatisticsStore();
		},
	},
};
</script>

<template>
	<div
		class="d-flex align-items-center justify-start flex-column gap-3 topic-scroll-container"
	>
		<TopicCompletionCard
			v-for="topic in this.statisticStores.getTopicsStatistics"
			:key="topic.topicDid"
			:topic="topic"
			:userDid="userDid"
			:renderAsInstructor="renderAsInstructor"
			class="w-100"
		/>
	</div>
</template>

<style scoped>
/* Scrollbar styles position */
.topic-scroll-container {
	max-height: 675px;
	overflow-y: scroll;
	padding-right: 10px;
	margin-right: -10px;
}

/* Scrollbar styles */
.topic-scroll-container::-webkit-scrollbar {
	width: 8px;
}
.topic-scroll-container::-webkit-scrollbar-track {
	margin-right: 5px;
}
.topic-scroll-container::-webkit-scrollbar-thumb {
	background: linear-gradient(to bottom, #ee7724, #d8363a, #dd3675, #b44593);
	border-radius: 4px;
}
</style>
