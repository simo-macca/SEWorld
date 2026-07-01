package ch.usi.inf.bsc.sa4.lab02spring.controller;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AttemptDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.Attempt;
import ch.usi.inf.bsc.sa4.lab02spring.response.ResponseHandler;
import ch.usi.inf.bsc.sa4.lab02spring.service.AttemptService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.UserRoleCheckerUtils;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

/**
 * REST controller for managing attempt-related endpoints.
 *
 * <p>This controller handles HTTP requests related to {@link Attempt} entities. It exposes
 * endpoints under the path <code>/api/auth/topic/exercises/attempts</code> and leverages the {@link
 * AttemptService} and {@link UserService} to perform the necessary business operations. The
 * responses are generated using the {@link ResponseHandler} to ensure a standardized API response
 * format.
 *
 * @see AttemptService
 * @see AttemptDTO
 * @see ResponseHandler
 */
@RestController
@RequestMapping("/api/auth/topic/exercises/attempts")
public class AttemptController {

  /** Constant message for attempts found responses. */
  private static final String ATTEMPTS_FOUND_MESSAGE = "Attempts found";

  /** Service layer for managing attempt operations. */
  private final AttemptService attemptService;

  /** Service layer for managing user operations. */
  private final UserService userService;

  /**
   * Constructs an {@code AttemptController} with the specified services.
   *
   * @param attemptService the service handling attempt-related operations
   * @param userService the service handling user-related operations
   */
  public AttemptController(AttemptService attemptService, UserService userService) {
    this.attemptService = attemptService;
    this.userService = userService;
  }

  /**
   * Get all attempts from the system.
   *
   * <p>This method fetches a list of all {@link Attempt} entities via the {@link
   * AttemptService#getAllAttempts()} method and maps each {@code Attempt} into an {@link
   * AttemptDTO} for data transfer purposes.
   *
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A message indicating the successful retrieval of attempts.
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A list of {@link AttemptDTO} objects representing the attempts.
   *     </ul>
   */
  @GetMapping
  public ResponseEntity<Object> getAllAttempts() {
    List<Attempt> attempts = attemptService.getAllAttempts();
    return ResponseHandler.generateResponse(
        "Attempts successfully loaded",
        HttpStatus.OK,
        true,
        attempts.stream().map(attemptService::getDTO).toList());
  }

  /**
   * Get all attempts associated with a specific user identified by their DID.
   *
   * <p>The method uses the {@code principal} parameter, injected by Spring Security to identify the
   * current user. This method fetches all {@link Attempt} entities for the user with the given
   * decentralized identifier (DID) via the {@link AttemptService#getAllAttemptsByUserDid(UUID)}
   * method, mapping each {@code Attempt} into an {@link AttemptDTO} for data transfer.
   *
   * @param principal the authenticated user's security details. It typically contains user
   *     credentials and authorities.
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A message indicating the successful retrieval of attempts.
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A list of {@link AttemptDTO} objects representing the attempts.
   *     </ul>
   */
  @GetMapping("/get_all_by_user")
  public ResponseEntity<Object> getAllAttemptsByUserDid(@AuthenticationPrincipal Object principal) {
    UUID userDid = userService.findOrCreateUser(principal).getDid();
    List<Attempt> attempts = attemptService.getAllAttemptsByUserDid(userDid);
    return ResponseHandler.generateResponse(
        ATTEMPTS_FOUND_MESSAGE,
        HttpStatus.OK,
        true,
        attempts.stream().map(attemptService::getDTO).toList());
  }

  /**
   * Get all attempts associated with a specific exercise identified by its DID.
   *
   * <p>This method fetches all {@link Attempt} entities for the exercise with the given
   * decentralized identifier (DID) via the {@link AttemptService#getAllAttemptsByExerciseDid(UUID)}
   * method, mapping each {@code Attempt} into an {@link AttemptDTO} for data transfer.
   *
   * @param exerciseDid the decentralized identifier (DID) of the exercise.
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A message indicating the successful retrieval of attempts.
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A list of {@link AttemptDTO} objects representing the attempts.
   *     </ul>
   *
   * @throws HttpClientErrorException if the current user is not authorized to access this resource.
   */
  @GetMapping("/get_all_by_exercise/{exercise_did}")
  public ResponseEntity<Object> getAllAttemptsByExerciseDid(
      @PathVariable("exercise_did") UUID exerciseDid) {
    List<Attempt> attempts = attemptService.getAllAttemptsByExerciseDid(exerciseDid);
    return ResponseHandler.generateResponse(
        ATTEMPTS_FOUND_MESSAGE,
        HttpStatus.OK,
        true,
        attempts.stream().map(attemptService::getDTO).toList());
  }

  /**
   * Get all attempts for a specific user and exercise combination, identified by their DIDs.
   *
   * <p>The method uses the {@code principal} parameter, injected by Spring Security to identify the
   * current user. This method fetches all {@link Attempt} entities matching the provided user and
   * exercise decentralized identifiers (DIDs) via the {@link
   * AttemptService#getAllAttemptsByUserAndExerciseDid(UUID, UUID)} method, mapping each {@code
   * Attempt} into an {@link AttemptDTO} for data transfer.
   *
   * @param principal the authenticated user's security details. It typically contains user
   *     credentials and authorities.
   * @param exerciseDid the decentralized identifier (DID) of the exercise.
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A message indicating the successful retrieval of attempts.
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A list of {@link AttemptDTO} objects representing the attempts.
   *     </ul>
   */
  @GetMapping("/get_all_by_user_exercise/{exercise_did}")
  public ResponseEntity<Object> getAllAttemptsByAndUserAndExerciseDid(
      @AuthenticationPrincipal Object principal, @PathVariable("exercise_did") UUID exerciseDid) {
    UUID userDid = userService.findOrCreateUser(principal).getDid();
    List<Attempt> attempts =
        attemptService.getAllAttemptsByUserAndExerciseDid(userDid, exerciseDid);
    return ResponseHandler.generateResponse(
        ATTEMPTS_FOUND_MESSAGE,
        HttpStatus.OK,
        true,
        attempts.stream().map(attemptService::getDTO).toList());
  }

  /**
   * Get an attempt by its decentralized identifier (DID).
   *
   * <p>This method fetches the {@link Attempt} entity corresponding to the provided DID via the
   * {@link AttemptService#getAttemptByDid(UUID)} method. If the attempt is found, it is mapped into
   * an {@link AttemptDTO}.
   *
   * @param did the decentralized identifier (DID) of the attempt.
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A message indicating the successful retrieval of attempts.
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A list of {@link AttemptDTO} objects representing the attempts.
   *     </ul>
   */
  @GetMapping("/get_by_did/{did}")
  public ResponseEntity<Object> getAttemptByDid(@PathVariable("did") UUID did) {
    Optional<Attempt> attempt = Optional.of(attemptService.getAttemptByDid(did));
    return ResponseHandler.generateResponse(
        "Attempt founds", HttpStatus.OK, true, attempt.map(attemptService::getDTO));
  }

  /**
   * Creates a new attempt for a given user and exercise.
   *
   * <p>The method uses the {@code principal} parameter, injected by Spring Security to identify the
   * current user. This method initiates the creation of a new {@link Attempt} by invoking the
   * {@link AttemptService#createAttempt(UUID, UUID, boolean)} method with the specified user and
   * exercise decentralized identifiers (DIDs). The created attempt is then mapped into an {@link
   * AttemptDTO}.
   *
   * @param principal the authenticated user's security details. It typically contains user
   *     credentials and authorities.
   * @param exerciseDid the decentralized identifier (DID) of the exercise related to the attempt.
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A message indicating the successful retrieval of attempts.
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A list of {@link AttemptDTO} objects representing the attempts.
   *     </ul>
   */
  @PostMapping("/create/{exercise_did}")
  public ResponseEntity<Object> createAttempt(
      @AuthenticationPrincipal Object principal, @PathVariable("exercise_did") UUID exerciseDid) {
    System.out.println(exerciseDid + "\n\n\n\n");
    UUID userDid = userService.findOrCreateUser(principal).getDid();
    boolean isInstructor =
        UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService);
    Optional<Attempt> attempts = attemptService.createAttempt(userDid, exerciseDid, !isInstructor);
    return ResponseHandler.generateResponse(
        "Attempt successfully created",
        HttpStatus.CREATED,
        true,
        attempts.stream().map(attemptService::getDTO).toList());
  }

  /**
   * Updates the submitted status of an existing {@link Attempt}.
   *
   * <p>This method delegates to {@link AttemptService#updateAttempt(UUID, Object)}, setting the
   * attempt's submitted status to {@code true} without modifying its stage. The updated attempt is
   * then converted into an {@link AttemptDTO} and returned within a {@link ResponseEntity}.
   *
   * @param did the decentralized identifier (DID) of the attempt to be updated
   * @param principal the authenticated user principal.
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A message indicating the successful update of attempts.
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A list of {@link AttemptDTO} objects representing the attempts.
   *     </ul>
   */
  @PatchMapping("/update/{did}")
  public ResponseEntity<Object> updateAttempt(
      @PathVariable("did") UUID did, @AuthenticationPrincipal Object principal) {
    Optional<Attempt> attempt = attemptService.updateAttempt(did, principal);
    return ResponseHandler.generateResponse(
        "Attempt successfully updated", HttpStatus.OK, true, attempt.map(attemptService::getDTO));
  }

  /**
   * Deletes an attempt identified by its decentralized identifier (DID).
   *
   * <p>This method removes the {@link Attempt} entity corresponding to the provided DID by invoking
   * the {@link AttemptService#deleteAttemptByDid(UUID)} method.
   *
   * @param did the decentralized identifier (DID) of the attempt to be deleted.
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A message indicating the successful retrieval of attempts.
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A list of {@link AttemptDTO} objects representing the attempts.
   *     </ul>
   */
  @DeleteMapping("/delete/{did}")
  public ResponseEntity<Object> deleteAttemptByDid(@PathVariable("did") UUID did) {
    attemptService.deleteAttemptByDid(did);
    return ResponseHandler.generateResponse(
        "Attempt successfully deleted", HttpStatus.OK, true, null);
  }
}
