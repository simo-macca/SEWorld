package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.AIVoteStudent;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractAIResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.Student;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for managing student votes on AI-generated responses.
 *
 * <p>Extends {@link JpaRepository} to provide standard CRUD operations and custom query methods for
 * accessing and modifying vote data associated with students and AI responses.
 */
public interface AIStudentVoteRepository extends JpaRepository<AIVoteStudent, Long> {

  /**
   * Retrieves all votes cast by a specific student.
   *
   * @param studentId the student whose votes are to be retrieved
   * @return a list of {@link AIVoteStudent} entities associated with the given student
   */
  List<AIVoteStudent> findAllByStudentId(Student studentId);

  /**
   * Retrieves all votes associated with a specific AI response.
   *
   * @param responseId the AI response whose votes are to be retrieved
   * @return a list of {@link AIVoteStudent} entities associated with the given AI response
   */
  List<AIVoteStudent> findAllByAiResponseId(AbstractAIResponse responseId);

  /**
   * Retrieves all votes cast by a specific student for a specific AI response.
   *
   * @param aiResponseId the AI response
   * @param studentId the student
   * @return a list of {@link AIVoteStudent} entities matching the specified AI response and student
   */
  List<AIVoteStudent> findAllByAiResponseIdAndStudentId(
      AbstractAIResponse aiResponseId, Student studentId);

  /**
   * Updates the vote value for a specific student and AI response combination.
   *
   * @param vote the new vote value
   * @param studentId the UUID of the student
   * @param responseId the UUID of the AI response
   * @return the number of entities updated (should be 0 or 1)
   */
  @Modifying
  @Transactional
  @Query(
      "UPDATE AIVoteStudent sva "
          + "SET sva.vote = :vote "
          + "WHERE sva.aiResponseId = :responseId "
          + "AND sva.studentId =:studentId")
  int update(
      @Param("vote") Integer vote,
      @Param("studentId") UUID studentId,
      @Param("responseId") UUID responseId);
}
