package backend.service;

import backend.controller.dto.CreateExerciseDTO;
import backend.controller.dto.ExerciseDTO;
import backend.model.Exercise;
import backend.model.Instructor;
import backend.repository.ExerciseRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExerciseService {

  private final ExerciseRepository exerciseRepository;

  public ExerciseService(ExerciseRepository exerciseRepository) {
    this.exerciseRepository = exerciseRepository;
  }

  @Transactional(readOnly = true)
  public List<ExerciseDTO> getAllExercises() {
    List<Exercise> exercises = exerciseRepository.findAll();
    if (exercises.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercises not found");
    }
    return exercises.stream().map(ExerciseDTO::new).toList();
  }

  @Transactional(readOnly = true)
  public ExerciseDTO getExerciseByDid(UUID did) {
    Exercise exercise =
        exerciseRepository
            .getByExerciseDid(did)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));
    return new ExerciseDTO(exercise);
  }

  @Transactional
  public void createExercise(Instructor instructor, CreateExerciseDTO createExerciseDTO) {
    exerciseRepository.save(
        new Exercise(createExerciseDTO.title(), createExerciseDTO.description(), instructor));
  }

  @Transactional(readOnly = true)
  public ExerciseDTO getExerciseBySlug(String exerciseSlug) {
    Exercise exercise =
        exerciseRepository
            .getByExerciseSlug(exerciseSlug)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));
    return new ExerciseDTO(exercise);
  }
}
