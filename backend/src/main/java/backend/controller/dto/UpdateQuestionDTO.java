package backend.controller.dto;

import backend.controller.dto.create.CreateQuestionDTO;

public record UpdateQuestionDTO(String questionSlug, CreateQuestionDTO questionDTO) {}
