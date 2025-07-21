<script setup>
import SearchBar from '@/components/SearchBar.vue'
import { computed } from 'vue'
import { useCollapseStore } from '@/stores/isCollapse.js'
import { useTopicsStore } from '@/stores/topicsStore.js'
import { BCard, BNav, BNavItem } from 'bootstrap-vue-next'

const collapse = useCollapseStore()
const topicStore = useTopicsStore()

const props = defineProps({
  topicSlug: {
    type: String,
    required: true,
  },
})

const topicName = computed(() => {
  let name = topicStore.findCurrentTopic(props.topicSlug) || props.topicSlug
  return name.charAt(0).toUpperCase() + name.slice(1).split('-').join(' ')
})
</script>

<template>
  <main>
    <SearchBar v-if="collapse.isCollapse" class="mobile" />
    <div class="container-fluid">
      <h1 class="mt-4 mb-4">Exercise {{ topicName }}</h1>
      <b-nav tabs justified>
        <b-nav-item active>ALL</b-nav-item>
        <b-nav-item>NOT COMPLETED</b-nav-item>
        <b-nav-item>COMPLETED</b-nav-item>
      </b-nav>
      <div class="container"></div>
    </div>
  </main>
</template>
