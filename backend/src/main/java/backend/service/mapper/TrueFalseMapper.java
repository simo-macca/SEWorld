package backend.service.mapper;

import backend.controller.dto.QuestionDTO;
import backend.controller.dto.TrueFalseQuestionDTO;
import backend.controller.dto.create.CreateQuestionDTO;
import backend.controller.dto.create.CreateTrueFalseQuestionDTO;
import backend.model.AbstractQuestion;
import backend.model.TrueFalseQuestion;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class TrueFalseMapper implements QuestionMapper {

  @Override
  public Class<? extends CreateQuestionDTO> getSupportedType() {
    return CreateTrueFalseQuestionDTO.class;
  }

  @Override
  public AbstractQuestion map(CreateQuestionDTO dto) {
    var specificDto = (CreateTrueFalseQuestionDTO) dto;
    var question = new TrueFalseQuestion();
    question.setQuestionText(specificDto.text());
    question.setCorrectBoolean(specificDto.correctBoolean());
    return question;
  }

  @Override
  public Class<? extends AbstractQuestion> getSupportedEntity() {
    return TrueFalseQuestion.class;
  }

  @Override
  public QuestionDTO mapToDto(AbstractQuestion entity) {
    TrueFalseQuestion q = (TrueFalseQuestion) entity;
    return new TrueFalseQuestionDTO(
        "TrueFalse",
        q.getQuestionSlug(),
        q.getQuestionText(),
        q.isCorrectBoolean(),
        q.getQuestionExercise().getExerciseSlug());
  }

  @Override
  public void update(AbstractQuestion entity, CreateQuestionDTO dto) {
    if (!(entity instanceof TrueFalseQuestion question)) {
      throw new IllegalArgumentException("Type mismatch");
    }
    var specificDto = (CreateTrueFalseQuestionDTO) dto;

    if (StringUtils.isNotBlank(specificDto.text())) {
      question.setQuestionText(specificDto.text());
    }

    if (specificDto.correctBoolean() != null) {
      question.setCorrectBoolean(specificDto.correctBoolean());
    }
  }
}
