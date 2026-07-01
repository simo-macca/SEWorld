import { defineStore } from 'pinia';
import {
	GET_USER_ME_ROUTE,
	GET_INSTRUCTOR_ME_ROUTE,
	GET_ALL_USERS_ROUTE,
	GET_USER_ROUTE_BY_USER_DID,
	GET_USER_ROUTE_BY_USERNAME_SEARCH,
	ROLE_INSTRUCTOR,
} from '@/utils/constants';

export const useUserStore = defineStore('user', {
	state: () => ({
		logged: false,
		user: null,

		allUsers: [],
		searchedUsers: [],
	}),

	actions: {
		async refreshUser() {
			return await this.getUserMe();
		},

		async getUserMe() {
			try {
				const res = await fetch(GET_USER_ME_ROUTE, {
					method: 'GET',
					credentials: 'include',
				});

				const user = (await res.json()).data;
				this.user = user;
				this.logged = true;
				return user;
			} catch (error) {
				console.error(`failed to get user, error: ${error}`);
				return null;
			}
		},

		async getInstructorMe() {
			try {
				const res = await fetch(GET_INSTRUCTOR_ME_ROUTE, {
					method: 'GET',
					credentials: 'include',
				});

				const instructor = (await res.json()).data;
				this.user = instructor;
				this.logged = true;
				return instructor;
			} catch (error) {
				console.error(`failed to get instructor, error: ${error}`);
				return null;
			}
		},

		async getUsers() {
			try {
				const res = await fetch(GET_ALL_USERS_ROUTE, {
					method: 'GET',
					credentials: 'include',
				});

				const users = (await res.json()).data;
				this.allUsers = users;
				return users;
			} catch (error) {
				console.error(`failed to get all users, error: ${error}`);
				return null;
			}
		},

		async getUserByDid(userDid) {
			try {
				const res = await fetch(
					GET_USER_ROUTE_BY_USER_DID.replace('{user_did}', userDid),
					{
						method: 'GET',
						credentials: 'include',
					},
				);

				return (await res.json()).data;
			} catch (error) {
				console.error(`failed to get user, error: ${error}`);
				return null;
			}
		},

		async searchUsersByName(userName) {
			try {
				const res = await fetch(
					GET_USER_ROUTE_BY_USERNAME_SEARCH.replace(
						'{user_name}',
						userName,
					),
					{
						method: 'GET',
						credentials: 'include',
					},
				);

				const usersFound = (await res.json()).data;
				this.searchedUsers = usersFound;
				return usersFound;
			} catch (error) {
				console.error(`failed to get user, error: ${error}`);
				return null;
			}
		},
	},

	getters: {
		isLoggedIn: (state) => state.logged,

		getUserInitials: (state) => {
			if (!state.logged) return '';
			const uppercaseChars = state.user.name.match(/[A-Z]/g);
			return uppercaseChars ? uppercaseChars.join('') : '';
		},

		getUserRole: (state) => (state.logged ? state.user.role : ''),

		getUserDid: (state) => (state.logged ? state.user.did : undefined),

		isInstructor: (state) =>
			state.logged ? state.user.role == ROLE_INSTRUCTOR : false,

		isInstructorField: (_) => (field) => field == ROLE_INSTRUCTOR,

		getAllUsers: (state) => state.allUsers,

		getSearchedUsers: (state) => state.allUsers,
	},
});
