package backend.controller;

import static backend.Utils.*;

import backend.controller.dto.ApiResponse;
import backend.controller.dto.ExerciseDTO;
import backend.controller.dto.UpdateExerciseDTO;
import backend.controller.dto.create.CreateExerciseDTO;
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
    List<ExerciseDTO> exercises = exerciseService.getAllExercises();
    return ResponseEntity.ok().body(createBody(exercises, "Exercises found"));
  }

  @GetMapping("/{slug}")
  public ResponseEntity<ApiResponse<ExerciseDTO>> getExerciseBySlug(
      @PathVariable("slug") String exerciseSlug) {
    ExerciseDTO exercise = exerciseService.getExerciseBySlug(exerciseSlug);
    return ResponseEntity.ok(createBody(exercise, "Exercise found"));
  }

  @GetMapping("get_by_topic/{slug}")
  public ResponseEntity<ApiResponse<List<ExerciseDTO>>> getAllExercisesByTopicSlug(
      @PathVariable("slug") String topicSlug) {
    List<ExerciseDTO> exercises = exerciseService.getAllExercisesByTopicSlug(topicSlug);
    return ResponseEntity.ok(createBody(exercises, "Exercises found"));
  }

  @PostMapping("/{slug}")
  public ResponseEntity<?> createExercise(
      @AuthenticationPrincipal Object principal,@PathVariable("slug") String topicSlug, @RequestBody CreateExerciseDTO createExerciseDTO) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    return withInstructor(
        user,
        "create exercises",
        instructor -> {
          ExerciseDTO exerciseCreate =
              exerciseService.createExercise(instructor, topicSlug, createExerciseDTO);
          return ResponseEntity.status(HttpStatus.CREATED)
              .body(createBody(exerciseCreate, "Exercise created"));
        });
  }

  @PatchMapping("update/{slug}")
  public ResponseEntity<?> updateExercise(
      @AuthenticationPrincipal Object principal,
      @PathVariable("slug") String exerciseSlug,
      @RequestBody UpdateExerciseDTO updateExerciseDTO) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    return withInstructor(
        user,
        "update exercises",
        instructor -> {
          ExerciseDTO updateExercise =
              exerciseService.updateExercise(exerciseSlug, updateExerciseDTO);
          return ResponseEntity.ok().body(createBody(updateExercise, "Exercise modified"));
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
          ExerciseDTO publishExercise = exerciseService.publishExercise(exerciseSlug);
          return ResponseEntity.ok().body(createBody(publishExercise, "Exercise published"));
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
