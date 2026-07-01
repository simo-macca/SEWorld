package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Attempt;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.Feedback;
import ch.usi.inf.bsc.sa4.lab02spring.model.Topic;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD and custom queries on {@link Feedback} entities.
 *
 * <p>Extends {@link JpaRepository} to provide basic persistence operations, and defines custom
 * query methods for aggregating and filtering feedback data according to application needs.
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

  /**
   * Finds the feedback record associated with the specified {@link Attempt}.
   *
   * @param attempt the {@link Attempt} whose feedback is to be retrieved
   * @return an {@link Optional} containing the matching {@link Feedback}, or empty if none found
   */
  Optional<Feedback> findByAttempt(Attempt attempt);

  /**
   * Retrieves all feedback entries submitted by the specified {@link AbstractUser}.
   *
   * @param user the {@link AbstractUser} whose feedback entries are to be fetched
   * @return a {@link List} of {@link Feedback} for the given user
   */
  List<Feedback> findAllByAttempt_User(AbstractUser user);

  /**
   * Calculates the average of each user’s highest feedback percentage for all exercises under the
   * topic identified by {@code topicId}.
   *
   * @param topicId the database identifier of the topic
   * @return the average of the maximum percentages per user, or {@code null} if no feedback exists
   */
  @Query(
      value =
          """
    SELECT AVG(max_percent)
    FROM (
        SELECT MAX(f.percentage) AS max_percent
        FROM feedback f
        JOIN attempt a ON f.attempt_id = a.attempt_id
        JOIN exercise e ON a.exercise_id = e.exercise_id
        WHERE e.topic_id = :topicId
        GROUP BY a.user_id
    ) AS sub
""",
      nativeQuery = true)
  Double generalAvgPercentageByTopicId(@Param("topicId") Long topicId);

  /**
   * Retrieves the best feedback record for each exercise in a given topic, sorted by descending
   * percentage.
   *
   * @param user the {@link AbstractUser} whose top feedback is sought
   * @param topic the {@link Topic} under which exercises are evaluated
   * @return a {@link List} of the best {@link Feedback} per exercise for the user
   */
  List<Feedback> findAllByAttempt_UserAndAttempt_Exercise_TopicOrderByPercentageDesc(
      AbstractUser user, Topic topic);

  /**
   * Computes the average of each user’s highest feedback percentages across all exercises attempted
   * by that user.
   *
   * @param user the {@link AbstractUser} whose average percentage is calculated
   * @return the average percentage of the user’s top feedback per exercise, never {@code null}
   */
  @Query(
      "SELECT COALESCE(AVG(f.percentage), 0.0) "
          + "FROM Feedback f "
          + "JOIN f.attempt a "
          + "WHERE a.user = :user "
          + "AND f.percentage = ("
          + "SELECT MAX(f2.percentage) "
          + "FROM Feedback f2 "
          + "JOIN f2.attempt a2 "
          + "WHERE a2.user = :user "
          + "AND a2.exercise = a.exercise"
          + ")")
  Double getAverageMaxFeedbackPercentage(@Param("user") AbstractUser user);

  /**
   * Retrieves the top feedback for a user and exercise, optionally filtering draft exercises,
   * ordered by descending percentage.
   *
   * @param user the {@link AbstractUser} whose feedback is queried
   * @param exercise the {@link Exercise} to filter by
   * @param isExerciseDraft {@code true} to include only draft exercises; {@code false} otherwise
   * @return an {@link Optional} containing the highest-scoring {@link Feedback}, or empty if none
   *     exists
   */
  Optional<Feedback>
      findTopByAttempt_UserAndAttempt_ExerciseAndAttempt_Exercise_ExerciseIsDraftOrderByPercentageDesc(
          AbstractUser user, Exercise exercise, boolean isExerciseDraft);

  /**
   * Retrieves the highest feedback per user for all exercises within a given topic.
   *
   * @param user the {@link AbstractUser} whose feedback is queried
   * @param topic the {@link Topic} to filter by
   * @return an {@link Optional} containing the highest-scoring {@link Feedback}, or empty if none
   *     exists
   */
  Optional<Feedback> findTopByAttempt_UserAndAttempt_Exercise_TopicOrderByPercentageDesc(
      AbstractUser user, Topic topic);

  /**
   * Retrieves the best feedback entry for each user on a specified exercise DID.
   *
   * @param exerciseDid the DID of the exercise
   * @return a {@link List} of {@link Feedback}, one per user, each with the highest percentage
   */
  @Query(
      """
      SELECT f
      FROM Feedback f
      JOIN f.attempt a
      WHERE a.exercise.exerciseDid = :exerciseDid
        AND f.percentage = (
          SELECT MAX(f2.percentage)
          FROM Feedback f2
          JOIN f2.attempt a2
          WHERE a2.user = a.user
            AND a2.exercise.exerciseDid = :exerciseDid
        )
    """)
  List<Feedback> findBestFeedbackPerUserByExerciseDid(@Param("exerciseDid") UUID exerciseDid);
}
