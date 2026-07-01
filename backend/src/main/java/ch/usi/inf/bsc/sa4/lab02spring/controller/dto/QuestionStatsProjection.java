package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

/**
 * Projection interface for retrieving statistics related to a specific question.
 *
 * <p>This interface defines methods to access the title of the question, the total number of
 * attempts made, and the count of incorrect answers.
 */
public interface QuestionStatsProjection {
  /**
   * Retrieves the title of the question.
   *
   * @return the question's title
   */
  String getQuestionTitle();

  /**
   * Retrieves the total number of attempts made on the question.
   *
   * @return the total attempt count
   */
  int getTotalAttempts();

  /**
   * Retrieves the count of incorrect answers submitted for the question.
   *
   * @return the count of wrong answers
   */
  int getWrongAnswersCount();
}
