package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing the completion status of a topic.
 *
 * <p>This record encapsulates the unique decentralized identifier (DID) of the topic, the overall
 * completion percentage for the topic, and a boolean flag indicating whether the topic contains any
 * exercises.
 *
 * @param topicDid the unique decentralized identifier (UUID) of the topic
 * @param completionPercentage the overall completion percentage for the topic
 * @param hasExercises a flag indicating whether the topic has any exercises
 */
public record CompletionTopicDTO(UUID topicDid, double completionPercentage, boolean hasExercises) {

  /**
   * Copy constructor for {@code CompletionTopicDTO}.
   *
   * <p>Creates a new instance by copying the values from an existing {@code CompletionTopicDTO}.
   *
   * @param ct the {@code CompletionTopicDTO} instance to copy
   */
  public CompletionTopicDTO(CompletionTopicDTO ct) {
    this(ct.topicDid(), ct.completionPercentage(), ct.hasExercises());
  }
}
