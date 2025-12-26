package backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@DiscriminatorValue("MultiChoice")
public class MultiChoiceQuestion extends AbstractQuestion {

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "options", columnDefinition = "jsonb")
  @NotEmpty
  private List<String> options;

  @Column(name = "correct_index")
  private Integer correctIndex;

  public MultiChoiceQuestion() {}

  public MultiChoiceQuestion(
      String questionText, Exercise questionExercise, List<String> options, Integer correctIndex) {
    super(questionText, questionExercise);
    this.options = options;
    this.correctIndex = correctIndex;
  }

  public List<String> getOptions() {
    return options;
  }

  public void setOptions(List<String> options) {
    this.options = options;
  }

  public Integer getCorrectIndex() {
    return correctIndex;
  }

  public void setCorrectIndex(Integer correctIndex) {
    this.correctIndex = correctIndex;
  }
}
