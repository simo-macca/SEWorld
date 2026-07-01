package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.TrueFalseQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.TrueFalseQuestion;
import ch.usi.inf.bsc.sa4.lab02spring.model.Variant;
import ch.usi.inf.bsc.sa4.lab02spring.repository.ExerciseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.QuestionRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.TrueFalseQuestionRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.VariantRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Service class responsible for managing operations related to true/false questions on exercises.
 */
@Service
public class TrueFalseQuestionService implements QuestionServiceInterface<TrueFalseQuestionDTO> {

  /** Repository for accessing and storing TrueFalseQuestion entities. */
  private final TrueFalseQuestionRepository trueFalseQuestionRepository;

  /** Repository for accessing and storing Question entities. */
  private final QuestionRepository questionRepository;

  /** Repository for accessing and storing Variant entities. */
  private final VariantRepository variantRepository;

  /** Service for retrieving {@link Variant} entities. */
  private final VariantService variantService;

  /** Repository for accessing and storing Exercise entities. */
  private final ExerciseRepository exerciseRepository;

  /**
   * Constructs a new {@code TrueFalseQuestionService} with injected dependencies.
   *
   * @param trueFalseQuestionRepository the repository for TrueFalseQuestion entities.
   * @param questionRepository the repository for Question entities.
   * @param variantRepository the repository for Variant entities.
   * @param variantService the service for retrieving {@link Variant} entities.
   * @param exerciseRepository the repository for Exercise entities.
   */
  @Autowired
  public TrueFalseQuestionService(
      TrueFalseQuestionRepository trueFalseQuestionRepository,
      QuestionRepository questionRepository,
      VariantRepository variantRepository,
      VariantService variantService,
      ExerciseRepository exerciseRepository) {
    this.trueFalseQuestionRepository = trueFalseQuestionRepository;
    this.questionRepository = questionRepository;
    this.variantRepository = variantRepository;
    this.variantService = variantService;
    this.exerciseRepository = exerciseRepository;
  }

  @Transactional(rollbackFor = HttpClientErrorException.class)
  public void save(TrueFalseQuestionDTO questionDTO, Exercise exercise, Variant variant) {
    TrueFalseQuestion newQuestion =
        new TrueFalseQuestion(
            questionDTO.questionTitle(), exercise, questionDTO.correctAnswer(), variant);
    questionRepository.save(newQuestion);
    this.trueFalseQuestionRepository.save(newQuestion);
  }

  @Transactional(rollbackFor = HttpClientErrorException.class)
  public TrueFalseQuestionDTO update(TrueFalseQuestionDTO updatedQuestion, UUID question_did) {

    TrueFalseQuestion old_question =
        trueFalseQuestionRepository.findTrueFalseQuestionByQuestion_DID(question_did);
    if (old_question == null) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise is not found");
    }

    Exercise exercise = old_question.getExercise();

    // check if the exercise is draft
    if (!exercise.isExerciseIsDraft()) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Exercise is not draft");
    }

    // Update only the fields which can be changed
    if (updatedQuestion.questionTitle() != null) {
      old_question.setQuestionTitle(updatedQuestion.questionTitle());
    }

    if (updatedQuestion.correctAnswer() != null) {
      old_question.setCorrectAnswer(updatedQuestion.correctAnswer());
    }
    trueFalseQuestionRepository.save(old_question);
    return old_question.convertToDTO(true);
  }
}
