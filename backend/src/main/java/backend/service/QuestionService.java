package backend.service;

import backend.controller.dto.QuestionDTO;
import backend.controller.dto.UpdateQuestionDTO;
import backend.controller.dto.create.CreateQuestionDTO;
import backend.model.AbstractQuestion;
import backend.model.Exercise;
import backend.repository.ExerciseRepository;
import backend.repository.QuestionRepository;
import backend.service.mapper.QuestionMapper;
import java.util.List;
import java.util.Map;
//
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuestionService {

  private final QuestionRepository questionRepository;
  private final ExerciseRepository exerciseRepository;
  // Maps DTO Class -> Mapper (Used for Creating/Input)
  private final Map<Class<?>, QuestionMapper> dtoMapperMap;
  // Maps Entity Class -> Mapper (Used for Reading/Output)
  private final Map<Class<?>, QuestionMapper> entityMapperMap;

  public QuestionService(
      QuestionRepository questionRepository,
      ExerciseRepository exerciseRepository,
      List<QuestionMapper> mappers) {
    this.questionRepository = questionRepository;
    this.exerciseRepository = exerciseRepository;
    this.dtoMapperMap =
        mappers.stream()
            .collect(Collectors.toMap(QuestionMapper::getSupportedType, Function.identity()));
    this.entityMapperMap =
        mappers.stream()
            .collect(Collectors.toMap(QuestionMapper::getSupportedEntity, Function.identity()));
  }

  @Cacheable(value = "SEWorldCache", key = "'allQuestions'")
  @Transactional(readOnly = true)
  public List<QuestionDTO> getAllQuestions() {
    List<AbstractQuestion> questions = questionRepository.findAll();
    if (questions.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Questions not found");
    }

    return questions.stream().map(this::convertToDto).toList();
  }

  @Cacheable(value = "SEWorldCache", key = "'exercise-' + #exerciseSlug + '-questions'")
  @Transactional(readOnly = true)
  public List<QuestionDTO> getAllQuestionsByExercise(String exerciseSlug) {
    List<AbstractQuestion> questions =
        questionRepository.getAllByQuestionExercise_ExerciseSlug(exerciseSlug);

    if (questions.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Questions not found");
    }
    return questions.stream().map(this::convertToDto).toList();
  }

  @CacheEvict(value = "SEWorldCache", allEntries = true)
  @Transactional
  public List<QuestionDTO> createQuestion(
      String exerciseSlug, List<CreateQuestionDTO> questionDTO) {
    Exercise exercise =
        exerciseRepository
            .getByExerciseSlug(exerciseSlug)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));

    if (!exercise.IsDraft()) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Cannot create questions to a published exercise");
    }
    return questionDTO.stream()
        .map(
            dto -> {
              QuestionMapper mapper = dtoMapperMap.get(dto.getClass());
              if (mapper == null) {
                throw new IllegalArgumentException(
                    "No mapper found for type: " + dto.getClass().getSimpleName());
              }
              AbstractQuestion question = mapper.map(dto);
              question.setQuestionExercise(exercise);
              question.setQuestionSlug(generateHybridSlug(question.getQuestionText()));
              questionRepository.save(question);
              return convertToDto(question);
            })
        .toList();
  }

  @CacheEvict(value = "SEWorldCache", allEntries = true)
  @Transactional
  public List<QuestionDTO> updateQuestions(List<UpdateQuestionDTO> updates) {
    return updates.stream()
        .map(updateItem -> updateQuestion(updateItem.questionSlug(), updateItem.questionDTO()))
        .toList();
  }

  @CacheEvict(value = "SEWorldCache", allEntries = true)
  @Transactional
  public void deleteQuestions(List<String> questionsSlug) {
    questionRepository.deleteAllByQuestionSlugIn(questionsSlug);
  }

  private QuestionDTO updateQuestion(String questionSlug, CreateQuestionDTO questionDTO) {
    AbstractQuestion question =
        questionRepository
            .getAbstractQuestionsByQuestionSlug(questionSlug)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

    if (!question.getQuestionExercise().IsDraft()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot update a published question");
    }

    QuestionMapper mapper = dtoMapperMap.get(questionDTO.getClass());
    if (mapper == null) {
      throw new IllegalArgumentException(
          "No mapper found for type: " + questionDTO.getClass().getSimpleName());
    }

    String oldText = question.getQuestionText();
    mapper.update(question, questionDTO);

    if (!question.getQuestionText().equals(oldText)) {
      question.setQuestionSlug(generateHybridSlug(question.getQuestionText()));
    }
    questionRepository.save(question);

    return convertToDto(question);
  }

  private QuestionDTO convertToDto(AbstractQuestion question) {
    QuestionMapper mapper = entityMapperMap.get(question.getClass());

    if (mapper == null) {
      throw new IllegalStateException("No mapper found for entity: " + question.getClass());
    }

    return mapper.mapToDto(question);
  }

  private String generateHybridSlug(String questionText) {
    String prettyPart =
        Optional.ofNullable(questionText)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(
                s ->
                    s.toLowerCase()
                        .replaceAll("[^a-z0-9]+", "-")
                        .replaceAll("^-+|-+$", "")
                        .replaceAll("-{2,}", "-"))
            .filter(s -> !s.isEmpty())
            .orElse("question");

    String truncatedPart =
        prettyPart.length() > 50 ? prettyPart.substring(0, 50).replaceAll("-+$", "") : prettyPart;

    String randomPart = UUID.randomUUID().toString().substring(0, 8);

    return truncatedPart + "-" + randomPart;
  }
}
