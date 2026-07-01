package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.ShortAnswerQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractQuestion;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.ShortAnswerQuestion;
import ch.usi.inf.bsc.sa4.lab02spring.model.Variant;
import ch.usi.inf.bsc.sa4.lab02spring.repository.ExerciseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.QuestionRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.ShortAnswerQuestionRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.VariantRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Service class responsible for managing operations related to short answer questions on exercises
 */
@Service
public class ShortAnswerQuestionService
    implements QuestionServiceInterface<ShortAnswerQuestionDTO> {

  /** Repository for storing and retrieving {@link ShortAnswerQuestion} entities. */
  private final ShortAnswerQuestionRepository shortAnswerQuestionRepository;

  /** Repository for retrieving {@link AbstractQuestion} entities. */
  private final QuestionRepository questionRepository;

  /** Repository for retrieving {@link Variant} entities. */
  private final VariantRepository variantRepository;

  /** Service for retrieving {@link Variant} entities. */
  private final VariantService variantService;

  /** Repository for retrieving {@link Exercise} entities. */
  private final ExerciseRepository exerciseRepository;

  /**
   * Constructs a new {@code ShortAnswerQuestionService} with injected dependencies.
   *
   * @param shortAnswerQuestionRepository the repository for ShortAnswerQuestion entities.
   * @param questionRepository the repository for Question entities.
   * @param variantRepository the repository for Variant entities.
   * @param variantService the service for retrieving {@link Variant} entities.
   * @param exerciseRepository the repository for Exercise entities.
   */
  @Autowired
  public ShortAnswerQuestionService(
      ShortAnswerQuestionRepository shortAnswerQuestionRepository,
      QuestionRepository questionRepository,
      VariantRepository variantRepository,
      VariantService variantService,
      ExerciseRepository exerciseRepository) {
    this.shortAnswerQuestionRepository = shortAnswerQuestionRepository;
    this.questionRepository = questionRepository;
    this.variantRepository = variantRepository;
    this.variantService = variantService;
    this.exerciseRepository = exerciseRepository;
  }

  @Transactional(rollbackFor = HttpClientErrorException.class)
  public void save(ShortAnswerQuestionDTO questionDTO, Exercise exercise, Variant variant) {
    exercise.setIsPresentShortQuestion(true);
    exerciseRepository.save(exercise);
    ShortAnswerQuestion newSAQuestion =
        new ShortAnswerQuestion(
            questionDTO.questionTitle(), exercise, questionDTO.correctAnswer(), variant);
    questionRepository.save(newSAQuestion);
    this.shortAnswerQuestionRepository.save(newSAQuestion);
  }

  // there might be a problem here with the first query
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public ShortAnswerQuestionDTO update(ShortAnswerQuestionDTO questionDTO, UUID questionDID) {
    ShortAnswerQuestion oldQuestion =
        shortAnswerQuestionRepository.findShortAnswerQuestionByQuestion_DID(questionDID);
    if (oldQuestion == null) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "AbstractQuestion not found");
    }

    Exercise exercise = oldQuestion.getExercise();

    // check if the exercise is draft
    if (!exercise.isExerciseIsDraft()) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "impossible create exercise for non draft question");
    }

    // Updates only the fields that need to be changed
    if (questionDTO.questionTitle() != null)
      oldQuestion.setQuestionTitle(questionDTO.questionTitle());

    if (questionDTO.correctAnswer() != null) {
      oldQuestion.setCorrectAnswer(questionDTO.correctAnswer());
    }
    questionRepository.save(oldQuestion);
    return oldQuestion.convertToDTO(true);
  }
}
