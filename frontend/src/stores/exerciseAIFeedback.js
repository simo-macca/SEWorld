import { defineStore } from "pinia";
import { ACCEPT_AI_EVALUATION, DENY_AI_EVALUATION, GET_AI_EVALUATIONS, GET_EXERCISE_ROUTE_BY_EXERCISE_DID } from "@/utils/constants";

export const useExerciseAIFeedbackStore = defineStore('exerciseAIFeedback', {
  state: () => ({
    isLoading: false,
    error: false,
    errorMessage: 'There was an error while loading the data',
    exerciseTitle: '',
    questionsAndAnswers: [],
  }),

  actions: {
    async loadExerciseData(exerciseDid) {
      this.isLoading = true;
      this.error = false;

      try {
        const exerciseResponse = await fetch(
          `${GET_EXERCISE_ROUTE_BY_EXERCISE_DID}`.replace(`{exercise_did}`, exerciseDid),
          {
            method: 'GET',
            credentials: 'include',
          }
        );

        if (!exerciseResponse.ok) {
          throw new Error(`Failed to load exercise: ${exerciseResponse.status}`);
        }

        const exerciseData = await exerciseResponse.json();
        if (exerciseData.data && exerciseData.data.length > 0) {
          this.exerciseTitle = exerciseData.data[0].exerciseTitle;
        }

        const feedbackResponse = await fetch(
          `${GET_AI_EVALUATIONS}/${exerciseDid}`,
          {
            method: 'GET',
            credentials: 'include',
          }
        );

        if (!feedbackResponse.ok) {
          throw new Error(`Failed to load feedback: ${feedbackResponse.status}`);
        }

        const feedbackData = await feedbackResponse.json();
        if (feedbackData.data) {
          this.questionsAndAnswers = feedbackData.data;
        }
      } catch (err) {
        console.error('Error loading AI feedback:', err);
        this.error = true;
        this.errorMessage = `Failed to load feedback: ${err.message}`;
      } finally {
        this.isLoading = false;
      }
    },

    async denyEvaluation(aiEvaluationDid) {
      try {
        const response = await fetch(
          `${DENY_AI_EVALUATION}/${aiEvaluationDid}`,
          {
            method: 'DELETE',
            credentials: 'include',
          }
        );

        if (!response.ok) {
          throw new Error(`Failed to deny evaluation: ${response.status}`);
        }

        return true;
      } catch (err) {
        console.error('Error denying AI evaluation:', err);
        throw err;
      }
    },

    async acceptEvaluation(aiEvaluationDid) {
      try {
        const response = await fetch(
          `${ACCEPT_AI_EVALUATION}/${aiEvaluationDid}`,
          {
            method: 'PATCH',
            credentials: 'include',
          }
        );

        if (!response.ok) {
          throw new Error(`Failed to accept evaluation: ${response.status}`);
        }

        return true;
      } catch (err) {
        console.error('Error accepting AI evaluation:', err);
        throw err;
      }
    },
  },
}); 