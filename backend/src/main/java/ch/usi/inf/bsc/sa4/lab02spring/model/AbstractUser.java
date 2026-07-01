package ch.usi.inf.bsc.sa4.lab02spring.model;

import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for all user types in the system.
 *
 * <p>This class is mapped to the "users" table using the single-table inheritance strategy.
 * Different types of users (e.g., Student, Instructor) are distinguished by the discriminator
 * column {@code user_type}. It defines common attributes for all users, such as a unique identifier
 * (ID), a decentralized identifier (DID), subject identifier (subId), name, and email.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
public abstract class AbstractUser {

  /**
   * The primary key identifier of the user.
   *
   * <p>This identifier is generated using the IDENTITY strategy.
   */
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  /**
   * The unique decentralized identifier (DID) of the user.
   *
   * <p>This value is generated automatically and is immutable.
   */
  @Column(name = "did", updatable = false, nullable = false, unique = true)
  private UUID did;

  /**
   * The subject identifier (subId) of the user.
   *
   * <p>Typically used to reference an external authentication provider's user id.
   */
  @Column(name = "sub_id", nullable = false, unique = true)
  private String subId;

  /** The name of the user. */
  @Column(nullable = false, length = 100)
  private String name;

  /** The email address of the user. */
  @Column(nullable = false, unique = true)
  private String email;

  /** Default no-argument constructor required by JPA. */
  protected AbstractUser() {}

  /**
   * Constructs a new {@code AbstractUser} with the specified subject identifier.
   *
   * <p>A new decentralized identifier (DID) is generated automatically.
   *
   * @param subId the subject identifier for the user
   */
  protected AbstractUser(String subId) {
    this.did = UUID.randomUUID();
    this.subId = subId;
  }

  /**
   * Constructs a new {@code AbstractUser} with the specified subject identifier, name, and email.
   *
   * @param subId the subject identifier for the user
   * @param name the name of the user
   * @param email the email address of the user
   */
  protected AbstractUser(String subId, String name, String email) {
    this(subId);
    this.name = name;
    this.email = email;
  }

  /**
   * Returns the role of the user.
   *
   * <p>This method is abstract and must be implemented by subclasses to return a string
   * representing the user's role (e.g., "STUDENT", "INSTRUCTOR").
   *
   * @return the user's role
   */
  public abstract String getRole();

  /**
   * Returns the primary key identifier of the user.
   *
   * @return the user's ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Returns the decentralized identifier (DID) of the user.
   *
   * @return the user's DID
   */
  public UUID getDid() {
    return did;
  }

  /**
   * Sets the decentralized identifier (DID) of the user.
   *
   * <p>Typically, this method should only be used internally since the DID is immutable once
   * generated.
   *
   * @param did the new DID to set
   */
  public void setDid(UUID did) {
    this.did = did;
  }

  /**
   * Returns the subject identifier of the user.
   *
   * @return the user's subject identifier (subId)
   */
  public String getSubId() {
    return subId;
  }

  /**
   * Returns the user's name.
   *
   * @return the name of the user
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the user's name.
   *
   * @param name the new name of the user
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the user's email address.
   *
   * @return the email address of the user
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the user's email address.
   *
   * @param email the new email address for the user
   */
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AbstractUser user)) {
      return false;
    }
    return Objects.equals(id, user.id)
        && Objects.equals(did, user.did)
        && Objects.equals(subId, user.subId)
        && Objects.equals(name, user.name)
        && Objects.equals(email, user.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, did, subId, name, email);
  }

  /**
   * Returns a string representation of the user.
   *
   * @return a string containing the user's details including id, DID, subId, name, and email
   */
  @Override
  public String toString() {
    return "AbstractUser{"
        + "id="
        + id
        + ", did="
        + did
        + ", subId='"
        + subId
        + '\''
        + ", name='"
        + name
        + '\''
        + ", email='"
        + email
        + '\''
        + '}';
  }

  /**
   * Throws an {@link UnsupportedOperationException} since this method is not applicable to
   * instructors.
   *
   * @return a {@link Student}
   * @throws UnsupportedOperationException always
   */
  public Student getStudent() {
    throw new UnsupportedOperationException("Not a student.");
  }

  /**
   * Throws an {@link UnsupportedOperationException} since this method is not applicable to
   * students.
   *
   * @return a {@link Instructor}
   * @throws UnsupportedOperationException always
   */
  public Instructor getInstructor() {
    throw new UnsupportedOperationException("Not an instructor.");
  }
}
