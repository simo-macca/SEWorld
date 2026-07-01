package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.*;
import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import ch.usi.inf.bsc.sa4.lab02spring.repository.*;
import java.security.SecureRandom;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

/** Service class responsible for managing operations related to questions on exercises. */
@Service
public class QuestionService {

  /** Repository for storing and retrieving {@link AbstractQuestion} entities. */
  private final QuestionRepository questionRepository;

  /** Repository for retrieving {@link Exercise} entities. */
  private final ExerciseRepository exerciseRepository;

  /** Service for retrieving {@link Variant} entities. */
  private final VariantService variantService;

  /** Repository for retrieving {@link Variant} entities. */
  private final VariantRepository variantRepository;

  /** Service for retrieving {@link Variant} entities. */
  private final QuestionServiceRegistry questionServiceRegistry;

  /** Repository for tracking user attempts. */
  private final AttemptRepository attemptRepository;

  /** Repository for retrieving {@link ShortAnswerQuestion} entities. */
  private final ShortAnswerQuestionRepository shortAnswerQuestionRepository;

  /**
   * Constructs a new {@code QuestionService} with injected dependencies.
   *
   * @param questionRepository the repository for Question entities.
   * @param exerciseRepository the repository for Exercise entities.
   * @param variantService the service for retrieving {@link Variant} entities.
   * @param variantRepository the repository for Variant entities.
   * @param questionServiceRegistry the registry for retrieving {@link QuestionServiceInterface}
   *     instances.
   * @param attemptRepository the repository for tracking user attempts.
   * @param shortAnswerQuestionRepository the repository for retrieving {@link ShortAnswerQuestion}
   *     entities.
   */
  @Autowired
  public QuestionService(
      QuestionRepository questionRepository,
      ExerciseRepository exerciseRepository,
      VariantService variantService,
      VariantRepository variantRepository,
      QuestionServiceRegistry questionServiceRegistry,
      AttemptRepository attemptRepository,
      ShortAnswerQuestionRepository shortAnswerQuestionRepository) {
    this.questionRepository = questionRepository;
    this.exerciseRepository = exerciseRepository;
    this.variantService = variantService;
    this.variantRepository = variantRepository;
    this.questionServiceRegistry = questionServiceRegistry;
    this.attemptRepository = attemptRepository;
    this.shortAnswerQuestionRepository = shortAnswerQuestionRepository;
  }

  /** Random number generator. */
  private static final SecureRandom random = new SecureRandom();

  /**
   * Retrieves a random question from the specified exercise.
   *
   * @param exerciseDid the unique identifier of the exercise
   * @return a random question from the specified exercise
   */
  public List<AbstractQuestion> getAllQuestionsRandomized(UUID exerciseDid) {
    System.out.println("getAllQuestionsRandomized\n\n\n\n");
    List<Variant> variants = variantService.getAllVariantsOfExercise(exerciseDid);
    List<AbstractQuestion> finalQuestions = new ArrayList<>();
    for (Variant variant : variants) {
      List<AbstractQuestion> questions = questionRepository.findByVariant(variant);
      int randomIndex = random.nextInt(questions.size());
      AbstractQuestion finalQuestion = questions.get(randomIndex);
      finalQuestions.add(finalQuestion);
    }
    return finalQuestions;
  }

  /**
   * Retrieves all questions for the specified exercise.
   *
   * @param exerciseDid the UUID of the exercise whose questions to retrieve
   * @param instructor {@code true} if the caller is an instructor (affects DTO content)
   * @return a list of {@link QuestionDTO} objects for the exercise
   * @throws HttpClientErrorException if the exercise is not found
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public List<QuestionDTO> getAll(UUID exerciseDid, boolean instructor) {
    Exercise exercise =
        exerciseRepository
            .findByExerciseDid(exerciseDid)
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    return questionRepository.findByExercise_ID(exercise.getExerciseDid()).stream()
        .map(question -> question.convertToDTO(instructor))
        .toList();
  }

  /**
   * Retrieves and orders all randomized questions for a given attempt.
   *
   * @param attemptDid the UUID of the attempt whose questions to retrieve
   * @param instructor {@code true} if the caller is an instructor (affects DTO content)
   * @return a list of {@link QuestionDTO} objects in variant order
   * @throws HttpClientErrorException if the attempt is not found
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public List<QuestionDTO> getAllQuestions(UUID attemptDid, boolean instructor) {

    Attempt attempt =
        attemptRepository
            .findByAttemptDid(attemptDid)
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

    List<AbstractQuestion> finalQuestions = attempt.getQuestions();

    finalQuestions.sort(Comparator.comparingInt(a -> a.getVariant().getIdx()));

    return finalQuestions.stream().map(question -> question.convertToDTO(instructor)).toList();
  }

  /**
   * Deletes the question identified by its DID.
   *
   * <p>If multiple questions share the same variant, only the question is deleted. Otherwise,
   * adjusts the indices of later variants, deletes the question and its now‑unused variant, and
   * updates the exercise’s short‑question flag if necessary.
   *
   * @param questionDid the UUID of the question to delete
   * @throws HttpClientErrorException if the question, variants, or related data are not found
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public void deleteQuestionByDID(UUID questionDid) {
    AbstractQuestion question =
        questionRepository
            .findByQuestionDid(questionDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Question not found"));
    Variant variant = question.getVariant();
    long count = questionRepository.countAbstractQuestionByVariant(variant);
    if (count > 1) {
      questionRepository.delete(question);
      return;
    }
    Exercise ex = variant.getExercise();
    List<Variant> variants = variantRepository.findVariantsByExerciseOrderByIdxAsc(ex);
    if (variants == null || variants.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Variants not found");
    }
    for (int i = variant.getIdx() + 1; i < variants.size(); ++i) {
      Variant variantTmp = variants.get(i);
      variantTmp.setIdx(i - 1);
      variantRepository.save(variantTmp);
    }
    questionRepository.delete(question);
    variantRepository.delete(variant);
    if (ex.getIsPresentShortQuestion()) {
      List<ShortAnswerQuestion> shortQuestions = shortAnswerQuestionRepository.findByExerciseId(ex);
      if (shortQuestions.isEmpty()) {
        ex.setIsPresentShortQuestion(false);
        exerciseRepository.save(ex);
      }
    }
  }

  /**
   * Saves or updates a list of questions for the specified exercise.
   *
   * <p>Ensures variants exist or creates them, then delegates each question DTO to its appropriate
   * {@link QuestionServiceInterface} for persistence.
   *
   * @param questions the list of {@link QuestionDTO} objects to save, sorted by variant index
   * @param exerciseDid the UUID of the exercise under which to save questions
   * @throws HttpClientErrorException if the exercise is not found or variant creation fails
   */
  public void saveAllQuestions(List<QuestionDTO> questions, UUID exerciseDid) {
    questions.sort(Comparator.comparingInt(QuestionDTO::variantIndex));
    Exercise exercise = exerciseRepository.findByExerciseDid(exerciseDid).orElse(null);
    if (exercise == null) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
    }
    for (QuestionDTO questionDTO : questions) {
      Optional<Variant> optVariant =
          variantRepository.findByExerciseAndIdx(exercise, questionDTO.variantIndex());
      Variant variant;
      variant =
          optVariant.orElseGet(
              () ->
                  variantService
                      .createNewVariant(
                          exerciseDid, new CreateVariantDTO(questionDTO.variantIndex()))
                      .orElseThrow(
                          () ->
                              new HttpClientErrorException(
                                  HttpStatus.BAD_REQUEST, "Variant creation failed")));
      QuestionServiceInterface<QuestionDTO> service =
          questionServiceRegistry.getService(questionDTO);
      service.save(questionDTO, exercise, variant);
    }
  }

  /**
   * Swaps the indices of two question variants and updates question content.
   *
   * <p>Validates swap pairs, persists the new variant order, and delegates updates to the
   * appropriate {@link QuestionServiceInterface} implementations.
   *
   * @param swapDTOs a {@link SwapIndexQuestionsDTO} containing swap instructions and question DTOs
   * @param exerciseDid the UUID of the exercise whose questions are to be updated
   * @throws HttpClientErrorException if the exercise/variants are not found or swap indices are
   *     invalid
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public void updateQuestion(SwapIndexQuestionsDTO swapDTOs, UUID exerciseDid) {
    swapDTOs.questionDTOs().sort(Comparator.comparingInt(QuestionDTO::variantIndex));
    Exercise ex =
        exerciseRepository
            .findByExerciseDid(exerciseDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found"));
    List<Variant> variants = variantRepository.findByExerciseOrderByIdxAsc(ex);
    if (variants == null || variants.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Variants not found");
    }
    swapDTOs
        .pairs()
        .forEach(
            pair -> {
              if (pair.checkPair(variants.size())) {
                Variant v1 = variants.get(pair.first());
                Variant v2 = variants.get(pair.second());
                int tmp = v1.getIdx();
                v1.setIdx(v2.getIdx());
                v2.setIdx(tmp);
                variantRepository.save(v1);
                variantRepository.save(v2);
              } else {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Index out of bounds");
              }
            });
    for (QuestionDTO questionDTO : swapDTOs.questionDTOs()) {
      QuestionServiceInterface<QuestionDTO> service =
          questionServiceRegistry.getService(questionDTO);
      service.update(questionDTO, questionDTO.questionDid());
    }
  }
}
