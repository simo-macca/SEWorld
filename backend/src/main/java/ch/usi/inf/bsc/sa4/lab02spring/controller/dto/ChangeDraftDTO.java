package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

/**
 * Data Transfer Object (DTO) for updating the draft status of an exercise.
 *
 * <p>This DTO encapsulates a boolean value indicating the new draft status of an exercise. It is
 * used when changing an exercise's draft state.
 *
 * @param exerciseIsDraft the new draft status for the exercise; {@code true} if the exercise should
 *     be in draft mode, {@code false} otherwise.
 */
public record ChangeDraftDTO(boolean exerciseIsDraft) {}
