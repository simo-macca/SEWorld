package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.Comment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Comment} entities.
 *
 * <p>Provides methods for accessing comments based on their unique identifier or associated AI
 * response.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  /**
   * Finds a comment by its domain-specific unique identifier (DID).
   *
   * @param commentDid the UUID of the comment
   * @return an {@link Optional} containing the {@link Comment} if found, or empty if not found
   */
  Optional<Comment> findByCommentDid(UUID commentDid);

  /**
   * Retrieves all comments associated with a specific AI response.
   *
   * @param aiResponseDid the UUID of the AI response
   * @return a list of {@link Comment} entities linked to the given AI response
   */
  List<Comment> findByAiResponse_AiResponseDID(UUID aiResponseDid);
}
