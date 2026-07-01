package ch.usi.inf.bsc.sa4.lab02spring.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.ExerciseStatsDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.StatisticsInstructorDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.StatisticsStudentDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.StudentExercisesStatsDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.StudentGradeDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.TopicExercisesStatsDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.TopicStatisticDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.TopicStatisticInstrDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.Topic;
import ch.usi.inf.bsc.sa4.lab02spring.service.StatisticsService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

@AutoConfigureMockMvc
@SpringBootTest
class StatisticsControllerTest {

  @MockitoBean private StatisticsService statisticsService;

  @MockitoBean private UserService userService;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private final String baseUrl = "/api/auth/statistics";
  private final String exerciseDidPathVariable = "/{exercise_did}";
  private final String exerciseDidParameter = "{exercise_did}";
  private final String topicDidPathVariable = "/{topic_did}";
  private final String topicDidParameter = "{topic_did}";
  private final String userDidPathVariable = "/{user_did}";
  private final String userDidParameter = "{user_did}";

  private final String studentViewPath = "/general_view_statistics";
  private final String getTopicExercisesStatisticsPath = "/topic";
  private final String getSingleExerciseStatsPath = "/exercise";

  private final String studentViewUrl = baseUrl + studentViewPath;
  private final String studentViewByUserDidUrl = baseUrl + studentViewPath + userDidPathVariable;
  private final String getTopicExercisesStatisticsUrl =
      baseUrl + getTopicExercisesStatisticsPath + topicDidPathVariable;
  private final String getTopicExercisesStatisticsByUserDidUrl =
      baseUrl + getTopicExercisesStatisticsPath + topicDidPathVariable + userDidPathVariable;
  private final String getSingleExerciseStatsUrl =
      baseUrl + getSingleExerciseStatsPath + exerciseDidPathVariable;

  private static Topic topic1;
  private static final String TOPIC1_TITLE = "Topic 1 title";
  private static final String TOPIC1_DESCRIPTION = "Topic 1 description";

  private static Exercise exercise1;
  private static final String EXERCISE1_TITLE = "Exercise 1 title";
  private static final String EXERCISE1_DESCRIPTION = "Exercise 1 description";

  private static AbstractUser mockStudent;
  private static Optional<AbstractUser> optMockStudent;
  private static FakeUser fakeStudent;
  private static AbstractUser mockInstructor;
  private static FakeUser fakeInstructor;

  private static StatisticsStudentDTO studentStatsDTO;
  private static TopicExercisesStatsDTO topicExerciseStatsDTO;
  private static StudentGradeDTO studentGradeDTO;
  private static StatisticsInstructorDTO statisticsInstructorDTO;
  private static ExerciseStatsDTO exerciseStatsDTO;

  @BeforeAll
  static void setUp() {
    final UUID topicDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc");
    topic1 = new Topic(900L, topicDid, TOPIC1_TITLE, TOPIC1_DESCRIPTION);
    exercise1 = new Exercise(EXERCISE1_TITLE, EXERCISE1_DESCRIPTION, topic1);
    UUID exerciseDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfd");
    exercise1.setExerciseDid(exerciseDid);
    mockStudent = mock(AbstractUser.class);
    optMockStudent = Optional.of(mockStudent);
    fakeStudent =
        new FakeUser(
            mockStudent.getDid(),
            mockStudent.getSubId(),
            mockStudent.getName(),
            mockStudent.getEmail());
    mockInstructor = mock(AbstractUser.class);
    fakeInstructor =
        new FakeUser(
            mockInstructor.getDid(),
            mockInstructor.getSubId(),
            mockInstructor.getName(),
            mockInstructor.getEmail());
    var topicStatsDTO = new TopicStatisticDTO(topic1.getDid(), TOPIC1_TITLE, 10.0, 20.0, 100.0);
    studentStatsDTO = new StatisticsStudentDTO(10.0, 55.0, 100.0, List.of(topicStatsDTO));
    var studentExercisesStatsDTO =
        new StudentExercisesStatsDTO(exercise1.getExerciseDid(), EXERCISE1_TITLE, true, 30.0);
    topicExerciseStatsDTO = new TopicExercisesStatsDTO(List.of(studentExercisesStatsDTO));
    studentGradeDTO = new StudentGradeDTO(27.0);
    var topicStatisticInstrDTO = new TopicStatisticInstrDTO(topic1.getDid(), TOPIC1_TITLE, 60.0);
    statisticsInstructorDTO = new StatisticsInstructorDTO(List.of(topicStatisticInstrDTO));
    exerciseStatsDTO = new ExerciseStatsDTO(EXERCISE1_TITLE, 57.0, 10L, List.of());
  }

  @DisplayName("All unauthorized requests should return 401 unauthorized")
  @Test
  void testUnauthorizedRequests() throws Exception {
    mockMvc.perform(get(studentViewUrl)).andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            get(
                getTopicExercisesStatisticsUrl.replace(
                    topicDidParameter, topic1.getDid().toString())))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            get(
                getSingleExerciseStatsUrl.replace(
                    exerciseDidParameter, exercise1.getExerciseDid().toString())))
        .andExpect(status().isUnauthorized());
  }

  @DisplayName("When the call is made by a student")
  @Nested
  class WhenTheCallIsMadeByAStudent {

    @BeforeEach
    void setUp() {
      UUID studentDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf1");
      when(mockStudent.getRole()).thenReturn("STUDENT");
      when(mockStudent.getDid()).thenReturn(studentDid);
      when(userService.findOrCreateUser(any())).thenReturn(mockStudent);
      when(userService.getByDid(studentDid)).thenReturn(optMockStudent);
    }

    @DisplayName("It should get the statistics for a student view")
    @Test
    void testGetStudentStatistics() throws Exception {
      when(statisticsService.getStudentView(mockStudent)).thenReturn(studentStatsDTO);
      mockMvc
          .perform(
              get(studentViewUrl)
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data.minGrade").value(10.0))
          .andExpect(jsonPath("$.data.avgGrade").value(55.0))
          .andExpect(jsonPath("$.data.maxGrade").value(100.0))
          .andExpect(jsonPath("$.data.topics").isArray())
          .andExpect(jsonPath("$.data.topics[0].topicDid").value(topic1.getDid().toString()))
          .andExpect(jsonPath("$.data.topics[0].topicTitle").value(TOPIC1_TITLE))
          .andExpect(jsonPath("$.data.topics[0].avgUsersGrade").value(10.0))
          .andExpect(jsonPath("$.data.topics[0].userGrade").value(20.0))
          .andExpect(jsonPath("$.data.topics[0].completionStage").value(100.0));
    }

    @DisplayName("It should get the statistics for a student view by the student did")
    @Test
    void testGetStudentStatisticsByStudentDid() throws Exception {
      when(statisticsService.getStudentView(mockStudent)).thenReturn(studentStatsDTO);
      mockMvc
          .perform(
              get(studentViewByUserDidUrl.replace(
                      userDidParameter, mockStudent.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data.minGrade").value(10.0))
          .andExpect(jsonPath("$.data.avgGrade").value(55.0))
          .andExpect(jsonPath("$.data.maxGrade").value(100.0))
          .andExpect(jsonPath("$.data.topics").isArray())
          .andExpect(jsonPath("$.data.topics[0].topicDid").value(topic1.getDid().toString()))
          .andExpect(jsonPath("$.data.topics[0].topicTitle").value(TOPIC1_TITLE))
          .andExpect(jsonPath("$.data.topics[0].avgUsersGrade").value(10.0))
          .andExpect(jsonPath("$.data.topics[0].userGrade").value(20.0))
          .andExpect(jsonPath("$.data.topics[0].completionStage").value(100.0));
    }

    @DisplayName(
        "It should throw when getting the statistics for a student view by the student did that does not exists")
    @Test
    void testGetStudentStatisticsByNotFoundStudentDid() throws Exception {
      final UUID randomUUID = UUID.fromString("76311b61-11c4-4be7-bf18-68e49733aaa6");
      when(userService.getByDid(randomUUID)).thenReturn(Optional.empty());
      mockMvc
          .perform(
              get(studentViewByUserDidUrl.replace(userDidParameter, randomUUID.toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("User Not found"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("It should send 404 not found when there are no statistics available")
    @Test
    void testGetStudentStatisticsNotFound() throws Exception {
      when(statisticsService.getStudentView(mockStudent))
          .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "No statistics available"));
      mockMvc
          .perform(
              get(studentViewUrl)
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("No statistics available"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("It should retrieve statistics for exercises associated with a specific topic")
    @Test
    void testGetExerciseStatistics() throws Exception {
      when(statisticsService.getExerciseStudentView(topic1.getDid(), mockStudent))
          .thenReturn(topicExerciseStatsDTO);
      mockMvc
          .perform(
              get(getTopicExercisesStatisticsUrl.replace(
                      topicDidParameter, topic1.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data.stats").isArray())
          .andExpect(
              jsonPath("$.data.stats[0].exerciseDid").value(exercise1.getExerciseDid().toString()))
          .andExpect(jsonPath("$.data.stats[0].exerciseTitle").value(EXERCISE1_TITLE))
          .andExpect(jsonPath("$.data.stats[0].userGrade").value(30.0))
          .andExpect(jsonPath("$.data.stats[0].successful").value(true))
          .andExpect(jsonPath("$.status").value("success"))
          .andExpect(jsonPath("$.message").value("Statistics uploaded"));
    }

    @DisplayName(
        "It should retrieve statistics for exercises associated with a specific topic of a specific user")
    @Test
    void testGetExerciseStatisticsByUserDid() throws Exception {
      when(statisticsService.getExerciseStudentView(topic1.getDid(), mockStudent))
          .thenReturn(topicExerciseStatsDTO);
      mockMvc
          .perform(
              get(getTopicExercisesStatisticsByUserDidUrl
                      .replace(topicDidParameter, topic1.getDid().toString())
                      .replace(userDidParameter, mockStudent.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data.stats").isArray())
          .andExpect(
              jsonPath("$.data.stats[0].exerciseDid").value(exercise1.getExerciseDid().toString()))
          .andExpect(jsonPath("$.data.stats[0].exerciseTitle").value(EXERCISE1_TITLE))
          .andExpect(jsonPath("$.data.stats[0].userGrade").value(30.0))
          .andExpect(jsonPath("$.data.stats[0].successful").value(true))
          .andExpect(jsonPath("$.status").value("success"))
          .andExpect(jsonPath("$.message").value("Statistics uploaded"));
    }

    @DisplayName(
        "It should throw when retrieving statistics for exercises associated with a"
            + "specific topic of a not not found user")
    @Test
    void testGetExerciseStatisticsByNotFoundUserDid() throws Exception {
      final UUID randomUUID = UUID.fromString("76311b61-11c4-4be7-bf18-68e49733aaa6");
      when(userService.getByDid(randomUUID)).thenReturn(Optional.empty());
      when(statisticsService.getExerciseStudentView(topic1.getDid(), mockStudent))
          .thenReturn(topicExerciseStatsDTO);
      mockMvc
          .perform(
              get(getTopicExercisesStatisticsByUserDidUrl
                      .replace(topicDidParameter, topic1.getDid().toString())
                      .replace(userDidParameter, randomUUID.toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("User Not found"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("It should send 404 not found where there are no statistics available")
    @Test
    void testGetExerciseStatisticsNotFound() throws Exception {
      when(statisticsService.getExerciseStudentView(topic1.getDid(), mockStudent))
          .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "No statistics available"));
      mockMvc
          .perform(
              get(getTopicExercisesStatisticsUrl.replace(
                      topicDidParameter, topic1.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("No statistics available"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("It should retrieve the statistics for a single exercise")
    @Test
    void testGetSingleExerciseStatistics() throws Exception {
      when(statisticsService.getSingleExerciseStudentStats(exercise1.getExerciseDid(), mockStudent))
          .thenReturn(studentGradeDTO);
      mockMvc
          .perform(
              get(getSingleExerciseStatsUrl.replace(
                      exerciseDidParameter, exercise1.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data.userGrade").value(27.0))
          .andExpect(jsonPath("$.status").value("success"))
          .andExpect(jsonPath("$.message").value("Statistics uploaded"));
    }

    @DisplayName("It should send 404 not found when the exercise's statistics are not available")
    @Test
    void testGetSingleExerciseStatisticsNotFound() throws Exception {
      when(statisticsService.getSingleExerciseStudentStats(exercise1.getExerciseDid(), mockStudent))
          .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "No statistics available"));
      mockMvc
          .perform(
              get(getSingleExerciseStatsUrl.replace(
                      exerciseDidParameter, exercise1.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("No statistics available"))
          .andExpect(jsonPath("$.status").value("error"));
    }
  }

  @DisplayName("When the call is made by a instructor")
  @Nested
  class WhenTheCallIsMadeByAInstructor {

    @BeforeEach
    void setUp() {
      UUID instructorDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf2");
      when(mockInstructor.getRole()).thenReturn("INSTRUCTOR");
      when(mockInstructor.getDid()).thenReturn(instructorDid);
      when(userService.findOrCreateUser(any())).thenReturn(mockInstructor);
    }

    @DisplayName("It should get the statistics for the instructor view")
    @Test
    void testGetInstructorStatistics() throws Exception {
      when(statisticsService.getInstructorView()).thenReturn(statisticsInstructorDTO);
      mockMvc
          .perform(
              get(studentViewUrl)
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data.topics").isArray())
          .andExpect(jsonPath("$.data.topics[0].topicDid").value(topic1.getDid().toString()))
          .andExpect(jsonPath("$.data.topics[0].topicTitle").value(TOPIC1_TITLE))
          .andExpect(jsonPath("$.data.topics[0].avgUsersGrade").value(60.0))
          .andExpect(jsonPath("$.status").value("success"))
          .andExpect(jsonPath("$.message").value("Statistics uploaded"));
    }

    @DisplayName("It should send 404 not found when there are no statistics available")
    @Test
    void testGetInstructorStatisticsNotFound() throws Exception {
      when(statisticsService.getInstructorView())
          .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "No statistics available"));
      mockMvc
          .perform(
              get(studentViewUrl)
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("No statistics available"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName(
        "It should retrieve statistics for exercises associated with a specific topic for an instructor")
    @Test
    void testGetExerciseStatistics() throws Exception {
      when(statisticsService.getExerciseInstructorView(topic1.getDid()))
          .thenReturn(topicExerciseStatsDTO);
      mockMvc
          .perform(
              get(getTopicExercisesStatisticsUrl.replace(
                      topicDidParameter, topic1.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data.stats").isArray())
          .andExpect(
              (jsonPath("$.data.stats[0].exerciseDid")
                  .value(exercise1.getExerciseDid().toString())))
          .andExpect(jsonPath("$.data.stats[0].exerciseTitle").value(EXERCISE1_TITLE))
          .andExpect(jsonPath("$.data.stats[0].userGrade").value(30.0))
          .andExpect(jsonPath("$.data.stats[0].successful").value(true))
          .andExpect(jsonPath("$.status").value("success"))
          .andExpect(jsonPath("$.message").value("Statistics uploaded"));
    }

    @DisplayName("It should send 404 not found when the statistics are not available")
    @Test
    void testGetExerciseStatisticsNotFound() throws Exception {
      when(statisticsService.getExerciseInstructorView(topic1.getDid()))
          .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "No statistics available"));
      mockMvc
          .perform(
              get(getTopicExercisesStatisticsUrl.replace(
                      topicDidParameter, topic1.getDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("No statistics available"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("It should retrieve statistics for a single exercise from the instructor view")
    @Test
    void testGetSingleExerciseStatistics() throws Exception {
      when(statisticsService.getSingleExerciseInstructorStats(exercise1.getExerciseDid()))
          .thenReturn(exerciseStatsDTO);
      mockMvc
          .perform(
              get(getSingleExerciseStatsUrl.replace(
                      exerciseDidParameter, exercise1.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data.exerciseTitle").value(EXERCISE1_TITLE))
          .andExpect(jsonPath("$.data.avgUsersGrade").value(57.0))
          .andExpect(jsonPath("$.data.studentsWhoAttempted").value(10L))
          .andExpect(jsonPath("$.status").value("success"))
          .andExpect(jsonPath("$.message").value("Statistics uploaded"));
    }

    @DisplayName("It should send 404 not found when the exercise's statistics are not available")
    @Test
    void testGetSingleExerciseStatisticsNotFound() throws Exception {
      when(statisticsService.getSingleExerciseInstructorStats(exercise1.getExerciseDid()))
          .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "No statistics available"));
      mockMvc
          .perform(
              get(getSingleExerciseStatsUrl.replace(
                      exerciseDidParameter, exercise1.getExerciseDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("No statistics available"))
          .andExpect(jsonPath("$.status").value("error"));
    }
  }
}
