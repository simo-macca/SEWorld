package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * A Data Transfer Object (DTO) used to encapsulate the information required to create a new
 * comment.
 *
 * <p>Includes validation constraints to ensure the comment is not blank and does not exceed 5000
 * characters.
 *
 * @param comment the textual content of the comment to be created; must be non-blank and no longer
 *     than 5000 characters
 */
public record CreateCommentDTO(
    @NotBlank(message = "Comment must not be blank")
        @Size(max = 5000, message = "Comment cannot exceed 5000 characters")
        String comment) {}
