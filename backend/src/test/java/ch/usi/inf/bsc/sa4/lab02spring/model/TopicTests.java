package ch.usi.inf.bsc.sa4.lab02spring.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Topic Model Test")
public class TopicTests {
  private Topic topic;
  private final String TITLE = "Glorious Title";
  private final String DESCRIPTION = "A big description";

  private Topic sTopic;
  private final Long sID = 900L;
  private final UUID sDID = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf1");
  private final String sTITLE = "A second Glorious Title";
  private final String sDESCRIPTION = "A second big description";

  private Exercise exercise;
  private final String eTITLE = "A second Glorious Title";
  private final String eDESCRIPTION = "A second big description";

  @DisplayName("Creates a new topic")
  @BeforeEach
  void setUp() {
    topic = new Topic(TITLE, DESCRIPTION);
    sTopic = new Topic(sID, sDID, sTITLE, sDESCRIPTION);
    exercise = new Exercise(eTITLE, eDESCRIPTION, topic);
  }

  @DisplayName(" which should have the right title and description, a valid did and a null id")
  @Test
  void testTopicSimpleConstructor() {
    assertEquals(TITLE, topic.getTitle());
    assertEquals(DESCRIPTION, topic.getDescription());
    assertNotNull(topic.getDid());
    assertNull(topic.getId());
    assertFalse(topic.hasMaterials());
    assertFalse(topic.hasExercises());
    assertNotEquals(topic, sTopic);
    assertNotEquals(topic.hashCode(), sTopic.hashCode());
    assertNotEquals(topic, exercise);
  }

  @DisplayName(" which should have the right title and description, a valid did and a valid id")
  @Test
  void testTopicConstructor() {
    assertEquals(sID, sTopic.getId());
    assertEquals(sDID, sTopic.getDid());
    assertEquals(sTITLE, sTopic.getTitle());
    assertEquals(sDESCRIPTION, sTopic.getDescription());
    assertFalse(sTopic.hasMaterials());
    assertFalse(sTopic.hasExercises());
    assertNotEquals(sTopic, topic);
    assertNotEquals(sTopic.hashCode(), topic.hashCode());
  }
}
