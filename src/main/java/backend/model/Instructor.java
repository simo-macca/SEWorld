package backend.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@DiscriminatorValue("Instructor")
public class Instructor extends AbstractUser {

  @OneToMany(mappedBy = "topicOwner", fetch = FetchType.LAZY)
  private List<Topic> ownedTopics = new ArrayList<>();

  protected Instructor() {}

  public Instructor(String name, String email, UUID userDid) {
    super(name, email, userDid);
  }

  @Override
  public String getRole() {
    return this.getClass().getSimpleName();
  }

  public List<Topic> getOwnedTopics() {
    return ownedTopics;
  }

  public void setOwnedTopics(List<Topic> ownedTopics) {
    this.ownedTopics = ownedTopics;
  }
}
