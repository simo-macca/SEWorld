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
      class="striped-bg h-4 rounded-full transition-all duration-300 ease-out"
      :class="nameClass"
      :style="{ width: topicProgress + '%' }"
    ></div>

    <div class="absolute inset-0 flex items-center justify-center">
      <span class="text-gray-700 drop-shadow-sm dark:text-white">
        {{ Math.round(topicProgress) }}%
      </span>
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
