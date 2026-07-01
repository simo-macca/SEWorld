import { defineStore } from 'pinia'
import {GET_ALL_EXERCISES_IN_TOPIC} from "@/utils/constants.js";

// what's difference between exercise and exercises?

export const useExerciseStore = defineStore("exercise", {
    state: () => ({
        exercises: [],
    }),
    actions: {
        async fetchTopicExercises(topic) {
            try {
                const response = await fetch(`${GET_ALL_EXERCISES_IN_TOPIC}/${topic}`, {
                    method: "GET",
                    credentials: "include"
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }

                const data = await response.json();
                this.exercises = data.data;
            } catch (error) {
                console.error("Error fetching exercises:", error);
            }
        }
    }
});