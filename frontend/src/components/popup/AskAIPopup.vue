<script>
import IconBiSearch from '~icons/bi/search';
import { BPopover, BButton } from 'bootstrap-vue-next';

export default {
	name: 'AskAIPopup',

	props: {
		materialDid: {
			type: String,
			required: true,
		},
		highlighted: {
			type: String,
			default: '',
		},
	},

	components: {
		IconBiSearch,
		BPopover,
		BButton,
	},

	data() {
		return {
			question: '',
			answer: '',
			loading: false,
			invalidInput: false,
		};
	},

	methods: {
		async askAI() {
			if (!this.question.trim()) {
				this.invalidInput = true;
				return;
			}
			this.invalidInput = false;
			this.loading = true;

			try {
				const response = await fetch(
					`/api/auth/AI/ask/materials/${this.materialDid}`,
					{
						method: 'POST',
						headers: {
							'Content-Type': 'application/json',
						},
						body: JSON.stringify({
							question: this.question,
							highlightedText: this.highlighted,
						}),
					}
				);

				if (!response.ok) {
					throw new Error(`HTTP error! Status: ${response.status}`);
				}

				const res = await response.json();

				this.answer = res.data.answer || 'No response from AI.';
			} catch (err) {
				console.error('Error:', err);
				this.answer = 'Error communicating with AI.';
			} finally {
				this.loading = false;
			}
		},
	},
};
</script>

<template>
	<b-popover
		target="ask-ai-button"
		triggers="click"
		placement="bottom"
		custom-class="ask-ai-popover"
	>
		<div class="ask-ai-popup shadow-lg rounded p-3">
			<div class="d-flex align-items-center gap-2 mb-2">
				<img
					src="../../assets/images/power-ranger.png"
					alt="Power Ranger"
					class="ai-icon"
					width="30"
					height="30"
				/>
				<strong>Ask AI!</strong>
			</div>

			<div v-if="highlighted" class="highlighted-container mb-3">
				<div class="highlighted-label mb-1">Highlighted Text:</div>
				<div class="highlighted-box p-2">{{ highlighted }}</div>
			</div>

			<div class="ai-question-box">
				<div class="search-bar-wrapper w-100 mb-1 position-relative">
					<input
						v-model="question"
						type="text"
						placeholder="Ask something about this material..."
						:class="[
							'search-input',
							{ 'is-invalid': invalidInput },
						]"
						@keyup.enter="askAI"
					/>
					<IconBiSearch
						class="search-icon-clickable"
						@click="askAI"
					/>
				</div>

				<div v-if="invalidInput" class="invalid-feedback d-block mb-2">
					Please enter a valid question
				</div>

				<div class="text-center">
					<b-button
						class="mt-2 px-4"
						variant="primary"
						:disabled="loading || !question"
						@click="askAI"
					>
						{{ loading ? 'Asking...' : '' }}
					</b-button>
				</div>

				<div class="mt-3" v-if="answer">
					<hr />
					<p><strong>AI Answer:</strong></p>
					<p style="text-align: justify">{{ answer }}</p>
				</div>
			</div>
		</div>
	</b-popover>
</template>

<style>
/* Global styles to override bootstrap-vue popover */
.ask-ai-popover {
	max-width: 450px !important;
	width: 450px !important;
}

.ask-ai-popover .popover-body {
	background-color: var(--primary-bg-color) !important;
	padding: 0 !important;
}

.ask-ai-popover.popover {
	background-color: var(--primary-bg-color) !important;
	border: none !important;
}
</style>

<style scoped>
.ask-ai-popup {
	width: 100%;
	background: var(--primary-bg-color);
	color: var(--primary-text-color);
	border: 1px solid #ccc;
	border-radius: 8px;
}

.search-bar-wrapper {
	display: flex;
	border: 1px solid #ccc;
	border-radius: 8px;
	background: var(--main-gradient-bottom);
	transition: all 0.125s ease;
	width: 100%;
	position: relative;
}

.search-input {
	width: 100%;
	outline: none;
	border-radius: 8px;
	border: none;
	background: var(--primary-bg-color);
	color: var(--primary-text-color);
	font-size: 1rem;
	padding: 0.5rem 0.75rem;
	padding-right: 2.5rem; /* leave room for icon on right */
}

.search-icon-clickable {
	position: absolute;
	right: 0.75rem;
	top: 50%;
	transform: translateY(-50%);
	cursor: pointer;
	color: var(--primary-text-color);
	font-size: 1.25rem;
}

.ai-question-box {
	width: 100%;
}

button {
	font-weight: bold;
}

.is-invalid {
	border: 1px solid #dc3545;
}

.invalid-feedback {
	color: #dc3545;
	font-size: 0.875rem;
}

.highlighted-container {
	width: 100%;
}

.highlighted-label {
	font-size: 0.875rem;
	font-weight: 600;
	color: var(--primary-text-color);
}

.highlighted-box {
	width: 100%;
	max-height: 100px;
	overflow-y: auto;
	background-color: rgba(0, 0, 0, 0.05);
	border-radius: 6px;
	border: 1px solid #ccc;
	font-size: 0.9rem;
	color: var(--primary-text-color);
}
</style>