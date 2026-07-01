package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.Answer;
import ch.usi.inf.bsc.sa4.lab02spring.model.ExerciseAIResponse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing {@link ExerciseAIResponse} entities. Provides methods to
 * perform CRUD operations and custom queries related to exercise AI responses.
 */
@Repository
public interface ExerciseAIResponseRepository extends JpaRepository<ExerciseAIResponse, Long> {

  /**
   * Retrieves a list of {@link ExerciseAIResponse} entities associated with the specified {@link
   * Answer}.
   *
   * @param answer the {@link Answer} entity to find associated AI responses for
   * @return a list of {@link ExerciseAIResponse} entities related to the provided answer
   */
  List<ExerciseAIResponse> findExerciseAIResponseByAnswer(Answer answer);
}
