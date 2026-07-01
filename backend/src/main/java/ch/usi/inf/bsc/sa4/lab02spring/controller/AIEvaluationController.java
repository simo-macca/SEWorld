package ch.usi.inf.bsc.sa4.lab02spring.controller;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AIEvalAndQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.response.ResponseHandler;
import ch.usi.inf.bsc.sa4.lab02spring.service.AiEvaluationService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.UserRoleCheckerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing AI evaluations on exercises.
 *
 * <p>Exposes endpoints for instructors to retrieve pending AI evaluations, accept them, or deny
 * them. Ensures that only authenticated instructors can perform these operations.
 */
@RestController
@RequestMapping("/api/auth/AI_Evaluation")
public class AIEvaluationController {

  /** Service layer for managing AI evaluation operations. */
  private final AiEvaluationService aiEvaluationService;

  /** Service layer for retrieving user information and roles. */
  private final UserService userService;

  /**
   * Constructs a new controller with the given evaluation and user services.
   *
   * @param aiEvaluationService service for performing AI evaluation operations
   * @param userService service for retrieving user information and roles
   */
  @Autowired
  public AIEvaluationController(AiEvaluationService aiEvaluationService, UserService userService) {
    this.aiEvaluationService = aiEvaluationService;
    this.userService = userService;
  }

  /**
   * Retrieves all pending AI evaluations for a given exercise.
   *
   * <p>Only instructors are authorized to view pending evaluations.
   *
   * @param exerciseDid the UUID of the exercise for which to fetch evaluations
   * @param principal the currently authenticated user
   * @return a {@code ResponseEntity} containing a list of {@link AIEvalAndQuestionDTO} objects
   *     wrapped by the {@link ResponseHandler}
   * @throws HttpClientErrorException if the user is not an instructor
   */
  @GetMapping("/get_AI_evaluations/{exercise_did}")
  public ResponseEntity<Object> getAIEvaluationsOnExercise(
      @PathVariable("exercise_did") UUID exerciseDid, @AuthenticationPrincipal Object principal) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "Student not authorized to deny AI evaluation");
    }
    List<AIEvalAndQuestionDTO> dtos = aiEvaluationService.getPendingEvaluations(exerciseDid);
    return ResponseHandler.generateResponse(
        "List of generated ai evaluations", HttpStatus.OK, true, dtos);
  }

  /**
   * Denies an AI evaluation, effectively removing it from pending state.
   *
   * <p>Only instructors are authorized to deny evaluations.
   *
   * @param aiEvalDid the UUID of the AI evaluation to deny
   * @param principal the currently authenticated user
   * @return a {@code ResponseEntity} indicating successful denial
   * @throws HttpClientErrorException if the user is not an instructor
   */
  @DeleteMapping("/deny_evaluation/{AI_evaluation_did}")
  public ResponseEntity<Object> denyAiEvaluation(
      @PathVariable("AI_evaluation_did") UUID aiEvalDid,
      @AuthenticationPrincipal Object principal) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "Student not authorized to deny AI evaluation");
    }
    aiEvaluationService.denyAiEvaluation(aiEvalDid);
    return ResponseHandler.generateResponse("Evaluation denied", HttpStatus.OK, true, null);
  }

  /**
   * Accepts an AI evaluation, marking it as approved and generating a new AI exercise response
   * identifier.
   *
   * <p>Only instructors are authorized to accept evaluations.
   *
   * @param aiEvalDid the UUID of the AI evaluation to accept
   * @param principal the currently authenticated user
   * @return a {@code ResponseEntity} containing the new AI exercise response DID in its body
   * @throws HttpClientErrorException if the user is not an instructor
   */
  @PatchMapping("/accept_evaluation/{AI_evaluation_did}")
  public ResponseEntity<Object> acceptAiEvaluation(
      @PathVariable("AI_evaluation_did") UUID aiEvalDid,
      @AuthenticationPrincipal Object principal) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "Student not authorized to accept AI evaluation");
    }
    UUID aiExerciseResponseDid = aiEvaluationService.acceptAiEvaluation(aiEvalDid);
    return ResponseHandler.generateResponse(
        "Evaluation accepted",
        HttpStatus.OK,
        true,
        "New did is: " + aiExerciseResponseDid.toString());
  }
}
