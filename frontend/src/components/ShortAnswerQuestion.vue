<template>
  <div class="card p-3">
    <h5 class="mb-3 fs-4">{{ question.variantIndex + 1}}. {{ question.questionTitle }}</h5>

    <input
        type="text"
        class="form-control fs-6"
        :class="{
        'border-success': showFeedback && feedback.isCorrect,
        'border-danger': showFeedback && !feedback.isCorrect
      }"
        placeholder="Type your answer here..."
        v-model="userInput"
        @input="onInputChanged"
        :disabled="submitted"
        :id="`short_${qdid}`"
    />
  </div>
</template>

<script>
import { useQuestionsStore } from '@/stores/questions'

export default {
  name: 'ShortAnswerQuestion',
  props: {
    question: Object,
    index: Number,
    showFeedback: Boolean,
    feedback: Object,
    userAnswer: Object,
    submitted: Boolean
  },
  data() {
    return {
      userInput: ''
    }
  },
  computed: {
    qdid() {
      return this.question.questionDid;
    }
  },
  mounted() {
    this.userInput = this.userAnswer.answerContent;
  },
  methods: {
    onInputChanged() {
      if(!this.submitted) {
        const store = useQuestionsStore()
        store.updateAnswer(this.qdid, this.userInput)
      }
    }
  }
}
</script>

<style scoped>
.card {
  margin-bottom: 1rem;
  background-color: #f8f9fa;
  border-radius: 10px;
}

.border-success {
  background: #0080007F;
}

.border-danger {
  background: #FF000080;
}
</style>
