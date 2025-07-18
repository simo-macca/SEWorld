package backend.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.UUID;

@Entity
@DiscriminatorValue("Student")
public class Student extends AbstractUser {

  protected Student() {}

  public Student(String name, String email, UUID userDid) {
    super(name, email, userDid);
  }

  @Override
  public String getRole() {
    return this.getClass().getSimpleName();
  }
}
