<script>
import SearchBarVue from '@/components/inputs/SearchBar.vue';
import UserSearchCard from './components/search/UserSearchCard.vue';

import { useUserStore } from '@/stores/user';

export default {
	name: 'SearchUserProfile',

	components: {
		SearchBarVue,
		UserSearchCard,
	},

	props: {
		searchName: {
			type: String,
			required: true,
		},
	},

	data() {
		return {
			searching: false,
			users: [],
		};
	},

	methods: {
		async searchRoutine(name) {
			if (name === undefined) {
				this.searching = false;
				return;
			} else if (name == '' && this.searching) {
				// update view to not display all users in the db
				this.searching = false;
				return;
			} else if (name != '' && !this.searching) {
				this.searching = true;
			}

			// search
			const usersFound = await this.userStore().searchUsersByName(name);

			// update users for the view
			this.users = usersFound;
		},

		userStore() {
			return useUserStore();
		},
	},

	watch: {
		searchName: {
			immediate: true,
			handler(newSearchName) {
				this.searchRoutine(newSearchName);
			},
		},
	},
};
</script>

<template>
	<div
		class="d-flex align-items-center justify-content-start w-full min-h-screen-header flex-column gap-3 py-5 px-4"
	>
		<div class="w-100 fg flex-center-start fc">
			<div v-if="!searching" class="flex-center w-100 fg-50">
				<h1>Search any user by <span class="highlight">name</span></h1>
			</div>
			<div v-else-if="users.length == 0" class="flex-center w-100 fg-50">
				<h1>No users found</h1>
			</div>
			<div v-else class="flex-center fc w-100 gap-4">
				<UserSearchCard
					v-for="user in users"
					:key="user.did"
					:user="user"
				/>
			</div>
		</div>
	</div>
</template>

<style scoped>
.flex-center-start {
	display: flex;
	align-items: center;
	justify-content: start;
}

.flex-center {
	display: flex;
	align-items: center;
	justify-content: center;
}

.fc {
	flex-direction: column;
}

.fg-50 {
	flex-grow: 0.3;
}

.fg {
	flex-grow: 1;
}
</style>