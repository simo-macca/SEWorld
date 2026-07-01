<script>
import {
	BCard,
	BCardText,
	BButton,
	BProgress,
	BProgressBar,
	BButtonGroup,
} from 'bootstrap-vue-next';

export default {
	name: 'TopicCard',
	components: {
		BCard,
		BCardText,
		BButton,
		BProgress,
		BProgressBar,
		BButtonGroup,
	},
	props: {
		topic: Object,
		isStudent: Boolean,
		materialsAvailable: Boolean,
		exercisesAvailable: Boolean,
	},
	methods: {
		isDisabled(type) {
			return !((this.isStudent && type) || !this.isStudent);
		},
	},
};
</script>

<template>
	<BCard
		class="topic-card"
		header-tag="header"
		header-border-variant="white"
		header-text-variant="white"
		text-variant="white"
	>
		<template #header>
			<div
				class="d-flex justify-content-between align-items-center flex-column flex-md-row w-100 gap-2"
			>
				<h2 class="mb-0 font-weight-bold text-capitalize">
					{{ topic.title }}
				</h2>
				<BButtonGroup>
					<BButton
						:disabled="isDisabled(materialsAvailable)"
						:to="`/materials/${topic.did}`"
						variant="primary"
					>
						{{
							isDisabled(materialsAvailable)
								? 'No materials'
								: 'Materials'
						}}
					</BButton>
					<BButton
						:disabled="isDisabled(exercisesAvailable)"
						:to="`/exercises/${topic.did}`"
						variant="primary"
						>{{
							isDisabled(exercisesAvailable)
								? 'No exercises'
								: 'Exercises'
						}}</BButton
					>
				</BButtonGroup>
			</div>
		</template>

		<BCardText class="mt-2"> {{ topic.description }} </BCardText>
		<div>
			<p v-if="topic.exercises && isStudent" class="little-text">
				% of completed exercises
			</p>
			<BProgress striped v-if="topic.exercises && isStudent">
				<BProgressBar
					:value="topic.completion"
					variant="info"
					class="position-relative text-center"
				/>
				<span class="progress-label"
					>{{ Math.round(topic.completion) }}%</span
				>
			</BProgress>
			<p class="little-text" v-else-if="!topic.exercises && isStudent">
				No exercises to complete yet
			</p>
		</div>
	</BCard>
</template>

<style scoped>
.little-text {
	font-size: large;
}

.topic-card {
	height: 20vh;
	max-height: 450px;
	min-height: 350px;
	width: 60vw;
	max-width: 700px;
	min-width: 280px;
	display: flex;
	flex-direction: column;
	padding: 1.5rem;
	background-color: #0a192f; /* Slightly lighter than the background */
	border-radius: 10px;
	box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
	transition: transform 0.2s ease-in-out;
}

.topic-card:hover {
	transform: translateY(-5px);
}

::v-deep(div.card-body) {
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	padding: 0;
}

::v-deep(header.card-header) {
	padding-left: 0;
	padding-right: 0;
}

::v-deep(p.card-text) {
	color: white;
	overflow-y: scroll;
	max-height: 170px;
	margin: 0;
	padding-right: 1rem;
	font-size: 1.3rem;

	scrollbar-width: thin;
	scrollbar-color: #888 #0a192f;
}

/* WebKit (Chrome, Safari, Edge) */
::v-deep(p.card-text::-webkit-scrollbar) {
	-webkit-appearance: none;
	width: 8px;
}

::v-deep(p.card-text::-webkit-scrollbar-track) {
	background: #0a192f;
}

::v-deep(p.card-text::-webkit-scrollbar-thumb) {
	background-color: #888;
	border-radius: 4px;
}

::v-deep(.progress-label) {
	position: absolute;
	width: 100%;
	text-align: center;
	left: 0;
	transform: translateY(-10%);
	color: black;
	font-weight: bold;
	pointer-events: none;
}

div.central-container {
	height: 50%;
}
</style>

