package ch.usi.inf.bsc.sa4.lab02spring.controller;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AnswerDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.Answer;
import ch.usi.inf.bsc.sa4.lab02spring.response.ResponseHandler;
import ch.usi.inf.bsc.sa4.lab02spring.service.AnswerService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling answer-related HTTP requests. Delegates business logic to the
 * AnswerService.
 */
@RestController
@RequestMapping("/api/auth/topic/exercises/answers")
public class AnswerController {
  /** Service layer dependency to handle answer-related operations. */
  private final AnswerService answerService;

  /**
   * Constructor-based dependency injection for AnswerService.
   *
   * @param answerService the service responsible for answer logic
   */
  @Autowired
  public AnswerController(AnswerService answerService) {
    this.answerService = answerService;
  }

  /**
   * Retrieves an answer for a given attempt and question. This method fetches an existing {@link
   * Answer} by invoking the {@link AnswerService#getAnswerDTOByAttemptAndQuestion(UUID, UUID)}
   * method with the specified attempt and question decentralized identifiers (DIDs). The retrieved
   * answer is then mapped into an {@link AnswerDTO}.
   *
   * @param attempt_did the decentralized identifier (DID) of the attempt related to the answer.
   * @param question_did the decentralized identifier (DID) of the question related to the answer.
   * @param principal the authenticated user principal.
   * @return a {@link ResponseEntity} containing: - A message indicating the successful retrieval of
   *     the answer. - An HTTP status code of {@code 200 OK}. - A success flag set to {@code true}.
   *     - An {@link AnswerDTO} representing the answer, if found.
   */
  @GetMapping("/get/{attempt_did}/{question_did}")
  public ResponseEntity<Object> getAnswer(
      @PathVariable UUID attempt_did,
      @PathVariable UUID question_did,
      @AuthenticationPrincipal Object principal) {
    AnswerDTO answer = answerService.getAnswerDTOByAttemptAndQuestion(attempt_did, question_did);
    return ResponseHandler.generateResponse(
        "Answer successfully retrieved", HttpStatus.OK, true, answer);
  }

  /**
   * Retrieves all answers associated with a specific attempt. This endpoint fetches all answers
   * related to the given attempt identifier. It returns a list of {@link AnswerDTO} objects
   * representing the answers.
   *
   * @param attempt_did the unique identifier of the attempt whose answers are to be retrieved
   * @return a {@link ResponseEntity} containing a list of {@link AnswerDTO} objects and an HTTP
   *     status of {@link HttpStatus#OK}
   */
  @GetMapping("/getall/{attempt_did}")
  public ResponseEntity<Object> getAllAnswers(@PathVariable UUID attempt_did) {
    List<AnswerDTO> answers = answerService.getAllAnswerDTOByAttempt(attempt_did);
    return ResponseHandler.generateResponse(
        "All answer successfully retrieved", HttpStatus.OK, true, answers);
  }

  /**
   * Creates a new answer for a given user and exercise.
   *
   * <p>This method initiates the creation of a new {@link Answer} by invoking the {@link
   * AnswerService#createAnswer(String, UUID, UUID, Object)} method with the specified String
   * content, attempt and exercise decentralized identifiers (DIDs). The created answer is then
   * mapped into an {@link AnswerDTO}.
   *
   * @param attempt_did the decentralized identifier (DID) of the attempt related to the answer.
   * @param question_did the decentralized identifier (DID) of the question related to the answer.
   * @param answerDTO the {@link AnswerDTO} from which to create the answer
   * @param principal the authenticated user principal.
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A message indicating the successful retrieval of answers.
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A list of {@link AnswerDTO} objects representing the answers.
   *     </ul>
   */
  @PostMapping("/create/{attempt_did}/{question_did}")
  public ResponseEntity<Object> createAnswer(
      @RequestBody AnswerDTO answerDTO,
      @PathVariable UUID attempt_did,
      @PathVariable UUID question_did,
      @AuthenticationPrincipal Object principal) {
    Optional<Answer> answer =
        answerService.createAnswer(answerDTO.answerContent(), attempt_did, question_did, principal);
    return ResponseHandler.generateResponse(
        "Answer successfully created",
        HttpStatus.CREATED,
        true,
        answer.stream().map(AnswerDTO::new).toList());
  }

  /**
   * Updates the completion status of an existing {@link Answer}.
   *
   * <p>This method delegates to {@link AnswerService#updateAnswer(UUID, AnswerDTO, Object)},
   * setting the answer's content as specified in the {@link AnswerDTO} and returns a {@link
   * ResponseEntity}.
   *
   * @param did the decentralized identifier (DID) of the answer to be updated
   * @param answerDTO the {@link AnswerDTO} from which to update the answer
   * @param principal the authenticated user principal.
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A message indicating the successful update of answers.
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A list of {@link AnswerDTO} objects representing the answers.
   *     </ul>
   */
  @PostMapping("/update/{did}")
  public ResponseEntity<Object> updateAnswer(
      @RequestBody AnswerDTO answerDTO,
      @PathVariable UUID did,
      @AuthenticationPrincipal Object principal) {
    Optional<AnswerDTO> answer = answerService.updateAnswer(did, answerDTO, principal);
    return ResponseHandler.generateResponse(
        "Answer successfully updated", HttpStatus.OK, true, answer.stream().toList());
  }

  /**
   * Deletes an answer identified by its decentralized identifier (DID).
   *
   * <p>This method removes the {@link Answer} entity corresponding to the provided DID by invoking
   * the {@link AnswerService#deleteAnswer(UUID, Object)} method.
   *
   * @param did the decentralized identifier (DID) of the answer to be deleted.
   * @param principal the authenticated user principal.
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A message indicating the successful retrieval of answers.
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A list of {@link AnswerDTO} objects representing the answers.
   *     </ul>
   */
  @DeleteMapping("/delete/{did}")
  public ResponseEntity<Object> deleteAttemptByDid(
      @PathVariable("did") UUID did, @AuthenticationPrincipal Object principal) {
    answerService.deleteAnswer(did, principal);
    return ResponseHandler.generateResponse(
        "Answer successfully deleted", HttpStatus.OK, true, null);
  }
}
