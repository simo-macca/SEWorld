package backend.controller.dto;

import backend.model.Exercise;
import java.time.LocalDate;

public record ExerciseDTO(
    String exerciseTitle,
    String exerciseDescription,
    boolean exerciseIsDraft,
    boolean exerciseIsCompleted,
    LocalDate exerciseCreatedDate,
    String exerciseSlug,
    String exerciseOwner,
    String topicSlug) {

  public ExerciseDTO(Exercise exercise) {
    this(
        exercise.getExerciseTitle(),
        exercise.getExerciseDescription(),
        exercise.IsDraft(),
        exercise.IsCompleted(),
        exercise.getExerciseCreatedDate(),
        exercise.getExerciseSlug(),
        exercise.getExerciseOwner().getName(),
        exercise.getExerciseTopic().getTopicSlug());
  }
}
