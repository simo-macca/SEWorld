package backend.service.mapper;

import backend.controller.dto.QuestionDTO;
import backend.controller.dto.ShortAnswerQuestionDTO;
import backend.controller.dto.create.CreateQuestionDTO;
import backend.controller.dto.create.CreateShortAnswerQuestionDTO;
import backend.model.AbstractQuestion;
import backend.model.ShortAnswerQuestion;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ShortAnswerMapper implements QuestionMapper {

  @Override
  public Class<? extends CreateQuestionDTO> getSupportedType() {
    return CreateShortAnswerQuestionDTO.class;
  }

  @Override
  public AbstractQuestion map(CreateQuestionDTO dto) {
    var specificDto = (CreateShortAnswerQuestionDTO) dto;
    var question = new ShortAnswerQuestion();
    question.setQuestionText(specificDto.text());
    question.setCorrectText(specificDto.correctText());
    return question;
  }

  @Override
  public Class<? extends AbstractQuestion> getSupportedEntity() {
    return ShortAnswerQuestion.class;
  }

  @Override
  public QuestionDTO mapToDto(AbstractQuestion entity) {
    ShortAnswerQuestion q = (ShortAnswerQuestion) entity;
    return new ShortAnswerQuestionDTO(
        "ShortAnswer",
        q.getQuestionSlug(),
        q.getQuestionText(),
        q.getCorrectText(),
        q.getQuestionExercise().getExerciseSlug());
  }

  @Override
  public void update(AbstractQuestion entity, CreateQuestionDTO dto) {
    if (!(entity instanceof ShortAnswerQuestion question)) {
      throw new IllegalArgumentException("Type mismatch");
    }
    var specificDto = (CreateShortAnswerQuestionDTO) dto;

    if (StringUtils.isNotBlank(specificDto.text())) {
      question.setQuestionText(specificDto.text());
    }
    if (StringUtils.isNotBlank(specificDto.correctText())) {
      question.setCorrectText(specificDto.correctText());
    }
  }
}
