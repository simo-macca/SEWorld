<template>
	<Header />

	<div
		class="exercise-ai-feedback-container min-h-screen-header d-flex flex-column"
	>
		<div class="header-row w-100">
			<GoBackPage :page-before="previousViewName" />
			<h1>{{ exerciseTitle }} - AI feedback</h1>
		</div>

		<div v-if="isLoading" class="text-center py-5">
			<div class="spinner-border text-light" role="status">
				<span class="visually-hidden">Loading...</span>
			</div>
			<p class="mt-3">Loading AI feedback...</p>
		</div>

		<div v-else-if="error" class="text-center py-5">
			<h3>{{ errorMessage }}</h3>
			<BButton variant="primary" @click="loadData" class="mt-3"
				>Try Again</BButton
			>
		</div>

		<div v-else class="feedback-content">
			<div v-if="!questionsAndAnswers.length" class="text-center py-5">
				<h3>No incorrect short answers found for this exercise</h3>
			</div>

			<div v-else class="feedback-grid">
				<div class="question-list">
					<div
						v-for="(question, index) in questionsAndAnswers"
						:key="index"
						class="qa-card mb-4"
					>
						<div class="question-title">
							<h1>Question</h1>
							<p>{{ question.questionTitle }}</p>
						</div>
						<div class="">
							<h1>Answer</h1>
							<p>{{ question.questionCorrectAnswer }}</p>
						</div>

						<h5 class="wrong-answers-header">
							<div
								class="d-flex gap-2 align-items-center justify-content-center"
							>
								<span class="badge bg-danger ms-2">{{
									question.listOfAIEvaluations.reduce(
										(acc, curr) =>
											acc +
											(!curr.isAnswerCorrect ? 1 : 0),
										0
									)
								}}</span>
								Incorrect answers
							</div>

							<div
								class="d-flex gap-2 align-items-center justify-content-center"
							>
								<span class="badge bg-success ms-2">{{
									question.listOfAIEvaluations.reduce(
										(acc, curr) =>
											acc +
											(curr.isAnswerCorrect ? 1 : 0),
										0
									)
								}}</span>
								Correct answers
							</div>
						</h5>

						<div class="wrong-answers-list">
							<div
								v-for="(
									response, responseIndex
								) in question.listOfAIEvaluations"
								:key="responseIndex"
								class="mb-3 p-3"
								:class="{
									selected:
										selectedResponse ===
										`${index}-${responseIndex}`,
									'wrong-answer': !response.isAnswerCorrect,
									'correct-answer': response.isAnswerCorrect,
								}"
								@click="selectResponse(index, responseIndex)"
							>
								<div>
									<strong>Student:</strong>
									{{ response.userName }}
								</div>
								<div>
									<strong>Answer:</strong>
									{{ response.userAnswer }}
								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="explanation-panel">
					<div v-if="!selectedResponse" class="select-prompt">
						<div class="ns-header mb-4">
							<span class="ns-text"
								>Select an answer to see the AI feedback</span
							>
						</div>
					</div>

					<div v-else class="explanation-content">
						<div class="question-info">
							<div class="mb-3 question-info-text">
								<div class="d-flex align-items-center">
									<IconBiCheckLg
										v-if="currentResponse.isAnswerCorrect"
										class="success-icon me-2"
									/>
									<IconBiXCircle
										v-else
										class="error-icon me-2"
									/>
									<strong>Student's Answer:</strong>
								</div>

								<div :class="[currentResponse.isAnswerCorrect ? 'correct-answer-box' : 'incorrect-answer-box', 'p-2 mt-2']">
									{{ currentResponse?.userAnswer }}
								</div>
							</div>

							<AcceptDenyButtonsVue
								:elementId="currentResponse.aiEvalDid"
								@deny="denyEvaluation"
								@accept="acceptEvaluation"
							/>
						</div>

						<div class="explanation-container">
							<div class="explanation-header">
								<strong>AI Reasoning:</strong>
							</div>
							<div class="explanation p-3">
								{{ currentResponse?.aiReasoning }}
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import { useUserStore } from '@/stores/user';
import { useExerciseAIFeedbackStore } from "@/stores/exerciseAIFeedback";
import Header from '@/components/Header.vue';
import IconBiArrowLeft from '~icons/bi/arrow-left';
import IconBiXCircle from '~icons/bi/x-circle';
import IconBiCheckLg from '~icons/bi/check-lg';
import { BButton } from 'bootstrap-vue-next';

import GoBackPage from '@/components/GoBackPage.vue';
import { useNavStore } from '@/stores/navigation.js';
import AcceptDenyButtonsVue from '@/components/buttons/AcceptDenyButtons.vue';

export default {
	name: 'ExerciseAIFeedbackView',

	components: {
		Header,
		BButton,
		IconBiArrowLeft,
		IconBiXCircle,
		GoBackPage,
		AcceptDenyButtonsVue,
		IconBiCheckLg,
	},

	props: {
		exerciseDid: {
			type: String,
			required: true,
		},
	},

	data() {
		return {
			selectedResponse: null,
			userStore: useUserStore(),
			aiFeedbackStore: useExerciseAIFeedbackStore(),
		};
	},

	computed: {
		isLoading() {
			return this.aiFeedbackStore.isLoading;
		},
		error() {
			return this.aiFeedbackStore.error;
		},
		errorMessage() {
			return this.aiFeedbackStore.errorMessage;
		},
		exerciseTitle() {
			return this.aiFeedbackStore.exerciseTitle;
		},
		questionsAndAnswers() {
			return this.aiFeedbackStore.questionsAndAnswers;
		},
		currentSelection() {
			if (!this.selectedResponse) return null;
			const [qIndex, rIndex] = this.selectedResponse
				.split('-')
				.map(Number);
			return { qIndex, rIndex };
		},

		currentQuestion() {
			if (!this.currentSelection) return null;
			return this.questionsAndAnswers[this.currentSelection.qIndex];
		},

		currentResponse() {
			if (!this.currentSelection || !this.currentQuestion) return null;
			return this.currentQuestion.listOfAIEvaluations[
				this.currentSelection.rIndex
			];
		},

		previousViewName() {
			return this.navStore().previous || 'Exercise';
		},
	},

	async mounted() {
		if (!this.userStore.isInstructor) {
			await this.$router.replace({ path: '/' });
			return;
		}

		await this.loadData();
	},

	methods: {
		navStore() {
			return useNavStore();
		},

		async loadData() {
			await this.aiFeedbackStore.loadExerciseData(this.exerciseDid);
		},

		async denyEvaluation(aiEvaluationDid) {
			await this.aiFeedbackStore.denyEvaluation(aiEvaluationDid);
			this.selectedResponse = null;
			await this.loadData();
		},

		async acceptEvaluation(aiEvaluationDid) {
			await this.aiFeedbackStore.acceptEvaluation(aiEvaluationDid);
			this.selectedResponse = null;
			await this.loadData();
		},

		selectResponse(questionIndex, responseIndex) {
			this.selectedResponse = `${questionIndex}-${responseIndex}`;
		},
	},
};
</script>

<style scoped>
.wrong-answers-list {
	max-height: 280px;
	overflow-y: scroll;
}

.question-info {
	width: 100%;

	display: flex;
	flex-direction: row;
	justify-content: space-between;
	align-items: center;
}

.exercise-ai-feedback-container {
	padding: 20px;
	max-width: 1400px;
	margin: 0 auto;
	width: 100%;
}

.header-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30px;
}

.feedback-grid {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 20px;
	height: 100%;
}

.question-list {
	overflow-y: auto;
	max-height: 70vh;
	padding-right: 10px;
}

.qa-card {
	background-color: var(--primary-bg-color-mid-dark);
	border-radius: 8px;
	padding: 16px;
}

.question-title {
	margin-bottom: 10px;
	border-bottom: 1px solid rgba(255, 255, 255, 0.2);
	padding-bottom: 8px;
}

.wrong-answers-header {
	display: flex;
	align-items: center;
	gap: 15px;
	margin-bottom: 15px;
	padding-bottom: 8px;
	border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.error-icon {
	color: #dc3545;
	font-size: 1.2em;
}

.success-icon {
	color: #198754;
	font-size: 1.2em;
}

.wrong-answer {
	background-color: rgba(255, 255, 255, 0.05);
	border-radius: 8px;
	transition: all 0.3s ease;
	cursor: pointer;
	border-left: 3px solid #dc3545;
}

.correct-answer {
	background-color: rgba(255, 255, 255, 0.05);
	border-radius: 8px;
	transition: all 0.3s ease;
	cursor: pointer;
	border-left: 3px solid #198754;
}

.wrong-answer:hover,
.correct-answer:hover {
	background-color: rgba(255, 255, 255, 0.1);
}

.wrong-answer.selected,
.correct-answer.selected {
	border: 2px solid;
	background-color: rgba(180, 69, 147, 0.2);
}

.explanation-panel {
	background-color: var(--primary-bg-color-mid-dark);
	border-radius: 8px;
	padding: 20px;
	position: sticky;
	top: 20px;
	height: calc(70vh - 40px);
	display: flex;
	flex-direction: column;
}

.ns-header {
	display: flex;
	align-items: center;
	gap: 12px;
}

.ns-text {
	font-size: 1.2rem;
	font-weight: 600;
}

.incorrect-answer-box {
	background-color: rgba(220, 53, 69, 0.1);
	border-left: 3px solid #dc3545;
	border-radius: 4px;
}

.correct-answer-box {
	background-color: rgba(25, 135, 84, 0.1);
	border-left: 3px solid #198754;
	border-radius: 4px;
}

.explanation-content {
	display: flex;
	flex-direction: column;
	gap: 15px;
	height: 100%;
	overflow: hidden;
}

.explanation-container {
	margin-top: 20px;
	flex: 1;
	display: flex;
	flex-direction: column;
	overflow: hidden;
}

.explanation-header {
	margin-bottom: 10px;
}

.explanation {
	background-color: rgba(255, 255, 255, 0.05);
	border-radius: 8px;
	white-space: pre-line;
	flex: 1;
	overflow-y: scroll;
	padding: 15px;
}

.explanation::-webkit-scrollbar {
	width: 8px;
	display: block;
}

.explanation::-webkit-scrollbar-track {
	background: rgba(255, 255, 255, 0.1);
	border-radius: 4px;
}

.explanation::-webkit-scrollbar-thumb {
	background: rgba(255, 255, 255, 0.3);
	border-radius: 4px;
}

.explanation::-webkit-scrollbar-thumb:hover {
	background: rgba(255, 255, 255, 0.4);
}

.select-prompt {
	height: 100%;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-direction: column;
	text-align: center;
	color: rgba(255, 255, 255, 0.6);
}

@media (max-width: 992px) {
	.question-info-text {
		width: 100%;
	}

	.question-info {
		align-items: start;
		flex-direction: column;
	}

	.feedback-grid {
		grid-template-columns: 1fr;
	}

	.explanation-panel {
		position: static;
	}
}
</style> 