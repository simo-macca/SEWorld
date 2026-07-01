package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.Objects;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) for encapsulating exercise statistics.
 *
 * <p>This DTO holds basic information about an exercise, including its unique decentralized
 * identifier (DID) and title. It is typically used for transferring exercise-related data between
 * different layers of the application.
 */
public class ExercisesStatsDTO {
  /** The unique decentralized identifier (DID) of the exercise. */
  private UUID exerciseDid;

  /** The title of the exercise */
  private String exerciseTitle;

  /**
   * Constructs a new {@code ExercisesStatsDTO} with the specified exercise DID and title.
   *
   * @param exerciseDid the unique decentralized identifier (DID) of the exercise
   * @param exerciseTitle the title of the exercise
   */
  public ExercisesStatsDTO(UUID exerciseDid, String exerciseTitle) {
    this.exerciseDid = exerciseDid;
    this.exerciseTitle = exerciseTitle;
  }

  /**
   * Returns the decentralized identifier (DID) of the exercise.
   *
   * @return the exercise DID
   */
  public UUID getExerciseDid() {
    return exerciseDid;
  }

  /**
   * Sets the decentralized identifier (DID) of the exercise.
   *
   * @param exerciseDid the exercise DID to set
   */
  public void setExerciseDid(UUID exerciseDid) {
    this.exerciseDid = exerciseDid;
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

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ExercisesStatsDTO that)) {
      return false;
    }
    return Objects.equals(exerciseDid, that.exerciseDid)
        && Objects.equals(exerciseTitle, that.exerciseTitle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(exerciseDid, exerciseTitle);
  }

  @Override
  public String toString() {
    return "ExercisesStatsDTO{"
        + "exerciseDid="
        + exerciseDid
        + ", exerciseTitle='"
        + exerciseTitle
        + '\''
        + '}';
  }
}
