package backend.service.mapper;

import backend.controller.dto.MultiChoiceQuestionDTO;
import backend.controller.dto.QuestionDTO;
import backend.controller.dto.create.CreateMultiChoiceQuestionDTO;
import backend.controller.dto.create.CreateQuestionDTO;
import backend.model.AbstractQuestion;
import backend.model.MultiChoiceQuestion;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class MultiChoiceMapper implements QuestionMapper {

  @Override
  public Class<? extends CreateQuestionDTO> getSupportedType() {
    return CreateMultiChoiceQuestionDTO.class;
  }

  @Override
  public AbstractQuestion map(CreateQuestionDTO dto) {
    var specificDto = (CreateMultiChoiceQuestionDTO) dto;
    var question = new MultiChoiceQuestion();
    question.setQuestionText(specificDto.text());
    question.setOptions(specificDto.options());
    question.setCorrectIndex(specificDto.correctIndex());
    return question;
  }

  @Override
  public Class<? extends AbstractQuestion> getSupportedEntity() {
    return MultiChoiceQuestion.class;
  }

  @Override
  public QuestionDTO mapToDto(AbstractQuestion entity) {
    MultiChoiceQuestion q = (MultiChoiceQuestion) entity;
    return new MultiChoiceQuestionDTO(
        "MultiChoice",
        q.getQuestionSlug(),
        q.getQuestionText(),
        q.getOptions(),
        q.getCorrectIndex(),
        q.getQuestionExercise().getExerciseSlug());
  }

  @Override
  public void update(AbstractQuestion entity, CreateQuestionDTO dto) {
    if (!(entity instanceof MultiChoiceQuestion question)) {
      throw new IllegalArgumentException("Type mismatch");
    }

    var specificDto = (CreateMultiChoiceQuestionDTO) dto;

    if (StringUtils.isNotBlank(specificDto.text())) {
      question.setQuestionText(specificDto.text());
    }

    if (specificDto.options() != null && !specificDto.options().isEmpty()) {
      question.setOptions(specificDto.options());
    }

    if (specificDto.correctIndex() != null) {
      question.setCorrectIndex(specificDto.correctIndex());
    }
  }
}
