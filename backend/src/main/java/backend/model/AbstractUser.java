package backend.model;

import static jakarta.persistence.InheritanceType.SINGLE_TABLE;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "role")
@Table(name = "users")
public abstract class AbstractUser {

  @Id
  @GeneratedValue
  @Column(name = "user_id", updatable = false, nullable = false, unique = true)
  private long userId;

  @Column(name = "user_did", updatable = false, nullable = false, unique = true)
  private String userDid;

  @Column(
      name = "name",
      updatable = false,
      nullable = false,
      unique = true,
      columnDefinition = "TEXT")
  private String name;

  @Column(
      name = "email",
      updatable = false,
      nullable = false,
      unique = true,
      columnDefinition = "TEXT")
  private String email;

  protected AbstractUser() {}

  public AbstractUser(String name, String email, String userDid) {
    this.name = name;
    this.email = email;
    this.userDid = userDid;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getUserDid() {
    return userDid;
  }

  public void setUserDid(String userDid) {
    this.userDid = userDid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public abstract String getRole();
}
