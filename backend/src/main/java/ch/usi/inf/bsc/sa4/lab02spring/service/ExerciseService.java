package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.*;
import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import ch.usi.inf.bsc.sa4.lab02spring.repository.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Provides business logic for managing {@link Exercise} entities associated with a {@link Topic}.
 *
 * <p>This service offers methods for retrieving, creating, updating (both draft status and
 * content), and deleting exercises. It applies role-based filtering so that non-instructor users
 * only receive published (non-draft) exercises.
 */
@Service
public class ExerciseService {

  /** Repository for accessing and storing {@link Exercise} entities. */
  private final ExerciseRepository exerciseRepository;

  /** Repository for accessing and storing {@link Topic} entities. */
  private final TopicRepository topicRepository;

  /** Repository for accessing and storing {@link TypePatternQuestions.Question} entities. */
  private final QuestionRepository questionRepository;

  /** Repository for accessing and storing {@link ShortAnswerQuestion} entities. */
  private final ShortAnswerQuestionRepository shortAnswerQuestionRepository;

  /** Repository for accessing and storing {@link TrueFalseQuestion} entities. */
  private final TrueFalseQuestionRepository trueFalseQuestionRepository;

  /** Repository for accessing and storing {@link MultiChoiceQuestion} entities. */
  private final MultiChoiceQuestionRepository multiChoiceQuestionRepository;

  /** Service providing user-related operations. */
  private final UserService userService;

  /** Service providing operations for {@link Variant} entities. */
  private final VariantService variantService;

  /** Service providing operations for {@link ShortAnswerQuestion} entities. */
  private final ShortAnswerQuestionService shortAnswerQuestionService;

  /** Service providing operations for {@link MultiChoiceQuestion} entities. */
  private final MultiChoiceQuestionService multiChoiceQuestionService;

  /** Service providing operations for {@link TrueFalseQuestion} entities. */
  private final TrueFalseQuestionService trueFalseQuestionService;

  /**
   * Constructs an ExerciseService with all required repositories and services.
   *
   * <p>This constructor is used by Spring's dependency injection to provide the necessary parts for
   * managing exercises, questions, topics, and user-related operations.
   *
   * @param exerciseRepository the repository for Exercise entities
   * @param topicRepository the repository for Topic entities
   * @param questionRepository the repository for Question entities
   * @param shortAnswerQuestionRepository the repository for Short Answer Question entities
   * @param trueFalseQuestionRepository the repository for True/False Question entities
   * @param multiChoiceQuestionRepository the repository for Multiple Choice Question entities
   * @param userService the service handling user-related logic
   * @param variantService the service managing exercise/question variants
   * @param shortAnswerQuestionService the service handling short answer questions
   * @param multiChoiceQuestionService the service handling multiple choice questions
   * @param trueFalseQuestionService the service handling true/false questions
   */
  @Autowired
  public ExerciseService(
      ExerciseRepository exerciseRepository,
      TopicRepository topicRepository,
      QuestionRepository questionRepository,
      ShortAnswerQuestionRepository shortAnswerQuestionRepository,
      TrueFalseQuestionRepository trueFalseQuestionRepository,
      MultiChoiceQuestionRepository multiChoiceQuestionRepository,
      UserService userService,
      VariantService variantService,
      ShortAnswerQuestionService shortAnswerQuestionService,
      MultiChoiceQuestionService multiChoiceQuestionService,
      TrueFalseQuestionService trueFalseQuestionService) {
    // Default constructor
    this.exerciseRepository = exerciseRepository;
    this.topicRepository = topicRepository;
    this.questionRepository = questionRepository;
    this.shortAnswerQuestionRepository = shortAnswerQuestionRepository;
    this.trueFalseQuestionRepository = trueFalseQuestionRepository;
    this.multiChoiceQuestionRepository = multiChoiceQuestionRepository;
    this.userService = userService;
    this.variantService = variantService;
    this.shortAnswerQuestionService = shortAnswerQuestionService;
    this.multiChoiceQuestionService = multiChoiceQuestionService;
    this.trueFalseQuestionService = trueFalseQuestionService;
  }

  /**
   * Retrieves all exercises associated with the specified topic.
   *
   * <p>For non-instructor users, only exercises that are not in draft mode are returned.
   *
   * @param topicDid the UUID of the topic.
   * @param isInstructor {@code true} if the requesting user is an instructor; {@code false}
   *     otherwise.
   * @return a list of exercises matching the criteria.
   * @throws HttpClientErrorException with status {@link HttpStatus#NOT_FOUND} if the topic is not
   *     found or no exercises exist.
   */
  public List<Exercise> getAllExercises(UUID topicDid, boolean isInstructor) {
    Topic topic = topicRepository.findByTopicDid(topicDid).orElse(null);
    if (topic == null) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Topic not found");
    }

    List<Exercise> exercises = exerciseRepository.findByTopic(topic).orElse(null);
    if (exercises == null || exercises.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercises not found");
    }

    if (isInstructor) {
      return exercises;
    }

    List<Exercise> notDraftExercises = new ArrayList<>();

    for (Exercise exercise : exercises) {
      if (!exercise.isExerciseIsDraft()) {
        notDraftExercises.add(exercise);
      }
    }

    if (notDraftExercises.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercises not found");
    }

    return notDraftExercises;
  }

  /**
   * Retrieves an exercise by its unique identifier (DID).
   *
   * <p>Non-instructor users are not allowed to retrieve exercises in draft mode.
   *
   * @param exerciseDid the UUID of the exercise.
   * @param isInstructor {@code true} if the requesting user is an instructor; {@code false}
   *     otherwise.
   * @return an {@code Optional} containing the exercise if found and accessible.
   * @throws HttpClientErrorException with status {@link HttpStatus#NOT_FOUND} if the exercise is
   *     not found or is in draft mode for non-instructors.
   */
  public Optional<Exercise> getExerciseByDid(UUID exerciseDid, boolean isInstructor) {
    Exercise ex = exerciseRepository.findByExerciseDid(exerciseDid).orElse(null);

    if (ex == null) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found");
    }

    if (isInstructor) {
      return Optional.of(ex);
    }

    if (ex.isExerciseIsDraft()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercises not found");
    }

    return Optional.of(ex);
  }

  /**
   * Creates a new exercise within the specified topic.
   *
   * @param exDTO the DTO containing the exercise's title and description.
   * @param topicDid the UUID of the topic in which the exercise will be created.
   * @return an {@code Optional} containing the newly created exercise.
   * @throws HttpClientErrorException with status {@link HttpStatus#NOT_FOUND} if the specified
   *     topic does not exist.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Optional<Exercise> createNewExercise(CreateExerciseDTO exDTO, UUID topicDid) {
    Topic topic = topicRepository.findByTopicDid(topicDid).orElse(null);
    if (topic == null) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Topic DID doesn't exist");
    }
    if (exDTO.exerciseTitle() == null || exDTO.exerciseDescription() == null) {
      throw new DataIntegrityViolationException("Title and description are required");
    }
    Exercise newExercise = new Exercise(exDTO.exerciseTitle(), exDTO.exerciseDescription(), topic);
    Exercise exercise = exerciseRepository.save(newExercise);
    // saveQuestions(exDTO.questions(), exercise);
    return Optional.of(exercise);
  }

  /**
   * Searches for exercises in the specified topic based on title and description keywords.
   *
   * <p>For non-instructor users, the search is automatically limited to non-draft exercises.
   *
   * @param titleKeywords the list of keywords to search within exercise titles.
   * @param descriptionKeywords the list of keywords to search within exercise descriptions.
   * @param draft flag indicating whether to include draft exercises (overridden for
   *     non-instructors).
   * @param newTopicDid the UUID of the topic.
   * @param isInstructor {@code true} if the requesting user is an instructor; {@code false}
   *     otherwise.
   * @return a list of exercises matching the search criteria.
   * @throws HttpClientErrorException with status {@link HttpStatus#NOT_FOUND} if the topic is not
   *     found or no matching exercises exist.
   */
  public List<Exercise> searchByKeywords(
      List<String> titleKeywords,
      List<String> descriptionKeywords,
      boolean draft,
      UUID newTopicDid,
      boolean isInstructor) {
    Topic topic = topicRepository.findByTopicDid(newTopicDid).orElse(null);
    if (topic == null) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Topic not found");
    }
    if (!isInstructor) {
      draft = false;
    }
    List<Exercise> ex =
        exerciseRepository.searchByKeywords(titleKeywords, descriptionKeywords, draft, topic);
    if (ex == null || ex.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercises not found");
    }
    return ex;
  }

  /**
   * Updates the draft status of an existing exercise.
   *
   * <p>The exercise must currently be in draft mode for this operation to be allowed.
   *
   * @param newDraft the DTO containing the new draft status.
   * @param exerciseDid the UUID of the exercise to update.
   * @return an {@code Optional} containing the updated exercise.
   * @throws HttpClientErrorException with status {@link HttpStatus#NOT_FOUND} if the exercise is
   *     not found, or with status {@link HttpStatus#BAD_REQUEST} if the exercise is not in draft
   *     mode.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Optional<Exercise> patchDraft(ChangeDraftDTO newDraft, UUID exerciseDid) {
    Exercise oldEx = exerciseRepository.findByExerciseDid(exerciseDid).orElse(null);
    if (oldEx == null) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found");
    }
    if (!oldEx.isExerciseIsDraft()) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "A not draft exercise cannot be modified");
    }
    oldEx.setExerciseIsDraft(newDraft.exerciseIsDraft());
    return Optional.of(exerciseRepository.save(oldEx));
  }

  /**
   * Applies updates to an exercise's title and description if it is in draft mode.
   *
   * <p>Only non-null values provided in the {@code ChangeExerciseDTO} are applied as updates.
   *
   * @param exerciseUpdatesDTO the DTO containing the updated title and/or description.
   * @param exerciseDid the UUID of the exercise to update.
   * @return an {@code Optional} containing the updated exercise.
   * @throws HttpClientErrorException with status {@link HttpStatus#NOT_FOUND} if the exercise is
   *     not found, or with status {@link HttpStatus#BAD_REQUEST} if the exercise is not in draft
   *     mode.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Optional<Exercise> patchExercise(ChangeExerciseDTO exerciseUpdatesDTO, UUID exerciseDid) {
    Exercise oldEx = exerciseRepository.findByExerciseDid(exerciseDid).orElse(null);
    if (oldEx == null) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found");
    }
    if (!oldEx.isExerciseIsDraft()) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Exercise is not draft");
    }
    if (exerciseUpdatesDTO.exerciseTitle() != null) {
      oldEx.setExerciseTitle(exerciseUpdatesDTO.exerciseTitle());
    }
    if (exerciseUpdatesDTO.exerciseDescription() != null) {
      oldEx.setExerciseDescription(exerciseUpdatesDTO.exerciseDescription());
    }
    return Optional.of(exerciseRepository.save(oldEx));
  }

  /**
   * Applies updates to an exercise's title, description and questions if it is in draft mode.
   *
   * <p>Only non-null values provided in the {@code ChangeExerciseDTO} are applied as updates.
   *
   * @param exerciseDTO the DTO containing the updated title and/or description + questions.
   * @param exerciseDid the UUID of the exercise to update.
   * @return an {@code Optional} containing the updated exercise.
   * @throws HttpClientErrorException with status {@link HttpStatus#NOT_FOUND} if the exercise is
   *     not found, or with status {@link HttpStatus#BAD_REQUEST} if the exercise is not in draft
   *     mode.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Optional<Exercise> patchExerciseComplete(CreateExerciseDTO exerciseDTO, UUID exerciseDid) {
    Exercise existingEx = exerciseRepository.findByExerciseDid(exerciseDid).orElse(null);
    if (existingEx == null) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found");
    }
    Optional<Exercise> ex =
        patchExercise(
            new ChangeExerciseDTO(exerciseDTO.exerciseTitle(), exerciseDTO.exerciseDescription()),
            exerciseDid);
    questionRepository.deleteAll(questionRepository.findByExercise_ID(existingEx.getExerciseDid()));
    // saveQuestions(exerciseDTO.questions(), ex.orElse(null));
    return ex;
  }

  /**
   * Deletes an exercise identified by its UUID.
   *
   * @param newExerciseDid the UUID of the exercise to delete.
   * @throws HttpClientErrorException with status {@link HttpStatus#NOT_FOUND} if the exercise is
   *     not found.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public void deleteExercise(UUID newExerciseDid) {
    var ex =
        exerciseRepository
            .findByExerciseDid(newExerciseDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found"));
    exerciseRepository.delete(ex);
  }

  /**
   * Deletes all exercises associated with the specified topic.
   *
   * @param newTopicDid the UUID of the topic whose exercises should be deleted.
   * @throws HttpClientErrorException with status {@link HttpStatus#NOT_FOUND} if the topic is not
   *     found.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public void deleteAllExercisesInTopic(UUID newTopicDid) {
    var topic = topicRepository.findByTopicDid(newTopicDid).orElse(null);
    if (topic == null) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Topic not found");
    }
    exerciseRepository.deleteByTopic(topic);
  }

  /**
   * Computes the completion status for a list of exercises based on the user's progress.
   *
   * <p>This method checks whether each exercise in the provided list has been completed by the
   * user. It updates the {@code completed} status of each exercise accordingly.
   *
   * @param exs the list of exercises to evaluate
   * @param principal the currently authenticated user
   * @throws IllegalArgumentException if any of the exercises in the list is {@code null}
   */
  public void computeCompleted(List<Exercise> exs, Object principal) {
    AbstractUser user = userService.findOrCreateUser(principal);
    exs.forEach(e -> e.setCompleted(exerciseRepository.isExerciseCompleted(e, user)));
  }
}
