package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AiAbstractResponseDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.RateQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AIMaterialResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractAIResponse;
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
import ch.usi.inf.bsc.sa4.lab02spring.repository.UserRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
// Use Zonky's Embedded Database for integration testing.
// This configures a real embedded database (e.g., PostgreSQL) instead of an in-memory one like H2.
// It helps ensure better compatibility with production environments and supports transactional
// tests.
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@Transactional
@DisplayName("AbstractAiResponse Service Test")
class AIQuestionServiceTest {

  @Autowired UserRepository userRepository;

  @Autowired TopicService topicService;

  @Autowired ExerciseService exerciseService;

  @Autowired QuestionService questionService;

  @Autowired AiQuestionService aiQuestionService;

  @Autowired InstructorRepository instructorRepository;

  @Autowired StudentRepository studentRepository;

  @Autowired TopicRepository topicRepository;

  @Autowired MaterialRepository materialRepository;

  @Autowired AIMaterialResponseRepository aiMaterialResponseRepository;

  @Autowired
  AIResponseRepository aiResponseRepository;

  /**
   * There should be a Student whos highlights some text from a markdown and receives an ai
   * response.
   */

  /** User entities */
  Student newStudent;

  int nStudent = 2;
  Student[] students;
  Instructor instructor;

  /** Topics entities */
  Topic newTopic;

  Topic[] topics;

  /** Materials */
  Material newMaterial;

  Material[] materials;

  /** Responses */
  AbstractAIResponse newAbstractAIResponse;

  AbstractAIResponse[] abstractAIResponses;

  AIMaterialResponse newAIMaterialResponse;
  AIMaterialResponse[] aiMaterialResponses;

  /** Utils */
  String question1 = "What does it mean?";

  String question2 = "Why is important?";
  String question3 = "Explain the concept to me";
  int nQuestionPerStudent = 5;

  int nTopics;
  int nMaterials;
  int nAiMaterialResponses;
  int totalMaterialResponses;

  @BeforeEach
  void setUp() {
    nTopics = 2;
    nMaterials = 2;

    topics = new Topic[nTopics];
    materials = new Material[nMaterials];
    students = new Student[nStudent];

    for (int i = 0; i < nStudent; i++) {
      newStudent = new Student("Student" + i, "Tizio", "Tizio" + i + "@example.com");
      students[i] = newStudent;
      studentRepository.save(newStudent);
    }

    instructor = new Instructor("Instructor", "Caio", "Caio@example.com");
    userRepository.save(instructor);

    /** Setup materials and topics */
    for (int i = 0; i < nTopics; i++) {
      topics[i] = new Topic("Topic" + i, "The " + i + "th programming language");
      String data = "Hello, world! I'm " + i + "th programming language";

      topicRepository.save(topics[i]);

      materials[i] =
          new Material(
              "Material" + i,
              "The " + i + "th programming language material",
              data.getBytes(StandardCharsets.UTF_8),
              LocalDateTime.now(),
              topics[i],
              "md");

      materialRepository.save(materials[i]);
    }

    /** Setup fake AI material answers */
    nAiMaterialResponses = nStudent * nQuestionPerStudent;
    totalMaterialResponses = nAiMaterialResponses;

    aiMaterialResponses = new AIMaterialResponse[nAiMaterialResponses + 1];

    int c = 0;
    for (int i = 0; i < nStudent; i++) {
      for (int j = 0; j < nQuestionPerStudent; j++) {
        newAIMaterialResponse =
            new AIMaterialResponse(
                false,
                question1,
                "Fake AI answer",
                question1,
                materials[j % (nMaterials - 1)],
                students[i]);
        aiMaterialResponses[c] = newAIMaterialResponse;
        aiMaterialResponseRepository.save(aiMaterialResponses[c]);
        c++;
      }
    }

    // This material is not saved in the db on purpose in order to test the not found exceptions
    newAIMaterialResponse =
        new AIMaterialResponse(false, "Not saved response", question1, null, null, null);
    aiMaterialResponses[nAiMaterialResponses] = newAIMaterialResponse;
  }

  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Student perform on AIMaterialResponse")
  @Nested
  class AiMaterialResponseTest {

    Stream<Arguments> provideVotes() {
      return Stream.of(
              Arguments.of(0, 0, 1, false, false, false),
              Arguments.of(0, 0, 0, false, false, false),
              Arguments.of(0, 0, -1, false, false, false),
              Arguments.of(0, 1, 0, true, false, false),
              Arguments.of(nStudent * nQuestionPerStudent, 1, 0, false, true, false),
              Arguments.of(0, 0, 6, false, false, true));
    }

    @DisplayName("Single student votes one of its own private answer")
    @ParameterizedTest
    @MethodSource("provideVotes")
    public void testStudentVotesPrivateAIMaterialResponse(
        int aiMaterialResponseIndex,
        int studentIndex,
        int vote,
        boolean throwsForbidden,
        boolean throwsNotFound,
        boolean throwsBadRequest) {
      HttpClientErrorException exception = null;

      if (throwsForbidden || throwsNotFound || throwsBadRequest) {
        exception =
            assertThrows(
                HttpClientErrorException.class,
                () ->
                    aiQuestionService.rateQuestion(
                        aiMaterialResponses[aiMaterialResponseIndex].getAiResponseDID(),
                        students[studentIndex],
                        new RateQuestionDTO(vote)));
      }

      if (throwsForbidden) {
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
      } else if (throwsBadRequest) {
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
      } else if (throwsNotFound) {
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      } else {
        AiAbstractResponseDTO response =
            aiQuestionService.rateQuestion(
                aiMaterialResponses[aiMaterialResponseIndex].getAiResponseDID(),
                students[studentIndex],
                new RateQuestionDTO(vote));
        assertNotNull(response);
        assertEquals(vote, response.getRating());
        assertEquals(
            aiMaterialResponses[aiMaterialResponseIndex].getAiResponseDID(),
            response.getAiResponseDid());
      }
    }

    @DisplayName("More students votes a public and ai answer")
    @Test
    public void testStudentVotesPublicAIMaterialResponse() {
      // Make all the AI answer public
      for (int i = 0; i < nAiMaterialResponses; i++) {
        aiMaterialResponses[i].setIsPublic(true);
        aiMaterialResponseRepository.save(aiMaterialResponses[i]);
      }

      // for even number of student all the votes should be 0
      for (int i = 0; i < nStudent; i++) {
        for (int j = 0; j < nAiMaterialResponses; j++) {
          aiQuestionService.rateQuestion(
              aiMaterialResponses[j].getAiResponseDID(),
              students[i],
              new RateQuestionDTO(i % 2 == 0 ? 1 : -1));
        }
      }

      for (int i = 0; i < nAiMaterialResponses - 1; i++) {
        assertEquals(
            0,
            aiMaterialResponseRepository
                .findByAiResponseDID(aiMaterialResponses[i].getAiResponseDID())
                .orElseThrow(
                    () ->
                        new HttpClientErrorException(
                            HttpStatus.NOT_FOUND, "response doesn't exist"))
                .getRate());
      }
    }
  }
}
