package ch.usi.inf.bsc.sa4.lab02spring.model;

import static jakarta.persistence.FetchType.EAGER;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AiAbstractResponseDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AiMaterialResponseDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Represents a response to a multiple-choice question within an exercise.
 *
 * <p>This entity extends the {@link AbstractAIResponse} class and encapsulates additional details
 * specific to multiple-choice questions including the highlighted text and the {@link Material}
 * associated with the response. It is mapped to the underlying database through JPA annotations.
 */
@Entity
public class AIMaterialResponse extends AbstractAIResponse {

  /** The highlighted text of the question. */
  @Column(name = "highlightedText")
  private String highlightedText;

  /** The {@link Material} associated with this response. */
  @ManyToOne(fetch = EAGER)
  @JoinColumn(name = "materialID")
  private Material material;

  /** The text of the question posed to the AI. */
  @Column(name = "questionText", nullable = false)
  protected String questionText;

  /** Protected no‐arg constructor required by JPA. */
  protected AIMaterialResponse() {}

  /**
   * Constructs a new {@code AIMaterialResponse} instance with the specified visibility, question,
   * answer, highlighted text, and owner.
   *
   * @param isPublic whether the response is publicly visible
   * @param questionText the text of the question
   * @param aiAnswer the AI answer
   * @param highlightedText the highlighted text of the question
   * @param material the {@link Material} associated with this response
   * @param owner the {@link Student} who created the response
   */
  public AIMaterialResponse(
      Boolean isPublic,
      String questionText,
      String aiAnswer,
      String highlightedText,
      Material material,
      Student owner) {
    super(isPublic, aiAnswer, owner);
    this.highlightedText = highlightedText;
    this.material = material;
    this.questionText = questionText;
  }

  /**
   * Returns the highlighted text of the question.
   *
   * @return the highlighted text
   */
  public String getHighlightedText() {
    return highlightedText;
  }

  /**
   * Sets the highlighted text of the question.
   *
   * @param highlightedText the new highlighted text to set
   */
  public void setHighlightedText(String highlightedText) {
    this.highlightedText = highlightedText;
  }

  /**
   * Returns the {@link Material} associated with this response.
   *
   * @return the associated {@link Material}
   */
  public Material getMaterial() {
    return material;
  }

  /**
   * Sets the {@link Material} associated with this response.
   *
   * @param material the new {@link Material} to associate with this response
   */
  public void setMaterial(Material material) {
    this.material = material;
  }

  /**
   * Returns the text of the question used from the ai to generate the response.
   *
   * @return the question text originally submitted to the AI
   */
  public String getQuestionText() {
    return questionText;
  }

  /**
   * Updates the question text for this AI response.
   *
   * @param questionText the new question text
   */
  public void setQuestionText(String questionText) {
    this.questionText = questionText;
  }

  @Override
  public AiAbstractResponseDTO toDTO(Integer userRating) {
    return new AiMaterialResponseDTO(this, userRating);
  }
}
