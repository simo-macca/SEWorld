package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.UUID;

/**
 * A data transfer object (DTO) that encapsulates the evaluation of a user's answer by an AI system.
 *
 * <p>This record holds information about the user's response, the AI's reasoning, the correctness
 * of the answer, and a unique identifier for the AI evaluation.
 *
 * @param userName the name of the user who provided the answer
 * @param userAnswer the answer submitted by the user
 * @param aiReasoning the reasoning provided by the AI regarding the user's answer
 * @param isAnswerCorrect indicates whether the AI determined the user's answer to be correct
 * @param aiEvalDid the unique identifier for this AI evaluation
 */
public record AIEvalDTO(
    String userName,
    String userAnswer,
    String aiReasoning,
    boolean isAnswerCorrect,
    UUID aiEvalDid) {}
