<script>
import { useUserStore } from '@/stores/user';
import { useAttemptsStore } from '@/stores/attempts';
import { BButton, BAlert, BModal } from 'bootstrap-vue-next';

import DeleteMaterialButton from './DeleteMaterialButton.vue';
import PublishMaterialButton from './PublishMaterialButton.vue';
import EditMaterialButton from './EditMaterialButton.vue';

import IconBiCheck2Circle from '~icons/bi/check2-circle';
import IconBiXCircle from '~icons/bi/x-circle';
import IconBiEyeSlash from '~icons/bi/eye-slash';
import IconBiTrash from '~icons/bi/trash';
import IconBiBarChart from '~icons/bi/barChart';

import { GET_QUESTIONS_BY_EXERCISE_DID } from '@/utils/constants.js';

import { successPopup, errorPopup } from '@/utils/globalPopup';

export default {
	name: 'ExerciseCard',

	components: {
		BModal,
		BButton,
		BAlert,
		DeleteMaterialButton,
		PublishMaterialButton,
		EditMaterialButton,
		IconBiCheck2Circle,
		IconBiXCircle,
		IconBiEyeSlash,
		IconBiTrash,
		IconBiBarChart,
	},

	props: {
		exerciseData: {
			type: Object,
			required: true,
		},
		topicId: {
			type: String,
			required: true,
		},
	},

	emits: ['exerciseDeleted', 'draftPublished'],

	data() {
		return {
			user: useUserStore(),
			showDraftModal: false,
			draftAttempt: null,
			hasShortAnswerQuestion: false,
		};
	},

	computed: {
		attemptStore() {
			return useAttemptsStore();
		},
		attempts() {
			return (
				this.attemptStore.getAttemptsByExerciseDid(
					this.exerciseData.exerciseDID
				) || []
			);
		},
		areAttemptsLoaded() {
			return Array.isArray(this.attempts);
		},
		submittedAttemptsCount() {
			return this.attempts.filter((a) => a.attemptIsSubmitted).length;
		},
		lastAttempt() {
			return this.attempts.length > 0
				? this.attempts[this.attempts.length - 1]
				: null;
		},
		isInstructor() {
			return this.user.getUserRole === 'INSTRUCTOR';
		},
		isStudent() {
			return this.user.getUserRole === 'STUDENT';
		},
	},

	methods: {
		async isExerciseValid() {
			if (!this.exerciseData || typeof this.exerciseData !== 'object')
				return false;

			const requiredFields = [
				'exerciseDID',
				'exerciseTitle',
				'exerciseDescription',
			];
			for (const field of requiredFields) {
				if (!this.exerciseData[field]?.trim()) {
					console.warn(`Invalid or missing field: ${field}`);
					return false;
				}
			}

			try {
				const response = await fetch(
					GET_QUESTIONS_BY_EXERCISE_DID.replace(
						'{exercise_did}',
						this.exerciseData.exerciseDID
					)
				);
				if (!response.ok) throw new Error(`HTTP ${response.status}`);

				const { data: questions } = await response.json();

				if (!Array.isArray(questions) || questions.length === 0) {
					console.warn('No questions found for this exercise.');
					return false;
				}

				for (const question of questions) {
					if (
						question.type === 'SHA' &&
						(typeof question.correctAnswer !== 'string' ||
							!question.correctAnswer.trim())
					) {
						console.warn('Found empty or invalid SHA answer.');
						return false;
					}
				}

				return true;
			} catch (err) {
				console.error('Error validating exercise questions:', err);
				return false;
			}
		},

		async deleteExercise(did) {
			try {
				await fetch(
					`/api/auth/topic/exercises/teacher/delete_exercise/${did}`,
					{ method: 'DELETE' }
				);
				this.$emit('exerciseDeleted', did);
				successPopup(
					'Succesfull Deletion',
					'Exercise has been deleted successfully'
				);
			} catch (err) {
				console.error('Delete failed:', err);
				errorPopup('Failed Deletion', 'Exercise has not been deleted');
			}
		},

		async publishExercise() {
			if (!(await this.isExerciseValid())) return;

			try {
				await fetch(
					`/api/auth/topic/exercises/teacher/change_draft/${this.exerciseData.exerciseDID}`,
					{
						method: 'PATCH',
						headers: { 'Content-Type': 'application/json' },
						body: JSON.stringify({ exercise_isDraft: false }),
					}
				);
				this.$emit('draftPublished', this.exerciseData.exerciseDID);
				successPopup(
					'Exercise Published',
					'The exercise has been published for users to attempt'
				);
			} catch (err) {
				console.error('Publish failed:', err);
				errorPopup(
					'Failed Published',
					'The exercise has not been published for user to attempt'
				);
			}
		},

		async newAttemptRequested() {
			if (this.lastAttempt && !this.lastAttempt.attemptIsSubmitted) {
				this.draftAttempt = this.lastAttempt;
				this.showDraftModal = true;
				return;
			}
			await this.createAndRedirectNewAttempt();
		},

		async createAndRedirectNewAttempt() {
			try {
				const attemptDid =
					await this.attemptStore.createAttemptByExerciseDid(
						this.exerciseData.exerciseDID
					);
				if (attemptDid) {
					this.$router.push(
						`/exercises/${this.exerciseData.exerciseDID}/attempt/${attemptDid}`
					);
				}
			} catch (err) {
				console.error('Failed to create attempt:', err);
			}
		},

		async discardDraftAndStartNew() {
			if (!this.draftAttempt) return;
			try {
				await this.attemptStore.deleteAttemptByAttemptDid(
					this.draftAttempt.attemptDID
				);
				this.draftAttempt = null;
				this.showDraftModal = false;
				await this.createAndRedirectNewAttempt();
			} catch (err) {
				console.error('Failed to discard draft:', err);
			}
		},
	},

	async created() {
		await this.attemptStore.fetchAttemptsByExerciseDid(
			this.exerciseData.exerciseDID
		);

		if (this.isInstructor) {
			try {
				const response = await fetch(
					GET_QUESTIONS_BY_EXERCISE_DID.replace(
						'{exercise_did}',
						this.exerciseData.exerciseDID
					),
					{ credentials: 'include' }
				);
				if (response.ok) {
					const { data: questions } = await response.json();
					this.hasShortAnswerQuestion = questions.some(
						(q) => q.type === 'SHA'
					);
				}
			} catch (err) {
				console.error(
					'Error checking for short answer questions:',
					err
				);
			}
		}
	},
};
</script>

<template>
	<div v-if="exerciseData" class="flash-card p-2 mb-2">
		<div
			class="d-flex flex-row align-items-center justify-content-between flash-card-content"
		>
			<div v-if="isStudent && !exerciseData.isDraft" class="status">
				<IconBiCheck2Circle
					v-if="exerciseData.isCompleted"
					class="completed-icon"
				/>
				<IconBiXCircle v-else class="not-completed-icon" />
			</div>

			<div class="name">{{ exerciseData.exerciseTitle }}</div>

			<div v-if="isStudent && areAttemptsLoaded" class="attempts">
				<span v-if="submittedAttemptsCount > 0">
					My attempt<span v-if="submittedAttemptsCount > 1">s</span>:
					{{ submittedAttemptsCount }}
				</span>
				<span v-else-if="!exerciseData.isDraft">No attempts yet</span>
			</div>
			<div v-else-if="isStudent && !areAttemptsLoaded" class="attempts">
				Error loading attempts
			</div>

			<div
				v-if="isStudent && areAttemptsLoaded && !exerciseData.isDraft"
				class="view-attempt"
			>
				<router-link
					v-if="lastAttempt"
					:key="lastAttempt.attemptDID"
					:to="`/exercises/${exerciseData.exerciseDID}/attempt/${lastAttempt.attemptDID}`"
					:class="
						lastAttempt.attemptIsCompleted ? 'text-success' : ''
					"
				>
					Last Attempt
				</router-link>
				<span
					v-else-if="
						isStudent &&
						!(areAttemptsLoaded && !exerciseData.isDraft)
					"
					>No attempts yet</span
				>
			</div>
		</div>

		<!-- Draft Attempt Modal -->
		<b-modal
			v-model="showDraftModal"
			title="Attempt in Draft"
			centered
			size="md"
			hide-header-close
			header-bg-variant="light"
			header-text-variant="dark"
		>
			<div class="text-center mb-3">
				<p class="mb-1 font-weight-bold">
					You already have an attempt in draft.
				</p>
				<p class="text-muted">
					Would you like to continue it or discard and start a new
					one?
				</p>
			</div>
			<template #footer>
				<div
					class="w-100 d-flex justify-content-between align-items-center"
				>
					<b-button
						variant="outline-secondary"
						@click="showDraftModal = false"
						>Cancel</b-button
					>
					<div class="d-flex gap-2">
						<b-button
							variant="outline-danger"
							@click="discardDraftAndStartNew"
							>Discard and Start New</b-button
						>
						<b-button
							variant="primary"
							@click="
								$router.push(
									`/exercises/${exerciseData.exerciseDID}/attempt/${draftAttempt.attemptDID}`
								)
							"
							>Continue Last Attempt</b-button
						>
					</div>
				</div>
			</template>
		</b-modal>

		<!-- Action buttons -->
		<div class="action-buttons" v-if="!exerciseData.isDraft && isStudent">
			<BButton
				class="attempt-button"
				:disabled="!exerciseData.isDraft && !isStudent && !isInstructor"
				@click="newAttemptRequested"
			>
				NEW ATTEMPT
			</BButton>
		</div>
		<div
			class="action-buttons"
			v-else-if="!exerciseData.isDraft && isInstructor"
		>
			<router-link
				title="statistics"
				:to="`/statistic/${this.exerciseData.exerciseDID}`"
			>
				<IconBiBarChart class="bi bi-bar-chart click"></IconBiBarChart>
			</router-link>

			<router-link
				:to="`/exercises/${this.exerciseData.exerciseDID}/ai_feedback`"
				:class="{ 'disabled-link': !hasShortAnswerQuestion }"
				title="AI feedback"
				class="ms-3"
			>
				<img
					src="@/assets/images/power-ranger.png"
					class="power-ranger-icon"
					:class="{ 'icon-disabled': !hasShortAnswerQuestion }"
				/>
			</router-link>
		</div>

		<div
			v-if="isInstructor && exerciseData.isDraft"
			class="d-flex align-items-center gap-2"
		>
			<PublishMaterialButton
				:elementId="exerciseData.exerciseDID"
				@publish="publishExercise"
			/>
			<EditMaterialButton
				:exerciseData="exerciseData"
				:topicId="topicId"
			/>
			<DeleteMaterialButton
				:elementId="exerciseData.exerciseDID"
				deletionSpecText="an exercise"
				@delete="deleteExercise"
			/>
		</div>
		<div v-else-if="isInstructor" class="d-flex align-items-center gap-1">
			<DeleteMaterialButton
				:elementId="exerciseData.exerciseDID"
				deletionSpecText="an exercise"
				@delete="deleteExercise"
			/>
		</div>
	</div>

	<div v-else class="Error">No data available.</div>
</template>

<style scoped>
.flash-card {
	width: 100%;
	border: 2px solid black;
	border-radius: 5px;
	padding: 5px 15px 5px 15px;
	display: flex;
	flex-direction: row;
	align-items: center;
	transition: all 0.3s ease;
}

.flash-card-content {
	display: flex;
	flex: 1;
	align-items: center;
	justify-content: space-between;
	width: 100%;
	gap: 10px;
}

.name,
.attempts,
.view-attempt {
	flex: 1; /* distribute available space equally */
	min-width: 100px; /* prevents shrinking too much */
	text-align: start;
}

.flash-card > div {
	margin: 0 10px;
}

.Error {
	color: red;
}

a {
	color: white;
}

a:hover {
	color: #b44593;
}

.name .attempts .view-attempt .status .action-buttons {
	margin: 0 50px;
	padding: 0 10px;
}

.action-buttons-dis {
	cursor: not-allowed;
}

BButton {
	padding: 4px 12px;
	font-size: 0.85rem;
	border-radius: 6px;
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
	transition: background-color 0.2s ease;
}

BButton:hover {
	filter: brightness(1.05);
}

.attempt-button {
	border: none;
	background: #b44593;
	color: white;
	transition: background-color 0.2s ease;
}

.attempt-button:hover {
	filter: brightness(1.05);
}

.name {
	display: flex;
	align-items: center;
	gap: 8px;
}

.draft-icon {
	display: flex;
	align-items: center;
	cursor: pointer;
}

.eye-slash {
	font-size: 1.2rem;
	transition: opacity 0.2s ease;
}

.eye-slash:hover {
	opacity: 0.7;
}

.power-ranger-icon {
	width: 24px;
	height: 24px;
	transition: opacity 0.2s ease;
}

.power-ranger-icon:hover {
	opacity: 0.7;
}

.icon-disabled {
	opacity: 0.3;
	cursor: not-allowed;
}

.disabled-link {
	pointer-events: none;
}

@media (max-width: 768px) {
	.flash-card {
		flex-direction: column;
		align-items: flex-start;
		height: auto;
		padding: 15px;
	}

	.flash-card > div,
	.action-buttons,
	.attempt-button {
		margin: 8px 0;
		width: 100%;
	}

	.action-buttons {
		justify-content: space-between;
	}

	.attempt-button {
		width: 100%;
		text-align: center;
	}
}
</style>
