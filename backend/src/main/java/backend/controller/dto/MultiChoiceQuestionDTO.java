package backend.controller.dto;

import java.util.List;

public record MultiChoiceQuestionDTO(
    String questionType,
    String questionSlug,
    String questionText,
    List<String> options,
    Integer correctIndex,
    String exerciseSlug)
    implements QuestionDTO {}
