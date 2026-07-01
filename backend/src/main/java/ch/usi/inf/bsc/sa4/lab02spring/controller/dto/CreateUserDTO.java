package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

/**
 * Represents a Data Transfer Object (DTO) for creating a new user.
 *
 * <p>This immutable record encapsulates the user's name, email address, and subscription identifier
 *
 * @param name the full name of the user
 * @param email the email address of the user
 * @param subId the subscription or subject identifier associated with the user
 */
public record CreateUserDTO(String name, String email, String subId) {}
