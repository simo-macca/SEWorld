package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.Instructor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Instructor} entities.
 *
 * <p>This interface extends {@link JpaRepository} to provide standard CRUD operations for {@link
 * Instructor} entities, and declares custom query methods for retrieving instructors based on
 * various attributes.
 *
 * @see JpaRepository
 * @see Instructor
 */
@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {

  /**
   * Finds instructors whose names contain the given substring.
   *
   * @param name the substring to search for within instructor names
   * @return a {@link List} of {@link Instructor} entities whose names contain the specified
   *     substring
   */
  List<Instructor> findByNameContaining(String name);

  /**
   * Finds an instructor by its primary key identifier.
   *
   * @param id the primary key identifier of the instructor
   * @return an {@link Optional} containing the {@link Instructor} if found, or an empty {@link
   *     Optional} if not found
   */
  Optional<Instructor> findById(Long id);

  /**
   * Finds an instructor by its unique decentralized identifier (DID).
   *
   * @param did the decentralized identifier (UUID) of the instructor
   * @return an {@link Optional} containing the {@link Instructor} if found, or an empty {@link
   *     Optional} if not found
   */
  Optional<Instructor> findByDid(UUID did);

  /**
   * Finds an instructor by its subscription identifier.
   *
   * @param subId the subscription identifier of the instructor
   * @return an {@link Optional} containing the {@link Instructor} if found, or an empty {@link
   *     Optional} if not found
   */
  Optional<Instructor> findBySubId(String subId);
}
