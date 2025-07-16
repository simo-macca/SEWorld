<script setup>
import { BFormInput, BInputGroup, BNavForm } from 'bootstrap-vue-next'
import { useCollapseStore } from '@/stores/isCollapse.js'
import { useThemeStore } from '@/stores/isDark.js'
const collapse = useCollapseStore()
const theme = useThemeStore()
</script>

<template>
  <b-nav-form :style="!collapse.isCollapse ? 'width: 38dvw' : ''">
    <b-input-group :prepend="$route.name">
      <b-form-input
        placeholder="Search"
        :class="`focus-ring focus-ring-${theme.isDark ? 'dark' : 'light'}`"
      />
    </b-input-group>
  </b-nav-form>
</template>
