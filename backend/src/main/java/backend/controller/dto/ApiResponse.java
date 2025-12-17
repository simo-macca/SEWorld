package backend.controller.dto;

public record ApiResponse<T>(T body, String message) {}
