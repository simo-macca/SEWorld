package backend.service;

import backend.controller.dto.CreateExerciseDTO;
import backend.controller.dto.ExerciseDTO;
import backend.model.Exercise;
import backend.model.Instructor;
import backend.repository.ExerciseRepository;
import java.util.List;
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
  public ExerciseDTO getExerciseBySlug(String exerciseSlug) {
    return new ExerciseDTO(getBySlug(exerciseSlug));
  }

  @Transactional
  public void createExercise(Instructor instructor, CreateExerciseDTO createExerciseDTO) {
    exerciseRepository.save(
        new Exercise(createExerciseDTO.title(), createExerciseDTO.description(), instructor));
  }

  @Transactional
  public void updateExercise(String exerciseSlug, CreateExerciseDTO exerciseDTO) {
    Exercise exercise = getBySlug(exerciseSlug);
    exercise.update(exerciseDTO);
    exerciseRepository.save(exercise);
  }

  @Transactional
  public void publishExercise(String exerciseSlug) {
    Exercise exercise = getBySlug(exerciseSlug);
    exercise.publish();
    exerciseRepository.save(exercise);
  }

  @Transactional
  public void deleteExercise(String exerciseSlug) {
    Exercise exercise = getBySlug(exerciseSlug);
    exerciseRepository.delete(exercise);
  }

  private Exercise getBySlug(String slug) {
    return exerciseRepository
        .getByExerciseSlug(slug)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Exercise not found by slug: " + slug));
  }
}
