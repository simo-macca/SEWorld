package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import ch.usi.inf.bsc.sa4.lab02spring.model.Comment;
import ch.usi.inf.bsc.sa4.lab02spring.model.Instructor;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A Data Transfer Object (DTO) representing a comment made by an instructor, enriched with
 * additional metadata such as the author's name and ownership status.
 *
 * <p>This DTO is used to send comment data from the backend to the client.
 *
 * @param commentDid the unique identifier of the comment
 * @param instructorName the full name of the instructor who made the comment
 * @param isCallerTheOwner true if the currently logged-in user is the author of the comment
 * @param commentContent the textual content of the comment
 * @param timeStamp the date and time when the comment was created
 */
public record CommentDTO(
    UUID commentDid,
    String instructorName,
    boolean isCallerTheOwner,
    String commentContent,
    LocalDateTime timeStamp) {

  /**
   * Constructs a {@code CommentDTO} from a {@link Comment} and its associated {@link Instructor},
   * along with the UUID of the currently logged-in user to determine ownership.
   *
   * @param comment the original {@link Comment} entity
   * @param instructor the {@link Instructor} who authored the comment
   * @param loggedUserDid the UUID of the currently logged-in user
   */
  public CommentDTO(Comment comment, Instructor instructor, UUID loggedUserDid) {
    this(
        comment.getCommentDid(),
        instructor.getName(),
        instructor.getDid().equals(loggedUserDid),
        comment.getCommentText(),
        comment.getLocalDateTime());
  }
}
