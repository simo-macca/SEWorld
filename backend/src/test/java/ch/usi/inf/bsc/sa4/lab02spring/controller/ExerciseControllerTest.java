package ch.usi.inf.bsc.sa4.lab02spring.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.*;
import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import ch.usi.inf.bsc.sa4.lab02spring.service.ExerciseService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.JwtTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.*;
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
@DisplayName("Exercise Controller Test")
public class ExerciseControllerTest {

  @MockitoBean private ExerciseService exerciseService;

  @MockitoBean private UserService userService;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private final String BASE_URL = "/api/auth/topic/exercises";

  private final String EXERCISE_DID_PATH_VARIABLE = "/{exercise_did}";
  private final String EXERCISE_DID_PARAMETER = "{exercise_did}";
  private final String TOPIC_DID_PATH_VARIABLE = "/{topic_did}";
  private final String TOPIC_DID_PARAMETER = "{topic_did}";

  private final String GET_ALL_EX_IN_TOPIC_PATH = "/get_all_exercises_in_topic";
  private final String GET_BY_EX_DID_PATH = "/get_by_exercise_did";
  private final String CREATE_PATH = "/create";
  private final String SEARCH_PATH = "/search";
  private final String CHANGE_DRAFT_PATH = "/teacher/change_draft";
  private final String UPDATE_EX_PATH = "/teacher/update_exercise";
  private final String DELETE_EX_PATH = "/teacher/delete_exercise";
  private final String DELETE_ALL_EX_PATH = "/teacher/delete_all_exercises_in_topic";

  private final String GET_ALL_EX_IN_TOPIC_URL =
      BASE_URL + GET_ALL_EX_IN_TOPIC_PATH + TOPIC_DID_PATH_VARIABLE;
  private final String GET_BY_EX_DID_URL =
      BASE_URL + GET_BY_EX_DID_PATH + EXERCISE_DID_PATH_VARIABLE;
  private final String CREATE_EX_URL = BASE_URL + CREATE_PATH + TOPIC_DID_PATH_VARIABLE;
  private final String SEARCH_EX_URL = BASE_URL + SEARCH_PATH + TOPIC_DID_PATH_VARIABLE;
  private final String CHANGE_DRAFT_URL = BASE_URL + CHANGE_DRAFT_PATH + EXERCISE_DID_PATH_VARIABLE;
  private final String UPDATE_EX_URL = BASE_URL + UPDATE_EX_PATH + EXERCISE_DID_PATH_VARIABLE;
  private final String DELETE_EX_URL = BASE_URL + DELETE_EX_PATH + EXERCISE_DID_PATH_VARIABLE;
  private final String DELETE_ALL_EX_URL = BASE_URL + DELETE_ALL_EX_PATH + TOPIC_DID_PATH_VARIABLE;

  private static Topic topic1;
  private static final String TOPIC1_TITLE = "Topic 1 title";
  private static final String TOPIC1_DESCRIPTION = "Topic 1 description";

  private static Topic topic2;
  private static final String TOPIC2_TITLE = "Topic 2 title";
  private static final String TOPIC2_DESCRIPTION = "Topic 2 description";

  private static Topic topic3;
  private static final String TOPIC3_TITLE = "Topic 3 title";
  private static final String TOPIC3_DESCRIPTION = "Topic 3 description";

  private static Exercise exercise1;
  private static final String EXERCISE1_TITLE = "Exercise 1 title";
  private static final String EXERCISE1_DESCRIPTION = "Exercise 1 description";

  private static Exercise exercise2;
  private static final String EXERCISE2_TITLE = "Exercise 2 title";
  private static final String EXERCISE2_DESCRIPTION = "Exercise 2 description";

  private static Exercise exercise3;

  private static Exercise exercise4;
  private static String EXERCISE4_TITLE = "Exercise 4 title";
  private static String EXERCISE4_DESCRIPTION = "Exercise 4 description";

  private static CreateExerciseDTO createExerciseDTO;
  private static CreateExerciseDTO createExerciseDTO2;

  private static final List<String> TITLE_KEYWORDS = List.of("title");
  private static final List<String> DESCRIPTION_KEYWORDS = List.of("description");

  private static ChangeExerciseDTO changeExerciseDTO;
  private static ChangeExerciseDTO changeExerciseDTO2;
  private static SearchExerciseDTO searchExerciseDTO;
  private static SearchExerciseDTO searchExerciseDTO2;

  private static final UUID studentDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf1");
  private static final UUID instructorDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf2");
  private static final UUID topic1Did = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc");
  private static final UUID topic2Did = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfa");
  private static final UUID topic3Did = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfb");
  private static final UUID exercise1Did = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfd");
  private static final UUID exercise2Did = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfe");
  private static final UUID exercise4Did = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bff");

  @BeforeAll
  public static void dataSetUp() {
    topic1 = new Topic(900L, topic1Did, TOPIC1_TITLE, TOPIC1_DESCRIPTION);
    topic2 = new Topic(901L, topic2Did, TOPIC2_TITLE, TOPIC2_DESCRIPTION);
    topic3 = new Topic(902L, topic3Did, TOPIC3_TITLE, TOPIC3_DESCRIPTION);
    exercise1 = new Exercise(EXERCISE1_TITLE, EXERCISE1_DESCRIPTION, topic1);
    exercise1.setExerciseDid(exercise1Did);
    exercise2 = new Exercise(EXERCISE2_TITLE, EXERCISE2_DESCRIPTION, topic1);
    exercise2.setExerciseDid(exercise2Did);
    exercise3 = new Exercise(EXERCISE2_TITLE, EXERCISE2_DESCRIPTION, topic1);
    exercise3.setExerciseDid(exercise1Did);
    exercise3.setExerciseIsDraft(false);
    exercise4 = new Exercise(EXERCISE4_TITLE, EXERCISE4_DESCRIPTION, topic1);
    exercise4.setExerciseDid(exercise4Did);
    exercise4.setExerciseIsDraft(false);
    createExerciseDTO = new CreateExerciseDTO(exercise1);
    createExerciseDTO2 = new CreateExerciseDTO(exercise2);
    changeExerciseDTO = new ChangeExerciseDTO(EXERCISE2_TITLE, EXERCISE2_DESCRIPTION);
    changeExerciseDTO2 = new ChangeExerciseDTO(EXERCISE4_TITLE, EXERCISE4_DESCRIPTION);
    searchExerciseDTO = new SearchExerciseDTO(TITLE_KEYWORDS, DESCRIPTION_KEYWORDS, true);
    searchExerciseDTO2 = new SearchExerciseDTO(TITLE_KEYWORDS, DESCRIPTION_KEYWORDS, false);
  }

  @BeforeEach
  public void setUp() {
    // get all
    given(exerciseService.getAllExercises(topic1.getDid(), true))
        .willReturn(List.of(exercise1, exercise2));
    given(exerciseService.getAllExercises(topic2.getDid(), true))
        .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercises not found"));
    given(exerciseService.getAllExercises(topic3.getDid(), true))
        .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Topic not found"));
    given(exerciseService.getAllExercises(topic1.getDid(), false))
        .willReturn(List.of(exercise1, exercise2, exercise3));
    given(exerciseService.getAllExercises(topic2.getDid(), false))
        .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercises not found"));

    // get by exercise did
    given(exerciseService.getExerciseByDid(exercise1.getExerciseDid(), true))
        .willReturn(Optional.of(exercise1));
    given(exerciseService.getExerciseByDid(exercise2.getExerciseDid(), true))
        .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found"));
    given(exerciseService.getExerciseByDid(exercise2.getExerciseDid(), false))
        .willReturn(Optional.of(exercise2));
    given(exerciseService.getExerciseByDid(exercise3.getExerciseDid(), false))
        .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercises not found"));

    // create
    given(exerciseService.createNewExercise(createExerciseDTO2, topic1.getDid()))
        .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Topic DID doesn't exist"));

    // search
    given(
            exerciseService.searchByKeywords(
                TITLE_KEYWORDS, DESCRIPTION_KEYWORDS, true, topic1.getDid(), true))
        .willReturn(List.of(exercise1, exercise2));
    given(
            exerciseService.searchByKeywords(
                TITLE_KEYWORDS, DESCRIPTION_KEYWORDS, true, topic3.getDid(), true))
        .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Topic not found"));
    given(
            exerciseService.searchByKeywords(
                TITLE_KEYWORDS, DESCRIPTION_KEYWORDS, true, topic2.getDid(), true))
        .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercises not found"));
    given(
            exerciseService.searchByKeywords(
                TITLE_KEYWORDS, DESCRIPTION_KEYWORDS, false, topic1.getDid(), false))
        .willReturn(List.of(exercise3));

    // change draft
    given(exerciseService.patchDraft(new ChangeDraftDTO(false), exercise1.getExerciseDid()))
        .willReturn(Optional.of(exercise3));
    given(exerciseService.patchDraft(new ChangeDraftDTO(false), exercise2.getExerciseDid()))
        .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found"));
    given(exerciseService.patchDraft(new ChangeDraftDTO(true), exercise3.getExerciseDid()))
        .willThrow(
            new HttpClientErrorException(
                HttpStatus.BAD_REQUEST, "A not draft exercise cannot be modified"));

    // change exercise
    given(exerciseService.patchExercise(changeExerciseDTO, exercise1.getExerciseDid()))
        .willReturn(Optional.of(exercise3));
    given(exerciseService.patchExercise(changeExerciseDTO, exercise2.getExerciseDid()))
        .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found"));
    given(exerciseService.patchExercise(changeExerciseDTO2, exercise4.getExerciseDid()))
        .willThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Exercise is not draft"));

    // delete exercise
    doNothing().when(exerciseService).deleteExercise(exercise1.getExerciseDid());
    doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found"))
        .when(exerciseService)
        .deleteExercise(exercise2.getExerciseDid());

    // delete all exercise in topic
    doNothing().when(exerciseService).deleteAllExercisesInTopic(topic1.getDid());
    doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Topic not found"))
        .when(exerciseService)
        .deleteAllExercisesInTopic(topic2.getDid());
  }

  @DisplayName("All unauthorized requests should return 401 unauthorized")
  @Test
  void testUnauthorizedRequests() throws Exception {
    mockMvc
        .perform(
            get(GET_ALL_EX_IN_TOPIC_URL.replace(TOPIC_DID_PARAMETER, topic1.getDid().toString())))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            get(
                GET_BY_EX_DID_URL.replace(
                    EXERCISE_DID_PARAMETER, exercise1.getExerciseDid().toString())))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(post(CREATE_EX_URL.replace(TOPIC_DID_PARAMETER, topic1.getDid().toString())))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(post(SEARCH_EX_URL.replace(TOPIC_DID_PARAMETER, topic1.getDid().toString())))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            patch(
                CHANGE_DRAFT_URL.replace(
                    EXERCISE_DID_PARAMETER, exercise1.getExerciseDid().toString())))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            patch(
                UPDATE_EX_URL.replace(
                    EXERCISE_DID_PARAMETER, exercise1.getExerciseDid().toString())))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            delete(
                DELETE_EX_URL.replace(
                    EXERCISE_DID_PARAMETER, exercise1.getExerciseDid().toString())))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(delete(DELETE_ALL_EX_URL.replace(TOPIC_DID_PARAMETER, topic1.getDid().toString())))
        .andExpect(status().isUnauthorized());
  }

  @DisplayName("When the call is made by an instructor")
  @Nested
  class WhenTheCallIsMadeByAnInstructor {

    Jwt jwt;
    AbstractUser mockInstructor;
    FakeUser fakeInstructor;

    @BeforeEach
    void setUp() {
      jwt = JwtTestUtil.createInstructorJwt();
      mockInstructor = mock(AbstractUser.class);
      when(mockInstructor.getRole()).thenReturn("INSTRUCTOR");
      when(mockInstructor.getDid()).thenReturn(instructorDid);
      when(userService.findOrCreateUser(any())).thenReturn(mockInstructor);
      fakeInstructor =
          new FakeUser(
              mockInstructor.getDid(),
              mockInstructor.getSubId(),
              mockInstructor.getName(),
              mockInstructor.getEmail());
    }

    @DisplayName("Should get all exercise in topic")
    @Test
    void testGetAllExercisesInTopic() throws Exception {
      mockMvc
          .perform(
              get(GET_ALL_EX_IN_TOPIC_URL.replace(TOPIC_DID_PARAMETER, topic1.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data").isArray())
          .andExpect(jsonPath("$.data[0].exerciseDID").value(exercise1.getExerciseDid().toString()))
          .andExpect(jsonPath("$.data[0].exerciseTitle").value(exercise1.getExerciseTitle()))
          .andExpect(
              jsonPath("$.data[0].exerciseDescription").value(exercise1.getExerciseDescription()))
          .andExpect(jsonPath("$.data[0].isDraft").value(exercise1.isExerciseIsDraft()))
          .andExpect(jsonPath("$.data[1].exerciseDID").value(exercise2.getExerciseDid().toString()))
          .andExpect(jsonPath("$.data[1].exerciseTitle").value(exercise2.getExerciseTitle()))
          .andExpect(
              jsonPath("$.data[1].exerciseDescription").value(exercise2.getExerciseDescription()))
          .andExpect(jsonPath("$.data[1].isDraft").value(exercise2.isExerciseIsDraft()))
          .andExpect(
              jsonPath("$.message")
                  .value("Exercises with topic DID " + topic1.getDid() + " have been loaded"))
          .andExpect(jsonPath("$.status").value("success"));
    }

    @DisplayName("Should send 404 not found when the topic DID does not exist")
    @Test
    void testGetAllExercisesInTopicButTopicDoesNotExist() throws Exception {
      mockMvc
          .perform(
              get(GET_ALL_EX_IN_TOPIC_URL.replace(TOPIC_DID_PARAMETER, topic3.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Topic not found"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should send 404 not found when topic does not have any exercise")
    @Test
    void testGetAllExercisesInTopicNotFound() throws Exception {
      mockMvc
          .perform(
              get(GET_ALL_EX_IN_TOPIC_URL.replace(TOPIC_DID_PARAMETER, topic2.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Exercises not found"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should get an exercise by its did")
    @Test
    void testGetExerciseByDid() throws Exception {
      mockMvc
          .perform(
              get(GET_BY_EX_DID_URL.replace(
                      EXERCISE_DID_PARAMETER, exercise1.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data[0].exerciseDID").value(exercise1.getExerciseDid().toString()))
          .andExpect(jsonPath("$.data[0].exerciseTitle").value(exercise1.getExerciseTitle()))
          .andExpect(
              jsonPath("$.data[0].exerciseDescription").value(exercise1.getExerciseDescription()))
          .andExpect(jsonPath("$.data[0].isDraft").value(exercise1.isExerciseIsDraft()))
          .andExpect(jsonPath("$.status").value("success"))
          .andExpect(
              jsonPath("$.message")
                  .value(
                      "Exercise with DID "
                          + exercise1.getExerciseDid().toString()
                          + " has been loaded"));
    }

    @DisplayName("Should send 404 not found when the exercise does not exist")
    @Test
    void testGetExerciseByDidNotFound() throws Exception {
      mockMvc
          .perform(
              get(GET_BY_EX_DID_URL.replace(
                      EXERCISE_DID_PARAMETER, exercise2.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Exercise not found"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should create an exercise")
    @Test
    void testCreateExercise() throws Exception {
      when(exerciseService.createNewExercise(any(), any())).thenReturn(Optional.of(exercise1));
      mockMvc
          .perform(
              post(CREATE_EX_URL.replace(TOPIC_DID_PARAMETER, topic1.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(createExerciseDTO)))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data[0].exerciseDID").value(exercise1.getExerciseDid().toString()))
          .andExpect(jsonPath("$.data[0].exerciseTitle").value(exercise1.getExerciseTitle()))
          .andExpect(
              jsonPath("$.data[0].exerciseDescription").value(exercise1.getExerciseDescription()))
          .andExpect(jsonPath("$.data[0].isDraft").value(exercise1.isExerciseIsDraft()))
          .andExpect(jsonPath("$.status").value("success"))
          .andExpect(
              jsonPath("$.message")
                  .value(
                      "Exercise with DID "
                          + exercise1.getExerciseDid().toString()
                          + " has been created"));
    }

    @DisplayName("Should send 404 not found when the topic does not exist")
    @Test
    void testCreateExerciseNotFound() throws Exception {
      mockMvc
          .perform(
              post(CREATE_EX_URL.replace(TOPIC_DID_PARAMETER, topic1.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(createExerciseDTO2)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Topic DID doesn't exist"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should search exercises using keywords")
    @Test
    void testSearchKeywords() throws Exception {
      mockMvc
          .perform(
              post(SEARCH_EX_URL.replace(TOPIC_DID_PARAMETER, topic1.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(searchExerciseDTO)))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data").isArray())
          .andExpect(jsonPath("$.data[0].exerciseDID").value(exercise1.getExerciseDid().toString()))
          .andExpect(jsonPath("$.data[0].exerciseTitle").value(exercise1.getExerciseTitle()))
          .andExpect(
              jsonPath("$.data[0].exerciseDescription").value(exercise1.getExerciseDescription()))
          .andExpect(jsonPath("$.data[0].isDraft").value(exercise1.isExerciseIsDraft()))
          .andExpect(jsonPath("$.data[1].exerciseDID").value(exercise2.getExerciseDid().toString()))
          .andExpect(jsonPath("$.data[1].exerciseTitle").value(exercise2.getExerciseTitle()))
          .andExpect(
              jsonPath("$.data[1].exerciseDescription").value(exercise2.getExerciseDescription()))
          .andExpect(jsonPath("$.data[1].isDraft").value(exercise2.isExerciseIsDraft()))
          .andExpect(jsonPath("$.status").value("success"));
    }

    @DisplayName("Should send 404 not found when the topic does not exist")
    @Test
    void testSearchExerciseNotFound() throws Exception {
      mockMvc
          .perform(
              post(SEARCH_EX_URL.replace(TOPIC_DID_PARAMETER, topic3.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(searchExerciseDTO)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Topic not found"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName(
        "Should send 404 not found when there are no exercises which contains the keywords")
    @Test
    void testSearchExerciseNotFoundWithKeywords() throws Exception {
      mockMvc
          .perform(
              post(SEARCH_EX_URL.replace(TOPIC_DID_PARAMETER, topic2.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(searchExerciseDTO)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Exercises not found"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should change the draft from true to false")
    @Test
    void testChangeDraft() throws Exception {
      mockMvc
          .perform(
              patch(
                      CHANGE_DRAFT_URL.replace(
                          EXERCISE_DID_PARAMETER, exercise1.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(new ChangeDraftDTO(false))))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data[0].exerciseDID").value(exercise3.getExerciseDid().toString()))
          .andExpect(jsonPath("$.data[0].isDraft").value(false))
          .andExpect(jsonPath("$.status").value("success"))
          .andExpect(
              jsonPath("$.message")
                  .value("Exercise with DID " + exercise3.getExerciseDid() + " has been updated"));
    }

    @DisplayName("Should send 404 not found when the exercise does not exist")
    @Test
    void testChangeExerciseNotFound() throws Exception {
      mockMvc
          .perform(
              patch(
                      CHANGE_DRAFT_URL.replace(
                          EXERCISE_DID_PARAMETER, exercise2.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(new ChangeDraftDTO(false))))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Exercise not found"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName(
        "Should send 400 bad request when the instructor tries to change the draft of a non draft exercise")
    @Test
    void testChangeDraftInvalidDraft() throws Exception {
      mockMvc
          .perform(
              patch(
                      CHANGE_DRAFT_URL.replace(
                          EXERCISE_DID_PARAMETER, exercise3.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(new ChangeDraftDTO(true))))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value("A not draft exercise cannot be modified"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should change an exercise title and description")
    @Test
    void testChangeTitleAndDescription() throws Exception {
      mockMvc
          .perform(
              patch(
                      UPDATE_EX_URL.replace(
                          EXERCISE_DID_PARAMETER, exercise1.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(changeExerciseDTO)))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data[0].exerciseDID").value(exercise3.getExerciseDid().toString()))
          .andExpect(jsonPath("$.data[0].exerciseTitle").value(exercise3.getExerciseTitle()))
          .andExpect(
              jsonPath("$.data[0].exerciseDescription").value(exercise3.getExerciseDescription()))
          .andExpect(jsonPath("$.status").value("success"))
          .andExpect(
              jsonPath("$.message")
                  .value("Exercise with DID " + exercise1.getExerciseDid() + " has been updated"));
    }

    @DisplayName("Should send 404 not found when the exercise does not exist")
    @Test
    void testChangeTitleAndDescriptionNotFound() throws Exception {
      mockMvc
          .perform(
              patch(
                      UPDATE_EX_URL.replace(
                          EXERCISE_DID_PARAMETER, exercise2.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(changeExerciseDTO)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Exercise not found"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName(
        "Should send 400 bad request when an instructor tries to change a non draft exercise")
    @Test
    void testChangeTitleAndDescriptionInvalidDraft() throws Exception {
      mockMvc
          .perform(
              patch(
                      UPDATE_EX_URL.replace(
                          EXERCISE_DID_PARAMETER, exercise4.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(changeExerciseDTO2)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value("Exercise is not draft"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should delete an exercise by did")
    @Test
    void testDeleteExerciseByDid() throws Exception {
      mockMvc
          .perform(
              delete(
                      DELETE_EX_URL.replace(
                          EXERCISE_DID_PARAMETER, exercise1.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isOk())
          .andExpect(
              jsonPath("$.message")
                  .value("Exercise with DID " + exercise1.getExerciseDid() + " has been deleted"))
          .andExpect(jsonPath("$.status").value("success"));
    }

    @DisplayName("Should send 404 not found when the exercise does not exist")
    @Test
    void testDeleteExerciseByDidNotFound() throws Exception {
      mockMvc
          .perform(
              delete(
                      DELETE_EX_URL.replace(
                          EXERCISE_DID_PARAMETER, exercise2.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Exercise not found"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should delete all exercise in a topic")
    @Test
    void testDeleteAllExercisesInTopic() throws Exception {
      mockMvc
          .perform(
              delete(DELETE_ALL_EX_URL.replace(TOPIC_DID_PARAMETER, topic1.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isOk())
          .andExpect(
              jsonPath("$.message")
                  .value(
                      "Exercises with topic DID "
                          + topic1.getDid().toString()
                          + " have been deleted"))
          .andExpect(jsonPath("$.status").value("success"));
    }

    @DisplayName("Should send 404 not found when the topic does not exist")
    @Test
    void testDeleteAllExercisesInTopicNotFound() throws Exception {
      mockMvc
          .perform(
              delete(DELETE_ALL_EX_URL.replace(TOPIC_DID_PARAMETER, topic2.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Topic not found"))
          .andExpect(jsonPath("$.status").value("error"));
    }
  }

  @DisplayName("When the call is made by a student")
  @Nested
  class WhenTheCallIsMadeByAnStudent {
    Jwt jwt;
    AbstractUser mockStudent;
    FakeUser fakeStudent;

    @BeforeEach
    void setUp() {
      jwt = JwtTestUtil.createStudentJwt();
      mockStudent = mock(AbstractUser.class);
      when(mockStudent.getRole()).thenReturn("STUDENT");
      when(mockStudent.getDid()).thenReturn(studentDid);
      when(userService.findOrCreateUser(any())).thenReturn(mockStudent);
      fakeStudent =
          new FakeUser(
              mockStudent.getDid(),
              mockStudent.getSubId(),
              mockStudent.getName(),
              mockStudent.getEmail());
    }

    @DisplayName("Should get all exercise in topic")
    @Test
    void testGetAllExercisesInTopic() throws Exception {
      mockMvc
          .perform(
              get(GET_ALL_EX_IN_TOPIC_URL.replace(TOPIC_DID_PARAMETER, topic1.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data[0].exerciseDID").value(exercise1.getExerciseDid().toString()))
          .andExpect(jsonPath("$.data[0].exerciseTitle").value(exercise1.getExerciseTitle()))
          .andExpect(jsonPath("$.data[1].exerciseDID").value(exercise2.getExerciseDid().toString()))
          .andExpect(
              jsonPath("$.data[1].exerciseDescription").value(exercise2.getExerciseDescription()))
          .andExpect(jsonPath("$.data[2].exerciseDID").value(exercise3.getExerciseDid().toString()))
          .andExpect(jsonPath("$.data[1].exerciseTitle").value(exercise3.getExerciseTitle()))
          .andExpect(
              jsonPath("$.message")
                  .value(
                      "Exercises with topic DID "
                          + topic1.getDid().toString()
                          + " have been loaded"))
          .andExpect(jsonPath("$.status").value("success"));
    }

    @DisplayName("Should send 404 when there are no visible exercises")
    @Test
    void testGetAllExercisesInTopicNotFound() throws Exception {
      mockMvc
          .perform(
              get(GET_ALL_EX_IN_TOPIC_URL.replace(TOPIC_DID_PARAMETER, topic2.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Exercises not found"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should get an exercise by its did")
    @Test
    void testGetExerciseByDid() throws Exception {
      mockMvc
          .perform(
              get(GET_BY_EX_DID_URL.replace(
                      EXERCISE_DID_PARAMETER, exercise2.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data[0].exerciseDID").value(exercise2.getExerciseDid().toString()))
          .andExpect(jsonPath("$.data[0].exerciseTitle").value(exercise2.getExerciseTitle()))
          .andExpect(
              jsonPath("$.data[0].exerciseDescription").value(exercise2.getExerciseDescription()))
          .andExpect(jsonPath("$.status").value("success"))
          .andExpect(
              jsonPath("$.message")
                  .value(
                      "Exercise with DID "
                          + exercise2.getExerciseDid().toString()
                          + " has been loaded"));
    }

    @DisplayName("Should send 404 not found when the exercise is draft")
    @Test
    void testGetExerciseByDidNotFound() throws Exception {
      mockMvc
          .perform(
              get(GET_BY_EX_DID_URL.replace(
                      EXERCISE_DID_PARAMETER, exercise3.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Exercises not found"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should not be able to create an exercise")
    @Test
    void testCreateExercise() throws Exception {
      mockMvc
          .perform(
              post(CREATE_EX_URL.replace(TOPIC_DID_PARAMETER, topic1.getDid().toString()))
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(createExerciseDTO)))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.message").value("Current user is not allowed to create exercise"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should search exercises using keywords")
    @Test
    void testSearchKeywords() throws Exception {
      mockMvc
          .perform(
              post(SEARCH_EX_URL.replace(TOPIC_DID_PARAMETER, topic1.getDid().toString()))
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(searchExerciseDTO2)))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data[0].exerciseDID").value(exercise3.getExerciseDid().toString()))
          .andExpect(jsonPath("$.data[0].exerciseTitle").value(exercise3.getExerciseTitle()))
          .andExpect(
              jsonPath("$.data[0].exerciseDescription").value(exercise3.getExerciseDescription()))
          .andExpect(jsonPath("$.data[0].isDraft").value(false))
          .andExpect(
              jsonPath("$.message")
                  .value(
                      "Exercises with topic DID "
                          + topic1.getDid().toString()
                          + " have been loaded"))
          .andExpect(jsonPath("$.status").value("success"));
    }

    @DisplayName("Should not be able to change the exercise draft")
    @Test
    void testChangeExerciseDraft() throws Exception {
      mockMvc
          .perform(
              patch(
                      CHANGE_DRAFT_URL.replace(
                          EXERCISE_DID_PARAMETER, exercise1.getExerciseDid().toString()))
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(new ChangeDraftDTO(false))))
          .andExpect(status().isUnauthorized())
          .andExpect(
              jsonPath("$.message").value("Current user is not allowed to modify the exercise"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should not be able to change exercise title or description")
    @Test
    void testChangeExerciseTitleOrDescription() throws Exception {
      mockMvc
          .perform(
              patch(
                      UPDATE_EX_URL.replace(
                          EXERCISE_DID_PARAMETER, exercise1.getExerciseDid().toString()))
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(changeExerciseDTO)))
          .andExpect(status().isUnauthorized())
          .andExpect(
              jsonPath("$.message").value("Current user is not allowed to modify the exercise"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should not be able to delete an exercise by DID")
    @Test
    void testDeleteExerciseByDidNotFound() throws Exception {
      mockMvc
          .perform(
              delete(
                      DELETE_EX_URL.replace(
                          EXERCISE_DID_PARAMETER, exercise1.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isUnauthorized())
          .andExpect(
              jsonPath("$.message").value("Current user is not allowed to delete the exercise"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should not be able to delete all exercises in a topic")
    @Test
    void testDeleteAllExercisesInTopic() throws Exception {
      mockMvc
          .perform(
              delete(DELETE_ALL_EX_URL.replace(TOPIC_DID_PARAMETER, topic1.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isUnauthorized())
          .andExpect(
              jsonPath("$.message").value("Current user is not allowed to delete the exercises"))
          .andExpect(jsonPath("$.status").value("error"));
    }
  }
}
