package backend.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "topics", uniqueConstraints = @UniqueConstraint(columnNames = "topic_slug"))
public class Topic {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "topic_did", updatable = false, nullable = false)
  private UUID topicDid;

  @Column(name = "topic_title", nullable = false, columnDefinition = "TEXT")
  private String topicTitle;

  @Column(name = "topic_description", columnDefinition = "TEXT")
  private String topicDescription;

  @Column(name = "topic_slug", nullable = false, unique = true)
  private String topicSlug;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "topic_owner", updatable = false, nullable = false)
  private Instructor topicOwner;

  protected Topic() {}

  public Topic(String topicTitle, String topicDescription, Instructor topicOwner) {
    this.topicTitle = topicTitle;
    this.topicDescription = topicDescription;
    this.topicOwner = topicOwner;
  }

  @PrePersist
  @PreUpdate
  private void generateSlug() {
    if (topicTitle != null) {
      this.topicSlug =
          topicTitle.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
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

  public String getTopicSlug() {
    return topicSlug;
  }

  public void setTopicSlug(String slug) {
    this.topicSlug = slug;
  }

  public Instructor getTopicOwner() {
    return topicOwner;
  }

  public void setTopicOwner(Instructor topicOwner) {
    this.topicOwner = topicOwner;
  }
}
