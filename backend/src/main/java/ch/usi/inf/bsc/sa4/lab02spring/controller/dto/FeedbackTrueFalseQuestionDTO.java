package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.UUID;

/**
 * A Data Transfer Object (DTO) for conveying feedback on a True/False question.
 *
 * <p>This record encapsulates the feedback details for a True/False question, including:
 *
 * <ul>
 *   <li>{@code questionDid}: The unique decentralized identifier (DID) of the question.
 *   <li>{@code isCorrect}: A boolean flag indicating whether the answer provided was correct.
 *   <li>{@code correctAnswer}: The correct answer for the question, which may be {@code true} or
 *       {@code false}. This value can also be {@code null} if the correct answer is not available.
 * </ul>
 *
 * @param questionDid the decentralized identifier (DID) of the question.
 * @param isCorrect {@code true} if the provided answer is correct; {@code false} otherwise.
 * @param correctAnswer the correct answer to the question; may be {@code null} if not applicable.
 */
public record FeedbackTrueFalseQuestionDTO(
    UUID questionDid, boolean isCorrect, Boolean correctAnswer)
    implements FeedbackQuestionDTO<Boolean> {
  @Override
  public FeedbackQuestionDTO<Boolean> newFeedbackQuestionDto(boolean isCorrect) {
    return new FeedbackTrueFalseQuestionDTO(questionDid, isCorrect, correctAnswer);
  }
}
