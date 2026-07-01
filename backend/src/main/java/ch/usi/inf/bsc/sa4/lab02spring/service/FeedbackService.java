package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.FeedbackDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractQuestion;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Answer;
import ch.usi.inf.bsc.sa4.lab02spring.model.Attempt;
import ch.usi.inf.bsc.sa4.lab02spring.model.Feedback;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AttemptRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.FeedbackRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.QuestionRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Service for generating feedback based on a user's attempt.
 *
 * <p>This service retrieves the user's attempt, loads the corresponding questions and answers,
 * evaluates the answers, and produces a feedback DTO containing the results.
 */
@Service
public class FeedbackService {

  /** Repository for retrieving {@link QuestionService} entities. */
  private final QuestionRepository questionRepository;

  /** Service layer for managing {@link Attempt} operations. */
  private final AttemptService attemptService;

  /** Service for retrieving {@link AbstractUser} details using decentralized identifiers. */
  private final UserService userService;

  /** Repository for persisting and retrieving {@link Feedback} entities. */
  private final FeedbackRepository feedbackRepository;

  /** Repository for persisting and retrieving {@link Attempt} entities. */
  private final AttemptRepository attemptRepository;

  private final AiQuestionService aiQuestionService;

  /**
   * Constructs a new {@code FeedbackService} with the required dependencies.
   *
   * <p>This constructor initializes the service with the necessary parts to handle feedback
   * generation:
   *
   * @param attemptService the service layer for managing {@link Attempt} operations.
   * @param attemptRepository the repository for persisting and retrieving {@link Attempt} entities.
   * @param questionRepository the repository for persisting and retrieving {@link AbstractQuestion}
   *     entities.
   * @param userService the service for retrieving {@link AbstractUser} details
   * @param feedbackRepository the repository for persisting and retrieving {@link Feedback}
   * @param aiQuestionService the service for retrieving {@link AiQuestionService} entities.
   *     entities
   */
  @Autowired
  public FeedbackService(
      AttemptService attemptService,
      UserService userService,
      FeedbackRepository feedbackRepository,
      QuestionRepository questionRepository,
      AttemptRepository attemptRepository,
      AiQuestionService aiQuestionService) {
    this.attemptService = attemptService;
    this.userService = userService;
    this.feedbackRepository = feedbackRepository;
    this.questionRepository = questionRepository;
    this.attemptRepository = attemptRepository;
    this.aiQuestionService = aiQuestionService;
  }

  /**
   * Retrieves feedback for a given attempt by evaluating the user's answers.
   *
   * @param principal the principal object representing the current user
   * @param attemptDid the unique identifier of the attempt
   * @return a {@link FeedbackDTO} containing the total correct answers, total questions, percentage
   *     score, and categorized feedback for each question type.
   * @throws HttpClientErrorException if the user is unauthorized, an attempt is not found, or if
   *     there are error loading answers or questions.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public FeedbackDTO getFeedbackAttempt(Object principal, UUID attemptDid) {

    final AbstractUser user = userService.findOrCreateUser(principal);

    if (!user.getDid().equals(attemptService.getUserByAttemptDid(attemptDid).getDid())) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "This user " + user.getName() + " does not have this attempt");
    }

    final Attempt attempt =
        attemptRepository
            .findByAttemptDid(attemptDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Attempt not found"));

    if (!attempt.isAttemptIsSubmitted()) {
      throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "This attempt is not submitted");
    }

    List<Answer> answers = attempt.loadAnswers();
    List<AbstractQuestion> questions = attempt.getQuestions();
    Feedback f = feedbackRepository.findByAttempt(attempt).orElse(null);

    return new FeedbackDTO(
        f != null
            ? f
            : Feedback.computeFeedback(attempt, questions, answers, aiQuestionService, principal)
                .getSecond());
  }
}
