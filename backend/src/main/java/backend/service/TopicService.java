package backend.service;

import backend.controller.dto.CreateTopicDTO;
import backend.controller.dto.TopicDTO;
import backend.model.Instructor;
import backend.model.Topic;
import backend.repository.TopicRepository;
import java.security.SecureRandom;
import java.util.List;
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

  @Transactional(readOnly = true)
  public List<TopicDTO> getAllTopics() {
    List<Topic> topics = topicRepository.findAll();
    if (topics.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topics not found");
    }
    return topics.stream()
        .map(topic -> new TopicDTO(topic, secureRandom.nextDouble() * 100.0))
        .toList();
  }

  @Transactional(readOnly = true)
  public TopicDTO getTopicBySlug(String topicSlug) {
    Topic topic = getBySlug(topicSlug);
    return new TopicDTO(topic, secureRandom.nextDouble() * 100.0);
  }

  @Transactional
  public void createTopic(Instructor instructor, CreateTopicDTO createTopicDTO) {
    topicRepository.save(
        new Topic(createTopicDTO.title(), createTopicDTO.description(), instructor));
  }

  @Transactional
  public void updateTopic(String topicSlug, CreateTopicDTO topicDTO) {
    Topic topic = getBySlug(topicSlug);
    topic.update(topicDTO);
    topicRepository.save(topic);
  }

  @Transactional
  public void deleteAllTopics() {
    topicRepository.deleteAll();
  }

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
}
