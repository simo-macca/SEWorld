package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.ai.chat.client.ChatClient;

/**
 * Marker interface for all question DTOs, enabling Jackson polymorphic
 * serialization/deserialization.
 *
 * <p>This sealed interface restricts its implementors to {@link MultiChoiceQuestionDTO}, {@link
 * ShortAnswerQuestionDTO}, and {@link TrueFalseQuestionDTO}. The {@code @JsonTypeInfo} annotation
 * uses the {@code type} property as a discriminator, and {@code @JsonSubTypes} registers each
 * subtype with its logical type name.
 *
 * @param <T> the type of the correct answer payload
 */
@JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = MultiChoiceQuestionDTO.class, name = "MCH"),
  @JsonSubTypes.Type(value = ShortAnswerQuestionDTO.class, name = "SHA"),
  @JsonSubTypes.Type(value = TrueFalseQuestionDTO.class, name = "TF")
})
public sealed interface QuestionDTO<T>
    permits MultiChoiceQuestionDTO, ShortAnswerQuestionDTO, TrueFalseQuestionDTO {

  /** Pattern used to match leading and trailing double quotes. */
  Pattern QUOTE_EDGE_PATTERN = Pattern.compile("(^\")|(\"$)");

  /**
   * Returns the DID of the question.
   *
   * @return the question’s UUID
   */
  UUID questionDid();

  /**
   * Returns the title of the question.
   *
   * @return the question’s title
   */
  String questionTitle();

  /**
   * Returns the DID of the exercise to which this question belongs.
   *
   * @return the exercise’s UUID
   */
  UUID exerciseDid();

  /**
   * Returns the correct answer for this question, in a type-specific form.
   *
   * @return the correct answer payload
   */
  T correctAnswer();

  /**
   * Returns the discriminator value used in JSON to identify the subtype.
   *
   * @return the logical type name
   */
  String type();

  /**
   * Returns the DID of the question’s variant
   *
   * @return the variant’s UUID
   */
  UUID variantDid();

  /**
   * Returns the index of the question’s variant
   *
   * @return the variant index
   */
  Integer variantIndex();

  /**
   * Generates a new question variant using an AI chat client based on existing question titles.
   *
   * <p>This method uses the provided {@link ChatClient} to create a new question variant
   * contextually related to the supplied list of question titles. The generated variant is
   * encapsulated within a {@link QuestionDTO} object.
   *
   * @param chatClient the {@link ChatClient} instance used to interact with the AI service
   * @param questionTitles a list of existing question titles to inform the AI-generated variant
   * @return a {@link QuestionDTO} containing the generated question variant
   */
  QuestionDTO<?> generateQuestionVariant(ChatClient chatClient, List<String> questionTitles);

  /**
   * Cleans the input string by removing enclosing double quotes and uncapping internal quotes.
   *
   * <p>This default method performs the following operations on the input string: Removes leading
   * and trailing double quotes, if present. Replaces escaped double quotes (e.g., {@code \"}) with
   * actual double quote characters. Trims leading and trailing whitespace from the resulting
   * string.
   *
   * @param input the string to be cleaned
   * @return the cleaned string with adjusted quotation marks and whitespace
   */
  default String cleanString(String input) {
    final String noEdgeQuotes = QUOTE_EDGE_PATTERN.matcher(input).replaceAll("");
    final String unescapedQuotes = noEdgeQuotes.replace("\\\"", "\"");
    return unescapedQuotes.trim();
  }
}
