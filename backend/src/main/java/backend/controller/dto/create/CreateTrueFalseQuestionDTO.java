package backend.controller.dto.create;

public record CreateTrueFalseQuestionDTO(String text, Boolean correctBoolean)
    implements CreateQuestionDTO {}
