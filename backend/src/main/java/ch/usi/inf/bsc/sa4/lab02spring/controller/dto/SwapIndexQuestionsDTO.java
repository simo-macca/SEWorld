package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.List;
import java.util.Set;

/**
 * Data Transfer Object (DTO) representing a collection of questions and a set of index pairs
 * indicating which question variants should be swapped.
 *
 * <p>This record is used to facilitate operations involving the swapping of question variants,
 * particularly in scenarios where multiple questions and their corresponding variant pairs are
 * involved.
 *
 * @param questionDTOs the list of question DTOs involved in the swap operation
 * @param pairs the set of index pairs specifying which question variants to swap
 */
public record SwapIndexQuestionsDTO(List<QuestionDTO> questionDTOs, Set<QuestionIndexPair> pairs) {}
