package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) representing statistical information for an instructor.
 *
 * <p>This record encapsulates a list of {@link TopicStatisticInstrDTO} objects, each containing
 * statistical data related to a specific topic from an instructor's perspective.
 *
 * @param topics the list of topic statistics for the instructor
 */
public record StatisticsInstructorDTO(List<TopicStatisticInstrDTO> topics) {
  /**
   * Constructs a new {@code StatisticsInstructorDTO} with a defensive copy of the provided list.
   *
   * <p>This constructor ensures that the internal list of topic statistics is immutable, preventing
   * external modifications after instantiation.
   *
   * @param topics the list of topic statistics to associate with this DTO
   * @throws NullPointerException if {@code topics} is {@code null}
   */
  public StatisticsInstructorDTO(List<TopicStatisticInstrDTO> topics) {
    this.topics = List.copyOf(topics);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof StatisticsInstructorDTO that)) {
      return false;
    }
    return Objects.equals(topics, that.topics);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(topics);
  }

  @Override
  public String toString() {
    return "StatisticsInstructorDTO{" + "topics=" + topics + '}';
  }
}
