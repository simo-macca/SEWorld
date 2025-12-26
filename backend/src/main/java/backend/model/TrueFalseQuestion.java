package backend.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("TrueFalse")
public class TrueFalseQuestion extends AbstractQuestion {

  @Column(name = "correct_boolean")
  private boolean correctBoolean;

  public TrueFalseQuestion() {}

  public TrueFalseQuestion(String questionText, Exercise questionExercise, boolean correctBoolean) {
    super(questionText, questionExercise);
    this.correctBoolean = correctBoolean;
  }

  public boolean isCorrectBoolean() {
    return correctBoolean;
  }

  public void setCorrectBoolean(boolean correctBoolean) {
    this.correctBoolean = correctBoolean;
  }
}
