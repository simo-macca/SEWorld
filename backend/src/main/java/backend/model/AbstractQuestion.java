package backend.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "questions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class AbstractQuestion {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "question_did", updatable = false, nullable = false)
  private UUID questionDid;

  @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
  private String questionText;

  @Column(name = "question_slug", nullable = false, unique = true)
  private String questionSlug;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_exercise", updatable = false, nullable = false)
  private Exercise questionExercise;

  protected AbstractQuestion() {}

  public AbstractQuestion(String questionText, Exercise questionExercise) {
    this.questionText = questionText;
    this.questionExercise = questionExercise;
  }

  public UUID getQuestionDid() {
    return questionDid;
  }

  public void setQuestionDid(UUID questionDid) {
    this.questionDid = questionDid;
  }

  public String getQuestionText() {
    return questionText;
  }

  public void setQuestionText(String questionText) {
    this.questionText = questionText;
  }

  public String getQuestionSlug() {
    return questionSlug;
  }

  public void setQuestionSlug(String questionSlug) {
    this.questionSlug = questionSlug;
  }

  public Exercise getQuestionExercise() {
    return questionExercise;
  }

  public void setQuestionExercise(Exercise questionExercise) {
    this.questionExercise = questionExercise;
  }
}
