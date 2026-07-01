<template>
	<div class="answer-explanation mt-3">
		<div v-if="isIncorrect" class="explanation-card">
			<div class="tab-header">
				<div
					class="tab-item"
					:class="{ active: activeTab === 'yourAnswer' }"
					@click="activeTab = 'yourAnswer'"
				>
					Yours
				</div>
				<div
					class="tab-item"
					:class="{ active: activeTab === 'explanation' }"
					@click="activeTab = 'explanation'"
				>
					AI
				</div>
				<div
					v-if="question.type !== 'SHA'"
					class="tab-item"
					:class="{ active: activeTab === 'solution' }"
					@click="activeTab = 'solution'"
				>
					Public
				</div>
			</div>

			<div class="card-content">
				<div v-if="activeTab === 'yourAnswer'" class="student-answer">
					<slot name="my-answer"></slot>
				</div>

				<div v-if="activeTab === 'explanation'" class="explanation">
					<div class="powery-header">
						<div class="powery-header-text">
							<img
								src="../assets/images/power-ranger.png"
								alt="Power Ranger"
								class="ai-icon"
								width="24"
								height="24"
							/>
							<span class="powery-says"
								>On "<strong
									>question {{ questionIndex + 1 }}:</strong
								>
								{{ getQuestionText() }}" powery says...</span
							>
						</div>
						<RefreshButton
							v-if="question.type !== 'SHA'"
							:element-id="userAnswer.answerDID"
							@refresh="refreshAnswer"
						></RefreshButton>
					</div>

					<div class="explanation-content">
						<div class="answer-display-inline">
							<strong>Your answer:</strong>
							{{ getUserAnswerContent() }}
						</div>
						<hr />
						<div v-if="isLoading" class="loading-state">
							<p>Loading explanation...</p>
						</div>
						<div v-else-if="explanation" v-html="explanation"></div>
						<div v-else>
							<p>No explanation available yet.</p>
						</div>
					</div>
				</div>

				<div v-if="activeTab === 'solution'" class="solution">
					<div class="public-header">
						<div class="public-icon-wrapper">
							<svg
								class="public-icon"
								xmlns="http://www.w3.org/2000/svg"
								width="16"
								height="16"
								viewBox="0 0 24 24"
								fill="none"
								stroke="currentColor"
								stroke-width="2"
								stroke-linecap="round"
								stroke-linejoin="round"
							>
								<path
									d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"
								></path>
								<circle cx="9" cy="7" r="4"></circle>
								<path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
								<path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
							</svg>
						</div>
						<span class="public-title"
							>Professor's approved solutions</span
						>
					</div>

					<div class="question-display-inline">
						<strong>Question {{ questionIndex + 1 }}:</strong>
						{{ getQuestionText() }}
					</div>

					<div class="answer-display-inline">
						<strong>Your answer:</strong>
						{{ getUserAnswerContent() }}
					</div>

					<div class="d-flex flex-column gap-2 solution-container">
						<div v-if="isLoadingSolution" class="solution-content">
							<div class="loading-state">
								<p>Loading public solution...</p>
							</div>
						</div>
						<div
							v-else-if="
								professorSolution && professorSolution.length
							"
						>
							<div
								v-for="(solution, index) in professorSolution"
								:key="index"
								class="solution-content"
							>
								<div
									class="d-flex align-items-center justify-content-between"
								>
									<p>
										<strong
											>Explanation
											{{ index + 1 }}:</strong
										>
									</p>
									<div
										class="d-flex align-items-center justify-content-center gap-2"
									>
										<CommentPopup
											:ai-response-did="
												solution.aiResponseDid
											"
										/>
										<Rating
											:is-able-to-vote="!isInstructor"
											:ai-response-did="
												solution.aiResponseDid
											"
											:rating="solution.rating"
											:vote="solution.userRating"
											:dark="true"
										/>
									</div>
								</div>
								<div v-html="solution.answer"></div>
							</div>
						</div>
						<div v-else>
							<p>No public solution available yet.</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>
  
<script>
import RefreshButton from '@/components/buttons/RefreshButton.vue';
import Rating from './buttons/Rating.vue';
import { useUserStore } from '@/stores/user';

import CommentPopup from './popup/CommentPopup.vue';

export default {
	name: 'AnswerExplanation',

	components: {
		RefreshButton,
		Rating,
		CommentPopup,
	},

	props: {
		questionDid: {
			type: String,
			required: true,
		},
		userAnswer: {
			type: Object,
			required: false,
			default: () => ({}),
		},
		showExplanation: {
			type: Boolean,
			default: false,
		},
		isIncorrect: {
			type: Boolean,
			default: false,
		},
		question: {
			type: Object,
			required: false,
			default: () => ({}),
		},
		questionIndex: {
			type: Number,
			default: 0,
		},
	},

	data() {
		return {
			activeTab: 'yourAnswer',
			explanation: null,
			professorSolution: null,
			isLoading: false,
			isLoadingSolution: false,
			hasError: false,
			errorMessage: '',
		};
	},

	created() {
		// Log to debugging  **will need to delete this later**
		console.log('AnswerExplanation created with:', {
			isIncorrect: this.isIncorrect,
			questionDid: this.questionDid,
			question: this.question,
			userAnswer: this.userAnswer,
		});
	},

	watch: {
		isIncorrect(newVal) {
			if (newVal) {
				this.fetchExplanation();
				this.fetchProfessorSolution();
			}
		},
		activeTab(newVal) {
			if (
				newVal === 'explanation' &&
				!this.explanation &&
				this.isIncorrect
			) {
				this.fetchExplanation();
			} else if (
				newVal === 'solution' &&
				!this.professorSolution &&
				this.isIncorrect
			) {
				this.fetchProfessorSolution();
			}
		},
	},

	computed: {
		isInstructor() {
			return this.userStore().isInstructor;
		},
	},

	methods: {
		userStore() {
			return useUserStore();
		},

		async refreshAnswer(answerDid) {
			this.isLoading = true;
			this.hasError = false;

			try {
				const response = await fetch(
					`/api/auth/AI/ask/questions/explanation/refresh/${answerDid}`,
					{
						method: 'GET',
						credentials: 'include',
						headers: {
							'Content-Type': 'application/json',
						},
					}
				);

				if (!response.ok) {
					throw new Error(`HTTP error! Status: ${response.status}`);
				}

				const data = await response.json();
				this.explanation =
					data.data.answer || 'No explanation provided by the AI.';
			} catch (error) {
				console.error('Error fetching AI explanation:', error);
				this.hasError = true;
				this.errorMessage = error.message;
				this.explanation =
					'Failed to load explanation. Please try again later.';
			} finally {
				this.isLoading = false;
			}
		},
		// Helper method to get question text safely
		getQuestionText() {
			if (!this.question) return 'Question not available';
			return this.question.questionTitle || 'Question text not available';
		},

		// Helper method to get user answer content safely
		getUserAnswerContent() {
			if (!this.userAnswer) return 'No answer provided';
			let answ = this.userAnswer.answerContent;
			let question = this.question;
			if (question && question.type === 'MCH') {
				return question.choices[answ] || 'No answer content available';
			}
			return answ || 'No answer content available';
		},

		async fetchExplanation() {
			if (!this.userAnswer || !this.getAnswerDid()) {
				this.explanation = 'Cannot load explanation';
				return;
			}

			this.isLoading = true;
			this.hasError = false;

			try {
				const response = await fetch(
					`/api/auth/AI/ask/questions/explanation/new/${this.getAnswerDid()}`,
					{
						method: 'GET',
						credentials: 'include',
						headers: {
							'Content-Type': 'application/json',
						},
					}
				);

				if (!response.ok) {
					throw new Error(`HTTP error! Status: ${response.status}`);
				}

				const data = await response.json();
				this.explanation =
					data.data.answer || 'No explanation provided by the AI.';
			} catch (error) {
				console.error('Error fetching AI explanation:', error);
				this.hasError = true;
				this.errorMessage = error.message;
				this.explanation =
					'Failed to load explanation. Please try again later.';
			} finally {
				this.isLoading = false;
			}
		},

		async fetchProfessorSolution() {
			if (!this.userAnswer || !this.getAnswerDid()) {
				this.professorSolution =
					'Cannot load solution: Missing answer data';
				return;
			}

			this.isLoadingSolution = true;

			try {
				const response = await fetch(
					`/api/auth/AI/ask/questions/explanation/published/${this.getAnswerDid()}`,
					{
						method: 'GET',
						credentials: 'include',
						headers: {
							'Content-Type': 'application/json',
						},
					}
				);

				if (!response.ok) {
					throw new Error(`HTTP error! Status: ${response.status}`);
				}

				const data = await response.json();
				this.professorSolution =
					data.data || 'No public solution provided.';
			} catch (error) {
				console.error('Error fetching professor solution:', error);
				this.professorSolution =
					'Failed to load public solution. Please try again later.';
			} finally {
				this.isLoadingSolution = false;
			}
		},

		// Helper method to safely get the answer DID
		getAnswerDid() {
			return this.userAnswer?.answerDID || this.userAnswer?.DID;
		},
	},
};
</script>
  
<style scoped>
.answer-explanation {
	margin-bottom: 1.5rem;
}

.explanation-card {
	background-color: #ffffff;
	color: #1e293b;
	border: 1px solid #e2e8f0;
	border-radius: 12px;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
	overflow: hidden;
}

.tab-header {
	display: flex;
	background-color: #f8fafc;
	border-bottom: 1px solid #e2e8f0;
	width: 100%;
	padding: 0.5rem;
}

.tab-item {
	flex: 1;
	text-align: center;
	padding: 0.75rem;
	font-size: 1rem;
	font-weight: 500;
	color: #64748b;
	cursor: pointer;
	border-radius: 8px;
	margin: 0 0.25rem;
	position: relative;
}

.tab-item:hover {
	color: #1e293b;
	background-color: rgba(0, 0, 0, 0.03);
}

.tab-item.active {
	color: #1e293b;
	font-weight: 600;
	background-color: rgba(0, 0, 0, 0.02);
}

.tab-item.active::after {
	content: '';
	position: absolute;
	bottom: -1px;
	left: 25%;
	width: 50%;
	height: 3px;
	background-color: #3b82f6;
	border-radius: 3px 3px 0 0;
}

.card-content {
	background-color: #ffffff;
	padding: 1.5rem;
}

.student-answer,
.explanation,
.solution {
	display: flex;
	flex-direction: column;
}

.question-display {
	margin-bottom: 1rem;
	display: flex;
}

.question-number {
	font-size: 1.25rem;
	font-weight: 600;
	color: #1e293b;
	margin-right: 0.5rem;
}

.question-text {
	font-size: 1.25rem;
	color: #1e293b;
	line-height: 1.5;
}

.question-display-inline,
.answer-display-inline {
	margin-bottom: 0.75rem;
	font-size: 0.95rem;
}

.user-answer {
	background-color: rgba(239, 68, 68, 0.1);
	border: 1px solid rgba(239, 68, 68, 0.3);
	border-radius: 8px;
	padding: 0.75rem 1rem;
	color: #1e293b;
	font-size: 1rem;
	margin-top: 0.5rem;
}

.explanation-content,
.solution-content {
	background-color: #f8fafc;
	border-radius: 8px;
	padding: 1rem;
	border: 1px solid #e2e8f0;
	font-size: 1rem;
	max-height: 300px;
	overflow-y: auto;
}

.solution-content {
	height: fit-content;
	overflow-y: visible;
}

.solution-container {
	max-height: 300px;
	overflow-y: auto;
}

hr {
	margin: 0.75rem 0;
	border: 0;
	border-top: 1px solid #e2e8f0;
}

.public-header {
	display: flex;
	align-items: center;
	gap: 0.5rem;
	margin-bottom: 0.75rem;
}

.powery-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 0.5rem;
	margin-bottom: 0.75rem;
}

.powery-header-text {
	display: flex;
	align-items: center;
	gap: 0.5rem;
}

.public-icon-wrapper {
	width: 24px;
	height: 24px;
	border-radius: 50%;
	background-color: #e0e7ff;
	display: flex;
	align-items: center;
	justify-content: center;
}

.public-icon {
	color: #4f46e5;
}

.powery-says,
.public-title {
	font-weight: 600;
	font-size: 1rem;
	color: #1e293b;
}

.loading-state {
	text-align: center;
	color: #64748b;
	padding: 1rem 0;
}

p {
	color: #64748b;
	margin: 0.5rem 0;
}
</style>