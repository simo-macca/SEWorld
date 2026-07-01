package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractQuestion;
import ch.usi.inf.bsc.sa4.lab02spring.model.Variant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link AbstractQuestion} entities.
 *
 * <p>This interface extends {@link JpaRepository} to provide standard CRUD operations for {@link
 * AbstractQuestion} entities, as well as custom query methods to retrieve questions based on the
 * exercise they belong to or by their unique decentralized identifier.
 *
 * @see JpaRepository
 * @see AbstractQuestion
 */
@Repository
public interface QuestionRepository extends JpaRepository<AbstractQuestion, Long> {

  /**
   * Retrieves a list of {@link AbstractQuestion} entities associated with a specific exercise.
   *
   * <p>This query selects all questions where the exercise's decentralized identifier matches the
   * given {@code exerciseId}.
   *
   * @param exerciseId the decentralized identifier (UUID) of the exercise
   * @return a {@link List} of {@link AbstractQuestion} entities associated with the specified
   *     exercise
   */
  @Query("SELECT q FROM AbstractQuestion q WHERE q.exerciseId.exerciseDid = :exerciseId")
  List<AbstractQuestion> findByExercise_ID(@Param("exerciseId") UUID exerciseId);

  /**
   * Retrieves a {@link AbstractQuestion} entity by its unique decentralized identifier.
   *
   * <p>This query selects the question that matches the provided decentralized identifier.
   *
   * @param questionDid the decentralized identifier (UUID) of the question to retrieve
   * @return the {@link AbstractQuestion} entity matching the given identifier
   */
  Optional<AbstractQuestion> findByQuestionDid(UUID questionDid);

  /**
   * Retrieves all {@link AbstractQuestion} entities associated with the specified {@link Variant}.
   *
   * <p>This method leverages Spring Data JPA's query derivation mechanism to fetch all questions
   * linked to the given variant.
   *
   * @param variant the {@link Variant} entity used as the filter criterion
   * @return a list of {@link AbstractQuestion} entities associated with the specified variant
   */
  List<AbstractQuestion> findByVariant(Variant variant);

  /**
   * Counts the number of {@link AbstractQuestion} entities associated with the specified {@link
   * Variant}.
   *
   * <p>This method uses Spring Data JPA's query derivation to return the count of questions linked
   * to the given variant.
   *
   * @param variant the {@link Variant} entity used as the filter criterion
   * @return the number of {@link AbstractQuestion} entities associated with the specified variant
   */
  long countAbstractQuestionByVariant(Variant variant);
}
