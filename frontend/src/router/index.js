// src/router/index.js
import { createRouter, createWebHistory, START_LOCATION } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { useNavStore } from '@/stores/navigation';

import HomeView from '@/views/HomeView.vue';

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes: [
		{
			path: '/',
			name: 'Home',
			component: HomeView,
		},
		{
			path: '/about',
			name: 'About',
			component: () => import('../views/AboutView.vue'),
		},
		{
			path: '/login',
			name: 'Login',
			component: () => import('../views/LoginView.vue'),
			meta: { requiresAuth: false },
		},
		{
			path: '/users',
			name: 'Users List',
			component: () => import('../views/UserListView.vue'),
		},
		{
			path: '/profile/:user_info?',
			name: 'Profile page',
			component: () => import('../views/UserProfileView.vue'),
		},
		{
			path: '/add_User',
			name: 'Add User',
			component: () => import('../views/AddUserView.vue'),
		},
		{
			path: '/components',
			name: 'Components',
			component: () => import('../views/ComponentsView.vue'),
		},
		// related to topics
		{
			path: '/topics',
			name: 'Topics List',
			component: () => import('../views/TopicListView.vue'),
		},
		{
			path: '/materials/:did',
			name: 'Materials',
			component: () => import('../views/MaterialListView.vue'),
		},
		{
			path: '/exercises/:exercise_did/attempt/:attempt_did',
			name: 'Attempt',
			component: () => import('../views/AttemptView.vue'),
			props: (route) => ({
				attemptDid: route.params.attempt_did,
				exerciseDidRoute: route.params.exercise_did,
			}),
		},
		{
			path: '/exercise/new/:topicDid?',
			name: 'New Exercise',
			component: () => import('../components/ExerciseForm.vue'),
		},
		{
			path: '/exercise/edit/:exerciseDid/:topicDid',
			name: 'Edit Exercise',
			component: () => import('../components/ExerciseForm.vue'),
		},
		{
			path: '/exercises/:did',
			name: 'Exercises List',
			component: () => import('../views/ExerciseListView.vue'),
		},
		{
			path: '/statistic/:exerciseDid',
			name: 'Exercises Statistics',
			component: () => import('../views/ExerciseStatisticView.vue'),
		},
		{
			path: '/MyAI',
			name: 'My AI',
			component: () => import('../views/MyAiView.vue'),
		},
		{
			path: '/exercises/:exercise_did/ai_feedback',
			name: 'Exercise AI Feedback',
			component: () => import('../views/ExerciseAIFeedbackView.vue'),
			props: (route) => ({
				exerciseDid: route.params.exercise_did,
			}),
		},
	],
});

// Dopo ogni navigazione, salvo la rotta di provenienza in Pinia + sessionStorage
router.afterEach((to, from) => {
	if (from === START_LOCATION) return;
	if (from.name) {
		const nav = useNavStore();
		nav.setPrevious(from.name);
	}
});

router.beforeEach(async (to, from) => {
	const userStore = useUserStore();
	await userStore.refreshUser();

	if (!userStore.isLoggedIn && to.name !== 'Login') {
		return { name: 'Login' };
	}
	if (userStore.isLoggedIn && to.name === 'Home') {
		return { name: 'Topics List' };
	}
});

export default router;
