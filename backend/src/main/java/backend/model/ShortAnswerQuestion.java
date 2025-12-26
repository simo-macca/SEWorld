package backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ShortAnswer")
public class ShortAnswerQuestion extends AbstractQuestion {

  @Column(name = "correct_text")
  private String correctText;

  public ShortAnswerQuestion() {}

  public ShortAnswerQuestion(String questionText, Exercise questionExercise, String correctText) {
    super(questionText, questionExercise);
    this.correctText = correctText;
  }

  public String getCorrectText() {
    return correctText;
  }

  public void setCorrectText(String correctText) {
    this.correctText = correctText;
  }
}
