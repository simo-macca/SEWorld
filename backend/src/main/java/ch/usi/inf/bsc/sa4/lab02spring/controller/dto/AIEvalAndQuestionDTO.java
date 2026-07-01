package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractQuestion;
import java.util.List;

/**
 * A data transfer object (DTO) that encapsulates the title and correct answer of a question, along
 * with a list of AI-generated evaluations related to that question.
 *
 * <p>This record serves as a container for transferring question details and associated AI
 * evaluations between different layers of the application.
 *
 * @param questionTitle the title of the question
 * @param questionCorrectAnswer the correct answer to the question
 * @param listOfAIEvaluations a list of AI evaluation DTOs associated with the question
 */
public record AIEvalAndQuestionDTO(
    String questionTitle, String questionCorrectAnswer, List<AIEvalDTO> listOfAIEvaluations) {

  /**
   * Constructs an {@code AIEvalAndQuestionDTO} using an {@code AbstractQuestion} instance and a
   * list of AI evaluations.
   *
   * @param question the question object from which to extract the title and correct answer
   * @param list the list of AI evaluation DTOs related to the question
   */
  public AIEvalAndQuestionDTO(AbstractQuestion question, List<AIEvalDTO> list) {
    this(question.getQuestionTitle(), question.getStringAnswer(), list);
  }
}
