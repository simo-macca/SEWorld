package backend.service;

import backend.controller.dto.CreateTopicDTO;
import backend.controller.dto.TopicDTO;
import backend.model.Exercise;
import backend.model.Instructor;
import backend.model.Topic;
import backend.repository.TopicRepository;
import java.security.SecureRandom;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TopicService {

  private final TopicRepository topicRepository;

  private final SecureRandom secureRandom = new SecureRandom();

  public TopicService(TopicRepository topicRepository) {
    this.topicRepository = topicRepository;
  }

  @Cacheable(value = "SEWorldCache", key = "'allTopics'")
  @Transactional(readOnly = true)
  public List<TopicDTO> getAllTopics() {
    List<Topic> topics = topicRepository.findAll();
    if (topics.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topics not found");
    }
    return topics.stream()
        .map(topic -> new TopicDTO(topic, countProgress(topic.getExerciseList())))
        .toList();
  }

  @Cacheable(value = "SEWorldCache", key = "'topic-' + #topicSlug")
  @Transactional(readOnly = true)
  public TopicDTO getTopicBySlug(String topicSlug) {
    Topic topic = getBySlug(topicSlug);
    return new TopicDTO(topic, countProgress(topic.getExerciseList()));
  }

  @CacheEvict(value = "SEWorldCache", key = "'allTopics'")
  @Transactional
  public TopicDTO createTopic(Instructor instructor, CreateTopicDTO createTopicDTO) {
    return new TopicDTO(
        topicRepository.save(
            new Topic(createTopicDTO.title(), createTopicDTO.description(), instructor)),
        0.0);
  }

  @Caching(
      evict = {
        @CacheEvict(value = "SEWorldCache", key = "'topic-' + #topicSlug"),
        @CacheEvict(value = "SEWorldCache", key = "'allTopics'")
      })
  @Transactional
  public TopicDTO updateTopic(String topicSlug, CreateTopicDTO topicDTO) {
    Topic topic = getBySlug(topicSlug);
    topic.update(topicDTO);
    topicRepository.save(topic);
    return new TopicDTO(topic, countProgress(topic.getExerciseList()));
  }

  @CacheEvict(value = "SEWorldCache", allEntries = true)
  @Transactional
  public void deleteAllTopics() {
    topicRepository.deleteAll();
  }

  @Caching(
      evict = {
        @CacheEvict(value = "SEWorldCache", key = "'topic-' + #topicSlug"),
        @CacheEvict(value = "SEWorldCache", key = "'allTopics'")
      })
  @Transactional
  public void deleteTopicBySlug(String topicSlug) {
    topicRepository.deleteByTopicSlug(topicSlug);
  }

  private Topic getBySlug(String slug) {
    return topicRepository
        .getByTopicSlug(slug)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Topic not found by slug: " + slug));
  }

  private double countProgress(List<Exercise> exercises) {
    if (exercises == null || exercises.isEmpty()) {
      return 0.0;
    }

    long completedCount = exercises.stream().filter(Exercise::IsCompleted).count();

    return ((double) completedCount / exercises.size()) * 100;
  }
}
