package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

/**
 * A data transfer object (DTO) representing the AI's evaluation of a short answer.
 *
 * <p>This record encapsulates whether the AI determined the answer to be correct, along with the
 * reasoning behind that evaluation.
 *
 * @param isCorrect indicates if the AI judged the answer as correct
 */
public record ShortAnswerAIReasoningResponseDTO(Boolean isCorrect) {}
