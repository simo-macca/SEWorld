<script setup>
import { RouterView } from 'vue-router'

import HeaderComponent from '@/components/HeaderComponent.vue'
import ThemeToggle from '@/components/ThemeToggleComponent.vue'
</script>

<template>
  <HeaderComponent v-if="!$route.meta.hideNavBar" />
  <RouterView class="container-fluid ps-2" />
  <ThemeToggle />
</template>
