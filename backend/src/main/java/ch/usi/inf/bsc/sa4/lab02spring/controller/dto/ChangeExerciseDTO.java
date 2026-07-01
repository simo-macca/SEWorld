package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

/**
 * Data Transfer Object (DTO) for updating an exercise's details.
 *
 * <p>This DTO encapsulates the new title and description for an exercise. It is used when updating
 * an exercise in draft mode.
 *
 * @param exerciseTitle the new title for the exercise.
 * @param exerciseDescription the new description for the exercise.
 */
public record ChangeExerciseDTO(String exerciseTitle, String exerciseDescription) {}
