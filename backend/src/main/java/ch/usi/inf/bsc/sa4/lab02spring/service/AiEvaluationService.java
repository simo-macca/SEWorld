package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AIEvalAndQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AIEvalDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AIEvaluation;
import ch.usi.inf.bsc.sa4.lab02spring.model.AIExerciseResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractQuestion;
import ch.usi.inf.bsc.sa4.lab02spring.model.Answer;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.Feedback;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AIEvaluationRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AIExerciseResponseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AIResponseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AttemptRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.ExerciseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.FeedbackRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Service layer for handling AI evaluation workflows.
 *
 * <p>Provides methods to accept or deny AI-generated evaluations and to fetch pending evaluations
 * for a given exercise. Transactional boundaries ensure data integrity during create, update, and
 * delete operations.
 */
@Service
public class AiEvaluationService {

  /** Repository for accessing and storing {@link AIEvaluation} entities. */
  private final AIEvaluationRepository aiEvaluationRepository;

  /** Repository for accessing and storing {@link AIExerciseResponse} entities. */
  private final AIResponseRepository aiResponseRepository;

  /** Repository for linking {@link AIExerciseResponse} entities to {@link Exercise} entities. */
  private final AIExerciseResponseRepository aiExerciseResponseRepository;

  /** Repository for persisting and retrieving {@link Feedback} entities. */
  private final FeedbackRepository feedbackRepository;

  /** Repository for retrieving {@link Exercise} entities. */
  private final ExerciseRepository exerciseRepository;

  /** Repository for retrieving {@link AbstractQuestion} entities. */
  private final QuestionRepository abstractQuestionRepository;

  /** Repository for tracking user attempts. */
  private final AttemptRepository attemptRepository;

  /**
   * Constructs the service with required repository dependencies.
   *
   * @param aiEvaluationRepository repository for AI evaluations
   * @param aiResponseRepository repository for AI exercise responses
   * @param aiExerciseResponseRepository repository for linking AI responses to exercises
   * @param feedbackRepository repository for persisting feedback entities
   * @param exerciseRepository repository for exercise data
   * @param abstractQuestionRepository repository for question metadata
   * @param attemptRepository repository for tracking user attempts
   */
  @Autowired
  public AiEvaluationService(
      AIEvaluationRepository aiEvaluationRepository,
      AIResponseRepository aiResponseRepository,
      AIExerciseResponseRepository aiExerciseResponseRepository,
      FeedbackRepository feedbackRepository,
      ExerciseRepository exerciseRepository,
      QuestionRepository abstractQuestionRepository,
      AttemptRepository attemptRepository) {
    this.aiEvaluationRepository = aiEvaluationRepository;
    this.aiResponseRepository = aiResponseRepository;
    this.aiExerciseResponseRepository = aiExerciseResponseRepository;
    this.feedbackRepository = feedbackRepository;
    this.exerciseRepository = exerciseRepository;
    this.abstractQuestionRepository = abstractQuestionRepository;
    this.attemptRepository = attemptRepository;
  }

  /**
   * Accepts an AI evaluation identified by its DID.
   *
   * <p>If the evaluation deems the submitted answer incorrect, creates an {@link
   * AIExerciseResponse} and persists it. The original evaluation is then removed.
   *
   * @param aiEvalDid the UUID (DID) of the AI evaluation to accept
   * @return the same UUID of the accepted evaluation
   * @throws HttpClientErrorException if no evaluation with the given DID is found
   */
  @Transactional
  public UUID acceptAiEvaluation(UUID aiEvalDid) {
    AIEvaluation eval =
        aiEvaluationRepository
            .findByAiEvaluationDid(aiEvalDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Evaluation not found"));

    if (!eval.isAnswerCorrect()) {
      AIExerciseResponse response = new AIExerciseResponse(eval);
      aiResponseRepository.save(response);
      aiExerciseResponseRepository.save(response);
    }

    aiEvaluationRepository.delete(eval);
    return aiEvalDid;
  }

  /**
   * Denies an AI evaluation identified by its DID.
   *
   * <p>Updates the related {@link Feedback} to include the AI’s reasoning, marks the attempt as
   * completed if applicable, and deletes the evaluation.
   *
   * @param aiEvalDid the UUID (DID) of the AI evaluation to deny
   * @throws HttpClientErrorException if no evaluation or related feedback is found
   */
  @Transactional
  public void denyAiEvaluation(UUID aiEvalDid) {
    AIEvaluation eval =
        aiEvaluationRepository
            .findByAiEvaluationDid(aiEvalDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Evaluation not found"));
    Feedback feedbackToUpdate =
        feedbackRepository
            .findByAttempt(eval.getAnswer().getAttempt())
            .orElseThrow(
                () ->
                    new HttpClientErrorException(
                        HttpStatus.NOT_FOUND,
                        "Feedback related to this ai evaluation has not been found..."));
    feedbackToUpdate.updateFeedback(eval);
    feedbackRepository.save(feedbackToUpdate);
    feedbackToUpdate.getAttempt().recomputeIsCompleted();
    attemptRepository.save(feedbackToUpdate.getAttempt());
    aiEvaluationRepository.delete(eval);
  }

  /**
   * Retrieves all pending evaluations for a specified exercise.
   *
   * <p>Iterates over each question in the exercise and collects AI evaluations mapped to {@link
   * AIEvalDTO}. Only questions with at least one evaluation are included in the result as {@link
   * AIEvalAndQuestionDTO}.
   *
   * @param exerciseDid the UUID (DID) of the exercise whose evaluations are requested
   * @return a list of DTOs pairing questions with their pending AI evaluations
   * @throws HttpClientErrorException if the exercise is not found
   */
  @Transactional
  public List<AIEvalAndQuestionDTO> getPendingEvaluations(UUID exerciseDid) {
    Exercise exercise =
        exerciseRepository
            .findByExerciseDid(exerciseDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found"));

    List<AIEvalAndQuestionDTO> result = new ArrayList<>();

    for (AbstractQuestion question : exercise.getQuestions()) {
      List<AIEvalDTO> evals =
          question.getAnswers().stream()
              .map(aiEvaluationRepository::findByAnswer)
              .filter(Optional::isPresent)
              .map(
                  e -> {
                    AIEvaluation eval = e.get();
                    Answer answer = eval.getAnswer();
                    return new AIEvalDTO(
                        answer.getAttempt().getUser().getName(),
                        answer.getAnswerContent(),
                        eval.getAiReasoning(),
                        eval.isAnswerCorrect(),
                        eval.getAiEvaluationDid());
                  })
              .toList();

      if (!evals.isEmpty()) {
        result.add(new AIEvalAndQuestionDTO(question, evals));
      }
    }
    return result;
  }
}
