package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.Objects;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing statistical information about a student's performance in
 * a specific exercise.
 *
 * <p>This class extends {@link ExercisesStatsDTO} to include details pertinent to a student's
 * attempt on an exercise, such as whether the attempt was successful and the grade achieved.
 */
public class StudentExercisesStatsDTO extends ExercisesStatsDTO {

  /** Indicates whether the operation was successful. */
  private boolean isSuccessful;

  /** The grade achieved by the user, represented as a decimal value. */
  private Double userGrade;

  /**
   * Constructs a new {@code StudentExercisesStatsDTO} with the specified exercise details, success
   * status, and user grade.
   *
   * @param exerciseDid the unique identifier of the exercise
   * @param exerciseTitle the title of the exercise
   * @param isSuccessful indicates if the student's attempt was successful
   * @param userGrade the grade achieved by the student in the exercise
   */
  public StudentExercisesStatsDTO(
      UUID exerciseDid, String exerciseTitle, boolean isSuccessful, Double userGrade) {
    super(exerciseDid, exerciseTitle);
    this.isSuccessful = isSuccessful;
    this.userGrade = userGrade;
  }

  /**
   * Constructs a new {@code StudentExercisesStatsDTO} with the specified exercise details.
   *
   * @param exerciseDid the unique identifier of the exercise
   * @param exerciseTitle the title of the exercise
   */
  public StudentExercisesStatsDTO(UUID exerciseDid, String exerciseTitle) {
    super(exerciseDid, exerciseTitle);
  }

  /**
   * Checks if the student's attempt on the exercise was successful.
   *
   * @return {@code true} if the attempt was successful; {@code false} otherwise
   */
  public boolean isSuccessful() {
    return isSuccessful;
  }

  /**
   * Sets the success status of the student's attempt on the exercise.
   *
   * @param successful {@code true} if the attempt was successful; {@code false} otherwise
   */
  public void setSuccessful(boolean successful) {
    isSuccessful = successful;
  }

  /**
   * Retrieves the grade achieved by the student in the exercise.
   *
   * @return the student's grade for the exercise
   */
  public Double getUserGrade() {
    return userGrade;
  }

  /**
   * Sets the grade achieved by the student in the exercise.
   *
   * @param userGrade the grade to set for the student
   */
  public void setUserGrade(Double userGrade) {
    this.userGrade = userGrade;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof StudentExercisesStatsDTO that)) {
      return false;
    }
    return isSuccessful == that.isSuccessful && Objects.equals(userGrade, that.userGrade);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isSuccessful, userGrade);
  }

  @Override
  public String toString() {
    return "StudentExercisesStatsDTO{"
        + "isSuccessful="
        + isSuccessful
        + ", userGrade="
        + userGrade
        + '}';
  }
}
