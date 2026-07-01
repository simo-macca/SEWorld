<template>
  <div class="card p-3">
    <h5 class="mb-3 fs-4">{{ question.variantIndex + 1}}. {{ question.questionTitle }}</h5>

    <div
        class="form-check"
        v-for="(option, i) in question.choices"
        :key="i"
        :class="{
        'border border-success rounded': showFeedback && feedback.isCorrect && selectedOption === i,
        'border border-danger rounded': showFeedback && !feedback.isCorrect && selectedOption === i
      }"
    >
      <input
          class="form-check-input"
          type="radio"
          :id="`q${qdid}_option${i}`"
          :name="`question_${qdid}`"
          :value="i"
          v-model="selectedOption"
          @change="onOptionSelected(i)"
          :disabled="submitted"
      />
      <label class="form-check-label text-start fs-6" :for="`q${qdid}_option${i}`">
        {{ option }}
      </label>
    </div>
  </div>
</template>

<script>
import { useQuestionsStore } from '@/stores/questions'

export default {
  name: 'MultipleChoiceQuestion',
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
      selectedOption: null
    }
  },
  computed: {
    qdid() {
      return this.question.questionDid;
    }
  },
  mounted() {
    this.selectedOption = parseInt(this.userAnswer.answerContent);
  },
  methods: {
    onOptionSelected(optionIndex) {
      if(!this.submitted) {
        const store = useQuestionsStore()
        store.updateAnswer(this.qdid, optionIndex.toString())
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
