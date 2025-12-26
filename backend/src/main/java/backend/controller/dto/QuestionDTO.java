package backend.controller.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = MultiChoiceQuestionDTO.class, name = "MultiChoice"),
  @JsonSubTypes.Type(value = TrueFalseQuestionDTO.class, name = "TrueFalse"),
  @JsonSubTypes.Type(value = ShortAnswerQuestionDTO.class, name = "ShortAnswer")
})
public sealed interface QuestionDTO
    permits MultiChoiceQuestionDTO, TrueFalseQuestionDTO, ShortAnswerQuestionDTO {
  String questionType();

  String questionSlug();

  String questionText();

  String exerciseSlug();
}
