package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

/**
 * Data Transfer Object (DTO) representing a rate question input. This record encapsulates the
 * rating value given to a question, typically for evaluation or feedback purposes.
 *
 * @param rate the rating value assigned to a question
 */
public record RateQuestionDTO(int rate) {}
