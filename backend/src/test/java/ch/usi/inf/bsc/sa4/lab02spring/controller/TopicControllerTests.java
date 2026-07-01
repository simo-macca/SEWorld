package ch.usi.inf.bsc.sa4.lab02spring.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.CompletionTopicDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.SearchTopicDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.TopicDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Topic;
import ch.usi.inf.bsc.sa4.lab02spring.service.TopicService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.JwtTestUtil;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Topic Controller Test")
public class TopicControllerTests {
  // Mocker
  @MockitoBean private TopicService topicService;

  @MockitoBean private UserService userService;

  // REST calls
  @Autowired private MockMvc mockMvc;

  @Autowired private TopicController topicController;

  @Autowired
  private HikariDataSource dataSource;

  // Routes components
  private final String BASE_URL = "/api/auth/topic";

  private final String DID_PATH_VARIABLE = "/{did}";
  private final String DID_VARIABLE = "{did}";

  private final String CREATE_PATH = "/create";
  private final String UPDATE_PATH = "/update";
  private final String DELETE_PATH = "/delete";
  private final String SEARCH_PATH = "/search";

  // Routes
  private final String GET_ALL_TOPICS_URL = BASE_URL;
  private final String GET_TOPIC_BY_DID_URL = BASE_URL + DID_PATH_VARIABLE;
  private final String CREATE_TOPIC_URL = BASE_URL + CREATE_PATH;
  private final String UPDATE_TOPIC_URL = BASE_URL + UPDATE_PATH + DID_PATH_VARIABLE;
  private final String DELETE_TOPIC_URL = BASE_URL + DELETE_PATH + DID_PATH_VARIABLE;
  private final String SEARCH_TOPIC_KEYWORDS_URL = BASE_URL + SEARCH_PATH;

  private static Topic topic1;
  private static final String TOPIC1_TITLE = "Topic beautiful title 1";
  private static final String TOPIC1_DESCRIPTION =
      "Topic description 1 very interesting description";

  private static Topic topic2;
  private static final String TOPIC2_TITLE = "Topic ugly title 2";
  private static final String TOPIC2_DESCRIPTION = "Topic description 2 very boring description";

  private static final UUID instructorDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf2");
  private static final UUID topic1Did = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc");
  private static final UUID topic2Did = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfa");

  /**
   * This function is run once before all tests, and we use it to initialize the data used in those
   * tests
   */
  @BeforeAll
  public static void testDataSetup() {
    topic1 = new Topic(900L, topic1Did, TOPIC1_TITLE, TOPIC1_DESCRIPTION);
    topic2 = new Topic(901L, topic2Did, TOPIC2_TITLE, TOPIC2_DESCRIPTION);
  }

  /** This function runs before each test to set up the Mockito service mock */
  @BeforeEach
  void testSetup() {
    // Replace Random with SecureRandom
    SecureRandom rand = new SecureRandom();

    // Id
    given(topicService.getTopicById(topic1.getId())).willReturn(topic1);
    given(topicService.getTopicById(topic2.getId())).willReturn(topic2);

    // Did
    given(topicService.getTopicByDid(topic1.getDid())).willReturn(topic1);
    given(topicService.getTopicByDid(topic2.getDid())).willReturn(topic2);

    // Get all
    given(topicService.getAllTopics()).willReturn(List.of(topic1, topic2));

    // Get all completion
    given(topicService.getAllCompletionStageTopics(any()))
        .willReturn(
            List.of(
                new CompletionTopicDTO(topic1.getDid(), 80.0, true),
                new CompletionTopicDTO(topic2.getDid(), 80.0, true)));

    // Get completion
    given(topicService.getCompletionStageTopic(any(), eq(topic1.getDid())))
        .willReturn(new CompletionTopicDTO(topic1.getDid(), 20.0, true));
    given(topicService.getCompletionStageTopic(any(), eq(topic2.getDid())))
        .willReturn(new CompletionTopicDTO(topic2.getDid(), 50.0, true));

    // Create
    given(topicService.createTopic(any(TopicDTO.class)))
        .willAnswer(
            invocation -> {
              TopicDTO inputTopic = invocation.getArgument(0);

              // null topic
              if (inputTopic == null) {
                return null;
              }

              // invalid
              if (inputTopic.description() == null || inputTopic.title() == null) {
                return null;
              }

              if (inputTopic.title().equals(TOPIC1_TITLE)
                  && inputTopic.description().equals(TOPIC2_DESCRIPTION)) {
                return topic1;
              }

              if (inputTopic.title().equals(TOPIC2_TITLE)
                  && inputTopic.description().equals(TOPIC2_DESCRIPTION)) {
                return topic2;
              }

              // We return the random created topic with UUID and id
              return new Topic(
                  rand.nextLong(),
                  UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfb"),
                  inputTopic.title(),
                  inputTopic.description());
            });

    // Update
    given(topicService.updateTopic(any(UUID.class), any(TopicDTO.class)))
        .willAnswer(
            invocation -> {
              UUID inputDid = invocation.getArgument(0);
              TopicDTO inputTopicDto = invocation.getArgument(1);

              // null topic
              if (inputTopicDto == null || inputDid == null) {
                return null;
              }

              // invalid
              if (inputTopicDto.description() == null && inputTopicDto.title() == null) {
                return null;
              }

              if (inputDid.equals(topic1.getDid())) {
                return new Topic(
                    topic1.getId(),
                    topic1.getDid(),
                    inputTopicDto.title() == null ? topic1.getTitle() : inputTopicDto.title(),
                    inputTopicDto.description() == null
                        ? topic1.getDescription()
                        : inputTopicDto.description());
              }

              if (inputDid.equals(topic2.getDid())) {
                return new Topic(
                    topic2.getId(),
                    topic2.getDid(),
                    inputTopicDto.title() == null ? topic2.getTitle() : inputTopicDto.title(),
                    inputTopicDto.description() == null
                        ? topic2.getDescription()
                        : inputTopicDto.description());
              }

              // We return the created topic with UUID and id
              return new Topic(
                  rand.nextLong(),
                  UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc"),
                  inputTopicDto.title(),
                  inputTopicDto.description());
            });

    // Delete
    doNothing().when(topicService).deleteTopic(topic1.getDid());
    doNothing().when(topicService).deleteTopic(topic2.getDid());

    // Search
    given(topicService.searchTopicsByTitleAndDescription(new SearchTopicDTO(null, null)))
        .willReturn(List.of(topic1, topic2));
    given(topicService.searchTopicsByTitleAndDescription(new SearchTopicDTO(List.of(), List.of())))
        .willReturn(List.of(topic1, topic2));
    given(
            topicService.searchTopicsByTitleAndDescription(
                new SearchTopicDTO(List.of("topic"), List.of("description"))))
        .willReturn(List.of(topic1, topic2));
    given(
            topicService.searchTopicsByTitleAndDescription(
                new SearchTopicDTO(List.of("beautiful"), List.of("description"))))
        .willReturn(List.of(topic1));
    given(
            topicService.searchTopicsByTitleAndDescription(
                new SearchTopicDTO(List.of("title"), List.of("boring"))))
        .willReturn(List.of(topic2));
  }

  @AfterEach
  void tearDown() {
    dataSource.close();
  }

  @DisplayName(" any authorized requests should return 401 Unauthorized when unauthorized")
  @Test
  public void testUnauthorizedRequests() throws Exception {
    // 400 not expected because UUID is not valid
    mockMvc.perform(get(GET_ALL_TOPICS_URL)).andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            get(GET_TOPIC_BY_DID_URL.replace(DID_VARIABLE, "c0daf844-82ca-441f-99f6-2e5099f60bfd")))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(post(CREATE_TOPIC_URL)).andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            patch(UPDATE_TOPIC_URL.replace(DID_VARIABLE, "c0daf844-82ca-441f-99f6-2e5099f60bfe")))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            delete(DELETE_TOPIC_URL.replace(DID_VARIABLE, "c0daf844-82ca-441f-99f6-2e5099f60bff")))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(post(SEARCH_TOPIC_KEYWORDS_URL)).andExpect(status().isUnauthorized());
  }

  @DisplayName(" should return all topics")
  @Test
  public void testGetAllTopics() {
    ResponseEntity<Object> res = topicController.getAllTopics();
    assertEquals(HttpStatus.OK, res.getStatusCode());
  }

  @DisplayName(" should return all topics")
  @Test
  public void testKeywordSearchTopics() {
    ResponseEntity<Object> res =
        topicController.searchTopicByTitleAndDescription(
            new SearchTopicDTO(List.of("title"), List.of("description")));
    assertEquals(HttpStatus.OK, res.getStatusCode());
  }

  @DisplayName(" an instructor call it")
  @Nested
  class WhenInstructorCall {

    Jwt jwt;
    AbstractUser mockInstructor;

    @BeforeEach
    void setUp() {
      // We mock the instructor call for the test
      jwt = JwtTestUtil.createInstructorJwt();
      mockInstructor = mock(AbstractUser.class);
      when(mockInstructor.getRole()).thenReturn("INSTRUCTOR");
      when(mockInstructor.getDid()).thenReturn(instructorDid);
      when(userService.findOrCreateUser(jwt)).thenReturn(mockInstructor);
    }

    @DisplayName(" should create a new topic")
    @Test
    public void testCreateTopic() {
      ResponseEntity<Object> res =
          topicController.createTopic(jwt, new TopicDTO(null, "title", "description"));
      assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @DisplayName(" should return all topics")
    @Test
    public void testUpdateTopic() {
      ResponseEntity<Object> res =
          topicController.updateTopic(
              jwt, topic1.getDid(), new TopicDTO(null, "A new title", "A new description"));
      assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @DisplayName(" should return all topics")
    @Test
    public void testDeleteTopic() {
      ResponseEntity<Object> res = topicController.deleteTopic(jwt, topic1.getDid());
      assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @DisplayName(" should return all topics completion")
    @Test
    public void testAllCompletionsTopic() {
      ResponseEntity<Object> res = topicController.getAllCompletionStageTopics(jwt);
      assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @DisplayName(" should return all a topic completion")
    @Test
    public void testCompletionTopic() {
      ResponseEntity<Object> res = topicController.getCompletionStageTopic(jwt, topic1.getDid());
      assertEquals(HttpStatus.OK, res.getStatusCode());
    }
  }
}
