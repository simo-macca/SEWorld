package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AnswerDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AnswerRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AttemptRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.QuestionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Service layer responsible for handling business logic related to answers, including creation,
 * validation, and persistence. This service collaborates with multiple repositories and other
 * services to manage user attempts and question-related data.
 */
@Service
public class AnswerService {

  /** Repository for managing Answer entities in the database. */
  private final AnswerRepository answerRepository;

  /** Repository for accessing and storing Attempt entities. */
  private final AttemptRepository attemptRepository;

  /** Repository for retrieving AbstractQuestion entities. */
  private final QuestionRepository questionRepository;

  /** Service providing user-related operations. */
  private final UserService userService;

  /** Service handling logic related to user attempts. */
  private final AttemptService attemptService;

  /**
   * Constructs an AnswerService with all required dependencies. All dependencies are injected via
   * constructor and are managed by Spring, ensuring immutability and thread safety.
   *
   * @param answerRepository repository for Answer persistence
   * @param attemptRepository repository for Attempt persistence
   * @param questionRepository repository for accessing questions
   * @param userService service for user management
   * @param attemptService service for attempt processing
   */
  @Autowired
  public AnswerService(
      AnswerRepository answerRepository,
      AttemptRepository attemptRepository,
      QuestionRepository questionRepository,
      UserService userService,
      AttemptService attemptService) {
    this.answerRepository = answerRepository;
    this.attemptRepository = attemptRepository;
    this.questionRepository = questionRepository;
    this.userService = userService;
    this.attemptService = attemptService;
  }

  /**
   * Retrieves all Answer entities from the repository.
   *
   * @return a {@link List} of all {@link Answer} objects
   */
  public List<Answer> getAllAnswers() {
    return this.answerRepository.findAll();
  }

  /**
   * Checks whether the currently authenticated principal is the owner of the given attempt.
   *
   * @param principal the security principal representing the current user
   * @param attempt the {@link Attempt} to verify ownership of
   * @return {@code true} if the principal’s email matches the attempt owner’s email; {@code false}
   *     otherwise
   */
  private boolean isLegit(Object principal, Attempt attempt) {
    AbstractUser userFromRequest = userService.findOrCreateUser(principal);
    AbstractUser userFromAttempt = attemptService.getUserByAttemptDid(attempt.getAttemptDid());

    return userFromRequest.getEmail().equals(userFromAttempt.getEmail());
  }

  /**
   * Fetches a single AnswerDTO for the specified attempt and question.
   *
   * <p>Looks up an {@link Answer} by attempt DID and question DID, then maps it to an {@link
   * AnswerDTO}. Rolls back on any HTTP client errors.
   *
   * @param attemptDid the UUID of the attempt
   * @param questionDid the UUID of the question
   * @return the corresponding {@link AnswerDTO}
   * @throws HttpClientErrorException if no matching Answer is found
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public AnswerDTO getAnswerDTOByAttemptAndQuestion(UUID attemptDid, UUID questionDid) {
    Optional<Answer> answer =
        answerRepository.findByAttemptDidAndQuestionDid(attemptDid, questionDid);
    return answer.stream().map(AnswerDTO::new).toList().getFirst();
  }

  /**
   * Retrieves all answers for a given attempt and maps them to DTOs.
   *
   * <p>Rolls back the transaction in case of HTTP client errors.
   *
   * @param attemptDid the UUID of the attempt
   * @return a {@link List} of {@link AnswerDTO} objects for the attempt
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public List<AnswerDTO> getAllAnswerDTOByAttempt(UUID attemptDid) {
    List<Answer> answers = answerRepository.findAllByAttemptDid(attemptDid);
    return answers.stream().map(AnswerDTO::new).toList();
  }

  /**
   * Creates or updates an Answer for the specified attempt and question.
   *
   * <p>Validates that the attempt exists, is not already submitted, and is owned by the current
   * principal. If an existing Answer exists, it is reused; otherwise a new one is created. Rolls
   * back on HTTP client errors.
   *
   * @param answerContent the content of the user’s answer
   * @param attemptDid the UUID of the attempt
   * @param questionDid the UUID of the question
   * @param principal the security principal representing the current user
   * @return an {@link Optional} wrapping the saved {@link Answer}
   * @throws HttpClientErrorException if the attempt or question is not found, if the attempt is
   *     already submitted, or if the principal is not the owner
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Optional<Answer> createAnswer(
      String answerContent, UUID attemptDid, UUID questionDid, Object principal) {
    Attempt attempt = attemptRepository.findByAttemptDid(attemptDid).orElse(null);
    AbstractQuestion question =
        questionRepository
            .findByQuestionDid(questionDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Question not found"));

    if (attempt == null)
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Attempt not found");

    if (attempt.isAttemptIsSubmitted())
      throw new HttpClientErrorException(HttpStatus.CONFLICT, "Attempt already completed");

    if (!isLegit(principal, attempt))
      throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Attempt not owned by user");

    Answer answer =
        answerRepository
            .findByAttemptDidAndQuestionDid(attemptDid, questionDid)
            .orElse(new Answer(answerContent, attempt, question));
    return Optional.of(answerRepository.save(answer));
  }

  /**
   * Updates the content of an existing Answer.
   *
   * <p>Validates existence, ownership, and submission status of the attempt before saving changes.
   * Rolls back on HTTP client errors.
   *
   * @param answerDid the UUID of the Answer to update
   * @param answerDTO the DTO containing updated answer content
   * @param principal the security principal representing the current user
   * @return an {@link Optional} wrapping the updated {@link AnswerDTO}
   * @throws HttpClientErrorException if the Answer or Attempt is not found, if the attempt is
   *     already submitted, or if the principal is not the owner
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Optional<AnswerDTO> updateAnswer(UUID answerDid, AnswerDTO answerDTO, Object principal) {
    Answer answer =
        answerRepository
            .findByAnswerDid(answerDid)
            .orElseThrow(
                () ->
                    new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, "Answer to update not found"));
    if (!answerDTO.answerContent().isEmpty()) answer.setAnswerContent(answerDTO.answerContent());

    Attempt attempt = answer.getAttempt();
    if (attempt == null)
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Attempt not found");
    if (attempt.isAttemptIsSubmitted())
      throw new HttpClientErrorException(HttpStatus.CONFLICT, "Attempt already completed");
    if (!isLegit(principal, attempt))
      throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Attempt not owned by user");

    return Optional.of(answerRepository.save(answer)).map(AnswerDTO::new);
  }

  /**
   * Deletes an existing Answer.
   *
   * <p>Validates existence, ownership, and submission status of the attempt before deletion. Rolls
   * back on HTTP client errors.
   *
   * @param did the UUID of the Answer to delete
   * @param principal the security principal representing the current user
   * @throws HttpClientErrorException if the Answer or Attempt is not found, if the attempt is
   *     already submitted, or if the principal is not the owner
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public void deleteAnswer(UUID did, Object principal) {
    Answer answer =
        answerRepository
            .findByAnswerDid(did)
            .orElseThrow(
                () ->
                    new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, "Answer to delete not found"));

    Attempt attempt = answer.getAttempt();
    if (attempt == null)
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Attempt not found");
    if (!isLegit(principal, attempt))
      throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Attempt not owned by user");
    if (attempt.isAttemptIsSubmitted())
      throw new HttpClientErrorException(HttpStatus.CONFLICT, "Attempt already completed");

    answerRepository.delete(answer);
  }
}
