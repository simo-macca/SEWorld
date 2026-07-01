package ch.usi.inf.bsc.sa4.lab02spring.model;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.annotation.PersistenceCreator;

/**
 * The Answer model.
 *
 * <p>This entity represents an answer provided for a specific question within an attempt. It is
 * mapped to the "Answer" table in the database and contains the answer content, the associated
 * attempt, and the related question.
 */
@Entity
@Table(name = "Answer")
public class Answer {

  /**
   * The primary key of the answer entity.
   *
   * <p>This value is automatically generated using a database sequence.
   */
  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "answer_seq")
  @SequenceGenerator(name = "answer_seq", sequenceName = "answer_seq", allocationSize = 1)
  @Column(name = "answer_id")
  private Long answerId;

  /**
   * The unique decentralized identifier (DID) of the answer.
   *
   * <p>This value is generated automatically and is immutable.
   */
  @Column(name = "answer_did", unique = true, nullable = false, updatable = false)
  private UUID answerDid;

  /**
   * The content of the answer.
   *
   * <p>This field stores the text of the answer provided.
   */
  @Column(name = "answer_content", nullable = false)
  private String answerContent;

  /**
   * The attempt to which this answer belongs.
   *
   * <p>This field represents a many-to-one association with the {@link Attempt} entity. It
   * indicates the attempt during which this answer was submitted.
   */
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "attempt_id", nullable = false)
  private Attempt attempt;

  /**
   * The question associated with this answer.
   *
   * <p>This field represents a many-to-one association with the {@link AbstractQuestion} entity. It
   * indicates which question the answer corresponds to.
   */
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "question_id", nullable = false)
  private AbstractQuestion question;

  @OneToMany(mappedBy = "answer", fetch = LAZY, cascade = REMOVE, orphanRemoval = true)
  private List<ExerciseAIResponse> exerciseAIResponse;

  /**
   * No-args constructor.
   *
   * <p>Required by JPA for entity instantiation during data retrieval.
   */
  protected Answer() {}

  /**
   * Helper constructor.
   *
   * <p>Constructs a new {@code Answer} instance with the specified content, associated attempt, and
   * question. A random UUID is generated for the answer's decentralized identifier.
   *
   * @param answerContent the text content of the answer
   * @param attempt the {@link Attempt} associated with this answer
   * @param question the {@link AbstractQuestion} to which this answer corresponds
   */
  public Answer(String answerContent, Attempt attempt, AbstractQuestion question) {
    this.answerDid = UUID.randomUUID();
    this.answerContent = answerContent;
    this.attempt = attempt;
    this.question = question;
  }

  /**
   * Persistence creator constructor.
   *
   * <p>This constructor is used by Spring Data JPA for instantiating an entity from the database.
   *
   * @param answerId the primary key identifier of the answer
   * @param answerDid the unique decentralized identifier (UUID) of the answer
   * @param answerContent the text content of the answer
   * @param attempt the {@link Attempt} associated with this answer
   */
  @PersistenceCreator
  public Answer(Long answerId, UUID answerDid, String answerContent, Attempt attempt) {
    this.answerId = answerId;
    this.answerDid = answerDid;
    this.answerContent = answerContent;
    this.attempt = attempt;
  }

  /**
   * Returns the primary key identifier of the answer.
   *
   * @return the answer ID
   */
  public Long getId() {
    return answerId;
  }

  /**
   * Returns the unique decentralized identifier of the answer.
   *
   * @return the answer DID
   */
  public UUID getDid() {
    return answerDid;
  }

  /**
   * Returns the text content of the answer.
   *
   * @return the answer content as a String
   */
  public String getAnswerContent() {
    return answerContent;
  }

  /**
   * Sets the text content of the answer.
   *
   * @param answerContent the new answer content to set
   */
  public void setAnswerContent(String answerContent) {
    this.answerContent = answerContent;
  }

  /**
   * Returns the {@link Attempt} associated with this answer.
   *
   * @return the associated {@link Attempt}
   */
  public Attempt getAttempt() {
    return attempt;
  }

  /**
   * Returns the {@link AbstractQuestion} associated with this answer.
   *
   * @return the associated {@link AbstractQuestion}
   */
  public AbstractQuestion getQuestion() {
    return question;
  }

  /**
   * Returns the unique decentralized identifier (DID) of the associated question.
   *
   * @return the question DID
   */
  public UUID getQuestionDid() {
    return question.getQuestionDid();
  }

  /**
   * Returns the unique decentralized identifier (DID) of the associated attempt.
   *
   * @return the attempt DID
   */
  public UUID getAttemptDid() {
    return attempt.getAttemptDid();
  }

  /**
   * Sets the unique decentralized identifier (DID) of the answer.
   *
   * @param answer the answer DID to set
   */
  public void setAnswerDid(UUID answer) {
    this.answerDid = answer;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Answer answer)) {
      return false;
    }
    return Objects.equals(answerId, answer.answerId)
        && Objects.equals(answerDid, answer.answerDid)
        && Objects.equals(answerContent, answer.answerContent)
        && Objects.equals(attempt, answer.attempt)
        && Objects.equals(question, answer.question);
  }

  @Override
  public int hashCode() {
    return Objects.hash(answerId, answerDid, answerContent, attempt, question);
  }

  @Override
  public String toString() {
    return "Answer{"
        + "answerId="
        + answerId
        + ", answerDid="
        + answerDid
        + ", answerContent='"
        + answerContent
        + '\''
        + ", attempt="
        + attempt
        + ", question="
        + question
        + '}';
  }

  /**
   * Returns the list of {@link ExerciseAIResponse} associated with this answer.
   *
   * @return the list of {@link ExerciseAIResponse}
   */
  public List<ExerciseAIResponse> getExerciseAIResponse() {
    return exerciseAIResponse;
  }

  /**
   * Sets the list of {@link ExerciseAIResponse} associated with this answer.
   *
   * @param exerciseAIResponse the list of {@link ExerciseAIResponse} to set
   */
  public void setExerciseAIResponse(List<ExerciseAIResponse> exerciseAIResponse) {
    this.exerciseAIResponse = exerciseAIResponse;
  }
}
