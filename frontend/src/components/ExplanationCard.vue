<script>
import PublishMaterialButton from '@/components/PublishMaterialButton.vue';

export default {
	name: 'ExplanationCard',

	components: {
		PublishMaterialButton,
	},

	data() {
		return {
			expanded: false,
		};
	},

	props: {
		shortAnswerDid: {
			type: String,
			required: true,
		},
		username: {
			type: String,
			required: true,
		},
		user_answer: {
			type: String,
			required: true,
		},
		generated_AI_feedback: {
			type: String,
			required: true,
		},
	},

	methods: {
		publishAnswer() {
			console.log('published');
		},

		toggleExpanded() {
			this.expanded = !this.expanded;
		},
	},
};
</script>

<template>
	<div class="explanation-container">
		<div :class="['explanation-card', { expanded }]">
			<div class="card-header">
				<span class="font-semibold">{{ username }}</span>
				<button class="mistake-button" @click="toggleExpanded">
					{{ expanded ? 'Collapse' : 'Mistake' }}
				</button>
			</div>

			<div v-if="expanded" class="card-body">
				<p><strong>Student Answer:</strong></p>
				<p>{{ user_answer }}</p>

				<div class="ai-feedback-row">
					<div class="text-section">
						<p><strong>AI Feedback:</strong></p>
						<p>{{ generated_AI_feedback }}</p>
					</div>
					<div class="make-public">
						<PublishMaterialButton
							@publish="publishAnswer"
							:elementId="shortAnswerDid"
							:popoverPlacement="`left`"
							:label="'By publishing an answer all users will be able to see it once they fail the exact same question'"
						/>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>
  
<style scoped>
.explanation-container {
	max-width: 600px;
	margin: 2rem auto;
	text-align: left;
}

.explanation-card {
	background: var(--primary-bg-color-mid-deep-dark);
	border: 1px solid rgba(255, 255, 255, 0.2);
	border-radius: 12px;
	padding: 1rem;
	margin-bottom: 1.5rem;
	color: #ffffff;
	transition: all 0.3s ease;
	backdrop-filter: blur(4px);
}

.explanation-card.expanded {
	background: var(--primary-bg-color-mid-deep-dark);
	border-color: #ffffff;
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.card-body {
	margin-top: 1rem;
}

.ai-feedback-row {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	gap: 1rem;
	margin-top: 1rem;
}

.text-section {
	flex: 3;
}

.make-public {
	flex: 1;
	display: flex;
	justify-content: flex-end;
	padding: 0;
	background: none;
	border: none;
}

.mistake-button {
	background: rgb(212, 56, 56);
	color: white;
	border: none;
	padding: 0.5rem 1rem;
	border-radius: 8px;
	font-weight: 500;
	cursor: pointer;
	transition: transform 0.2s ease;
}

.mistake-button:hover {
	transform: scale(1.05);
}

.make-public-btn {
	background: #8b5cf6;
	color: white;
	padding: 0.4rem 1rem;
	border: none;
	border-radius: 6px;
	cursor: pointer;
	margin-top: 0.5rem;
}

.make-public-btn:hover {
	background: #7c3aed;
}
</style>
  