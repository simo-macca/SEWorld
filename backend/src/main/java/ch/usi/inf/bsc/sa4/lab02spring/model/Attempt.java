package ch.usi.inf.bsc.sa4.lab02spring.model;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Represents the feedback for an attempt, including performance metrics and detailed feedback data.
 *
 * <p>This entity is mapped to the "Attempt" table in the database and stores attempt information
 * such as the current stage of the attempt, whether the attempt has been submitted and whether the
 * attempt has been completed. Additionally, it holds a reference to the associated {@link Exercise}
 * and {@link AbstractUser} entity.
 */
@Entity
@Table(name = "Attempt")
public class Attempt {

  /**
   * The primary key of the attempt record.
   *
   * <p>It is automatically generated using a database sequence.
   */
  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "attempt_seq")
  @SequenceGenerator(name = "attempt_seq", sequenceName = "attempts_seq", allocationSize = 1)
  @Column(name = "attempt_id")
  private Long attemptId;

  /**
   * A unique UUID used to hide the real database id.
   *
   * <p>This field is unique, cannot be null, and is not updatable after creation.
   */
  @Column(name = "attempt_did", unique = true, nullable = false, updatable = false)
  private UUID attemptDid;

  /**
   * Indicates whether the attempt has been submitted.
   *
   * <p>This flag cannot be null and can be updated.
   */
  @Column(name = "attempt_is_submitted", nullable = false)
  private boolean attemptIsSubmitted;

  /**
   * Indicates whether the attempt has been completed.
   *
   * <p>This flag cannot be null and can be updated.
   */
  @Column(name = "attempt_is_completed", nullable = false)
  private boolean attemptIsCompleted;

  /**
   * The associated {@link Exercise} entity for this attempt.
   *
   * <p>This field represents a many-to-one relationship, linking the attempt to a specific
   * exercise. It is non-nullable.
   */
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "exercise_id", nullable = false)
  private Exercise exercise;

  /**
   * The associated {@link AbstractUser} entity for this attempt.
   *
   * <p>This field represents a many-to-one relationship, linking the attempt to a specific user. It
   * is non-nullable.
   */
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private AbstractUser user;

  @ManyToMany(fetch = LAZY)
  @JoinTable(
      name = "attempt_question",
      joinColumns = @JoinColumn(name = "attempt_id"),
      inverseJoinColumns = @JoinColumn(name = "question_id"))
  private List<AbstractQuestion> questions;

  /**
   * The list of answers associated with this attempt.
   *
   * <p>This collection holds the {@link Attempt} objects that represent the answers or responses
   * linked to the current attempt.
   */
  @OneToMany(mappedBy = "attempt", fetch = LAZY, cascade = REMOVE, orphanRemoval = true)
  private final List<Answer> answers = new ArrayList<>();

  /**
   * The list of feedback associated with this attempt.
   *
   * <p>This collection holds the {@link Attempt} objects that represent the feedback linked to the
   * current attempt.
   */
  @OneToMany(mappedBy = "attempt", fetch = LAZY, cascade = REMOVE, orphanRemoval = true)
  private final List<Feedback> feedbacks = new ArrayList<>();

  /**
   * Default no-args constructor.
   *
   * <p>Required by JPA for entity instantiation during retrieval.
   */
  public Attempt() {}

  /**
   * Constructs a new {@link Attempt} with the specified {@link Exercise} and {@link AbstractUser}.
   *
   * <p>This constructor explicitly initializes the attempt by:
   *
   * <ul>
   *   <li>Generating a new random UUID for {@code attemptDid}
   *   <li>Setting {@code attemptCompletionStage} to 0
   *   <li>Marking the attempt as not submitted
   *   <li>Marking the attempt as not completed
   *   <li>Associating the provided {@link Exercise} and {@link AbstractUser} with the attempt
   * </ul>
   *
   * @param user the {@link AbstractUser} entity associated with this attempt
   * @param exercise the {@link Exercise} entity associated with this attempt
   * @param questions the list of {@link AbstractQuestion} entities associated with this attempt
   */
  public Attempt(AbstractUser user, Exercise exercise, List<AbstractQuestion> questions) {
    this.attemptDid = UUID.randomUUID();
    this.attemptIsSubmitted = false;
    this.attemptIsCompleted = false;
    this.exercise = exercise;
    this.user = user;
    this.questions = questions;
  }

  /**
   * Retrieves the primary key of the attempt.
   *
   * @return the attempt's primary key
   */
  public Long getAttemptId() {
    return attemptId;
  }

  /**
   * Retrieves the decentralized identifier (DID) of the attempt.
   *
   * @return the attempt's UUID used as a DID
   */
  public UUID getAttemptDid() {
    return attemptDid;
  }

  /**
   * Sets the decentralized identifier (DID) of the attempt.
   *
   * @param attemptDid the new UUID for the attempt's DID
   */
  public void setAttemptDid(UUID attemptDid) {
    this.attemptDid = attemptDid;
  }

  /**
   * Checks whether the attempt has been submitted.
   *
   * @return {@code true} if the attempt is submitted; {@code false} otherwise
   */
  public boolean isAttemptIsSubmitted() {
    return attemptIsSubmitted;
  }

  /**
   * Updates the submission status of the attempt.
   *
   * @param attemptIsSubmitted the new completion status
   */
  public void setAttemptIsSubmitted(boolean attemptIsSubmitted) {
    this.attemptIsSubmitted = attemptIsSubmitted;
  }

  /**
   * Checks whether the attempt has been completed.
   *
   * @return {@code true} if the attempt is completed; {@code false} otherwise
   */
  public boolean isAttemptIsCompleted() {
    return attemptIsCompleted;
  }

  /**
   * Updates the completion status of the attempt.
   *
   * @param attemptIsCompleted the new completion status
   */
  public void setAttemptIsCompleted(boolean attemptIsCompleted) {
    this.attemptIsCompleted = attemptIsCompleted;
  }

  /**
   * Retrieves the foreign key of the exercise that created the attempt.
   *
   * @return the exercise's foreign key
   */
  public Exercise getExercise() {
    return exercise;
  }

  /**
   * Retrieves the foreign key of the user who created the attempt.
   *
   * @return the user's foreign key
   */
  public AbstractUser getUser() {
    return user;
  }

  /**
   * Retrieves the answers of the attempt
   *
   * @return answers of the attempt
   */
  public List<Answer> getAnswers() {
    return new ArrayList<>(answers);
  }

  /**
   * Sets the list of answers for this object.
   *
   * <p>If the provided {@code answers} list is {@code null}, the existing list of answers is
   * cleared. Otherwise, the current list is cleared, and all elements from the provided list are
   * added to it. This ensures that the internal list reflects exactly the contents of the provided
   * list.
   *
   * @param answers the new list of {@link Answer} objects to set; may be {@code null}
   */
  public void setAnswers(List<Answer> answers) {
    if (answers == null) {
      this.answers.clear();
    } else {
      this.answers.clear();
      this.answers.addAll(answers);
    }
  }

  /**
   * Replaces the current list of {@link Feedback} objects.
   *
   * <p>If the provided {@code feedbacks} list is {@code null}, this method clears any existing
   * entries. Otherwise, it replaces the contents of the internal list with all elements from {@code
   * feedbacks}.
   *
   * @param feedbacks the new list of {@link Feedback} to apply; may be {@code null}
   */
  public void setFeedbacks(List<Feedback> feedbacks) {
    if (feedbacks == null) {
      this.feedbacks.clear();
    } else {
      this.feedbacks.clear();
      this.feedbacks.addAll(feedbacks);
    }
  }

  /**
   * Retrieves the list of {@link Feedback} objects associated with the attempt.
   *
   * @return the list of {@link Feedback} objects associated with the attempt
   */
  public List<Feedback> getFeedbacks() {
    return new ArrayList<>(feedbacks);
  }

  /**
   * Retrieves the list of {@link AbstractQuestion} objects associated with the attempt.
   *
   * @return the list of {@link AbstractQuestion} objects associated with the attempt
   */
  public List<AbstractQuestion> getQuestions() {
    return questions;
  }

  /**
   * Sets the list of {@link AbstractQuestion} objects associated with the attempt.
   *
   * @param questions the list of {@link AbstractQuestion} objects to set
   */
  public void setQuestions(List<AbstractQuestion> questions) {
    this.questions = questions;
  }

  /**
   * Loads the list of answers associated with the given {@link Attempt}.
   *
   * <p>Attempts to retrieve and return {@code attempt.getAnswers()}.
   *
   * @return a {@link List} of {@link Answer} objects retrieved from the attempt
   * @throws org.springframework.web.client.HttpClientErrorException if answers cannot be loaded,
   *     wrapping the underlying exception as an HTTP 500 error
   */
  public List<Answer> loadAnswers() {
    try {
      return this.getAnswers();
    } catch (Exception e) {
      throw new HttpClientErrorException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load answers");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Attempt attempt)) {
      return false;
    }
    return attemptIsSubmitted == attempt.attemptIsSubmitted
        && attemptIsCompleted == attempt.attemptIsCompleted
        && Objects.equals(attemptId, attempt.attemptId)
        && Objects.equals(attemptDid, attempt.attemptDid)
        && Objects.equals(exercise, attempt.exercise)
        && Objects.equals(user, attempt.user)
        && Objects.equals(answers, attempt.answers)
        && Objects.equals(feedbacks, attempt.feedbacks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        attemptId,
        attemptDid,
        attemptIsSubmitted,
        attemptIsCompleted,
        exercise,
        user,
        answers,
        feedbacks);
  }

  /**
   * Recomputes the completion status of the attempt based on the feedback percentages.
   *
   * <p>This method iterates over the associated feedbacks of the attempt, filters those with a
   * percentage greater than or equal to 60, and sets the {@code attemptIsCompleted} flag to {@code
   * true} if any such feedback exists; otherwise, it sets the flag to {@code false}. The completion
   * status is determined solely by the presence of feedbacks with a percentage of 60 or higher.
   */
  public void recomputeIsCompleted() {
    List<Feedback> f = new ArrayList<>(this.feedbacks);
    f = f.stream().filter(feedback -> feedback.getPercentage() >= 60).toList();
    if (!f.isEmpty()) {
      this.attemptIsCompleted = true;
      return;
    }
    this.attemptIsCompleted = false;
  }
}
