import { defineStore } from 'pinia';

import {
	GET_STATISTICS_GENERAL_VIEW_ROUTE,
	GET_STATISTICS_TOPIC_EXERCISES_ROUTE_BY_TOPIC_DID,
	GET_STATISTICS_GENERAL_VIEW_ROUTE_BY_USER_DID,
	GET_STATISTICS_TOPIC_EXERCISES_ROUTE_BY_TOPIC_DID_USER_DID,
	GET_STATISTICS_SINGLE_EXERCISE,
} from '@/utils/constants.js';

import {
	orderFirstThreePositionsByShortestTopicsTitle,
	orderLastThreePositionsByLongestTopicsTitle,
	shortenTopicsTitles,
	shortenExercisesTitles,
	sortQuestionsByResult,
} from '@/utils/statistics';

export const useStatisticsStore = defineStore('statistics', {
	state: () => ({
		areTopicsFetched: false,
		topics: [],
		stats: {
			min: 0,
			average: 0,
			max: 0,
		},
		topicExercises: {},
		exerciseInfo: {},
	}),

	actions: {
		async fetchTopicsStatistics(did = undefined) {
			try {
				const response = await fetch(
					did
						? GET_STATISTICS_GENERAL_VIEW_ROUTE_BY_USER_DID.replace(
								`{user_did}`,
								did,
							)
						: GET_STATISTICS_GENERAL_VIEW_ROUTE,
					{
						method: 'GET',
						credentials: 'include',
					},
				);

				if (!response.ok) {
					throw new Error(`HTTP error! Status: ${response.status}`);
				}

				let resTopics = (await response.json()).data;
				if (!resTopics) {
					throw new Error('Response Object is undefined');
				}

				if (resTopics.topics.length == 0) {
					resTopics = resTopics.topics;
				} else if (this.isUserTopic(resTopics)) {
					// format user specifc values

					// stats values
					this.stats.min = (resTopics.minGrade / 10).toFixed(1);
					this.stats.average = (resTopics.avgGrade / 10).toFixed(1);
					this.stats.max = (resTopics.maxGrade / 10).toFixed(1);

					// traverse to topic array
					resTopics = resTopics.topics;

					// format avgUsersGrade
					resTopics = resTopics.map((t) => {
						t.avgUsersGrade = Number(
							(t.avgUsersGrade / 10).toFixed(1),
						);
						return t;
					});
					// format userGrade
					resTopics = resTopics.map((t) => {
						t.userGrade = Number((t.userGrade / 10).toFixed(1));
						return t;
					});
				} else {
					// traverse to topic array
					resTopics = resTopics.topics;

					// format instructor specific values
					// format avgUsersGrade
					resTopics = resTopics.map((t) => {
						t.avgUsersGrade = Number(
							(t.avgUsersGrade / 10).toFixed(1),
						);
						return t;
					});

					// calculate statistics
					const grades = resTopics.map((t) => t.avgUsersGrade);
					this.stats.min = Math.min(...grades).toFixed(1);
					this.stats.average = (
						grades.reduce((a, b) => a + b, 0) / grades.length
					).toFixed(1);
					this.stats.max = Math.max(...grades).toFixed(1);
				}

				// shorten titles
				shortenTopicsTitles(resTopics, 25);
				// position the three shotest titles first
				orderFirstThreePositionsByShortestTopicsTitle(resTopics);
				// position the three longest titles last
				orderLastThreePositionsByLongestTopicsTitle(resTopics);

				this.topics = resTopics;
				this.areTopicsFetched = true;
			} catch (error) {
				this.areTopicsFetched = false;
				console.error('Error fetching topics statistics:', error);
			}
		},

		async fetchTopicExercisesStatisticsByDid(
			topicDid,
			userDid = undefined,
		) {
			// if it is already there do not fetch
			if (this.topicExercises[topicDid]) {
				return;
			}

			try {
				const response = await fetch(
					userDid
						? GET_STATISTICS_TOPIC_EXERCISES_ROUTE_BY_TOPIC_DID_USER_DID.replace(
								'{topic_did}',
								topicDid,
							).replace('{user_did}', userDid)
						: GET_STATISTICS_TOPIC_EXERCISES_ROUTE_BY_TOPIC_DID.replace(
								'{topic_did}',
								topicDid,
							),
					{ method: 'GET', credentials: 'include' },
				);

				if (!response.ok) {
					throw new Error(`HTTP error! Status: ${response.status}`);
				}

				let resExercises = (await response.json()).data.stats;
				if (!resExercises) {
					throw new Error('Response Object is undefined');
				}

				const exs = {
					average: '0',
					exercises: [],
				};

				if (resExercises.length == 0) {
					// Nothing to do
				} else if (this.isUserExerciseTopic(resExercises[0])) {
					// format user data
					// format userGrade
					resExercises = resExercises.map((e) => {
						e.userGrade = Number((e.userGrade / 10).toFixed(1));
						return e;
					});

					// Calculate average
					exs.average = (
						resExercises.reduce((acc, e) => acc + e.userGrade, 0) /
						resExercises.length
					).toFixed(1);
				} else {
					// format instructor data
					// format avgUsersGrade
					resExercises = resExercises.map((e) => {
						e.avgUsersGrade = Number(
							(e.avgUsersGrade / 10).toFixed(1),
						);
						return e;
					});

					// calculate average
					exs.average = (
						resExercises.reduce(
							(acc, e) => acc + e.avgUsersGrade,
							0,
						) / resExercises.length
					).toFixed(1);
				}

				shortenExercisesTitles(resExercises, 25);
				exs.exercises = resExercises;

				// assign it
				this.topicExercises[topicDid] = exs;
			} catch (error) {
				// add default on error
				this.topicExercises[topicDid] = {
					average: '0',
					exercises: [],
				};
				console.error(
					'Error fetching topic exercises statistics:',
					error,
				);
			}
		},

		async fetchSingleExerciseStatistic(did) {
			try {
				const url = `${GET_STATISTICS_SINGLE_EXERCISE}/${encodeURIComponent(did)}`;

				const response = await fetch(url, {
					method: 'GET',
					credentials: 'include',
					headers: {
						Accept: 'application/json',
					},
				});

				if (!response.ok) {
					throw new Error(
						`HTTP Error: ${response.status} ${response.statusText}`,
					);
				}

				const payload = await response.json();
				if (!payload?.data) {
					throw new Error(
						'Unexpected API response format: missing `data` field',
					);
				}

				this.exerciseInfo = payload.data;
				return sortQuestionsByResult(this.exerciseInfo);
			} catch (err) {
				console.error('Error fetching single exercise statistic:', err);
				throw err;
			}
		},

		isUserTopic(topic) {
			return (
				'minGrade' in topic &&
				'avgGrade' in topic &&
				'maxGrade' in topic
			);
		},

		isUserExerciseTopic(exercise) {
			return 'userGrade' in exercise && `successful` in exercise;
		},

		clearTopicExercises() {
			this.topicExercises = {};
		},

		exerciseInfo() {
			return this.exerciseInfo;
		},
	},

	getters: {
		getTopicsStatistics: (state) => state.topics,

		getTopicsStatsStatistic: (state) => state.stats,

		getTopicExercisesStatisticsByDid: (state) => (did) =>
			state.topicExercises[did]
				? state.topicExercises[did].exercises
				: [],
		getTopicExercisesAverageByDid: (state) => (did) =>
			state.topicExercises[did] ? state.topicExercises[did].average : 0,

		doesTopicExerciseStatisticsExistByDid: (state) => (did) => {
			return state.topicExercises[did] !== undefined;
		},

		areTopicsStatisticsEmpty: (state) => state.topics.length == 0,

		isTopicExerciseStatisticsEmpty: (state) => (did) => {
			return state.topicExercises[did] &&
				state.topicExercises[did].exercises
				? state.topicExercises[did].exercises.length == 0
				: true;
		},

		getTopicExerciseStatisticsLengthByDid: (state) => (did) => {
			return state.topicExercises[did] &&
				state.topicExercises[did].exercises
				? state.topicExercises[did].exercises.length
				: 0;
		},

		getTopicAvgUsersGrades: (state) =>
			state.topics ? state.topics.map((t) => t.avgUsersGrade) : 0,

		getTopicUserGrades: (state) =>
			state.topics ? state.topics.map((t) => t.userGrade) : [],

		getTopicDids: (state) =>
			state.topics ? state.topics.map((t) => t.topicDid) : [],

		getTopicTitles: (state) =>
			state.topics ? state.topics.map((t) => t.topicTitle) : [],
	},
});
