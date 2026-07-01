package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import ch.usi.inf.bsc.sa4.lab02spring.model.Topic;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a topic along with availability indicators.
 *
 * <p>Encapsulates a topic’s unique identifier, title, description, and flags indicating whether
 * materials or exercises are available for that topic.
 *
 * @param did the unique identifier of the topic (UUID)
 * @param title the title of the topic
 * @param description a brief description of the topic
 * @param materialsAvailable {@code true} if the topic has one or more associated materials; {@code
 *     false} otherwise
 * @param exercisesAvailable {@code true} if the topic has one or more associated exercises; {@code
 *     false} otherwise
 */
public record TopicDTO(
    UUID did,
    String title,
    String description,
    boolean materialsAvailable,
    boolean exercisesAvailable) {

  /**
   * Constructs a {@code TopicDTO} from a {@link Topic} model instance.
   *
   * <p>Maps the model’s properties to the DTO’s components, including material and exercise
   * availability.
   *
   * @param topic the {@link Topic} model instance to convert
   */
  public TopicDTO(Topic topic) {
    this(
        topic.getDid(),
        topic.getTitle(),
        topic.getDescription(),
        topic.hasMaterials(),
        topic.hasExercises());
  }

  /**
   * Constructs a {@code TopicDTO} with availability flags defaulting to {@code false}.
   *
   * <p>Use this constructor when availability details are not required or not yet determined.
   *
   * @param did the unique identifier of the topic
   * @param title the title of the topic
   * @param description a brief description of the topic
   */
  public TopicDTO(UUID did, String title, String description) {
    this(did, title, description, false, false);
  }
}
