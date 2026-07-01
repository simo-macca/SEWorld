package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import ch.usi.inf.bsc.sa4.lab02spring.model.TrueFalseQuestion;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Data Transfer Object (DTO) representing a true/false question.
 *
 * <p>This record encapsulates the essential information for a true/false question, including its
 * unique identifier, title, associated exercise ID, the correct answer, and a type indicator. It
 * implements the {@link QuestionDTO} interface, allowing for polymorphic handling of different
 * question types.
 *
 * @param questionDid the unique identifier of the question
 * @param questionTitle the title or text of the question
 * @param exerciseDid the unique identifier of the associated exercise
 * @param correctAnswer the correct answer to the question; {@code true} or {@code false}
 * @param type the type indicator of the question
 * @param variantDid the unique identifier of the associated variant
 * @param variantIndex the index of the associated variant
 */
public record TrueFalseQuestionDTO(
    UUID questionDid,
    String questionTitle,
    UUID exerciseDid,
    Boolean correctAnswer,
    String type,
    UUID variantDid,
    Integer variantIndex)
    implements QuestionDTO<Boolean> {

  /**
   * Constructs a {@code TrueFalseQuestionDTO} from a {@code TrueFalseQuestion} model instance.
   *
   * <p>This constructor maps the properties of the {@code TrueFalseQuestion} model to the
   * corresponding components of the DTO.
   *
   * @param question the {@code TrueFalseQuestion} model instance
   */
  public TrueFalseQuestionDTO(TrueFalseQuestion question) {
    this(
        question.getQuestionDid(),
        question.getQuestionTitle(),
        question.getExercise().getExerciseDid(),
        question.isCorrectAnswer(),
        "TF",
        question.getVariant().getVariantDid(),
        question.getVariant().getIdx());
  }

  /**
   * Generates a new true/false question variant using AI prompts and evaluates its correctness.
   *
   * <p>This method first instructs the AI to produce a concise true/false statement on the same
   * topic as the provided questions, then prompts the AI again to verify whether that statement is
   * true or false.
   *
   * @param chatClient the {@link ChatClient} used to send system and user prompts to the AI model
   * @param questionTitles the list of existing question titles to inform the variant generation
   * @return a {@link TrueFalseQuestionDTO} containing the generated statement, its correctness, and
   *     related metadata
   * @throws HttpClientErrorException if the AI’s response for the statement or its truth evaluation
   *     is null or empty
   */
  @Override
  public TrueFalseQuestionDTO generateQuestionVariant(
      ChatClient chatClient, List<String> questionTitles) {

    final String generateQuestionPrompt =
        "You are an expert educator specializing in creating clear and unambiguous true/false questions. "
            + "Your task is to create a true/false question following these guidelines:"
            + "1. Focus on factual content that can be verified"
            + "2. Use clear, precise language without qualifiers like 'sometimes' or 'often'"
            + "3. Avoid double negatives or confusing phrasing"
            + "4. The statement should be educational and test knowledge in academic subjects"
            + "5. Keep the statement moderate in length (15-25 words)";

    String variantTitle =
        chatClient
            .prompt()
            .system(s -> s.text(generateQuestionPrompt))
            .user(
                u ->
                    u.text(
                            "Create an original true/false statement that is referring to the same topic"
                                + " as these following questions without utilize them directly:"
                                + "{existingQuestions}"
                                + "Provide ONLY the statement text itself,"
                                + " with no additional explanation or information.")
                        .param("existingQuestions", questionTitles))
            .call()
            .content();

    if (variantTitle == null || variantTitle.isEmpty()) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "The AI output for true/false question cannot be null or empty");
    }

    variantTitle = this.cleanString(variantTitle);

    final String generateCorrectAnswerPrompt =
        "You are a highly knowledgeable fact-checker with expertise across academic fields. "
            + "Your task is to evaluate whether the given statement is TRUE or FALSE. "
            + "Follow these guidelines for your evaluation:"
            + "1. Analyze the statement carefully for factual accuracy"
            + "2. Consider the statement exactly as written, without adding assumptions"
            + "3. Evaluate based on widely accepted academic consensus"
            + "4. If any part of the statement is false, the entire statement is false"
            + "5. Apply strict logical reasoning in your evaluation"
            + "6. Do not consider edge cases or extremely rare exceptions"
            + "7. Base your judgment on current, established knowledge"
            + "Respond with ONLY the word 'true' or 'false' - nothing else.";

    final String finalVariantTitle = variantTitle;
    String evaluationResult =
        chatClient
            .prompt()
            .system(s -> s.text(generateCorrectAnswerPrompt))
            .user(
                u ->
                    u.text(
                            "Evaluate whether this statement is TRUE or FALSE:"
                                + "{statement}"
                                + "Respond with ONLY the word 'true' or 'false'.")
                        .param("statement", finalVariantTitle))
            .call()
            .content();

    if (evaluationResult == null || evaluationResult.isEmpty()) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "The AI output for truth evaluation cannot be null or empty");
    }

    evaluationResult = evaluationResult.trim().toLowerCase(Locale.ROOT);

    return new TrueFalseQuestionDTO(
        null,
        variantTitle.trim(),
        exerciseDid,
        isCorrect(evaluationResult),
        type,
        null,
        variantIndex);
  }

  private boolean isCorrect(String evaluationResult) {
    if (evaluationResult.contains("true")) {
      return true;
    } else if (evaluationResult.contains("false")) {
      return false;
    } else {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "AI output for truth evaluation must be 'true' or 'false'");
    }
  }
}
