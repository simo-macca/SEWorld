package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractQuestion;
import ch.usi.inf.bsc.sa4.lab02spring.model.Answer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Answer} entities.
 *
 * <p>This interface extends {@link JpaRepository} to provide standard CRUD operations and defines
 * custom query methods for retrieving {@link Answer} entities based on their associated attempt and
 * question identifiers.
 *
 * @see JpaRepository
 * @see Answer
 */
@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

  /**
   * Retrieves an {@link Answer} entity by its unique decentralized identifier.
   *
   * @param answerDid the unique identifier (UUID) of the answer to retrieve
   * @return an {@link Optional} containing the found {@link Answer}, or an empty {@link Optional}
   *     if no matching answer is found
   */
  Optional<Answer> findByAnswerDid(UUID answerDid);

  /**
   * Retrieves an {@link Answer} based on the decentralized identifiers of the attempt and question.
   *
   * <p>This method uses a JPQL query to locate the answer that belongs to a specific attempt and
   * corresponds to a particular question.
   *
   * @param attemptDid the decentralized identifier (UUID) of the attempt
   * @param questionDid the decentralized identifier (UUID) of the question
   * @return an {@link Optional} containing the {@link Answer} if found, or an empty {@link
   *     Optional} if no matching answer exists
   */
  @Query(
      "SELECT a FROM Answer a "
          + "WHERE a.attempt.attemptDid = :attemptDid "
          + "AND a.question.questionDid = :questionDid")
  Optional<Answer> findByAttemptDidAndQuestionDid(
      @Param("attemptDid") UUID attemptDid, @Param("questionDid") UUID questionDid);

  /**
   * Retrieves all {@link Answer} entities associated with the specified attempt.
   *
   * <p>This method executes a JPQL query to select all answers that belong to an attempt identified
   * by the provided decentralized identifier.
   *
   * @param attemptDid the decentralized identifier (UUID) of the attempt
   * @return a {@link List} of {@link Answer} entities associated with the specified attempt
   */
  @Query("SELECT a FROM Answer a WHERE a.attempt.attemptDid = :attemptDid")
  List<Answer> findAllByAttemptDid(@Param("attemptDid") UUID attemptDid);

  /**
   * Retrieves all {@link Answer} entities associated with the specified question.
   *
   * @param question the {@link AbstractQuestion} whose answers are to be retrieved
   * @return a {@link List} of {@link Answer} entities associated with the specified question
   */
  List<Answer> findByQuestion(AbstractQuestion question);
}
