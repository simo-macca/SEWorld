package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a question along with its correct answer and a list of
 * associated short answer responses.
 *
 * @param question_title The title or prompt of the question.
 * @param question_correct_answer The correct answer to the question.
 * @param shaResponseDTOs A list of {@link SHAResponseDTO} objects representing short answer
 *     responses.
 */
public record QuestionAndUserAnswerDTO(
    String question_title, String question_correct_answer, List<SHAResponseDTO> shaResponseDTOs) {}
