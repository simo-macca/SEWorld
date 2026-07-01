package ch.usi.inf.bsc.sa4.lab02spring.service;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.*;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.*;
import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import ch.usi.inf.bsc.sa4.lab02spring.repository.*;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@Transactional
@DisplayName("AbstractQuestion Service Test")
class AbstractQuestionServiceTest {

  @Autowired UserRepository userRepository;

  @Autowired VariantRepository variantRepository;

  @Autowired TopicService topicService;

  @Autowired ExerciseService exerciseService;

  @Autowired QuestionService questionService;

  @Autowired MultiChoiceQuestionService multiChoiceQuestionService;

  @Autowired TrueFalseQuestionService trueFalseQuestionService;

  @Autowired ShortAnswerQuestionService shortAnswerQuestionService;

  // user entities
  AbstractUser newUser;
  AbstractUser[] users;

  // topic entities
  Topic newTopic;
  Topic[] topics;

  // exercise entities
  Exercise newExercise;
  Exercise[] exercises;

  Variant[] variants;

  // number of users
  int nUsers, nTopics;
  int nExercises;
  int totalNumberOfQuestionsForEx;

  // utils
  UUID wrongDID = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc");
  List<String> keys = Stream.of("key1", "key2", "key3", "key4").collect(Collectors.toList());
  TrueFalseQuestionDTO sampleTrueFalseDTO =
      new TrueFalseQuestionDTO(null, "example", null, true, "TF", null, null);
  ShortAnswerQuestionDTO sampleShortAnswerQuestionDTO =
      new ShortAnswerQuestionDTO(null, "example", null, keys.getFirst(), "SHA", null, null);
  MultiChoiceQuestionDTO sampleMultiChoiceQuestionDTO =
      new MultiChoiceQuestionDTO(null, "example", keys, null, 0, "MCH", null, null);

  @SuppressWarnings("unchecked")
  private static <T extends QuestionDTO> T latest(List<QuestionDTO> list, Class<T> type) {
    return (T) list.stream().filter(type::isInstance).reduce((f, l) -> l).orElseThrow();
  }

  @SuppressWarnings("unchecked")
  private static <T extends QuestionDTO> T byTitle(
      List<QuestionDTO> list, Class<T> type, String title) {
    return (T)
        list.stream()
            .filter(type::isInstance)
            .filter(
                dto ->
                    switch (dto) {
                      case MultiChoiceQuestionDTO mcq -> mcq.questionTitle().equals(title);
                      case ShortAnswerQuestionDTO saq -> saq.questionTitle().equals(title);
                      case TrueFalseQuestionDTO tfq -> tfq.questionTitle().equals(title);
                    })
            .findFirst()
            .orElseThrow();
  }

  @BeforeEach
  void setUp() {

    nUsers = nTopics = 4; // keep them with the same value

    users = new AbstractUser[nUsers];
    topics = new Topic[nTopics];

    int i;
    for (i = 0; i < nUsers; i++) {
      // string test variable
      String email = "AbstractUser" + (i + 1) + "@example.com";
      String name = "AbstractUser " + (i + 1);

      String subId = "Sub Id AbstractUser " + (i + 1);

      newUser = new Instructor("instr_" + name, "instr_" + email, "instr " + subId);
      userRepository.save(newUser);
      users[i] = newUser;

      String title = "Test Title " + (i + 1);
      String description = "Test Description " + (i + 1);

      // topic creation
      TopicDTO tmpTopicDTO = new TopicDTO(wrongDID, title, description);
      newTopic = topicService.createTopic(tmpTopicDTO);
      topics[i] = newTopic;
    }
    nExercises = topics.length;

    i = 0;
    exercises = new Exercise[nExercises];
    variants = new Variant[nExercises];
    for (Topic topic : topics) {
      // exercise creation
      CreateExerciseDTO tmpExerciseDTO =
          new CreateExerciseDTO(topic.getTitle(), topic.getDescription());
      var optNewExercise = exerciseService.createNewExercise(tmpExerciseDTO, topic.getDid());
      optNewExercise.ifPresent(exercise -> newExercise = exercise);
      Variant v = new Variant(0, newExercise);
      variantRepository.save(v);
      variants[i] = v;
      exercises[i++] = newExercise;
    }
    totalNumberOfQuestionsForEx = 0;
    List<Integer> nOfQuestionsForEx =
        Arrays.asList(4, 1, 0, 3); // 4 element since we have for exercises
    for (Integer ofQuestionsForEx : nOfQuestionsForEx) {
      totalNumberOfQuestionsForEx += ofQuestionsForEx;
    }

    int indexEx = 0;
    for (Exercise exercise : exercises) {
      Variant v = variants[indexEx];
      int loops = nOfQuestionsForEx.get(indexEx);
      for (int j = 0; j < loops; j++) {
        trueFalseQuestionService.save(sampleTrueFalseDTO, exercise, v);
        shortAnswerQuestionService.save(sampleShortAnswerQuestionDTO, exercise, v);
        multiChoiceQuestionService.save(sampleMultiChoiceQuestionDTO, exercise, v);
      }
      indexEx++;
    }

    System.out.println("in init:" + totalNumberOfQuestionsForEx);
  }

  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("When creating a new AbstractQuestion of type:")
  @Nested
  class createNewAbstractQuestionOfTypeTest {

    Stream<org.junit.jupiter.params.provider.Arguments> provideMultiChoiceQuestions() {
      List<Arguments> arguments = new ArrayList<>();
      // valid arguments
      int i = 0;
      List<String> choices1 = new ArrayList<>(List.of("A language", "A drink", "A car"));
      List<String> choices2 = new ArrayList<>(List.of("3", "4", "5"));
      List<String> choices3 = new ArrayList<>(List.of("Windows", "Mac", "Linux"));
      for (Exercise ignored : exercises) {
        List<Arguments> ith =
            Stream.of(
                    org.junit.jupiter.params.provider.Arguments.of(
                        i, "Ex " + i + " What is Java?", choices1, 0, true),
                    org.junit.jupiter.params.provider.Arguments.of(
                        i, "Ex " + i + " What is 2 + 2?", choices2, 1, true),
                    org.junit.jupiter.params.provider.Arguments.of(
                        i, "Ex " + i + " Best OS?", choices3, 2, true))
                .toList();
        arguments.addAll(ith);
        i++;
      }
      // invalid arguments
      List<Arguments> ith =
          Stream.of(
                  org.junit.jupiter.params.provider.Arguments.of(
                      0, "Ex " + i + " What is Java?", new ArrayList<>(), 0, false),
                  org.junit.jupiter.params.provider.Arguments.of(
                      0, "Ex " + i + " What is 2 + 2?", choices2, 7, false),
                  org.junit.jupiter.params.provider.Arguments.of(
                      0, "Ex " + i + " Best OS?", choices3, -1, false))
              .toList();
      arguments.addAll(ith);

      return arguments.stream();
    }

    @DisplayName("Multi choice")
    @ParameterizedTest
    @MethodSource("provideMultiChoiceQuestions")
    void testMultiChoiceQuestionCreation(
        int index,
        String questionTitle,
        List<String> choices,
        int correctAnswerIndex,
        boolean shouldExist) {
      MultiChoiceQuestionDTO dto =
          new MultiChoiceQuestionDTO(
              null,
              questionTitle,
              choices,
              null,
              correctAnswerIndex,
              "MCH",
              variants[index].getVariantDid(),
              variants[index].getIdx());
      if (shouldExist) {
        multiChoiceQuestionService.save(dto, exercises[index], variants[index]);
        MultiChoiceQuestionDTO saved =
            byTitle(
                questionService.getAll(exercises[index].getExerciseDid(), true),
                MultiChoiceQuestionDTO.class,
                questionTitle);
        assertNotNull(saved);
        assertEquals(questionTitle, saved.questionTitle());
        assertEquals(choices, saved.choices());
        assertEquals(correctAnswerIndex, saved.correctAnswer());
      } else {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> multiChoiceQuestionService.save(dto, exercises[0], variants[0]));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
      }
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> provideShortAnswerQuestions() {
      // valid arguments
      int i = 0;
      String keys1 = "A language";
      String keys2 = "3";
      String keys3 = "Windows";
      // valid arguments
      List<Arguments> ith =
          Stream.of(
                  org.junit.jupiter.params.provider.Arguments.of(
                      i, "Ex " + i + " What is Java?", keys1),
                  org.junit.jupiter.params.provider.Arguments.of(
                      i, "Ex " + i + " What is 2 + 2?", keys2),
                  org.junit.jupiter.params.provider.Arguments.of(i, "Ex " + i + " Best OS?", keys3))
              .toList();
      List<Arguments> arguments = new ArrayList<>(ith);
      return arguments.stream();
    }

    @DisplayName("Short Answer")
    @ParameterizedTest
    @MethodSource("provideShortAnswerQuestions")
    public void testCreateShortAnswer(int index, String questionTitle, String keys) {
      ShortAnswerQuestionDTO dto =
          new ShortAnswerQuestionDTO(null, questionTitle, null, keys, "SHA", null, null);
      shortAnswerQuestionService.save(dto, exercises[index], variants[index]);
      ShortAnswerQuestionDTO saved =
          byTitle(
              questionService.getAll(exercises[index].getExerciseDid(), true),
              ShortAnswerQuestionDTO.class,
              questionTitle);
      assertNotNull(saved);
      assertEquals(questionTitle, saved.questionTitle());
      assertEquals(keys, saved.correctAnswer());
    }

    static Stream<Arguments> provideTrueFalseQuestions() {
      List<Arguments> tfQuestions =
          Stream.of(
                  org.junit.jupiter.params.provider.Arguments.of(
                      0, "Ex " + " Do you even know algo", true),
                  org.junit.jupiter.params.provider.Arguments.of(
                      0, "Ex " + " Are you the goat?", true),
                  org.junit.jupiter.params.provider.Arguments.of(
                      0, "Ex " + " Do you suck at programming ?", false))
              .toList();
      return tfQuestions.stream();
    }

    @DisplayName("True False")
    @ParameterizedTest
    @MethodSource("provideTrueFalseQuestions")
    public void testCreateTrueFalse(int exerciseIndex, String questionTitle, boolean answer) {
      TrueFalseQuestionDTO dto =
          new TrueFalseQuestionDTO(null, questionTitle, null, answer, "TF", null, null);
      trueFalseQuestionService.save(dto, exercises[exerciseIndex], variants[exerciseIndex]);
      TrueFalseQuestionDTO saved =
          byTitle(
              questionService.getAll(exercises[exerciseIndex].getExerciseDid(), true),
              TrueFalseQuestionDTO.class,
              questionTitle);
      assertNotNull(saved);
      assertEquals(questionTitle, saved.questionTitle());
      assertEquals(answer, saved.correctAnswer());
    }
  }

  @DisplayName("Information handling")
  @Nested
  class InformationHandling {

    @DisplayName("Get all question")
    @Test
    public void testGetAllQuestion() {
      for (Exercise exercise : exercises) {
        List<QuestionDTO> dbList = questionService.getAll(exercise.getExerciseDid(), true);
        List<QuestionDTO> copy = new ArrayList<>(dbList);
        assertEquals(new HashSet<>(dbList), new HashSet<>(copy));
      }
    }
  }

  @DisplayName("When updating a AbstractQuestion of type:")
  @Nested
  class updateNewAbstractQuestionOfTypeTest {

    static Stream<Arguments> provideUpdateMultiChoice() {
      List<String> singleChoice = new ArrayList<>(List.of("single answer"));
      List<String> multiChoice = new ArrayList<>(List.of("A", "B", "C"));
      return Stream.of(
          Arguments.of(null, null, null, 0, 0, true),
          Arguments.of(null, singleChoice, 0, 0, 0, true),
          Arguments.arguments(null, multiChoice, null, 0, 0, true),
          Arguments.arguments("New Title", null, 1, 0, 0, true),
          Arguments.arguments(null, null, 2, 0, 0, true),
          Arguments.arguments(null, singleChoice, 2, 0, 0, false),
          Arguments.arguments(null, singleChoice, -2, 0, 0, false));
    }

    @DisplayName("Multi choice")
    @ParameterizedTest
    @MethodSource("provideUpdateMultiChoice")
    public void testUpdateQuestionOfTypeMultiChoice(
        String newQuestionTitle,
        List<String> newChoices,
        Integer newCorrectAnswer,
        Integer indexEx,
        Integer question_idx,
        boolean result) {
      Exercise exercise = exercises[indexEx];
      MultiChoiceQuestionDTO oldQuestion =
          latest(
              questionService.getAll(exercises[indexEx].getExerciseDid(), true),
              MultiChoiceQuestionDTO.class);
      Variant v = variants[indexEx];
      MultiChoiceQuestion updated_question =
          new MultiChoiceQuestion(
              newQuestionTitle == null ? oldQuestion.questionTitle() : newQuestionTitle,
              newChoices == null ? oldQuestion.choices() : newChoices,
              newCorrectAnswer == null ? oldQuestion.correctAnswer() : newCorrectAnswer,
              exercises[indexEx],
              v);

      updated_question.setQuestionDid(oldQuestion.questionDid());

      MultiChoiceQuestionDTO dtoTOUpdate =
          new MultiChoiceQuestionDTO(
              oldQuestion.questionDid(),
              newQuestionTitle,
              newChoices,
              exercise.getExerciseDid(),
              newCorrectAnswer,
              "MCH",
              variants[indexEx].getVariantDid(),
              variants[indexEx].getIdx());

      if (result) {
        MultiChoiceQuestionDTO changed =
            multiChoiceQuestionService.update(dtoTOUpdate, oldQuestion.questionDid());
        assertEquals(changed, updated_question.convertToDTO(true));
      } else {
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> multiChoiceQuestionService.update(dtoTOUpdate, oldQuestion.questionDid()));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
      }
    }

    static Stream<Arguments> provideUpdateShortAnswer() {
      String singleKeyword = "single key";
      String multiKeyword = "key 1";
      return Stream.of(
          Arguments.arguments(null, null, 0, 0, true),
          Arguments.arguments(null, singleKeyword, 0, 0, true),
          Arguments.arguments("new Title", multiKeyword, 0, 0, false));
    }

    @DisplayName("Short Answer")
    @ParameterizedTest
    @MethodSource("provideUpdateShortAnswer")
    public void testUpdateQuestionOfTypeShortAnswer(
        String title, String keyWords, int indexEx, int indexQuestion, boolean result) {
      Exercise exercise = exercises[indexEx];
      Variant v = variants[indexEx];
      ShortAnswerQuestionDTO oldQuestion =
          latest(
              questionService.getAll(exercises[indexEx].getExerciseDid(), true),
              ShortAnswerQuestionDTO.class);
      ShortAnswerQuestion updatedQuestion =
          new ShortAnswerQuestion(
              title == null ? oldQuestion.questionTitle() : title,
              exercise,
              keyWords == null ? oldQuestion.correctAnswer() : keyWords,
              v);
      updatedQuestion.setQuestionDid(oldQuestion.questionDid());

      ShortAnswerQuestionDTO dtoToUpdate =
          new ShortAnswerQuestionDTO(
              oldQuestion.questionDid(),
              title,
              exercise.getExerciseDid(),
              keyWords,
              "SHA",
              variants[indexEx].getVariantDid(),
              variants[indexEx].getIdx());

      if (result) {
        ShortAnswerQuestionDTO changed =
            shortAnswerQuestionService.update(dtoToUpdate, oldQuestion.questionDid());
        assertEquals(changed, updatedQuestion.convertToDTO(true));
      } else {
        exercises[indexEx].setExerciseIsDraft(false);
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> shortAnswerQuestionService.update(dtoToUpdate, oldQuestion.questionDid()));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
      }
    }

    static Stream<Arguments> provideUpdateTrueFalse() {
      return Stream.of(
          Arguments.arguments(null, null, 0, 0, true),
          Arguments.arguments(null, false, 0, 0, true));
    }

    @DisplayName("True False")
    @ParameterizedTest
    @MethodSource("provideUpdateTrueFalse")
    public void testUpdateQuestionOfTypeTrueFalse(
        String title, Boolean newAnswer, Integer indexEx, Integer question_idx, boolean result) {
      Exercise exercise = exercises[indexEx];
      Variant v = variants[indexEx];
      TrueFalseQuestionDTO oldQuestion =
          latest(
              questionService.getAll(exercises[indexEx].getExerciseDid(), true),
              TrueFalseQuestionDTO.class);
      TrueFalseQuestion updatedQuestion =
          new TrueFalseQuestion(
              title == null ? oldQuestion.questionTitle() : title,
              exercise,
              newAnswer == null ? oldQuestion.correctAnswer() : newAnswer,
              v);
      updatedQuestion.setQuestionDid(oldQuestion.questionDid());
      TrueFalseQuestionDTO dtoToUpdate =
          new TrueFalseQuestionDTO(
              oldQuestion.questionDid(),
              title,
              exercise.getExerciseDid(),
              newAnswer,
              "TF",
              variants[indexEx].getVariantDid(),
              variants[indexEx].getIdx());
      if (result) {
        TrueFalseQuestionDTO changed =
            trueFalseQuestionService.update(dtoToUpdate, oldQuestion.questionDid());
        assertEquals(changed, updatedQuestion.convertToDTO(true));
      }
    }
  }
}
