package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AttemptDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import ch.usi.inf.bsc.sa4.lab02spring.repository.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Service class for managing {@link Attempt} entities.
 *
 * <p>This class provides the business operations for attempts, including retrieval, creation,
 * update, and deletion. It interacts with the underlying data store through {@link
 * AttemptRepository} and uses {@link UserRepository} and {@link ExerciseRepository} to resolve
 * related {@link AbstractUser} and {@link Exercise} entities. Additionally, the {@link UserService}
 * is leveraged to fetch user details based on decentralized identifiers.
 *
 * @see AttemptRepository
 * @see Attempt
 */
@Service
public class AttemptService {

  /** Repository for performing CRUD operations on {@link Attempt} entities. */
  private final AttemptRepository attemptRepository;

  /** Repository for retrieving {@link AbstractUser} entities. */
  private final UserRepository userRepository;

  /** Repository for retrieving {@link Exercise} entities. */
  private final ExerciseRepository exerciseRepository;

  /** Service for retrieving {@link AbstractUser} details using decentralized identifiers. */
  private final UserService userService;

  /** Return message when a user is not found */
  private static final String USER_NOT_FOUND = "AbstractUser not found";

  /** Repository for persisting and retrieving {@link Feedback} entities. */
  private final FeedbackRepository feedbackRepository;

  private final QuestionService questionService;

  private final AiQuestionService aiQuestionService;

  /**
   * Constructs a new {@code AttemptService}, injecting all required repositories and services for
   * managing attempts, users, exercises, questions, and feedback.
   *
   * @param attemptRepository the repository for managing {@link Attempt} entities
   * @param userRepository the repository for retrieving {@link AbstractUser} entities
   * @param exerciseRepository the repository for retrieving {@link Exercise} entities
   * @param userService the service for fetching user details by decentralized identifier entities
   * @param feedbackRepository the repository for retrieving and aggregating {@link Feedback}
   * @param questionService the service for retrieving {@link AbstractQuestion}
   * @param aiQuestionService the service for retrieving {@link AiQuestionService} entities
   */
  @Autowired
  public AttemptService(
      AttemptRepository attemptRepository,
      UserRepository userRepository,
      ExerciseRepository exerciseRepository,
      UserService userService,
      QuestionService questionService,
      FeedbackRepository feedbackRepository,
      AiQuestionService aiQuestionService) {
    this.attemptRepository = attemptRepository;
    this.userRepository = userRepository;
    this.exerciseRepository = exerciseRepository;
    this.userService = userService;
    this.feedbackRepository = feedbackRepository;
    this.questionService = questionService;
    this.aiQuestionService = aiQuestionService;
  }

  /**
   * Retrieves all {@link Attempt} entities from the database.
   *
   * <p>This method uses the {@link AttemptRepository} to fetch all attempt records and returns them
   * as a list.
   *
   * @return a {@link List} containing all {@link Attempt} entities.
   * @throws HttpClientErrorException if no attempts are found.
   */
  public List<Attempt> getAllAttempts() {
    final List<Attempt> attempts = attemptRepository.findAll();
    if (attempts.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "There are no attempts");
    }
    return attempts;
  }

  /**
   * Retrieves all {@link Attempt} entities associated with a specific user, identified by their
   * decentralized identifier (DID).
   *
   * <p>The method first retrieves the {@link AbstractUser} entity using the provided DID via the
   * {@link UserService}. It then fetches all attempts related to that user from the {@link
   * AttemptRepository}.
   *
   * @param userDid the decentralized identifier (DID) of the user.
   * @return a {@link List} of {@link Attempt} entities related to the user.
   * @throws HttpClientErrorException if the user is not found or no attempts are associated with
   *     the user.
   */
  public List<Attempt> getAllAttemptsByUserDid(UUID userDid) {
    final AbstractUser user =
        userService
            .getByDid(userDid)
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
    final List<Attempt> attempts = attemptRepository.findByUser(user);
    if (attempts.isEmpty()) {
      throw new HttpClientErrorException(
          HttpStatus.NOT_FOUND, "There are no attempts related to this user: " + user.getName());
    }
    return attempts;
  }

  /**
   * Retrieves all {@link Attempt} entities associated with a specific exercise, identified by its
   * decentralized identifier (DID).
   *
   * <p>The method first retrieves the {@link Exercise} entity using the provided DID from the
   * {@link ExerciseRepository}. It then fetches all attempts related to that exercise from the
   * {@link AttemptRepository}.
   *
   * @param createdDid the decentralized identifier (DID) of the exercise.
   * @return a {@link List} of {@link Attempt} entities related to the exercise.
   * @throws HttpClientErrorException if the exercise is not found or no attempts are associated
   *     with the exercise.
   */
  public List<Attempt> getAllAttemptsByExerciseDid(UUID createdDid) {
    final Exercise exercise =
        exerciseRepository
            .findByExerciseDid(createdDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found"));
    final List<Attempt> attempts = attemptRepository.findByExercise(exercise);
    if (attempts.isEmpty()) {
      throw new HttpClientErrorException(
          HttpStatus.NOT_FOUND,
          "There are no attempts related to this exercise: " + exercise.getExerciseTitle());
    }
    return attempts;
  }

  /**
   * Retrieves all {@link Attempt} entities for a specific user and exercise combination, identified
   * by their decentralized identifiers (DIDs).
   *
   * <p>The method retrieves the corresponding {@link AbstractUser} and {@link Exercise} entities
   * first, then fetches the matching attempts from the {@link AttemptRepository}.
   *
   * @param createdUserDid the decentralized identifier (DID) of the user.
   * @param createdExerciseDid the decentralized identifier (DID) of the exercise.
   * @return a {@link List} of {@link Attempt} entities matching the provided user and exercise.
   * @throws HttpClientErrorException if the user or exercise is not found or no matching attempts
   *     exist.
   */
  public List<Attempt> getAllAttemptsByUserAndExerciseDid(
      UUID createdUserDid, UUID createdExerciseDid) {
    final AbstractUser user =
        userService
            .getByDid(createdUserDid)
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
    final Exercise exercise =
        exerciseRepository
            .findByExerciseDid(createdExerciseDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found"));
    final List<Attempt> attemptsFilter = attemptRepository.findByUserAndExercise(user, exercise);
    if (attemptsFilter.isEmpty()) {
      throw new HttpClientErrorException(
          HttpStatus.NOT_FOUND,
          "There are no attempts related to this user: "
              + user.getName()
              + " and this exercise: "
              + exercise.getExerciseTitle());
    }
    return attemptsFilter;
  }

  /**
   * Retrieves an {@link Attempt} entity by its decentralized identifier (DID).
   *
   * <p>This method uses the {@link AttemptRepository} to fetch the attempt corresponding to the
   * provided DID.
   *
   * @param did the decentralized identifier of the attempt to retrieve.
   * @return the {@link Attempt} if found.
   * @throws HttpClientErrorException if the attempt with the provided DID is not found.
   */
  public Attempt getAttemptByDid(UUID did) {
    return attemptRepository
        .findByAttemptDid(did)
        .orElseThrow(
            () ->
                new HttpClientErrorException(
                    HttpStatus.NOT_FOUND, "Attempt with this DID " + did + " not found"));
  }

  /**
   * Converts an {@link Attempt} into its corresponding {@link AttemptDTO}.
   *
   * <p>Looks up the {@link Exercise} linked to the attempt by its internal ID. If no matching
   * exercise is found, an {@link HttpClientErrorException} with status {@code NOT_FOUND} is thrown.
   * Otherwise, the method constructs and returns a new {@link AttemptDTO} containing the attempt
   * data and the exercise’s DID.
   *
   * @param attempt the {@link Attempt} to be converted; must not be null
   * @return an {@link AttemptDTO} wrapping the original attempt and its exercise DID
   * @throws HttpClientErrorException if the related exercise cannot be found
   */
  public AttemptDTO getDTO(Attempt attempt) {
    Optional<Exercise> ex = exerciseRepository.findById(attempt.getExercise().getExerciseId());
    if (ex.isEmpty()) {
      throw new HttpClientErrorException(
          HttpStatus.NOT_FOUND, "Attempt doesn't seem related to any exercise");
    }
    return new AttemptDTO(attempt, ex.get().getExerciseDid());
  }

  /**
   * Creates and persists a new {@link Attempt} for the given user and exercise.
   *
   * @param userDID the decentralized identifier of the {@link AbstractUser} creating the attempt
   * @param exerciseDID the decentralized identifier of the {@link Exercise} for which the attempt
   *     is created
   * @param isStudent {@code true} if the caller is a student (prevents drafting exercises)
   * @return an {@link Optional} containing the newly created and saved {@link Attempt}
   * @throws HttpClientErrorException with {@link HttpStatus#NOT_FOUND} if the user or exercise is
   *     not found
   * @throws HttpClientErrorException with {@link HttpStatus#BAD_REQUEST} if a student attempts a
   *     draft exercise
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Optional<Attempt> createAttempt(UUID userDID, UUID exerciseDID, boolean isStudent) {
    final AbstractUser user =
        userRepository
            .findByDid(userDID)
            .orElseThrow(
                () ->
                    new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, "AbstractUser to create attempt not found"));

    final Exercise exercise =
        exerciseRepository
            .findByExerciseDid(exerciseDID)
            .orElseThrow(
                () ->
                    new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, "Exercise to create attempt not found"));

    if (exercise.isExerciseIsDraft() && isStudent) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "Not possible to create attempt on draft exercise");
    }

    List<AbstractQuestion> questions = questionService.getAllQuestionsRandomized(exerciseDID);
    if (questions == null || questions.isEmpty()) {
      throw new HttpClientErrorException(
          HttpStatus.NOT_FOUND, "No questions related to this exercise");
    }
    Attempt newAttempt = new Attempt(user, exercise, questions);
    return Optional.of(attemptRepository.save(newAttempt));
  }

  /**
   * Updates an existing {@link Attempt} entity.
   *
   * <p>Depending on the provided flags, this method increments the attempt's completion stage
   * and/or marks the attempt as completed. The updated entity is then persisted using the {@link
   * AttemptRepository}.
   *
   * @param attemptDID the decentralized identifier of the {@link Attempt} to be updated.
   * @param principal the authenticated user principal.
   * @return an {@link Optional} containing the updated {@link Attempt} if the update was
   *     successful.
   * @throws HttpClientErrorException if the attempt to update is not found.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Optional<Attempt> updateAttempt(UUID attemptDID, Object principal) {
    final Attempt attempt =
        attemptRepository
            .findByAttemptDid(attemptDID)
            .orElseThrow(
                () ->
                    new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, "Attempt to update not found"));

    final boolean isInstructor = attempt.getUser().getRole().equals("INSTRUCTOR");

    final List<Answer> answers = attempt.loadAnswers();
    final List<AbstractQuestion> questions = attempt.getQuestions();
    final Pair<Boolean, Feedback> res =
        Feedback.computeFeedback(attempt, questions, answers, aiQuestionService, principal);

    if (!isInstructor) {
      attempt.setAttemptIsCompleted(res.getFirst());
      feedbackRepository.save(res.getSecond());
    }
    attempt.setAttemptIsSubmitted(true);

    return Optional.of(attemptRepository.save(attempt));
  }

  /**
   * Deletes an existing {@link Attempt} entity identified by its decentralized identifier (DID).
   *
   * <p>This method retrieves the {@link Attempt} entity using the provided DID and deletes it from
   * the database.
   *
   * @param did the decentralized identifier of the {@link Attempt} to be deleted.
   * @throws HttpClientErrorException if the attempt to delete is not found.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public void deleteAttemptByDid(UUID did) {
    final Attempt attempt =
        attemptRepository
            .findByAttemptDid(did)
            .orElseThrow(
                () ->
                    new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, "Attempt to delete not found"));
    attemptRepository.delete(attempt);
  }

  /**
   * Retrieves the {@link AbstractUser} associated with the {@link Attempt} identified by the
   * provided decentralized identifier (DID).
   *
   * @param attemptDid the decentralized identifier (DID) of the {@link Attempt} used to locate the
   *     corresponding {@link AbstractUser}.
   * @return the {@link AbstractUser} associated with the given {@link Attempt}.
   * @throws HttpClientErrorException if the attempt or the user is not found.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public AbstractUser getUserByAttemptDid(UUID attemptDid) {
    final Attempt attempt =
        attemptRepository
            .findByAttemptDid(attemptDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Attempt not found"));
    return userService
        .getByDid(attempt.getUser().getDid())
        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
  }

  /**
   * Retrieves the exercise decentralized identifier (DID) from the {@link Attempt} specified by the
   * given DID.
   *
   * @param attemptDid the decentralized identifier (DID) of the {@link Attempt}
   * @return the decentralized identifier (DID) of the {@link Exercise} associated with the attempt
   * @throws HttpClientErrorException if the attempt with the specified DID is not found
   */
  @Transactional(readOnly = true, rollbackFor = HttpClientErrorException.class)
  public UUID getExerciseDidFromAttempt(UUID attemptDid) {
    Attempt attempt =
        this.attemptRepository
            .findByAttemptDid(attemptDid)
            .orElseThrow(
                () ->
                    new HttpClientErrorException(
                        HttpStatus.NOT_FOUND,
                        "Attempt with this DID " + attemptDid + " not found"));
    Exercise exercise = attempt.getExercise();
    return exercise.getExerciseDid();
  }
}
