package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) for conveying exercise statistics specific to instructors.
 *
 * <p>This DTO extends {@link ExercisesStatsDTO} by adding an average user grade, representing the
 * average grade obtained by users on a given exercise. It is used to provide instructors with a
 * summarized view of exercise performance.
 */
public class InstructorExerciseStatsDTO extends ExercisesStatsDTO {
  /** The average grade of users for this exercise. */
  private Double avgUsersGrade;

  /**
   * Constructs a new {@code InstructorExerciseStatsDTO} with the specified exercise details and
   * average user grade.
   *
   * @param exerciseDid the unique decentralized identifier (DID) of the exercise
   * @param exerciseTitle the title of the exercise
   * @param avgUsersGrade the average grade of users for the exercise
   */
  public InstructorExerciseStatsDTO(UUID exerciseDid, String exerciseTitle, Double avgUsersGrade) {
    super(exerciseDid, exerciseTitle);
    this.avgUsersGrade = avgUsersGrade;
  }

  /**
   * Constructs a new {@code InstructorExerciseStatsDTO} with the specified exercise details.
   *
   * <p>The average user grade is not initialized with this constructor.
   *
   * @param exerciseDid the unique decentralized identifier (DID) of the exercise
   * @param exerciseTitle the title of the exercise
   */
  public InstructorExerciseStatsDTO(UUID exerciseDid, String exerciseTitle) {
    super(exerciseDid, exerciseTitle);
  }

  /**
   * Returns the average grade of users for the exercise.
   *
   * @return the average user grade, or {@code null} if not set
   */
  public Double getAvgUsersGrade() {
    return avgUsersGrade;
  }

  /**
   * Sets the average grade of users for the exercise.
   *
   * @param avgUsersGrade the average grade to be set
   */
  public void setAvgUsersGrade(Double avgUsersGrade) {
    this.avgUsersGrade = avgUsersGrade;
  }
}
