package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.Topic;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Topic} entities.
 *
 * <p>Provides methods for querying the database to retrieve topics based on specific criteria.
 */
@Repository
public interface TopicRepository
    extends JpaRepository<Topic, Long>, JpaSpecificationExecutor<Topic> {

  /**
   * Retrieves a {@link Topic} by its numeric ID.
   *
   * @param topicId the numeric ID of the topic
   * @return an {@link Optional} containing the matching {@link Topic}, or empty if not found
   * @spec.effects No effects
   */
  Optional<Topic> findByTopicId(Long topicId);

  /**
   * Retrieves a {@link Topic} by its UUID.
   *
   * @param topicDid the UUID of the topic
   * @return an {@link Optional} containing the matching {@link Topic}, or empty if not found
   * @spec.effects No effects
   */
  Optional<Topic> findByTopicDid(UUID topicDid);

  /**
   * Retrieves all {@link Topic} entities that contain at least one matching keyword in both the
   * title and the description. The search is case-insensitive.
   *
   * @param titleKeywords a list of keywords to match against topic titles; if null or empty,
   *     matches any title
   * @param descriptionKeywords a list of keywords to match against topic descriptions; if null or
   *     empty, matches any description
   * @return a list of {@link Topic} entities matching the specified criteria
   * @spec.requires {@code titleKeywords} and {@code descriptionKeywords} are lists of keywords;
   *     null or empty lists match any title or description respectively
   * @spec.effects No effects
   */
  default List<Topic> findByTitleAndDescriptionKeywords(
      List<String> titleKeywords, List<String> descriptionKeywords) {
    return findAll(
        TopicRepositorySpecifications.withKeywordsInTitleOrDescription(
            titleKeywords, descriptionKeywords));
  }
}
