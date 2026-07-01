package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CRUD operations and lookups on AI-generated material responses.
 *
 * <p>Extends {@link JpaRepository} to provide basic persistence methods, and adds derived-query
 * methods for filtering by material and response identifier.
 */
@Repository
public interface AIExerciseResponseRepository extends JpaRepository<AIExerciseResponse, Long> {

  /**
   * Retrieves a list of {@link AIExerciseResponse} entities that are publicly visible, associated
   * with the specified {@link AbstractQuestion}, and have a user answer matching the provided
   * string.
   *
   * @param question the {@link AbstractQuestion} to filter the responses by
   * @param userAnswer the user answer to match against
   * @return a {@link List} of {@link AIExerciseResponse} entities that match the criteria
   */
  List<AIExerciseResponse> findByIsPublicTrueAndQuestionAndUserAnswer(
      AbstractQuestion question, String userAnswer);

  /**
   * Retrieves an {@link Optional} containing the {@link AIExerciseResponse} entity associated with
   * the specified {@link Answer}, if it exists.
   *
   * @param answer the {@link Answer} to find the associated response for
   * @return an {@link Optional} containing the {@link AIExerciseResponse} if found, or {@link
   *     Optional#empty()} if no matching response exists
   */
  Optional<AIExerciseResponse> findByAnswer(Answer answer);
}
