import { createRouter, createWebHistory } from 'vue-router'

// Route Definitions
const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomeView.vue'),
    meta: { requiresAuth: false, hideNavBar: true },
  },
  {
    path: '/prova',
    name: 'Prova',
    component: () => import('@/views/provaView.vue'),
  },
  {
    path: '/topics',
    name: 'Topic',
    component: () => import('@/views/TopicsView.vue'),
  },
  {
    path: '/materials',
    name: 'Material',
    component: () => import('@/views/MaterialsView.vue'),
    props: (route) => ({
      topicDid: route.query.topicDid || '',
      topicName: route.query.topicName || '',
    }),
  },
  {
    path: '/exercises',
    name: 'Exercises',
    component: () => import('@/views/ExercisesView.vue'),
    props: (route) => ({
      topicDid: route.query.topicDid || '',
      topicName: route.query.topicName || '',
    }),
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/ProfileView.vue'),
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/AboutView.vue'),
    meta: { hideSearchBar: true },
  },
]

// Router Initialization
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router
