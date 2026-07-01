<template>
	<div v-if="visible" class="faq-card faq-popover">
		<h3>FAQ</h3>

		<div v-if="loading">Loading...</div>

		<div v-else-if="error">{{ error }}</div>
		<div
			v-else
			class="d-flex align-items-center jusify-content-center gap-4 flex-column"
		>
			<div
				v-for="item in questions"
				:key="item.aiResponseDid"
				class="faq-item"
			>
				<p><strong>Q:</strong> {{ item.questionText }}</p>

				<p><strong>A:</strong> {{ item.answer }}</p>

				<p v-if="item.highlighted" class="highlighted-text">
					<em>From material:</em> "{{ item.highlighted }}"
				</p>

				<!-- Placeholder for Rating component-->
				<div
					class="d-flex align-items-center justify-content-center gap-5"
				>
					<CommentPopupVue :aiResponseDid="item.aiResponseDid" />

					<Rating
						:rating="item.rating"
						:vote="item.userRating"
						:ai-response-did="item.aiResponseDid"
					/>
				</div>
			</div>
		</div>
	</div>
</template>
  
<script setup>
import { ref, onMounted } from 'vue';

import Rating from './buttons/Rating.vue';

import { BASE_AUTH_URL } from '@/utils/constants';

import axios from 'axios';
import CommentPopupVue from './popup/CommentPopup.vue';

const apiClient = axios.create({
	baseURL: `${BASE_AUTH_URL}`,
	withCredentials: true,
});

const props = defineProps({
	materialDid: {
		type: String,
		required: true,
	},
	visible: {
		type: Boolean,
		default: true,
	},
});

const questions = ref([]);
const loading = ref(true);
const error = ref(null);

onMounted(async () => {
	try {
		const res = await apiClient.patch(
			`/AI/response/public/${props.materialDid}`
		);

		if (res.statusText != 'OK') throw new Error('Failed to fetch FAQ');

		const data = res.data.data;

		// sort by most rated one
		data.sort((a, b) => b.rating - a.rating);

		questions.value = data;
	} catch (err) {
		error.value = err.message;
	} finally {
		loading.value = false;
	}
});
</script>

<style scoped>
.faq-card {
	border: 1px solid #ccc;
	padding: 1rem;
	border-radius: 8px;
	background: var(--primary-bg-color);
}

.highlighted-text {
	background-color: #56490d;
	color: white;
	padding: 0.25rem 0.5rem;
	border-left: 3px solid #dada2b;
	margin-top: 0.5rem;
	font-style: italic;
}
</style>
