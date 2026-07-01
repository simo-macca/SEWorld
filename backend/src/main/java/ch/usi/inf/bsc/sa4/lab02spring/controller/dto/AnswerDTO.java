package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import ch.usi.inf.bsc.sa4.lab02spring.model.Answer;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) for conveying information about an answer.
 *
 * <p>This record encapsulates key details of an answer, including its unique decentralized
 * identifier (DID), the answer content, and the identifiers for the associated attempt and
 * question.
 *
 * @param did the unique decentralized identifier (UUID) of the answer
 * @param answerContent the content of the answer as a String
 * @param attemptDid the decentralized identifier (UUID) of the associated attempt
 * @param questionDid the decentralized identifier (UUID) of the associated question
 */
public record AnswerDTO(UUID did, String answerContent, UUID attemptDid, UUID questionDid) {

  /**
   * Constructs an {@code AnswerDTO} by extracting fields from an {@link Answer} entity.
   *
   * @param answer the {@link Answer} entity from which to create the DTO
   */
  public AnswerDTO(Answer answer) {
    this(
        answer.getDid(),
        answer.getAnswerContent(),
        answer.getAttemptDid(),
        answer.getQuestionDid());
  }
}
