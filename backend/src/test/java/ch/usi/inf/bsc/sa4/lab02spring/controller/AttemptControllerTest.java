package ch.usi.inf.bsc.sa4.lab02spring.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AttemptDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import ch.usi.inf.bsc.sa4.lab02spring.service.AttemptService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.JwtTestUtil;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("Attempt Controller Test")
public class AttemptControllerTest {

  @MockitoBean private UserService userService;

  @MockitoBean private AttemptService attemptService;

  @Autowired private MockMvc mockMvc;

  Jwt jwt;
  AbstractUser mockStudent;
  FakeUser fakeUser;

  private static final String TOPIC_TITLE = "Topic Title Test 1";
  private static final String TOPIC_DESCRIPTION = "Topic Description Test 1";

  private Exercise exercise;
  private static final String EXERCISE_TITLE = "Exercise Test 1";
  private static final String EXERCISE_DESCRIPTION = "Exercise Description Test 1";

  private Attempt attempt1;
  private List<Attempt> attempts;

  UUID wrongDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc");
  UUID userDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfa");
  UUID topicDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfb");
  UUID exerciseDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfd");

  @BeforeEach
  public void setup() {
    // user
    jwt = JwtTestUtil.createStudentJwt();
    mockStudent = mock(AbstractUser.class);
    when(mockStudent.getRole()).thenReturn("STUDENT");
    when(mockStudent.getDid()).thenReturn(userDid);
    when(userService.findOrCreateUser(any())).thenReturn(mockStudent);

    // fake user
    fakeUser =
        new FakeUser(
            mockStudent.getDid(),
            mockStudent.getSubId(),
            mockStudent.getName(),
            mockStudent.getEmail());

    // topic
    Topic topic = new Topic(900L, topicDid, TOPIC_TITLE, TOPIC_DESCRIPTION);

    // exercise
    exercise = new Exercise(EXERCISE_TITLE, EXERCISE_DESCRIPTION, topic);
    exercise.setExerciseDid(exerciseDid);

    // attempt
    attempt1 =
        new Attempt(
            mockStudent,
            exercise,
            List.of(new TrueFalseQuestion("titel", exercise, false, new Variant(0, exercise))));
    Attempt attempt2 = new Attempt(mockStudent, exercise, Collections.emptyList());
    // list of attempts
    attempts = Arrays.asList(attempt1, attempt2);
  }

  @DisplayName("When creating a new attempt")
  @Nested
  class WhenCreatingNewAttempt {

    // Tests the creation of a new attempt.
    @DisplayName("Create a new attempt")
    @Test
    public void testCreateNewAttempt() throws Exception {
      when(attemptService.createAttempt(mockStudent.getDid(), exercise.getExerciseDid(), true))
          .thenReturn(Optional.of(attempt1));
      when(attemptService.getDTO(attempt1))
          .thenReturn(new AttemptDTO(attempt1, exercise.getExerciseDid()));

      mockMvc
          .perform(
              post("/api/auth/topic/exercises/attempts/create/" + exercise.getExerciseDid())
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken()))
                  .with(csrf()))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.status_code").value(201))
          .andExpect(jsonPath("$.message").value("Attempt successfully created"))
          .andExpect(jsonPath("$.status").value("success"))
          .andExpect(jsonPath("$.data").isArray())
          .andExpect(jsonPath("$.data", hasSize(1)))
          .andExpect(jsonPath("$.data[0].attemptDID").exists())
          .andExpect(jsonPath("$.data[0].attemptIsSubmitted").value(false))
          .andExpect(jsonPath("$.data[0].attemptIsCompleted").value(false));
    }

    // Tests the creation of a new attempt with the wrong exercise did
    @DisplayName("Create a new attempt with the exercise that could not be found")
    @Test
    public void testCreateNewAttemptWithExerciseNotFound() throws Exception {
      when(attemptService.createAttempt(mockStudent.getDid(), wrongDid, true))
          .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found"));

      mockMvc
          .perform(
              post("/api/auth/topic/exercises/attempts/create/" + wrongDid)
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken()))
                  .with(csrf()))
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data").doesNotExist())
          .andExpect(jsonPath("$.message").value("Exercise not found"))
          .andExpect(jsonPath("$.status").value("error"));
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
      public void testGetAllAttempts() throws Exception {
        when(attemptService.getAllAttempts()).thenReturn(attempts);
        when(attemptService.getDTO(attempt1))
            .thenReturn(new AttemptDTO(attempt1, exercise.getExerciseDid()));

        mockMvc
            .perform(
                get("/api/auth/topic/exercises/attempts")
                    .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data", hasSize(attempts.size())))
            .andExpect(jsonPath("$.data[0].attemptDID").exists());
      }

      // Tests retrieving all attempts when the attempt list is empty.
      @DisplayName("Get all the attempts but the list is empty")
      @Test
      public void testGetAllAttemptsEmpty() throws Exception {
        when(attemptService.getAllAttempts())
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "There are no attempts"));

        mockMvc
            .perform(
                get("/api/auth/topic/exercises/attempts")
                    .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.message").value("There are no attempts"))
            .andExpect(jsonPath("$.status").value("error"));
      }

      // Tests retrieving an attempt by a user DID.
      @DisplayName("Get all attempts by the user")
      @Test
      public void testGetAllAttemptsByUser() throws Exception {
        when(attemptService.getAllAttemptsByUserDid(mockStudent.getDid())).thenReturn(attempts);
        when(attemptService.getDTO(attempt1))
            .thenReturn(new AttemptDTO(attempt1, exercise.getExerciseDid()));

        mockMvc
            .perform(
                get("/api/auth/topic/exercises/attempts/get_all_by_user")
                    .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data", hasSize(attempts.size())))
            .andExpect(jsonPath("$.data[0].attemptDID").exists());
      }

      // Tests retrieving all attempts by the user when the attempt list is empty.
      @DisplayName("Get all attempts by the user but the list is empty")
      @Test
      public void testGetAllAttemptsByUserEmpty() throws Exception {
        when(attemptService.getAllAttemptsByUserDid(mockStudent.getDid()))
            .thenThrow(
                new HttpClientErrorException(
                    HttpStatus.NOT_FOUND, "There are not attempts for the user"));

        mockMvc
            .perform(
                get("/api/auth/topic/exercises/attempts/get_all_by_user")
                    .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.message").value("There are not attempts for the user"))
            .andExpect(jsonPath("$.status").value("error"));
      }

      // Tests retrieving an attempt by an exercise DID.
      @DisplayName("Get all attempts by the exercise")
      @Test
      public void testGetAllAttemptsByExercise() throws Exception {
        when(attemptService.getAllAttemptsByExerciseDid(exercise.getExerciseDid()))
            .thenReturn(attempts);
        when(attemptService.getDTO(attempt1))
            .thenReturn(new AttemptDTO(attempt1, exercise.getExerciseDid()));
        mockMvc
            .perform(
                get("/api/auth/topic/exercises/attempts/get_all_by_exercise/"
                        + exercise.getExerciseDid())
                    .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data", hasSize(attempts.size())))
            .andExpect(jsonPath("$.data[0].attemptDID").exists());
      }

      // Tests retrieving an attempt by a wrong exercise DID.
      @DisplayName("Get all attempts by the wrong exercise")
      @Test
      public void testGetAllAttemptsByWrongExercise() throws Exception {
        when(attemptService.getAllAttemptsByExerciseDid(wrongDid))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found"));

        mockMvc
            .perform(
                get("/api/auth/topic/exercises/attempts/get_all_by_exercise/" + wrongDid)
                    .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.message").value("Exercise not found"))
            .andExpect(jsonPath("$.status").value("error"));
      }

      // Tests retrieving all attempts by the exercise when the attempt list is empty.
      @DisplayName("Get all attempts by the exercise but the list is empty")
      @Test
      public void testGetAllAttemptsByExerciseEmpty() throws Exception {
        when(attemptService.getAllAttemptsByExerciseDid(exercise.getExerciseDid()))
            .thenThrow(
                new HttpClientErrorException(
                    HttpStatus.NOT_FOUND,
                    "There are no attempts related to this exercise: "
                        + exercise.getExerciseTitle()));

        mockMvc
            .perform(
                get("/api/auth/topic/exercises/attempts/get_all_by_exercise/"
                        + exercise.getExerciseDid())
                    .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(
                jsonPath("$.message")
                    .value("There are no attempts related to this exercise: Exercise Test 1"))
            .andExpect(jsonPath("$.status").value("error"));
      }

      // Tests retrieving an attempt by a user and an exercise DID.
      @DisplayName("Get all attempts by the user and exercise")
      @Test
      public void testGetAllAttemptsByUserAndExercise() throws Exception {
        when(attemptService.getAllAttemptsByUserAndExerciseDid(
                mockStudent.getDid(), exercise.getExerciseDid()))
            .thenReturn(attempts);
        when(attemptService.getDTO(attempt1))
            .thenReturn(new AttemptDTO(attempt1, exercise.getExerciseDid()));
        mockMvc
            .perform(
                get("/api/auth/topic/exercises/attempts/get_all_by_user_exercise/"
                        + exercise.getExerciseDid())
                    .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data", hasSize(attempts.size())))
            .andExpect(jsonPath("$.data[0].attemptDID").exists());
      }

      // Tests retrieving an attempt by its DID.
      @DisplayName("Get an attempt by its DID")
      @Test
      public void testGetAttemptByDID() throws Exception {
        when(attemptService.getAttemptByDid(attempt1.getAttemptDid())).thenReturn(attempt1);
        when(attemptService.getDTO(attempt1))
            .thenReturn(new AttemptDTO(attempt1, exercise.getExerciseDid()));
        mockMvc
            .perform(
                get("/api/auth/topic/exercises/attempts/get_by_did/" + attempt1.getAttemptDid())
                    .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.attemptDID").value(attempt1.getAttemptDid().toString()));
      }

      // Tests retrieving an attempt using a wrong DID.
      @DisplayName("Get an attempt by its wrong DID")
      @Test
      public void testGetAttemptByWrongDID() throws Exception {
        when(attemptService.getAttemptByDid(wrongDid))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Attempt not found"));

        mockMvc
            .perform(
                get("/api/auth/topic/exercises/attempts/get_by_did/" + wrongDid)
                    .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.message").value("Attempt not found"))
            .andExpect(jsonPath("$.status").value("error"));
      }
    }

    @DisplayName("Update an attempt by its DID")
    @Nested
    class UpdateAttempt {

      // Tests updating the submitted stage of an attempt.
      @DisplayName("Update the submitted stage")
      @Test
      public void testUpdateSubmittedStage() throws Exception {
        Attempt updateAttempt = attempt1;
        updateAttempt.setAttemptIsSubmitted(true);
        when(attemptService.updateAttempt(attempt1.getAttemptDid(), fakeUser))
            .thenReturn(Optional.of(updateAttempt));
        when(attemptService.getDTO(attempt1))
            .thenReturn(new AttemptDTO(attempt1, exercise.getExerciseDid()));

        mockMvc
            .perform(
                patch("/api/auth/topic/exercises/attempts/update/" + attempt1.getAttemptDid())
                    .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken()))
                    .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.attemptDID").value(attempt1.getAttemptDid().toString()))
            .andExpect(jsonPath("$.data.attemptIsSubmitted").value(true))
            .andExpect(jsonPath("$.data.attemptIsCompleted").value(false));
      }

      // Tests updating an attempt with an incorrect DID.
      @DisplayName("Update a wrong attempt")
      @Test
      public void testUpdatedWrongAttempt() throws Exception {
        Attempt updateAttempt = attempt1;
        updateAttempt.setAttemptIsSubmitted(true);
        when(attemptService.updateAttempt(wrongDid, fakeUser))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Attempt not found"));

        mockMvc
            .perform(
                patch("/api/auth/topic/exercises/attempts/update/" + wrongDid)
                    .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken()))
                    .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.message").value("Attempt not found"))
            .andExpect(jsonPath("$.status").value("error"));
      }
    }

    @DisplayName("Delete an attempt")
    @Nested
    class DeleteAttempt {

      // Tests deleting an attempt by its DID.
      @DisplayName("Delete an attempt by its DID")
      @Test
      public void testDeleteAttemptByDID() throws Exception {
        doNothing().when(attemptService).deleteAttemptByDid(attempt1.getAttemptDid());

        mockMvc
            .perform(
                delete("/api/auth/topic/exercises/attempts/delete/" + attempt1.getAttemptDid())
                    .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken()))
                    .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.message").value("Attempt successfully deleted"))
            .andExpect(jsonPath("$.status").value("success"));
      }

      // Tests deleting an attempt using a wrong DID.
      @DisplayName("Delete an attempt by its wrong DID")
      @Test
      public void testDeleteAttemptByWrongDID() throws Exception {
        doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Attempt not found"))
            .when(attemptService)
            .deleteAttemptByDid(wrongDid);

        mockMvc
            .perform(
                delete("/api/auth/topic/exercises/attempts/delete/" + wrongDid)
                    .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken()))
                    .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.message").value("Attempt not found"))
            .andExpect(jsonPath("$.status").value("error"));
      }
    }
  }
}
