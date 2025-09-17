package backend.controller;

import backend.controller.dto.ApiResponse;
import backend.controller.dto.CreateExerciseDTO;
import backend.controller.dto.ExerciseDTO;
import backend.model.AbstractUser;
import backend.model.Instructor;
import backend.model.Student;
import backend.service.AbstractUserService;
import backend.service.ExerciseService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/exercise")
public class ExerciseController {

  private final AbstractUserService abstractUserService;
  private final ExerciseService exerciseService;

  private final String UPDATE_ERROR = "Only instructors can update exercises";
  private final String FOUND_MESSAGE = "Exercise found";

  public ExerciseController(
      AbstractUserService abstractUserService, ExerciseService exerciseService) {
    this.abstractUserService = abstractUserService;
    this.exerciseService = exerciseService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ExerciseDTO>>> getAllExercises() {
    List<ExerciseDTO> exercise = exerciseService.getAllExercises();
    ApiResponse<List<ExerciseDTO>> body = new ApiResponse<>(exercise, "Exercises found");
    return ResponseEntity.ok().body(body);
  }

  @GetMapping("/get_by_did/{did}")
  public ResponseEntity<ApiResponse<ExerciseDTO>> getExercise(
      @PathVariable("did") UUID exerciseDid) {
    ExerciseDTO exercise = exerciseService.getExerciseByDid(exerciseDid);
    return ResponseEntity.ok().body(createBody(exercise, FOUND_MESSAGE));
  }

  @GetMapping("get_by_slug/{slug}")
  public ResponseEntity<ApiResponse<ExerciseDTO>> getExerciseBySlug(
      @PathVariable("slug") String exerciseSlug) {
    ExerciseDTO exercise = exerciseService.getExerciseBySlug(exerciseSlug);
    return ResponseEntity.ok(createBody(exercise, FOUND_MESSAGE));
  }

  @PostMapping
  public ResponseEntity<?> createExercise(
      @AuthenticationPrincipal Object principal, @RequestBody CreateExerciseDTO createExerciseDTO) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    if (isStudent(user)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(createBody(null, "Only instructors can create exercises"));
    }
    Instructor instructor = (Instructor) user;
    exerciseService.createExercise(instructor, createExerciseDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(createBody(null, "Exercise created"));
  }

  @PatchMapping("update/{slug}")
  public ResponseEntity<?> updateExercise(
      @AuthenticationPrincipal Object principal,
      @PathVariable("slug") String exerciseSlug,
      @RequestBody CreateExerciseDTO exerciseDTO) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    if (isStudent(user)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createBody(null, UPDATE_ERROR));
    }
    exerciseService.updateExercise(exerciseSlug, exerciseDTO);
    return ResponseEntity.ok().body(createBody(null, "Exercise modified"));
  }

  @PatchMapping("publish/{slug}")
  public ResponseEntity<?> publishExercise(
      @AuthenticationPrincipal Object principal, @PathVariable("slug") String exerciseSlug) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    if (isStudent(user)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createBody(null, UPDATE_ERROR));
    }
    exerciseService.publishExercise(exerciseSlug);
    return ResponseEntity.ok().body(createBody(null, "Exercise published"));
  }

    @PatchMapping("delete/{slug}")
    public ResponseEntity<?> deleteExercise(
            @AuthenticationPrincipal Object principal, @PathVariable("slug") String exerciseSlug) {
        AbstractUser user = abstractUserService.createOrFindUser(principal);
        if (isStudent(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createBody(null, UPDATE_ERROR));
        }
        exerciseService.deleteExercise(exerciseSlug);
        return ResponseEntity.ok().body(createBody(null, "Exercise deleted"));
    }

  private boolean isStudent(AbstractUser user) {
    return user instanceof Student;
  }

  private ApiResponse<ExerciseDTO> createBody(ExerciseDTO exerciseDTO, String message) {
    return new ApiResponse<>(exerciseDTO, message);
  }
}
