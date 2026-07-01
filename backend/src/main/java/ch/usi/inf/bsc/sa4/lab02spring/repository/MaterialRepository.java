package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.Material;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository interface for Material entity. Extends JpaRepository to provide CRUD operations. */
@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

  /**
   * Finds a material by its unique UUID.
   *
   * @param materialDID The UUID of the material.
   * @return An Optional containing the Material if found.
   */
  Optional<Material> findByMaterialDid(UUID materialDID);

  /**
   * Deletes a material by its unique UUID.
   *
   * @param materialDID The UUID of the material.
   */
  void deleteByMaterialDid(UUID materialDID);

  /**
   * Retrieves a list of {@link Material} entities that are associated with the specified topic,
   * identified by its unique decentralized identifier (DID).
   *
   * @param topicDid the unique decentralized identifier (UUID) of the topic
   * @return a list of {@link Material} entities corresponding to the given topic
   */
  List<Material> findByTopic_TopicDid(UUID topicDid);
}
