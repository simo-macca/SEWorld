package backend.controller.dto;

public record TrueFalseQuestionDTO(
    String questionType,
    String questionSlug,
    String questionText,
    boolean correctBoolean,
    String exerciseSlug)
    implements QuestionDTO {}
