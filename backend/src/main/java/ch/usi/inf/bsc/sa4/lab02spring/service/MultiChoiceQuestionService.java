package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.MultiChoiceQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.MultiChoiceQuestion;
import ch.usi.inf.bsc.sa4.lab02spring.model.Variant;
import ch.usi.inf.bsc.sa4.lab02spring.repository.ExerciseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.MultiChoiceQuestionRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.QuestionRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.VariantRepository;
import java.util.UUID;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Service class responsible for managing operations related to multi-choice questions on exercises.
 */
@Service
public class MultiChoiceQuestionService
    implements QuestionServiceInterface<MultiChoiceQuestionDTO> {

  /** Repository for storing and retrieving {@link MultiChoiceQuestion} entities. */
  private final MultiChoiceQuestionRepository multiChoiceRepository;

  /** Repository for retrieving {@link TypePatternQuestions.Question} entities. */
  private final QuestionRepository questionRepository;

  /** Repository for retrieving {@link Variant} entities. */
  private final VariantRepository variantRepository;

  /** Service for retrieving {@link Variant} entities. */
  private final VariantService variantService;

  /** Repository for retrieving {@link Exercise} entities. */
  private final ExerciseRepository exerciseRepository;

  /**
   * Constructs a new {@code MultiChoiceQuestionService} with injected dependencies.
   *
   * @param multiChoiceRepository the repository for MultiChoiceQuestion entities.
   * @param questionRepository the repository for Question entities.
   * @param variantRepository the repository for Variant entities.
   * @param variantService the service for retrieving {@link Variant} entities.
   * @param exerciseRepository the repository for Exercise entities.
   */
  @Autowired
  public MultiChoiceQuestionService(
      MultiChoiceQuestionRepository multiChoiceRepository,
      QuestionRepository questionRepository,
      VariantRepository variantRepository,
      VariantService variantService,
      ExerciseRepository exerciseRepository) {
    this.multiChoiceRepository = multiChoiceRepository;
    this.questionRepository = questionRepository;
    this.variantRepository = variantRepository;
    this.variantService = variantService;
    this.exerciseRepository = exerciseRepository;
  }

  @Transactional(rollbackFor = HttpClientErrorException.class)
  public void save(MultiChoiceQuestionDTO questionDTO, Exercise exercise, Variant variant) {
    if (questionDTO.choices().isEmpty()) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "You must provide at least one choice");
    }
    if (questionDTO.correctAnswer() < 0
        || questionDTO.correctAnswer() > questionDTO.choices().size()) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "index out of bounds");
    }
    MultiChoiceQuestion newQuestion =
        new MultiChoiceQuestion(
            questionDTO.questionTitle(),
            questionDTO.choices(),
            questionDTO.correctAnswer(),
            exercise,
            variant);
    questionRepository.save(newQuestion);
    this.multiChoiceRepository.save(newQuestion);
  }

  /**
   * Retrieves a {@link MultiChoiceQuestion} entity by its DID.
   *
   * @param did the DID of the question to retrieve.
   * @return the {@link MultiChoiceQuestion} entity with the specified DID.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public MultiChoiceQuestion getMultiChoiceQuestionByDid(UUID did) {
    return multiChoiceRepository.findByQuestion_DID(did);
  }

  @Transactional(rollbackFor = HttpClientErrorException.class)
  public MultiChoiceQuestionDTO update(MultiChoiceQuestionDTO new_question, UUID did) {

    MultiChoiceQuestion old_question = getMultiChoiceQuestionByDid(did);

    // Check if the old question exists
    if (old_question == null) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "AbstractQuestion not found");
    }
    Exercise exercise = old_question.getExercise();

    // Check if the exercise is a draft
    if (!exercise.isExerciseIsDraft()) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "impossible to create an exercise for non draft question");
    }

    // Update only the fields which can be changed, including the parent field (questionTitle)
    if (new_question.questionTitle() != null)
      old_question.setQuestionTitle(new_question.questionTitle()); // Parent field

    if (new_question.choices() != null) {
      old_question.setChoices(new_question.choices());
    }

    if (new_question.correctAnswer() != null) {
      int size =
          new_question.choices() == null
              ? old_question.getChoices().size()
              : new_question.choices().size();
      if (new_question.correctAnswer() > size || new_question.correctAnswer() < 0)
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "index out of bounds");
      old_question.setCorrectAnswer(new_question.correctAnswer());
    }

    // Save only the MultiChoiceQuestion entity (parent fields will be updated too)
    multiChoiceRepository.save(old_question); // This will update both parent and child fields
    return old_question.convertToDTO(true);
  }
}
