<template>
	<PrimaryButton
		:size="`xs`"
		title="edit"
		class="d-flex align-items-center justify-content-center"
		style="width: 38px; height: 38px"
		@click="goToEditPage"
	>
		<BxsEdit />
	</PrimaryButton>
</template>

<script>
import { BButton } from 'bootstrap-vue-next';
import BxsEdit from '~icons/bxs/edit';

export default {
	components: {
		BButton,
		BxsEdit,
	},
	props: {
		exerciseData: {
			type: Object,
			required: true,
		},
		topicId: {
			type: [String, Number],
			required: true,
		},
	},
	methods: {
		goToEditPage() {
			this.$router.push(
				`/exercise/edit/${this.exerciseData.exerciseDID}/${this.topicId}`
			);
		},
	},
};
</script>
