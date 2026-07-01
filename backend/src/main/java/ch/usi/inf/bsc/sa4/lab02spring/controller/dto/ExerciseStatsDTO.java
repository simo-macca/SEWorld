package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for conveying statistics related to an exercise.
 *
 * <p>This DTO encapsulates the title of the exercise, the average grade achieved by users, the
 * number of students who attempted the exercise, and detailed statistics for each question
 * associated with the exercise.
 */
public class ExerciseStatsDTO {
  /** The title of the exercise. */
  private String exerciseTitle;

  /** The average grade of users for this exercise. */
  private Double avgUsersGrade;

  /** The total number of students who attempted the exercise. */
  private Long studentsWhoAttempted;

  /** A list of detailed statistics for each question in the exercise. */
  private List<QuestionStatsDTO> questions;

  /**
   * Constructs a new {@code ExerciseStatsDTO} with the given metrics.
   *
   * @param exerciseTitle the exercise’s title
   * @param avgUsersGrade the average percentage score achieved by all users
   * @param studentsWhoAttempted the number of users who attempted the exercise
   * @param questions per-question statistics; must not be null
   * @throws NullPointerException if {@code questions} is null
   */
  public ExerciseStatsDTO(
      String exerciseTitle,
      Double avgUsersGrade,
      Long studentsWhoAttempted,
      List<QuestionStatsDTO> questions) {
    this.exerciseTitle = exerciseTitle;
    this.avgUsersGrade = avgUsersGrade;
    this.studentsWhoAttempted = studentsWhoAttempted;
    this.questions = new ArrayList<>(questions);
  }

  /**
   * Returns the title of the exercise.
   *
   * @return the exercise title
   */
  public String getExerciseTitle() {
    return exerciseTitle;
  }

  /**
   * Sets the title of the exercise.
   *
   * @param exerciseTitle the exercise title to set
   */
  public void setExerciseTitle(String exerciseTitle) {
    this.exerciseTitle = exerciseTitle;
  }

  /**
   * Returns the average grade of users for the exercise.
   *
   * @return the average user grade
   */
  public Double getAvgUsersGrade() {
    return avgUsersGrade;
  }

  /**
   * Sets the average grade of users for the exercise.
   *
   * @param avgUsersGrade the average user grade to set
   */
  public void setAvgUsersGrade(Double avgUsersGrade) {
    this.avgUsersGrade = avgUsersGrade;
  }

  /**
   * Returns the total number of students who attempted the exercise.
   *
   * @return the number of students who attempted the exercise
   */
  public Long getStudentsWhoAttempted() {
    return studentsWhoAttempted;
  }

  /**
   * Sets the total number of students who attempted the exercise.
   *
   * @param studentsWhoAttempted the number of students who attempted the exercise to set
   */
  public void setStudentsWhoAttempted(Long studentsWhoAttempted) {
    this.studentsWhoAttempted = studentsWhoAttempted;
  }

  /**
   * Returns the list of detailed question statistics associated with the exercise.
   *
   * @return a list of {@link QuestionStatsDTO} objects representing question statistics
   */
  public List<QuestionStatsDTO> getQuestions() {
    return new ArrayList<>(questions);
  }

  /**
   * Sets the list of detailed question statistics associated with the exercise.
   *
   * @param questions a list of {@link QuestionStatsDTO} objects to set
   */
  public void setQuestions(List<QuestionStatsDTO> questions) {
    this.questions = new ArrayList<>(questions);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ExerciseStatsDTO statsDTO)) {
      return false;
    }
    return Objects.equals(exerciseTitle, statsDTO.exerciseTitle)
        && Objects.equals(avgUsersGrade, statsDTO.avgUsersGrade)
        && Objects.equals(studentsWhoAttempted, statsDTO.studentsWhoAttempted)
        && Objects.equals(questions, statsDTO.questions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(exerciseTitle, avgUsersGrade, studentsWhoAttempted, questions);
  }

  @Override
  public String toString() {
    return "ExerciseStatsDTO{"
        + "exerciseTitle='"
        + exerciseTitle
        + '\''
        + ", avgUsersGrade="
        + avgUsersGrade
        + ", studentsWhoAttempted="
        + studentsWhoAttempted
        + ", questions="
        + questions
        + '}';
  }
}
