package ch.usi.inf.bsc.sa4.lab02spring.service;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.*;
import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import ch.usi.inf.bsc.sa4.lab02spring.repository.*;
import ch.usi.inf.bsc.sa4.lab02spring.utils.JwtTestUtil;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.transaction.Transactional;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@Transactional
@DisplayName("Feedback Service Test")
class FeedbackServiceTests {

  @Autowired private UserService userService;

  @Autowired private TopicRepository topicRepository;

  @Autowired private ExerciseRepository exerciseRepository;

  @Autowired private MultiChoiceQuestionRepository multiChoiceQuestionRepository;

  @Autowired private TrueFalseQuestionRepository trueFalseQuestionRepository;

  @Autowired private ShortAnswerQuestionRepository shortAnswerQuestionRepository;

  @Autowired private AttemptRepository attemptRepository;

  @Autowired private AnswerRepository answerRepository;

  @Autowired private FeedbackService feedbackService;

  @Autowired private VariantRepository variantRepository;

  @MockitoBean private AiQuestionService aiQuestionService;

  @Autowired private MockMvc mockMvc;

  // topic
  Topic topic;

  // exercise
  Exercise exercise;

  // attempt
  Attempt studentAttempt;
  Attempt instrcutorAttempt;

  // question
  MultiChoiceQuestion multiChoiceQuestion;
  TrueFalseQuestion trueFalseQuestion;
  ShortAnswerQuestion shortAnswerQuestion;
  List<AbstractQuestion> questions = new ArrayList<>();

  // answer
  List<Answer> answers = new ArrayList<>();

  // student
  Jwt studentJwt = JwtTestUtil.createStudentJwt();
  AbstractUser student;

  Jwt wrongStudentJwt =
      JwtTestUtil.createStudentJwt("wrong-sub-1", "wrong.student@example.com", "wrong student");
  AbstractUser wrongStudent;

  // instructor
  Jwt instructorJwt = JwtTestUtil.createInstructorJwt();
  AbstractUser instructor;

  // utils
  UUID wrongDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc");
  @Autowired private AttemptService attemptService;

  @BeforeEach
  void setUp() {
    // Create users
    student = userService.findOrCreateUser(studentJwt);
    instructor = userService.findOrCreateUser(instructorJwt);
    wrongStudent = userService.findOrCreateUser(wrongStudentJwt);

    topic = topicRepository.save(new Topic("Test Topic title", "Test Topic description"));
    exercise =
        exerciseRepository.save(
            new Exercise("Test Exercise Title", "Test Exercise description", topic));

    Variant variant = new Variant(0, exercise);
    variantRepository.save(variant);

    createQuestions(exercise, variant);

    exercise.setExerciseIsDraft(false);

    // Create attempts and answers
    studentAttempt = createAttempt(student, exercise);
    instrcutorAttempt = createAttempt(instructor, exercise);

    when(aiQuestionService.isCorrect(anyString(), any(), anyString(), any())).thenReturn(false);
  }

  private void createQuestions(Exercise exercise, Variant variant) {
    multiChoiceQuestion = createMultiChoiceQuestion(exercise, variant);
    questions.add(multiChoiceQuestion);

    shortAnswerQuestion = createShortAnswerQuestion(exercise, variant);
    questions.add(shortAnswerQuestion);

    trueFalseQuestion = createTrueFalseQuestion(exercise, variant);
    questions.add(trueFalseQuestion);
  }

  private MultiChoiceQuestion createMultiChoiceQuestion(Exercise exercise, Variant variant) {
    List<String> choices = Arrays.asList("A", "B", "C", "D");
    return multiChoiceQuestionRepository.save(
        new MultiChoiceQuestion(
            "Multi choice question", new ArrayList<>(choices), 2, exercise, variant));
  }

  private ShortAnswerQuestion createShortAnswerQuestion(Exercise exercise, Variant variant) {
    return shortAnswerQuestionRepository.save(
        new ShortAnswerQuestion(
            "Short answer question",
            exercise,
            "Photosynthesis is the process by which green plants and"
                + " some other organisms use sunlight to synthesize nutrients from carbon dioxide and water."
                + " During this process, oxygen is released as a waste product.",
            variant));
  }

  private TrueFalseQuestion createTrueFalseQuestion(Exercise exercise, Variant variant) {
    return trueFalseQuestionRepository.save(
        new TrueFalseQuestion("True false question", exercise, false, variant));
  }

  private Attempt createAttempt(AbstractUser user, Exercise exercise) {
    Attempt attempt = attemptRepository.save(new Attempt(user, exercise, questions));
    assertEquals(user.getDid(), attempt.getUser().getDid());
    assertEquals(exercise.getExerciseDid(), attempt.getExercise().getExerciseDid());

    List<Answer> attemptAnswers = new ArrayList<>();

    Answer multiAnswer = createAnswer("2", attempt, multiChoiceQuestion);
    attemptAnswers.add(multiAnswer);
    answers.add(multiAnswer);

    Answer shortAnswer =
        createAnswer(
            "The French Revolution was a period of radical political and "
                + "societal change in France that began with the Estates General of 1789 and "
                + "ended with the formation of the French Consulate in November 1799.",
            attempt,
            shortAnswerQuestion);
    attemptAnswers.add(shortAnswer);
    answers.add(shortAnswer);

    Answer tfAnswer = createAnswer("false", attempt, trueFalseQuestion);
    attemptAnswers.add(tfAnswer);
    answers.add(tfAnswer);

    attempt.setAnswers(attemptAnswers);
    attempt.setAttemptIsSubmitted(true);

    return attempt;
  }

  private Answer createAnswer(String answerText, Attempt attempt, AbstractQuestion question) {
    Answer answer = answerRepository.save(new Answer(answerText, attempt, question));
    assertEquals(question.getQuestionDid(), answer.getQuestion().getQuestionDid());
    assertEquals(attempt.getAttemptDid(), answer.getAttempt().getAttemptDid());
    return answer;
  }

  private List<Object> createFeedbackData() {
    return List.of(
        new FeedbackMultiChoiceQuestionDTO(multiChoiceQuestion.getQuestionDid(), true, 2),
        new FeedbackShortAnswerQuestionDTO(
            shortAnswerQuestion.getQuestionDid(),
            false,
            "Photosynthesis is the process by which green plants and"
                + " some other organisms use sunlight to synthesize nutrients from carbon dioxide and water."
                + " During this process, oxygen is released as a waste product."),
        new FeedbackTrueFalseQuestionDTO(trueFalseQuestion.getQuestionDid(), true, false));
  }

  @DisplayName("Get a feedback by user")
  @Test
  void getFeedbackByUser() {
    FeedbackDTO studentFeedback =
        feedbackService.getFeedbackAttempt(studentJwt, studentAttempt.getAttemptDid());

    System.out.println(studentFeedback);

    assertEquals(2, studentFeedback.totalCorrect());
    assertEquals(3, studentFeedback.totalQuestions());
    assertEquals(66.67, studentFeedback.percentage(), 0.001);
    assertIterableEquals(createFeedbackData(), studentFeedback.feedbackData());

    FeedbackDTO instructorFeedback =
        feedbackService.getFeedbackAttempt(instructorJwt, instrcutorAttempt.getAttemptDid());
    assertEquals(2, instructorFeedback.totalCorrect());
    assertEquals(3, instructorFeedback.totalQuestions());
    assertEquals(66.67, instructorFeedback.percentage(), 0.001);
    assertEquals(createFeedbackData(), instructorFeedback.feedbackData());
    assertIterableEquals(createFeedbackData(), studentFeedback.feedbackData());
  }

  @DisplayName("When getting a feedback but receive error")
  @Nested
  class GetFeedbackError {

    @DisplayName("Get a feedback for the wrong user")
    @Test
    void getFeedbackForWrongUser() {
      HttpClientErrorException studenteException =
          assertThrowsExactly(
              HttpClientErrorException.class,
              () ->
                  feedbackService.getFeedbackAttempt(
                      wrongStudentJwt, studentAttempt.getAttemptDid()));
      assertEquals(HttpStatus.UNAUTHORIZED, studenteException.getStatusCode());
      assertEquals(
          "401 This user " + wrongStudent.getName() + " does not have this attempt",
          studenteException.getMessage());

      HttpClientErrorException instructorException =
          assertThrowsExactly(
              HttpClientErrorException.class,
              () ->
                  feedbackService.getFeedbackAttempt(
                      instructorJwt, studentAttempt.getAttemptDid()));
      assertEquals(HttpStatus.UNAUTHORIZED, instructorException.getStatusCode());
      assertEquals(
          "401 This user " + instructor.getName() + " does not have this attempt",
          instructorException.getMessage());
    }

    @DisplayName("Get a feedback for the wrong attempt")
    @Test
    void getFeedbackForWrongAttempt() {
      HttpClientErrorException exception =
          assertThrowsExactly(
              HttpClientErrorException.class,
              () -> feedbackService.getFeedbackAttempt(studentJwt, wrongDid));
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      assertEquals("404 Attempt not found", exception.getMessage());
    }

    @DisplayName("Get a feedback for a non submitted attempt")
    @Test
    void getFeedbackForNoSubmittedAttempt() {
      studentAttempt.setAttemptIsSubmitted(false);
      HttpClientErrorException exception =
          assertThrowsExactly(
              HttpClientErrorException.class,
              () -> feedbackService.getFeedbackAttempt(studentJwt, studentAttempt.getAttemptDid()));
      assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
      assertEquals("401 This attempt is not submitted", exception.getMessage());
    }
  }
}
