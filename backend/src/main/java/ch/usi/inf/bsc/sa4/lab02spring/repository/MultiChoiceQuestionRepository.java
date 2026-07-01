package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.MultiChoiceQuestion;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link MultiChoiceQuestion} entities.
 *
 * <p>This interface extends {@link JpaRepository} to provide standard CRUD operations for {@link
 * MultiChoiceQuestion} entities and includes custom query methods to retrieve questions based on
 * their unique identifiers, titles, or associated exercises.
 *
 * @see JpaRepository
 * @see MultiChoiceQuestion
 */
@Repository
public interface MultiChoiceQuestionRepository extends JpaRepository<MultiChoiceQuestion, Long> {

  /**
   * Retrieves a {@link MultiChoiceQuestion} entity based on its unique decentralized identifier.
   *
   * <p>This query selects a multi-choice question where the question's decentralized identifier
   * matches the provided {@code did}.
   *
   * @param did the unique decentralized identifier (UUID) of the question
   * @return the matching {@link MultiChoiceQuestion} entity, or {@code null} if not found
   */
  @Query("SELECT q FROM MultiChoiceQuestion q WHERE q.questionDid = :did")
  MultiChoiceQuestion findByQuestion_DID(UUID did);

  /**
   * Retrieves a {@link MultiChoiceQuestion} entity based on its title.
   *
   * <p>This query selects a multi-choice question where the question's title matches the provided
   * {@code title}.
   *
   * @param title the title of the question to search for
   * @return the matching {@link MultiChoiceQuestion} entity, or {@code null} if not found
   */
  @Query("SELECT q FROM MultiChoiceQuestion q WHERE q.questionTitle = :title")
  MultiChoiceQuestion findByQuestion_title(String title);

  /**
   * Retrieves all {@link MultiChoiceQuestion} entities associated with a specific exercise.
   *
   * <p>This query selects all multi-choice questions that are part of the specified {@link
   * Exercise}. The association is based on the exercise identifier.
   *
   * @param exercise the {@link Exercise} entity for which to retrieve the associated questions
   * @return a {@link List} of {@link MultiChoiceQuestion} entities linked to the given exercise
   */
  @Query("SELECT q FROM MultiChoiceQuestion q WHERE q.exerciseId.exerciseId = :exerciseId")
  List<MultiChoiceQuestion> findByExercise_ID(Exercise exercise);
}
