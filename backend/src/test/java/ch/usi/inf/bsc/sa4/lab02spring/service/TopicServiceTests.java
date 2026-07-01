package ch.usi.inf.bsc.sa4.lab02spring.service;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.CompletionTopicDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.SearchTopicDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.TopicDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Attempt;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.Topic;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AttemptRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.ExerciseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.utils.JwtTestUtil;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@Transactional
@DisplayName("Topic Service Test")
public class TopicServiceTests {

  @Autowired TopicService topicService;

  @Autowired ExerciseService exerciseService;

  @Autowired AttemptService attemptService;

  @Autowired UserService userService;

  Random rand;

  // Base title and description to create a topic
  String TITLE = "A quick look at Domain Driven Design title";
  String DESCRIPTION =
      "Critics of domain-driven design argue that developers must typically implement a great deal of isolation and encapsulation to maintain the model as a pure and helpful construct. While domain-driven design provides benefits such as maintainability, Microsoft recommends it only for complex domains where the model provides clear benefits in formulating a common understanding of the domain.";

  // Used to create and hold the last topic created
  List<Topic> topics;
  TopicDTO newTopicDto;
  Topic newTopic;

  // number of topics to init a database with
  int nTopics = 3;
  @Autowired private AttemptRepository attemptRepository;
  @Autowired private ExerciseRepository exerciseRepository;

  // A @BeforeEach and @Transactional ensure that for each test the
  // db has the same data, thus any test run as a single test is like it
  // is run by the root class. Consistency.
  @BeforeEach
  public void setUp() {
    // Sets up the SecureRandom instance
    rand = new SecureRandom();
    topics = new ArrayList<>();

    // Adds nTopics to the db for each test
    for (int i = 0; i < nTopics; i++) {
      TopicDTO tmpTopicDto = new TopicDTO(null, TITLE, DESCRIPTION);

      Topic topic;
      try {
        topic = topicService.createTopic(tmpTopicDto);
      } catch (HttpClientErrorException e) {
        throw new RuntimeException(e);
      }

      newTopic = topic;
      // Did is calculated at creation of the topic
      newTopicDto = new TopicDTO(topic.getDid(), TITLE, DESCRIPTION);
      topics.add(topic);
    }
  }

  /**
   * Returns true if two {@link List<Topic>} have elements that are equal, i.e. have the same {@link
   * Topic#getDid()}.
   *
   * <p>Runs in O(n^2)
   *
   * @param tl1 {@link List<Topic>} one
   * @param tl2 {@link List<Topic>} two
   * @return true if the two list elements are equal
   * @spec.requires Two {@link List<Topic>} to be passed in, with any elements order
   * @spec.effects No effects
   */
  public boolean topicListsEqual(Iterable<Topic> tl1, Iterable<Topic> tl2) {
    boolean equals = true;
    for (Topic t1 : tl1) {
      boolean found = false;
      for (Topic t2 : tl2) {
        if (t1.getDid().equals(t2.getDid())) {
          found = true;
          break;
        }
      }
      if (!found) {
        equals = false;
        break;
      }
    }
    return equals;
  }

  @DisplayName(" after creating a new topic")
  @Nested
  class WhenCreatingNewTopic {

    @DisplayName(" the new topic should not be null")
    @Test
    public void testTopicNotNull() {
      assertNotNull(newTopic);
    }

    @DisplayName(" the new topic fields should be equal to the topic dto")
    @Test
    public void testSameAsTopicDto() {
      assertEquals(newTopicDto.did(), newTopic.getDid());
      assertEquals(newTopicDto.title(), newTopic.getTitle());
      assertEquals(newTopicDto.description(), newTopic.getDescription());
    }

    @DisplayName(" the new topic should be returned as part of topic lists")
    @Test
    public void testTopicPresentInTopicList() {
      List<Topic> lTopics = topicService.getAllTopics();

      assert !lTopics.isEmpty();
      assertEquals(3, lTopics.size());

      Topic topic = null;
      // Look for the last topic in the list
      for (Topic t : lTopics) {
        if (t.getDid().equals(newTopic.getDid())) {
          topic = t;
        }
      }
      assertNotNull(topic);

      assertEquals(newTopicDto.did(), topic.getDid());
      assertEquals(newTopicDto.title(), topic.getTitle());
      assertEquals(newTopicDto.description(), topic.getDescription());
    }

    @DisplayName(" an exception should be returned when fetching by a null did")
    @Test
    public void testTopicGetByNullDidIsEmpty() {
      HttpClientErrorException exception =
          assertThrows(HttpClientErrorException.class, () -> topicService.getTopicByDid(null));
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @DisplayName(" an exception should be returned when fetching by a null id")
    @Test
    public void testTopicGetByNullIdIsEmpty() {
      HttpClientErrorException exception =
          assertThrows(HttpClientErrorException.class, () -> topicService.getTopicById(null));
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @DisplayName(" the new topic should be returned when fetching by its did")
    @Test
    public void testTopicPresentByDid() {
      Topic topic = topicService.getTopicByDid(newTopicDto.did());

      assertEquals(newTopicDto.did(), topic.getDid());
      assertEquals(newTopicDto.title(), topic.getTitle());
      assertEquals(newTopicDto.description(), topic.getDescription());
    }

    @DisplayName(" the new topic should be returned when fetching by its id")
    @Test
    public void testTopicPresentById() {
      Topic topic = topicService.getTopicById(newTopic.getId());

      assertEquals(newTopicDto.did(), topic.getDid());
      assertEquals(newTopicDto.title(), topic.getTitle());
      assertEquals(newTopicDto.description(), topic.getDescription());
    }

    @DisplayName(" an exception should be returned when fetching by a not present did")
    @Test
    public void testTopicInvalidSearchByDid() {
      HttpClientErrorException exception =
          assertThrows(
              HttpClientErrorException.class, () -> topicService.getTopicByDid(UUID.randomUUID()));
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @DisplayName(" an exception should be returned when fetching by a not present id")
    @Test
    public void testTopicInvalidSearchById() {
      HttpClientErrorException exception =
          assertThrows(
              HttpClientErrorException.class, () -> topicService.getTopicById(rand.nextLong()));
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @DisplayName(" a topic created with invalid dto throws an exception")
    @Test
    public void testCreateInvalidDto() {
      HttpClientErrorException exception =
          assertThrows(HttpClientErrorException.class, () -> topicService.createTopic(null));
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @DisplayName(" a topic created with null title and description throws an exception")
    @Test
    public void testCreateNullTitleAnNullDescription() {
      HttpClientErrorException exception =
          assertThrows(
              HttpClientErrorException.class,
              () -> topicService.createTopic(new TopicDTO(UUID.randomUUID(), null, null)));
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @DisplayName(" a topic created with null title throws an exception")
    @Test
    public void testCreateNullTitle() {
      HttpClientErrorException exception =
          assertThrows(
              HttpClientErrorException.class,
              () ->
                  topicService.createTopic(
                      new TopicDTO(UUID.randomUUID(), null, "A very detailed description")));
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @DisplayName(" a topic created with null description throws an exception")
    @Test
    public void testCreateNullDescription() {
      HttpClientErrorException exception =
          assertThrows(
              HttpClientErrorException.class,
              () -> topicService.createTopic(new TopicDTO(null, "A very detailed title", null)));
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @DisplayName(
        " a topic created is present in the list of all topic and can be found by id and did")
    @Test
    public void testCreateTopic() {
      String description = "A very detailed description";
      String title = "A very detailed title";

      Topic createdTopic = topicService.createTopic(new TopicDTO(null, title, description));

      assertNotNull(createdTopic);
      assertEquals(title, createdTopic.getTitle());
      assertEquals(description, createdTopic.getDescription());

      Topic idQueryTopic = topicService.getTopicById(createdTopic.getId());
      assertEquals(createdTopic.getId(), idQueryTopic.getId());
      assertEquals(createdTopic.getDid(), idQueryTopic.getDid());
      assertEquals(createdTopic.getTitle(), idQueryTopic.getTitle());
      assertEquals(createdTopic.getDescription(), idQueryTopic.getDescription());

      Topic didQueryTopic = topicService.getTopicByDid(createdTopic.getDid());
      assertEquals(createdTopic.getId(), didQueryTopic.getId());
      assertEquals(createdTopic.getDid(), didQueryTopic.getDid());
      assertEquals(createdTopic.getTitle(), didQueryTopic.getTitle());
      assertEquals(createdTopic.getDescription(), didQueryTopic.getDescription());

      List<Topic> allTopics = topicService.getAllTopics();
      boolean present = false;
      for (Topic topic : allTopics) {
        if (topic.equals(createdTopic)) {
          present = true;
          break;
        }
      }
      assertTrue(present);
    }

    @DisplayName(" a topic deleted with a null did throws an error")
    @Test
    public void testDeleteTopicByNullDid() {
      HttpClientErrorException exception =
          assertThrows(HttpClientErrorException.class, () -> topicService.deleteTopic(null));
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @DisplayName(" a topic deleted with an invalid did throws an error")
    @Test
    public void testDeleteTopicByInvalidDid() {
      HttpClientErrorException exception =
          assertThrows(
              HttpClientErrorException.class, () -> topicService.deleteTopic(UUID.randomUUID()));
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @DisplayName(" a topic deleted will not be present in the database anymore")
    @Test
    public void testDeleteTopic() {
      String description = "A very detailed description";
      String title = "A very detailed title";
      Topic createdTopic = topicService.createTopic(new TopicDTO(null, title, description));
      assertNotNull(createdTopic);

      topicService.deleteTopic(createdTopic.getDid());

      HttpClientErrorException exception =
          assertThrows(
              HttpClientErrorException.class,
              () -> topicService.getTopicById(createdTopic.getId()));
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

      exception =
          assertThrows(
              HttpClientErrorException.class,
              () -> topicService.getTopicByDid(createdTopic.getDid()));
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

      List<Topic> allTopics = topicService.getAllTopics();
      boolean present = false;
      for (Topic topic : allTopics) {
        if (topic.equals(createdTopic)) {
          present = true;
          break;
        }
      }
      assertFalse(present);
    }

    @DisplayName(" after creating multiple new topics")
    @Nested
    class WhenCreatingMultipleNewTopic {

      String NEW_TD_TITLE = "This is literally the first new title";
      String NEW_TD_DESCRIPTION =
          "Docker provides a way to package and distribute applications along with their dependencies in a containerized environment. This approach offers several advantages over installing software directly on a local machine";

      String NEW_T_TITLE = "Very second new title";

      String NEW_D_DESCRIPTION =
          "The backend application is composed of more than one component. To be exact it is made of a postgres database and a Java application. To run both and run them correctly we set up a docker compose to manage both of them as separate containers. Docker compose is nothing else than an utility to manage multiple containers at once.";

      @DisplayName(" updating with empty dto should throws an exception")
      @Test
      public void testUpdatingTopicWithEmptyDto() {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> topicService.updateTopic(newTopic.getDid(), null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
      }

      @DisplayName(" updating with empty did should throws an exception")
      @Test
      public void testUpdateTopicWithEmptyDid() {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> topicService.updateTopic(null, new TopicDTO(null, "Impossible", "DTO")));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
      }

      @DisplayName(
          " updating with empty title and description should throws an exception even with a valid uuid")
      @Test
      public void testUpdateTopicWithEmptyTitleAndDescription() {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> topicService.updateTopic(newTopic.getDid(), new TopicDTO(null, null, null)));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
      }

      @DisplayName(" updating with a did that is not present should throws an exception")
      @Test
      public void testUpdateTopicWithNotPresentDid() {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () ->
                    topicService.updateTopic(
                        UUID.randomUUID(), new TopicDTO(null, "Very Rare", "Random UUID")));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      }

      @DisplayName(" a topic can modify both title and description")
      @Test
      public void testUpdateFirstTopicTitleAndDescription() {
        List<Topic> topics = topicService.getAllTopics();
        assertEquals(3, topics.size());

        // hold unmodified element
        Topic oldTopic = topics.getFirst();

        String oldTopicTitle = oldTopic.getTitle();
        String oldTopicDescription = oldTopic.getDescription();

        Topic updatedTopic = null;
        try {
          updatedTopic =
              topicService.updateTopic(
                  oldTopic.getDid(), new TopicDTO(null, NEW_TD_TITLE, NEW_TD_TITLE));
        } catch (Exception e) {
          fail("Failed to run updateTopic: ".concat(e.getMessage()));
        }

        assertNotNull(updatedTopic);

        assertEquals(oldTopic.getDid(), updatedTopic.getDid());
        assertNotEquals(oldTopicTitle, updatedTopic.getTitle());
        assertNotEquals(oldTopicDescription, updatedTopic.getDescription());

        // Note that Spring already updates changes made using the repository
        // to the db to the element referenced by a variable and returns the
        // same object.
        assertEquals(oldTopic, updatedTopic);
        assertEquals(oldTopic.getTitle(), updatedTopic.getTitle());
        assertEquals(oldTopic.getDescription(), updatedTopic.getDescription());

        // Ensure size is still the same as it updates a row
        List<Topic> updatedTopics = topicService.getAllTopics();
        assertEquals(3, updatedTopics.size());
      }

      @DisplayName(" a topic can modify title")
      @Test
      public void testUpdateSecondTopicTitle() {
        List<Topic> topics = topicService.getAllTopics();
        assertEquals(3, topics.size());

        Topic topic = topics.getFirst();

        // hold old element
        UUID oldDid = topic.getDid();
        String oldTitle = topic.getTitle();
        String oldDescription = topic.getDescription();

        try {
          topicService.updateTopic(topic.getDid(), new TopicDTO(null, NEW_T_TITLE, null));
        } catch (Exception e) {
          fail("Failed to run updateTopic: ".concat(e.getMessage()));
        }

        assertEquals(oldDid, topic.getDid());
        assertEquals(NEW_T_TITLE, topic.getTitle());
        assertNotEquals(oldTitle, topic.getTitle());
        assertEquals(oldDescription, topic.getDescription());

        // Ensure size is still the same as it updates a row
        List<Topic> updatedTopics = topicService.getAllTopics();
        assertEquals(3, updatedTopics.size());
      }

      @DisplayName(" a topic can modify a description")
      @Test
      public void testUpdateSecondTopicDescription() {
        List<Topic> topics = topicService.getAllTopics();
        assertEquals(3, topics.size());

        Topic topic = topics.getFirst();

        // hold old element
        UUID oldDid = topic.getDid();
        String oldTitle = topic.getTitle();
        String oldDescription = topic.getDescription();

        // Update
        try {
          topicService.updateTopic(topic.getDid(), new TopicDTO(null, null, NEW_D_DESCRIPTION));
        } catch (Exception e) {
          fail("Failed to run updateTopic: ".concat(e.getMessage()));
        }

        assertEquals(oldDid, topic.getDid());
        assertEquals(oldTitle, topic.getTitle());
        assertEquals(NEW_D_DESCRIPTION, topic.getDescription());
        assertNotEquals(oldDescription, topic.getDescription());

        // Ensure size is still the same as it updates a row
        List<Topic> updatedTopics = topicService.getAllTopics();
        assertEquals(3, updatedTopics.size());
      }

      @DisplayName(" a topic can be selected by keywords")
      @Test
      public void testSearchTopicByKeywords() {
        List<Topic> topics = topicService.getAllTopics();
        assertEquals(3, topics.size());

        try {
          topicService.updateTopic(
              topics.getFirst().getDid(), new TopicDTO(null, NEW_TD_TITLE, NEW_TD_DESCRIPTION));
        } catch (Exception e) {
          fail("Failed to run updateTopic: ".concat(e.getMessage()));
        }
        assertEquals(NEW_TD_TITLE, topics.getFirst().getTitle());
        assertEquals(NEW_TD_DESCRIPTION, topics.getFirst().getDescription());

        try {
          topicService.updateTopic(
              topics.get(1).getDid(), new TopicDTO(null, NEW_T_TITLE, DESCRIPTION));
        } catch (Exception e) {
          fail("Failed to run updateTopic: ".concat(e.getMessage()));
        }
        assertEquals(NEW_T_TITLE, topics.get(1).getTitle());
        assertEquals(DESCRIPTION, topics.get(1).getDescription());

        try {
          topicService.updateTopic(
              topics.get(2).getDid(), new TopicDTO(null, null, NEW_D_DESCRIPTION));
        } catch (Exception e) {
          fail("Failed to run updateTopic: ".concat(e.getMessage()));
        }
        assertEquals(TITLE, topics.get(2).getTitle());
        assertEquals(NEW_D_DESCRIPTION, topics.get(2).getDescription());

        List<Topic> updatedTopics = topicService.getAllTopics();
        assertEquals(3, updatedTopics.size());

        // Test Keyword searches

        List<Topic> kTopics =
            topicService.searchTopicsByTitleAndDescription(new SearchTopicDTO(null, null));
        assertEquals(3, kTopics.size());
        List<Topic> shouldHave = updatedTopics;
        assert topicListsEqual(shouldHave, kTopics);

        kTopics =
            topicService.searchTopicsByTitleAndDescription(
                new SearchTopicDTO(List.of(), List.of()));
        assertEquals(3, kTopics.size());
        // shouldHave = updatedTopics; still
        assert topicListsEqual(shouldHave, kTopics);

        kTopics =
            topicService.searchTopicsByTitleAndDescription(
                new SearchTopicDTO(List.of("title"), List.of("docker")));
        assertEquals(2, kTopics.size());
        shouldHave = List.of(updatedTopics.get(0), updatedTopics.get(2));
        assert topicListsEqual(shouldHave, kTopics);

        kTopics =
            topicService.searchTopicsByTitleAndDescription(
                new SearchTopicDTO(null, List.of("BACKEND")));
        assertEquals(1, kTopics.size());
        shouldHave = List.of(updatedTopics.get(2));
        assert topicListsEqual(shouldHave, kTopics);

        kTopics =
            topicService.searchTopicsByTitleAndDescription(
                new SearchTopicDTO(List.of("This", "IS", "literally"), List.of()));
        assertEquals(1, kTopics.size());
        shouldHave = List.of(updatedTopics.getFirst());
        assert topicListsEqual(shouldHave, kTopics);
      }
    }

    @DisplayName(" after creating an user and exercises for topics")
    @Nested
    class WhenCreatingSomeUserExercisesAnsSomeAbstractUserAttempts {

      Jwt studentJwt = JwtTestUtil.createStudentJwt();
      AbstractUser user;

      // Indexed by topic by exercise, each topic has nExercises
      List<List<Exercise>> exercises;
      final int nExercises = 5;

      // Indexed by topic by exercise (since one attempt == one exercise)
      List<List<Attempt>> attempts;

      // Lists length should reflect the number of exercises a topic has since each
      // exercise has a single attempt
      final List<List<Boolean>> attemptCompleted =
          List.of(
              List.of(), // No attempts for the first one
              List.of(
                  true, false, false, false,
                  true), // 3 not completed adn 2 completed attempts for second one
              List.of() // No exercise for the third topic
              );
      final List<List<Boolean>> areExercisesTopicDraft =
          List.of(List.of(), List.of(false, false, true, false, false), List.of());
      final List<Boolean> hasExercisesTopic = List.of(true, true, false);

      @BeforeEach
      void setUp() {
        exercises = new ArrayList<>();
        attempts = new ArrayList<>();

        // Create the user
        user = userService.findOrCreateUser(studentJwt);

        // For each topic we create one exercise with a single attempt based on attemptCompleted
        for (int i = 0; i < nTopics; i++) {
          Topic topic = topics.get(i);
          exercises.add(new ArrayList<>());
          attempts.add(new ArrayList<>());

          // The third topic has no exercises, thus also no attempts
          if (i == 2) {
            continue;
          }

          for (int j = 0; j < nExercises; j++) {
            // create the exercise
            Exercise ex =
                new Exercise(
                    "Title Topic n: " + i + " Exercise n: " + j,
                    "Description Topic n: " + i + " Exercise n: " + j,
                    topic);

            exerciseRepository.save(ex);

            ex.setExerciseIsDraft(i == 1 && j == 2);

            exercises.get(i).add(ex);

            // The first topic has 5 exercises with no user attempts
            if (i == 0) {
              continue;
            }

            if (!ex.isExerciseIsDraft()) {
              Attempt attempt = new Attempt(user, ex, List.of());
              attemptRepository.save(attempt);

              // update to completion stage if it is completed
              System.out.println("attempt " + attempt.getAttemptDid() + " is:");
              if (attemptCompleted.get(i).get(j)) {
                System.out.println(" completed");
                attempt.setAttemptIsCompleted(true);
              }
              System.out.println(" uncompleted");
              attempts.get(i).add(attempt);
            }
          }
        }
      }

      @DisplayName(" completion should be calculated correctly")
      @Test
      public void testCompletionCalculatedCorrectly() {
        CompletionTopicDTO completion =
            topicService.getCompletionStageTopic(studentJwt, topics.getFirst().getDid());
        assertEquals(topics.getFirst().getDid(), completion.topicDid());
        assertTrue(completion.hasExercises());
        assertEquals(0.0, completion.completionPercentage(), 0.001);

        completion = topicService.getCompletionStageTopic(studentJwt, topics.get(1).getDid());
        assertEquals(topics.get(1).getDid(), completion.topicDid());
        assertEquals(50.0, completion.completionPercentage(), 0.001);

        completion = topicService.getCompletionStageTopic(studentJwt, topics.get(2).getDid());
        assertEquals(topics.get(2).getDid(), completion.topicDid());
        assertFalse(completion.hasExercises());
        assertEquals(100.0, completion.completionPercentage(), 0.001);
      }

      @DisplayName(" all completions should be calculated correctly")
      @Test
      public void testAllCompletionsCalculatedCorrectly() {
        List<CompletionTopicDTO> completions = topicService.getAllCompletionStageTopics(studentJwt);

        for (int i = 0; i < topics.size(); i++) {
          assertEquals(topics.get(i).getDid(), completions.get(i).topicDid());

          int completed = 0;
          int nDrafts = 0;
          for (int j = 0; j < exercises.get(i).size(); j++) {
            if (attemptCompleted.get(i).size() >= exercises.get(i).size()
                && attemptCompleted.get(i).get(j)
                && !areExercisesTopicDraft.get(i).get(j)) {
              completed++;
            }
            if (attemptCompleted.get(i).size() >= exercises.get(i).size()
                && areExercisesTopicDraft.get(i).get(j)) {
              nDrafts++;
            }
          }

          double completionPercentage =
              ((double) completed / ((double) nExercises - (double) nDrafts)) * 100.0;
          if (!this.hasExercisesTopic.get(i)) {
            completionPercentage = 100.0;
          }

          assertEquals(completionPercentage, completions.get(i).completionPercentage(), 0.001);
          assertEquals(this.hasExercisesTopic.get(i), completions.get(i).hasExercises());
        }
      }
    }
  }
}
