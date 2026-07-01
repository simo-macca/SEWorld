package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;

/**
 * Data Transfer Object (DTO) representing a user.
 *
 * <p>This record encapsulates the essential information of a user, including their unique
 * identifier, name, email address, and role.
 *
 * @param did the unique identifier of the user
 * @param name the name of the user
 * @param email the email address of the user
 * @param role the role assigned to the user (e.g., "admin", "student")
 */
public record UserDTO(String did, String name, String email, String role) {

  /**
   * Constructs a {@code UserDTO} from an {@code AbstractUser} model instance.
   *
   * <p>This constructor maps the properties of the {@code AbstractUser} model to the corresponding
   * components of the DTO.
   *
   * @param user the {@code AbstractUser} model instance
   */
  public UserDTO(AbstractUser user) {
    this(user.getDid().toString(), user.getName(), user.getEmail(), user.getRole());
  }
}
