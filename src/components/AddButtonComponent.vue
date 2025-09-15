<script setup>

import { BButton } from 'bootstrap-vue-next'

const props = defineProps({
  route: { type: String },
})

</script>

<template>
  <b-button class="border-0 bg-transparent m-0 p-0 add-button" :to="props.route">
    <svg xmlns="http://www.w3.org/2000/svg" width="60" height="60" viewBox="0 0 60 60" fill="none">
      <circle cx="30" cy="30" r="30" fill="#0096BF"/>
      <rect x="12" y="26" width="36" height="7.2" fill="black"/>
      <rect x="26.3999" y="12" width="7.2" height="36" fill="black"/>
    </svg>

  </b-button>
</template>

<style scoped>
.add-button {
  position: fixed;
  bottom: 70px;
  right: 20px;
}
</style>
