// stores/rating.js
import { defineStore } from 'pinia';

import axios from 'axios';
import { BASE_AUTH_URL } from '@/utils/constants';

const apiClient = axios.create({
	baseURL: `${BASE_AUTH_URL}`,
	withCredentials: true,
});

export const useRatingStore = defineStore('ratingStore', {
	state: () => ({}),

	actions: {
		async toggleVote(responseDid, newVote) {
			try {
				const response = await apiClient.patch(
					`/AI/response/rate/${responseDid}`,
					{ rate: newVote },
				);

				if (response.statusText != 'OK') {
					throw new Error('Failed to rate AI response');
				}

				return response.data.data;
			} catch (error) {
				console.error('Error rating an AI response:', error);
				return false;
			}
		},
	},
});
