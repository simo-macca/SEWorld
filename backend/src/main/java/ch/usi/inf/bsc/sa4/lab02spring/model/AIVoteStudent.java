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
import jakarta.persistence.UniqueConstraint;

/**
 * Entity class representing a vote cast by a student on an AI-generated response. This class maps
 * to the "studentVoteAi" table in the database and is uniquely defined by the combination of a
 * student and an AI response. Each vote is associated with a specific student and a specific AI
 * response, with an optional integer value representing the actual vote.
 */
@Entity
@Table(
    name = "studentVoteAi",
    uniqueConstraints = @UniqueConstraint(columnNames = {"studentId", "aiResponseId"}))
public class AIVoteStudent {

  /** The primary key identifier of the vote. */
  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "vote_seq")
  @SequenceGenerator(name = "vote_seq", sequenceName = "vote_seq", allocationSize = 1)
  @Column(name = "voteId")
  private Long voteId;

  /** The student who cast the vote. */
  @ManyToOne
  @JoinColumn(name = "studentId")
  private Student studentId;

  /** The AI response on which the vote was cast. */
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "aiResponseId")
  private AbstractAIResponse aiResponseId;

  /** The vote associated with this response. */
  private Integer vote;

  /** Protected no‐arg constructor required by JPA. */
  public AIVoteStudent() {}

  /**
   * Constructs a new {@code AIVoteStudent} instance with the specified vote ID.
   *
   * @param voteId the primary key identifier of the vote
   */
  protected AIVoteStudent(Long voteId) {
    this.voteId = voteId;
  }

  /**
   * Constructs a new {@code AIVoteStudent} instance with the specified student and response.
   *
   * @param studentId the student who cast the vote
   * @param aiResponse the response on which the vote was cast
   */
  public AIVoteStudent(Student studentId, AbstractAIResponse aiResponse) {
    this.studentId = studentId;
    this.aiResponseId = aiResponse;
  }

  /**
   * Constructs a new {@code AIVoteStudent} instance with the specified student, response, and vote.
   *
   * @param studentId the student who cast the vote
   * @param aiResponse the response on which the vote was cast
   * @param vote the vote associated with the response
   */
  public AIVoteStudent(Student studentId, AbstractAIResponse aiResponse, int vote) {
    this.studentId = studentId;
    this.aiResponseId = aiResponse;
    this.vote = vote;
  }

  /**
   * Returns the primary key identifier of the vote.
   *
   * @return the vote ID
   */
  public Long getVoteId() {
    return voteId;
  }

  /**
   * Sets the primary key identifier of the vote.
   *
   * @param voteId the vote ID to set
   */
  public void setVoteId(Long voteId) {
    this.voteId = voteId;
  }

  /**
   * Returns the {@link Student} associated with this vote.
   *
   * @return the associated {@link Student}
   */
  public Student getStudentId() {
    return studentId;
  }

  /**
   * Sets the {@link Student} associated with this vote.
   *
   * @param studentId the {@link Student} to associate with this vote
   */
  public void setStudentId(Student studentId) {
    this.studentId = studentId;
  }

  /**
   * Returns the {@link AbstractAIResponse} associated with this vote.
   *
   * @return the associated {@link AbstractAIResponse}
   */
  public AbstractAIResponse getAiResponseId() {
    return aiResponseId;
  }

  /**
   * Sets the {@link AbstractAIResponse} associated with this vote.
   *
   * @param aiResponseId the {@link AbstractAIResponse} to associate with this vote
   */
  public void setAiResponseId(AbstractAIResponse aiResponseId) {
    this.aiResponseId = aiResponseId;
  }

  /**
   * Returns the vote associated with this response.
   *
   * @return the vote
   */
  public Integer getVote() {
    return vote;
  }

  /**
   * Sets the vote associated with this response.
   *
   * @param vote the vote to set
   */
  public void setVote(Integer vote) {
    this.vote = vote;
  }
}
