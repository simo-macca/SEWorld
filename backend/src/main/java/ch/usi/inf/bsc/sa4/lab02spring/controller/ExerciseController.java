package ch.usi.inf.bsc.sa4.lab02spring.controller;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.*;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.response.ResponseHandler;
import ch.usi.inf.bsc.sa4.lab02spring.service.ExerciseService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.UserRoleCheckerUtils;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Controller for managing exercises within topics.
 *
 * <p>This controller exposes endpoints to retrieve, create, update, and delete exercises. It uses
 * the {@link ExerciseService} to perform business logic and the {@link UserService} for role
 * checks.
 */
@RestController
@RequestMapping("/api/auth/topic/exercises")
public class ExerciseController {

  @Autowired private ExerciseService exerciseService;

  @Autowired private UserService userService;

  /**
   * Default constructor for {@link ExerciseController}.
   *
   * <p>This constructor is provided to adhere to Java conventions.
   */
  public ExerciseController() {
    // Default constructor
  }

  /**
   * Retrieves all exercises within a given topic.
   *
   * @param principal The authenticated user.
   * @param topicDid The UUID of the topic.
   * @return A response entity containing a list of exercises.
   */
  @GetMapping("/get_all_exercises_in_topic/{topic_did}")
  public ResponseEntity<Object> getAllExercisesInTopic(
      @AuthenticationPrincipal Object principal, @PathVariable("topic_did") UUID topicDid) {
    boolean isInstructor =
        UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService);
    List<Exercise> exs = exerciseService.getAllExercises(topicDid, isInstructor);
    exerciseService.computeCompleted(exs, principal);
    return ResponseHandler.generateResponse(
        "Exercises with topic DID " + topicDid + " have been loaded",
        HttpStatus.OK,
        true,
        exs.stream().map(ExerciseDTO::new).toList());
  }

  /**
   * Retrieves a specific exercise by its DID.
   *
   * @param principal The authenticated user.
   * @param exerciseDid The UUID of the exercise.
   * @return A response entity containing the exercise.
   */
  @GetMapping("/get_by_exercise_did/{exercise_did}")
  public ResponseEntity<Object> getExercise(
      @AuthenticationPrincipal Object principal, @PathVariable("exercise_did") UUID exerciseDid) {
    boolean isInstructor =
        UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService);
    Optional<Exercise> ex = exerciseService.getExerciseByDid(exerciseDid, isInstructor);
    return ResponseHandler.generateResponse(
        "Exercise with DID " + exerciseDid + " has been loaded",
        HttpStatus.OK,
        true,
        ex.stream().map(ExerciseDTO::new).toList());
  }

  /**
   * Creates a new exercise within a given topic.
   *
   * @param principal The authenticated user.
   * @param exDTO The data transfer object containing exercise details.
   * @param topic_did The UUID of the topic.
   * @return A response entity containing the created exercise.
   * @throws HttpClientErrorException with status {@link HttpStatus#UNAUTHORIZED} if the current
   *     user is not allowed to create an exercise.
   */
  @PostMapping("/create/{topic_did}")
  public ResponseEntity<Object> createExercise(
      @AuthenticationPrincipal Object principal,
      @RequestBody CreateExerciseDTO exDTO,
      @PathVariable("topic_did") UUID topic_did) {
    if (UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      Optional<Exercise> ex = exerciseService.createNewExercise(exDTO, topic_did);

      if (ex.isPresent()) {
        Exercise exercise = ex.get();
        return ResponseHandler.generateResponse(
            "Exercise with DID " + exercise.getExerciseDid() + " has been created",
            HttpStatus.CREATED,
            true,
            ex.stream().map(ExerciseDTO::new).toList());
      } else {
        return ResponseHandler.generateResponse(
            "Failed to create exercise", HttpStatus.BAD_REQUEST, false, null);
      }
    }
    throw new HttpClientErrorException(
        HttpStatus.UNAUTHORIZED, "Current user is not allowed to create exercise");
  }

  /**
   * Searches for exercises within a topic using keywords.
   *
   * @param principal The authenticated user.
   * @param searchExerciseDTO The DTO containing search parameters.
   * @param topicDid The UUID of the topic.
   * @return A response entity containing matching exercises.
   */
  @PostMapping("/search/{topic_did}")
  public ResponseEntity<Object> searchExercisesByKeywords(
      @AuthenticationPrincipal Object principal,
      @RequestBody SearchExerciseDTO searchExerciseDTO,
      @PathVariable("topic_did") UUID topicDid) {
    boolean isInstructor =
        UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService);
    List<Exercise> exs =
        exerciseService.searchByKeywords(
            searchExerciseDTO.titleKeywords(),
            searchExerciseDTO.descriptionKeywords(),
            searchExerciseDTO.draft(),
            topicDid,
            isInstructor);
    return ResponseHandler.generateResponse(
        "Exercises with topic DID " + topicDid + " have been loaded",
        HttpStatus.OK,
        true,
        exs.stream().map(ExerciseDTO::new).toList());
  }

  /**
   * Changes the draft status of an exercise.
   *
   * @param principal The authenticated user.
   * @param newDraft The new draft status.
   * @param exercise_did The UUID of the exercise.
   * @return A response entity confirming the update.
   * @throws HttpClientErrorException with status {@link HttpStatus#UNAUTHORIZED} if the user is not
   *     allowed to modify the exercise.
   */
  @PatchMapping("/teacher/change_draft/{exercise_did}")
  public ResponseEntity<Object> changeDraft(
      @AuthenticationPrincipal Object principal,
      @RequestBody ChangeDraftDTO newDraft,
      @PathVariable("exercise_did") UUID exercise_did) {
    if (UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      Optional<Exercise> ex = exerciseService.patchDraft(newDraft, exercise_did);
      return ResponseHandler.generateResponse(
          "Exercise with DID " + exercise_did + " has been updated",
          HttpStatus.OK,
          true,
          ex.stream().map(ExerciseDTO::new).toList());
    }
    throw new HttpClientErrorException(
        HttpStatus.UNAUTHORIZED, "Current user is not allowed to modify the exercise");
  }

  /**
   * Updates an existing exercise.
   *
   * @param principal The authenticated user.
   * @param exerciseUpdatesDTO The DTO containing updates.
   * @param exercise_did The UUID of the exercise.
   * @return A response entity confirming the update.
   * @throws HttpClientErrorException with status {@link HttpStatus#UNAUTHORIZED} if the user is not
   *     allowed to modify the exercise.
   */
  @PatchMapping("/teacher/update_exercise/{exercise_did}")
  public ResponseEntity<Object> updateExercise(
      @AuthenticationPrincipal Object principal,
      @RequestBody ChangeExerciseDTO exerciseUpdatesDTO,
      @PathVariable("exercise_did") UUID exercise_did) {
    if (UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      Optional<Exercise> ex = exerciseService.patchExercise(exerciseUpdatesDTO, exercise_did);
      return ResponseHandler.generateResponse(
          "Exercise with DID " + exercise_did + " has been updated",
          HttpStatus.OK,
          true,
          ex.stream().map(ExerciseDTO::new).toList());
    }
    throw new HttpClientErrorException(
        HttpStatus.UNAUTHORIZED, "Current user is not allowed to modify the exercise");
  }

  /**
   * Updates an existing exercise while allowing modifications to its questions.
   *
   * @param principal The authenticated user.
   * @param exerciseDTO The DTO containing updates.
   * @param exercise_did The UUID of the exercise.
   * @return A response entity confirming the update.
   * @throws HttpClientErrorException with status {@link HttpStatus#UNAUTHORIZED} if the user is not
   *     allowed to modify the exercise.
   */
  @PatchMapping("/teacher/update_exercise_complete/{exercise_did}")
  public ResponseEntity<Object> updateExerciseComplete(
      @AuthenticationPrincipal Object principal,
      @RequestBody CreateExerciseDTO exerciseDTO,
      @PathVariable("exercise_did") UUID exercise_did) {
    if (UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      Optional<Exercise> exO = exerciseService.getExerciseByDid(exercise_did, true), ex;
      if (exO.isPresent()) {
        ex = exerciseService.patchExerciseComplete(exerciseDTO, exercise_did);
      } else {
        throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found");
      }
      return ResponseHandler.generateResponse(
          "Exercise with DID " + exercise_did + " has been updated",
          HttpStatus.OK,
          true,
          ex.stream().map(ExerciseDTO::new).toList());
    }
    throw new HttpClientErrorException(
        HttpStatus.UNAUTHORIZED, "Current user is not allowed to modify the exercise");
  }

  /**
   * Deletes a specific exercise by its DID.
   *
   * @param principal The authenticated user.
   * @param exerciseDid The UUID of the exercise.
   * @return A response entity confirming the deletion.
   * @throws HttpClientErrorException with status {@link HttpStatus#UNAUTHORIZED} if the user is not
   *     allowed to delete the exercise.
   */
  @DeleteMapping("/teacher/delete_exercise/{exercise_did}")
  public ResponseEntity<Object> deleteExercise(
      @AuthenticationPrincipal Object principal, @PathVariable("exercise_did") UUID exerciseDid) {
    if (UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      exerciseService.deleteExercise(exerciseDid);
      return ResponseHandler.generateResponse(
          "Exercise with DID " + exerciseDid + " has been deleted", HttpStatus.OK, true, null);
    }
    throw new HttpClientErrorException(
        HttpStatus.UNAUTHORIZED, "Current user is not allowed to delete the exercise");
  }

  /**
   * Deletes all exercises within a given topic.
   *
   * @param principal The authenticated user.
   * @param topicDid The UUID of the topic.
   * @return A response entity confirming the deletion.
   * @throws HttpClientErrorException with status {@link HttpStatus#UNAUTHORIZED} if the user is not
   *     allowed to delete the exercises.
   */
  @DeleteMapping("/teacher/delete_all_exercises_in_topic/{topic_did}")
  public ResponseEntity<Object> deleteAllExercisesInTopic(
      @AuthenticationPrincipal Object principal, @PathVariable("topic_did") UUID topicDid) {
    if (UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      exerciseService.deleteAllExercisesInTopic(topicDid);
      return ResponseHandler.generateResponse(
          "Exercises with topic DID " + topicDid + " have been deleted", HttpStatus.OK, true, null);
    }
    throw new HttpClientErrorException(
        HttpStatus.UNAUTHORIZED, "Current user is not allowed to delete the exercises");
  }
}
