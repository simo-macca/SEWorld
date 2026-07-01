package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) for searching exercises using keywords.
 *
 * <p>This DTO encapsulates lists of keywords to search within the title and description of
 * exercises, along with a flag indicating whether draft exercises should be included in the search.
 *
 * @param titleKeywords list of keywords to search in exercise titles.
 * @param descriptionKeywords list of keywords to search in exercise descriptions.
 * @param draft flag indicating whether to include draft exercises in the search.
 */
public record SearchExerciseDTO(
    List<String> titleKeywords, List<String> descriptionKeywords, boolean draft) {}
