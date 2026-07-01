import { defineStore } from 'pinia';
import {
	GET_ALL_AI_RESPONSES_ROUTE,
	GET_ALL_USER_AI_RESPONSES_ROUTE,
} from '@/utils/constants';

import { TYPE_OPTIONS } from '@/stores/myAiFilter';
import {
	TYPE_OPTIONS_USER,
	TYPE_VISIBILITY_USER,
} from '@/stores/myAiFilterUser';

import axios from 'axios';
import { BASE_AUTH_URL } from '@/utils/constants';

const apiClient = axios.create({
	baseURL: `${BASE_AUTH_URL}`,
	withCredentials: true,
});

export const useMyAiStore = defineStore('myAiStore', {
	state: () => ({
		responses: [],
	}),

	actions: {
		async getAiResponses() {
			// call route
			try {
				const response = await fetch(GET_ALL_AI_RESPONSES_ROUTE, {
					method: 'GET',
					credentials: 'include',
				});

				if (!response.ok) {
					throw new Error(
						`Error: ${response.status} Message: ${response.message}`,
					);
				}

				const res = await response.json();
				const questions = res.data;

				// Normalize data for the UI
				questions.map((q) => {
					switch (q.questionType) {
						case 'Markdown Question':
							q.type = 'MATERIAL';
							break;
						case 'Exercise Question':
							q.type = 'EXERCISE';
							q.questionText = q.questionTitle;
							break;
						default:
							q.type = 'UNKOWN';
							break;
					}
				});

				console.log(res);

				this.responses = questions;
				return this.responses;
			} catch (error) {
				console.error(`Failed to fetch AI responses: ${error}`);
				return [];
			}
		},

		async getAiResponsesUser(userDid = undefined) {
			if (userDid === undefined) {
				return;
			}

			try {
				const response = await fetch(GET_ALL_USER_AI_RESPONSES_ROUTE, {
					method: 'GET',
					credentials: 'include',
				});

				if (!response.ok) {
					throw new Error(
						`Error: ${response.status} Message: ${response.message}`,
					);
				}

				const res = await response.json();
				const questions = res.data;

				// Normalize data for the UI
				questions.map((q) => {
					switch (q.questionType) {
						case 'Markdown Question':
							q.type = 'MATERIAL';
							break;
						case 'Exercise Question':
							q.type = 'EXERCISE';
							q.questionText = q.questionTitle;
							break;
						default:
							q.type = 'UNKOWN';
							break;
					}
				});

				this.responses = questions;
				return questions;
			} catch (error) {
				console.error(`Failed to feetch user AI responses: ${error}`);
				return [];
			}
		},

		async publishAiResponse(responseDid) {
			// Sanitize inputs
			if (!responseDid) return undefined;

			// call route
			try {
				// For Patch cors we need to use axios
				const response = await apiClient.patch(
					`/AI/response/publish/${responseDid}`,
				);

				console.log(response);

				if (response.statusText != 'OK') {
					throw new Error(
						`Error: ${response.status} Message: ${response.message}`,
					);
				}

				// We mock data for now
				return true;
			} catch (error) {
				console.error(`Failed to publish the AI response: ${error}`);
				return false;
			}
		},
	},

	getters: {
		getResponses: (state) => [...state.responses],
		getPublicResponses: (state) =>
			state.responses.filter((res) => res.public),
		getPrivateResponses: (state) =>
			state.responses.filter((res) => !res.public),

		sortedQuestionAsc:
			(state) =>
			(responses = undefined) =>
				[...state.fillResponses(responses)].sort((a, b) =>
					a.questionText.localeCompare(b.questionText),
				),
		sortedQuestionDesc:
			(state) =>
			(responses = undefined) =>
				[...state.fillResponses(responses)].sort((a, b) =>
					b.questionText.localeCompare(a.questionText),
				),

		sortedRatingAsc:
			(state) =>
			(responses = undefined) =>
				[...state.fillResponses(responses)].sort(
					(a, b) => a.rating - b.rating,
				),
		sortedRatingDesc:
			(state) =>
			(responses = undefined) =>
				[...state.fillResponses(responses)].sort(
					(a, b) => b.rating - a.rating,
				),

		filterByResponseType:
			(state) =>
			(
				responses = undefined,
				types = TYPE_OPTIONS.map((to) => to.value),
			) =>
				state
					.fillResponses(responses)
					.filter((res) => types.includes(res.type)),
		filterByResponseTypeUser:
			(state) =>
			(
				responses = undefined,
				types = TYPE_OPTIONS_USER.map((to) => to.value),
			) =>
				state
					.fillResponses(responses)
					.filter((res) => types.includes(res.type)),
		filterByVisibilityTypeUser:
			(state) =>
			(
				responses = undefined,
				types = TYPE_VISIBILITY_USER.map((to) => to.value),
			) =>
				state
					.fillResponses(responses)
					.filter(
						(res) =>
							(types.includes('PRIVATE') && !res.isPublic) ||
							(types.includes('PUBLIC') && !res.isPublic),
					),

		// Fill the materials variable with the state material array if it is not valid
		fillResponses: (state) => (responses) => {
			if (
				responses == undefined ||
				responses == null ||
				!Array.isArray(responses)
			) {
				responses = state.responses;
			}

			return responses;
		},
	},
});
