package backend.controller.dto.create;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = CreateMultiChoiceQuestionDTO.class, name = "MultiChoice"),
  @JsonSubTypes.Type(value = CreateTrueFalseQuestionDTO.class, name = "TrueFalse"),
  @JsonSubTypes.Type(value = CreateShortAnswerQuestionDTO.class, name = "ShortAnswer")
})
public sealed interface CreateQuestionDTO
    permits CreateMultiChoiceQuestionDTO, CreateTrueFalseQuestionDTO, CreateShortAnswerQuestionDTO {
  String text();
}
