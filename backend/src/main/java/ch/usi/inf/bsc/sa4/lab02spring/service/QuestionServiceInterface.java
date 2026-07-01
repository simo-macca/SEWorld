package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.QuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.Variant;
import java.util.UUID;

/**
 * Interface defining the contract for services handling different types of questions.
 * Implementations of this interface are responsible for saving and updating question data.
 *
 * @param <T> the type of the question DTO
 */
public interface QuestionServiceInterface<T extends QuestionDTO> {

  /**
   * Saves a new question associated with a specific exercise and variant.
   *
   * @param questionDTO the data transfer object containing the question details
   * @param exercise the exercise to which the question belongs
   * @param variant the variant of the exercise
   */
  void save(T questionDTO, Exercise exercise, Variant variant);

  /**
   * Updates an existing question identified by its unique identifier.
   *
   * @param questionDTO the data transfer object containing the updated question details
   * @param questionDid the unique identifier of the question to be updated
   * @return the updated question DTO
   */
  T update(T questionDTO, UUID questionDid);
}
