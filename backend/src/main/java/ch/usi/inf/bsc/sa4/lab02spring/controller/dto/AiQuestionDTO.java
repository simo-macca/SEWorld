package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

/**
 * Data Transfer Object (DTO) representing a question related to the highlighted text. This record
 * encapsulates the following details: - A segment of text that is highlighted for contextual
 * reference. - A question generated or associated with the highlighted text.
 *
 * @param highlightedText the portion of text that is highlighted, serving as the context for the
 *     question
 * @param question the question associated with the highlighted text
 */
public record AiQuestionDTO(String highlightedText, String question) {}
