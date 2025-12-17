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

  @Transactional
  public void createTopic(Instructor instructor, CreateTopicDTO createTopicDTO) {
    topicRepository.save(
        new Topic(createTopicDTO.title(), createTopicDTO.description(), instructor));
  }

  @Transactional(readOnly = true)
  public TopicDTO getTopicBySlug(String topicSlug) {
    Topic topic =
        topicRepository
            .getByTopicSlug(topicSlug)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
    return new TopicDTO(topic, secureRandom.nextDouble() * 100.0);
  }
}
