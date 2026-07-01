package ch.usi.inf.bsc.sa4.lab02spring.controller;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.CommentDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.CreateCommentDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.response.ResponseHandler;
import ch.usi.inf.bsc.sa4.lab02spring.service.CommentService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.UserRoleCheckerUtils;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

/**
 * REST controller responsible for handling comment-related operations on AI-generated responses.
 *
 * <p>Includes endpoints for adding, deleting, and retrieving comments.
 */
@RestController
@RequestMapping("/api/auth/AI/responses")
@Validated
public class CommentController {
  /** The {@link CommentService} used by this controller. */
  private final CommentService commentService;

  /** The {@link UserService} used by this controller. */
  private final UserService userService;

  /**
   * Constructs a new {@code CommentController} with the required services.
   *
   * @param commentService service to manage comment operations
   * @param userService service to manage user operations
   */
  @Autowired
  public CommentController(CommentService commentService, UserService userService) {
    this.commentService = commentService;
    this.userService = userService;
  }

  /**
   * Adds a new comment to a specific AI response.
   *
   * @param responseDid the UUID of the AI response to comment on
   * @param dto the comment content, validated via {@link CreateCommentDTO}
   * @param principal the authenticated user making the request
   * @return a {@link ResponseEntity} containing success status and the new comment ID
   * @throws HttpClientErrorException if the user is a student (unauthorized to comment)
   */
  @PostMapping("/comment/{response_did}")
  public ResponseEntity<Object> addComment(
      @PathVariable("response_did") UUID responseDid,
      @Valid @RequestBody CreateCommentDTO dto,
      @AuthenticationPrincipal Object principal) {
    final AbstractUser user = userService.findOrCreateUser(principal);

    if ("STUDENT".equals(user.getRole())) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "Students aren't allowed to add comments to ai responses");
    }

    final UUID commentDid =
        commentService.addComment(responseDid, dto.comment(), user.getInstructor());

    return ResponseHandler.generateResponse(
        "Comment added successfully", HttpStatus.CREATED, true, commentDid);
  }

  /**
   * Deletes a comment by its UUID.
   *
   * @param commentDid the UUID of the comment to delete
   * @param principal the authenticated user making the request
   * @return a {@link ResponseEntity} indicating the deletion result
   * @throws HttpClientErrorException if the user is not an instructor
   */
  @DeleteMapping("/comment/{comment_did}/delete")
  public ResponseEntity<Object> deleteComment(
      @PathVariable("comment_did") UUID commentDid, @AuthenticationPrincipal Object principal) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "Students aren't allowed to delete comments");
    }
    commentService.deleteComment(commentDid, principal);
    return ResponseHandler.generateResponse(
        "Comment deleted successfully", HttpStatus.OK, true, null);
  }

  /**
   * Retrieves all comments associated with a specific AI response.
   *
   * @param responseDid the UUID of the AI response
   * @param principal the authenticated user making the request
   * @return a {@link ResponseEntity} containing a list of {@link CommentDTO}
   */
  @GetMapping("/comment/{responseDid}/allComments")
  public ResponseEntity<Object> getComments(
      @PathVariable UUID responseDid, @AuthenticationPrincipal Object principal) {
    final List<CommentDTO> comments = commentService.getCommentsForResponse(responseDid, principal);
    return ResponseHandler.generateResponse(
        "Comments found successfully", HttpStatus.CREATED, true, comments);
  }
}
