package ch.usi.inf.bsc.sa4.lab02spring.controller;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.FeedbackDTO;
import ch.usi.inf.bsc.sa4.lab02spring.response.ResponseHandler;
import ch.usi.inf.bsc.sa4.lab02spring.service.FeedbackService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for handling feedback-related endpoints.
 *
 * <p>This controller processes requests related to feedback for attempts. It exposes an endpoint
 * that retrieves the feedback for a specific attempt identified by its decentralized identifier
 * (DID). The controller delegates the logic to the {@link FeedbackService} and uses {@link
 * ResponseHandler} to format the response.
 *
 * @see FeedbackService
 * @see FeedbackDTO
 * @see ResponseHandler
 */
@Controller
@RequestMapping("/api/auth/feedback")
public class FeedbackController {

  /** Service layer for managing feedback operations. */
  private final FeedbackService feedbackService;

  /**
   * Constructs a new {@code FeedbackController} with the specified {@link FeedbackService}.
   *
   * <p>This constructor enables dependency injection of the {@code FeedbackService}, which is
   * responsible for handling the business logic related to feedback operations.
   *
   * @param feedbackService the service layer responsible for managing feedback operations
   */
  @Autowired
  public FeedbackController(FeedbackService feedbackService) {
    this.feedbackService = feedbackService;
  }

  /**
   * Retrieves feedback for a specific attempt.
   *
   * <p>This endpoint fetches the feedback details of an attempt using the provided attempt DID. The
   * feedback information is processed by the {@link FeedbackService} and returned as a {@link
   * FeedbackDTO}.
   *
   * @param principal the authenticated user making the request.
   * @param attemptDid the unique decentralized identifier (DID) of the attempt.
   * @return a {@link ResponseEntity} containing:
   *     <ul>
   *       <li>A success message indicating that the attempt feedback has been calculated.
   *       <li>An HTTP status code of {@code 200 OK}.
   *       <li>A success flag set to {@code true}.
   *       <li>A {@link FeedbackDTO} object containing the feedback details.
   *     </ul>
   */
  @GetMapping("/{attempt_did}")
  public ResponseEntity<Object> getFeedbackAttempt(
      @AuthenticationPrincipal Object principal, @PathVariable("attempt_did") UUID attemptDid) {
    final FeedbackDTO feedback = feedbackService.getFeedbackAttempt(principal, attemptDid);
    return ResponseHandler.generateResponse(
        "Attempt feedback calculated", HttpStatus.OK, true, feedback);
  }
}
