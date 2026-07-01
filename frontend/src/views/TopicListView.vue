<script>
import TopicList from "@/components/TopicList.vue";
import { useTopicsStore } from "@/stores/topics";
import Header from "@/components/Header.vue";
import { useUserStore } from "@/stores/user";
import { useSearchStore } from "@/stores/search";
import AskAIPopup from "@/components/popup/AskAIPopup.vue";

const store = useTopicsStore();

export default {
  name: "TopicListView",
  components: {
    TopicList,
    Header,
  },
  data() {
    return {
      topics: [],
      isStudent: true,
    };
  },

  computed: {
    computeTopics() {
      let TOPICS = this.topics;
      const searchWord = this.searchStore().getWord
      if (searchWord !== undefined && typeof searchWord  === 'string' && searchWord.length > 0) {
        // filter by search
				TOPICS = [...TOPICS].filter(
					(t) =>
						t.title &&
						t.title.toLowerCase().includes(searchWord.toLowerCase())
				);
      }

      // Return filtered topics
      return TOPICS;
    }
  },

  methods: {
    userStore() {
      return useUserStore();
    },
    searchStore() {
      return useSearchStore();
    }
  },
  async mounted() {
    await this.userStore().refreshUser();
    this.isStudent = this.userStore().getUserRole === 'STUDENT';
    await store.fetchTopics();
    this.topics = store.topics;
  },
};
</script>

<template>
  <Header />
  <span>
        <AskAIPopup
        />
  
</span>
  <div class="topics-container">
    <h1 class="highlight">Topic List</h1>
    <TopicList :topics="computeTopics" :isStudent="isStudent" />
  </div>
</template>

<style scoped>
.topics-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 2rem;
}

.highlight {
  align-self: center;
	font-size: 4rem;
	margin-bottom: 2rem;
}
</style>