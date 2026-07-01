package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import ch.usi.inf.bsc.sa4.lab02spring.model.MultiChoiceQuestion;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Data Transfer Object (DTO) for representing a multiple choice question.
 *
 * <p>This record encapsulates all relevant data for a multiple choice question, including its
 * unique identifier, question title, list of answer choices, the exercise identifier it belongs to,
 * the index of the correct answer, and a type indicator which. It implements the {@code
 * QuestionDTO} interface, adhering to a common contract for question DTOs.
 *
 * @param questionDid the unique identifier of the question
 * @param questionTitle the title or text of the question
 * @param choices the list of possible answer choices
 * @param exerciseDid the unique identifier of the exercise associated with this question
 * @param correctAnswer the index of the correct answer within the list of choices
 * @param type the type indicator of the question
 * @param variantDid the unique identifier of the associated variant
 * @param variantIndex the index of the associated variant
 */
public record MultiChoiceQuestionDTO(
    UUID questionDid,
    String questionTitle,
    List<String> choices,
    UUID exerciseDid,
    Integer correctAnswer,
    String type,
    UUID variantDid,
    Integer variantIndex)
    implements QuestionDTO<Integer> {

  /**
   * Constructs a {@code MultiChoiceQuestionDTO} from a {@code MultiChoiceQuestion} model instance.
   *
   * <p>This constructor converts a {@code MultiChoiceQuestion} into its corresponding DTO by
   * mapping the model's properties to the DTO's components.
   *
   * @param question the {@code MultiChoiceQuestion} instance from which the DTO is created
   */
  public MultiChoiceQuestionDTO(MultiChoiceQuestion question) {
    this(
        question.getQuestionDid(),
        question.getQuestionTitle(),
        question.getChoices(),
        question.getExercise().getExerciseDid(),
        question.getCorrectAnswer(),
        "MCH",
        question.getVariant().getVariantDid(),
        question.getVariant().getIdx());
  }

  /**
   * Generates a new multi choices question variant using AI prompts and evaluates its correctness.
   *
   * <p>This method first instructs the AI to produce a concise multi choices statement on the same
   * topic as the provided questions, then prompts the AI again to verify whether that statement is
   * true or false.
   *
   * @param chatClient the {@link ChatClient} used to send system and user prompts to the AI model
   * @param questionTitles the list of existing question titles to inform the variant generation
   * @return a {@link MultiChoiceQuestionDTO} containing the generated statement, its correctness,
   *     and related metadata
   * @throws HttpClientErrorException if the AI’s response for the statement or its truth evaluation
   *     is null or empty
   */
  @Override
  public MultiChoiceQuestionDTO generateQuestionVariant(
      ChatClient chatClient, List<String> questionTitles) {

    final String generateQuestionTitlePrompt =
        "You are an expert educator specializing in creating clear, unambiguous multiple‑choice questions. "
            + "Your task is to craft a high‑quality question stem "
            + "(the text before the answer options) following these guidelines:"
            + "1. Pose a single, focused problem or question—avoid compound or multi‑part stems."
            + "2. Focus on factual content that can be objectively assessed."
            + "3. Use precise, direct language without qualifiers like “often” or “sometimes.”"
            + "4. Avoid negative phrasing or double negatives in the stem."
            + "5. Keep the stem concise (15–25 words) but sufficient to stand alone."
            + "6. The stem should be pedagogically valuable and test academic knowledge."
            + "7. Make it challenging without being misleading or tricky."
            + "8. Create an ORIGINAL stem that doesn’t closely mirror existing questions."
            + "Your output should be ONLY the question stem (no options, no explanation).";

    final String variantTitle =
        chatClient
            .prompt()
            .system(s -> s.text(generateQuestionTitlePrompt))
            .user(
                u ->
                    u.text(
                            "Create an original multiple‑choice question stem "
                                + "that is NOT similar to these existing stems:"
                                + "{existingStems}"
                                + "Provide ONLY the stem text itself, "
                                + "with no additional explanation or answer options.")
                        .param("existingStems", questionTitles))
            .call()
            .content();

    if (variantTitle == null || variantTitle.isEmpty()) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST,
          "The AI title output for multi choice question cannot be null or empty");
    }

    final String generateChoicesPrompt =
        "You are an expert question creator specializing in accurate multiple-choice questions. "
            + "Generate exactly 4 possible answers of 5-8 words each"
            + " directly related to this question: {questionTitle} "
            + "Follow these rules carefully:"
            + "1. Exactly ONE answer must be correct,"
            + " the other options should sound plausible but be completely incorrect"
            + "2. Do not include any explanation, introduction or conclusion"
            + "3. Do not indicate which answer is correct"
            + "4. Return your answer proposals like the following format: \"Answer 1, Answer 2, Answer 3, Answer 4\"."
            + " Don't add any number or letter indicating what is the order of the answers";

    final var aiChoices =
        chatClient
            .prompt()
            .system(s -> s.text(generateChoicesPrompt))
            .user(u -> u.text("Question: {questionTitle}").param("questionTitle", variantTitle))
            .call()
            .content();

    if (aiChoices == null) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "The AI multi question choices output cannot be null");
    }

    final String correctAnswerPrompt =
        "You are an expert evaluator for multiple-choice questions. "
            + "Determine which of the provided answer choices is correct for the given question."
            + "Your task is to identify the ZERO-BASED INDEX of the correct answer. "
            + "Follow these steps carefully:"
            + "1. Read the question carefully"
            + "2. Examine each answer choice thoroughly"
            + "3. Identify which answer is factually correct"
            + "4. Return ONLY the index number (0, 1, 2, or 3) of the correct answer"
            + "Examples:"
            + "- If the first answer is correct, return: 0"
            + "- If the second answer is correct, return: 1"
            + "- If the third answer is correct, return: 2"
            + "- If the fourth answer is correct, return: 3"
            + "Return ONLY the number, with no additional text or explanation.";

    final var aiIdx =
        chatClient
            .prompt()
            .system(s -> s.text(correctAnswerPrompt))
            .user(
                u ->
                    u.text("Question: {questionTitle}" + "Answer choices: {aiChoices}")
                        .param("questionTitle", variantTitle)
                        .param("aiChoices", aiChoices))
            .call()
            .content();

    if (aiIdx == null) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST,
          "The AI index of correct answer output for multi choice cannot be null");
    }

    final int variantCorrectAnswer = Integer.parseInt(aiIdx.trim());

    final List<String> variantChoices =
        Arrays.stream(aiChoices.split("(?i)answer[ \\t]*\\d+:[ \\t]*"))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();

    if (variantCorrectAnswer < 0 || variantCorrectAnswer > variantChoices.size() - 1) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST,
          "The AI index of correct answer output for multi choice cannot be out of the range 0-3");
    }

    return new MultiChoiceQuestionDTO(
        null,
        this.cleanString(variantTitle).trim(),
        variantChoices,
        exerciseDid,
        variantCorrectAnswer,
        type,
        null,
        variantIndex);
  }
}
