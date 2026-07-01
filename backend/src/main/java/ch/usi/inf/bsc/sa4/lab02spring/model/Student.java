package ch.usi.inf.bsc.sa4.lab02spring.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a student user within the system.
 *
 * <p>This entity extends the {@link AbstractUser} class and is identified by the discriminator
 * value "Student". It encapsulates additional attributes and behaviors specific to student users,
 * including their completion stage and the number of attempts they have made.
 */
@Entity
@DiscriminatorValue("Student")
public class Student extends AbstractUser {

  /**
   * The current completion stage of the student.
   *
   * <p>This value can be incremented or decremented to track the student's progress.
   */
  private int completionStage;

  /** The number of attempts made by the student. */
  private int numberAttempts;

  /**
   * The votes cast by the student.
   *
   * <p>This field represents a one-to-many relationship, linking the student to a set of votes.
   */
  @OneToMany(mappedBy = "studentId", cascade = CascadeType.ALL, orphanRemoval = true)
  protected Set<AIVoteStudent> votes = new HashSet<>();

  /**
   * Default constructor.
   *
   * <p>Required by JPA for entity instantiation.
   */
  public Student() {
    super();
  }

  /**
   * Constructs a new {@code Student} with the specified subscription identifier, name, and email.
   *
   * @param subId the subscription identifier associated with the student
   * @param name the name of the student
   * @param email the email address of the student
   */
  public Student(String subId, String name, String email) {
    super(subId, name, email);
  }

  /**
   * Returns the role of the user.
   *
   * <p>For a {@code Student}, this method always returns "STUDENT".
   *
   * @return the role "STUDENT"
   */
  public String getRole() {
    return "STUDENT";
  }

  /**
   * Gets the current completion stage of the student.
   *
   * @return the completion stage
   */
  public int getCompletionStage() {
    return completionStage;
  }

  /**
   * Sets the completion stage of the student.
   *
   * @param completionStage the new completion stage to set
   */
  public void setCompletionStage(int completionStage) {
    this.completionStage = completionStage;
  }

  /** Increments the completion stage of the student by one. */
  public void increaseCompletionStage() {
    this.completionStage++;
  }

  /** Decrements the completion stage of the student by one if it is greater than zero. */
  public void decreaseCompletionStage() {
    if (this.completionStage > 0) {
      this.completionStage--;
    }
  }

  /**
   * Gets the number of attempts made by the student.
   *
   * @return the number of attempts
   */
  public int getNumberAttempts() {
    return numberAttempts;
  }

  /**
   * Sets the number of attempts made by the student.
   *
   * @param numberAttempts the new number of attempts to set
   */
  public void setNumberAttempts(int numberAttempts) {
    this.numberAttempts = numberAttempts;
  }

  /** Increments the number of attempts made by the student by one. */
  public void increaseNumberAttempts() {
    this.numberAttempts++;
  }

  /** Decrements the number of attempts made by the student by one if it is greater than zero. */
  public void decreaseNumberAttempts() {
    if (this.numberAttempts > 0) {
      this.numberAttempts--;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Student student)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return completionStage == student.completionStage && numberAttempts == student.numberAttempts;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), completionStage, numberAttempts);
  }

  @Override
  public String toString() {
    return "Student{"
        + "completionStage="
        + completionStage
        + ", numberAttempts="
        + numberAttempts
        + '}';
  }

  @Override
  public Student getStudent() {
    return this;
  }
}
