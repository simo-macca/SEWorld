package ch.usi.inf.bsc.sa4.lab02spring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

/**
 * Entity representing an Exercise.
 *
 * <p>This class encapsulates the details of an exercise including its title, description, draft
 * status, associated topic, and its related attempts and questions. The unique identifier {@code
 * exerciseDid} is generated as a UUID, and each exercise is initially set to draft mode.
 */
@Entity
@Table(name = "Exercise")
public class Exercise {

  /** The primary key of the exercise. */
  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "exercise_seq")
  @SequenceGenerator(name = "exercise_seq", sequenceName = "exercise_seq", allocationSize = 1)
  @Column(name = "exercise_id")
  private Long exerciseId;

  /** The unique identifier for the exercise. */
  @Column(name = "exercise_did", unique = true, nullable = false, updatable = false)
  private UUID exerciseDid;

  /** The title of the exercise. */
  @Column(name = "exercise_title", nullable = false)
  private String exerciseTitle;

  /** The description of the exercise. */
  @Column(name = "exercise_description", nullable = false, columnDefinition = "TEXT")
  private String exerciseDescription;

  /** Indicates whether the exercise is in draft mode. */
  @Column(name = "exercise_is_draft", nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
  private boolean exerciseIsDraft;

  /** Indicates whether the exercise has a short question. */
  @Column(
      name = "is_present_short_question",
      nullable = false,
      columnDefinition = "BOOLEAN DEFAULT false")
  private boolean isPresentShortQuestion;

  /** Indicates whether the exercise has been completed. */
  @Transient private boolean completed;

  /** The topic to which this exercise belongs. */
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "topic_id")
  private Topic topic;

  /** The list of attempts associated with this exercise. */
  @OneToMany(mappedBy = "exercise", fetch = LAZY, orphanRemoval = true, cascade = REMOVE)
  private final List<Attempt> attempts = new ArrayList<>();

  /** The list of questions associated with this exercise. */
  @OneToMany(mappedBy = "exerciseId", fetch = LAZY, orphanRemoval = true, cascade = REMOVE)
  private final List<AbstractQuestion> questions = new ArrayList<>();

  /** The list of variants associated with this exercise. */
  @OneToMany(mappedBy = "exercise", fetch = LAZY, orphanRemoval = true, cascade = REMOVE)
  private final List<Variant> variants = new ArrayList<>();

  /** Default constructor for JPA. */
  public Exercise() {}

  /**
   * Constructs a new Exercise with the specified title, description, and associated topic.
   *
   * <p>A random UUID is generated for {@code exerciseDid} and the exercise is set to draft mode by
   * default.
   *
   * @param exerciseTitle the title of the exercise.
   * @param exerciseDescription the description of the exercise.
   * @param topic the topic to which this exercise belongs.
   */
  public Exercise(String exerciseTitle, String exerciseDescription, Topic topic) {
    this.completed = false;
    this.exerciseDid = UUID.randomUUID();
    this.exerciseTitle = exerciseTitle;
    this.exerciseDescription = exerciseDescription;
    this.exerciseIsDraft = true;
    this.topic = topic;
  }

  /**
   * Gets the primary key of the exercise.
   *
   * @return the exerciseId.
   */
  public Long getExerciseId() {
    return exerciseId;
  }

  /**
   * Sets the primary key of the exercise.
   *
   * @param exerciseId the exerciseId to set.
   */
  public void setExerciseId(Long exerciseId) {
    this.exerciseId = exerciseId;
  }

  /**
   * Gets the unique identifier of the exercise.
   *
   * @return the exerciseDid.
   */
  public UUID getExerciseDid() {
    return exerciseDid;
  }

  /**
   * Sets the unique identifier of the exercise.
   *
   * @param did the UUID to set as exerciseDid.
   */
  public void setExerciseDid(UUID did) {
    this.exerciseDid = did;
  }

  /**
   * Gets the title of the exercise.
   *
   * @return the exerciseTitle.
   */
  public String getExerciseTitle() {
    return exerciseTitle;
  }

  /**
   * Sets the title of the exercise.
   *
   * @param exerciseTitle the title to set.
   */
  public void setExerciseTitle(String exerciseTitle) {
    this.exerciseTitle = exerciseTitle;
  }

  /**
   * Gets the description of the exercise.
   *
   * @return the exerciseDescription.
   */
  public String getExerciseDescription() {
    return exerciseDescription;
  }

  /**
   * Sets the description of the exercise.
   *
   * @param exerciseDescription the description to set.
   */
  public void setExerciseDescription(String exerciseDescription) {
    this.exerciseDescription = exerciseDescription;
  }

  /**
   * Gets the draft status of the exercise.
   *
   * @return {@code true} if the exercise is in draft mode; {@code false} otherwise.
   */
  public boolean isExerciseIsDraft() {
    return exerciseIsDraft;
  }

  /**
   * Sets the draft status of the exercise.
   *
   * @param exerciseIsDraft the draft status to set.
   */
  public void setExerciseIsDraft(boolean exerciseIsDraft) {
    this.exerciseIsDraft = exerciseIsDraft;
  }

  /**
   * Gets the topic associated with the exercise.
   *
   * @return the associated Topic.
   */
  public Topic getTopic() {
    return topic;
  }

  /**
   * Sets the topic associated with the exercise.
   *
   * @param topic the Topic to associate with this exercise.
   */
  public void setTopic(Topic topic) {
    this.topic = topic;
  }

  /**
   * Gets the completion associated with the exercise.
   *
   * @return the completion stage of the exercise.
   */
  public boolean isCompleted() {
    return completed;
  }

  /**
   * Sets the status of exercise completion.
   *
   * @param completed the boolean value to update the completion stage.
   */
  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  /**
   * Returns the current list of {@link Attempt} instances associated with this entity.
   *
   * <p>The returned list reflects the internal state; callers should not modify it directly to
   * avoid unintended side effects.
   *
   * @return an unmodifiable {@link List} of {@link Attempt} objects representing all attempts
   */
  public List<Attempt> getAttempts() {
    return attempts;
  }

  /**
   * Replaces the internal list of {@link Attempt} instances.
   *
   * <p>If the provided list is {@code null}, the existing list is cleared. Otherwise, the contents
   * of the internal list are replaced with all elements from the given list.
   *
   * @param attempts the new list of {@link Attempt} objects; may be {@code null} to clear all
   *     entries
   */
  public void setAttempts(List<Attempt> attempts) {
    if (attempts == null) {
      this.attempts.clear();
    } else {
      this.attempts.clear();
      this.attempts.addAll(attempts);
    }
  }

  /**
   * Retrieves the current status indicating whether a short-answer question is present.
   *
   * @return {@code true} if the short-answer question is present; {@code false} otherwise.
   */
  public boolean getIsPresentShortQuestion() {
    return isPresentShortQuestion;
  }

  /**
   * Sets the status indicating whether a short-answer question is present.
   *
   * @param isPresentShortQuestion {@code true} to indicate the short-answer question is present;
   *     {@code false} otherwise.
   */
  public void setIsPresentShortQuestion(boolean isPresentShortQuestion) {
    this.isPresentShortQuestion = isPresentShortQuestion;
  }

  /**
   * Retrieves a copy of the list of questions associated with this entity. The returned list is a
   * new {@link ArrayList} containing the elements of the original list.
   *
   * @return A list containing all the questions.
   */
  public List<AbstractQuestion> getQuestions() {
    return new ArrayList<>(questions);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Exercise exercise)) {
      return false;
    }
    return exerciseIsDraft == exercise.exerciseIsDraft
        && completed == exercise.completed
        && Objects.equals(exerciseId, exercise.exerciseId)
        && Objects.equals(exerciseDid, exercise.exerciseDid)
        && Objects.equals(exerciseTitle, exercise.exerciseTitle)
        && Objects.equals(exerciseDescription, exercise.exerciseDescription)
        && Objects.equals(topic, exercise.topic)
        && Objects.equals(attempts, exercise.attempts)
        && Objects.equals(questions, exercise.questions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        exerciseId,
        exerciseDid,
        exerciseTitle,
        exerciseDescription,
        exerciseIsDraft,
        completed,
        topic,
        attempts,
        questions);
  }

  @Override
  public String toString() {
    return "Exercise{"
        + "exerciseId="
        + exerciseId
        + ", exerciseDid="
        + exerciseDid
        + ", exerciseTitle='"
        + exerciseTitle
        + '\''
        + ", exerciseDescription='"
        + exerciseDescription
        + '\''
        + ", exerciseIsDraft="
        + exerciseIsDraft
        + ", completed="
        + completed
        + ", topic="
        + topic
        + ", attempts="
        + attempts
        + ", questions="
        + questions
        + '}';
  }
}
