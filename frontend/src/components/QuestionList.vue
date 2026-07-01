<template>
  <div>
    <div v-for="(question, index) in questions" :key="qdid(question)" class="question-container mb-4">
      <component
        v-if="!showFeedback || !feedbackMap[qdid(question)] || feedbackMap[qdid(question)].correctness !== false"
        :is="getQuestionComponent(question.type)"
        :question="question"
        :feedback="showFeedback ? feedbackMap[qdid(question)] : {}"
        :show-feedback="showFeedback"
        :user-answer="getUserAnswer(question)"
        :submitted="submitted"
      />
      <AnswerExplanation
        v-else
        :question-did="qdid(question)"
        :user-answer="getUserAnswer(question)"
        :show-explanation="true"
        :is-incorrect="true"
        :question="question"
        :question-index="index"
      >
        <template v-slot:my-answer>
          <component
              :is="getQuestionComponent(question.type)"
              :question="question"
              :feedback="showFeedback ? feedbackMap[qdid(question)] : {}"
              :show-feedback="showFeedback"
              :user-answer="getUserAnswer(question)"
              :submitted="submitted"
          />
        </template>
      </AnswerExplanation>
    </div>
  </div>
</template>

<script>
import MultipleChoiceQuestion from '@/components/MultipleChoiceQuestion.vue';
import ShortAnswerQuestion from '@/components/ShortAnswerQuestion.vue';
import TrueFalseQuestion from '@/components/TrueFalseQuestion.vue';
import AnswerExplanation from '@/components/AnswerExplanation.vue';

export default {
  name: 'QuestionList',
  props: {
    questions: Array,
    feedback: Array,
    showFeedback: Boolean,
    userAnswers: Object,
    submitted: Boolean,
  },
  
  components: {
    MultipleChoiceQuestion,
    ShortAnswerQuestion,
    TrueFalseQuestion,
    AnswerExplanation
  },
  
  computed: {
    feedbackMap() {
      const map = {};
      
      if (this.feedback && this.feedback.length) {
        for (const fb of this.feedback) {
          const did = fb.questionDid;
          
          // Create a copy of the feedback to avoid modifying props directly
          map[did] = { ...fb };
          
          // If correctness property is missing, make sure it's explicitly defined
          if (map[did].correctness === undefined) {
            // Try to determine correctness from other properties
            if (map[did].score !== undefined) {
              map[did].correctness = map[did].score > 0;
            } else if (map[did].isCorrect !== undefined) {
              map[did].correctness = map[did].isCorrect;
            } else if (map[did].correct !== undefined) {
              map[did].correctness = map[did].correct;
            } else {
              // Default to false if we can't determine correctness
              map[did].correctness = false;
            }
          }
        }
      }
      
      return map;
    }
  },
  
  methods: {
    console: console,
    
    getQuestionComponent(type) {
      switch (type) {
        case 'MCH':
          return 'MultipleChoiceQuestion';
        case 'SHA':
          return 'ShortAnswerQuestion';
        case 'TF':
          return 'TrueFalseQuestion';
        default:
          return 'div';
      }
    },
    
    qdid(question) {
      return question.questionDid;
    },
    
    getUserAnswer(question) {
      const questionDid = this.qdid(question);
      return this.userAnswers[questionDid];
    }
  }
};
</script>

<style scoped>
.question-container {
  border-bottom: 1px solid #eee;
  padding-bottom: 1rem;
  font-size: 1.25rem;
}

.question-container:last-child {
  border-bottom: none;
}

.fs-4 {
  font-size: 1.25rem !important;
}
</style>