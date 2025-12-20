<script setup>
import { RouterView } from 'vue-router'
import HeaderDesktop from '@/components/HeaderComponents/HeaderDesktop.vue'
import ThemeToggle from '@/components/UtilsComponents/ThemeToggle.vue'
import { Toaster } from 'vue-sonner'
import 'vue-sonner/style.css'
import { useThemeStore } from '@/stores/isDark.js'

const theme = useThemeStore()
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
    <RouterView class="px-4" />
    <ThemeToggle />
  </div>
</template>
