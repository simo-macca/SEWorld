<script>
import {BCardGroup} from "bootstrap-vue-next";
import TopicCard from './TopicCard.vue';

export default {
  name: 'TopicList',
  components: {
    TopicCard
  },
  props: {
    topics: Array,
    isStudent: Boolean
  }
}
</script>

<template>
  <div class="d-flex flex-column gap-3 justify-content-center align-items-center">
    <TopicCard v-for="topic in topics" :key="topic.id" :topic="topic" :isStudent="isStudent" :exercisesAvailable="topic.exercisesAvailable" :materialsAvailable="topic.materialsAvailable"></TopicCard>
  </div>
</template>

<style scoped>
</style>
