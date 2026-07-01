package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractAIResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.Student;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link AbstractAIResponse} entities.
 *
 * <p>Extends {@link JpaRepository} to provide basic CRUD operations and includes custom methods for
 * updating AI responses and retrieving public responses.
 */
@Repository
public interface AIResponseRepository extends JpaRepository<AbstractAIResponse, Long> {

  /**
   * Updates the AI-generated answer for a specific response identified by its UUID.
   *
   * <p>This method uses a custom JPQL update query and is annotated with {@code @Modifying} and
   * {@code @Transactional} to indicate that it performs a modifying operation within a transaction.
   *
   * @param response the new AI-generated answer to set
   * @param id the UUID of the response to update
   * @return the number of entities updated: 1 if successful, 0 if no matching response exists, or
   *     greater than 1 if multiple responses matched (which should not occur)
   */
  @Modifying
  @Transactional
  @Query("UPDATE AbstractAIResponse ar SET ar.aiAnswer= :response WHERE ar.aiResponseId = :id")
  int update(@Param("response") String response, @Param("id") UUID id);

  /**
   * Retrieves all AI responses that are marked as public.
   *
   * @return a list of {@link AbstractAIResponse} instances where {@code isPublic} is {@code true}
   */
  List<AbstractAIResponse> findAbstractAIResponsesByIsPublicTrue();

  /**
   * Retrieves all AI responses of the student that are marked as private
   *
   * @param owner represented by the student
   * @return the unpublished AI answers owned by the given student DID
   */
  List<AbstractAIResponse> findAbstractAIResponseByIsPublicFalseAndOwner(Student owner);

  /**
   * Retrieves an AI response by its DID
   *
   * @param aiResponseDID the UUID of the AI response to find
   * @return an {@link Optional} containing the {@link AbstractAIResponse} if found, or an empty
   *     {@link Optional} if not found
   */
  Optional<AbstractAIResponse> findByAiResponseDID(UUID aiResponseDID);
}
