package ch.usi.inf.bsc.sa4.lab02spring.service;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.*;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.*;
import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import ch.usi.inf.bsc.sa4.lab02spring.repository.*;
import ch.usi.inf.bsc.sa4.lab02spring.utils.JwtTestUtil;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@Transactional
@DisplayName("Statistic Service Test")
class StatisticsServiceTest {

  @Autowired private UserService userService;

  @Autowired private StudentRepository studentRepository;

  @Autowired private TopicRepository topicRepository;

  @Autowired private AttemptRepository attemptRepository;

  @Autowired private ExerciseRepository exerciseRepository;

  @Autowired private FeedbackRepository feedbackRepository;

  @Autowired private StatisticsService statisticsService;

  List<AbstractUser> students = new ArrayList<>();

  AbstractUser emptyStudent;

  Jwt instructorJwt = JwtTestUtil.createInstructorJwt();
  AbstractUser instructor;

  List<Topic> topics = new ArrayList<>();

  List<Exercise> exercises = new ArrayList<>();

  List<Attempt> attempts = new ArrayList<>();

  List<Feedback> feedbacks = new ArrayList<>();

  List<Integer> total = new ArrayList<>();
  List<Integer> correct = new ArrayList<>();
  List<Double> grades = new ArrayList<>();
  List<Integer> nAttempt = List.of(0, 4, 1, 2, 5);

  UUID wrongDID = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc");

  private static Feedback createFeedback(
      int correct, int total, double percentage, Attempt attempt) {

    attempt.setAttemptIsSubmitted(true);
    if (percentage >= 60) {
      attempt.setAttemptIsCompleted(true);
    }

    List<Map<String, Object>> data =
        IntStream.range(0, total)
            .mapToObj(
                i -> {
                  boolean isCorrect = i < correct;
                  return Map.<String, Object>of(
                      "isCorrect",
                      isCorrect,
                      "questionDid",
                      "d9d8cba1-a145-4fe4-a1fe-6741f113246f",
                      "correctAnswer",
                      true);
                })
            .toList();

    List<Object> d = new ArrayList<>(data);

    return new Feedback(correct, total, percentage, d, attempt);
  }

  @BeforeEach
  void setUp() {
    students =
        IntStream.rangeClosed(1, 5)
            .mapToObj(
                i ->
                    JwtTestUtil.createStudentJwt(
                        "student1-sub" + i, "student1" + i + "@example.com", "student1 " + i))
            .map(userService::findOrCreateUser)
            .peek(students::add)
            .toList();

    emptyStudent =
        userService.findOrCreateUser(
            JwtTestUtil.createStudentJwt(
                "empty-student1-sub1", "emptystudent@example.com", "student1 empty 1"));
    instructor = userService.findOrCreateUser(instructorJwt);

    topics =
        IntStream.rangeClosed(1, 5)
            .mapToObj(i -> new Topic("Topic: " + i, "Desc: " + i))
            .peek(topicRepository::save)
            .peek(topics::add)
            .toList();

    exercises =
        topics.stream()
            .map(
                t -> {
                  Exercise ex =
                      new Exercise("Exercise for: " + t.getTitle(), t.getDescription(), t);
                  ex.setExerciseIsDraft(false);
                  t.setExercises(List.of(ex));
                  exerciseRepository.save(ex);
                  return ex;
                })
            .peek(exercises::add)
            .toList();

    attempts =
        IntStream.range(0, students.size())
            .mapToObj(
                idx -> {
                  AbstractUser user = students.get(idx);
                  return IntStream.range(0, nAttempt.get(idx))
                      .mapToObj(
                          j -> {
                            Exercise ex = exercises.get(j);
                            Attempt at = new Attempt(user, ex, Collections.emptyList());
                            attemptRepository.save(at);
                            return at;
                          })
                      .peek(a -> exercises.get(idx).getAttempts().add(a))
                      .toList();
                })
            .flatMap(List::stream)
            .peek(attempts::add)
            .toList();

    total = List.of(5, 7, 10, 4, 8, 6, 2, 8, 4, 5, 10, 11);
    correct = List.of(1, 2, 3, 4, 5, 1, 2, 4, 1, 2, 3, 9);
    grades =
        IntStream.range(0, total.size())
            .mapToDouble(idx -> (double) correct.get(idx) / total.get(idx) * 100)
            .boxed()
            .toList();
    IntStream.range(0, attempts.size())
        .mapToObj(
            i -> {
              Attempt at = attempts.get(i);
              int tot = total.get(i);
              int corr = correct.get(i);
              double pct = (double) corr / tot * 100;
              Feedback fb = createFeedback(corr, tot, pct, at);
              at.setFeedbacks(List.of(fb));
              feedbackRepository.save(fb);
              return fb;
            })
        .forEach(feedbacks::add);
  }

  @DisplayName("Get Statistic for Student")
  @Nested
  class GetStatisticForStudent {

    @DisplayName("Get the student1-specific statistics overview")
    @Test
    void getStatisticOverview() {
      List<Double> mins = List.of(0.0, 20.0, 62.5, 16.67, 25.0);
      List<Double> avgs = List.of(0.0, 44.64, 62.5, 58.33, 45.36);
      List<Double> maxes = List.of(0.0, 100.0, 62.5, 100.0, 81.82);
      List<Double> allAvgs = List.of(37.29, 51.19, 35.0, 65.0, 81.82);

      for (int studentIdx = 0, idx = 0; studentIdx < students.size(); studentIdx++) {
        AbstractUser student = students.get(studentIdx);
        int attemptsCount = nAttempt.get(studentIdx);

        List<TopicStatisticDTO> topicStats =
            createTopicStats(topics, correct, total, allAvgs, idx, attemptsCount);
        idx += attemptsCount;

        StatisticsStudentDTO expected =
            new StatisticsStudentDTO(
                mins.get(studentIdx), avgs.get(studentIdx), maxes.get(studentIdx), topicStats);

        StatisticsStudentDTO actual = statisticsService.getStudentView(student);

        assertEquals(expected, actual);
      }
    }

    private List<TopicStatisticDTO> createTopicStats(
        List<Topic> topics,
        List<Integer> correct,
        List<Integer> total,
        List<Double> allAvgs,
        int startIdx,
        int count) {

      return IntStream.range(0, count)
          .mapToObj(
              i -> {
                Topic t = topics.get(i);
                int idx = startIdx + i;
                double vote = Math.round(correct.get(idx) * 100.0 / total.get(idx) * 100) / 100.0;
                double pass = vote >= 60.0 ? 100.0 : 0.0;
                double classAvg = allAvgs.get(i);

                return new TopicStatisticDTO(t.getDid(), t.getTitle(), classAvg, vote, pass);
              })
          .toList();
    }

    @DisplayName("Get a student1’s per-exercise statistics under a specific topic")
    @Nested
    class GetStatisticForExercise {

      @DisplayName("Get a student1’s per-exercise statistics by topic")
      @Test
      void getStatisticByTopic() {
        int studentIdx = 0;
        int g = 0;
        for (AbstractUser u : students) {
          for (int i = 0; i < nAttempt.get(studentIdx); i++) {
            TopicExercisesStatsDTO statsDTO =
                statisticsService.getExerciseStudentView(topics.get(i).getDid(), u);

            double grade = grades.get(g++);
            boolean isSuccess = grade >= 60;
            List<StudentExercisesStatsDTO> dtoList =
                List.of(
                    new StudentExercisesStatsDTO(
                        exercises.get(i).getExerciseDid(),
                        exercises.get(i).getExerciseTitle(),
                        isSuccess,
                        grade));
            TopicExercisesStatsDTO myStatsDTO = new TopicExercisesStatsDTO(dtoList);

            assertEquals(myStatsDTO, statsDTO);
          }
          studentIdx++;
        }
      }

      @DisplayName("Get a student1’s per-exercise statistics by wrong topic")
      @Test
      void getStatisticByWrongTopic() {
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () -> statisticsService.getExerciseStudentView(wrongDID, students.getFirst()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 Topic not found", exception.getMessage());
      }

      @DisplayName("Get a student1’s per-wrong-exercise statistics by topic")
      @Test
      void getStatisticByWrongExercise() {
        attemptRepository.deleteAll();
        exerciseRepository.deleteAll();
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () ->
                    statisticsService.getExerciseStudentView(
                        topics.getFirst().getDid(), students.getFirst()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 Exercise not found", exception.getMessage());
      }

      @DisplayName("Get a empty student1’s per-exercise statistics by topic")
      @Test
      void getStatisticByEmptyTopic() {
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () ->
                    statisticsService.getExerciseStudentView(
                        topics.getFirst().getDid(), emptyStudent));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 No statistics available", exception.getMessage());
      }
    }

    @DisplayName("Get a student1’s grade statistics for a single exercise")
    @Nested
    class GetStatisticForGrade {

      @DisplayName("Get the grade by student1 for a single exercise")
      @Test
      void getStatisticUnderExercise() {
        int i = 0;
        for (Attempt a : attempts) {
          StudentGradeDTO grade =
              statisticsService.getSingleExerciseStudentStats(
                  a.getExercise().getExerciseDid(), a.getUser());
          double v = ((double) correct.get(i) / total.get(i)) * 100;
          StudentGradeDTO myGrade = new StudentGradeDTO(v);
          assertEquals(myGrade, grade);
          ++i;
        }
      }

      @DisplayName("Get the grade by student1 for a wrong exercise")
      @Test
      void getStatisticUnderWrongExercise() {
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () ->
                    statisticsService.getSingleExerciseStudentStats(wrongDID, students.getFirst()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 No exercise found", exception.getMessage());
      }
    }

    @DisplayName("Get the grade by student1 for a draft exercise")
    @Test
    void getStatisticUnderDraft() {
      Exercise e = exercises.getFirst();
      e.setExerciseIsDraft(true);
      HttpClientErrorException exception =
          assertThrowsExactly(
              HttpClientErrorException.class,
              () ->
                  statisticsService.getSingleExerciseStudentStats(
                      e.getExerciseDid(), students.getFirst()));

      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
      assertEquals(
          "400 The exercise is draft, you cannot retrieve the draft statistics",
          exception.getMessage());
    }

    @DisplayName("Get the grade without having exercises")
    @Test
    void getStatisticWithoutExercises() {
      HttpClientErrorException exception =
          assertThrowsExactly(
              HttpClientErrorException.class,
              () ->
                  statisticsService.getSingleExerciseStudentStats(
                      exercises.getLast().getExerciseDid(), students.getFirst()));

      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      assertEquals("404 No statistics available", exception.getMessage());
    }
  }

  @DisplayName("Get Statistic for Instructor")
  @Nested
  class GetStatisticForInstructor {

    @DisplayName("Get the global instructor overview across all topics")
    @Nested
    class GetStatisticForInstructorOverview {

      @DisplayName("Get a instructor statistics by all topics")
      @Test
      void getInstructorViewStatistic() {
        List<Double> expGrades = List.of(37.29, 51.19, 35.0, 65.0, 81.82);

        List<TopicStatisticInstrDTO> expectedList =
            IntStream.range(0, topics.size())
                .mapToObj(
                    idx -> {
                      Topic t = topics.get(idx);
                      return new TopicStatisticInstrDTO(
                          t.getDid(), t.getTitle(), expGrades.get(idx));
                    })
                .toList();
        StatisticsInstructorDTO expected = new StatisticsInstructorDTO(expectedList);
        System.out.println("response Test: " + expected);

        StatisticsInstructorDTO actual = statisticsService.getInstructorView();
        System.out.println("response Test: " + actual);
        assertEquals(expected, actual);
      }

      @DisplayName("Get empty instructor statistics by all topics")
      @Test
      void getInstructorViewEmptyStatistic() {
        feedbackRepository.deleteAll();
        attemptRepository.deleteAll();
        exerciseRepository.deleteAll();
        topicRepository.deleteAll();

        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class, () -> statisticsService.getInstructorView());

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 No statistics available", exception.getMessage());
      }
    }

    @DisplayName("Get the instructor view for exercise statistics by topic")
    @Nested
    class GetStatisticForInstructorExerciseOverview {

      @DisplayName("Get exercise statistic by topic")
      @Test
      void getStatisticForExerciseOverview() {
        List<Double> expGrades = List.of(37.29, 51.19, 35.0, 65.0, 81.82);
        int i = 0;
        for (Exercise e : exercises) {
          TopicExercisesStatsDTO actual =
              statisticsService.getExerciseInstructorView(e.getTopic().getDid());
          System.out.println(actual);
          TopicExercisesStatsDTO expected =
              new TopicExercisesStatsDTO(
                  List.of(
                      new InstructorExerciseStatsDTO(
                          e.getExerciseDid(), e.getExerciseTitle(), expGrades.get(i++))));
          assertEquals(expected, actual);
        }
      }

      @DisplayName("Get exercise statistic by wrong topic")
      @Test
      void getStatisticForWrongTopic() {
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () -> statisticsService.getExerciseInstructorView(wrongDID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 Topic not found", exception.getMessage());
      }

      @DisplayName("Get wrong exercise statistic by topic")
      @Test
      void getStatisticForWrongExercise() {
        attemptRepository.deleteAll();
        exerciseRepository.deleteAll();

        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () -> statisticsService.getExerciseInstructorView(topics.getFirst().getDid()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 Exercises not found", exception.getMessage());
      }

      @DisplayName("Get exercise statistic by topic for no user")
      @Test
      void getStatisticForEmptyUser() {
        attemptRepository.deleteAll();
        studentRepository.deleteAll();

        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () -> statisticsService.getExerciseInstructorView(topics.getFirst().getDid()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 Students not found", exception.getMessage());
      }

      @DisplayName("Get empty exercise statistic by topic")
      @Test
      void getStatisticForEmptyExercise() {
        feedbackRepository.deleteAll();

        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () -> statisticsService.getExerciseInstructorView(topics.getFirst().getDid()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 No statistics available", exception.getMessage());
      }
    }

    @DisplayName("Get instructor statistics for a single exercise")
    @Nested
    class GetStatisticForInstructorSingleExerciseOverview {

      @DisplayName("Get detailed instructor statistics by exercise")
      @Test
      void getStatisticForExerciseOverview() {
        int i = 0;
        List<Long> howManyAttempt = List.of(4L, 3L, 2L, 2L, 1L);
        List<Double> allAvgs = List.of(37.29, 51.19, 35.0, 65.0, 81.82);

        for (Exercise e : exercises) {
          ExerciseStatsDTO expected =
              new ExerciseStatsDTO(
                  e.getExerciseTitle(), allAvgs.get(i), howManyAttempt.get(i++), List.of());
          ExerciseStatsDTO actual =
              statisticsService.getSingleExerciseInstructorStats(e.getExerciseDid());

          assertEquals(expected, actual);
        }
      }

      @DisplayName("Get detailed instructor statistics by wrong exercise")
      @Test
      void getStatisticForWrongExercise() {
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () -> statisticsService.getSingleExerciseInstructorStats(wrongDID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 Exercise not found: " + wrongDID, exception.getMessage());
      }
    }
  }
}
