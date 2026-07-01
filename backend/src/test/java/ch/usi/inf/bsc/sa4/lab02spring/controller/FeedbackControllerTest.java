package ch.usi.inf.bsc.sa4.lab02spring.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.*;
import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import ch.usi.inf.bsc.sa4.lab02spring.service.*;
import ch.usi.inf.bsc.sa4.lab02spring.utils.JwtTestUtil;
import java.util.*;
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
@DisplayName("Feedback Controller Test")
public class FeedbackControllerTest {

  @MockitoBean private UserService userService;

  @MockitoBean private FeedbackService feedbackService;

  @Autowired private MockMvc mockMvc;

  // user
  Jwt jwt;
  FakeUser fakeUser;
  private static final UUID userDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfa");

  private static final UUID topicDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfb");

  // exercise
  private static Exercise exercise;
  private static final UUID exerciseDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc");

  // question
  private static MultiChoiceQuestion multiChoiceQuestion;
  private static final UUID mcqDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfd");
  private static ShortAnswerQuestion shortAnswerQuestion;
  private static final UUID saqDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfe");
  private static TrueFalseQuestion trueFalseQuestion;
  private static final UUID tfqDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bff");

  // attempt
  private Attempt attempt;
  private static final UUID attemptDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf1");

  private final UUID mcaDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60b2");
  private final UUID saDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf3");
  private final UUID tfaDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf4");

  // feedback
  private FeedbackDTO feedbackDTO;

  // utils
  private static final UUID wrongDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf5");

  @BeforeAll
  public static void setUp() {
    // topic
    Topic topic = new Topic(900L, topicDid, "Test Topic Title", "Test Topic Description");
    topic.setDid(topicDid);

    exercise = new Exercise("Test Exercise Title", "Test Exercise Title", topic);
    exercise.setExerciseDid(exerciseDid);

    Variant variant = new Variant(0, exercise);

    multiChoiceQuestion =
        new MultiChoiceQuestion(
            "Multi choice question",
            new ArrayList<>(
                Arrays.asList(
                    "First choice always wrong",
                    "Second quite close",
                    "Third is always correct since its the longest",
                    "Fourth just to confuse you")),
            2,
            exercise,
            variant);
    multiChoiceQuestion.setQuestionDid(mcqDid);

    shortAnswerQuestion =
        new ShortAnswerQuestion("Short answer question", exercise, "Keyword", variant);
    shortAnswerQuestion.setQuestionDid(saqDid);

    trueFalseQuestion = new TrueFalseQuestion("True false question", exercise, false, variant);
    trueFalseQuestion.setQuestionDid(tfqDid);

    // Finalize exercise
    exercise.setExerciseIsDraft(false);
  }

  @BeforeEach
  public void setup() {
    // user
    jwt = JwtTestUtil.createStudentJwt();
    AbstractUser mockStudent = mock(AbstractUser.class);
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

    attempt = new Attempt(mockStudent, exercise, Collections.emptyList());
    attempt.setAttemptDid(attemptDid);

    // answer
    Answer multiChoiceAnswer = new Answer("2", attempt, multiChoiceQuestion);
    multiChoiceAnswer.setAnswerDid(mcaDid);

    Answer shortAnswerAnswer =
        new Answer("I have no idea what to write", attempt, shortAnswerQuestion);
    shortAnswerAnswer.setAnswerDid(saDid);

    Answer trueFalseAnswer = new Answer("false", attempt, trueFalseQuestion);
    trueFalseAnswer.setAnswerDid(tfaDid);

    attempt.getAnswers().add(multiChoiceAnswer);
    attempt.getAnswers().add(shortAnswerAnswer);
    attempt.getAnswers().add(trueFalseAnswer);

    // feedback
    List<Object> feedback = new ArrayList<>();
    feedback.add(
        new FeedbackMultiChoiceQuestionDTO(
            UUID.fromString("c1daf844-82ca-441f-99f6-2e5099f60bf4"), true, 2));
    feedback.add(
        new FeedbackShortAnswerQuestionDTO(
            UUID.fromString("c2daf844-82ca-441f-99f6-2e5099f60bf4"), false, "Keyword"));
    feedback.add(
        new FeedbackTrueFalseQuestionDTO(
            UUID.fromString("c3daf844-82ca-441f-99f6-2e5099f60bf4"), true, false));
    feedbackDTO = new FeedbackDTO(2, 3, 66.66, feedback);
  }

  @DisplayName("When creating a new feedback")
  @Nested
  class WhenCreatingNewAttempt {

    // Tests the creation of new feedback.
    @DisplayName("Create a new feedback")
    @Test
    public void testCreateNewFeedback() throws Exception {
      when(feedbackService.getFeedbackAttempt(jwt, attempt.getAttemptDid()))
          .thenReturn(feedbackDTO);

      mockMvc
          .perform(
              get("/api/auth/feedback/" + attempt.getAttemptDid())
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken()))
                  .with(csrf()))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.status_code").value(200))
          .andExpect(jsonPath("$.message").value("Attempt feedback calculated"))
          .andExpect(jsonPath("$.status").value("success"));
    }

    // Tests the creation of new feedback for the wrong attempt.
    @DisplayName("Create a new feedback for the wrong attempt")
    @Test
    public void testCreateNewFeedbackByWrongAttempt() throws Exception {
      when(feedbackService.getFeedbackAttempt(any(), eq(wrongDid)))
          .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Attempt not found"));

      mockMvc
          .perform(
              get("/api/auth/feedback/" + wrongDid)
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data").doesNotExist())
          .andExpect(jsonPath("$.message").value("Attempt not found"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    // Tests the creation of new feedback for the wrong attempt.
    @DisplayName("Create a new feedback passing wrong param")
    @Test
    public void testCreateNewFeedbackByWrongParam() throws Exception {
      mockMvc
          .perform(
              get("/api/auth/feedback/" + "ciao")
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data").doesNotExist())
          .andExpect(jsonPath("$.message").value("Parameter passed is invalid"))
          .andExpect(jsonPath("$.system_error").exists())
          .andExpect(jsonPath("$.status").value("error"));
    }
  }
}
