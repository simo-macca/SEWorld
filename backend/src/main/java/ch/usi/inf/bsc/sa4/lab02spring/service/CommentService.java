package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.CommentDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractAIResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Comment;
import ch.usi.inf.bsc.sa4.lab02spring.model.Instructor;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AIResponseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.CommentRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service class responsible for managing operations related to comments on AI-generated responses.
 *
 * <p>This includes creating, retrieving, and deleting comments. Authorization and data validation
 * are handled to ensure only authorized instructors can manipulate comments.
 */
@Service
@Transactional
public class CommentService {
  /** {@link AIResponseRepository} used by this service. */
  private final AIResponseRepository aiResponseRepo;

  /** {@link CommentRepository} used by this service. */
  private final CommentRepository commentRepo;

  /** {@link UserService} used by this service. */
  private final UserService userService;

  /**
   * Constructs a {@code CommentService} with the necessary dependencies.
   *
   * @param aiResponseRepo the repository for accessing AI response data
   * @param commentRepo the repository for managing comment data
   * @param userService the service for handling user authentication and lookup
   */
  @Autowired
  public CommentService(
      AIResponseRepository aiResponseRepo, CommentRepository commentRepo, UserService userService) {
    this.aiResponseRepo = aiResponseRepo;
    this.commentRepo = commentRepo;
    this.userService = userService;
  }

  /**
   * Adds a new comment to a given AI response by a specific instructor.
   *
   * @param responseDid the UUID of the AI response to comment on
   * @param commentText the text content of the comment
   * @param instructor the instructor creating the comment
   * @return the UUID of the newly created comment
   * @throws ResponseStatusException if the referenced AI response does not exist
   */
  public UUID addComment(UUID responseDid, String commentText, Instructor instructor) {

    final AbstractAIResponse aiResp =
        aiResponseRepo
            .findByAiResponseDID(responseDid)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AI response not found"));

    Comment c = new Comment(commentText, instructor, aiResp);
    c = commentRepo.save(c);

    return c.getCommentDid();
  }

  /**
   * Deletes a comment if the authenticated instructor is the original author.
   *
   * @param commentDid the UUID of the comment to delete
   * @param principal the currently authenticated user principal
   * @throws ResponseStatusException if the comment does not exist
   * @throws HttpClientErrorException if the user is not authorized to delete the comment
   */
  public void deleteComment(UUID commentDid, Object principal) {
    final AbstractUser user = userService.findOrCreateUser(principal);
    final Comment c =
        commentRepo
            .findByCommentDid(commentDid)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
    if (!c.getInstructor().getDid().equals(user.getDid())) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED,
          "You are not authorized to delete a comment made by another instructor");
    }
    commentRepo.delete(c);
  }

  /**
   * Retrieves all comments for a specific AI response, formatted as DTOs.
   *
   * @param responseDid the UUID of the AI response
   * @param principal the currently authenticated user principal
   * @return a list of {@link CommentDTO} representing the comments
   * @throws ResponseStatusException if the AI response does not exist
   */
  public List<CommentDTO> getCommentsForResponse(UUID responseDid, Object principal) {
    final AbstractUser user = userService.findOrCreateUser(principal);
    aiResponseRepo
        .findByAiResponseDID(responseDid)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AI response not found"));
    return commentRepo.findByAiResponse_AiResponseDID(responseDid).stream()
        .map(c -> new CommentDTO(c, c.getInstructor(), user.getDid()))
        .toList();
  }
}
