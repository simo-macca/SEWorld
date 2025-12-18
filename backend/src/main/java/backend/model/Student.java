package backend.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Student")
public class Student extends AbstractUser {

  protected Student() {}

  public Student(String name, String email, String userDid) {
    super(name, email, userDid);
  }

  @Override
  public String getRole() {
    return "Student";
  }
}
