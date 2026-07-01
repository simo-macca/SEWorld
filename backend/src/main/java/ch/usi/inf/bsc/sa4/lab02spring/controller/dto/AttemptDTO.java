package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import ch.usi.inf.bsc.sa4.lab02spring.model.Attempt;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) that provides a simplified view of an {@link Attempt} entity.
 *
 * <p>This record encapsulates the essential details of an attempt that need to be transferred
 * between the server and clients. It includes the attempt's unique decentralized identifier (DID),
 * its current completion stage, submission and completion status, and optionally the associated
 * {@link Exercise} DID.
 *
 * @param attemptDID the unique decentralized identifier (UUID) of the attempt
 * @param attemptIsSubmitted a flag indicating whether the attempt has been submitted
 * @param attemptIsCompleted a flag indicating whether the attempt has been completed
 * @param exerciseDid the unique decentralized identifier (UUID) of the associated exercise; this
 *     can be {@code null} if not applicable
 */
public record AttemptDTO(
    UUID attemptDID, boolean attemptIsSubmitted, boolean attemptIsCompleted, UUID exerciseDid) {

  /**
   * Constructs a new {@code AttemptDTO} from an {@link Attempt} entity.
   *
   * <p>This constructor initializes the DTO by extracting the necessary fields from the given
   * {@code Attempt} instance. The associated exercise DID is set to {@code null}.
   *
   * @param attempt the {@link Attempt} entity used to initialize the DTO
   */
  public AttemptDTO(Attempt attempt) {
    this(
        attempt.getAttemptDid(),
        attempt.isAttemptIsSubmitted(),
        attempt.isAttemptIsCompleted(),
        null);
  }

  /**
   * Constructs a new {@code AttemptDTO} from an {@link Attempt} entity, associating it with a
   * specific exercise.
   *
   * <p>This constructor initializes the DTO by extracting the necessary fields from the given
   * {@code Attempt} instance and sets the associated exercise's decentralized identifier.
   *
   * @param attempt the {@link Attempt} entity used to initialize the DTO
   * @param exerciseDid the unique decentralized identifier (UUID) of the related {@link Exercise}
   */
  public AttemptDTO(Attempt attempt, UUID exerciseDid) {
    this(
        attempt.getAttemptDid(),
        attempt.isAttemptIsSubmitted(),
        attempt.isAttemptIsCompleted(),
        exerciseDid);
  }
}
