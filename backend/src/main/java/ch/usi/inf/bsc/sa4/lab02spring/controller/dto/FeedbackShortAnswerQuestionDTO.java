package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.UUID;

/**
 * A Data Transfer Object (DTO) for conveying feedback on a short answer question.
 *
 * <p>This record encapsulates the feedback details for a short answer question, including:
 *
 * <ul>
 *   <li>{@code questionDid}: The unique decentralized identifier (DID) of the question.
 *   <li>{@code isCorrect}: A boolean flag indicating whether the answer provided is correct.
 *   <li>{@code correctAnswer}: A list of acceptable correct answers for the question.
 * </ul>
 *
 * @param questionDid the decentralized identifier (DID) of the question.
 * @param isCorrect {@code true} if the provided answer is correct; {@code false} otherwise. //
 * @param correctAnswer a correct phrase answer for the question.
 */
public record FeedbackShortAnswerQuestionDTO(
    UUID questionDid, boolean isCorrect, String correctAnswer)
    implements FeedbackQuestionDTO<String> {
  @Override
  public FeedbackQuestionDTO<String> newFeedbackQuestionDto(boolean isCorrect) {
    return new FeedbackShortAnswerQuestionDTO(questionDid, isCorrect, correctAnswer);
  }
}
