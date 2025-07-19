<script setup>
import SearchBar from '@/components/SearchBar.vue'
import { useCollapseStore } from '@/stores/isCollapse.js'

const collapse = useCollapseStore()

const props = defineProps({
  topicDid: {
    type: String,
    required: true,
  },
  topicName: {
    type: String,
    required: true,
  },
})
</script>

<template>
  <main>
    <SearchBar v-if="collapse.isCollapse" class="mobile" />
    <div class="container-fluid">
      <h1 class="mt-4 mb-4">Exercise {{ topicName }}</h1>
      <div class="">
        <h2 class="">Not Completed</h2>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
      </div>
      <div class="border border-2 rounded-pill mt-4 mb-4"></div>
      <div class="">
        <h2>Completed</h2>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
        <div class="">ciao</div>
      </div>
    </div>
  </main>
</template>
