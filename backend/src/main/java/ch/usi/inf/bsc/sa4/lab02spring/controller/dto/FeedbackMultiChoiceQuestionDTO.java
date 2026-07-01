package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.UUID;

/**
 * A Data Transfer Object (DTO) for conveying feedback on a multiple choice question.
 *
 * <p>This record encapsulates the feedback details for a multiple choice question, including:
 *
 * <ul>
 *   <li>{@code questionDid}: The unique decentralized identifier (DID) of the question.
 *   <li>{@code isCorrect}: A boolean flag indicating whether the provided answer is correct.
 *   <li>{@code correctAnswer}: The correct answer for the question, represented as an integer. This
 *       corresponds to the index of the correct option.
 * </ul>
 *
 * @param questionDid the decentralized identifier (DID) of the question.
 * @param isCorrect {@code true} if the provided answer is correct; {@code false} otherwise.
 * @param correctAnswer the correct answer for the question, typically represented as an index.
 */
public record FeedbackMultiChoiceQuestionDTO(
    UUID questionDid, boolean isCorrect, Integer correctAnswer)
    implements FeedbackQuestionDTO<Integer> {
  @Override
  public FeedbackQuestionDTO<Integer> newFeedbackQuestionDto(boolean isCorrect) {
    return new FeedbackMultiChoiceQuestionDTO(questionDid, isCorrect, correctAnswer);
  }
}
