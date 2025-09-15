package backend.controller.dto;

import backend.model.Exercise;
import java.time.LocalDateTime;

public record ExerciseDTO(
    String exerciseTitle,
    String exerciseDescription,
    boolean exerciseIsDraft,
    boolean exerciseIsCompleted,
    LocalDateTime exerciseCreatedDate,
    String exerciseSlug,
    String exerciseOwner) {

  public ExerciseDTO(Exercise exercise) {
    this(
        exercise.getTopicTitle(),
        exercise.getTopicDescription(),
        exercise.IsDraft(),
        exercise.IsCompleted(),
        exercise.getExerciseCreatedDate(),
        exercise.getExerciseSlug(),
        exercise.getTopicOwner().getName());
  }
}
