package ch.usi.inf.bsc.sa4.lab02spring.service;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.*;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.*;
import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import ch.usi.inf.bsc.sa4.lab02spring.repository.*;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.transaction.Transactional;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@Transactional
@DisplayName("Answer Service Test")
public class AnswerServiceTest {

  @Autowired private AnswerService answerService;
  @Autowired private AnswerRepository answerRepository;
  @Autowired private TopicRepository topicRepository;
  @Autowired private ExerciseRepository exerciseRepository;
  @Autowired private AttemptRepository attemptRepository;
  @Autowired private QuestionRepository questionRepository;
  @Autowired private UserRepository userRepository;

  private AbstractUser testUser;
  Topic testTopic;
  Exercise testExercise;
  private Attempt testAttempt;
  private AbstractQuestion testQuestion;

  private UUID wrongDID = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf1");
  private UUID attemptDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf2");
  private UUID questionDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc");
  OAuth2User principal;

  @BeforeEach
  void setUp() {
    testUser = new Instructor("Test AbstractUser", "test@example.com", "sub123");
    userRepository.save(testUser);

    testTopic = new Topic("TopicTitle", "TopicDescription");
    topicRepository.save(testTopic);

    testExercise = new Exercise("ExerciseTitle", "ExerciseDescripton", testTopic);
    exerciseRepository.save(testExercise);

    testAttempt = new Attempt(testUser, testExercise, Collections.emptyList());
    attemptRepository.save(testAttempt);

    testQuestion =
        new MultiChoiceQuestion(
            "QuestionTitle", new ArrayList<>(List.of("choices1")), 0, testExercise, null);
    questionRepository.save(testQuestion);

    principal =
        new DefaultOAuth2User(
            List.of(new SimpleGrantedAuthority("ROLE_USER")),
            Map.of(
                "sub", testUser.getSubId(),
                "email", testUser.getEmail(),
                "name", testUser.getEmail()),
            "sub");
  }

  @Test
  @DisplayName("Should create a valid answer")
  void testCreateAnswer() {
    String answerContent = "This is an answer.";
    Answer createdAnswer =
        answerService
            .createAnswer(
                answerContent,
                testAttempt.getAttemptDid(),
                testQuestion.getQuestionDid(),
                principal)
            .orElseThrow();
    assertNotNull(createdAnswer);
    assertEquals(answerContent, createdAnswer.getAnswerContent());
    assertEquals(testAttempt.getAttemptDid(), createdAnswer.getAttemptDid());
    assertEquals(testQuestion.getQuestionDid(), createdAnswer.getQuestionDid());
  }

  @Test
  @DisplayName("Should fail creating an answer for a non-existent attempt")
  void testCreateAnswerWithInvalidAttempt() {
    HttpClientErrorException exception =
        assertThrows(
            HttpClientErrorException.class,
            () ->
                answerService.createAnswer(
                    "Test Answer", wrongDID, testQuestion.getQuestionDid(), testUser));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }

  @Test
  @DisplayName("Should update an existing answer")
  void testUpdateAnswer() {
    Answer answer = new Answer("Old Content", testAttempt, testQuestion);
    answerRepository.save(answer);

    AnswerDTO answerDTO =
        new AnswerDTO(answer.getDid(), "Updated Content", attemptDid, questionDid);
    Optional<AnswerDTO> updatedAnswerDTO =
        answerService.updateAnswer(answer.getDid(), answerDTO, principal);

    assertTrue(updatedAnswerDTO.isPresent());
    assertEquals("Updated Content", updatedAnswerDTO.get().answerContent());
  }

  @Test
  @DisplayName("Should fail updating a non-existent answer")
  void testUpdateNonExistentAnswer() {
    AnswerDTO answerDTO = new AnswerDTO(wrongDID, "Updated Content", attemptDid, questionDid);
    HttpClientErrorException exception =
        assertThrows(
            HttpClientErrorException.class,
            () -> answerService.updateAnswer(wrongDID, answerDTO, testUser));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }

  @Test
  @DisplayName("Should delete an existing answer")
  void testDeleteAnswer() {
    Answer answer = new Answer("To be deleted", testAttempt, testQuestion);
    answerRepository.save(answer);

    answerService.deleteAnswer(answer.getDid(), principal);

    assertFalse(answerRepository.findById(answer.getId()).isPresent());
  }

  @Test
  @DisplayName("Should fail deleting a non-existent answer")
  void testDeleteNonExistentAnswer() {
    HttpClientErrorException exception =
        assertThrows(
            HttpClientErrorException.class, () -> answerService.deleteAnswer(wrongDID, testUser));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }
}
