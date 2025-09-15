package backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "exercises", uniqueConstraints = @UniqueConstraint(columnNames = "exercise_slug"))
public class Exercise {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "exercise_did", updatable = false, nullable = false)
  private UUID exerciseDid;

  @Column(name = "exercise_title", nullable = false, columnDefinition = "TEXT")
  private String exerciseTitle;

  @Column(name = "exercise_description", columnDefinition = "TEXT")
  private String exerciseDescription;

  @Column(name = "exercise_is_draft", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
  private boolean exerciseIsDraft;

  @Column(
      name = "exercise_is_completed",
      nullable = false,
      columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean exerciseIsCompleted;

  @CreationTimestamp
  @Column(name = "exercise_created_date", nullable = false, updatable = false)
  private LocalDateTime exerciseCreatedDate;

  @Column(name = "exercise_slug", nullable = false, unique = true)
  private String exerciseSlug;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exercise_owner", updatable = false, nullable = false)
  private Instructor exerciseOwner;

  protected Exercise() {}

  public Exercise(String exerciseTitle, String exerciseDescription, Instructor exerciseOwner) {
    this.exerciseTitle = exerciseTitle;
    this.exerciseDescription = exerciseDescription;
    this.exerciseOwner = exerciseOwner;
  }

  @PrePersist
  @PreUpdate
  private void generateSlug() {
    if (exerciseTitle != null) {
      this.exerciseSlug =
          exerciseTitle
              .trim()
              .toLowerCase()
              .replaceAll("[^a-z0-9]+", "-")
              .replaceAll("(^-|-$)", "");
    }
  }

  public UUID getTopicDid() {
    return exerciseDid;
  }

  public void setTopicDid(UUID exerciseDid) {
    this.exerciseDid = exerciseDid;
  }

  public String getTopicTitle() {
    return exerciseTitle;
  }

  public void setTopicTitle(String exerciseTitle) {
    this.exerciseTitle = exerciseTitle;
  }

  public String getTopicDescription() {
    return exerciseDescription;
  }

  public void setTopicDescription(String exerciseDescription) {
    this.exerciseDescription = exerciseDescription;
  }

  public boolean IsDraft() {
    return exerciseIsDraft;
  }

  public void setExerciseIsDraft(boolean exerciseIsDraft) {
    this.exerciseIsDraft = exerciseIsDraft;
  }

  public boolean IsCompleted() {
    return exerciseIsCompleted;
  }

  public void setExerciseIsCompleted(boolean exerciseIsCompleted) {
    this.exerciseIsCompleted = exerciseIsCompleted;
  }

  public LocalDateTime getExerciseCreatedDate() {
    return exerciseCreatedDate;
  }

  public void setExerciseCreatedDate(LocalDateTime exerciseCreatedDate) {
    this.exerciseCreatedDate = exerciseCreatedDate;
  }

  public String getExerciseSlug() {
    return exerciseSlug;
  }

  public void setExerciseSlug(String slug) {
    this.exerciseSlug = slug;
  }

  public Instructor getTopicOwner() {
    return exerciseOwner;
  }

  public void setTopicOwner(Instructor exerciseOwner) {
    this.exerciseOwner = exerciseOwner;
  }
}
