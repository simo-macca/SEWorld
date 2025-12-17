package backend.model;

import backend.controller.dto.CreateExerciseDTO;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import java.time.LocalDate;
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

  @Column(name = "exercise_is_draft", nullable = false)
  private boolean exerciseIsDraft = true;

  @Column(name = "exercise_is_completed", nullable = false)
  private boolean exerciseIsCompleted = false;

  @CreationTimestamp
  @Column(name = "exercise_created_date", nullable = false, updatable = false)
  private LocalDate exerciseCreatedDate;

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

  public void update(CreateExerciseDTO createExerciseDTO) {
    if (StringUtils.isNotBlank(createExerciseDTO.title())) {
      this.exerciseTitle = createExerciseDTO.title();
    }
    if (StringUtils.isNotBlank(createExerciseDTO.description())) {
      this.exerciseDescription = createExerciseDTO.description();
    }
  }

  public void publish() {
    this.exerciseIsDraft = false;
  }

  public UUID getExerciseDid() {
    return exerciseDid;
  }

  public void setExerciseDid(UUID exerciseDid) {
    this.exerciseDid = exerciseDid;
  }

  public String getExerciseTitle() {
    return exerciseTitle;
  }

  public void setExerciseTitle(String exerciseTitle) {
    this.exerciseTitle = exerciseTitle;
  }

  public String getExerciseDescription() {
    return exerciseDescription;
  }

  public void setExerciseDescription(String exerciseDescription) {
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

  public LocalDate getExerciseCreatedDate() {
    return exerciseCreatedDate;
  }

  public void setExerciseCreatedDate(LocalDate exerciseCreatedDate) {
    this.exerciseCreatedDate = exerciseCreatedDate;
  }

  public String getExerciseSlug() {
    return exerciseSlug;
  }

  public void setExerciseSlug(String slug) {
    this.exerciseSlug = slug;
  }

  public Instructor getExerciseOwner() {
    return exerciseOwner;
  }

  public void setExerciseOwner(Instructor exerciseOwner) {
    this.exerciseOwner = exerciseOwner;
  }
}
