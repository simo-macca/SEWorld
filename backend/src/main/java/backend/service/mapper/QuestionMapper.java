package backend.service.mapper;

import backend.controller.dto.QuestionDTO;
import backend.controller.dto.create.CreateQuestionDTO;
import backend.model.AbstractQuestion;

public interface QuestionMapper {
  Class<? extends CreateQuestionDTO> getSupportedType();

  AbstractQuestion map(CreateQuestionDTO dto);

  Class<? extends AbstractQuestion>
      getSupportedEntity(); // To know which Entity this mapper handles

  QuestionDTO mapToDto(AbstractQuestion entity); // The actual conversion

  void update(AbstractQuestion entity, CreateQuestionDTO dto);
}
