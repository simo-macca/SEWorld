package backend.service;

import backend.controller.dto.ExerciseDTO;
import backend.controller.dto.UpdateExerciseDTO;
import backend.controller.dto.create.CreateExerciseDTO;
import backend.model.Exercise;
import backend.model.Instructor;
import backend.model.Topic;
import backend.repository.ExerciseRepository;
import backend.repository.TopicRepository;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExerciseService {

  private final ExerciseRepository exerciseRepository;
  private final TopicRepository topicRepository;

  public ExerciseService(ExerciseRepository exerciseRepository, TopicRepository topicRepository) {
    this.exerciseRepository = exerciseRepository;
    this.topicRepository = topicRepository;
  }

  @Cacheable(value = "exerciseCache", key = "'allExercises'")
  @Transactional(readOnly = true)
  public List<ExerciseDTO> getAllExercises() {
    List<Exercise> exercises = exerciseRepository.findAll();
    if (exercises.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercises not found");
    }
    return exercises.stream().map(ExerciseDTO::new).toList();
  }

  @Cacheable(value = "exerciseCache", key = "'exercise-' + #exerciseSlug")
  @Transactional(readOnly = true)
  public ExerciseDTO getExerciseBySlug(String exerciseSlug) {
    return new ExerciseDTO(getBySlug(exerciseSlug));
  }

  @Cacheable(value = "exerciseCache", key = "'topic-exercises-' + #topicSlug")
  @Transactional(readOnly = true)
  public List<ExerciseDTO> getAllExercisesByTopicSlug(String topicSlug) {
    List<Exercise> exercises = exerciseRepository.findAllByExerciseTopic_TopicSlug(topicSlug);
    if (exercises.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercises not found");
    }
    return exercises.stream().map(ExerciseDTO::new).toList();
  }

  @CacheEvict(value = "exerciseCache", key = "'allExercises'")
  @Transactional
  public ExerciseDTO createExercise(Instructor instructor, String topicSlug, CreateExerciseDTO createExerciseDTO) {
    Topic topic =
        topicRepository
            .getByTopicSlug(topicSlug)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Topic not found by slug: " + topicSlug));
    Exercise exercise =
        new Exercise(createExerciseDTO.title(), createExerciseDTO.description(), topic, instructor);
    exerciseRepository.save(exercise);
    return new ExerciseDTO(exercise);
  }

  @Caching(
      evict = {
        @CacheEvict(value = "exerciseCache", key = "'exercise-' + #exerciseSlug"),
        @CacheEvict(value = "exerciseCache", key = "'allExercises'"),
        @CacheEvict(value = "exerciseCache", key = "'topic-exercises-' + #result.topicSlug")
      })
  @Transactional
  public ExerciseDTO updateExercise(String exerciseSlug, UpdateExerciseDTO updateExerciseDTO) {
    Exercise exercise = getBySlug(exerciseSlug);
    exercise.update(updateExerciseDTO);
    exerciseRepository.save(exercise);
    return new ExerciseDTO(exercise);
  }

  @Caching(
      evict = {
        @CacheEvict(value = "exerciseCache", key = "'exercise-' + #exerciseSlug"),
        @CacheEvict(value = "exerciseCache", key = "'allExercises'"),
        @CacheEvict(value = "exerciseCache", key = "'topic-exercises-' + #result.topicSlug")
      })
  @Transactional
  public ExerciseDTO publishExercise(String exerciseSlug) {
    Exercise exercise = getBySlug(exerciseSlug);
    exercise.publish();
    exerciseRepository.save(exercise);
    return new ExerciseDTO(exercise);
  }

  @CacheEvict(value = "exerciseCache", allEntries = true)
  @Transactional
  public void deleteAllExercises() {
    exerciseRepository.deleteAll();
  }

  @CacheEvict(value = "exerciseCache", allEntries = true)
  @Transactional
  public void deleteExercise(String exerciseSlug) {
    Exercise exercise = getBySlug(exerciseSlug);
    exerciseRepository.delete(exercise);
    new ExerciseDTO(exercise);
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
