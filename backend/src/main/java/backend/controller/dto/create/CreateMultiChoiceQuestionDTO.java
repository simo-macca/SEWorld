package backend.controller.dto.create;

import java.util.List;

public record CreateMultiChoiceQuestionDTO(String text, List<String> options, Integer correctIndex)
    implements CreateQuestionDTO {}
