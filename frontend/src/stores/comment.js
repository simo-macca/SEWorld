import { defineStore } from 'pinia';

import axios from 'axios';

import {
	GET_ALL_COMMENTS_ROUTE_BY_RESPONSE_DID,
	POST_COMMENT_ROUTE_BY_RESPONSE_DID,
	BASE_AUTH_URL,
} from '@/utils/constants';

const apiClient = axios.create({
	baseURL: `${BASE_AUTH_URL}`,
	withCredentials: true,
});

export const useCommentStore = defineStore('commentStore', {
	state: () => ({
		comments: {},
	}),

	actions: {
		async loadComments(aiResponseDid = undefined) {
			if (aiResponseDid === undefined) return;

			try {
				const response = await fetch(
					GET_ALL_COMMENTS_ROUTE_BY_RESPONSE_DID.replace(
						'{response_did}',
						aiResponseDid,
					),
					{
						method: 'GET',
						credentials: 'include',
					},
				);

				if (!response.ok) {
					throw new Error(`HTTP error! Status: ${response.status}`);
				}

				const res = await response.json();

				this.comments[aiResponseDid] = res.data;
				return res.data;
			} catch (error) {
				console.error('Error fetching comments:', error);
				return false;
			}
		},

		async postComment(aiResponseDid = undefined, comment) {
			if (aiResponseDid === undefined || typeof comment != 'string')
				return;

			try {
				const res = await apiClient.post(
					`/AI/responses/comment/${aiResponseDid}`,
					{ comment },
				);

				if (res.statusText != 'Created') {
					throw new Error(`HTTP error! Status: ${res.status}`);
				}

				return res.data;
			} catch (error) {
				console.error('Error fetching comments:', error);
				return false;
			}
		},

		async deleteComment(commentDid = undefined) {
			if (commentDid === undefined) return;

			try {
				const res = await apiClient.delete(
					`/AI/responses/comment/${commentDid}/delete`,
				);

				if (res.statusText != 'OK') {
					throw new Error(`HTTP error! Status: ${response.status}`);
				}

				return true;
			} catch (error) {
				console.error('Error fetching comments:', error);
				return false;
			}
		},
	},

	getters: {
		getComments:
			(state) =>
			(aiResponseDid = undefined) =>
				!aiResponseDid
					? []
					: state.comments[aiResponseDid]
						? state.comments[aiResponseDid]
						: [],
	},
});
