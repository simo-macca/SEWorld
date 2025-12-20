<script setup>
defineProps({
  nameClass: {
    type: String,
    required: true,
  },
  topicProgress: {
    type: Number,
    required: true,
  },
})
</script>

<template>
  <div class="relative h-4 w-full overflow-hidden rounded-full bg-gray-200 dark:bg-gray-600">
    <div
      class="striped-bg flex h-4 items-center justify-center rounded-full font-bold text-white"
      :class="nameClass"
      :style="{ width: topicProgress + '%' }"
    >
      {{ Math.round(topicProgress) }}%
    </div>
  </div>
</template>

<style scoped>
.striped-bg {
  background-image: linear-gradient(
    -45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%,
    transparent
  );
  background-size: 1rem 1rem;
}
</style>
