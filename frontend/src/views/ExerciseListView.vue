<script>
// Stores
import { useExerciseStore } from '@/stores/exercise';
import { useUserStore } from '@/stores/user';
import { useTopicsStore } from '@/stores/topics';

// Components
import ExerciseCard from '@/components/ExerciseCard.vue';
import Header from '@/components/Header.vue';
import { BButton } from 'bootstrap-vue-next';
import IconBiEyeSlash from '~icons/bi/EyeSlash';
import { useSearchStore } from '@/stores/search';

export default {
	name: 'ExerciseListView',

	components: {
		Header,
		ExerciseCard,
		BButton,
		IconBiEyeSlash,
	},

	data() {
		return {
			exercises: [],
			topic: null,
			isLoading: true,
			topicId: null,
			userStore: useUserStore(),
		};
	},

	computed: {
		isInstructor() {
			return this.userStore.getUserRole === 'INSTRUCTOR';
		},
		currentTopic() {
			const topicsStore = useTopicsStore();
			return (
				topicsStore.topics.find(
					(topic) => topic.did === this.topicId
				) || {}
			);
		},
		exerciseSort() {
			const result = [];

			const sections = this.isInstructor
				? [
						{
							title: 'Draft Exercises',
							filter: (exercise) => exercise.isDraft,
							emptyMessage: 'No exercises in draft',
						},
						{
							title: 'Public Exercises',
							filter: (exercise) => !exercise.isDraft,
							emptyMessage: 'No public exercises',
						},
				  ]
				: [
						{
							title: 'To complete',
							filter: (exercise) => !exercise.isCompleted,
							emptyMessage: 'No exercises to complete',
						},
						{
							title: 'Completed',
							filter: (exercise) => exercise.isCompleted,
							emptyMessage: 'No completed exercises',
						},
				  ];

			for (const section of sections) {
				let filtered = this.exercises.filter(section.filter);

				result.push({ isHeader: true, title: section.title });

				// apply search filter
				const searchWord = this.searchStore().getWord;
				if (
					searchWord !== undefined &&
					typeof searchWord === 'string' &&
					searchWord.length > 0
				) {
					// filter by search word
					filtered = [...filtered].filter(
						(e) =>
							e.exerciseTitle &&
							e.exerciseTitle
								.toLowerCase()
								.includes(searchWord.toLowerCase())
					);
				}

				if (filtered.length === 0) {
					result.push({
						isPlaceholder: true,
						message: section.emptyMessage,
					});
				} else {
					result.push(...filtered);
				}
			}

			return result;
		},
	},

	methods: {
		goToNewExercise() {
			this.$router.push(`/exercise/new/${this.topicId}`);
		},
		onExerciseDeleted(did) {
			this.exercises = this.exercises.filter(
				(ex) => ex.exerciseDID !== did
			);
		},
		onDraftPublished(did) {
			const ex = this.exercises.find((e) => e.exerciseDID === did);
			if (ex) {
				ex.isDraft = false;
			}
		},

		searchStore() {
			return useSearchStore();
		},
	},

	async mounted() {
		// Get topic ID from route params
		this.topicId = this.$route.params.did;
		// Refresh user data once at the parent level
		await this.userStore.refreshUser();
		// Fetch exercises for this topic
		const store = useExerciseStore();
		await store.fetchTopicExercises(this.topicId);
		this.exercises = store.exercises;
		this.isLoading = false;
		const topicsStore = useTopicsStore();
		if (!topicsStore.topics.length) {
			await topicsStore.fetchTopics();
		}
	},
};
</script>

<template>
	<Header />

	<div
		class="exercise-list-container min-h-screen-header d-flex align-items-center justify-content-start flex-column"
	>
		<div class="header-row w-100">
			<h1>Exercise for {{ currentTopic.title || 'Loading...' }}</h1>
			<div v-if="isInstructor">
				<BButton
					class="button"
					variant="primary"
					@click="goToNewExercise"
					>+</BButton
				>
			</div>
		</div>

		<div
			v-if="isLoading"
			class="min-h-screen-header align-items-center justify-content-center w-100 flex-grow-1"
		>
			<h1>Loading exercises...</h1>
		</div>
		<div
			v-else-if="exercises.length === 0"
			class="d-flex align-items-center justify-content-center w-100 flex-grow-1"
		>
			<div
				class="d-flex align-items-center justify-content-center w-100 flex-grow-1"
			>
				<h1 class="fs-1">
					There are
					<span class="highlight">no exercises available</span> for
					this topic
				</h1>
			</div>
		</div>
		<div v-else class="exercises-wrapper">
			<template v-for="item in exerciseSort" :key="item.exerciseDid">
				<!-- Section Header -->
				<div v-if="item.isHeader" class="section-header">
					<h3>{{ item.title }}</h3>
				</div>

				<!-- Empty Section Placeholder -->
				<div v-else-if="item.isPlaceholder" class="section-placeholder">
					{{ item.message }}
				</div>

				<!-- Regular Exercise Card -->
				<ExerciseCard
					v-else
					:exerciseData="item"
					:topicId="topicId"
					@exerciseDeleted="onExerciseDeleted"
					@draftPublished="onDraftPublished"
				/>
			</template>
		</div>
	</div>
</template>

<style scoped>
@import '@/assets/main.css';

.exercise-list-container {
	padding: 20px;
}
.header-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20px;
}
.loading,
.no-exercises {
	text-align: center;
	margin: 40px 0;
	font-size: 18px;
	color: #666;
}
.exercises-wrapper {
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-top: 20px;
	width: 100%;
}
.button {
	padding: 10px 20px;
	font-size: 20px;
}
.draft-icon {
	display: inline-flex;
	align-items: center;
	margin-left: 8px;
	cursor: pointer;
}
.eye-slash {
	font-size: 1.1rem;
	color: #b44593;
	transition: opacity 0.2s ease;
}
.eye-slash:hover {
	opacity: 0.7;
}

.section-header {
	width: 100%;
	margin: 15px 0 10px 0;
	padding-bottom: 8px;
	border-bottom: 2px solid #eaeaea;
}
.section-placeholder {
	width: 100%;
	text-align: center;
	color: #888;
	font-style: italic;
	padding: 15px 0;
	margin-bottom: 15px;
}
</style>