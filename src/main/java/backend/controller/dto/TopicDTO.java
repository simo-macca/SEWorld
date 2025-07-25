package backend.controller.dto;

import backend.model.Topic;
import java.util.UUID;

public record TopicDTO(
    UUID topicDid,
    String topicTitle,
    String topicDescription,
    Double topicProgress,
    String topicSlug,
    String topicOwner) {

  public TopicDTO(Topic topic, Double progress) {
    this(
        topic.getTopicDid(),
        topic.getTopicTitle(),
        topic.getTopicDescription(),
        progress,
        topic.getSlug(),
        topic.getTopicOwner().getName());
  }
}
