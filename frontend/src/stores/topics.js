import { defineStore } from 'pinia';
import {
	GET_LIST_OF_TOPICS_ROUTE,
	GET_TOPICS_COMPLETION_ROUTE,
} from '@/utils/constants.js';

function chartColours(n) {
	return Array.from(
		{ length: n },
		(_, i) =>
			`hsl(${270 + (i * (30 - 270)) / Math.max(n - 1, 1)}, 80%, 60%)`,
	);
}

export const useTopicsStore = defineStore('topics', {
	state: () => ({
		topics: [],
		material: [],
		exercises: [],

		chartContent: {
			labels: [],
			data: [],
			colours: [],
		},
		attemptedTopics: 0,
		exerciseAttempts: 0,
		averageCompletion: 0,
	}),
	actions: {
		async fetchTopics() {
			try {
				const response = await fetch(`${GET_LIST_OF_TOPICS_ROUTE}`, {
					method: 'GET',
					credentials: 'include',
				});

				if (!response.ok) {
					throw new Error(`HTTP error! Status: ${response.status}`);
				}

				const topics_no_compl = await response.json();

				let mapping_with_did = {};

				for (const t of topics_no_compl.data) {
					mapping_with_did[`${t.did}`] = t;
				}

				const response2 = await fetch(
					`${GET_TOPICS_COMPLETION_ROUTE}`,
					{
						method: 'GET',
						credentials: 'include',
					},
				);

				if (!response2.ok) {
					throw new Error(`HTTP error! Status: ${response.status}`);
				}

				const only_compl = await response2.json();

				let topic_with_compl = [];

				for (const topic of only_compl.data) {
					mapping_with_did[`${topic.topicDid}`].completion =
						topic.completionPercentage;
					mapping_with_did[`${topic.topicDid}`].exercises =
						topic.hasExercises;
					topic_with_compl.push(
						mapping_with_did[`${topic.topicDid}`],
					);
				}

				this.topics = topic_with_compl;
			} catch (error) {
				console.error('Error fetching topics:', error);
			}
		},

		// Move to topic
		async fetchUserStats() {
			try {
				const res = await fetch(
					`${BASE_URL}${BASE_AUTH_URL}/topic/completion`,
					{
						method: 'GET',
						credentials: 'include',
					},
				);
				const { data } = await res.json();
				if (!data || data.length === 0) return;

				const startedTopics = data.filter(
					(i) => i.completionPercentage > 0,
				);
				this.attemptedTopics = startedTopics.length;

				this.averageCompletion =
					data.reduce((s, i) => s + i.completionPercentage, 0) /
					data.length;

				this.chartContent = {
					labels: data.map((i, x) => `Topic ${x + 1}`),
					data: data.map((i) => i.completionPercentage),
					colours: chartColours(data.length),
				};

				const ex_res = await fetch(
					`${BASE_URL}${BASE_AUTH_URL}/topic/exercises/attempts/get_all_by_user`,
					{
						method: 'GET',
						credentials: 'include',
					},
				);
				const { data: attempts } = await ex_res.json();
				if (!attempts) return;

				this.exerciseAttempts = attempts.length;
			} catch (error) {
				console.error('Failed to fetch user stats:', error);
			}
		},
	},
});
