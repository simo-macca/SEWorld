package backend.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table
public class Topic {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_did", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column(name = "topic_did", updatable = false, nullable = false, unique = true)
  private UUID topicDid;

  @Column(name = "topic_title", nullable = false, columnDefinition = "TEXT")
  private String topicTitle;

  @Column(name = "topic_description", columnDefinition = "TEXT")
  private String topicDescription;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "topic_owner", updatable = false, nullable = false)
  private Instructor topicOwner;

  protected Topic() {}

  public Topic(String topicTitle, String topicDescription, Instructor topicOwner) {
    this.topicDid = UUID.randomUUID();
    this.topicTitle = topicTitle;
    this.topicDescription = topicDescription;
    this.topicOwner = topicOwner;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UUID getTopicDid() {
    return topicDid;
  }

  public void setTopicDid(UUID topicDid) {
    this.topicDid = topicDid;
  }

  public String getTopicTitle() {
    return topicTitle;
  }

  public void setTopicTitle(String topicTitle) {
    this.topicTitle = topicTitle;
  }

  public String getTopicDescription() {
    return topicDescription;
  }

  public void setTopicDescription(String topicDescription) {
    this.topicDescription = topicDescription;
  }

  public Instructor getTopicOwner() {
    return topicOwner;
  }

  public void setTopicOwner(Instructor topicOwner) {
    this.topicOwner = topicOwner;
  }
}
