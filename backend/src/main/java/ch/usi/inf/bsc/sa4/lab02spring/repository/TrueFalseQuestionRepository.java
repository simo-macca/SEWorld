package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.TrueFalseQuestion;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link TrueFalseQuestion} entities.
 *
 * <p>This interface extends {@link JpaRepository} to provide CRUD operations for {@link
 * TrueFalseQuestion} entities and defines a custom query method to retrieve a true/false question
 * based on its unique decentralized identifier (DID).
 *
 * @see JpaRepository
 * @see TrueFalseQuestion
 */
@Repository
public interface TrueFalseQuestionRepository extends JpaRepository<TrueFalseQuestion, Long> {

  /**
   * Retrieves a {@link TrueFalseQuestion} entity by its unique decentralized identifier (DID).
   *
   * <p>This method executes a JPQL query to fetch the true/false question where the {@code
   * questionDid} matches the provided UUID.
   *
   * @param questionDid the unique decentralized identifier (UUID) of the true/false question to
   *     retrieve
   * @return the {@link TrueFalseQuestion} entity matching the given identifier, or {@code null} if
   *     no such question exists
   */
  @Query("SELECT q FROM TrueFalseQuestion q WHERE q.questionDid = :questionDid")
  TrueFalseQuestion findTrueFalseQuestionByQuestion_DID(UUID questionDid);
}
