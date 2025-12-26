package backend.controller.dto;

public record ShortAnswerQuestionDTO(
    String questionType,
    String questionSlug,
    String questionText,
    String correctText,
    String exerciseSlug)
    implements QuestionDTO {}
