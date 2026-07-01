package ch.usi.inf.bsc.sa4.lab02spring.service;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.*;

import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import ch.usi.inf.bsc.sa4.lab02spring.repository.*;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.transaction.Transactional;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@Transactional
@DisplayName("Attempt Service Test")
class AttemptServiceTest {

  @Autowired UserRepository userRepository;

  @Autowired private TopicRepository topicRepository;

  @Autowired private ExerciseRepository exerciseRepository;

  @Autowired AttemptService attemptService;

  @Autowired private AttemptRepository attemptRepository;

  @Autowired private VariantRepository variantRepository;

  @Autowired private TrueFalseQuestionRepository trueFalseQuestionRepository;

  // user entities
  AbstractUser user;
  List<AbstractUser> users = new ArrayList<>();

  // topic entities
  Topic topic;
  List<Topic> topics = new ArrayList<>();

  // exercise entities
  Exercise exercise;
  List<Exercise> exercises = new ArrayList<>();

  // attempt entities
  Attempt attempt;
  List<Attempt> attempts = new ArrayList<>();

  // utils
  UUID wrongDID = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc");

  List<Variant> variants = new ArrayList<>();

  List<TrueFalseQuestion> questions = new ArrayList<>();

  // This method creates users, topics, exercises, and attempts, initializing the arrays used across
  // tests.
  @BeforeEach
  void setUp() {
    // user and topic initialization
    for (int i = 0; i < 4; i++) {
      // string test variable
      String email = "user" + (i + 1) + "@example.com";
      String name = "AbstractUser " + (i + 1);
      String subId = "Sub Id AbstractUser " + (i + 1);
      String title = "Test Title " + (i + 1);
      String description = "Test Description " + (i + 1);

      user = new Instructor(name, email, subId);
      userRepository.save(user);

      // topic creation
      topic = topicRepository.save(new Topic(title, description));
      users.add(user);
      topics.add(topic);
    }

    // exercise initialization
    for (Topic topic : topics) {
      // exercise creation
      exercise =
          exerciseRepository.save(new Exercise(topic.getTitle(), topic.getDescription(), topic));
      exercise.setExerciseIsDraft(false);
      exercises.add(exercise);
    }

    for (int i = 0; i < 4; i++) {
      Variant variant = variantRepository.save(new Variant(0, exercises.get(i)));
      variants.add(variant);
    }

    for (int i = 0; i < 4; i++) {
      TrueFalseQuestion question =
          trueFalseQuestionRepository.save(
              new TrueFalseQuestion("title", exercises.get(i), false, variants.get(i)));
      questions.add(question);
    }

    // attempt initialization
    for (AbstractUser user : users) {
      for (Exercise exercise : exercises) {
        attempt = attemptRepository.save(new Attempt(user, exercise, Collections.emptyList()));
        attempts.add(attempt);
      }
    }
  }

  @DisplayName("When creating a new attempt")
  @Nested
  class WhenCreatingNewAttempt {

    // Tests the creation of a new attempt.
    @DisplayName("Create a new attempt")
    @Test
    public void testCreateNewAttempt() {
      int i = 0, j;
      for (AbstractUser user : users) {
        j = 0;
        for (Exercise exercise : exercises) {
          // attempt creation
          var optNewAttempt =
              attemptService.createAttempt(user.getDid(), exercise.getExerciseDid(), true);
          optNewAttempt.ifPresent(attempt -> AttemptServiceTest.this.attempt = attempt);

          assert attempt != null;
          assertEquals(
              attempts.get(j).getExercise().getExerciseId(), attempt.getExercise().getExerciseId());
          j++;
        }

        assertEquals(attempts.get(i).getUser().getId(), attempt.getUser().getId());
        i += 4;
      }
    }

    // Tests the creation of a new attempt with the wrong user did.
    @DisplayName("Create a new attempt with the user that could not be found")
    @Test
    public void testCreateNewAttemptWithUserNotFound() {
      HttpClientErrorException exception =
          assertThrowsExactly(
              HttpClientErrorException.class,
              () ->
                  attemptService.createAttempt(
                      wrongDID, exercises.getFirst().getExerciseDid(), true));

      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      assertEquals("404 AbstractUser to create attempt not found", exception.getMessage());
    }

    // Tests the creation of a new attempt with the wrong exercise did
    @DisplayName("Create a new attempt with the exercise that could not be found")
    @Test
    public void testCreateNewAttemptWithExerciseNotFound() {
      HttpClientErrorException exception =
          assertThrowsExactly(
              HttpClientErrorException.class,
              () -> attemptService.createAttempt(users.getFirst().getDid(), wrongDID, true));

      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      assertEquals("404 Exercise to create attempt not found", exception.getMessage());
    }
  }

  @DisplayName("After created a new attempt")
  @Nested
  class AfterCreatedAttempt {

    @DisplayName("Getting an attempt")
    @Nested
    class GetAttempt {

      // Tests retrieving all attempts.
      @DisplayName("Get all the attempts")
      @Test
      public void testGetAllAttempts() {
        List<Attempt> allAttempts = attemptService.getAllAttempts();

        assert !allAttempts.isEmpty() : "Attempt list is empty";
        assertNotNull(allAttempts, "Attempt list is null");
        assertEquals(16, allAttempts.size(), "Number of attempts is incorrect");

        assertIterableEquals(attempts, allAttempts, "Attempt list contains wrong attempts");
      }

      // Tests retrieving all attempts when the attempt list is empty.
      @DisplayName("Get all the attempts but the list is empty")
      @Test
      public void testGetAllAttemptsEmpty() {
        for (Attempt attempt : attempts) attemptService.deleteAttemptByDid(attempt.getAttemptDid());
        HttpClientErrorException exception =
            assertThrows(HttpClientErrorException.class, () -> attemptService.getAllAttempts());

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 There are no attempts", exception.getMessage());
      }

      // Tests retrieving an attempt by a user DID.
      @DisplayName("Get all attempts by the user")
      @Test
      public void testGetAllAttemptsByUser() {
        for (AbstractUser user : users) {
          UUID userDid = user.getDid();
          List<Attempt> attemptsUser = attemptService.getAllAttemptsByUserDid(userDid);

          assertNotNull(attemptsUser, "Attempt list is null");
          assertEquals(4, attemptsUser.size(), "Number of attempts is incorrect");
          attemptsUser.forEach(
              attempt ->
                  assertEquals(
                      userDid,
                      attempt.getUser().getDid(),
                      "AbstractUser DID did not match Attempt"));
        }
      }

      // Tests retrieving an attempt by a wrong user DID.
      @DisplayName("Get all attempts by the wrong user")
      @Test
      public void testGetAllAttemptsByWrongUser() {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> attemptService.getAllAttemptsByUserDid(wrongDID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 AbstractUser not found", exception.getMessage());
      }

      // Tests retrieving all attempts by the user when the attempt list is empty.
      @DisplayName("Get all attempts by the user but the list is empty")
      @Test
      public void testGetAllAttemptsByUserEmpty() {
        attemptRepository.deleteAll();
        AbstractUser user = users.getFirst();
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () -> attemptService.getAllAttemptsByUserDid(user.getDid()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(
            "404 There are no attempts related to this user: " + user.getName(),
            exception.getMessage());
      }

      // Tests retrieving an attempt by an exercise DID.
      @DisplayName("Get all attempts by the exercise")
      @Test
      public void testGetAllAttemptsByExercise() {
        for (Exercise exercise : exercises) {
          UUID exerciseDid = exercise.getExerciseDid();
          List<Attempt> attemptByExercise = attemptService.getAllAttemptsByExerciseDid(exerciseDid);

          assertNotNull(attemptByExercise, "Attempt list is null");
          assertEquals(4, attemptByExercise.size(), "Number of attempts is incorrect");
          attemptByExercise.forEach(
              attempt ->
                  assertEquals(
                      exerciseDid,
                      attempt.getExercise().getExerciseDid(),
                      "Exercise DID did not match Attempt"));
        }
      }

      // Tests retrieving an attempt by a wrong exercise DID.
      @DisplayName("Get all attempts by the wrong exercise")
      @Test
      public void testGetAllAttemptsByWrongExercise() {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> attemptService.getAllAttemptsByExerciseDid(wrongDID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 Exercise not found", exception.getMessage());
      }

      // Tests retrieving all attempts by the exercise when the attempt list is empty.
      @DisplayName("Get all attempts by the exercise but the list is empty")
      @Test
      public void testGetAllAttemptsByExerciseEmpty() {
        attemptRepository.deleteAll();
        Exercise exercise = exercises.getFirst();
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () -> attemptService.getAllAttemptsByExerciseDid(exercise.getExerciseDid()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(
            "404 There are no attempts related to this exercise: " + exercise.getExerciseTitle(),
            exception.getMessage());
      }

      // Tests retrieving an attempt by a user and an exercise DID.
      @DisplayName("Get all attempts by the user and exercise")
      @Test
      public void testGetAllAttemptsByUserAndExercise() {
        for (AbstractUser user : users) {
          UUID userDid = user.getDid();
          for (Exercise exercise : exercises) {
            UUID exerciseDid = exercise.getExerciseDid();
            List<Attempt> attemptByUserAndExercise =
                attemptService.getAllAttemptsByUserAndExerciseDid(userDid, exerciseDid);

            assertNotNull(attemptByUserAndExercise, "Attempt list is null");
            attemptByUserAndExercise.forEach(
                attempt -> {
                  assertEquals(
                      userDid,
                      attempt.getUser().getDid(),
                      "AbstractUser DID did not match Attempt");
                  assertEquals(
                      exerciseDid,
                      attempt.getExercise().getExerciseDid(),
                      "Exercise DID did not match Attempt");
                });
          }
        }
      }

      // Tests retrieving an attempt by its DID.
      @DisplayName("Get an attempt by its DID")
      @Test
      public void testGetAttemptByDID() {
        for (Attempt attempt : attempts) {
          Optional<Attempt> attemptByDID =
              Optional.of(attemptService.getAttemptByDid(attempt.getAttemptDid()));

          assertEquals(attempt, attemptByDID.get(), "Attempt DID did not match");
        }
      }

      // Tests retrieving an attempt using a wrong DID.
      @DisplayName("Get an attempt by its wrong DID")
      @Test
      public void testGetAttemptByWrongDID() {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class, () -> attemptService.getAttemptByDid(wrongDID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(
            "404 Attempt with this DID " + wrongDID + " not found", exception.getMessage());
      }
    }

    @DisplayName("Update an attempt by its DID")
    @Nested
    class UpdateAttempt {

      // Tests updating the submitted stage of an attempt.
      @DisplayName("Update the submitted stage")
      @Test
      public void testUpdateSubmittedStage() {
        for (Attempt attempt : attempts) {
          attempt.setAttemptIsSubmitted(true);
          Optional<Attempt> updatedAttempt =
              attemptService.updateAttempt(attempt.getAttemptDid(), null);

          assert updatedAttempt.isPresent() : "Attempt DID not found";
          assertEquals(
              attempt, updatedAttempt.get(), "Attempt submitted field has not been changed");
        }
      }

      // Tests updating the completed field of an attempt.
      @DisplayName("Update the completed field")
      @Test
      public void testUpdateCompletedField() {
        for (Attempt attempt : attempts) {
          attempt.setAttemptIsCompleted(true);
          Optional<Attempt> updatedAttempt =
              attemptService.updateAttempt(attempt.getAttemptDid(), null);

          assert updatedAttempt.isPresent() : "Attempt DID not found";
          assertEquals(
              attempt, updatedAttempt.get(), "Attempt completed filed has not been changed");
        }
      }

      // Tests updating an attempt with an incorrect DID.
      @DisplayName("Update a wrong attempt")
      @Test
      public void testUpdatedWrongAttempt() {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class, () -> attemptService.updateAttempt(wrongDID, null));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 Attempt to update not found", exception.getMessage());
      }
    }

    @DisplayName("Delete an attempt")
    @Nested
    class DeleteAttempt {

      // Tests deleting an attempt by its DID.
      @DisplayName("Delete an attempt by its DID")
      @Test
      public void testDeleteAttemptByDID() {
        for (Attempt attempt : attempts) {
          UUID attemptDID = attempt.getAttemptDid();
          attemptService.deleteAttemptByDid(attemptDID);

          HttpClientErrorException exception =
              assertThrows(
                  HttpClientErrorException.class, () -> attemptService.getAttemptByDid(attemptDID));

          assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
          assertEquals(
              "404 Attempt with this DID " + attemptDID + " not found", exception.getMessage());
        }
      }

      // Tests deleting an attempt using a wrong DID.
      @DisplayName("Delete an attempt by its wrong DID")
      @Test
      public void testDeleteAttemptByWrongDID() {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class, () -> attemptService.deleteAttemptByDid(wrongDID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 Attempt to delete not found", exception.getMessage());
      }
    }
  }
}
