package ch.usi.inf.bsc.sa4.lab02spring.model;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.FeedbackQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.FeedbackTrueFalseQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.TrueFalseQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.service.AiQuestionService;
import jakarta.persistence.Entity;

import java.util.Objects;

/**
 * Represents a True/False question in an exercise.
 *
 * <p>This entity extends {@link AbstractQuestion} and adds the specific property of a correct
 * answer, represented as a Boolean value. It provides methods to retrieve and update the correct
 * answer, and to convert the entity into a corresponding Data Transfer Object (DTO) for external
 * exposure.
 */
@Entity
public class TrueFalseQuestion extends AbstractQuestion {

  /** The correct answer for this true/false question. */
  private Boolean correctAnswer;

  /**
   * No-args constructor.
   *
   * <p>Required by JPA for entity instantiation during data retrieval.
   */
  public TrueFalseQuestion() {
    super();
  }

  /**
   * Constructs a new {@code TrueFalseQuestion} with the specified title, associated exercise, and
   * correct answer.
   *
   * @param questionTitle the title of the question
   * @param exerciseId the {@link Exercise} to which this question belongs
   * @param correctAnswer the correct answer (true or false) for the question
   * @param variant the {@link Variant} associated with the question
   */
  public TrueFalseQuestion(
      String questionTitle, Exercise exerciseId, Boolean correctAnswer, Variant variant) {
    super(questionTitle, exerciseId, variant);
    this.correctAnswer = correctAnswer;
  }

  /**
   * Returns the correct answer for this true/false question.
   *
   * @return a {@link Boolean} indicating the correct answer
   */
  public Boolean isCorrectAnswer() {
    return correctAnswer;
  }

  /**
   * Sets the correct answer for this true/false question.
   *
   * @param correctAnswer the correct answer (true or false) to set
   */
  public void setCorrectAnswer(Boolean correctAnswer) {
    this.correctAnswer = correctAnswer;
  }

  /**
   * Converts this {@code TrueFalseQuestion} entity into a {@link TrueFalseQuestionDTO}.
   *
   * <p>Depending on the specified role, the conversion may include or omit the correct answer: If
   * {@code role} is {@code true}, the DTO is generated with the correct answer. If {@code role} is
   * {@code false}, the DTO is generated without exposing the correct answer (set to {@code null}).
   *
   * @param role a boolean flag indicating whether to include the correct answer in the DTO (true
   *     for instructor view, false for student view)
   * @return a {@link TrueFalseQuestionDTO} representing this question
   */
  @Override
  public TrueFalseQuestionDTO convertToDTO(boolean role) {
    final TrueFalseQuestionDTO dto;
    if (role) {
      dto = new TrueFalseQuestionDTO(this);
    } else {
      final TrueFalseQuestion newTF =
          new TrueFalseQuestion(
              this.getQuestionTitle(), this.getExercise(), null, this.getVariant());
      newTF.setQuestionDid(this.getQuestionDid());
      dto = new TrueFalseQuestionDTO(newTF);
    }
    return dto;
  }

  @Override
  public FeedbackQuestionDTO<Boolean> provideFeedbackForAnswer(
      Answer submittedAnswer, AiQuestionService aiQuestionService, Object principal) {
    final String answerContent = submittedAnswer.getAnswerContent();
    final Boolean isCorrect = correctAnswer;
    return new FeedbackTrueFalseQuestionDTO(
        questionDid, answerContent.equalsIgnoreCase(Boolean.toString(isCorrect)), isCorrect);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof TrueFalseQuestion that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return Objects.equals(correctAnswer, that.correctAnswer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), correctAnswer);
  }

  @Override
  public String toString() {
    return "TrueFalseQuestion{" + "correctAnswer=" + correctAnswer + '}';
  }

  @Override
  public String getStringAnswer() {
    return isCorrectAnswer().toString();
  }

  @Override
  public String getStringAnswerByUserAnswer(String userAnswer) {
    return userAnswer;
  }
}
