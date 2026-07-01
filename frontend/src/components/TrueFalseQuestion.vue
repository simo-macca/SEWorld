<template>
  <div class="card p-3">
    <h5 class="mb-3 fs-4">{{ question.variantIndex + 1}}. {{ question.questionTitle }}</h5>

    <div class="form-check">
      <input
          class="form-check-input"
          type="radio"
          :id="`true-${qdid}`"
          :name="`question_${qdid}`"
          value="true"
          v-model="selectedAnswer"
          @change="onAnswerSelected"
          :disabled="submitted"
          :class="{
          'border-success': showFeedback && feedback.isCorrect && selectedAnswer === 'true',
          'border-danger': showFeedback && !feedback.isCorrect && selectedAnswer === 'true'
        }"
      />
      <label class="form-check-label text-start fs-6" :for="`true-${qdid}`">
        True
      </label>
    </div>

    <div class="form-check">
      <input
          class="form-check-input"
          type="radio"
          :id="`false-${qdid}`"
          :name="`question_${qdid}`"
          value="false"
          v-model="selectedAnswer"
          @change="onAnswerSelected"
          :disabled="submitted"
          :class="{
          'border-success': showFeedback && feedback.isCorrect === true && selectedAnswer === 'false',
          'border-danger': showFeedback && feedback.isCorrect === false && selectedAnswer === 'false'
        }"
      />
      <label class="form-check-label fs-6" :for="`false-${qdid}`">
        False
      </label>
    </div>
  </div>
</template>

<script>
import { useQuestionsStore } from '@/stores/questions'

export default {
  name: 'TrueFalseQuestion',
  props: {
    question: Object,
    showFeedback: Boolean,
    feedback: Object,
    userAnswer: Object,
    submitted: Boolean
  },
  data() {
    return {
      selectedAnswer: null
    }
  },
  computed: {
    qdid() {
      return this.question.questionDid;
    }
  },
  mounted() {
    if (this.userAnswer && this.userAnswer.answerContent !== undefined) {
      this.selectedAnswer = this.userAnswer.answerContent;
    }
  },
  methods: {
    onAnswerSelected() {
      if(!this.submitted) {
        const store = useQuestionsStore();
        store.updateAnswer(this.qdid, this.selectedAnswer);
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
