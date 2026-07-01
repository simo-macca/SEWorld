package ch.usi.inf.bsc.sa4.lab02spring.model;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.FeedbackMultiChoiceQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.FeedbackQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.MultiChoiceQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.service.AiQuestionService;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.FetchType.EAGER;

/**
 * Represents a multiple-choice question within an exercise.
 *
 * <p>This entity extends the {@link AbstractQuestion} class and encapsulates additional details
 * specific to multiple-choice questions including the list of answer choices and the correct
 * answer(s). It is mapped to the underlying database through JPA annotations.
 */
@Entity
public class MultiChoiceQuestion extends AbstractQuestion {

  /**
   * The list of answer choices for this multiple-choice question.
   *
   * <p>This collection is eagerly fetched and stored in a separate table named
   * "multi_choice_question_choices" with a foreign key referencing this question. Each choice is
   * represented as a non-null string.
   */
  @ElementCollection(fetch = EAGER)
  @CollectionTable(
      name = "multi_choice_question_choices",
      joinColumns = @JoinColumn(name = "question_id"))
  @Column(name = "choice", nullable = false)
  private List<String> choices;

  /**
   * The correct answer(s) for this multiple-choice question.
   *
   * <p>This field stores the correct answer as an integer. Depending on the implementation, this
   * may represent the index of the correct option or a reference to a set of correct answers.
   */
  @Column(nullable = false)
  private Integer correctAnswer;

  /**
   * No-argument constructor.
   *
   * <p>Required by the JPA provider for entity instantiation during data retrieval.
   */
  public MultiChoiceQuestion() {
    super();
  }

  /**
   * Constructs a new {@code MultiChoiceQuestion} with the specified details.
   *
   * @param questionTitle the title of the question
   * @param choices the list of answer choices for the question
   * @param correctAnswer the correct answer(s) for the question
   * @param exerciseId the {@link Exercise} associated with this question
   * @param variant the {@link Variant} associated with this question
   */
  public MultiChoiceQuestion(
      String questionTitle,
      List<String> choices,
      Integer correctAnswer,
      Exercise exerciseId,
      Variant variant) {
    super(questionTitle, exerciseId, variant);
    this.choices = new ArrayList<>(choices);
    this.correctAnswer = correctAnswer;
  }

  /**
   * Returns a copy of the list of choices for this question.
   *
   * @return a new {@link ArrayList} containing the answer choices
   */
  public List<String> getChoices() {
    return new ArrayList<>(choices);
  }

  /**
   * Sets the answer choices for this question.
   *
   * @param choices the list of answer choices to set
   */
  @Override
  public void setChoices(List<String> choices) {
    this.choices = new ArrayList<>(choices);
  }

  /**
   * Returns the correct answer(s) for this question.
   *
   * @return the correct answer(s) as an {@link Integer}
   */
  public Integer getCorrectAnswer() {
    return correctAnswer;
  }

  /**
   * Sets the correct answer(s) for this question.
   *
   * @param correctAnswers the correct answer(s) to set
   */
  public void setCorrectAnswer(Integer correctAnswers) {
    this.correctAnswer = correctAnswers;
  }

  /**
   * Converts this {@code MultiChoiceQuestion} entity into a Data Transfer Object (DTO).
   *
   * <p>Depending on the role provided, the conversion may either include the correct answer(s) or
   * omit them (for example, when returning the question for a student view).
   *
   * @param role a boolean flag indicating whether to include the correct answer(s) (true for an
   *     instructor role, false for a student role)
   * @return a {@link MultiChoiceQuestionDTO} representing this question
   */
  public MultiChoiceQuestionDTO convertToDTO(boolean role) {
    final MultiChoiceQuestionDTO dto;
    if (role) {
      dto = new MultiChoiceQuestionDTO(this);
    } else {
      final MultiChoiceQuestion newMCH =
          new MultiChoiceQuestion(
              this.getQuestionTitle(),
              this.getChoices(),
              null,
              this.getExercise(),
              this.getVariant());
      newMCH.setQuestionDid(this.getQuestionDid());
      dto = new MultiChoiceQuestionDTO(newMCH);
    }
    return dto;
  }

  @Override
  public FeedbackQuestionDTO<Integer> provideFeedbackForAnswer(
      Answer submittedAnswer, AiQuestionService aiQuestionService, Object principal) {
    final String answerContent = submittedAnswer.getAnswerContent();
    final Integer correct = correctAnswer;
    return new FeedbackMultiChoiceQuestionDTO(
        questionDid, answerContent.equals(correct.toString()), correct);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof MultiChoiceQuestion that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return Objects.equals(choices, that.choices)
        && Objects.equals(correctAnswer, that.correctAnswer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), choices, correctAnswer);
  }

  @Override
  public String toString() {
    return "MultiChoiceQuestion{" + "choices=" + choices + ", correctAnswer=" + correctAnswer + '}';
  }

  @Override
  public boolean isMch() {
    return true;
  }

  @Override
  public String getStringAnswer() {
    return this.choices.get(this.correctAnswer);
  }

  @Override
  public String getStringAnswerByUserAnswer(String userAnswer) {
    return this.choices.get(Integer.parseInt(userAnswer));
  }

  @Override
  public MultiChoiceQuestion getMch() {
    return this;
  }
}
