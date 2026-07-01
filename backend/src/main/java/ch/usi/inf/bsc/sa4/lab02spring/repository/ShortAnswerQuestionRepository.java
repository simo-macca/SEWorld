package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.ShortAnswerQuestion;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link ShortAnswerQuestion} entities.
 *
 * <p>This interface extends {@link JpaRepository} to provide CRUD operations for {@link
 * ShortAnswerQuestion} entities and includes a custom query method to retrieve a short answer
 * question based on its unique decentralized identifier (DID).
 *
 * @see JpaRepository
 * @see ShortAnswerQuestion
 */
@Repository
public interface ShortAnswerQuestionRepository extends JpaRepository<ShortAnswerQuestion, Long> {

  /**
   * Retrieves a {@link ShortAnswerQuestion} entity based on its decentralized identifier (DID).
   *
   * <p>This custom query method selects the short answer question where the {@code questionDid}
   * matches the provided {@code did}.
   *
   * @param did the unique decentralized identifier (UUID) of the short answer question
   * @return the {@link ShortAnswerQuestion} entity that matches the given identifier
   */
  @Query("SELECT q FROM ShortAnswerQuestion q WHERE q.questionDid = :did")
  ShortAnswerQuestion findShortAnswerQuestionByQuestion_DID(UUID did);

  /**
   * Retrieves all short answer questions for a given exercise
   *
   * @param exercise the exercise
   * @return a list of questions
   */
  List<ShortAnswerQuestion> findByExerciseId(Exercise exercise);

  /**
   * Retrieves all short answer questions for a given exercise ID along with their answers
   *
   * <p>and associated AI responses, to minimize database queries.
   *
   * @param exerciseId the ID of the exercise
   * @return a list of questions with answers and AI responses eagerly loaded
   */
  @Query(
      """
  SELECT DISTINCT q FROM ShortAnswerQuestion q
  LEFT JOIN q.answers a
  LEFT JOIN a.exerciseAIResponse
  WHERE q.exerciseId = :exerciseId
  """)
  List<ShortAnswerQuestion> findAllWithAnswersAndResponsesByExerciseId(Exercise exerciseId);
}
