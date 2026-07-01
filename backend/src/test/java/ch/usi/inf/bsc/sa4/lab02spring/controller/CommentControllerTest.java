package ch.usi.inf.bsc.sa4.lab02spring.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.CommentDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.CreateCommentDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AIMaterialResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Comment;
import ch.usi.inf.bsc.sa4.lab02spring.model.Instructor;
import ch.usi.inf.bsc.sa4.lab02spring.model.Material;
import ch.usi.inf.bsc.sa4.lab02spring.model.Student;
import ch.usi.inf.bsc.sa4.lab02spring.model.Topic;
import ch.usi.inf.bsc.sa4.lab02spring.service.CommentService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.JwtTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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

@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("Comment Controller Test")
public class CommentControllerTest {
  @MockitoBean private CommentService commentService;

  @MockitoBean private UserService userService;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private final String BASE_URL = "/api/auth/AI/responses";

  private final String COMMENT_DID_PATH_VARIABLE = "/{comment_did}";
  private final String COMMENT_DID_PARAMETER = "{comment_did}";
  private final String RESPONSE_DID_PATH_VARIABLE = "/{response_did}";
  private final String RESPONSE_DID_PARAMETER = "{response_did}";

  private final String POST_COMMENT_PATH = "/comment";
  private final String DELETE_COMMENT_PATH = "/delete";
  private final String GET_ALL_COMMENTS_PATH = "/allComments";

  private final String POST_COMMENT_URL = BASE_URL + POST_COMMENT_PATH + RESPONSE_DID_PATH_VARIABLE;
  private final String DELETE_COMMENT_URL =
      BASE_URL + POST_COMMENT_PATH + COMMENT_DID_PATH_VARIABLE + DELETE_COMMENT_PATH;
  private final String GET_ALL_COMMENTS_URL =
      BASE_URL + POST_COMMENT_PATH + RESPONSE_DID_PATH_VARIABLE + GET_ALL_COMMENTS_PATH;

  private static final UUID studentDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf1");
  private static final UUID instructor1Did =
      UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf2");
  private static final UUID instructor2Did =
      UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bff");
  private static final UUID topicDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc");
  private static final UUID materialDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfa");
  private static UUID aiResponseDid;

  private static Student student;
  //    private static Object studentPrincipal;
  private static final String STUDENT_NAME = "Student";
  private static final String STUDENT_EMAIL = "student@email.com";

  private static Instructor instructor1;
  private static final String INSTRUCTOR1_NAME = "Instructor 1";
  private static final String INSTRUCTOR1_EMAIL = "instructor1@email.com";

  private static Instructor instructor2;
  private static final String INSTRUCTOR2_NAME = "Instructor 2";
  private static final String INSTRUCTOR2_EMAIL = "instructor2@email.com";

  private static AIMaterialResponse aiMaterialResponse;
  private static final String AI_MATERIAL_QUESTION_TEXT = "Question text";
  private static final String AI_MATERIAL_AI_ANSWER = "AI answer";
  private static final String AI_MATERIAL_HIGHLIGHTED_TEXT = "Highlighted text";

  private static Topic topic;
  private static final String TOPIC_TITLE = "Topic title";
  private static final String TOPIC_DESCRIPTION = "Topic description";

  private static Material material;
  private static final String MATERIAL_TITLE = "Material 1 title";
  private static final String MATERIAL_DESCRIPTION = "Material description";

  private static Comment comment1;
  private static final String COMMENT1_TEXT = "Comment 1 text";
  private static final LocalDateTime comment1Timestamp = LocalDateTime.of(2024, 5, 15, 10, 0);

  private static CommentDTO comment1DTOOwner;
  private static CommentDTO comment1DTO;

  private static Comment comment2;
  private static final String COMMENT2_TEXT = "Comment 2 text";
  private static final LocalDateTime comment2Timestamp = LocalDateTime.of(2024, 5, 15, 12, 0);

  private static CommentDTO comment2DTOOwner;
  private static CommentDTO comment2DTO;

  @BeforeAll
  public static void dataSetUp() {
    topic = new Topic(900L, topicDid, TOPIC_TITLE, TOPIC_DESCRIPTION);
    material = new Material(materialDid, MATERIAL_TITLE, MATERIAL_DESCRIPTION);

    student = new Student(studentDid.toString(), STUDENT_NAME, STUDENT_EMAIL);
    aiMaterialResponse =
        new AIMaterialResponse(
            true,
            AI_MATERIAL_QUESTION_TEXT,
            AI_MATERIAL_AI_ANSWER,
            AI_MATERIAL_HIGHLIGHTED_TEXT,
            material,
            student);
    aiResponseDid = aiMaterialResponse.getAiResponseDID();

    instructor1 = new Instructor(instructor1Did.toString(), INSTRUCTOR1_NAME, INSTRUCTOR1_EMAIL);
    instructor2 = new Instructor(instructor2Did.toString(), INSTRUCTOR2_NAME, INSTRUCTOR2_EMAIL);

    comment1 = new Comment(COMMENT1_TEXT, instructor1, aiMaterialResponse);
    comment2 = new Comment(COMMENT2_TEXT, instructor2, aiMaterialResponse);

    comment1DTOOwner =
        new CommentDTO(
            comment1.getCommentDid(),
            instructor1.getName(),
            true,
            comment1.getCommentText(),
            comment1Timestamp);
    comment1DTO =
        new CommentDTO(
            comment1.getCommentDid(),
            instructor1.getName(),
            false,
            comment1.getCommentText(),
            comment1Timestamp);

    comment2DTOOwner =
        new CommentDTO(
            comment2.getCommentDid(),
            instructor2.getName(),
            true,
            comment2.getCommentText(),
            comment2Timestamp);
    comment2DTO =
        new CommentDTO(
            comment2.getCommentDid(),
            instructor2.getName(),
            false,
            comment2.getCommentText(),
            comment2Timestamp);
  }

  @BeforeEach
  public void setUp() {
    // add comment
    given(commentService.addComment(aiResponseDid, COMMENT1_TEXT, instructor1))
        .willReturn(comment1.getCommentDid());
    given(commentService.addComment(aiResponseDid, COMMENT2_TEXT, instructor2))
        .willReturn(comment2.getCommentDid());

    // delete
    doNothing().when(commentService).deleteComment(eq(comment1.getCommentDid()), any());

    // get all
    given(commentService.getCommentsForResponse(aiResponseDid, instructor1))
        .willReturn(List.of(comment1DTOOwner, comment2DTO));
    given(commentService.getCommentsForResponse(aiResponseDid, instructor2))
        .willReturn(List.of(comment1DTO, comment2DTOOwner));
    given(commentService.getCommentsForResponse(aiResponseDid, student))
        .willReturn(List.of(comment1DTO, comment2DTO));
  }

  @DisplayName(" when the call is made by an instructor")
  @Nested
  class WhenTheCallIsMadeByAnInstructor {
    Jwt jwt;
    AbstractUser mockInstructor1;
    FakeUser fakeInstructor1;
    AbstractUser mockInstructor2;
    FakeUser fakeInstructor2;

    @BeforeEach
    void setUp() {
      given(userService.findOrCreateUser(instructor1)).willReturn(instructor1);
      given(userService.findOrCreateUser(instructor2)).willReturn(instructor2);

      jwt = JwtTestUtil.createInstructorJwt();
      mockInstructor1 = mock(AbstractUser.class);
      when(mockInstructor1.getRole()).thenReturn("INSTRUCTOR");
      when(mockInstructor1.getDid()).thenReturn(instructor1Did);
      fakeInstructor1 =
          new FakeUser(
              mockInstructor1.getDid(),
              mockInstructor1.getSubId(),
              mockInstructor1.getName(),
              mockInstructor1.getEmail());
      when(userService.findOrCreateUser(any())).thenReturn(mockInstructor1);
      when(mockInstructor1.getInstructor()).thenReturn(instructor1);

      given(commentService.getCommentsForResponse(aiResponseDid, fakeInstructor1))
          .willReturn(List.of(comment1DTOOwner, comment2DTO));

      jwt = JwtTestUtil.createInstructorJwt();
      mockInstructor2 = mock(AbstractUser.class);
      when(mockInstructor2.getRole()).thenReturn("INSTRUCTOR");
      when(mockInstructor2.getDid()).thenReturn(instructor2Did);
      fakeInstructor2 =
          new FakeUser(
              mockInstructor2.getDid(),
              mockInstructor2.getSubId(),
              mockInstructor2.getName(),
              mockInstructor2.getEmail());
      when(userService.findOrCreateUser(fakeInstructor2)).thenReturn(mockInstructor2);
      when(mockInstructor2.getInstructor()).thenReturn(instructor2);

      given(commentService.getCommentsForResponse(aiResponseDid, fakeInstructor2))
          .willReturn(List.of(comment1DTO, comment2DTOOwner));
    }

    @Test
    @DisplayName(" the user should not be able to create a comment")
    void testWhenTheInstructorCreatesAComment() throws Exception {
      mockMvc
          .perform(
              post(POST_COMMENT_URL.replace(RESPONSE_DID_PARAMETER, aiResponseDid.toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor1.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(new CreateCommentDTO(COMMENT1_TEXT))))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.data").value(comment1.getCommentDid().toString()))
          .andExpect(jsonPath("$.message").value("Comment added successfully"))
          .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @DisplayName(" the user should not be able to delete a comment")
    void testWhenTheInstructorDeletesAComment() throws Exception {
      mockMvc
          .perform(
              delete(
                      DELETE_COMMENT_URL.replace(
                          COMMENT_DID_PARAMETER, comment1.getCommentDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor1.getToken())))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Comment deleted successfully"))
          .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @DisplayName(" the user should be able to see all ai response comments")
    void testWhenTheInstructorGetsAllComments() throws Exception {
      mockMvc
          .perform(
              get(GET_ALL_COMMENTS_URL.replace(RESPONSE_DID_PARAMETER, aiResponseDid.toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(
                          fakeInstructor1.getToken())))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data").isArray())
          .andExpect(jsonPath("$.data[0].commentDid").value(comment1.getCommentDid().toString()))
          .andExpect(jsonPath("$.data[0].instructorName").value(instructor1.getName()))
          .andExpect(jsonPath("$.data[0].isCallerTheOwner").value(true))
          .andExpect(jsonPath("$.data[0].commentContent").value(COMMENT1_TEXT))
          .andExpect(
              result ->
                  assertTrue(
                      Duration.between(
                                  comment1Timestamp,
                                  LocalDateTime.parse(
                                      JsonPath.read(
                                          result.getResponse().getContentAsString(),
                                          "$.data[0].timeStamp")))
                              .abs()
                              .getSeconds()
                          <= 5))
          .andExpect(jsonPath("$.data[1].commentDid").value(comment2.getCommentDid().toString()))
          .andExpect(jsonPath("$.data[1].instructorName").value(instructor2.getName()))
          .andExpect(jsonPath("$.data[1].isCallerTheOwner").value(false))
          .andExpect(jsonPath("$.data[1].commentContent").value(COMMENT2_TEXT))
          .andExpect(
              result ->
                  assertTrue(
                      Duration.between(
                                  comment2Timestamp,
                                  LocalDateTime.parse(
                                      JsonPath.read(
                                          result.getResponse().getContentAsString(),
                                          "$.data[1].timeStamp")))
                              .abs()
                              .getSeconds()
                          <= 5))
          .andExpect(jsonPath("$.message").value("Comments found successfully"))
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

      given(commentService.getCommentsForResponse(aiResponseDid, fakeStudent))
          .willReturn(List.of(comment1DTO, comment2DTO));
    }

    @Test
    @DisplayName(" the user should not be able to create a comment")
    void testWhenTheUserCreatesAComment() throws Exception {
      mockMvc
          .perform(
              post(POST_COMMENT_URL.replace(RESPONSE_DID_PARAMETER, aiResponseDid.toString()))
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken()))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(new CreateCommentDTO("A comment"))))
          .andExpect(status().isUnauthorized())
          .andExpect(
              jsonPath("$.message")
                  .value("Students aren't allowed to add comments to ai responses"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @DisplayName(" the user should not be able to delete a comment")
    void testWhenTheUserDeletesAComment() throws Exception {
      mockMvc
          .perform(
              delete(
                      DELETE_COMMENT_URL.replace(
                          COMMENT_DID_PARAMETER, comment1.getCommentDid().toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.message").value("Students aren't allowed to delete comments"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @DisplayName(" the user should be able to see all ai response comments")
    void testWhenTheUserGetsAllComments() throws Exception {
      mockMvc
          .perform(
              get(GET_ALL_COMMENTS_URL.replace(RESPONSE_DID_PARAMETER, aiResponseDid.toString()))
                  .with(
                      SecurityMockMvcRequestPostProcessors.authentication(fakeStudent.getToken())))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data").isArray())
          .andExpect(jsonPath("$.data[0].commentDid").value(comment1.getCommentDid().toString()))
          .andExpect(jsonPath("$.data[0].instructorName").value(instructor1.getName()))
          .andExpect(jsonPath("$.data[0].isCallerTheOwner").value(false))
          .andExpect(jsonPath("$.data[0].commentContent").value(COMMENT1_TEXT))
          .andExpect(
              result ->
                  assertTrue(
                      Duration.between(
                                  comment1Timestamp,
                                  LocalDateTime.parse(
                                      JsonPath.read(
                                          result.getResponse().getContentAsString(),
                                          "$.data[0].timeStamp")))
                              .abs()
                              .getSeconds()
                          <= 5))
          .andExpect(jsonPath("$.data[1].commentDid").value(comment2.getCommentDid().toString()))
          .andExpect(jsonPath("$.data[1].instructorName").value(instructor2.getName()))
          .andExpect(jsonPath("$.data[1].isCallerTheOwner").value(false))
          .andExpect(jsonPath("$.data[1].commentContent").value(COMMENT2_TEXT))
          .andExpect(
              result ->
                  assertTrue(
                      Duration.between(
                                  comment2Timestamp,
                                  LocalDateTime.parse(
                                      JsonPath.read(
                                          result.getResponse().getContentAsString(),
                                          "$.data[1].timeStamp")))
                              .abs()
                              .getSeconds()
                          <= 5))
          .andExpect(jsonPath("$.message").value("Comments found successfully"))
          .andExpect(jsonPath("$.status").value("success"));
    }
  }
}
