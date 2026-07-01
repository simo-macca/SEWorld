<script>
import { useUserStore } from '@/stores/user';

import MyAiInstructor from './components/MyAiInstructor.vue';

export default {
	components: {
		MyAiInstructor,
	},

	computed: {
		isInstructor() {
			return this.userStore().isInstructor;
		},
	},

	methods: {
		userStore() {
			return useUserStore();
		},
	},
};
</script>

<template>
	<MyAiInstructor v-if="isInstructor" />
	<MyAiUser v-else />
</template>

<style>
</style>