package ch.usi.inf.bsc.sa4.lab02spring.service;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.CommentDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AIMaterialResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.Comment;
import ch.usi.inf.bsc.sa4.lab02spring.model.Instructor;
import ch.usi.inf.bsc.sa4.lab02spring.model.Material;
import ch.usi.inf.bsc.sa4.lab02spring.model.Student;
import ch.usi.inf.bsc.sa4.lab02spring.model.Topic;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AIMaterialResponseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AIResponseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.InstructorRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.MaterialRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.StudentRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.TopicRepository;
import ch.usi.inf.bsc.sa4.lab02spring.utils.JwtTestUtil;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@Transactional
@DisplayName("Comment Service Test")
class CommentServiceTest {
  @Autowired private CommentService commentService;

  @Autowired private AIResponseRepository aiResponseRepo;

  @Autowired private StudentRepository studentRepo;

  @Autowired private InstructorRepository instructorRepo;

  @Autowired private MaterialRepository materialRepo;

  @Autowired private AIMaterialResponseRepository aiMaterialResponseRepo;

  @Autowired private TopicRepository topicRepo;

  @Autowired private UserService userService;

  @Autowired private AiQuestionService aiQuestionService;

  private final UUID RANDOM_UUID = UUID.fromString("8c69bdcb-dce0-41e7-9361-500f8fbaea0f");

  final String instructorName = "instructor name";
  final String instructorEmail = "instructor@example.com";
  Jwt instructorJwt =
      JwtTestUtil.createJwt(
          "ae2af65e-0899-43b3-8640-3140c6168395", instructorEmail, instructorName);
  Instructor instructor =
      new Instructor("ae2af65e-0899-43b3-8640-3140c6168395", instructorName, instructorEmail);

  final String secondInstructorName = "second instructor name";
  final String secondInstructorEmail = "second_instructor@example.com";
  Jwt secondInstructorJwt =
      JwtTestUtil.createJwt(
          "738c8ba2-9cc9-4ecd-b988-021df1683a8c", secondInstructorEmail, secondInstructorName);
  Instructor secondInstructor =
      new Instructor(
          "738c8ba2-9cc9-4ecd-b988-021df1683a8c", secondInstructorName, secondInstructorEmail);

  Student student;
  UUID studentSubId = UUID.fromString("2def6bdb-3de9-4de2-87ad-9787885861e6");

  Topic topic;

  Material material;
  AIMaterialResponse aiMaterialResponse;

  Comment comment;
  UUID commentDid;

  int counter = 0;

  @BeforeEach
  void init() {
    instructorRepo.save(instructor);
    instructorRepo.save(secondInstructor);

    student = new Student(studentSubId.toString(), "Student", "student@gmail.com");
    studentRepo.save(student);

    topic = new Topic("Test Topic title", "Test Topic description");
    topicRepo.save(topic);

    material = new Material("Title", "Description", new byte[0], LocalDateTime.now(), topic, "md");
    materialRepo.save(material);

    aiMaterialResponse =
        new AIMaterialResponse(
            false,
            "This is a question",
            "This is an answer",
            "This is the highlighted text",
            material,
            student);
    aiMaterialResponseRepo.save(aiMaterialResponse);
  }

  @Test
  @DisplayName(" should not create a comment with random response UUID")
  void creatingACommentWithWrongResponseDid() {
    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> commentService.addComment(RANDOM_UUID, "A comment", instructor));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("404 NOT_FOUND \"AI response not found\"", exception.getMessage());
  }

  @Test
  @DisplayName(" should not find a comment to delete")
  void deletingNotFoundComment() {
    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> commentService.deleteComment(RANDOM_UUID, instructorJwt));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("404 NOT_FOUND \"Comment not found\"", exception.getMessage());
  }

  @Test
  @DisplayName(" should not find a comment to delete")
  void getCommentsNotFoundForResponse() {
    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> commentService.getCommentsForResponse(RANDOM_UUID, instructorJwt));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("404 NOT_FOUND \"AI response not found\"", exception.getMessage());
  }

  @Test
  @DisplayName(" should create a comment")
  void creatingACommentTest() {
    commentDid =
        commentService.addComment(aiMaterialResponse.getAiResponseDID(), "A comment", instructor);
    assertNotNull(commentDid);
  }

  @Nested
  @DisplayName(" after creating a comment")
  class AddComment {
    @BeforeEach
    void init() {
      commentDid =
          commentService.addComment(aiMaterialResponse.getAiResponseDID(), "A comment", instructor);
    }

    @Test
    @DisplayName(" should not delete the comment if it is not yours")
    void deleteACommentThatIsNotYoursTest() {
      HttpClientErrorException exception =
          assertThrows(
              HttpClientErrorException.class,
              () -> commentService.deleteComment(commentDid, secondInstructorJwt));
      assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
      assertEquals(
          "401 You are not authorized to delete a comment made by another instructor",
          exception.getMessage());
    }

    @Test
    @DisplayName(" should delete the comment if it is yours")
    void deleteACommentThatIsYoursTest() {
      commentService.deleteComment(commentDid, instructorJwt);
      ResponseStatusException exception =
          assertThrows(
              ResponseStatusException.class,
              () -> commentService.deleteComment(commentDid, instructorJwt));
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      assertEquals("404 NOT_FOUND \"Comment not found\"", exception.getMessage());
    }

    @Test
    @DisplayName(" should return all comments")
    void getCommentsForResponse() {
      UUID secondCommentDid =
          commentService.addComment(
              aiMaterialResponse.getAiResponseDID(), "A second comment", secondInstructor);

      List<CommentDTO> comments =
          commentService.getCommentsForResponse(
              aiMaterialResponse.getAiResponseDID(), instructorJwt);

      assertEquals(2, comments.size());

      CommentDTO fComment = comments.getFirst();
      assertEquals(commentDid, fComment.commentDid());
      assertEquals("A comment", fComment.commentContent());
      assertEquals(instructor.getName(), fComment.instructorName());
      assertTrue(fComment.isCallerTheOwner());

      CommentDTO sComment = comments.get(1);
      assertEquals(secondCommentDid, sComment.commentDid());
      assertEquals("A second comment", sComment.commentContent());
      assertEquals(secondInstructor.getName(), sComment.instructorName());
      assertFalse(sComment.isCallerTheOwner());
    }
  }
}
