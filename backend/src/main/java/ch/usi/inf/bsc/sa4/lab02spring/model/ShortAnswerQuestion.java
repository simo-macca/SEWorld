package ch.usi.inf.bsc.sa4.lab02spring.model;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.FeedbackQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.FeedbackShortAnswerQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.ShortAnswerQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.service.AiQuestionService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a short answer question within an exercise.
 *
 * <p>This entity extends {@link AbstractQuestion} to encapsulate attributes specific to short
 * answer questions, including a collection of correct answers. The correct answers are stored as a
 * list of strings in a separate collection table ("short_answer_correct_answer") with a foreign key
 * relationship to the question.
 */
@Entity
public class ShortAnswerQuestion extends AbstractQuestion {

  /**
   * The list of correct answer(s) for the short answer question.
   *
   * <p>This collection is eagerly fetched and mapped to the "short_answer_correct_answer" table.
   */
  @Column(name = "correct_answer", columnDefinition = "TEXT")
  private String correctAnswer;

  /**
   * No-argument constructor.
   *
   * <p>Required by JPA for entity instantiation during data retrieval.
   */
  public ShortAnswerQuestion() {
    super();
  }

  /**
   * Constructs a new {@code ShortAnswerQuestion} with the specified title, associated exercise, and
   * correct answer(s).
   *
   * @param questionTitle the title of the question
   * @param exerciseId the {@link Exercise} to which this question belongs
   * @param correctAnswer a list of correct answers for the question
   * @param variant the {@link Variant} associated with the question
   */
  public ShortAnswerQuestion(
      String questionTitle, Exercise exerciseId, String correctAnswer, Variant variant) {
    super(questionTitle, exerciseId, variant);
    this.correctAnswer = correctAnswer;
  }

  /**
   * Returns a copy of the list of correct answers for this short answer question.
   *
   * @return a new {@link ArrayList} containing the correct answers
   */
  public String getCorrectAnswer() {
    return correctAnswer;
  }

  /**
   * Sets the correct answer(s) for this short answer question.
   *
   * @param correctAnswer a list of correct answers to be set
   */
  public void setCorrectAnswer(String correctAnswer) {
    this.correctAnswer = correctAnswer;
  }

  /**
   * Converts this {@code ShortAnswerQuestion} entity into a Data Transfer Object (DTO).
   *
   * <p>The conversion may include the correct answers based on the provided role. If {@code role}
   * is {@code true}, the DTO will contain the complete question details; if {@code false}, the
   * correct answers will be omitted (set to {@code null}) in the DTO.
   *
   * @param role a boolean flag indicating the context for conversion.
   * @return a {@link ShortAnswerQuestionDTO} representing the question data
   */
  @Override
  public ShortAnswerQuestionDTO convertToDTO(boolean role) {
    final ShortAnswerQuestionDTO dto;
    if (role) {
      dto = new ShortAnswerQuestionDTO(this);
    } else {
      final ShortAnswerQuestion newSHA =
          new ShortAnswerQuestion(
              this.getQuestionTitle(), this.exerciseId, null, this.getVariant());
      newSHA.setQuestionDid(this.getQuestionDid());
      dto = new ShortAnswerQuestionDTO(newSHA);
    }
    return dto;
  }

  @Override
  public FeedbackQuestionDTO<String> provideFeedbackForAnswer(
      Answer submittedAnswer, AiQuestionService aiQuestionService, Object principal) {
    final String correctAnswers = correctAnswer;

    final boolean isMatch =
        aiQuestionService.isCorrect(questionTitle, submittedAnswer, correctAnswers, principal);
    return new FeedbackShortAnswerQuestionDTO(questionDid, isMatch, correctAnswers);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ShortAnswerQuestion that)) {
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
    return "ShortAnswerQuestion{" + "correctAnswer=" + correctAnswer + '}';
  }

  @Override
  public String getStringAnswer() {
    return getCorrectAnswer();
  }

  @Override
  public String getStringAnswerByUserAnswer(String userAnswer) {
    return userAnswer;
  }

  @Override
  public boolean isSha() {
    return true;
  }
}
