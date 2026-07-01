package ch.usi.inf.bsc.sa4.lab02spring.model;

import static jakarta.persistence.FetchType.LAZY;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AiAbstractResponseDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA entity representing an AI-generated response associated with an exercise answer.
 *
 * <p>Extends {@code AbstractAIResponse} to include the user’s submitted answer and a link to the
 * corresponding persisted {@link Answer} entity.
 */
@Entity
@Table(name = "exercise_ai_response")
public class ExerciseAIResponse extends AbstractAIResponse {

  /** The persisted Answer entity to which this AI response refers. */
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "answer_id", nullable = false)
  private Answer answer;

  /** The answer text provided by the user. */
  @Column(name = "user_answer", nullable = false)
  private String userAnswer;

  /**
   * Returns the persisted Answer associated with this AI response.
   *
   * @return the {@link Answer} entity linked to this response
   */
  public Answer getAnswer() {
    return answer;
  }

  /**
   * Returns the user’s submitted answer text.
   *
   * @return the answer text as a {@link String}
   */
  public String getUserAnswer() {
    return userAnswer;
  }

  /** Default constructor. */
  public ExerciseAIResponse() {
    super();
  }

  /**
   * Converts this entity into a transport DTO, including the provided user rating.
   *
   * <p>Implementations should construct and return an appropriate subclass of {@link
   * AiAbstractResponseDTO} containing all relevant response data.
   *
   * @param userRating the rating given by the user, or {@code null} if not rated
   * @return an {@link AiAbstractResponseDTO} representing this AI response
   */
  public AiAbstractResponseDTO toDTO(Integer userRating) {
    // TODO: implement
    return null;
  }
}
