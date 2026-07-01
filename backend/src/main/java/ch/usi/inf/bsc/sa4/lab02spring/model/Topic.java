package ch.usi.inf.bsc.sa4.lab02spring.model;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.annotation.PersistenceCreator;

/**
 * Represents a topic within the system.
 *
 * <p>This entity models a topic record stored in the "Topic" table in the database. It encapsulates
 * information about the topic such as its unique identifier (both primary key and decentralized
 * ID), title, description, and the collection of exercises associated with it.
 */
@Entity
@Table(name = "Topic")
public class Topic {

  /**
   * The primary key identifier of the topic.
   *
   * <p>This value is generated using a database sequence.
   */
  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "topics_seq")
  @SequenceGenerator(name = "topics_seq", sequenceName = "topics_seq", allocationSize = 1)
  @Column(name = "topic_id")
  private Long topicId;

  /**
   * The unique decentralized identifier (DID) of the topic.
   *
   * <p>This value is generated automatically upon topic creation and is immutable.
   */
  @Column(name = "topic_did", unique = true, nullable = false, updatable = false)
  private UUID topicDid;

  /** The title of the topic. */
  @Column(name = "topic_title", nullable = false)
  private String topicTitle;

  /**
   * The description of the topic.
   *
   * <p>This can be used to provide additional context or information about the topic.
   */
  @Column(name = "topic_description", length = 1000)
  private String topicDescription;

  /**
   * The list of exercises associated with this topic.
   *
   * <p>This one-to-many relationship links the topic to its exercises. When a topic is deleted, all
   * associated exercises are also removed.
   */
  @OneToMany(mappedBy = "topic", fetch = LAZY, cascade = REMOVE, orphanRemoval = true)
  private final List<Exercise> exercises = new ArrayList<>();

  /**
   * The list of materials associated with this topic.
   *
   * <p>This one-to-many relationship links the topic to its materials. When a topic is deleted, all
   * associated exercises are also removed.
   */
  @OneToMany(mappedBy = "topic", fetch = LAZY, cascade = REMOVE, orphanRemoval = true)
  private final List<Material> materials = new ArrayList<>();

  /**
   * No-argument constructor.
   *
   * <p>Required by JPA for instantiating the entity during data retrieval.
   */
  protected Topic() {}

  /**
   * Helper constructor.
   *
   * <p>Creates a new {@code Topic} with the specified title and description, generating a random
   * decentralized identifier.
   *
   * @param topicTitle the title of the topic
   * @param topicDescription the description of the topic
   */
  public Topic(String topicTitle, String topicDescription) {
    this.topicDid = UUID.randomUUID();
    this.topicTitle = topicTitle;
    this.topicDescription = topicDescription;
  }

  /**
   * Persistence creator constructor.
   *
   * <p>Used by Spring Data JPA when instantiating the entity from the database.
   *
   * @param topicId the primary key identifier of the topic
   * @param topicDid the unique decentralized identifier (DID) of the topic
   * @param topicTitle the title of the topic
   * @param topicDescription the description of the topic
   */
  @PersistenceCreator
  public Topic(Long topicId, UUID topicDid, String topicTitle, String topicDescription) {
    this.topicId = topicId;
    this.topicDid = topicDid;
    this.topicTitle = topicTitle;
    this.topicDescription = topicDescription;
  }

  /**
   * Returns the primary key identifier of the topic.
   *
   * @return the topic ID
   */
  public Long getId() {
    return topicId;
  }

  /**
   * Returns the unique decentralized identifier (DID) of the topic.
   *
   * @return the topic DID
   */
  public UUID getDid() {
    return topicDid;
  }

  /**
   * Returns the title of the topic.
   *
   * @return the topic title
   */
  public String getTitle() {
    return topicTitle;
  }

  /**
   * Returns the description of the topic.
   *
   * @return the topic description
   */
  public String getDescription() {
    return topicDescription;
  }

  /**
   * Returns {@code true} if the topic has materials.}
   *
   * @return if the topic has materials.
   */
  public boolean hasMaterials() {
    return !materials.isEmpty();
  }

  /**
   * Returns {@code true} if the topic has materials.}
   *
   * @return if the topic has materials.
   */
  public boolean hasExercises() {
    return !exercises.isEmpty();
  }

  /**
   * Sets the decentralized identifier (DID) of the topic.
   *
   * @param topicDid the topic DID to set
   */
  public void setDid(UUID topicDid) {
    this.topicDid = topicDid;
  }

  /**
   * Returns the current list of {@link Exercise} objects associated with this entity.
   *
   * @return the list of {@link Exercise} instances
   */
  public List<Exercise> getExercises() {
    return new ArrayList<>(exercises);
  }

  /**
   * Replaces the internal list of exercises.
   *
   * <p>If the provided {@code exercises} list is {@code null}, this method clears any existing
   * entries. Otherwise, it replaces the contents of the internal list with all elements from the
   * given list.
   *
   * @param exercises the new list of {@link Exercise} objects; may be {@code null} to clear all
   *     existing entries
   */
  public void setExercises(List<Exercise> exercises) {
    if (exercises == null) {
      this.exercises.clear();
    } else {
      this.exercises.clear();
      this.exercises.addAll(exercises);
    }
  }

  /**
   * Compares this {@code Topic} instance to another object for equality.
   *
   * <p>Two {@code Topic} instances are considered equal if they have the same {@code topicId},
   * {@code topicDid}, {@code topicTitle}, {@code topicDescription}, and {@code exercises}.
   *
   * @param o the object to compare with this {@code Topic}
   * @return {@code true} if the given object is a {@code Topic} and all fields match; {@code false}
   *     otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Topic topic)) {
      return false;
    }
    return Objects.equals(topicId, topic.topicId)
        && Objects.equals(topicDid, topic.topicDid)
        && Objects.equals(topicTitle, topic.topicTitle)
        && Objects.equals(topicDescription, topic.topicDescription)
        && Objects.equals(exercises, topic.exercises)
        && Objects.equals(materials, topic.materials);
  }

  /**
   * Returns a hash code value for this {@code Topic} instance.
   *
   * <p>The hash code is computed based on {@code topicId}, {@code topicDid}, {@code topicTitle},
   * {@code topicDescription}, and {@code exercises}.
   *
   * @return a hash code value for this {@code Topic}
   */
  @Override
  public int hashCode() {
    return Objects.hash(topicId, topicDid, topicTitle, topicDescription, exercises, materials);
  }

  @Override
  public String toString() {
    return "Topic{"
        + "topicId="
        + topicId
        + ", topicDid="
        + topicDid
        + ", topicTitle='"
        + topicTitle
        + '\''
        + ", topicDescription='"
        + topicDescription
        + '\''
        + ", exercises="
        + exercises
        + ", materials="
        + materials
        + '}';
  }
}
