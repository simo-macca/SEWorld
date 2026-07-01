package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.Variant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Variant} entities.
 *
 * <p>Extends {@link JpaRepository} to provide CRUD operations and custom query methods for
 * accessing and manipulating {@link Variant} data.
 */
@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {

  /**
   * Retrieves a {@link Variant} entity based on its unique identifier.
   *
   * @param variantDid the UUID of the variant
   * @return an {@link Optional} containing the found {@link Variant}, or empty if not found
   */
  Optional<Variant> findByVariantDid(UUID variantDid);

  /**
   * Retrieves a {@link Variant} entity associated with a specific {@link Exercise} and index.
   *
   * @param exercise the {@link Exercise} entity
   * @param idx the index of the variant within the exercise
   * @return an {@link Optional} containing the found {@link Variant}, or empty if not found
   */
  Optional<Variant> findByExerciseAndIdx(Exercise exercise, int idx);

  /**
   * Retrieves all {@link Variant} entities associated with a specific {@link Exercise}, ordered by
   * their index in ascending order.
   *
   * @param exercise the {@link Exercise} entity
   * @return a list of {@link Variant} entities ordered by index
   */
  List<Variant> findByExerciseOrderByIdxAsc(Exercise exercise);

  /**
   * Retrieves the {@link Variant} entity with the highest index value.
   *
   * @param exercise the {@link Exercise} whose variants will be searched
   * @return an {@link Optional} containing the {@link Variant} with the highest index, or empty if
   *     none exist
   */
  Optional<Variant> findTopByExerciseOrderByIdxDesc(Exercise exercise);

  /**
   * Retrieves all {@link Variant} entities associated with a specific {@link Exercise}, ordered by
   * their index in ascending order.
   *
   * @param exercise the {@link Exercise} entity
   * @return a list of {@link Variant} entities ordered by index
   */
  List<Variant> findVariantsByExerciseOrderByIdxAsc(Exercise exercise);
}
