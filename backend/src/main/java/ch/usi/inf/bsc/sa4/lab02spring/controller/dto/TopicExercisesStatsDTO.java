package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) representing statistical information about exercises within a specific
 * topic.
 *
 * <p>This record encapsulates a list of exercise statistics, providing a comprehensive overview of
 * the exercises associated with a particular topic. The list can contain various types of {@link
 * ExercisesStatsDTO} or its subclasses, allowing for flexibility in the type of statistical data
 * represented.
 *
 * @param stats a list containing statistical data of exercises related to a topic
 */
public record TopicExercisesStatsDTO(List<? extends ExercisesStatsDTO> stats) {

  /**
   * Constructs a new {@code TopicExercisesStatsDTO} by performing a defensive (deep) copy of the
   * provided list.
   *
   * <p>This ensures that the internal state cannot be modified externally after construction.
   *
   * @param stats a list of {@link ExercisesStatsDTO} (or its subclasses) to be safely encapsulated
   */
  public TopicExercisesStatsDTO(List<? extends ExercisesStatsDTO> stats) {
    this.stats = List.copyOf(stats);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof TopicExercisesStatsDTO statsDTO)) {
      return false;
    }
    return Objects.equals(stats, statsDTO.stats);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(stats);
  }

  @Override
  public String toString() {
    return "TopicExercisesStatsDTO{" + "stats=" + stats + '}';
  }
}
