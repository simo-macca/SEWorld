package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import ch.usi.inf.bsc.sa4.lab02spring.model.Feedback;
import java.util.List;

/**
 * A Data Transfer Object (DTO) for conveying overall feedback details.
 *
 * <p>This record encapsulates the summary of feedback for a set of questions, including:
 *
 * <ul>
 *   <li>{@code totalCorrect}: The total number of questions answered correctly.
 *   <li>{@code totalQuestions}: The total number of questions that were evaluated.
 *   <li>{@code percentage}: The percentage of questions answered correctly.
 *   <li>{@code feedbackData}: A list containing detailed feedback for each question. The list
 *       elements include various types of feedback objects.
 * </ul>
 *
 * @param totalCorrect the total number of correct answers.
 * @param totalQuestions the total number of questions evaluated.
 * @param percentage the percentage of correct answers.
 * @param feedbackData a list of feedback objects for individual questions.
 */
public record FeedbackDTO(
    int totalCorrect, int totalQuestions, Double percentage, List<Object> feedbackData) {
  /**
   * Constructs a new {@code FeedbackDTO} by extracting data from the provided {@code Feedback}
   * object.
   *
   * @param feedback the {@code Feedback} object containing the data to initialize this DTO
   */
  public FeedbackDTO(Feedback feedback) {
    this(
        feedback.getTotalCorrect(),
        feedback.getTotalQuestions(),
        feedback.getPercentage(),
        feedback.getFeedbackData());
  }
}
