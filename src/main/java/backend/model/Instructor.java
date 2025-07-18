package backend.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.UUID;

@Entity
@DiscriminatorValue("Instructor")
public class Instructor extends AbstractUser {

  protected Instructor() {}

  public Instructor(String name, String email, UUID userDid) {
    super(name, email, userDid);
  }

  @Override
  public String getRole() {
    return this.getClass().getSimpleName();
  }
}
