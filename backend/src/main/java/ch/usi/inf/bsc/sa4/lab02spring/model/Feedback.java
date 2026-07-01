package ch.usi.inf.bsc.sa4.lab02spring.model;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.FeedbackQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.service.AiQuestionService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.util.Pair;
import org.springframework.web.client.HttpClientErrorException;
import org.yaml.snakeyaml.util.Tuple;

/**
 * Represents the feedback for an attempt, including performance metrics and detailed feedback data.
 *
 * <p>This entity is mapped to the "Feedback" table in the database and stores feedback information
 * such as the total number of correct answers, total questions answered, and the percentage score.
 * Additionally, it holds a JSON representation of detailed feedback data and a reference to the
 * associated {@link Attempt} entity.
 */
@Entity
@Table(name = "Feedback")
public class Feedback {

  /** The primary key of the feedback record. */
  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "feedback_seq")
  @SequenceGenerator(name = "feedback_seq", sequenceName = "feedback_seq", allocationSize = 1)
  @Column(name = "feedback_id")
  private Long feedbackId;

  /**
   * The unique decentralized identifier (DID) for the feedback.
   *
   * <p>This value is generated automatically and is immutable.
   */
  @Column(name = "feedback_did", unique = true, nullable = false, updatable = false)
  private UUID feedbackDid;

  /** The total number of correct answers recorded in the feedback. */
  @Column(name = "total_correct", nullable = false)
  private int totalCorrect;

  /** The total number of questions evaluated in the feedback. */
  @Column(name = "total_questions", nullable = false, updatable = false)
  private int totalQuestions;

  /** The percentage of correct answers. */
  @Column(name = "percentage", nullable = false)
  private double percentage;

  /**
   * The detailed feedback data stored as a JSON object.
   *
   * <p>This field holds a list of objects representing individual feedback items and is stored in a
   * JSONB column.
   */
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "feedback_data", columnDefinition = "jsonb", nullable = false)
  private List<?> feedbackData;

  /**
   * The attempt associated with this feedback.
   *
   * <p>This is a many-to-one relationship, indicating that multiple feedback records can be
   * associated with a single attempt.
   */
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "attempt_id", nullable = false)
  private Attempt attempt;

  /** Default no-argument constructor. */
  public Feedback() {}

  /**
   * Constructs a new {@code Feedback} instance with the specified performance metrics and feedback
   * data.
   *
   * @param totalCorrect the total number of correct answers
   * @param totalQuestions the total number of questions evaluated
   * @param percentage the percentage of correct answers
   * @param feedbackData the detailed feedback data as a list of objects
   * @param attempt the associated {@link Attempt} for which this feedback is generated
   */
  public Feedback(
      int totalCorrect,
      int totalQuestions,
      double percentage,
      List<?> feedbackData,
      Attempt attempt) {
    this.feedbackDid = UUID.randomUUID();
    this.totalCorrect = totalCorrect;
    this.totalQuestions = totalQuestions;
    this.percentage = percentage;
    this.feedbackData = new ArrayList<>(feedbackData);
    this.attempt = attempt;
  }

  /**
   * Evaluates a list of answers against their corresponding questions and produces feedback.
   *
   * <p>After processing all answers, calculates the overall percentage (rounded to two decimals)
   * and determines whether at least 60% of the questions were answered correctly.
   *
   * @param attempt the {@link Attempt} context to which these answers belong; used when
   *     constructing the returned {@link Feedback}
   * @param questions the ordered list of {@link AbstractQuestion} objects to validate against
   * @param answers the list of {@link Answer} instances provided by the user
   * @param aiQuestionService the {@link AiQuestionService} to use isCorrect method
   * @param principal the principal user making the request, used to determine whether the user
   * @return a {@link Tuple} whose first element is {@code true} if the user answered more than half
   *     correctly, and whose second element is the constructed {@link Feedback} containing
   *     per-question details and overall metrics
   * @throws HttpClientErrorException if an unrecognized question subtype is encountered, with
   *     status {@code BAD_REQUEST}
   */
  public static Pair<Boolean, Feedback> computeFeedback(
      Attempt attempt,
      Collection<AbstractQuestion> questions,
      Collection<Answer> answers,
      AiQuestionService aiQuestionService,
      Object principal) {

    final List<? extends FeedbackQuestionDTO<?>> feedbacks =
        answers.stream()
            .map(
                (Answer answer) -> {
                  AbstractQuestion question = answer.getQuestion();
                  question = (AbstractQuestion) Hibernate.unproxy(question);
                  return question.provideFeedbackForAnswer(answer, aiQuestionService, principal);
                })
            .toList();

    final int totalCorrect =
        Math.toIntExact(feedbacks.stream().filter(FeedbackQuestionDTO::isCorrect).count());

    final int totalQuestions = questions.size();
    final double percentage = Math.round((100.0 * totalCorrect / totalQuestions) * 100.0) / 100.0;

    final double PASSING_THRESHOLD = 60.0;

    final boolean updated = percentage >= PASSING_THRESHOLD;

    final Feedback finalFeedback =
        new Feedback(totalCorrect, totalQuestions, percentage, feedbacks, attempt);

    return Pair.of(updated, finalFeedback);
  }

  /**
   * Gets the unique identifier of the feedback record.
   *
   * @return the feedback ID
   */
  public Long getFeedbackId() {
    return feedbackId;
  }

  /**
   * Sets the unique identifier of the feedback record.
   *
   * @param feedbackId the feedback ID to set
   */
  public void setFeedbackId(Long feedbackId) {
    this.feedbackId = feedbackId;
  }

  /**
   * Gets the unique decentralized identifier (DID) of the feedback.
   *
   * @return the feedback DID
   */
  public UUID getFeedbackDid() {
    return feedbackDid;
  }

  /**
   * Sets the unique decentralized identifier (DID) of the feedback.
   *
   * @param feedbackDid the feedback DID to set
   */
  public void setFeedbackDid(UUID feedbackDid) {
    this.feedbackDid = feedbackDid;
  }

  /**
   * Gets the total number of correct answers.
   *
   * @return the total correct answers
   */
  public int getTotalCorrect() {
    return totalCorrect;
  }

  /**
   * Sets the total number of correct answers.
   *
   * @param totalCorrect the total correct answers to set
   */
  public void setTotalCorrect(int totalCorrect) {
    this.totalCorrect = totalCorrect;
  }

  /**
   * Gets the total number of questions evaluated.
   *
   * @return the total number of questions
   */
  public int getTotalQuestions() {
    return totalQuestions;
  }

  /**
   * Sets the total number of questions evaluated.
   *
   * @param totalQuestions the total number of questions to set
   */
  public void setTotalQuestions(int totalQuestions) {
    this.totalQuestions = totalQuestions;
  }

  /**
   * Gets the percentage of correct answers.
   *
   * @return the percentage score
   */
  public double getPercentage() {
    return percentage;
  }

  /**
   * Sets the percentage of correct answers.
   *
   * @param percentage the percentage score to set
   */
  public void setPercentage(double percentage) {
    this.percentage = percentage;
  }

  /**
   * Gets the detailed feedback data.
   *
   * @return a list of objects representing the detailed feedback data
   */
  public List<Object> getFeedbackData() {
    return new ArrayList<>(feedbackData);
  }

  /**
   * Sets the detailed feedback data.
   *
   * @param feedbackData the list of feedback data objects to set
   */
  public void setFeedbackData(List<Object> feedbackData) {
    this.feedbackData = new ArrayList<>(feedbackData);
  }

  /**
   * Gets the associated attempt for this feedback.
   *
   * @return the {@link Attempt} associated with this feedback
   */
  public Attempt getAttempt() {
    return attempt;
  }

  /**
   * Sets the associated attempt for this feedback.
   *
   * @param attempt the {@link Attempt} to associate with this feedback
   */
  public void setAttempt(Attempt attempt) {
    this.attempt = attempt;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Feedback feedback)) {
      return false;
    }
    return totalCorrect == feedback.totalCorrect
        && totalQuestions == feedback.totalQuestions
        && Double.compare(percentage, feedback.percentage) == 0
        && Objects.equals(feedbackId, feedback.feedbackId)
        && Objects.equals(feedbackDid, feedback.feedbackDid)
        && Objects.equals(feedbackData, feedback.feedbackData)
        && Objects.equals(attempt, feedback.attempt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        feedbackId, feedbackDid, totalCorrect, totalQuestions, percentage, feedbackData, attempt);
  }

  @Override
  public String toString() {
    return "Feedback{"
        + "feedbackId="
        + feedbackId
        + ", feedbackDid="
        + feedbackDid
        + ", totalCorrect="
        + totalCorrect
        + ", totalQuestions="
        + totalQuestions
        + ", percentage="
        + percentage
        + ", feedbackData="
        + feedbackData
        + ", attempt="
        + attempt
        + '}';
  }

  /**
   * Recomputes the completion status of the attempt based on the feedback data.
   *
   * <p>This method iterates through the feedback data and checks if any feedback has a percentage
   * greater than or equal to 60. If such feedback exists, the attempt is marked as completed.
   * Otherwise, it is marked as not completed.
   *
   * @param eval the {@link AIEvaluation} to use for the feedback data
   */
  public void updateFeedback(AIEvaluation eval) {
    if (feedbackData == null || feedbackData.isEmpty()) {
      return;
    }

    final UUID targetDid = eval.getAnswer().getQuestion().getQuestionDid();

    for (final Object feedbackDatum : feedbackData) {
      @SuppressWarnings("unchecked")
      final Map<String, Object> item = (Map<String, Object>) feedbackDatum;
      final UUID did = UUID.fromString((String) item.get("questionDid"));
      if (targetDid.equals(did)) {
        item.put("isCorrect", !eval.isAnswerCorrect());
        break;
      }
    }

    totalCorrect = eval.isAnswerCorrect() ? (totalCorrect - 1) : (totalCorrect + 1);
    percentage = Math.round((100.0 * totalCorrect / totalQuestions) * 100.0) / 100.0;
  }
}
