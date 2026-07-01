package ch.usi.inf.bsc.sa4.lab02spring.model;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a comment made by an {@link Instructor} on an {@link AbstractAIResponse}.
 *
 * <p>Each comment has a globally unique identifier (UUID), is linked to a specific instructor and
 * AI response, and stores the comment text along with its creation timestamp.
 */
@Entity
@Table(name = "Comment")
public class Comment {

  /** Primary key for internal database usage. */
  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "comment_seq")
  @SequenceGenerator(name = "comment_seq", sequenceName = "comment_seq", allocationSize = 1)
  @Column(name = "comment_id")
  private Long commentId;

  /** Domain-specific unique identifier (DID) for external reference. */
  @Column(name = "comment_did", unique = true, updatable = false, nullable = false)
  private final UUID commentDid = UUID.randomUUID();

  /** The content of the comment. */
  @Column(name = "comment_text", nullable = false, length = 5000)
  private String commentText;

  /**
   * The timestamp when the comment was created. This value is generated automatically by the
   * database.
   */
  @Column(
      name = "date_and_time",
      nullable = false,
      updatable = false,
      insertable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime localDateTime;

  /** The instructor who authored the comment. */
  @ManyToOne(fetch = LAZY, optional = false)
  @JoinColumn(name = "instructor_id", nullable = false)
  private Instructor instructor;

  /** The AI response that the comment refers to. */
  @ManyToOne(fetch = LAZY, optional = false)
  @JoinColumn(name = "aiResponseId", nullable = false)
  private AbstractAIResponse aiResponse;

  /** Protected no-args constructor for JPA. */
  protected Comment() {
    /* JPA only */
  }

  /**
   * Constructs a new {@code Comment}.
   *
   * @param commentText the content of the comment
   * @param instructor the instructor who created the comment
   * @param aiResponse the AI response the comment is associated with
   */
  public Comment(String commentText, Instructor instructor, AbstractAIResponse aiResponse) {
    this.commentText = commentText;
    this.instructor = instructor;
    this.aiResponse = aiResponse;
  }

  /**
   * Returns the internal database ID.
   *
   * @return the comment's internal ID
   */
  public Long getCommentId() {
    return commentId;
  }

  /**
   * Returns the domain identifier (UUID) of the comment.
   *
   * @return the comment's DID
   */
  public UUID getCommentDid() {
    return commentDid;
  }

  /**
   * Returns the text content of the comment.
   *
   * @return the comment text
   */
  public String getCommentText() {
    return commentText;
  }

  /**
   * Returns the instructor who wrote the comment.
   *
   * @return the author of the comment
   */
  public Instructor getInstructor() {
    return instructor;
  }

  /**
   * Returns the AI response that the comment is associated with.
   *
   * @return the related AI response
   */
  public AbstractAIResponse getAiResponse() {
    return aiResponse;
  }

  /**
   * Returns the timestamp when the comment was created.
   *
   * @return the creation timestamp
   */
  public LocalDateTime getLocalDateTime() {
    return localDateTime;
  }
}
