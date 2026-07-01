package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) for {@link Exercise} entities.
 *
 * <p>Encapsulates an exercise’s globally unique identifier (DID), title, description, draft status,
 * and completion flag for transfer between application layers.
 *
 * @param exerciseDID the decentralized identifier (UUID) of the exercise
 * @param exerciseTitle the human-readable title of the exercise
 * @param exerciseDescription a detailed description of what the exercise entails
 * @param isDraft {@code true} if the exercise is in draft mode and not yet published; {@code false}
 *     otherwise
 * @param isCompleted {@code true} if the exercise has been completed by a user; {@code false}
 *     otherwise
 */
public record ExerciseDTO(
    UUID exerciseDID,
    String exerciseTitle,
    String exerciseDescription,
    boolean isDraft,
    boolean isCompleted) {

  /**
   * Constructs an {@code ExerciseDTO} from an {@link Exercise} entity.
   *
   * @param ex the Exercise entity from which to extract the DTO fields.
   */
  public ExerciseDTO(Exercise ex) {
    this(
        ex.getExerciseDid(),
        ex.getExerciseTitle(),
        ex.getExerciseDescription(),
        ex.isExerciseIsDraft(),
        ex.isCompleted());
  }
}
