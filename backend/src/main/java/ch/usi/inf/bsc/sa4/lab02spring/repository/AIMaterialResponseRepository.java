package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.AIMaterialResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractAIResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.Material;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CRUD operations and lookups on AI-generated material responses.
 *
 * <p>Extends {@link JpaRepository} to provide basic persistence methods, and adds derived-query
 * methods for filtering by material and response identifier.
 */
@Repository
public interface AIMaterialResponseRepository extends JpaRepository<AIMaterialResponse, Long> {

  /**
   * Finds all public AI responses associated with the given material.
   *
   * @param material the {@link Material} for which to retrieve public responses
   * @return a {@link List} of all {@link AbstractAIResponse} instances that are public and linked
   *     to the specified material
   */
  List<AbstractAIResponse> findByIsPublicTrueAndMaterial(Material material);

  /**
   * Retrieves a specific AI material response by its globally unique identifier.
   *
   * @param aiResponseDID the UUID of the {@link AIMaterialResponse}
   * @return an {@link Optional} containing the matching {@link AIMaterialResponse}, or empty if
   *     none exists
   */
  Optional<AIMaterialResponse> findByAiResponseDID(UUID aiResponseDID);
}
