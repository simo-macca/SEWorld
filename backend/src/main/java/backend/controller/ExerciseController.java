package backend.controller;

import static backend.Utils.*;

import backend.controller.dto.ApiResponse;
import backend.controller.dto.CreateExerciseDTO;
import backend.controller.dto.ExerciseDTO;
import backend.model.AbstractUser;
import backend.service.AbstractUserService;
import backend.service.ExerciseService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/exercise")
public class ExerciseController {

  private final AbstractUserService abstractUserService;
  private final ExerciseService exerciseService;

  public ExerciseController(
      AbstractUserService abstractUserService, ExerciseService exerciseService) {
    this.abstractUserService = abstractUserService;
    this.exerciseService = exerciseService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ExerciseDTO>>> getAllExercises() {
    List<ExerciseDTO> exercise = exerciseService.getAllExercises();
    return ResponseEntity.ok().body(createBody(exercise, "Exercises found"));
  }

  @GetMapping("/{slug}")
  public ResponseEntity<ApiResponse<ExerciseDTO>> getExerciseBySlug(
      @PathVariable("slug") String exerciseSlug) {
    ExerciseDTO exercise = exerciseService.getExerciseBySlug(exerciseSlug);
    return ResponseEntity.ok(createBody(exercise, "Exercise found"));
  }

  @PostMapping
  public ResponseEntity<?> createExercise(
      @AuthenticationPrincipal Object principal, @RequestBody CreateExerciseDTO createExerciseDTO) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    return withInstructor(
        user,
        "create exercises",
        instructor -> {
          exerciseService.createExercise(instructor, createExerciseDTO);
          return ResponseEntity.status(HttpStatus.CREATED).body(createBody("Exercise created"));
        });
  }

  @PatchMapping("update/{slug}")
  public ResponseEntity<?> updateExercise(
      @AuthenticationPrincipal Object principal,
      @PathVariable("slug") String exerciseSlug,
      @RequestBody CreateExerciseDTO exerciseDTO) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    return withInstructor(
        user,
        "update exercises",
        instructor -> {
          exerciseService.updateExercise(exerciseSlug, exerciseDTO);
          return ResponseEntity.ok().body(createBody("Exercise modified"));
        });
  }

  @PatchMapping("publish/{slug}")
  public ResponseEntity<?> publishExercise(
      @AuthenticationPrincipal Object principal, @PathVariable("slug") String exerciseSlug) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    return withInstructor(
        user,
        "publish exercises",
        instructor -> {
          exerciseService.publishExercise(exerciseSlug);
          return ResponseEntity.ok().body(createBody("Exercise published"));
        });
  }

  @DeleteMapping
  public ResponseEntity<?> deleteAllExercises(@AuthenticationPrincipal Object principal) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    return withInstructor(
        user,
        "delete exercises",
        instructor -> {
          exerciseService.deleteAllExercises();
          return ResponseEntity.ok().body(createBody("All exercises deleted"));
        });
  }

  @DeleteMapping("/{slug}")
  public ResponseEntity<?> deleteExercise(
      @AuthenticationPrincipal Object principal, @PathVariable("slug") String exerciseSlug) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    return withInstructor(
        user,
        "delete exercises",
        instructor -> {
          exerciseService.deleteExercise(exerciseSlug);
          return ResponseEntity.ok().body(createBody("Exercise deleted"));
        });
  }
}
