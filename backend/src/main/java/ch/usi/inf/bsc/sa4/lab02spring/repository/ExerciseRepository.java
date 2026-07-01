package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Attempt;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.Topic;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for managing {@link Exercise} entities.
 *
 * <p>This interface extends {@link JpaRepository} for basic CRUD operations and {@link
 * JpaSpecificationExecutor} for executing specifications.
 */
public interface ExerciseRepository
    extends JpaRepository<Exercise, Long>, JpaSpecificationExecutor<Exercise> {

  /**
   * Finds an exercise by its unique identifier (DID).
   *
   * @param exerciseDid the UUID of the exercise.
   * @return an {@code Optional} containing the found exercise, or empty if no exercise is found.
   */
  Optional<Exercise> findByExerciseDid(UUID exerciseDid);

  /**
   * Finds exercises associated with the specified topic.
   *
   * @param topic the topic entity.
   * @return an {@code Optional} containing a list of exercises for the given topic, or empty if
   *     none are found.
   */
  Optional<List<Exercise>> findByTopic(Topic topic);

  /**
   * Searches for exercises that match the provided title and description keywords, draft status,
   * and topic.
   *
   * <p>This method uses a specification defined in {@link ExerciseSpecifications} to build the
   * query.
   *
   * @param titleKeywords list of keywords to search within the exercise title.
   * @param descriptionKeywords list of keywords to search within the exercise description.
   * @param draft the draft status to match.
   * @param topic the topic associated with the exercise.
   * @return a list of exercises matching the criteria.
   */
  default List<Exercise> searchByKeywords(
      List<String> titleKeywords, List<String> descriptionKeywords, boolean draft, Topic topic) {
    return findAll(
        ExerciseSpecifications.withKeywordsInTitleOrDescription(
            titleKeywords, descriptionKeywords, draft, topic));
  }

  /**
   * Deletes all exercises associated with the specified topic.
   *
   * @param topic the topic whose exercises should be deleted.
   */
  void deleteByTopic(Topic topic);

  /**
   * Determines whether a given user has completed the specified exercise.
   *
   * <p>Executes a JPQL query that counts all {@link Attempt} records where: the exercise matches
   * the supplied {@code exercise} parameter, the attempt is marked as completed, and the attempt’s
   * user matches the supplied {@code user} parameter. If the count is greater than zero, the method
   * returns {@code true}; otherwise, {@code false}.
   *
   * @param exercise the {@link Exercise} to check for completion status
   * @param user the {@link AbstractUser} whose attempts are evaluated
   * @return {@code true} if at least one completed {@link Attempt} exists for the user and
   *     exercise; {@code false} otherwise
   */
  @Query(
      """
    SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
    FROM Attempt a
    WHERE a.exercise = :exercise
      AND a.attemptIsCompleted = true
      AND a.user = :user
  """)
  boolean isExerciseCompleted(
      @Param("exercise") Exercise exercise, @Param("user") AbstractUser user);
}

/** Utility class containing specifications for querying {@link Exercise} entities. */
class ExerciseSpecifications {

  /**
   * Creates a {@link Specification} to search for exercises containing the specified keywords in
   * their title or description, matching the given draft status and associated with the specified
   * topic.
   *
   * @param titleKeywords list of keywords to search in the exercise title.
   * @param descriptionKeywords list of keywords to search in the exercise description.
   * @param draft the draft status that the exercise must match.
   * @param topic the topic associated with the exercise.
   * @return a {@link Specification} that can be used with {@link
   *     ExerciseRepository#findAll(Specification)}
   */
  public static Specification<Exercise> withKeywordsInTitleOrDescription(
      List<String> titleKeywords, List<String> descriptionKeywords, boolean draft, Topic topic) {
    return (Root<Exercise> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
      final List<Predicate> predicates = new ArrayList<>();
      if (titleKeywords != null && !titleKeywords.isEmpty()) {
        final List<Predicate> titlePredicates = new ArrayList<>();
        for (String keyword : titleKeywords) {
          keyword = keyword.toLowerCase();
          titlePredicates.add(
              criteriaBuilder.like(
                  criteriaBuilder.lower(root.get("exerciseTitle")), "%" + keyword + "%"));
        }
        predicates.add(criteriaBuilder.or(titlePredicates.toArray(new Predicate[0])));
      }
      if (descriptionKeywords != null && !descriptionKeywords.isEmpty()) {
        final List<Predicate> descriptionPredicates = new ArrayList<>();
        for (String keyword : descriptionKeywords) {
          keyword = keyword.toLowerCase();
          descriptionPredicates.add(
              criteriaBuilder.like(
                  criteriaBuilder.lower(root.get("exerciseDescription")), "%" + keyword + "%"));
        }
        predicates.add(criteriaBuilder.or(descriptionPredicates.toArray(new Predicate[0])));
      }
      predicates.add(criteriaBuilder.equal(root.get("exerciseIsDraft"), draft));
      predicates.add(criteriaBuilder.equal(root.get("topic"), topic));
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}
