package backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("Instructor")
public class Instructor extends AbstractUser {

  @OneToMany(
      mappedBy = "topicOwner",
      fetch = FetchType.LAZY,
      cascade = CascadeType.REMOVE,
      orphanRemoval = true)
  private List<Topic> ownedTopics = new ArrayList<>();

  protected Instructor() {}

  public Instructor(String name, String email, String userDid) {
    super(name, email, userDid);
  }

  @Override
  public String getRole() {
    return "Instructor";
  }

  public List<Topic> getOwnedTopics() {
    return ownedTopics;
  }

  public void setOwnedTopics(List<Topic> ownedTopics) {
    this.ownedTopics = ownedTopics;
  }
}
