package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.UUID;

/**
 * Represents the user’s answer to a question along with correctness information.
 *
 * <p>This generic interface defines the contract for question feedback DTOs, exposing the
 * question’s unique identifier, whether the submitted answer was correct, and the expected correct
 * answer.
 *
 * @param <T> the type of the correct answer
 */
public interface FeedbackQuestionDTO<T> {

  /**
   * Returns the globally unique identifier (DID) of the question.
   *
   * @return the question’s UUID
   */
  UUID questionDid();

  /**
   * Indicates whether the user’s submitted answer was judged correct.
   *
   * @return {@code true} if the answer is correct; {@code false} otherwise
   */
  boolean isCorrect();

  /**
   * Returns the expected correct answer in its DTO form.
   *
   * @return the correct answer payload of type {@code T}
   */
  T correctAnswer();

  /**
   * Creates a new {@link FeedbackQuestionDTO} instance based on the correctness of the answer.
   *
   * @param isCorrect a boolean indicating whether the answer is correct
   * @return a new {@link FeedbackQuestionDTO} instance representing the feedback for the question
   */
  FeedbackQuestionDTO<T> newFeedbackQuestionDto(boolean isCorrect);
}
