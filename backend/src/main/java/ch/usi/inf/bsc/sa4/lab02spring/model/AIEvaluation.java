package ch.usi.inf.bsc.sa4.lab02spring.model;

import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * Represents an AI evaluation of a particular answer in the system.
 *
 * <p>Each {@code AIEvaluation} holds the AI’s reasoning, whether the user’s answer was judged
 * correct, and links to the corresponding {@link Answer} entity.
 */
@Entity
@Table(name = "AIEvaluation")
public class AIEvaluation {

  /** The primary key identifier for the AI evaluation entity. */
  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "aieval_seq")
  @SequenceGenerator(name = "aieval_seq", sequenceName = "aieval_seq", allocationSize = 1)
  @Column(name = "aievaluation_id")
  private Long aiEvaluationId;

  /** A universally unique identifier (UUID) for the AI evaluation. */
  @Column(name = "ai_evaluation_did", unique = true, nullable = false, updatable = false)
  private UUID aiEvaluationDid;

  /**
   * The reasoning provided by the AI for evaluating the user's answer.
   *
   * <p>This field contains a detailed explanation or justification from the AI, with a maximum
   * length of 10,000 characters. It is a mandatory field and cannot be null.
   */
  @Column(name = "ai_reasoning", nullable = false, length = 10000)
  private String aiReasoning;

  /**
   * Indicates whether the AI determined the user's answer to be correct.
   *
   * <p>This boolean field captures the AI's assessment of the user's answer. It is a required field
   * and cannot be null.
   */
  @Column(name = "is_user_answer_correct", nullable = false)
  private boolean isAnswerCorrect;

  /**
   * The answer entity associated with this AI evaluation.
   *
   * <p>This field establishes a one-to-one relationship with the {@code Answer} entity, linking the
   * AI evaluation to the specific answer it pertains to.
   */
  @OneToOne
  @JoinColumn(name = "answer_id", referencedColumnName = "answer_id")
  private Answer answer;

  /** Default no-argument constructor required by JPA. */
  public AIEvaluation() {}

  /**
   * Constructs a new AI evaluation for the given answer.
   *
   * <p>A new unique identifier ({@code aiEvaluationDid}) is generated automatically.
   *
   * @param answer the answer entity being evaluated
   * @param isCorrect {@code true} if the user's answer is correct according to the AI; {@code
   *     false} otherwise
   * @param aiReasoning the explanation or reasoning provided by the AI for its judgment
   */
  public AIEvaluation(Answer answer, boolean isCorrect, String aiReasoning) {
    this.aiEvaluationDid = UUID.randomUUID();
    this.aiReasoning = aiReasoning;
    this.isAnswerCorrect = isCorrect;
    this.answer = answer;
  }

  /**
   * Returns the AI’s reasoning for this evaluation.
   *
   * @return a {@code String} describing how the AI arrived at its judgment
   */
  public String getAiReasoning() {
    return aiReasoning;
  }

  /**
   * Indicates whether the AI judged the user’s answer to be correct.
   *
   * @return {@code true} if the answer was considered correct; {@code false} otherwise
   */
  public boolean isAnswerCorrect() {
    return this.isAnswerCorrect;
  }

  /**
   * Returns the student who owns the evaluated answer.
   *
   * @return the {@code Student} associated with this evaluation’s answer
   */
  public Student getOwner() {
    return this.answer.getAttempt().getUser().getStudent();
  }

  /**
   * Returns the answer entity that was evaluated.
   *
   * @return the {@link Answer} object under evaluation
   */
  public Answer getAnswer() {
    return this.answer;
  }

  /**
   * Returns the question associated with the evaluated answer.
   *
   * @return the {@link AbstractQuestion} for which this answer was given
   */
  public AbstractQuestion getQuestion() {
    return this.getAnswer().getQuestion();
  }

  /**
   * Returns the unique identifier (DID) for this AI evaluation.
   *
   * @return a {@link UUID} representing this evaluation’s DID
   */
  public UUID getAiEvaluationDid() {
    return aiEvaluationDid;
  }

  /**
   * Sets the unique identifier (DID) for this AI evaluation.
   *
   * <p>Should normally only be used by persistence frameworks.
   *
   * @param aiEvaluationDid the {@link UUID} to assign as this evaluation’s DID
   */
  public void setAiEvaluationDid(UUID aiEvaluationDid) {
    this.aiEvaluationDid = aiEvaluationDid;
  }
}
