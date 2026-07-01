package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

/**
 * Data Transfer Object (DTO) for conveying statistics related to a specific question.
 *
 * <p>This record encapsulates details about a question, including its title, the total number of
 * attempts made on the question, and the count of wrong answers. It serves as a data carrier
 * between different application layers.
 *
 * @param questionTitle the title of the question
 * @param totalAttempts the total number of attempts made on the question
 * @param wrongAnswers the count of wrong answers for the question
 */
public record QuestionStatsDTO(String questionTitle, Integer totalAttempts, Integer wrongAnswers) {
  /**
   * Converts a {@code QuestionStatsProjection} instance into a {@code QuestionStatsDTO}.
   *
   * <p>This helper method creates a new {@code QuestionStatsDTO} by extracting the relevant fields
   * from the provided projection, thereby facilitating the transfer of statistics data.
   *
   * @param p the {@code QuestionStatsProjection} containing the raw statistics data
   * @return a new {@code QuestionStatsDTO} populated with data from the projection
   */
  public static QuestionStatsDTO toDto(QuestionStatsProjection p) {
    return new QuestionStatsDTO(
        p.getQuestionTitle(), p.getTotalAttempts(), p.getWrongAnswersCount());
  }
}
