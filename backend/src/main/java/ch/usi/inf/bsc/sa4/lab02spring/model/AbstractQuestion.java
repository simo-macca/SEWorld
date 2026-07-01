package ch.usi.inf.bsc.sa4.lab02spring.model;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.FeedbackQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.QuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.service.AiQuestionService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.TABLE;
import static jakarta.persistence.InheritanceType.JOINED;

/**
 * Abstract base class for questions
 *
 * <p>This entity represents the common properties and relationships of all question types. It is
 * mapped to the "question" table in the database and uses the {@code JOINED} inheritance strategy,
 * meaning that subclasses will have their own tables that share a primary key with this table.
 */
@Entity
@Inheritance(strategy = JOINED)
@Table(name = "question")
public abstract class AbstractQuestion {

  /**
   * The primary key identifier of the question.
   *
   * <p>This value is generated using the {@code TABLE} strategy.
   */
  @Id
  @GeneratedValue(strategy = TABLE)
  @Column(name = "question_ID")
  protected Long questionId;

  /**
   * The unique decentralized identifier (DID) for the question.
   *
   * <p>This value is generated automatically and is used to uniquely identify the question
   * externally.
   */
  @Column(name = "question_DID", unique = true, updatable = false)
  protected UUID questionDid;

  /** The title of the question. */
  @Column(name = "question_title", nullable = false, columnDefinition = "TEXT")
  protected String questionTitle;

  /**
   * The exercise to which the question belongs.
   *
   * <p>This many-to-one association links the question to its parent exercise.
   */
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "exercise_id")
  protected Exercise exerciseId;

  /**
   * The {@link Variant} associated with this entity.
   *
   * <p>This field establishes a many-to-one relationship, indicating that multiple instances of
   * this entity can be linked to a single {@code Variant}
   */
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "variant_ID")
  protected Variant variant;

  /**
   * The list of {@link Attempt} instances associated with this entity.
   *
   * <p>This field defines a bidirectional many-to-many relationship with the {@code Attempt}
   * entity. The {@code mappedBy} attribute indicates that the {@code Attempt} entity owns the
   * relationship, and this side is the inverse.
   */
  @ManyToMany(mappedBy = "questions")
  private List<Attempt> attempts = new ArrayList<>();

  /**
   * The list of answers associated with this question.
   *
   * <p>This one-to-many relationship ensures that when a question is deleted, all its associated
   * answers are also removed. The relationship is lazily fetched.
   */
  @OneToMany(mappedBy = "question", cascade = REMOVE, orphanRemoval = true, fetch = LAZY)
  @BatchSize(size = 10)
  private final List<Answer> answers = new ArrayList<>();

  /**
   * No-argument constructor.
   *
   * <p>Required by JPA for entity instantiation during data retrieval.
   */
  protected AbstractQuestion() {}

  /**
   * Constructs a new {@code AbstractQuestion} with the specified title and associated exercise.
   *
   * <p>Generates a new unique decentralized identifier (DID) for the question.
   *
   * @param questionTitle the title of the question
   * @param exerciseId the {@link Exercise} associated with the question
   * @param variant the {@link Variant} associated with the question
   */
  protected AbstractQuestion(String questionTitle, Exercise exerciseId, Variant variant) {
    this.questionDid = UUID.randomUUID();
    this.questionTitle = questionTitle;
    this.exerciseId = exerciseId;
    this.variant = variant;
  }

  /**
   * Retrieves the variant associated with this entity.
   *
   * @return the current {@code Variant} instance
   */
  public Variant getVariant() {
    return variant;
  }

  /**
   * Sets the variant for this entity.
   *
   * @param variant the {@code Variant} instance to associate
   */
  public void setVariant(Variant variant) {
    this.variant = variant;
  }

  /**
   * Retrieves the list of attempts linked to this entity.
   *
   * @return a {@code List} of {@code Attempt} instances
   */
  public List<Attempt> getAttempts() {
    return attempts;
  }

  /**
   * Sets the list of attempts for this entity.
   *
   * @param attempts a {@code List} of {@code Attempt} instances to associate
   */
  public void setAttempts(List<Attempt> attempts) {
    this.attempts = attempts;
  }

  /**
   * Sets the correct answer for this question.
   *
   * <p>This method is used by subclasses to set the correct answer for the question.
   *
   * @param integer the correct answer
   */
  public void setCorrectAnswer(Object integer) {}

  /**
   * Sets the list of choices for this question.
   *
   * <p>This method is used by subclasses to set the list of choices for the question.
   *
   * @param choices the list of choices
   */
  public void setChoices(List<String> choices) {}

  /**
   * Returns the title of the question.
   *
   * @return the question title
   */
  public String getQuestionTitle() {
    return questionTitle;
  }

  /**
   * Sets the title of the question.
   *
   * @param questionTitle the new title of the question
   */
  public void setQuestionTitle(String questionTitle) {
    this.questionTitle = questionTitle;
  }

  /**
   * Returns the primary key identifier of the question.
   *
   * @return the question ID
   */
  public Long getQuestionId() {
    return questionId;
  }

  /**
   * Returns the unique decentralized identifier (DID) of the question.
   *
   * @return the question DID
   */
  public UUID getQuestionDid() {
    return questionDid;
  }

  /**
   * Sets the unique decentralized identifier (DID) of the question.
   *
   * @param questionDid the new DID to be set
   */
  public void setQuestionDid(UUID questionDid) {
    this.questionDid = questionDid;
  }

  /**
   * Returns the {@link Exercise} associated with this question.
   *
   * @return the associated exercise
   */
  public Exercise getExercise() {
    return exerciseId;
  }

  /**
   * Sets the {@link Exercise} associated with this question.
   *
   * @param exerciseId the exercise to be set
   */
  public void setExerciseId(Exercise exerciseId) {
    this.exerciseId = exerciseId;
  }

  /**
   * Converts the question entity into a Data Transfer Object (DTO).
   *
   * <p>The conversion may vary depending on the role (e.g., student or instructor).
   *
   * @param role a boolean flag indicating the role context (true for one role, false for another)
   * @return an object representing the DTO for this question
   */
  public abstract QuestionDTO convertToDTO(boolean role);

  /**
   * Generates feedback for a submitted answer using AI services.
   *
   * <p>This abstract method is intended to be implemented by subclasses to provide specific logic
   * for generating feedback based on the submitted answer, utilizing AI services and considering
   * the user's identity.
   *
   * @param submittedAnswer the {@code Answer} object representing the user's submitted answer
   * @param aiQuestionService the {@code AiQuestionService} used to process the answer and generate
   *     feedback
   * @param principal an object representing the authenticated user, typically used for
   *     authorization and personalization
   * @return a {@code FeedbackQuestionDTO} containing the generated feedback
   */
  public abstract FeedbackQuestionDTO<?> provideFeedbackForAnswer(
      Answer submittedAnswer, AiQuestionService aiQuestionService, Object principal);

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AbstractQuestion that)) {
      return false;
    }

    return Objects.equals(questionId, that.questionId)
        && Objects.equals(questionDid, that.questionDid)
        && Objects.equals(questionTitle, that.questionTitle)
        && Objects.equals(exerciseId, that.exerciseId)
        && Objects.equals(variant, that.variant)
        && Objects.equals(answers, that.answers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(questionId, questionDid, questionTitle, exerciseId, variant, answers);
  }

  @Override
  public String toString() {
    return "AbstractQuestion{"
        + "questionId="
        + questionId
        + ", questionDid="
        + questionDid
        + ", questionTitle='"
        + questionTitle
        + '\''
        + ", exerciseId="
        + exerciseId
        + ", variant="
        + variant
        + ", answers="
        + answers
        + '}';
  }

  /**
   * Retrieves a list of answers associated with this instance.
   *
   * @return a new {@code List} containing all {@code Answer} objects from this instance.
   */
  public List<Answer> getAnswers() {
    return new ArrayList<>(this.answers);
  }

  /**
   * Returns the string representation of the answer.
   *
   * @return a {@code String} representing the answer.
   */
  public abstract String getStringAnswer();

  /**
   * Returns the string representation of the answer given by the user.
   *
   * @return a {@code String} representing the answer given by the user.
   */
  public abstract String getStringAnswerByUserAnswer(String userAnswer);

  /**
   * Indicates whether this instance represents a short answer (SHA) question.
   *
   * @return {@code false}, as this instance does not represent a SHA question by default.
   */
  public boolean isSha() {
    return false;
  }

  /**
   * Indicates whether this instance represents a multiple-choice (MCH) question.
   *
   * @return {@code false}, as this instance does not represent a MCH question by default.
   */
  public boolean isMch() {
    return false;
  }

  /**
   * Retrieves the multiple-choice question associated with this instance.
   *
   * @return the {@code MultiChoiceQuestion} associated with this instance.
   * @throws UnsupportedOperationException if this instance does not represent a MCH question.
   */
  public MultiChoiceQuestion getMch() {
    throw new UnsupportedOperationException("Not an MCH question.");
  }
}
