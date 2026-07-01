<template>
	<Header />
	<div class="container py-4">
		<!-- Back to exercises button -->
		<div class="mb-3">
			<button
				v-if="preview"
				class="btn btn-outline-secondary d-flex align-items-center"
				@click="goToEditPage"
			>
				<svg
					xmlns="http://www.w3.org/2000/svg"
					width="16"
					height="16"
					fill="currentColor"
					class="bi bi-arrow-left me-2"
					viewBox="0 0 16 16"
				>
					<path
						fill-rule="evenodd"
						d="M15 8a.5.5 0 0 0-.5-.5H2.707l3.147-3.146a.5.5 0 1 0-.708-.708l-4 4a.5.5 0 0 0 0 .708l4 4a.5.5 0 0 0 .708-.708L2.707 8.5H14.5A.5.5 0 0 0 15 8z"
					/>
				</svg>
				Back to editing
			</button>
			<GoBackPage v-else page-before="Exercise"></GoBackPage>
		</div>

		<div
			class="d-flex flex-column flex-lg-row justify-content-around align-items-start gap-4"
			style="height: 100%"
		>
			<div class="description-wrapper">
				<div
					class="title-wrapper d-flex align-items-center justify-content-center justify-content-lg-start w-100"
				>
					<h1 class="display-5 fw-bold">Description</h1>
				</div>
				<div class="markdown-wrapper">
					<MdPreview
						id="markdown"
						class="bg-transparent"
						:language="`en-US`"
						:previewTheme="`mk-cute`"
						:theme="'dark'"
						:modelValue="exerciseDescription"
					/>
				</div>
			</div>

			<div class="attempt-wrapper">
				<div
					class="title-wrapper d-flex align-items-center justify-content-center justify-content-lg-start w-100"
				>
					<h1 class="display-5 fw-bold">
						{{ preview ? 'Preview for' : 'Attempt for' }}
						{{ this.exerciseTitle }}
					</h1>
				</div>

				<div class="question-list-wrapper">
					<QuestionList
						v-if="Object.keys(this.userAnswers).length > 0"
						:questions="questions"
						:feedback="feedback"
						:show-feedback="showFeedback"
						:userAnswers="userAnswers"
						:submitted="this.submitted"
					/>
				</div>

				<div class="d-grid gap-2 mt-4">
					<button
						class="btn btn-outline-primary"
						@click="saveChanges"
						v-if="!submitted && !preview"
					>
						Save Changes
					</button>

					<button
						v-if="!submitted"
						class="btn btn-warning text-white fw-semibold shadow-sm"
						@click="submitAttempt"
					>
						Submit Attempt
					</button>

					<button
						v-if="submitted && !showFeedback"
						class="btn btn-success fw-semibold"
						@click="fetchFeedback"
					>
						See Feedback
					</button>
				</div>

				<div
					v-if="submitted && !showFeedback"
					class="alert alert-success text-center mt-3"
					role="alert"
				>
					Attempt saved
				</div>

				<div
					v-if="changesSaved && !showFeedback"
					class="alert alert-info text-center mt-2"
					role="alert"
				>
					Changes saved successfully!
				</div>

				<div
					v-if="showFeedback"
					class="alert alert-info text-center mt-2"
					role="alert"
				>
					Your grade:
					<span>{{ Math.round(feedbackGrade * 100) / 100 }}</span>
				</div>

				<button
					v-if="preview && submitted && showFeedback"
					class="btn btn-secondary fw-semibold"
					@click="goToEditPage"
				>
					Continue editing
				</button>
			</div>
		</div>
	</div>
</template>


<script>
import Header from '@/components/Header.vue';
import QuestionList from '@/components/QuestionList.vue';
import { useQuestionsStore } from '@/stores/questions';

import {
	BASE_AUTH_URL,
	CREATE_ANSWER_ROUTE,
	DELETE_ATTEMPT,
	GET_ATTEMPT_STATUS_ROUTE,
	GET_FEEDBACK_ROUTE,
	PATCH_UPDATE_ATTEMPT_AS_SUBMITTED,
	GET_EXERCISE_ROUTE_BY_EXERCISE_DID,
} from '@/utils/constants';
import router from '@/router/index.js';
import GoBackPage from '@/components/GoBackPage.vue';

import { MdPreview } from 'md-editor-v3';

const store = useQuestionsStore();

export default {
	name: 'AttemptView',

	components: {
		GoBackPage,
		Header,
		QuestionList,
		MdPreview,
	},

	props: {
		attemptDid: {
			type: String,
			required: true,
		},
		exerciseDidRoute: {
			type: String,
			required: true,
		},
	},

	data() {
		return {
			preview: false,
			questions: [],
			submitted: false,
			feedback: [],
			showFeedback: false,
			changesSaved: false,
			userAnswers: Object,
			exerciseTitle: 'Exercise',
			exerciseDescription: 'Description',
			feedbackGrade: 0,
			exerciseDid: null,
		};
	},

	async mounted() {
		this.preview = this.$route.query.preview === 'true';
		await this.fetchAttemptStatus(this.attemptDid);
		await store.fetchQuestionsByAttempt(this.attemptDid);
		this.questions = store.questions;

		await this.getExerciseTitleAndDescription();

		if (!this.showFeedback) {
			await this.createEmptyAnswersForQuestions();
		} else {
			await this.fetchFeedback();
		}
		await store.fetchUserAttemptAnswers(this.attemptDid);
		this.userAnswers = store.userAnswers;
	},

	beforeRouteLeave(to, from, next) {
		if (this.preview) {
			this.delPreviewAttempt();
		}
		next();
	},

	methods: {
		async getExerciseTitleAndDescription() {
			try {
				const response = await fetch(
					GET_EXERCISE_ROUTE_BY_EXERCISE_DID.replace(
						'{exercise_did}',
						this.exerciseDidRoute
					),
					{
						method: 'GET',
						credentials: 'include',
					}
				);

				const res = await response.json();
				console.log(res);
				this.exerciseTitle = res.data[0].exerciseTitle;
				this.exerciseDescription = res.data[0].exerciseDescription;
			} catch (err) {
				console.error("Couldn't get exercise: ", err);
			}
		},

		async delPreviewAttempt() {
			try {
				await fetch(`${DELETE_ATTEMPT}/${this.attemptDid}`, {
					method: 'DELETE',
					credentials: 'include',
				});
			} catch (err) {
				console.error(
					"Couldn't delete attempt. The attempt may have been saved!",
					err
				);
			}
		},

		router() {
			return router;
		},

		async submitAttempt() {
			try {
				const response = await fetch(
					`${PATCH_UPDATE_ATTEMPT_AS_SUBMITTED}/${this.attemptDid}`,
					{
						method: 'PATCH',
						headers: {
							'Content-Type': 'application/json',
						},
					}
				);

				if (!response.ok) {
					throw new Error(`HTTP error! Status: ${response.status}`);
				}

				this.submitted = true;
			} catch (err) {
				console.error('Attempt submission failed, error:', err);
			}
		},
		async fetchFeedback() {
			try {
				const res = await fetch(
					`${GET_FEEDBACK_ROUTE}/${this.attemptDid}`
				);
				const json = await res.json();
				this.feedback = json.data.feedbackData;

				// Ensure each feedback item has a correctness property
				this.feedback = this.feedback.map((fb) => {
					// Check if feedback already has a correctness property
					if (fb.correctness === undefined) {
						// If not, try to determine it from other properties
						if (fb.score !== undefined) {
							fb.correctness = fb.score > 0;
						} else if (fb.isCorrect !== undefined) {
							fb.correctness = fb.isCorrect;
						} else if (fb.correct !== undefined) {
							fb.correctness = fb.correct;
						} else {
							// Default to false if can't determine correctness
							fb.correctness = false;
						}
					}
					return fb;
				});

				this.feedbackGrade = json.data.percentage / 10;
				this.showFeedback = true;
			} catch (err) {
				console.error('Error fetching feedback:', err);
			}
		},
		async saveChanges() {
			const store = useQuestionsStore();

			for (const questionDid in store.userAnswers) {
				const entry = store.userAnswers[questionDid];
				if (entry && entry.answerContent !== undefined) {
					await store.updateAnswer(questionDid, entry.answerContent);
				}
			}
			this.changesSaved = true;
			setTimeout(() => (this.changesSaved = false), 3000);
		},
		async createEmptyAnswersForQuestions() {
			if (!this.submitted) {
				const requests = this.questions.map(async (question) => {
					const qdid = question.questionDid;

					try {
						const response = await fetch(
							`${CREATE_ANSWER_ROUTE}/${this.attemptDid}/${qdid}`,
							{
								method: 'POST',
								headers: {
									'Content-Type': 'application/json',
								},
								body: JSON.stringify({ answerContent: '' }),
							}
						);

						if (!response.ok && response.status !== 409) {
							console.error(
								`Failed to create answer for question ${qdid}: ${response.statusText}`
							);
						}
					} catch (error) {
						console.error(
							`Failed to create answer for question ${qdid}:`,
							error
						);
					}
				});

				await Promise.all(requests);
			} else {
				const requests = this.questions.map(async (question) => {
					const qdid = question.questionDid;

					try {
						const response = await fetch(
							`${BASE_AUTH_URL}/topic/exercises/answers/get/${this.attemptDid}/${qdid}`,
							{
								method: 'GET',
								credentials: 'include',
							}
						);

						if (!response.ok && response.status !== 409) {
							console.error(
								`Failed to create answer for question ${qdid}: ${response.statusText}`
							);
						}
					} catch (error) {
						console.error(
							`Failed to create answer for question ${qdid}:`,
							error
						);
					}
				});

				await Promise.all(requests);
			}
		},

		async goToEditPage() {
			const topicId = this.$route.query.topic;
			if (!this.exerciseDid || !topicId) return;

			this.$router.push(`/exercise/edit/${this.exerciseDid}/${topicId}`);
		},

		async fetchAttemptStatus(attemptDid) {
			try {
				const res = await fetch(
					`${GET_ATTEMPT_STATUS_ROUTE}/${attemptDid}`
				);

				if (!res.ok) {
					console.error(
						`Failed to fetch attempt status: ${res.status} ${res.statusText}`
					);
					return;
				}

				const res_data = await res.json();
				const data = res_data.data;
				this.submitted = data.attemptIsSubmitted;
				this.exerciseDid = data.exerciseDid;
			} catch (error) {
				console.error('Error while fetching attempt status:', error);
			}
		},
	},
};
</script>

<style scoped>
.title-wrapper {
	min-height: 95px;
}

.description-wrapper {
	display: flex;
	align-items: center;
	justify-content: center;
	flex-direction: column;

	width: 40%;
}

.attempt-wrapper {
	width: 50%;
}

#markdown {
	max-height: 650px;
	overflow-y: scroll;
}
.markdown-wrapper {
	width: 100%;
}

.question-list-wrapper {
	max-height: 58vh;
	overflow-y: scroll;
}

.container {
	max-width: 1400px;
	margin: 0 auto;
	min-height: calc(100vh - var(--header-height));
}

@media screen and (max-width: 991px) {
	.container {
		max-width: 600px;
		margin: 0 auto;
	}

	.description-wrapper {
		width: 100%;
	}
	.attempt-wrapper {
		width: 100%;
	}
}
</style>