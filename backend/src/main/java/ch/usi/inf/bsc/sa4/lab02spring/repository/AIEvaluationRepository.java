package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.AIEvaluation;
import ch.usi.inf.bsc.sa4.lab02spring.model.Answer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and managing {@link AIEvaluation} entities.
 *
 * <p>Extends {@code JpaRepository} to provide standard CRUD operations and defines custom query
 * methods for retrieving AI evaluations based on specific criteria.
 */
@Repository
public interface AIEvaluationRepository extends JpaRepository<AIEvaluation, Long> {

  /**
   * Retrieves an {@code AIEvaluation} associated with the specified {@code Answer}.
   *
   * @param answer the {@code Answer} entity for which the AI evaluation is to be retrieved
   * @return an {@code Optional} containing the found {@code AIEvaluation}, or empty if none found
   */
  Optional<AIEvaluation> findByAnswer(Answer answer);

  /**
   * Retrieves an {@code AIEvaluation} by its unique identifier.
   *
   * @param did the UUID of the AI evaluation to retrieve
   * @return an {@code Optional} containing the found {@code AIEvaluation}, or empty if none found
   */
  Optional<AIEvaluation> findByAiEvaluationDid(UUID did);
}
