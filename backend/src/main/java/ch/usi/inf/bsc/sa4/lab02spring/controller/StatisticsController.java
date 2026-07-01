package ch.usi.inf.bsc.sa4.lab02spring.controller;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.ExerciseStatsDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.StatisticsInstructorDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.StatisticsStudentDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.StudentGradeDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.TopicExercisesStatsDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.response.ResponseHandler;
import ch.usi.inf.bsc.sa4.lab02spring.service.StatisticsService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.UserRoleCheckerUtils;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

/**
 * REST controller for handling statistics-related endpoints.
 *
 * <p>This controller exposes endpoints for retrieving various statistics views based on the role of
 * the authenticated user. The responses vary between student and instructor views.
 */
@RestController
@RequestMapping("/api/auth/statistics")
public class StatisticsController {
  /** Return message */
  private static final String MESSAGE = "Statistics uploaded";

  /** Service layer for managing user operations. */
  private final UserService userService;

  /** Service layer for managing statistic operations. */
  private final StatisticsService statisticsService;

  /**
   * Constructs a new {@code StatisticsController} with the required services.
   *
   * @param userService the service for managing user-related operations
   * @param statisticsService the service for retrieving statistics views and metrics
   */
  @Autowired
  public StatisticsController(UserService userService, StatisticsService statisticsService) {
    this.userService = userService;
    this.statisticsService = statisticsService;
  }

  /**
   * Retrieves the general statistics view.
   *
   * <p>This endpoint returns a general profile page with statistical information. The response
   * varies depending on whether the authenticated user is an instructor or a student:
   *
   * @param principal the currently authenticated user, injected by Spring Security.
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A success message ("Statistics uploaded").
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A statistics data transfer object representing either a student or instructor view.
   *     </ul>
   */
  @GetMapping("/general_view_statistics")
  public ResponseEntity<Object> studentView(@AuthenticationPrincipal Object principal) {
    if (UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      final StatisticsInstructorDTO instructorDTO = statisticsService.getInstructorView();
      return ResponseHandler.generateResponse(MESSAGE, HttpStatus.OK, true, instructorDTO);
    }
    final AbstractUser user = userService.findOrCreateUser(principal);
    final StatisticsStudentDTO studentDTO = statisticsService.getStudentView(user);
    return ResponseHandler.generateResponse(MESSAGE, HttpStatus.OK, true, studentDTO);
  }

  /**
   * Retrieves the general statistics view for a specific user.
   *
   * <p>This endpoint returns a general profile page with statistical information. The response
   * varies depending on the decentralized identifier (DID) of the user:
   *
   * @param userDid the user decentralized identifier (DID) to retrieve statistics for.
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A success message ("Statistics uploaded").
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A statistics data transfer object representing either a student or instructor view.
   *     </ul>
   */
  @GetMapping("/general_view_statistics/{user_did}")
  public ResponseEntity<Object> studentViewByUserDid(@PathVariable("user_did") UUID userDid) {
    final AbstractUser user =
        userService
            .getByDid(userDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "User Not found"));
    final StatisticsStudentDTO studentDTO = statisticsService.getStudentView(user);
    return ResponseHandler.generateResponse(MESSAGE, HttpStatus.OK, true, studentDTO);
  }

  /**
   * Retrieves statistics for exercises associated with a specific topic.
   *
   * <p>This endpoint provides exercise statistics for a given topic, identified by its
   * decentralized identifier (DID). The response differs based on the user's role
   *
   * @param principal the currently authenticated user, injected by Spring Security.
   * @param topicDid the decentralized identifier (DID) of the topic
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A success message ("Statistics uploaded").
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A {@link TopicExercisesStatsDTO} representing the topic exercise statistics.
   *     </ul>
   */
  @GetMapping("/topic/{topic_did}")
  public ResponseEntity<Object> getTopicExercisesStatistics(
      @AuthenticationPrincipal Object principal, @PathVariable("topic_did") UUID topicDid) {
    if (UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      final TopicExercisesStatsDTO exerciseStatistics =
          statisticsService.getExerciseInstructorView(topicDid);
      return ResponseHandler.generateResponse(MESSAGE, HttpStatus.OK, true, exerciseStatistics);
    }
    final AbstractUser user = userService.findOrCreateUser(principal);
    final TopicExercisesStatsDTO exerciseStatistics =
        statisticsService.getExerciseStudentView(topicDid, user);
    return ResponseHandler.generateResponse(MESSAGE, HttpStatus.OK, true, exerciseStatistics);
  }

  /**
   * Retrieves statistics for exercises associated with a specific topic for a specific user.
   *
   * <p>This endpoint provides exercise statistics for a given topic, identified by its
   * decentralized identifier (DID). The response differs based on the user's decentralized
   * identifier (DID).
   *
   * @param userDid the user decentralized identifier (DID) to retrieve statistics for.
   * @param topicDid the decentralized identifier (DID) of the topic
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A success message ("Statistics uploaded").
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A {@link TopicExercisesStatsDTO} representing the topic exercise statistics.
   *     </ul>
   */
  @GetMapping("/topic/{topic_did}/{user_did}")
  public ResponseEntity<Object> getTopicExercisesStatisticsByUserDid(
      @PathVariable("topic_did") UUID topicDid, @PathVariable("user_did") UUID userDid) {
    final AbstractUser user =
        userService
            .getByDid(userDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "User Not found"));
    final TopicExercisesStatsDTO exerciseStatistics =
        statisticsService.getExerciseStudentView(topicDid, user);
    return ResponseHandler.generateResponse(MESSAGE, HttpStatus.OK, true, exerciseStatistics);
  }

  /**
   * Retrieves statistics for a single exercise.
   *
   * <p>This endpoint returns detailed statistics for an individual exercise, identified by its
   * decentralized identifier (DID). The response is tailored based on the user's role
   *
   * @param principal the currently authenticated user, injected by Spring Security.
   * @param exerciseDid the decentralized identifier (DID) of the exercise
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A success message ("Statistics uploaded").
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A data transfer object containing the exercise statistics ({@link ExerciseStatsDTO}
   *           for instructors or {@link StudentGradeDTO} for students).
   *     </ul>
   */
  @GetMapping("/exercise/{exercise_did}")
  public ResponseEntity<Object> getSingleExerciseStats(
      @AuthenticationPrincipal Object principal, @PathVariable("exercise_did") UUID exerciseDid) {
    if (UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      final ExerciseStatsDTO exerciseStatistics =
          statisticsService.getSingleExerciseInstructorStats(exerciseDid);
      return ResponseHandler.generateResponse(MESSAGE, HttpStatus.OK, true, exerciseStatistics);
    }
    final AbstractUser user = userService.findOrCreateUser(principal);
    final StudentGradeDTO exerciseStatistics =
        statisticsService.getSingleExerciseStudentStats(exerciseDid, user);
    return ResponseHandler.generateResponse(MESSAGE, HttpStatus.OK, true, exerciseStatistics);
  }
}
