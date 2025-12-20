package backend.service;

import backend.controller.dto.CreateExerciseDTO;
import backend.controller.dto.ExerciseDTO;
import backend.controller.dto.UpdateExerciseDTO;
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

  @Cacheable(value = "SEWorldCache", key = "'allExercises'")
  @Transactional(readOnly = true)
  public List<ExerciseDTO> getAllExercises() {
    List<Exercise> exercises = exerciseRepository.findAll();
    if (exercises.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercises not found");
    }
    return exercises.stream().map(ExerciseDTO::new).toList();
  }

  @Cacheable(value = "SEWorldCache", key = "#exerciseSlug")
  @Transactional(readOnly = true)
  public ExerciseDTO getExerciseBySlug(String exerciseSlug) {
    return new ExerciseDTO(getBySlug(exerciseSlug));
  }

  @Cacheable(value = "SEWorldCache", key = "#topicSlug")
  @Transactional(readOnly = true)
  public List<ExerciseDTO> getAllExercisesByTopicSlug(String topicSlug) {
    List<Exercise> exercises = exerciseRepository.findAllByExerciseTopic_TopicSlug(topicSlug);
    if (exercises.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercises not found");
    }
    return exercises.stream().map(ExerciseDTO::new).toList();
  }

  @CacheEvict(value = "SEWorldCache", key = "'allExercises'") // Clear the list
  @Transactional
  public void createExercise(Instructor instructor, CreateExerciseDTO createExerciseDTO) {
    Topic topic =
        topicRepository
            .getByTopicSlug(createExerciseDTO.topicSlug())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Topic not found by slug: " + createExerciseDTO.topicSlug()));
    exerciseRepository.save(
        new Exercise(
            createExerciseDTO.title(), createExerciseDTO.description(), topic, instructor));
  }

  @Caching(
      evict = {
        @CacheEvict(value = "SEWorldCache", key = "#exerciseSlug"), // Clear specific entry
        @CacheEvict(value = "SEWorldCache", key = "'allExercises'") // Clear the list
      })
  @Transactional
  public void updateExercise(String exerciseSlug, UpdateExerciseDTO updateExerciseDTO) {
    Exercise exercise = getBySlug(exerciseSlug);
    exercise.update(updateExerciseDTO);
    exerciseRepository.save(exercise);
  }

  @Caching(
      evict = {
        @CacheEvict(value = "SEWorldCache", key = "#exerciseSlug"), // Clear specific entry
        @CacheEvict(value = "SEWorldCache", key = "'allExercises'") // Clear the list
      })
  @Transactional
  public void publishExercise(String exerciseSlug) {
    Exercise exercise = getBySlug(exerciseSlug);
    exercise.publish();
    exerciseRepository.save(exercise);
  }

  @CacheEvict(value = "SEWorldCache", allEntries = true)
  @Transactional
  public void deleteAllExercises() {
    exerciseRepository.deleteAll();
  }

  @Caching(
      evict = {
        @CacheEvict(value = "SEWorldCache", key = "#exerciseSlug"),
        @CacheEvict(value = "SEWorldCache", key = "'allExercises'")
      })
  @Transactional
  public void deleteExercise(String exerciseSlug) {
    exerciseRepository.deleteByExerciseSlug(exerciseSlug);
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
