package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Attempt;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Attempt} entities.
 *
 * <p>This interface extends {@link JpaRepository} to provide standard CRUD operations along with
 * custom query methods specific to {@link Attempt} entities.
 *
 * <p>The custom query methods include:
 *
 * <ul>
 *   <li>{@link #findByUser(AbstractUser)} - Retrieves all {@link Attempt} entities associated with
 *       a given {@link AbstractUser}.
 *   <li>{@link #findByExercise(Exercise)} - Retrieves all {@link Attempt} entities associated with
 *       a given {@link Exercise}.
 *   <li>{@link #findByUserAndExercise(AbstractUser, Exercise)} - Retrieves all {@link Attempt}
 *       entities for a specific combination of {@link AbstractUser} and {@link Exercise}.
 *   <li>{@link #findByAttemptDid(UUID)} - Retrieves an {@link Attempt} by its decentralized
 *       identifier (DID).
 * </ul>
 *
 * @see JpaRepository
 * @see Attempt
 */
@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {

  /**
   * Finds all {@link Attempt} entities associated with the specified {@link AbstractUser}.
   *
   * @param user the {@link AbstractUser} whose attempts are to be retrieved.
   * @return a list of {@link Attempt} entities associated with the given user.
   */
  List<Attempt> findByUser(AbstractUser user);

  /**
   * Finds all {@link Attempt} entities associated with the specified {@link Exercise}.
   *
   * @param exercise the {@link Exercise} whose attempts are to be retrieved.
   * @return a list of {@link Attempt} entities associated with the given exercise.
   */
  List<Attempt> findByExercise(Exercise exercise);

  /**
   * Finds all {@link Attempt} entities for a specific combination of {@link AbstractUser} and
   * {@link Exercise}.
   *
   * @param user the {@link AbstractUser} whose attempts are to be retrieved.
   * @param exercise the {@link Exercise} for which the attempts are to be retrieved.
   * @return a list of {@link Attempt} entities matching the specified user and exercise.
   */
  List<Attempt> findByUserAndExercise(AbstractUser user, Exercise exercise);

  /**
   * Finds an {@link Attempt} entity by its decentralized identifier (DID).
   *
   * @param attemptDid the decentralized identifier of the attempt to be retrieved
   * @return an {@link Optional} containing the found {@link Attempt} if it exists, or an empty
   *     {@link Optional} if no attempt with the given DID is found
   */
  Optional<Attempt> findByAttemptDid(UUID attemptDid);
}
