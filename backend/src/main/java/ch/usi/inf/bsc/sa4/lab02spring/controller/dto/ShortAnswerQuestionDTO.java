package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import ch.usi.inf.bsc.sa4.lab02spring.model.ShortAnswerQuestion;
import java.util.List;
import java.util.UUID;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Data Transfer Object (DTO) representing a short answer question.
 *
 * <p>This record encapsulates the essential information for a short answer question, including its
 * unique identifier, title, associated exercise ID, list of correct answers, and a type indicator.
 * It implements the {@link QuestionDTO} interface, allowing for polymorphic handling of different
 * question types.
 *
 * @param questionDid the unique identifier of the question
 * @param questionTitle the title or text of the question
 * @param exerciseDid the unique identifier of the associated exercise
 * @param correctAnswer a list of acceptable correct answers for the question
 * @param type the type indicator of the question
 * @param variantDid the unique identifier of the associated variant
 * @param variantIndex the index of the associated variant
 */
public record ShortAnswerQuestionDTO(
    UUID questionDid,
    String questionTitle,
    UUID exerciseDid,
    String correctAnswer,
    String type,
    UUID variantDid,
    Integer variantIndex)
    implements QuestionDTO<String> {

  /**
   * Constructs a {@code ShortAnswerQuestionDTO} from a {@code ShortAnswerQuestion} model instance.
   *
   * <p>This constructor maps the properties of the {@code ShortAnswerQuestion} model to the
   * corresponding components of the DTO.
   *
   * @param question the {@code ShortAnswerQuestion} model instance
   */
  public ShortAnswerQuestionDTO(ShortAnswerQuestion question) {
    this(
        question.getQuestionDid(),
        question.getQuestionTitle(),
        question.getExercise().getExerciseDid(),
        question.getCorrectAnswer(),
        "SHA",
        question.getVariant().getVariantDid(),
        question.getVariant().getIdx());
  }

  /**
   * Generates a new short answer question variant using AI prompts and evaluates its correctness.
   *
   * <p>This method first instructs the AI to produce a concise short answer statement on the same
   * topic as the provided questions, then prompts the AI again to verify whether that statement is
   * true or false.
   *
   * @param chatClient the {@link ChatClient} used to send system and user prompts to the AI model
   * @param questionTitles the list of existing question titles to inform the variant generation
   * @return a {@link ShortAnswerQuestionDTO} containing the generated statement, its correctness,
   *     and related metadata
   * @throws HttpClientErrorException if the AI’s response for the statement or its truth evaluation
   *     is null or empty
   */
  @Override
  public ShortAnswerQuestionDTO generateQuestionVariant(
      ChatClient chatClient, List<String> questionTitles) {

    final String generateShortAnswerPrompt =
        "You are an expert educator specializing in creating clear, unambiguous short‐answer questions. "
            + "Your task is to craft a high‑quality prompt that elicits a concise,"
            + " factual response, following these guidelines:"
            + "1. Ask for a specific piece of information (fact, definition, date, term, name, equation, etc.)."
            + "2. Use precise, direct language without qualifiers like “often” or “usually.”"
            + "3. Avoid multi‑step or compound questions—focus on a single concept."
            + "4. Keep the prompt succinct (10–20 words) but fully self‑contained."
            + "5. Ensure there is one clear, correct answer that can be stated briefly."
            + "6. Do not include the answer in the prompt itself."
            + "7. The question should be educational and test academic knowledge."
            + "8. Make it challenging without being misleading."
            + "Your output should be ONLY the question text (no answer, no explanation).";

    final String variantTitle =
        chatClient
            .prompt()
            .system(s -> s.text(generateShortAnswerPrompt))
            .user(
                u ->
                    u.text(
                            "Create an original short‐answer question that is referring to the same topic"
                                + " as these following questions without utilize them directly:"
                                + "{existingPrompts}"
                                + "Provide ONLY the question text itself, with no answer or additional information.")
                        .param("existingPrompts", questionTitles))
            .call()
            .content();

    if (variantTitle == null || variantTitle.isEmpty()) {
      throw new IllegalStateException(
          "The AI title output for short answer question result cannot be null or empty");
    }

    final String generateAnswerPrompt =
        "You are an authoritative subject matter expert with extensive knowledge across academic fields. "
            + "Your task is to provide the definitive correct answer to an academic question. "
            + "This answer will serve as the 'golden standard' against which student responses will be evaluated. "
            + "Follow these specific guidelines:"
            + "1. Interpret the question accurately and answer precisely what is being asked"
            + "2. Provide a factually perfect response containing all essential information"
            + "3. Include ALL key concepts, terminologies, and facts that would be required in a correct answer"
            + "4. Be comprehensive but focused - include everything necessary and nothing superfluous"
            + "5. Structure your answer clearly with a logical progression of ideas"
            + "6. Use precise academic language and proper terminology"
            + "7. Ensure your answer is objective and represents the consensus view in the field"
            + "8. Make your answer detailed enough to distinguish between partial and complete understanding"
            + "Your answer must be definitive and authoritative - "
            + "it will be used to determine whether students' answers are correct.";

    final String variantCorrectAnswer =
        chatClient
            .prompt()
            .system(s -> s.text(generateAnswerPrompt))
            .user(
                u ->
                    u.text(
                            "Answer to this question: {questionTitle}"
                                + "this answer must contain ALL key points, concepts,"
                                + " and facts needed for a complete response. "
                                + "Include only the answer itself with no introductory phrases or meta-commentary.")
                        .param("questionTitle", variantTitle))
            .call()
            .content();

    if (variantCorrectAnswer == null) {
      throw new IllegalStateException(
          "The AI correct answer output for short answer question result cannot be null");
    }

    return new ShortAnswerQuestionDTO(
        null,
        this.cleanString(variantTitle),
        exerciseDid,
        this.cleanString(variantCorrectAnswer).trim(),
        type,
        null,
        variantIndex);
  }
}
