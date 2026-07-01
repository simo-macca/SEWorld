package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import ch.usi.inf.bsc.sa4.lab02spring.model.AIExerciseResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractQuestion;
import ch.usi.inf.bsc.sa4.lab02spring.model.Answer;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing an AI-generated response to an exercise question. This
 * DTO encapsulates the AI's answer, the user's answer, associated metadata, and user ratings. It is
 * used to transfer AI exercise response data between layers of the application.
 */
public class AiExerciseResponseDTO extends AiAbstractResponseDTO {

  /** The unique identifier (DID) of the associated answer. */
  private final UUID answerDid;

  /** The title of the exercise's question associated with this response. */
  private final String questionTitle;

  /** The user's answer to the exercise question. */
  private final String userAnswer;

  /**
   * Constructs an {@code AiExerciseResponseDTO} from the provided {@link AIExerciseResponse} and
   * user rating.
   *
   * @param exerciseResponse the {@link AIExerciseResponse} entity containing the AI's response data
   * @param userRating the rating provided by the user for this response
   */
  public AiExerciseResponseDTO(AIExerciseResponse exerciseResponse, Integer userRating) {
    super(
        exerciseResponse.getAiResponseDID(),
        exerciseResponse.getIsPublic(),
        exerciseResponse.getRate(),
        userRating,
        exerciseResponse.getAiAnswer());
    final Answer answer = exerciseResponse.getAnswer();
    final AbstractQuestion question = answer.getQuestion();

    this.userAnswer = question.getStringAnswerByUserAnswer(exerciseResponse.getUserAnswer());
    this.answerDid = answer.getDid();
    this.questionTitle = question.getQuestionTitle();

    setQuestionType("Exercise Question");
  }

  /**
   * Returns the unique identifier (DID) of the associated answer.
   *
   * @return the {@link UUID} representing the answer's DID
   */
  public UUID getAnswerDid() {
    return answerDid;
  }

  /**
   * Returns the user's answer to the exercise question.
   *
   * @return the user's answer as a {@link String}
   */
  public String getUserAnswer() {
    return userAnswer;
  }

  /**
   * Returns the title of the exercise's question associated with this response.
   *
   * @return the title of the exercise's question as a {@link String}
   */
  public String getQuestionTitle() {
    return questionTitle;
  }
}
