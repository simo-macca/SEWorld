package ch.usi.inf.bsc.sa4.lab02spring.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.*;
import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import ch.usi.inf.bsc.sa4.lab02spring.service.*;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.JwtTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("AbstractQuestion Controller Test")
class AbstractQuestionControllerTest {

  @MockitoBean private AttemptService attemptService;

  @MockitoBean private QuestionService questionService;

  @MockitoBean private MultiChoiceQuestionService multiChoiceQuestionService;

  @MockitoBean private ShortAnswerQuestionService shortAnswerQuestionService;

  @MockitoBean private TrueFalseQuestionService trueFalseQuestionService;

  @MockitoBean private UserService userService;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private final String BASE_URL = "/api/auth/topic/exercises/question";

  private final String MULTI_CHOICE_QUESTION_PATH = "/multiChoiceQuestion";
  private final String SHORT_ANSWER_QUESTION_PATH = "/ShortAnswerQuestion";
  private final String TRUE_FALSE_QUESTION_PATH = "/TrueFalseQuestion";

  private final String ATTEMPT_DID_PATH_VARIABLE = "/attempt/{attempt_did}";
  private final String EXERCISE_PATH = "/exercise/{exercise_did}";
  private final String SAVE_ALL_PATH = "/save_all/{exercise_did}";
  private final String UPDATE_PATH = "/update/{exercise_did}";

  private final String ATTEMPT_DID_PARAMETER = "{attempt_did}";
  private final String QUESTION_DID_PATH = "/{question_did}";
  private final String QUESTION_PARAMETER = "{question_did}";
  private final String EXERCISE_DID_PATH = "/{exercise_did}";
  private final String EXERCISE_DID_PARAMETER = "{exercise_did}";
  private final String DID_PATH = "/{did}";
  private final String DID_PARAMETER = "{did}";

  private final String CREATE = "/create";
  private final String UPDATE = "/update";

  // AbstractQuestion controller
  // get all questions
  private final String GET_ALL_QUESTIONS = BASE_URL + EXERCISE_PATH;

  private final String SAVE_ALL_QUESTIONS = BASE_URL + SAVE_ALL_PATH;

  private final String UPDATE_QUESTIONS = BASE_URL + UPDATE_PATH;

  // delete a question
  private final String DELETE_QUESTION = BASE_URL + "/delete" + QUESTION_DID_PATH;

  // Multi Choice Controller
  // create new mch question
  private final String CREATE_MCH_QUESTION =
      BASE_URL + MULTI_CHOICE_QUESTION_PATH + CREATE + EXERCISE_DID_PATH;
  // update mch question
  private final String UPDATE_MCH_QUESTION =
      BASE_URL + MULTI_CHOICE_QUESTION_PATH + UPDATE + DID_PATH;

  // True False Controller
  // create new tf question
  private final String CREATE_TF_QUESTION =
      BASE_URL + TRUE_FALSE_QUESTION_PATH + CREATE + EXERCISE_DID_PATH;
  // update tf question
  private final String UPDATE_TF_QUESTION =
      BASE_URL + TRUE_FALSE_QUESTION_PATH + UPDATE + QUESTION_DID_PATH;

  // Short Answer Controller
  // create new sha  question
  private final String CREATE_SHA_QUESTION =
      BASE_URL + SHORT_ANSWER_QUESTION_PATH + CREATE + EXERCISE_DID_PATH;
  // update sha question
  private final String UPDATE_SHA_QUESTION =
      BASE_URL + SHORT_ANSWER_QUESTION_PATH + UPDATE + QUESTION_DID_PATH;

  private static Attempt attempt;
  private static Exercise exercise1;
  private static Variant variant1;

  private static final String TITLE = "Title #";
  private static final String DESCRIPTION = "Description #";

  private static MultiChoiceQuestion multiChoiceQuestion1;
  private static MultiChoiceQuestionDTO multiChoiceQuestionDTOInstr;
  private static MultiChoiceQuestionDTO multiChoiceQuestionDTOStud;
  private static MultiChoiceQuestion multiChoiceQuestion2;
  private static MultiChoiceQuestionDTO changeMultiChoiceDTOInstr;

  private static ShortAnswerQuestion shortAnswerQuestion1;
  private static ShortAnswerQuestionDTO shortAnswerQuestionDTOInstr;
  private static ShortAnswerQuestionDTO shortAnswerQuestionDTOStud;
  private static ShortAnswerQuestion shortAnswerQuestion2;
  private static ShortAnswerQuestionDTO changeShortAnswerQuestionDTOInstr;

  private static TrueFalseQuestion trueFalseQuestion1;
  private static TrueFalseQuestionDTO trueFalseQuestionDTOInstr;
  private static TrueFalseQuestionDTO trueFalseQuestionDTOStud;
  private static TrueFalseQuestion trueFalseQuestion2;
  private static TrueFalseQuestionDTO changeTrueFalseQuestionDTOInstr;

  private static List<AbstractQuestion> questions1;

  private static List<QuestionDTO> questionDTOs;

  private static final List<String> KEYS_1 = List.of("choice 1", "choice 2", "choice 3");
  private static final List<String> KEYS_2 = List.of("A", "B");

  private static final UUID studentDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf1");
  private static final UUID instructorDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf2");
  private static final UUID exerciseDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfd");
  private static final UUID attemptDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfe");
  private static final UUID mcqDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bff");
  private static final UUID saqDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfb");
  private static final UUID tfqDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfa");
  private static final UUID variantDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc");

  private static SwapIndexQuestionsDTO swapDTO;

  private static Set<QuestionIndexPair> pairs;

  @BeforeAll
  static void basicSetup() {
    exercise1 = new Exercise(TITLE, DESCRIPTION, null);
    exercise1.setExerciseDid(exerciseDid);

    variant1 = new Variant(0, exercise1);
    variant1.setVariantDid(variantDid);

    attempt = new Attempt(null, exercise1, questions1);
    attempt.setAttemptDid(attemptDid);

    multiChoiceQuestion1 = new MultiChoiceQuestion(TITLE, KEYS_1, 0, exercise1, variant1);
    multiChoiceQuestion1.setQuestionDid(mcqDid);
    multiChoiceQuestionDTOInstr = multiChoiceQuestion1.convertToDTO(true);
    multiChoiceQuestionDTOStud = multiChoiceQuestion1.convertToDTO(false);

    shortAnswerQuestion1 = new ShortAnswerQuestion(TITLE, exercise1, KEYS_1.getFirst(), variant1);
    shortAnswerQuestion1.setQuestionDid(saqDid);
    shortAnswerQuestionDTOInstr = shortAnswerQuestion1.convertToDTO(true);
    shortAnswerQuestionDTOStud = shortAnswerQuestion1.convertToDTO(false);

    trueFalseQuestion1 = new TrueFalseQuestion(TITLE, exercise1, false, variant1);
    trueFalseQuestion1.setQuestionDid(tfqDid);
    trueFalseQuestionDTOInstr = trueFalseQuestion1.convertToDTO(true);
    trueFalseQuestionDTOStud = trueFalseQuestion1.convertToDTO(false);

    // updated
    multiChoiceQuestion2 = new MultiChoiceQuestion(TITLE, KEYS_2, 0, exercise1, variant1);
    multiChoiceQuestion2.setQuestionDid(multiChoiceQuestion1.getQuestionDid());
    changeMultiChoiceDTOInstr = multiChoiceQuestion2.convertToDTO(true);

    shortAnswerQuestion2 = new ShortAnswerQuestion(TITLE, exercise1, KEYS_2.getFirst(), variant1);
    shortAnswerQuestion2.setQuestionDid(shortAnswerQuestion1.getQuestionDid());
    changeShortAnswerQuestionDTOInstr = shortAnswerQuestion2.convertToDTO(true);

    trueFalseQuestion2 = new TrueFalseQuestion(TITLE, exercise1, true, variant1);
    trueFalseQuestion2.setQuestionDid(trueFalseQuestion1.getQuestionDid());
    changeTrueFalseQuestionDTOInstr = trueFalseQuestion2.convertToDTO(true);

    questions1 = List.of(multiChoiceQuestion1, shortAnswerQuestion1, trueFalseQuestion1);

    questionDTOs = List.of(multiChoiceQuestionDTOInstr, shortAnswerQuestionDTOInstr);

    pairs = new HashSet<>();
    pairs.add(new QuestionIndexPair(0, 1));

    swapDTO = new SwapIndexQuestionsDTO(questionDTOs, pairs);
  }

  @BeforeEach
  public void setUp() {

    given(attemptService.getExerciseDidFromAttempt(attempt.getAttemptDid()))
        .willReturn(exercise1.getExerciseDid());

    // get all - question service
    List<QuestionDTO> listInstr = questions1.stream().map(q -> q.convertToDTO(true)).toList();
    List<QuestionDTO> listStud = questions1.stream().map(q -> q.convertToDTO(false)).toList();
    given(questionService.getAll(any(UUID.class), eq(true))).willReturn(listInstr);
    given(questionService.getAll(any(UUID.class), eq(false))).willReturn(listStud);

    // update - multi choice service
    given(
            multiChoiceQuestionService.update(
                changeMultiChoiceDTOInstr, multiChoiceQuestion1.getQuestionDid()))
        .willReturn(multiChoiceQuestion2.convertToDTO(true));

    // update - true false service
    given(
            trueFalseQuestionService.update(
                changeTrueFalseQuestionDTOInstr, trueFalseQuestion1.getQuestionDid()))
        .willReturn(changeTrueFalseQuestionDTOInstr);

    // update - short answer service
    given(
            shortAnswerQuestionService.update(
                changeShortAnswerQuestionDTOInstr, shortAnswerQuestion1.getQuestionDid()))
        .willReturn(shortAnswerQuestion2.convertToDTO(true));

    // delete
    doNothing().when(questionService).deleteQuestionByDID(shortAnswerQuestion1.getQuestionDid());
    doNothing().when(questionService).deleteQuestionByDID(trueFalseQuestion1.getQuestionDid());
    doNothing().when(questionService).deleteQuestionByDID(multiChoiceQuestion1.getQuestionDid());
  }

  @DisplayName("All unauthorized question should return the error code 401")
  @Test
  void testAllUnauthorizedQuestionShouldReturn401() throws Exception {
    mockMvc
        .perform(
            post(
                CREATE_MCH_QUESTION.replace(
                    EXERCISE_DID_PATH, exercise1.getExerciseDid().toString())))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            post(
                CREATE_TF_QUESTION.replace(
                    EXERCISE_DID_PATH, exercise1.getExerciseDid().toString())))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            post(
                CREATE_SHA_QUESTION.replace(
                    EXERCISE_DID_PATH, exercise1.getExerciseDid().toString())))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            post(
                UPDATE_MCH_QUESTION.replace(
                    DID_PATH, multiChoiceQuestion1.getQuestionDid().toString())))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            post(
                UPDATE_TF_QUESTION.replace(
                    QUESTION_DID_PATH, trueFalseQuestion1.getQuestionDid().toString())))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            post(
                UPDATE_SHA_QUESTION.replace(
                    QUESTION_DID_PATH, shortAnswerQuestion1.getQuestionDid().toString())))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            delete(
                DELETE_QUESTION.replace(
                    QUESTION_DID_PATH, shortAnswerQuestion1.getQuestionDid().toString())))
        .andExpect(status().isUnauthorized());
  }

  @DisplayName("When the call is made by the instructor")
  @Nested
  class WhenTheCallIsMadeByTheInstructor {

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

    @DisplayName("Get all question of an exercise with the answers")
    @Test
    void testGetAllQuestionOfExerciseWithAnswers() throws Exception {
      when(questionService.getAll(exerciseDid, true)).thenReturn(questionDTOs);
      mockMvc
          .perform(
              get(GET_ALL_QUESTIONS.replace(EXERCISE_DID_PARAMETER, exerciseDid.toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data").isArray())
          .andExpect(
              jsonPath("$.data[0].questionDid")
                  .value(multiChoiceQuestion1.getQuestionDid().toString()))
          .andExpect(
              jsonPath("$.data[1].questionDid")
                  .value(shortAnswerQuestion1.getQuestionDid().toString()));
    }

    @DisplayName("Delete one question")
    @Test
    void deleteOneQuestion() throws Exception {
      mockMvc
          .perform(
              delete(
                      DELETE_QUESTION.replace(
                          QUESTION_PARAMETER, multiChoiceQuestionDTOInstr.questionDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(
              jsonPath("$.message")
                  .value(
                      "AbstractQuestion with DID "
                          + multiChoiceQuestionDTOInstr.questionDid().toString()
                          + " has been deleted"));

      mockMvc
          .perform(
              delete(
                      DELETE_QUESTION.replace(
                          QUESTION_PARAMETER, shortAnswerQuestionDTOInstr.questionDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(
              jsonPath("$.message")
                  .value(
                      "AbstractQuestion with DID "
                          + shortAnswerQuestionDTOInstr.questionDid().toString()
                          + " has been deleted"));

      mockMvc
          .perform(
              delete(
                      DELETE_QUESTION.replace(
                          QUESTION_PARAMETER, trueFalseQuestionDTOInstr.questionDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken())))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(
              jsonPath("$.message")
                  .value(
                      "AbstractQuestion with DID "
                          + trueFalseQuestionDTOInstr.questionDid().toString()
                          + " has been deleted"));
    }

    @DisplayName("Test saveAllQuestions")
    @Test
    void testSaveAllQuestions() throws Exception {
      doNothing().when(questionService).saveAllQuestions(questionDTOs, exerciseDid);
      mockMvc
          .perform(
              post(SAVE_ALL_QUESTIONS.replace(EXERCISE_DID_PARAMETER, exerciseDid.toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(questionDTOs)))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Questions saved successfully"))
          .andExpect(jsonPath("$.status").value("success"));
    }

    @DisplayName("Test update questions")
    @Test
    void testUpdateQuestions() throws Exception {
      doNothing().when(questionService).updateQuestion(swapDTO, exerciseDid);
      mockMvc
          .perform(
              patch(UPDATE_QUESTIONS.replace(EXERCISE_DID_PARAMETER, exerciseDid.toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(swapDTO)))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Questions successfully updated"))
          .andExpect(jsonPath("$.status").value("success"));
    }
  }
}
