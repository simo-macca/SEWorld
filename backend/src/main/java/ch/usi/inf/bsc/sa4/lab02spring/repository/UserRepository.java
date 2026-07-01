package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link AbstractUser} entities.
 *
 * <p>This interface extends {@link JpaRepository} to provide standard CRUD operations for {@link
 * AbstractUser} entities. It also defines custom query methods to retrieve users based on their
 * name, primary key identifier, decentralized identifier (DID), or subscription identifier.
 *
 * @see JpaRepository
 * @see AbstractUser
 */
@Repository
public interface UserRepository extends JpaRepository<AbstractUser, Long> {

  /**
   * Finds users whose names contain the specified substring.
   *
   * @param name the substring to search for within usernames
   * @return a {@link List} of {@link AbstractUser} entities whose names include the specified
   *     substring
   */
  List<AbstractUser> findByNameContainingIgnoreCase(String name);

  /**
   * Retrieves a user by their primary key identifier.
   *
   * @param id the primary key identifier of the user
   * @return an {@link Optional} containing the {@link AbstractUser} if found, or an empty {@link
   *     Optional} if not found
   */
  Optional<AbstractUser> findById(Long id);

  /**
   * Retrieves a user by their unique decentralized identifier (DID).
   *
   * @param did the unique decentralized identifier (UUID) of the user
   * @return an {@link Optional} containing the {@link AbstractUser} if found, or an empty {@link
   *     Optional} if not found
   */
  Optional<AbstractUser> findByDid(UUID did);

  /**
   * Retrieves a user by their subscription identifier.
   *
   * @param subId the subscription identifier of the user
   * @return an {@link Optional} containing the {@link AbstractUser} if found, or an empty {@link
   *     Optional} if not found
   */
  Optional<AbstractUser> findBySubId(String subId);
}
