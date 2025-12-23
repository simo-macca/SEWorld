<script setup>
import { RouterView } from 'vue-router'
import HeaderDesktop from '@/components/HeaderComponents/HeaderDesktop.vue'
import ThemeToggle from '@/components/UtilsComponents/ThemeToggle.vue'
import { Toaster } from 'vue-sonner'
import 'vue-sonner/style.css'
import { useThemeStore } from '@/stores/isDark.js'
import { useTopicsStore } from '@/stores/topicsStore.js'
import { onMounted } from 'vue'
import { useUsersStore } from '@/stores/usersStore.js'

const theme = useThemeStore()
const topicsStore = useTopicsStore()
const userStore = useUsersStore()

// UPDATED: Fetch global data on app mount
onMounted(async () => {
  await userStore.getUser() // Recommended: Fetch user profile here too
  topicsStore.getTopics()
})
</script>

<template>
  <Toaster
    v-if="$route.name !== 'Home'"
    closeButton
    :expand="false"
    position="top-right"
    :theme="theme.isDark ? 'dark' : 'light'"
    richColors
    closeButtonPosition="top-left"
  />
  <div class="flex min-h-screen flex-col font-sans">
    <HeaderDesktop v-if="!$route.meta.hideNavBar" />
    <RouterView class="relative min-h-screen px-4 pb-10" />
    <ThemeToggle />
  </div>
</template>
