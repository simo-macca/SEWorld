package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.Student;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Student} entities.
 *
 * <p>This interface extends {@link JpaRepository} to provide CRUD operations for {@link Student}
 * entities, and declares custom query methods to retrieve students based on various attributes.
 *
 * @see JpaRepository
 * @see Student
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

  /**
   * Finds students whose names contain the specified substring.
   *
   * @param name the substring to search for within student names
   * @return a {@link List} of {@link Student} entities whose names include the specified substring
   */
  List<Student> findByNameContaining(String name);

  /**
   * Finds a student by its primary key identifier.
   *
   * @param id the primary key identifier of the student
   * @return an {@link Optional} containing the {@link Student} if found, or an empty {@link
   *     Optional} if not found
   */
  Optional<Student> findById(Long id);

  /**
   * Finds a student by its unique decentralized identifier (DID).
   *
   * @param did the decentralized identifier (UUID) of the student
   * @return an {@link Optional} containing the {@link Student} if found, or an empty {@link
   *     Optional} if not found
   */
  Optional<Student> findByDid(UUID did);

  /**
   * Finds a student by its subscription identifier.
   *
   * @param subId the subscription identifier of the student
   * @return an {@link Optional} containing the {@link Student} if found, or an empty {@link
   *     Optional} if not found
   */
  Optional<Student> findBySubId(String subId);
}
