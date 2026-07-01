package ch.usi.inf.bsc.sa4.lab02spring.service;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.*;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.ChangeDraftDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.ChangeExerciseDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.CreateExerciseDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.TopicDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import ch.usi.inf.bsc.sa4.lab02spring.repository.UserRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@Transactional
@DisplayName("Exercise Service Test")
class ExerciseServiceTest {

  @Autowired UserRepository userRepository;

  @Autowired TopicService topicService;

  @Autowired ExerciseService exerciseService;

  // user entities
  AbstractUser newUser;
  AbstractUser[] users;

  // topic entities
  Topic newTopic;
  Topic[] topics;

  // exercise entities
  Exercise newExercise;
  Exercise[] exercises;

  // number of users
  int nUsers = 4;
  int nTopics = 4;
  int nExercises = 4;

  // utils
  UUID wrongDID = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc");

  // This method creates users, topics, exercises, and attempts, initializing the arrays used across
  // tests.
  @BeforeEach
  void setUp() {
    // user and topic initialization
    users = new AbstractUser[nUsers];
    topics = new Topic[nTopics];
    exercises = new Exercise[nExercises];
    int i;
    for (i = 0; i < nUsers; i++) {
      // string test variable
      String email = "user" + (i + 1) + "@example.com";
      String name = "AbstractUser " + (i + 1);
      String subId = "Sub Id AbstractUser " + (i + 1);
      String title = "Test Title " + (i + 1);
      String description = "Test Description " + (i + 1);

      newUser = new Instructor(name, email, subId);
      userRepository.save(newUser);

      // topic creation
      TopicDTO tmpTopicDTO = new TopicDTO(wrongDID, title, description);
      newTopic = topicService.createTopic(tmpTopicDTO);
      users[i] = newUser;
      topics[i] = newTopic;

      // exercise creation
      CreateExerciseDTO tmpExerciseDTO =
          new CreateExerciseDTO(newTopic.getTitle(), newTopic.getDescription());
      var optNewExercise = exerciseService.createNewExercise(tmpExerciseDTO, newTopic.getDid());
      optNewExercise.ifPresent(exercise -> newExercise = exercise);
      exercises[i] = newExercise;
    }

    // creating a topic containing an exercise with draft false
    newUser = new Student("Sub Id AbstractUser 5", "studentName", "student1@example.com");
    userRepository.save(newUser);
    TopicDTO tmpTopicDTO = new TopicDTO(wrongDID, "TopicTitle", "TopicDescription");
    newTopic = topicService.createTopic(tmpTopicDTO);
    CreateExerciseDTO tmpExerciseDTO =
        new CreateExerciseDTO(newTopic.getTitle(), newTopic.getDescription());
    var optNewExercise = exerciseService.createNewExercise(tmpExerciseDTO, newTopic.getDid());
    optNewExercise.ifPresent(exercise -> newExercise = exercise);
    newExercise.setExerciseIsDraft(false);
  }

  @DisplayName("When creating a new exercise")
  @Nested
  class WhenCreatingNewExercise {

    @DisplayName("Create a new exercise")
    @Test
    void createNewExercise() {
      UUID topicDid = topics[0].getDid();
      Optional<Exercise> newEx =
          exerciseService.createNewExercise(
              new CreateExerciseDTO("Title test", "Description test"), topicDid);
      assertTrue(newEx.isPresent());
      var ex = newEx.get();
      assertEquals("Title test", ex.getExerciseTitle());
      assertEquals("Description test", ex.getExerciseDescription());
      assertTrue(ex.isExerciseIsDraft());
      assertEquals(topics[0].getDid(), ex.getTopic().getDid());
    }

    @DisplayName("Create a new exercise with wrong topic did")
    @Test
    void createNewExerciseWithWrongTopicDid() {
      HttpClientErrorException exception =
          assertThrows(
              HttpClientErrorException.class,
              () ->
                  exerciseService.createNewExercise(
                      new CreateExerciseDTO("Test title", "Description title"), wrongDID));

      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @DisplayName("Create a new exercise with one body field missing")
    @Test
    void createNewExerciseWithOneBodyFieldMissing() {
      assertThrows(
          DataIntegrityViolationException.class,
          () ->
              exerciseService.createNewExercise(
                  new CreateExerciseDTO(null, "Description title"), topics[0].getDid()));
    }
  }

  @DisplayName("After create an exercise")
  @Nested
  class AfterCreateNewExercise {

    @DisplayName("Getting an exercise")
    @Nested
    class GettingAnExercise {

      @DisplayName("Get all the exercise by topic did as teacher")
      @Test
      void getAllExercises() {
        for (Topic topic : topics) {
          UUID topicDid = topic.getDid();
          List<Exercise> exercises = exerciseService.getAllExercises(topicDid, true);
          assert !exercises.isEmpty() : "Exercise list is empty";
          assertNotNull(exercises, "Exercise list is null");
          assertEquals(1, exercises.size());
          exercises.forEach(exercise -> assertEquals(topicDid, exercise.getTopic().getDid()));
        }
      }

      @DisplayName("Get all the exercise of a topic but the list is empty as teacher")
      @Test
      void getAllExercisesEmpty() {
        Topic topic = topicService.createTopic(new TopicDTO(wrongDID, "Title", "Descr"));
        UUID topicDid = topic.getDid();
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> exerciseService.getAllExercises(topicDid, true));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      }

      @DisplayName("Get all the exercise using a wrong topic did as teacher")
      @Test
      void getAllExerciseByWrongTopicDid() {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> exerciseService.getAllExercises(wrongDID, true));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      }

      @DisplayName("Get all the exercise of a topic as student1")
      @Test
      void getAllExercisesAsStudent() {
        UUID topicDid = newTopic.getDid();
        List<Exercise> exercises = exerciseService.getAllExercises(topicDid, false);
        assert !exercises.isEmpty() : "Exercise list is empty";
        assertNotNull(exercises, "Exercise list is null");
        assertEquals(1, exercises.size());
        assertEquals(topicDid, exercises.get(0).getTopic().getDid());
      }

      @DisplayName("Get all the exercise of a topic as student1 but the draft is true")
      @Test
      void getAllExercisesAsStudentDraft() {
        UUID topicDid = topics[0].getDid();
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> exerciseService.getAllExercises(topicDid, false));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      }

      @DisplayName("Get exercise by DID as teacher")
      @Test
      void getExerciseByDID() {
        UUID exerciseDid = exercises[0].getExerciseDid();
        Optional<Exercise> exercise = exerciseService.getExerciseByDid(exerciseDid, true);
        assertTrue(exercise.isPresent());
        assertEquals("Test Title 1", exercise.get().getExerciseTitle());
        assertEquals("Test Description 1", exercise.get().getExerciseDescription());
        assertTrue(exercise.get().isExerciseIsDraft());
        assertEquals(exerciseDid, exercise.get().getExerciseDid());
      }

      @DisplayName("Get exercise by wrong DID as teacher")
      @Test
      void getExerciseByWrongDID() {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> exerciseService.getExerciseByDid(wrongDID, true));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      }

      @DisplayName("Get exercise by DID as student1")
      @Test
      void getExerciseByDIDAsStudent() {
        UUID exerciseDid = newExercise.getExerciseDid();
        Optional<Exercise> exercise = exerciseService.getExerciseByDid(exerciseDid, false);
        assertTrue(exercise.isPresent());
        assertEquals("TopicTitle", exercise.get().getExerciseTitle());
        assertEquals("TopicDescription", exercise.get().getExerciseDescription());
        assertFalse(exercise.get().isExerciseIsDraft());
        assertEquals(exerciseDid, exercise.get().getExerciseDid());
      }

      @DisplayName("Get exercise by DID as student1 but draft is true")
      @Test
      void getExerciseByDIDAsStudentDraft() {
        UUID exerciseDid = exercises[0].getExerciseDid();
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> exerciseService.getExerciseByDid(exerciseDid, false));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      }
    }

    @DisplayName("Update an exercise")
    @Nested
    class UpdateAnExercise {

      @DisplayName("Update exercise draft from true to false")
      @Test
      void updateExerciseDraft() {
        UUID exerciseDid = exercises[0].getExerciseDid();
        Optional<Exercise> exercise =
            exerciseService.patchDraft(new ChangeDraftDTO(false), exerciseDid);
        assertTrue(exercise.isPresent());
        assertFalse(exercise.get().isExerciseIsDraft());
      }

      @DisplayName("Update exercise draft from false to true")
      @Test
      void updateExerciseDraftFalse() {
        UUID exerciseDid = exercises[0].getExerciseDid();
        exercises[0] =
            exerciseService.patchDraft(new ChangeDraftDTO(false), exerciseDid).orElseThrow();
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> exerciseService.patchDraft(new ChangeDraftDTO(true), exerciseDid));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
      }

      @DisplayName("Update exercise title and description")
      @Test
      void updateExerciseTitleAndDescription() {
        UUID exerciseDid = exercises[0].getExerciseDid();
        Optional<Exercise> exercise =
            exerciseService.patchExercise(
                new ChangeExerciseDTO("newTitle", "newDescription"), exerciseDid);
        assertTrue(exercise.isPresent());
        assertEquals("newTitle", exercise.get().getExerciseTitle());
        assertEquals("newDescription", exercise.get().getExerciseDescription());
      }

      @DisplayName("Update exercise with wrong did")
      @Test
      void updateExerciseWithWrongDid() {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () ->
                    exerciseService.patchExercise(
                        new ChangeExerciseDTO("Title", "Description"), wrongDID));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      }

      @DisplayName("Update only one exercise field")
      @Test
      void updateExerciseOnlyOneField() {
        UUID exerciseDid = exercises[0].getExerciseDid();
        Optional<Exercise> exercise =
            exerciseService.patchExercise(new ChangeExerciseDTO("newTitle", null), exerciseDid);
        assertTrue(exercise.isPresent());
        assertEquals("newTitle", exercise.get().getExerciseTitle());
        assertEquals("Test Description 1", exercise.get().getExerciseDescription());
      }
    }

    @DisplayName("Search an exercise by keywords")
    @Nested
    class SearchAnExercise {

      @DisplayName("Search an exercise by title keywords")
      @Test
      void searchAnExerciseByTitleKeywords() {
        List<String> titleKeywords = List.of("Title");
        List<String> descriptionKeywords = List.of();
        boolean isDraft = true;
        int i = 1;
        for (Topic topic : topics) {
          List<Exercise> searchByKeywords =
              exerciseService.searchByKeywords(
                  titleKeywords, descriptionKeywords, isDraft, topic.getDid(), true);
          assertNotNull(searchByKeywords, "Exercise list is empty");
          assertEquals(1, searchByKeywords.size());
          for (Exercise exercise : searchByKeywords)
            assertTrue(exercise.getExerciseTitle().contains("Test Title " + i));
          i++;
        }
      }

      @DisplayName("Search an exercise by description keywords")
      @Test
      void searchAnExerciseByDescriptionKeywords() {
        List<String> titleKeywords = List.of();
        List<String> descriptionKeywords = List.of("Description");
        boolean isDraft = true;
        int i = 1;
        for (Topic topic : topics) {
          List<Exercise> searchByKeywords =
              exerciseService.searchByKeywords(
                  titleKeywords, descriptionKeywords, isDraft, topic.getDid(), true);
          assertNotNull(searchByKeywords, "Exercise list is empty");
          assertEquals(1, searchByKeywords.size());
          for (Exercise exercise : searchByKeywords)
            assertTrue(exercise.getExerciseDescription().contains("Test Description " + i));
          i++;
        }
      }

      @DisplayName("Search an exercise by isExerciseIsDraft keywords")
      @Test
      void searchAnExerciseByIsExerciseIsDraftKeywords() {
        List<String> titleKeywords = List.of();
        List<String> descriptionKeywords = List.of();
        boolean isDraft = true;
        for (Topic topic : topics) {
          List<Exercise> searchByKeywords =
              exerciseService.searchByKeywords(
                  titleKeywords, descriptionKeywords, isDraft, topic.getDid(), true);
          assertNotNull(searchByKeywords, "Exercise list is empty");
          assertEquals(1, searchByKeywords.size());
          searchByKeywords.forEach(exercise -> assertTrue(exercise.isExerciseIsDraft()));
        }
      }

      @DisplayName("Search an exercise by all field keywords")
      @Test
      void searchAnExerciseByAllFieldKeywords() {
        List<String> titleKeywords = List.of("Title");
        List<String> descriptionKeywords = List.of("Description");
        boolean isDraft = true;
        int i = 1;
        for (Topic topic : topics) {
          List<Exercise> searchByKeywords =
              exerciseService.searchByKeywords(
                  titleKeywords, descriptionKeywords, isDraft, topic.getDid(), true);
          assertNotNull(searchByKeywords, "Exercise list is empty");
          assertEquals(1, searchByKeywords.size());
          for (Exercise exercise : searchByKeywords) {
            assertTrue(exercise.getExerciseTitle().contains("Test Title " + i));
            assertTrue(exercise.getExerciseDescription().contains("Test Description " + i));
            assertTrue(exercise.isExerciseIsDraft());
          }
          i++;
        }
      }

      @DisplayName("Search an exercise by the wrong topic DID")
      @Test
      void searchAnExerciseByWrongTopicDID() {
        List<String> titleKeywords = List.of("Title");
        List<String> descriptionKeywords = List.of("Description");
        boolean isDraft = true;
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () ->
                    exerciseService.searchByKeywords(
                        titleKeywords, descriptionKeywords, isDraft, wrongDID, true));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Topic not found"));
      }

      @DisplayName("Search an exercise but the list is empty")
      @Test
      void searchAnExerciseButEmpty() {
        List<String> titleKeywords = List.of();
        List<String> descriptionKeywords = List.of();
        boolean isDraft = false;
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () ->
                    exerciseService.searchByKeywords(
                        titleKeywords, descriptionKeywords, isDraft, topics[0].getDid(), true));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Exercises not found"));
      }
    }

    @DisplayName("Delete an exercise")
    @Nested
    class DeleteAnExercise {

      @DisplayName("Delete an exercise by its DID as teacher")
      @Test
      void deleteAnExerciseByDid() {
        UUID exerciseDid = exercises[0].getExerciseDid();
        exerciseService.deleteExercise(exerciseDid);
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> exerciseService.getExerciseByDid(exerciseDid, true));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      }

      @DisplayName("Delete an exercise bi its wrong DID")
      @Test
      void deleteAnExerciseByWrongDid() {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class, () -> exerciseService.deleteExercise(wrongDID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 Exercise not found", exception.getMessage());
      }

      @DisplayName("Delete all exercise by topic as teacher")
      @Test
      void deleteAllExerciseByTopic() {
        for (Topic topic : topics) {
          UUID topicDid = topic.getDid();
          exerciseService.deleteAllExercisesInTopic(topicDid);
          HttpClientErrorException exception =
              assertThrows(
                  HttpClientErrorException.class,
                  () -> exerciseService.getAllExercises(topicDid, true));
          assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        }
      }

      @DisplayName("Delete all exercise by wrong topic")
      @Test
      void deleteAllExerciseByWrongTopic() {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> exerciseService.deleteAllExercisesInTopic(wrongDID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 Topic not found", exception.getMessage());
      }
    }
  }
}
