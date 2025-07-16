<script setup>
import { RouterView } from 'vue-router'
import ThemeToggle from '@/components/ThemeToggleComponent.vue'
import HeaderComponent from '@/components/HeaderComponent.vue'
</script>

<template>
  <HeaderComponent v-if="!$route.meta.hideNavBar" />
  <RouterView class="container-fluid ps-2" />
  <ThemeToggle />
</template>
