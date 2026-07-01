import { defineStore } from 'pinia'  ;
import { GET_EXERCISE_BY_ATTEMPT_DID_ROUTE } from '../utils/constants';


export const useQuestionsStore = defineStore('questions', {
  state: () => ({
    questions: [],
    userAnswers: {}
  }),


  actions: {

    async fetchQuestionsByAttempt(attemptDid) {
      const res = await fetch(`${GET_EXERCISE_BY_ATTEMPT_DID_ROUTE}/${attemptDid}`);
      const json = await res.json()
      this.questions = json.data
    },
    async fetchUserAttemptAnswers(attemptDid) {
        this.userAnswers = {};
      try {
        const res = await fetch(`/api/auth/topic/exercises/answers/getall/${attemptDid}`);
        const json = await res.json();

        for (const answer of json.data) {
          this.userAnswers[answer.questionDid] = {
            answerDID: answer.did,
            answerContent: answer.answerContent
          };
        }
      } catch (error) {
        console.error("Error fetching answers:", error);
      }
    }
,
async updateAnswer(questionDID, newValue) {
  const entry = this.userAnswers[questionDID];
  if (!entry) {
    console.warn(`No entry for questionDID ${questionDID}`);
    return;
  }

  entry.answerContent = newValue;

  await fetch(`/api/auth/topic/exercises/answers/update/${entry.answerDID}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      answerContent: newValue
    })
  });
}

  }
})
