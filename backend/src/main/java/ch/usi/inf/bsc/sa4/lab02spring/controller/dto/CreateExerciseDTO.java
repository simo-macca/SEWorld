package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import java.util.Collections;
import java.util.List;

/**
 * Data Transfer Object (DTO) for creating a new exercise.
 *
 * <p>This DTO encapsulates the information required to create an exercise, including its title,
 * description, and an optional list of questions.
 *
 * @param exerciseTitle the title of the exercise
 * @param exerciseDescription the description of the exercise
 * @param questions the list of questions associated with the exercise; may be empty if not provided
 */
public record CreateExerciseDTO(
    String exerciseTitle, String exerciseDescription, List<QuestionDTO> questions) {

  /**
   * Constructs a {@code CreateExerciseDTO} from an {@link Exercise} entity.
   *
   * <p>This constructor extracts the title and description from the given {@code Exercise} object
   * and initializes the list of questions as empty.
   *
   * @param exercise the {@link Exercise} entity from which to create the DTO
   */
  public CreateExerciseDTO(Exercise exercise) {
    this(exercise.getExerciseTitle(), exercise.getExerciseDescription(), Collections.emptyList());
  }

  /**
   * Constructs a {@code CreateExerciseDTO} with the specified title and description.
   *
   * <p>The list of questions will be initialized as empty.
   *
   * @param exerciseTitle the title of the exercise
   * @param exerciseDescription the description of the exercise
   */
  public CreateExerciseDTO(String exerciseTitle, String exerciseDescription) {
    this(exerciseTitle, exerciseDescription, Collections.emptyList());
  }
}
