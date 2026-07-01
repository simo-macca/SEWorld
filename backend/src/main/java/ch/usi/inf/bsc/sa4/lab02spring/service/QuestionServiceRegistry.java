package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.MultiChoiceQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.QuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.ShortAnswerQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.TrueFalseQuestionDTO;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Registry for associating {@link QuestionDTO} types with their corresponding {@link
 * QuestionServiceInterface} implementations.
 *
 * <p>This component is initialized with concrete question services for each DTO subtype and
 * provides lookup functionality to retrieve the appropriate service based on the runtime class of a
 * given DTO.
 */
@Component
public class QuestionServiceRegistry {

  /** Internal map of DTO classes to their handling services. */
  private final Map<Class<? extends QuestionDTO>, QuestionServiceInterface<? extends QuestionDTO>>
      registry = new HashMap<>();

  /**
   * Constructs the registry by registering each supported QuestionDTO type with its corresponding
   * service implementation.
   *
   * @param multiChoiceService service handling {@link MultiChoiceQuestionDTO}
   * @param shortAnswerService service handling {@link ShortAnswerQuestionDTO}
   * @param trueFalseService service handling {@link TrueFalseQuestionDTO}
   */
  public QuestionServiceRegistry(
      MultiChoiceQuestionService multiChoiceService,
      ShortAnswerQuestionService shortAnswerService,
      TrueFalseQuestionService trueFalseService) {
    registry.put(MultiChoiceQuestionDTO.class, multiChoiceService);
    registry.put(ShortAnswerQuestionDTO.class, shortAnswerService);
    registry.put(TrueFalseQuestionDTO.class, trueFalseService);
  }

  /**
   * Retrieves the {@link QuestionServiceInterface} responsible for the given DTO instance.
   *
   * <p>The service is looked up by the DTO’s runtime class. If no service is registered for that
   * class, this method will return {@code null}.
   *
   * @param <T> the specific subtype of {@link QuestionDTO}
   * @param dto the question DTO instance for which to retrieve a service
   * @return the {@link QuestionServiceInterface} handling the given DTO type, or {@code null} if
   *     none is found
   */
  @SuppressWarnings("unchecked")
  public <T extends QuestionDTO> QuestionServiceInterface<T> getService(T dto) {
    return (QuestionServiceInterface<T>) registry.get(dto.getClass());
  }
}
