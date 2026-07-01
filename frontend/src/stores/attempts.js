import { defineStore } from 'pinia'
import {
    GET_ALL_ATTEMPTS_FOR_USER_BY_EXERCISE_DID,
    CREATE_NEW_ATTEMPT_BY_EXERCISE_DID,
    DELETE_ATTEMPT_BY_ATTEMPT_DID
} from "@/utils/constants.js";

export const useAttemptsStore = defineStore("attempts", {
    state: () => ({
        attempts: {},
    }),

    actions: {
        async fetchAttemptsByExerciseDid(exerciseDid) {
            try {
                const response = await fetch(`${GET_ALL_ATTEMPTS_FOR_USER_BY_EXERCISE_DID}`.replace(`{exercise_did}`, exerciseDid), {
                    method: "GET",
                    credentials: "include"
                });

                if (!response.ok) {
                    switch (response.status) {
                        case 404: {
                            // no attempts
                            this.attempts[exerciseDid] = [];
                        }
                    }
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }

                const attempts = (await response.json()).data;
                this.attempts[exerciseDid] = attempts;
            } catch (error) {
                console.error("Error fetching attempts:", error);
            }
        },

        // Returns did of the newly created attempt or null
        async createAttemptByExerciseDid(exerciseDid) {
            try {
                const response = await fetch(`${CREATE_NEW_ATTEMPT_BY_EXERCISE_DID}`.replace(`{exercise_did}`, exerciseDid), {
                    method: "POST",
                    credentials: "include"
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }

                const newAttempt = (await response.json()).data;

                if (newAttempt) {
                    return newAttempt[0].attemptDID;
                }

                return null;
            } catch (error) {
                console.error("Error fetching attempts:", error);
                return null;
            }
        },

        // Delete attempt by the attemptDid
        // Returns true if deleted successfully
        async deleteAttemptByAttemptDid(attemptDid) {
            try {
                const response = await fetch(`${DELETE_ATTEMPT_BY_ATTEMPT_DID}/${attemptDid}`, {
                    method: "DELETE",
                    credentials: "include",
                    headers: {
                        "Content-Type": "application/json"
                    }
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }

                // Optionally remove it from the local store
                for (const exDid in this.attempts) {
                    if (Array.isArray(this.attempts[exDid])) {
                        this.attempts[exDid] = this.attempts[exDid].filter(
                            attempt => attempt.attemptDID !== attemptDid
                        );
                    }
                }

                return true;
            } catch (error) {
                console.error("Error deleting attempt:", error);
                return false;
            }
        }

    },

    getters: {
        getAttemptsByExerciseDid: (state) => (exDid) => state.attempts[exDid] ? state.attempts[exDid] : null,

        isExerciseCompletedByExerciseDid: (state) => (exDid) => state.attempts[exDid] ? state.attempts[exDid].some(a => a.attemptIsCompleted === true) : false,
    }
});