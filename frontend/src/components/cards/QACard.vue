<script>
import ExplanationCard from '@/components/ExplanationCard.vue';
export default {
	name: 'QACard',

	components: {
		ExplanationCard,
	},

	props: {
		questionTitle: {
			type: String,
			required: true,
		},
		correctAnswer: {
			type: String,
			required: true,
		},
		shortAnswerList: {
			type: Array,
			required: true,
		},
	},
};
</script>

<template>
	<div class="qa-card">
		<!-- Left Div: Question Title and Correct Answer -->
		<div class="left-column">
			<h3>{{ questionTitle }}</h3>
			<p>
				<strong>Correct Answer:</strong> <br />
				{{ correctAnswer }}
			</p>
		</div>

		<div class="d-flex align-items-center justify-content-center">
			<div class="divisor" />
		</div>

		<!-- Right Div: Display items from shortAnswerList -->
		<div class="right-column custom-scrollbar">
			<ExplanationCard
				v-for="item in shortAnswerList"
				:key="item.shortAnswerDid"
				:shortAnswerDid="item.shortAnswerDid"
				:username="item.username"
				:user_answer="item.user_answer"
				:generated_AI_feedback="item.generated_AI_feedback"
			/>
		</div>
	</div>
</template>


<style scoped>
.qa-card {
	width: 100%;

	max-width: 1200px;

	box-sizing: border-box;

	display: grid;
	grid-template-columns: calc(34.5% - 16px) 1% calc(64.5% - 16px);
	align-items: center;
	justify-content: center;

	gap: 10px;

	border: 2px solid black;
	border-radius: 12px;
	background-color: #05050d23;
	padding: 16px;
}

.divisor {
	width: 1.5px;
	height: 150px;
	background: black;
}

.left-column {
	width: 100%;
	height: 100%;

	display: flex;
	flex-direction: column;
	align-content: center;
	justify-content: center;

	gap: 20px;
}

.right-column {
	width: 100%;
	height: 100%;

	max-height: 350px;

	overflow-y: scroll;
}

@media screen and (max-width: 786px) {
	.divisor {
		height: 1.5px;
		width: 150px;
	}

	.qa-card {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		gap: 10px;
	}
}
</style>