package backend.controller;

import backend.controller.dto.ApiResponse;
import backend.controller.dto.CreateExerciseDTO;
import backend.controller.dto.ExerciseDTO;
import backend.model.Instructor;
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
    ApiResponse<ExerciseDTO> body = new ApiResponse<>(exercise, "Exercise found");
    return ResponseEntity.ok().body(body);
  }

  @GetMapping("get_by_slug/{slug}")
  public ResponseEntity<ApiResponse<ExerciseDTO>> getExerciseBySlug(
      @PathVariable("slug") String exerciseSlug) {
    ExerciseDTO exercise = exerciseService.getExerciseBySlug(exerciseSlug);
    ApiResponse<ExerciseDTO> body = new ApiResponse<>(exercise, "Exercise found");
    return ResponseEntity.ok(body);
  }

  @PostMapping
  public ResponseEntity<?> createExercise(
      @AuthenticationPrincipal Object principal, @RequestBody CreateExerciseDTO createExerciseDTO) {
    Instructor Instructor = (Instructor) abstractUserService.createOrFindUser(principal);
    exerciseService.createExercise(Instructor, createExerciseDTO);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse<>(null, "Exercise created"));
  }
}
