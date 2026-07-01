package ch.usi.inf.bsc.sa4.lab02spring.controller;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AiQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.RateQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AIExerciseResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.AIMaterialResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Instructor;
import ch.usi.inf.bsc.sa4.lab02spring.model.Material;
import ch.usi.inf.bsc.sa4.lab02spring.model.Student;
import ch.usi.inf.bsc.sa4.lab02spring.service.AiQuestionService;
import ch.usi.inf.bsc.sa4.lab02spring.service.AttemptService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.JwtTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("Comment Controller Test")
public class AiQuestionControllerTest {
  @MockitoBean private AttemptService attemptService;

  @MockitoBean private AiQuestionService aiQuestionService;

  @MockitoBean private UserService userService;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private final String BASE_URL = "/api/auth/AI";

  private final String MATERIAL_DID_PATH = "/{material_did}";
  private final String MATERIAL_DID_PATH_VARIABLE = "{material_did}";
  private final String RESPONSE_DID_PATH = "/{response_did}";
  private final String RESPONSE_DID_PATH_VARIABLE = "{response_did}";
  private final String EXERCISE_DID_PATH = "/{exercise_did}";
  private final String EXERCISE_DID_PATH_VARIABLE = "{exercise_did}";
  private final String ANSWER_DID_PATH = "/{answer_did}";
  private final String ANSWER_DID_PATH_VARIABLE = "{answer_did}";

  private final String ASK_MATERIAL_PATH = "/ask/materials";
  private final String RATE_QUESTION_PATH = "/response/rate";
  private final String GET_ALL_PATH = "/response";
  private final String PUBLISH_RESPONSES_PATH = "/response/publish";
  private final String PUBLISHED_MATERIAL_RESPONSES_PATH = "/response/public";
  private final String RESPONSES_MINE_PATH = "/response/mine";
  private final String GENERATE_QUESTION_VARIANT_PATH = "/generate/question_variant";
  private final String RESPONSES_PATH = "/responses";
  private final String ALL_GENERATE_AI_EXPLANATIONS = "/all_generated_AI_explanations";
  private final String ASK_QUESTION_EXPLANATION_PATH = "/ask/questions/explanation/new";
  private final String REFRESH_QUESTION_EXPLANATION_PATH = "/ask/questions/explanation/refresh";
  private final String GET_PUBLISHED_QUESTION_EXPLANATION_PATH =
      "/ask/questions/explanation/published";

  private final String ASK_MATERIAL_URL = BASE_URL + ASK_MATERIAL_PATH + MATERIAL_DID_PATH;
  private final String RATE_QUESTION_URL = BASE_URL + RATE_QUESTION_PATH + RESPONSE_DID_PATH;
  private final String GET_ALL_AI_RESPONSES_URL = BASE_URL + GET_ALL_PATH;
  private final String PUBLISH_AI_RESPONSE_BY_RESPONSE_URL =
      BASE_URL + PUBLISH_RESPONSES_PATH + RESPONSE_DID_PATH;
  private final String GET_PUBLIC_AI_MATERIAL_RESPONSES_URL =
      BASE_URL + PUBLISHED_MATERIAL_RESPONSES_PATH + MATERIAL_DID_PATH;
  private final String GET_RESPONSES_MINE_URL = BASE_URL + RESPONSES_MINE_PATH;
  private final String POST_GENERATE_QUESTION_VARIANT_URL =
      BASE_URL + GENERATE_QUESTION_VARIANT_PATH;
  private final String GET_ALL_GENERATED_AI_EXPLANATIONS =
      BASE_URL + RESPONSES_PATH + EXERCISE_DID_PATH + ALL_GENERATE_AI_EXPLANATIONS;
  private final String ASK_NEW_QUESTION_EXPLANATION =
      BASE_URL + ASK_QUESTION_EXPLANATION_PATH + ANSWER_DID_PATH;
  private final String REFRESH_NEW_QUESTION_EXPLANATION =
      BASE_URL + REFRESH_QUESTION_EXPLANATION_PATH + ANSWER_DID_PATH;
  private final String GET_PUBLIC_AI_EXPLANATION_RESPONSES =
      BASE_URL + GET_PUBLISHED_QUESTION_EXPLANATION_PATH + ANSWER_DID_PATH;

  private static final UUID studentDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf1");
  private static final UUID instructorDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf2");
  private static final UUID instructor2Did =
      UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bff");
  private static final UUID exerciseDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc");
  private static final UUID materialDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfa");
  private static UUID aiResponseDid;

  private static Student student;
  private static final String STUDENT_NAME = "Student";
  private static final String STUDENT_EMAIL = "student@email.com";

  private static Instructor instructor;
  private static final String INSTRUCTOR_NAME = "Instructor 1";
  private static final String INSTRUCTOR_EMAIL = "instructor1@email.com";

  private static Material material;
  private static final String MATERIAL_TITLE = "Material 1 title";
  private static final String MATERIAL_DESCRIPTION = "Material description";

  private static AiQuestionDTO aiQuestionDto;

  private static AIMaterialResponse aiMaterialResponse;
  private static AIMaterialResponse aiMaterialResponsePublic;

  private static AIExerciseResponse aiExerciseResponse;
  private static AIExerciseResponse aiExerciseResponsePublic;

  private static RateQuestionDTO rateQuestionDto;

  @BeforeAll
  public static void dataSetUp() {
    student = new Student(studentDid.toString(), STUDENT_NAME, STUDENT_EMAIL);
    instructor = new Instructor(instructorDid.toString(), INSTRUCTOR_NAME, INSTRUCTOR_EMAIL);

    material = new Material(materialDid, MATERIAL_TITLE, MATERIAL_DESCRIPTION);

    aiQuestionDto = new AiQuestionDTO("Highlighted Text", "A question");
    aiMaterialResponse =
        new AIMaterialResponse(
            false,
            aiQuestionDto.question(),
            "Ai Answer",
            aiQuestionDto.highlightedText(),
            material,
            student);
    aiMaterialResponsePublic =
        new AIMaterialResponse(
            true,
            aiQuestionDto.question(),
            "Ai Answer",
            aiQuestionDto.highlightedText(),
            material,
            student);

    aiExerciseResponse =
        new AIExerciseResponse(
            false, "Exercise Question", student, "Exercise Highlighted Text", null, null);
    aiExerciseResponsePublic =
        new AIExerciseResponse(
            true, "Exercise Question", student, "Exercise Highlighted Text", null, null);

    rateQuestionDto = new RateQuestionDTO(0);
  }

  @BeforeEach
  public void setUp() {
    // add comment
    given(
            aiQuestionService.askAboutHighlight(
                eq(aiQuestionDto.highlightedText()),
                eq(materialDid),
                eq(aiQuestionDto.question()),
                any()))
        .willReturn(aiMaterialResponse);

    // rate question
//    given(
//            aiQuestionService.rateQuestion(
//                aiMaterialResponse.getAiResponseDID(), student, rateQuestionDto))
//        .willReturn(aiMaterialResponse);

    // get all
    given(aiQuestionService.getAll()).willReturn(List.of(aiMaterialResponse.toDTO(0)));

    // publish
    given(aiQuestionService.publish(aiMaterialResponse.getAiResponseDID()))
        .willReturn(aiMaterialResponsePublic);

    // get all published
    given(aiQuestionService.getAllPublished(eq(material.getMaterialDid()), any()))
        .willReturn(List.of(aiMaterialResponsePublic.toDTO(0)));

    // get all user responses
    given(aiQuestionService.getAllUserResponses(student))
        .willReturn(List.of(aiMaterialResponse.toDTO(0)));

    // generate question variants
    given(aiQuestionService.generateQuestionVariant(any())).willReturn(null);

    // generate question variants
    given(aiQuestionService.getQuestionsAndAnswers(exerciseDid)).willReturn(List.of());

    // generate question variants
    given(aiQuestionService.askAboutAnswer(any(), any())).willReturn(aiExerciseResponse);

    // generate question variants
    given(aiQuestionService.refreshAnswer(any(), any())).willReturn(aiExerciseResponse);

    // generate question variants
//    given(aiQuestionService.getPublicAiExerciseResponses(any()))
//        .willReturn(List.of(aiExerciseResponse));
  }

  @DisplayName(" when the call is made by an instructor")
  @Nested
  class WhenTheCallIsMadeByAnInstructor {
    Jwt jwt;
    AbstractUser mockInstructor;
    FakeUser fakeInstructor;

    @BeforeEach
    void setUp() {
      given(userService.findOrCreateUser(instructor)).willReturn(instructor);

      jwt = JwtTestUtil.createInstructorJwt();
      mockInstructor = mock(AbstractUser.class);
      when(mockInstructor.getRole()).thenReturn("INSTRUCTOR");
      when(mockInstructor.getDid()).thenReturn(instructor.getDid());
      fakeInstructor =
          new FakeUser(
              mockInstructor.getDid(),
              mockInstructor.getSubId(),
              mockInstructor.getName(),
              mockInstructor.getEmail());
      when(userService.findOrCreateUser(any())).thenReturn(mockInstructor);
      when(mockInstructor.getInstructor()).thenReturn(instructor);
    }

    @Test
    @DisplayName(" asks about material explanation should return forbidden")
      void testAskAboutMaterialHighlight() throws Exception {
        mockMvc
                .perform(
                        post(ASK_MATERIAL_URL.replace(MATERIAL_DID_PATH_VARIABLE, material.getMaterialDid().toString()))
                                .with(
                                        SecurityMockMvcRequestPostProcessors.authentication(fakeInstructor.getToken()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(aiQuestionDto))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You do not have permission to publish this resource"))
                .andExpect(jsonPath("$.status").value("error"));
      }


    @Test
    @DisplayName(" asks about material explanation should return forbidden")
    void testRetriveAllResponses() throws Exception {
      mockMvc
              .perform(
                      get(GET_ALL_AI_RESPONSES_URL)
                              .with(
                                      SecurityMockMvcRequestPostProcessors.authentication(fakeInstructor.getToken()))
              )
              .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$.data").isArray())
              .andExpect(jsonPath("$.data[0].aiResponseDid").value(aiMaterialResponse.getAiResponseDID().toString()))
              .andExpect(jsonPath("$.data[0].public").value(false))
              .andExpect(jsonPath("$.data[0].rating").value(0))
              .andExpect(jsonPath("$.data[0].answer").value("Ai Answer"))
              .andExpect(jsonPath("$.data[0].questionType").value("Markdown Question"))
              .andExpect(jsonPath("$.message").value("Response rated"))
              .andExpect(jsonPath("$.status").value("success"));
    }
  }

  @Nested
  @DisplayName(" when the call is made by a student")
  class WhenTheCallIsMadeByAnStudent {
    Jwt jwt;
    AbstractUser mockStudent;
    FakeUser fakeStudent;

    @BeforeEach
    void setUp() {
      jwt = JwtTestUtil.createStudentJwt();
      mockStudent = mock(AbstractUser.class);
      when(mockStudent.getRole()).thenReturn("STUDENT");
      when(mockStudent.getDid()).thenReturn(student.getDid());
      when(userService.findOrCreateUser(any())).thenReturn(student);
      fakeStudent =
          new FakeUser(student.getDid(), student.getSubId(), student.getName(), student.getEmail());
    }
  }
}
