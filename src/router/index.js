import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: () => import('@/views/HomeView.vue'),
      meta: { requiresAuth: false, hideNavBar: true },
    },
    {
      path: '/topic',
      name: 'Topic',
      component: () => import('@/views/TopicsView.vue'),
    },
    {
      path: '/material',
      name: 'Material',
      component: () => import('@/views/MaterialView.vue'),
    },
    {
      path: '/about',
      name: 'About',
      component: () => import('@/views/AboutView.vue'),
      meta: { hideSearchBar: true },
    },
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('@/views/ProfileView.vue'),
    },
  ],
})

export default router
