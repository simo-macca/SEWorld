package ch.usi.inf.bsc.sa4.lab02spring.controller;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.QuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.SwapIndexQuestionsDTO;
import ch.usi.inf.bsc.sa4.lab02spring.response.ResponseHandler;
import ch.usi.inf.bsc.sa4.lab02spring.service.QuestionService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.UserRoleCheckerUtils;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

/**
 * REST controller for managing questions on exercises.
 *
 * <p>Exposes endpoints for retrieving all questions on an exercise, deleting a question, and saving
 * all questions on an exercise. Access to modifying endpoints is restricted to instructor users.
 */
@RestController
@RequestMapping("/api/auth/topic/exercises/question")
public class QuestionController {

  /** Service layer for managing question operations. */
  private final QuestionService questionService;

  /** Service layer for retrieving user information and roles. */
  private final UserService userService;

  /**
   * Constructs a new {@code QuestionController} with the required services.
   *
   * @param questionService the service for retrieving questions
   * @param userService the service for retrieving user information and roles
   */
  @Autowired
  public QuestionController(QuestionService questionService, UserService userService) {
    this.questionService = questionService;
    this.userService = userService;
  }

  /**
   * Retrieves all randomized questions for a given attempt.
   *
   * @param principal the authenticated user principal
   * @param attemptDid the UUID of the attempt whose questions are to be fetched
   * @return a {@link ResponseEntity} containing a list of {@link QuestionDTO} objects with HTTP
   *     status 200
   * @throws HttpClientErrorException if the user lacks access (handled inside the service)
   */
  @GetMapping("/attempt/{attempt_did}")
  public ResponseEntity<Object> getAllQuestion(
      @AuthenticationPrincipal Object principal, @PathVariable("attempt_did") UUID attemptDid) {
    final boolean instructor =
        UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService);
    List<QuestionDTO> questions = questionService.getAllQuestions(attemptDid, instructor);
    return ResponseHandler.generateResponse(
        "List of all randomized questions", HttpStatus.OK, true, questions);
  }

  /**
   * Retrieves all questions for a given exercise.
   *
   * @param principal the authenticated user principal
   * @param exerciseDid the UUID of the exercise whose questions are to be fetched
   * @return a {@link ResponseEntity} containing a list of {@link QuestionDTO} objects with HTTP
   *     status 200
   * @throws HttpClientErrorException if the principal is not an instructor
   */
  @GetMapping("/exercise/{exercise_did}")
  public ResponseEntity<Object> getAll(
      @AuthenticationPrincipal Object principal, @PathVariable("exercise_did") UUID exerciseDid) {
    final boolean instructor =
        UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService);
    if (!instructor) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED,
          "You do not have the required permissions to perform this operation");
    }
    List<QuestionDTO> questions = questionService.getAll(exerciseDid, instructor);
    return ResponseHandler.generateResponse(
        "List of all questions", HttpStatus.OK, true, questions);
  }

  /**
   * Deletes a question by its DID.
   *
   * <p>Only instructors are authorized. If the principal is an instructor, deletes the question
   *
   * @param principal the authenticated user principal
   * @param questionDid the UUID of the question to delete
   * @return a {@link ResponseEntity} with a confirmation message and HTTP status 200
   * @throws HttpClientErrorException if the principal is not an instructor
   */
  @DeleteMapping("/delete/{question_did}")
  public ResponseEntity<Object> deleteQuestion(
      @AuthenticationPrincipal Object principal, @PathVariable("question_did") UUID questionDid) {
    if (UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      questionService.deleteQuestionByDID(questionDid);
      return ResponseHandler.generateResponse(
          "AbstractQuestion with DID " + questionDid + " has been deleted",
          HttpStatus.OK,
          true,
          null);
    }
    throw new HttpClientErrorException(
        HttpStatus.UNAUTHORIZED,
        "You do not have the required permissions to perform this operation");
  }

  /**
   * Saves a list of questions for a given exercise.
   *
   * @param principal the authenticated user principal
   * @param exerciseDid the UUID of the exercise under which to save questions
   * @param questions the list of {@link QuestionDTO} objects to save
   * @return a {@link ResponseEntity} with HTTP status 200 upon successful save
   * @throws HttpClientErrorException if the principal is not an instructor
   */
  @PostMapping("/save_all/{exercise_did}")
  public ResponseEntity<Object> saveAllQuestions(
      @AuthenticationPrincipal Object principal,
      @PathVariable("exercise_did") UUID exerciseDid,
      @RequestBody List<QuestionDTO> questions) {
    if (UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      questionService.saveAllQuestions(questions, exerciseDid);
      return ResponseHandler.generateResponse(
          "Questions saved successfully", HttpStatus.OK, true, null);
    }
    throw new HttpClientErrorException(
        HttpStatus.UNAUTHORIZED,
        "You do not have the required permissions to perform this operation");
  }

  /**
   * Updates the order or contents of questions within an exercise.
   *
   * @param principal the authenticated user principal
   * @param exerciseDid the UUID of the exercise to update
   * @param swapDTO the {@link SwapIndexQuestionsDTO} specifying how to reorder questions
   * @return a {@link ResponseEntity} with HTTP status 200 upon successful update
   * @throws HttpClientErrorException if the principal is not an instructor
   */
  @PatchMapping("/update/{exercise_did}")
  public ResponseEntity<Object> updateQuestion(
      @AuthenticationPrincipal Object principal,
      @PathVariable("exercise_did") UUID exerciseDid,
      @RequestBody SwapIndexQuestionsDTO swapDTO) {
    if (UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      questionService.updateQuestion(swapDTO, exerciseDid);
      return ResponseHandler.generateResponse(
          "Questions successfully updated", HttpStatus.OK, true, null);
    }
    throw new HttpClientErrorException(
        HttpStatus.UNAUTHORIZED,
        "You do not have the required permissions to perform this operation");
  }
}
