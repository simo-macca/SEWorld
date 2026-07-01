package ch.usi.inf.bsc.sa4.lab02spring.controller;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AiAbstractResponseDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AiQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.QuestionAndUserAnswerDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.QuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.RateQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AIExerciseResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.AIMaterialResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractAIResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.response.ResponseHandler;
import ch.usi.inf.bsc.sa4.lab02spring.service.AiQuestionService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.UserRoleCheckerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.UUID;

/**
 * REST controller that exposes endpoints for AI-driven question operations on learning materials.
 *
 * <p>Uses {@code AiQuestionService} to submit, rate, retrieve, and publish AI-generated responses,
 * and enforces role-based access for instructor-only operations.
 */
@RestController
@RequestMapping("/api/auth/AI")
public class AiQuestionController {

  /** Service layer for handling AI question-related operations. */
  private final AiQuestionService aiQuestionService;

  /** Service layer for retrieving user information and roles. */
  private final UserService userService;

  /** Constant for the question type. */
  private static final String QUESTION_TYPE = "Markdown Question";

  /**
   * Constructs an {@code AiQuestionController} with injected services.
   *
   * @param aiQuestionService service handling AI question logic
   * @param userService service for user lookup and role checking
   */
  @Autowired
  public AiQuestionController(AiQuestionService aiQuestionService, UserService userService) {
    this.aiQuestionService = aiQuestionService;
    this.userService = userService;
  }

  /**
   * /** Submits a highlighted section of Markdown material as a question to the AI model.
   *
   * @param principal the authenticated user principal
   * @param materialDid the UUID of the material being queried
   * @param aiQuestionDTO the question payload containing highlighted text and query
   * @return a {@link ResponseEntity} wrapping the generated {@link AiAbstractResponseDTO} and HTTP
   *     200 status
   * @throws HttpClientErrorException on errors from the AI service
   */
  @PostMapping("/ask/materials/{material_did}")
  public ResponseEntity<Object> ask(
      @AuthenticationPrincipal Object principal,
      @PathVariable("material_did") UUID materialDid,
      @RequestBody AiQuestionDTO aiQuestionDTO) {

    if (UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.FORBIDDEN, "You do not have permission to publish this resource");
    }
    AbstractUser user = userService.findOrCreateUser(principal);
    AIMaterialResponse response =
        aiQuestionService.askAboutHighlight(
            aiQuestionDTO.highlightedText(), materialDid, aiQuestionDTO.question(), user);

    AiAbstractResponseDTO responseDto = response.toDTO(0);

    return ResponseHandler.generateResponse("Question asked", HttpStatus.OK, true, responseDto);
  }

  /**
   * Submits a rating for an existing AI response.
   *
   * @param principal the authenticated user principal
   * @param responseDid the UUID of the AI response being rated
   * @param rateQuestionDTO the rating payload containing the new rating value
   * @return a {@link ResponseEntity} wrapping the updated {@link AiAbstractResponseDTO} and HTTP
   *     200 status
   * @throws HttpClientErrorException on unauthorized or rating errors
   */
  @PatchMapping("/response/rate/{response_did}")
  public ResponseEntity<Object> rateQuestion(
      @AuthenticationPrincipal Object principal,
      @PathVariable("response_did") UUID responseDid,
      @RequestBody RateQuestionDTO rateQuestionDTO) {

    AbstractUser user = userService.findOrCreateUser(principal);
    AiAbstractResponseDTO response = aiQuestionService.rateQuestion(responseDid, user.getStudent(), rateQuestionDTO);
    return ResponseHandler.generateResponse("Response rated", HttpStatus.OK, true, response);
  }

  /**
   * Retrieves all AI responses. Accessible only to instructors.
   *
   * @param principal the authenticated user principal
   * @return a {@link ResponseEntity} wrapping a list of {@link AiAbstractResponseDTO} and HTTP 200
   *     status
   * @throws HttpClientErrorException with HTTP 403 if the user is not an instructor
   */
  @GetMapping("/response")
  public ResponseEntity<Object> getAll(@AuthenticationPrincipal Object principal) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.FORBIDDEN, "You do not have permission to access this resource");
    }
    List<AiAbstractResponseDTO> responses = aiQuestionService.getAll();

    return ResponseHandler.generateResponse(
        "Response rated", HttpStatus.OK, true, responses);
  }

  /**
   * Publishes an AI response for public visibility. Accessible only to instructors.
   *
   * @param principal the authenticated user principal
   * @param response_did the UUID of the AI response to publish
   * @return a {@link ResponseEntity} wrapping the published {@link AiAbstractResponseDTO} and HTTP
   *     200 status
   * @throws HttpClientErrorException with HTTP 403 if the user is not an instructor
   */
  @PatchMapping("/response/publish/{response_did}")
  public ResponseEntity<Object> getAll(
      @AuthenticationPrincipal Object principal, @PathVariable UUID response_did) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.FORBIDDEN, "You do not have permission to publish this resource");
    }

    AbstractAIResponse response = aiQuestionService.publish(response_did);
    AiAbstractResponseDTO responseDto = response.toDTO(0);

    return ResponseHandler.generateResponse("Response published", HttpStatus.OK, true, responseDto);
  }

  /**
   * Retrieves all public AI responses for a given material.
   *
   * @param principal the authenticated user principal
   * @param materialDid the UUID of the material whose responses to fetch
   * @return a {@link ResponseEntity} wrapping a list of public {@link AiAbstractResponseDTO} and
   *     HTTP 200 status
   */
  @PatchMapping("/response/public/{material_did}")
  public ResponseEntity<Object> getAllPublic(
      @AuthenticationPrincipal Object principal, @PathVariable("material_did") UUID materialDid) {
    List<AiAbstractResponseDTO> responseDtos =
        aiQuestionService.getAllPublished(materialDid, principal);

    return ResponseHandler.generateResponse(
        "Fetched All public Responses", HttpStatus.OK, true, responseDtos);
  }

  /**
   * Retrieves all AI responses for a given user.
   *
   * @param principal an authenticated user
   * @return list of {@link AbstractAIResponse} visible for a given user.
   */
  @GetMapping("/response/mine")
  public ResponseEntity<Object> getAllUserResponses(@AuthenticationPrincipal Object principal) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      AbstractUser user = userService.findOrCreateUser(principal);
      List<AiAbstractResponseDTO> usersResponses =
          aiQuestionService.getAllUserResponses(user.getStudent());

      return ResponseHandler.generateResponse(
          "Fetched all answers of the given student", HttpStatus.OK, true, usersResponses);
    } else {
      throw new HttpClientErrorException(
          HttpStatus.FORBIDDEN, "You do not have permission to publish this resource");
    }
  }

  /**
   * Generates variant questions using AI for a given set of question DTOs. Accessible only to
   * instructors.
   *
   * @param principal the authenticated user principal
   * @param generateQuestionVariantDTO the list of question DTOs to generate variants for
   * @return a {@link ResponseEntity} wrapping the generated variants and HTTP 200 status
   * @throws HttpClientErrorException with HTTP 401 if the user is not an instructor
   */
  @PostMapping("/generate/question_variant")
  public ResponseEntity<Object> generateQuestionVariant(
      @AuthenticationPrincipal Object principal,
      @RequestBody List<QuestionDTO<?>> generateQuestionVariantDTO) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "You do not have permission to access this resource");
    }

    var questions = aiQuestionService.generateQuestionVariant(generateQuestionVariantDTO);

    return ResponseHandler.generateResponse(
        "Question variant generated", HttpStatus.OK, true, questions);
  }

  /**
   * Retrieves all AI-generated explanations for the questions and answers of a given exercise.
   *
   * <p>Only authenticated instructors are authorized to access this endpoint.
   *
   * @param principal the currently authenticated user
   * @param exerciseDid the UUID of the exercise whose explanations are to be fetched
   * @return a {@code ResponseEntity} containing a list of {@link QuestionAndUserAnswerDTO} objects
   *     wrapped by the {@link ResponseHandler}, with HTTP status 200 if successful
   * @throws HttpClientErrorException if the user is not an instructor (HTTP 401)
   */
  @GetMapping("/responses/{exercise_did}/all_generated_AI_explanations")
  public ResponseEntity<Object> getAllExplanations(
      @AuthenticationPrincipal Object principal, @PathVariable("exercise_did") UUID exerciseDid) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "You do not have permission to access this resource");
    }
    List<QuestionAndUserAnswerDTO> questionsAndAnswers =
        aiQuestionService.getQuestionsAndAnswers(exerciseDid);
    return ResponseHandler.generateResponse(
        "Questions, answers and AI response successfully loaded",
        HttpStatus.OK,
        true,
        questionsAndAnswers);
  }

  /**
   * Asks the AI for an explanation of a specific user answer.
   *
   * <p>Only non-instructor users are allowed to request explanations. The returned AI explanation
   * is wrapped in a DTO with an initial user rating of 0.
   *
   * @param principal the currently authenticated user
   * @param answerDid the UUID of the answer for which an explanation is requested
   * @return a {@code ResponseEntity} containing the {@link AiAbstractResponseDTO} of the AI
   *     explanation, with HTTP status 200 if successful
   * @throws HttpClientErrorException if the user is an instructor (HTTP 401)
   */
  @GetMapping("/ask/questions/explanation/new/{answer_did}")
  public ResponseEntity<Object> askQuestionExplanation(
      @AuthenticationPrincipal Object principal, @PathVariable("answer_did") UUID answerDid) {
    if (UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "You do not have permission to access this resource");
    }
    AbstractUser user = userService.findOrCreateUser(principal);
    AIExerciseResponse aiExerciseResponse = aiQuestionService.askAboutAnswer(answerDid, user);
    return ResponseHandler.generateResponse(
        "Question asked", HttpStatus.OK, true, aiExerciseResponse.toDTO(0));
  }

  /**
   * Refreshes a previously requested AI explanation for a specific user answer.
   *
   * <p>Only non-instructor users are allowed to refresh explanations. The refreshed explanation is
   * wrapped in a DTO with an initial user rating of 0.
   *
   * @param principal the currently authenticated user
   * @param answerDid the UUID of the answer for which the explanation is to be refreshed
   * @return a {@code ResponseEntity} containing the refreshed {@link AiAbstractResponseDTO}, with
   *     HTTP status 200 if successful
   * @throws HttpClientErrorException if the user is an instructor (HTTP 401)
   */
  @GetMapping("/ask/questions/explanation/refresh/{answer_did}")
  public ResponseEntity<Object> refreshQuestionExplanation(
      @AuthenticationPrincipal Object principal, @PathVariable("answer_did") UUID answerDid) {
    if (UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "You do not have permission to access this resource");
    }
    AbstractUser user = userService.findOrCreateUser(principal);
    AIExerciseResponse aiExerciseResponse = aiQuestionService.refreshAnswer(answerDid, user);
    return ResponseHandler.generateResponse(
        "Question asked", HttpStatus.OK, true, aiExerciseResponse.toDTO(0));
  }

  /**
   * Retrieves all publicly available AI-generated exercise responses for a given answer.
   *
   * <p>No authentication or role check is performed; any user may view public responses. The
   * responses are converted to DTOs without user ratings.
   *
   * @param principal the currently authenticated user (maybe null)
   * @param answerDid the UUID of the answer whose public responses are fetched
   * @return a {@code ResponseEntity} containing a list of {@link AiAbstractResponseDTO} objects
   *     wrapped by the {@link ResponseHandler}, with HTTP status 200 if successful
   */
  @GetMapping("/ask/questions/explanation/published/{answer_did}")
  public ResponseEntity<Object> getPublicAiExerciseresponses(
      @AuthenticationPrincipal Object principal, @PathVariable("answer_did") UUID answerDid) {
    List<AiAbstractResponseDTO> aiExerciseResponsesDTOs =
        aiQuestionService.getPublicAiExerciseResponses(answerDid, userService.findOrCreateUser(principal));
    return ResponseHandler.generateResponse("Question asked", HttpStatus.OK, true, aiExerciseResponsesDTOs);
  }
}
