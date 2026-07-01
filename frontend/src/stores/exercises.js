import {defineStore} from 'pinia'
import axios from 'axios'
import {BASE_AUTH_URL} from "@/utils/constants.js";

const apiClient = axios.create({
  baseURL: `${BASE_AUTH_URL}`,
  withCredentials: true
});

export const useExercisesStore = defineStore('exercises', {
  state: () => ({
    formTitle: '',
    formDescription: '',
    questions: [],
    tempQuestionDid: 0,
    questionToDelete: [],
    pairs: [],
  }),

  actions: {
    resetForm() {
      this.formTitle = '';
      this.formDescription = '';
      this.questions = [];
      this.tempQuestionDid = 0;
      this.questionToDelete = [];
      this.pairs = [];
    },


    nextTempQuestionDid() {
      const did = `temp-questionDid-${this.tempQuestionDid}`;
      this.tempQuestionDid += 1;
      return did;
    },

    nextVariantIndex() {
      return this.questions.length === 0 ? 0
          : Math.max(...this.questions.map((q) => q.variantIndex ?? 0)) + 1;
    },

    addQuestion() {
      const question = {
        variantIndex: this.nextVariantIndex(),
        questionDid: this.nextTempQuestionDid(),
        text: '',
        type: '',
        _cachedType: '',
        options: [''],
        correctAnswer: 0
      };
      this.questions.push(question);
      this.sortQuestionsByVariantIndex();
    },

    async setQuestionType(question, type) {
      // Update the cached type
      question._cachedType = type;

      // Delete all variants with the same variantIndex
      await this.deleteQuestionsWithVariantIndex(question.questionDid);

      // Update the current question's type
      question.type = type;

      // Reset the question's data based on the new type
      if (type === 'multiple') {
        question.options = [''];
        question.correctAnswer = 0;
      }
      if (type === 'truefalse') {
        delete question.options;
        question.correctAnswer = true;
      }
      if (type === 'short') {
        delete question.options;
        question.correctAnswer = '';
      }

      this.sortQuestionsByVariantIndex();

      // Validate questions after type change
      if (!this.validateQuestions()) {
        console.error('Question validation failed after type change');
        throw new Error('Invalid question state after type change');
      }
    },

    // Used for multiple choice question to add an option
    addOption(questionDid) {
      const question = this.questions.find(q => q.questionDid === questionDid);
      if (question && Array.isArray(question.options)) {
        question.options.push('');
      }
    },

    // Sort the questions by variantIndex
    sortQuestionsByVariantIndex() {
      this.questions.sort((a, b) => a.variantIndex - b.variantIndex);
    },

    // Add a variant for the current question
    // the new question is a copy of the current question
    // Call the `save_all` API to create the new question in backend
    addVariant(question) {
      const base = {
        variantIndex: question.variantIndex,
        questionDid: this.nextTempQuestionDid(),
        text: '',            // blank title
        type: question.type, // keep same type
        _cachedType: question.type,
      };
      // initialize fields per type:
      if (question.type === 'multiple') {
        base.options = [''];       // one empty option
        base.correctAnswer = 0;    // reset selection
      } else if (question.type === 'truefalse') {
        base.correctAnswer = true; // default to true
      } else if (question.type === 'short') {
        base.correctAnswer = ''; // one empty keyword
      }
      this.questions.push(base);
      this.sortQuestionsByVariantIndex();
      if (!this.validateQuestions()) throw new Error('Invalid question after addVariant');
    },

    // Add a variant for the current question with AI
    async addVariantWithAI(question) {
      const questionDTO = {
        questionTitle: question.text,
        type: question.type === 'multiple' ? 'MCH' :
            question.type === 'truefalse' ? 'TF' : 'SHA',
        correctAnswer: question.correctAnswer,
        variantIndex: question.variantIndex
      };

      if (question.type === 'multiple') {
        questionDTO.choices = question.options;
      }

      const response = await apiClient.post(`AI/generate/question_variant`, [questionDTO]);

      const resQuestion  = response.data.data;
      // Create a new question from the AI response
      const newQuestion = {
        variantIndex: question.variantIndex,
        questionDid: this.nextTempQuestionDid(),
        text: resQuestion.questionTitle,
        type: question.type,
        _cachedType: question.type,
        correctAnswer: resQuestion.correctAnswer
      };

      if (newQuestion.type === 'multiple') {
        newQuestion.options = resQuestion.choices;
      }

      this.questions.push(newQuestion);
      this.sortQuestionsByVariantIndex();
    },

    // Delete the questions with the same variantIndex
    // This function is used when changing a question's type.
    async deleteQuestionsWithVariantIndex(questionDid) {
      const currentQuestion = this.questions.find(q => q.questionDid === questionDid);
      if (!currentQuestion) return;

      const variantIndex = currentQuestion.variantIndex;

      // Find questions to delete (same variantIndex but different questionDid)
      const questionsToDelete = this.questions.filter(q =>
          q.variantIndex === variantIndex && q.questionDid !== questionDid
      );

      // Delete each question using the existing deleteQuestion function
      for (const question of questionsToDelete) {
        await this.deleteQuestion(question.questionDid);
      }

      this.sortQuestionsByVariantIndex();
    },

    validateQuestions() {
      // Get all unique variantIndexes and sort them
      const variantIndexes = [...new Set(this.questions.map(q => q.variantIndex))].sort((a, b) => a - b);

      // Check if variantIndexes start from 0 and are consecutive
      const isConsecutive = variantIndexes.every((index, i) => index === i);
      if (!isConsecutive) {
        console.error('Variant indexes are not consecutive starting from 0');
        return false;
      }

      // Check if questions with same variantIndex have same type
      for (const variantIndex of variantIndexes) {
        const questionsWithSameIndex = this.questions.filter(q => q.variantIndex === variantIndex);
        const firstType = questionsWithSameIndex[0]?.type;

        const allSameType = questionsWithSameIndex.every(q => q.type === firstType);
        if (!allSameType) {
          console.error(`Questions with variantIndex ${variantIndex} have different types`);
          return false;
        }
      }

      return true;
    },

    deleteQuestion(questionDid) {
      const questionIndex = this.questions.findIndex(q => q.questionDid === questionDid);
      if (questionIndex !== -1) {
        this.questions.splice(questionIndex, 1);
      }

      if (questionDid && !questionDid.startsWith('temp-questionDid-')) {
        this.questionToDelete.push(questionDid);
      }
    },

    async deleteQuestionsBeforeSave() {
      for (const questionDid of this.questionToDelete) {
        await apiClient.delete(`/topic/exercises/question/delete/${questionDid}`);
      }
    },

    deleteOption(questionDid, optionIndex) {
      const question = this.questions.find(q => q.questionDid === questionDid);
      if (!question || !Array.isArray(question.options)) return;

      // Delete an option from multiple choice question
      question.options.splice(optionIndex, 1);

      // If the deleted option was the correct answer, reset correctAnswer
      if (question.correctAnswer === optionIndex) {
        question.correctAnswer = '';
      }
      // If deleted option comes before the correct answer, shift the correct answer index
      else if (question.correctAnswer > optionIndex) {
        question.correctAnswer -= 1;
      }
    },

    deleteKeyword(questionDid, keywordIndex) {
      // Delete a keyword from short answer question
      const question = this.questions.find(q => q.questionDid === questionDid);
      if (question && Array.isArray(question.correctAnswer)) {
        question.correctAnswer.splice(keywordIndex, 1);
      }
    },

    // Create a new exercise and save the questions.
    // Call the `exercises/create` API to create the exercise in the backend.
    async createExercise(topicDid) {
      const payload = {
        exerciseTitle: this.formTitle,
        exerciseDescription: this.formDescription,
      };

      const response = await apiClient.post(`/topic/exercises/create/${topicDid}`, payload);
      const exercise = response.data.data[0];

      // Update the exercise with the questions.
      await this.updateExercise(exercise.exerciseDID);

      // Reset the form.
      this.resetForm();

      return exercise.exerciseDID;
    },

    async reindexVariantIndex() {
      // Sort questions by their current variantIndex
      this.sortQuestionsByVariantIndex();

      // Get all unique variantIndexes
      const variantIndexes = [...new Set(this.questions.map(q => q.variantIndex))].sort((a, b) => a - b);

      // Create a mapping from old to new variantIndex
      const indexMapping = {};
      variantIndexes.forEach((oldIndex, newIndex) => {
        indexMapping[oldIndex] = newIndex;
      });

      // Update all questions with new variantIndex
      this.questions.forEach(question => {
        question.variantIndex = indexMapping[question.variantIndex];
      });

      // Sort questions again to ensure correct order
      this.sortQuestionsByVariantIndex();
    },

    // Populate the form with the exercise and questions.
    // This function is used when the user clicks on the "Edit" button.
    // Fetch the exercise and questions from the backend API.
    async populateForm(exerciseDid) {
      /* Get the information of an exercise, but not questions of the exercise. */
      const response = await apiClient.get(`/topic/exercises/get_by_exercise_did/${exerciseDid}`);
      const exercise = response.data.data[0]; // typeof exercise: Object

      /* Get all the questions related to this exercise. */
      const questionResponse = await apiClient.get(`/topic/exercises/question/exercise/${exerciseDid}`);
      const questions = questionResponse.data.data; // typeof questions: Array<QuestionDTO>

      // Set the exercise title and description.
      this.formTitle = exercise.exerciseTitle;
      this.formDescription = exercise.exerciseDescription;

      // Set the questions.
      this.questions = questions.map((q) => {
        // Set the question type.
        let type = 'short';
        if (q.type === 'TF') type = 'truefalse';
        else if (q.type === 'MCH') type = 'multiple';

        let correctAnswer = q.correctAnswer;
        if (type === 'short') {
          if (Array.isArray(q.correctAnswer)) {
            correctAnswer = q.correctAnswer[0] || '';
          }
        }

        return {
          text: q.questionTitle,
          type,
          _cachedType: type,
          options: q.choices || [],
          correctAnswer: correctAnswer,
          questionDid: q.questionDid,
          variantIndex: q.variantIndex,
          variantDid: q.variantDid
        };
      });

      await this.reindexVariantIndex();

      // We suppose that the questions' variantIndexs are sequential.
      // Sort the questions by variantIndex.
      this.sortQuestionsByVariantIndex();
    },

    async updateExercise(exerciseDid) {
      // Validate questions before updating
      if (!this.validateQuestions()) {
        console.error('Question validation failed before update');
        throw new Error('Invalid question state before update');
      }

      await this.deleteQuestionsBeforeSave();

      await this.reindexVariantIndex();

      // Construct the questions' payload.
      const updateQuestions = this.questions
          .filter(q => !q.questionDid.startsWith('temp-questionDid'))
          .map(q => {
            const base = {
              questionDid: q.questionDid,
              questionTitle: q.text,
              correctAnswer: q.correctAnswer,
              variantIndex: q.variantIndex,
              exerciseDid: exerciseDid,
            };

            if (q.type === 'multiple') {
              return {
                ...base,
                type: 'MCH',
                choices: q.options
              };
            }
            if (q.type === 'truefalse') {
              return {
                ...base,
                type: 'TF'
              };
            }
            if (q.type === 'short') {
              return {
                ...base,
                type: 'SHA'
              };
            }
          })

      const newQuestions = this.questions
          .filter(q => q.questionDid.startsWith('temp-questionDid'))
          .map(q => {
            const base = {
              questionTitle: q.text,
              correctAnswer: q.correctAnswer,
              variantIndex: q.variantIndex,
              exerciseDid: exerciseDid,
            };

            if (q.type === 'multiple') {
              return {
                ...base,
                type: 'MCH',
                choices: q.options
              };
            }
            if (q.type === 'truefalse') {
              return {
                ...base,
                type: 'TF'
              };
            }
            if (q.type === 'short') {
              return {
                ...base,
                type: 'SHA'
              };
            }
          });

      const exerciseDTO = {
        exerciseTitle: this.formTitle,
        exerciseDescription: this.formDescription,
      };
      await apiClient.patch(
          `/topic/exercises/teacher/update_exercise/${exerciseDid}`,
          exerciseDTO
      );

      const SwapIndexQuestionsDTO = {
        questionDTOs: updateQuestions,
        pairs: this.pairs
      };

      if (updateQuestions.length > 0) {
        await apiClient.patch(
            `/topic/exercises/question/update/${exerciseDid}`,
            SwapIndexQuestionsDTO
        );
      }

      if (newQuestions.length > 0) {
        await apiClient.post(`/topic/exercises/question/save_all/${exerciseDid}`, newQuestions);
      }
      
      this.resetForm();
    },

    async swapVariantIndex(exerciseDid, x, y) {
      const pair = { first: x, second: y };
      this.pairs.push(pair);
      await apiClient.patch(`/topic/exercises/question/update/${exerciseDid}`, {
        questionDTOs: [],
        pairs: this.pairs
      });
    },

    deleteQuestionsByVariantIndex(variantIndex) {
      const toDelete = this.questions.filter(
          q => q.variantIndex === variantIndex
      );
      toDelete.forEach(q => this.deleteQuestion(q.questionDid));
    },

    createNewTypeQuestion(variantIndex, type, questionTitle) {
      const q = {
        variantIndex,
        questionDid: this.nextTempQuestionDid(),
        text: questionTitle,
        type,
        _cachedType: type,
      };

      if (type === 'multiple') {
        q.options = [''];
        q.correctAnswer = 0;
      } else if (type === 'truefalse') {
        q.correctAnswer = true;
      } else {
        q.correctAnswer = '';
      }

      this.questions.push(q);
      this.sortQuestionsByVariantIndex();
      return q;
    },
  }
});
