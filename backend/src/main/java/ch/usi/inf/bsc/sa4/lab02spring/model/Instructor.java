package ch.usi.inf.bsc.sa4.lab02spring.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Represents an instructor within the system.
 *
 * <p>This entity is a specialization of the {@link AbstractUser} class and is distinguished by the
 * discriminator value "Instructor". It encapsulates any attributes or behavior specific to
 * instructors.
 */
@Entity
@DiscriminatorValue("Instructor")
public class Instructor extends AbstractUser {

  /**
   * Default constructor for JPA.
   *
   * <p>This no-argument constructor is required by the JPA provider for entity instantiation.
   */
  public Instructor() {
    super();
  }

  /**
   * Constructs a new {@code Instructor} with the specified subscription identifier, name, and
   * email.
   *
   * @param subId the subscription identifier associated with the instructor
   * @param name the name of the instructor
   * @param email the email address of the instructor
   */
  public Instructor(String subId, String name, String email) {
    super(subId, name, email);
  }

  /**
   * Retrieves the role of this user.
   *
   * <p>For an {@code Instructor}, this method always returns "INSTRUCTOR".
   *
   * @return a {@code String} representing the role "INSTRUCTOR"
   */
  public String getRole() {
    return "INSTRUCTOR";
  }

  @Override
  public Instructor getInstructor() {
    return this;
  }
}
