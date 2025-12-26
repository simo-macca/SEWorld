package backend.controller.dto.create;

public record CreateShortAnswerQuestionDTO(String text, String correctText)
    implements CreateQuestionDTO {}
