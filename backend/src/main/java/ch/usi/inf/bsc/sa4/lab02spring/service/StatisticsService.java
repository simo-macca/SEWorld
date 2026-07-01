package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.*;
import ch.usi.inf.bsc.sa4.lab02spring.model.*;
import ch.usi.inf.bsc.sa4.lab02spring.repository.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Service for retrieving and processing statistical data related to exercises and topics.
 *
 * <p>Provides both student- and instructor-focused views of statistics, including per-topic
 * aggregates, per-exercise details, and overall metrics. Leverages the {@link FeedbackRepository}
 * for raw feedback data and the {@link TopicService} for topic lookups.
 */
@Service
public class StatisticsService {
  /** Message used when no statistics can be found. */
  private static final String NO_STATISTICS = "No statistics available";

  private final StudentRepository studentRepository;

  /** Repository for retrieving {@link Topic} entities. */
  private final TopicRepository topicRepository;

  /** Repository for retrieving {@link Exercise} entities. */
  private final ExerciseRepository exerciseRepository;

  /** Repository for retrieving {@link Attempt} entities. */
  private final AttemptRepository attemptRepository;

  /** Repository for retrieving {@link AbstractQuestion} entities. */
  private final QuestionRepository questionRepository;

  /** Repository for retrieving {@link Feedback} entities. */
  private final FeedbackRepository feedbackRepository;

  /**
   * Constructs a new {@code StatisticsService} with injected dependencies.
   *
   * @param feedbackRepository repository to fetch feedback records
   * @param exerciseRepository repository to fetch exercise metadata
   * @param attemptRepository repository to fetch attempt records
   * @param questionRepository repository to fetch question definitions
   * @param topicRepository repository to fetch topic entities
   * @param studentRepository repository to fetch student entities
   */
  @Autowired
  public StatisticsService(
      StudentRepository studentRepository,
      TopicRepository topicRepository,
      ExerciseRepository exerciseRepository,
      AttemptRepository attemptRepository,
      QuestionRepository questionRepository,
      FeedbackRepository feedbackRepository) {
    this.feedbackRepository = feedbackRepository;
    this.exerciseRepository = exerciseRepository;
    this.attemptRepository = attemptRepository;
    this.questionRepository = questionRepository;
    this.topicRepository = topicRepository;
    this.studentRepository = studentRepository;
  }

  /**
   * Retrieves the student-specific statistics overview.
   *
   * <p>Computes, for each topic:
   *
   * <ul>
   *   <li>General average percentage across all users
   *   <li>Average of the student’s best attempts
   *   <li>Percentage of completed (≥60%) attempts
   * </ul>
   *
   * Then aggregates the student’s overall min, average, and max percentages.
   *
   * @param student the {@link AbstractUser} representing the student
   * @return a {@link StatisticsStudentDTO} with per-topic and overall student metrics
   * @throws HttpClientErrorException if there is an error retrieving statistics
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public StatisticsStudentDTO getStudentView(AbstractUser student) {
    final List<Topic> topics = topicRepository.findAll();

    final List<TopicStatisticDTO> topicStatisticDTOS =
        topics.stream()
            .map(
                topic -> {
                  final List<Feedback> bestFeedbacks =
                      feedbackRepository
                          .findAllByAttempt_UserAndAttempt_Exercise_TopicOrderByPercentageDesc(
                              student, topic);

                  if (bestFeedbacks.isEmpty()) {
                    return null;
                  }

                  final double avgUsersGrade =
                      Math.round(
                              feedbackRepository.generalAvgPercentageByTopicId(topic.getId()) * 100)
                          / 100.0;

                  double userGrade =
                      bestFeedbacks.stream()
                          .mapToDouble(Feedback::getPercentage)
                          .average()
                          .orElse(0.0);

                  userGrade = Math.round(userGrade * 100) / 100.0;

                  final long countCompleted =
                      bestFeedbacks.stream().filter(fb -> fb.getPercentage() >= 60).count();

                  final double completionStage = (countCompleted * 100.0) / bestFeedbacks.size();

                  return new TopicStatisticDTO(
                      topic.getDid(), topic.getTitle(), avgUsersGrade, userGrade, completionStage);
                })
            .filter(Objects::nonNull)
            .toList();

    final List<Feedback> allFeedbacks = feedbackRepository.findAllByAttempt_User(student);

    double max = allFeedbacks.stream().mapToDouble(Feedback::getPercentage).max().orElse(0.0);
    max = Math.round(max * 100.0) / 100.0;

    double min = allFeedbacks.stream().mapToDouble(Feedback::getPercentage).min().orElse(0.0);
    min = Math.round(min * 100.0) / 100.0;

    double avg = feedbackRepository.getAverageMaxFeedbackPercentage(student);
    avg = Math.round(avg * 100.0) / 100.0;

    return new StatisticsStudentDTO(min, avg, max, topicStatisticDTOS);
  }

  /**
   * Retrieves a student’s per-exercise statistics under a specific topic.
   *
   * <p>Throws 404 if any exercise statistic lacks a user grade.
   *
   * @param topicDid the UUID of the topic
   * @param user the {@link AbstractUser} student
   * @return a {@link TopicExercisesStatsDTO} with exercise-level student stats
   * @throws HttpClientErrorException with status NOT_FOUND if data invalid
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public TopicExercisesStatsDTO getExerciseStudentView(UUID topicDid, AbstractUser user) {
    final Topic topic =
        topicRepository
            .findByTopicDid(topicDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Topic not found"));

    final List<Exercise> exercises =
        exerciseRepository
            .findByTopic(topic)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found"));

    if (exercises.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercise not found");
    }

    final List<StudentExercisesStatsDTO> stats =
        exercises.stream()
            .map(
                exercise ->
                    feedbackRepository
                        .findTopByAttempt_UserAndAttempt_ExerciseAndAttempt_Exercise_ExerciseIsDraftOrderByPercentageDesc(
                            user, exercise, false)
                        .map(
                            feedback ->
                                new StudentExercisesStatsDTO(
                                    exercise.getExerciseDid(),
                                    exercise.getExerciseTitle(),
                                    feedback.getAttempt().isAttemptIsCompleted(),
                                    feedback.getPercentage())))
            .flatMap(Optional::stream)
            .toList();

    if (stats.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, NO_STATISTICS);
    }

    return new TopicExercisesStatsDTO(stats);
  }

  /**
   * Retrieves a student’s grade statistics for a single exercise.
   *
   * @param exerciseDid the UUID of the exercise
   * @param user the {@link AbstractUser} student
   * @return a {@link StudentGradeDTO} containing the student’s highest percentage
   * @throws HttpClientErrorException if no feedback is found for the user’s attempts
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public StudentGradeDTO getSingleExerciseStudentStats(UUID exerciseDid, AbstractUser user) {
    final Exercise e =
        exerciseRepository
            .findByExerciseDid(exerciseDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "No exercise found"));

    if (e.isExerciseIsDraft()) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST,
          "The exercise is draft, you cannot retrieve the draft statistics");
    }

    return new StudentGradeDTO(
        feedbackRepository
            .findTopByAttempt_UserAndAttempt_ExerciseAndAttempt_Exercise_ExerciseIsDraftOrderByPercentageDesc(
                user, e, e.isExerciseIsDraft())
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, NO_STATISTICS))
            .getPercentage());
  }

  /**
   * Retrieves the global instructor overview across all topics.
   *
   * <p>For each topic, retrieves the instructor’s aggregated average percentages. Throws 404 if any
   * topic’s stat is incomplete.
   *
   * @return a {@link StatisticsInstructorDTO} with per-topic instructor metrics
   * @throws HttpClientErrorException with status NOT_FOUND if incomplete data
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public StatisticsInstructorDTO getInstructorView() {
    final List<Topic> topics = topicRepository.findAll();

    final List<Student> students = studentRepository.findAll();

    final List<TopicStatisticInstrDTO> stats =
        topics.stream()
            .map(
                topic -> {
                  double rawAvg =
                      students.stream()
                          .map(
                              student ->
                                  feedbackRepository
                                      .findTopByAttempt_UserAndAttempt_Exercise_TopicOrderByPercentageDesc(
                                          student, topic))
                          .flatMap(Optional::stream)
                          .mapToDouble(Feedback::getPercentage)
                          .average()
                          .orElse(Double.NaN);

                  double avgBest = rawAvg;
                  if (!Double.isNaN(rawAvg)) {
                    avgBest = Math.round(avgBest * 100) / 100.0;
                  }

                  return new TopicStatisticInstrDTO(topic.getDid(), topic.getTitle(), avgBest);
                })
            .filter(dto -> !Double.isNaN(dto.avgUsersGrade()))
            .toList();

    if (stats.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, NO_STATISTICS);
    }

    return new StatisticsInstructorDTO(stats);
  }

  /**
   * Retrieves the instructor view of exercise statistics for a given topic.
   *
   * <p>Fetches each exercise’s average student grade under the specified topic. Throws 404 if any
   * average is missing.
   *
   * @param topicDid the UUID of the topic
   * @return a {@link TopicExercisesStatsDTO} with per-exercise averages
   * @throws HttpClientErrorException with status NOT_FOUND if no valid stats
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public TopicExercisesStatsDTO getExerciseInstructorView(UUID topicDid) {
    final Topic topic =
        topicRepository
            .findByTopicDid(topicDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Topic not found"));

    final List<Exercise> exercises =
        exerciseRepository
            .findByTopic(topic)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercises not found"));

    if (exercises.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Exercises not found");
    }

    final List<Student> students = studentRepository.findAll();

    if (students.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Students not found");
    }

    final List<InstructorExerciseStatsDTO> stats =
        exercises.stream()
            .map(
                exercise -> {
                  final double rawAvg =
                      students.stream()
                          .map(
                              student ->
                                  feedbackRepository
                                      .findTopByAttempt_UserAndAttempt_ExerciseAndAttempt_Exercise_ExerciseIsDraftOrderByPercentageDesc(
                                          student, exercise, false))
                          .flatMap(Optional::stream)
                          .mapToDouble(Feedback::getPercentage)
                          .average()
                          .orElse(Double.NaN);

                  double avgBest = rawAvg;
                  if (!Double.isNaN(rawAvg)) {
                    avgBest = Math.round(avgBest * 100) / 100.0;
                  }

                  return new InstructorExerciseStatsDTO(
                      exercise.getExerciseDid(), exercise.getExerciseTitle(), avgBest);
                })
            .filter(dto -> !Double.isNaN(dto.getAvgUsersGrade()))
            .toList();

    if (stats.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, NO_STATISTICS);
    }

    return new TopicExercisesStatsDTO(stats);
  }

  /**
   * Retrieves detailed instructor statistics for a single exercise.
   *
   * <p>Includes per-question wrong-answer counts, attempt counts, average, and number of distinct
   * students. Throws 404 if exercise not found or stats incomplete.
   *
   * @param exerciseDid the UUID of the exercise
   * @return an {@link ExerciseStatsDTO} with detailed exercise metrics
   * @throws HttpClientErrorException with status NOT_FOUND on missing data
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public ExerciseStatsDTO getSingleExerciseInstructorStats(UUID exerciseDid) {
    final Exercise exercise =
        exerciseRepository
            .findByExerciseDid(exerciseDid)
            .orElseThrow(
                () ->
                    new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, "Exercise not found: " + exerciseDid));

    final List<Feedback> feedbacks =
        feedbackRepository.findBestFeedbackPerUserByExerciseDid(exerciseDid);

    double avg = feedbacks.stream().mapToDouble(Feedback::getPercentage).average().orElse(0.0);
    avg = Math.round(avg * 100.0) / 100.0;

    final long howManyUsers = feedbacks.size();

    final int howManyAttempts =
        Math.toIntExact(
            attemptRepository.findByExercise(exercise).stream()
                .filter(Attempt::isAttemptIsSubmitted)
                .count());

    final Map<UUID, Integer> wrongCount = HashMap.newHashMap(feedbacks.size() * 2);
    for (Feedback fb : feedbacks) {
      for (Object o : fb.getFeedbackData()) {
        @SuppressWarnings("unchecked")
        final Map<String, Object> m = (Map<String, Object>) o;
        if (Boolean.FALSE.equals(m.get("isCorrect"))) {
          final UUID qDid = UUID.fromString((String) m.get("questionDid"));
          wrongCount.merge(qDid, 1, Integer::sum);
        }
      }
    }

    final List<QuestionStatsDTO> questionStats =
        questionRepository.findByExercise_ID(exerciseDid).stream()
            .map(
                q -> {
                  final int wrongs = wrongCount.getOrDefault(q.getQuestionDid(), 0);
                  return new QuestionStatsDTO(q.getQuestionTitle(), howManyAttempts, wrongs);
                })
            .toList();

    return new ExerciseStatsDTO(exercise.getExerciseTitle(), avg, howManyUsers, questionStats);
  }
}
