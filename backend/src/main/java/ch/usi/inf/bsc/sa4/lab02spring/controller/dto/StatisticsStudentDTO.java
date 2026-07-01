package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing statistical information for a student.
 *
 * <p>This record encapsulates the minimum, average, and maximum grades achieved by the student,
 * along with a list of statistics for each topic the student has engaged with.
 *
 * @param minGrade the minimum grade achieved by the student
 * @param avgGrade the average grade achieved by the student
 * @param maxGrade the maximum grade achieved by the student
 * @param topics the list of topic statistics related to the student
 */
public record StatisticsStudentDTO(
    Double minGrade, Double avgGrade, Double maxGrade, List<TopicStatisticDTO> topics) {
  /**
   * Constructs a new {@code StatisticsStudentDTO} with a defensive copy of the topics list to
   * prevent exposure of internal mutable state.
   *
   * @param minGrade the minimum grade achieved by the student
   * @param avgGrade the average grade achieved by the student
   * @param maxGrade the maximum grade achieved by the student
   * @param topics the list of topic statistics related to the student
   */
  public StatisticsStudentDTO {
    if (topics == null) {
      topics = List.of();
    } else {
      topics = List.copyOf(topics);
    }
  }
}
