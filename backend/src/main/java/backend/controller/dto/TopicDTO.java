package backend.controller.dto;

import backend.model.Topic;

public record TopicDTO(
    String topicTitle,
    String topicDescription,
    Double topicProgress,
    String topicSlug,
    String topicOwner) {

  public TopicDTO(Topic topic, Double progress) {
    this(
        topic.getTopicTitle(),
        topic.getTopicDescription(),
        progress,
        topic.getTopicSlug(),
        topic.getTopicOwner().getName());
  }

  public TopicDTO(Topic topic, Double progress, String topicSlug) {
    this(
            topic.getTopicTitle(),
            topic.getTopicDescription(),
            progress,
            topicSlug,
            topic.getTopicOwner().getName());
  }
}
