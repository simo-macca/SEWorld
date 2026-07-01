package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AiAbstractResponseDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.AiMaterialResponseDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.QuestionAndUserAnswerDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.QuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.RateQuestionDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.SHAResponseDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AIEvaluation;
import ch.usi.inf.bsc.sa4.lab02spring.model.AIExerciseResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.AIMaterialResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.AIVoteStudent;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractAIResponse;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractQuestion;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Answer;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.Material;
import ch.usi.inf.bsc.sa4.lab02spring.model.ShortAnswerQuestion;
import ch.usi.inf.bsc.sa4.lab02spring.model.Student;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AIEvaluationRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AIExerciseResponseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AIMaterialResponseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AIResponseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AIStudentVoteRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.AnswerRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.ExerciseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.MaterialRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.MultiChoiceQuestionRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.QuestionRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.ShortAnswerQuestionRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.TrueFalseQuestionRepository;
import ch.usi.inf.bsc.sa4.lab02spring.utils.UserRoleCheckerUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class responsible for managing AI question-related operations.
 *
 * <p>This service integrates various repositories and services to handle AI interactions, user and
 * material management, answers, questions, exercises, and evaluations.
 */
@Service
public class AiQuestionService {

  /** Client to interact with the AI chat model. */
  private final ChatClient chatClient;

  /** Service to manage educational materials. */
  private final MaterialService materialService;

  /** Service to manage user-related operations. */
  private final UserService userService;

  /** Repository for Answer entities. */
  private final AnswerRepository answerRepository;

  /** Repository for Question entities. */
  private final QuestionRepository questionRepository;

  /** Repository for ShortAnswerQuestion entities. */
  private final ShortAnswerQuestionRepository shortAnswerQuestionRepository;

  /** Repository for Material entities. */
  private final MaterialRepository materialRepository;

  /** Repository for AIResponse entities. */
  private final AIResponseRepository aiResponseRepository;

  /** Repository for AIMaterialResponse entities. */
  private final AIMaterialResponseRepository materialResponseRepository;

  /** Repository for AIExerciseResponse entities. */
  private final AIExerciseResponseRepository aiExerciseResponseRepository;

  /** Repository for AIStudentVote entities. */
  private final AIStudentVoteRepository studentVoteAIRepository;

  /** Repository for Exercise entities. */
  private final ExerciseRepository exerciseRepository;

  /** Repository for AIEvaluation entities. */
  private final AIEvaluationRepository aiEvaluationRepository;

  /** Constant message used when requested material does not exist. */
  private final String MESSAGE_NO_AI_RESPONSE = "AI response doesn't exist";

  /**
   * Constructs the AiQuestionService with all required dependencies.
   *
   * @param myChatModel the AI chat model for creating the chat client
   * @param materialService service managing educational materials
   * @param materialResponseRepository repository for AI material responses
   * @param aiResponseRepository repository for AI responses
   * @param userService service managing users
   * @param materialRepository repository for materials
   * @param studentVoteAIRepository repository for AI student votes
   * @param aiExerciseResponseRepository repository for AI exercise responses
   * @param answerRepository repository for answers
   * @param questionRepository repository for questions
   * @param multiChoiceQuestionRepository repository for multiple choice questions (injected but not
   *     stored)
   * @param trueFalseQuestionRepository repository for true/false questions (injected but not
   *     stored)
   * @param shortAnswerQuestionRepository repository for short answer questions
   * @param exerciseRepository repository for exercises
   * @param aiEvaluationRepository repository for AI evaluations
   */
  @Autowired
  public AiQuestionService(
      ChatModel myChatModel,
      MaterialService materialService,
      AIMaterialResponseRepository materialResponseRepository,
      AIResponseRepository aiResponseRepository,
      UserService userService,
      MaterialRepository materialRepository,
      AIStudentVoteRepository studentVoteAIRepository,
      AIExerciseResponseRepository aiExerciseResponseRepository,
      AnswerRepository answerRepository,
      QuestionRepository questionRepository,
      MultiChoiceQuestionRepository multiChoiceQuestionRepository,
      TrueFalseQuestionRepository trueFalseQuestionRepository,
      ShortAnswerQuestionRepository shortAnswerQuestionRepository,
      ExerciseRepository exerciseRepository,
      AIEvaluationRepository aiEvaluationRepository) {
    this.studentVoteAIRepository = studentVoteAIRepository;
    this.chatClient = ChatClient.create(myChatModel);
    this.materialService = materialService;
    this.materialResponseRepository = materialResponseRepository;
    this.aiResponseRepository = aiResponseRepository;
    this.userService = userService;
    this.materialRepository = materialRepository;
    this.aiExerciseResponseRepository = aiExerciseResponseRepository;
    this.answerRepository = answerRepository;
    this.questionRepository = questionRepository;
    this.shortAnswerQuestionRepository = shortAnswerQuestionRepository;
    this.exerciseRepository = exerciseRepository;
    this.aiEvaluationRepository = aiEvaluationRepository;
  }

  /**
   * Generates an AI answer about a highlighted section of material.
   *
   * @param highlightedText the selected text from the material.
   * @param materialDid the UUID of the material.
   * @param question the user’s question.
   * @param user the Abstract user, i.e., either a Student or Instructor.
   * @return the saved {@link AIMaterialResponse}.
   * @throws HttpClientErrorException if the material is not found or not, markdown.
   */
  @Transactional
  public AIMaterialResponse askAboutHighlight(
      String highlightedText, UUID materialDid, String question, AbstractUser user) {
    final Optional<Material> material = materialRepository.findByMaterialDid(materialDid);

    if (material.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "material doesn't exist");
    }
    if (!"md".equals(material.get().getMaterialType())) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "mark down does not exist");
    }

    final String answer =
        chatClient
            .prompt()
            .user(
                u ->
                    u.text(
                            "Given the highlighted text: \"{highlight}\","
                                + " within the material: \"{material}\", "
                                + "answer the question: \"{question}\".")
                        .param("highlight", highlightedText)
                        .param("material", material.get().getMaterialDescription())
                        .param("question", question))
            .call()
            .content();

    final AIMaterialResponse materialResponse =
        new AIMaterialResponse(
            false, question, answer, highlightedText, material.get(), user.getStudent());
    aiResponseRepository.save(materialResponse);
    materialResponseRepository.save(materialResponse);
    return materialResponse;
  }

  /**
   * Allows a student to rate a specific AI material response.
   *
   * @param responseDid the UUID of the AI material response.
   * @param user the authenticated user as {@link Student}
   * @param rateQuestionDTO the rating details.
   * @return the updated {@link AIMaterialResponse}.
   * @throws HttpClientErrorException if the response is not found, unauthorized, or invalid rating.
   */
  @Transactional
  public AiAbstractResponseDTO rateQuestion(
      UUID responseDid, Student user, RateQuestionDTO rateQuestionDTO) {

    final AbstractAIResponse aiResponse =
        aiResponseRepository
            .findByAiResponseDID(responseDid)
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, MESSAGE_NO_AI_RESPONSE));

    if (!aiResponse.getIsPublic()
        && !aiResponse.getOwner().getId().equals(user.getStudent().getId())) {
      throw new HttpClientErrorException(
          HttpStatus.FORBIDDEN, "You do not have permission to rate this resource");
    }
    if (rateQuestionDTO.rate() != 1 && rateQuestionDTO.rate() != 0 && rateQuestionDTO.rate() != -1) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Rate must be 1 or 0 or -1");
    }

    final Optional<List<AIVoteStudent>> previousVote =
        Optional.ofNullable(
            studentVoteAIRepository.findAllByAiResponseIdAndStudentId(aiResponse, user));
    int previousRating = 0;
    if (previousVote.isPresent() && !previousVote.get().isEmpty()) {
      previousRating = previousVote.get().getFirst().getVote();
      previousVote.get().getFirst().setVote(rateQuestionDTO.rate());
      studentVoteAIRepository.save(previousVote.get().getFirst());
    } else {
      final AIVoteStudent studentVoteAI =
          new AIVoteStudent(user, aiResponse, rateQuestionDTO.rate());
      studentVoteAIRepository.save(studentVoteAI);
    }

    aiResponse.setRate(aiResponse.getRate() + rateQuestionDTO.rate() - previousRating);

    aiResponseRepository.save(aiResponse);
    return aiResponse.toDTO(rateQuestionDTO.rate());
  }

  /**
   * Retrieves all stored AI material responses.
   *
   * @return a {@link List} of all {@link AIMaterialResponse} entries.
   */
  @Transactional
  public List<AiAbstractResponseDTO> getAll() {
    List<AbstractAIResponse> responses = aiResponseRepository.findAll();
    return responses.stream().map(aar -> aar.toDTO(0)).toList();
  }

  /**
   * Publishes an AI material response.
   *
   * @param responseDid the UUID of the AI material response.
   * @return the updated {@link AIMaterialResponse}.
   * @throws HttpClientErrorException if the response is not found or already public.
   */
  @Transactional
  public AbstractAIResponse publish(UUID responseDid) {
    final AbstractAIResponse aiResponse =
        aiResponseRepository
            .findByAiResponseDID(responseDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "response doesn't exist"));
    if (aiResponse.getIsPublic()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "response is already public");
    }
    aiResponse.setIsPublic(true);
    aiResponseRepository.save(aiResponse);

    return aiResponse;
  }

  private int getVoteStudent(AbstractAIResponse response, Student student) {
    final List<AIVoteStudent> vote =
        studentVoteAIRepository.findAllByAiResponseIdAndStudentId(response, student);
    if (vote.isEmpty()) {
      return 0;
    }
    return vote.getFirst().getVote();
  }

  private int getVoteUser(AbstractAIResponse response, AbstractUser user) {
    try {
      return getVoteStudent(response, user.getStudent());
    } catch (Exception e) {
      return 0;
    }
  }

  /**
   * Retrieves all published AI material responses for a given material.
   *
   * @param materialDid the UUID of the material.
   * @param principal the authenticated user principal.
   * @return a {@link List} of {@link AiMaterialResponseDTO} representing published responses.
   * @throws HttpClientErrorException if the material is not found.
   */
  @Transactional
  public List<AiAbstractResponseDTO> getAllPublished(UUID materialDid, Object principal) {
    final Optional<Material> material = materialService.getMaterialByDid(materialDid);

    if (material.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, MESSAGE_NO_AI_RESPONSE);
    }
    final List<AbstractAIResponse> publishedRes =
        materialResponseRepository.findByIsPublicTrueAndMaterial(material.get());
    final AbstractUser user = userService.findOrCreateUser(principal);
    return publishedRes.stream()
        .map(
            (AbstractAIResponse r) -> {
              final Integer userVote =
                  user.getRole().equals(new Student().getRole())
                      ? getVoteStudent(r, user.getStudent())
                      : null;
              return r.toDTO(userVote);
            })
        .toList();
  }

  /**
   * Retrieves all private AI responses of a specific user along with all public AI responses.
   *
   * @param student is an authenticated user.
   * @return All the {@link AIMaterialResponse} which can be viewed by a student, i.e., all the
   *     public plus all the private owned by the given student
   */
  @Transactional
  public List<AiAbstractResponseDTO> getAllUserResponses(Student student) {
    final List<AbstractAIResponse> published =
        aiResponseRepository.findAbstractAIResponsesByIsPublicTrue();
    final List<AbstractAIResponse> unpublished =
        aiResponseRepository.findAbstractAIResponseByIsPublicFalseAndOwner(student);
    published.addAll(unpublished);

    return published.stream()
        .map(
            (AbstractAIResponse res) -> {
//              String highlightedText = "";
//              int vote = 0;
//
//              if (res instanceof final AIMaterialResponse matRes) {
//                highlightedText = matRes.getHighlightedText();
//                vote = getVoteStudent(res, student);
//              }

              return res.toDTO(getVoteStudent(res, student));
            })
        .toList();
  }

  /**
   * Generates a new question variant from a list of existing question DTOs.
   *
   * <p>All questions in the provided list must share the same variant index and type; otherwise, a
   * {@code HttpClientErrorException} with status 400 is thrown. The new variant is generated by
   * delegating to the first question’s {@code generateQuestionVariant} method, passing in the
   * shared list of titles.
   *
   * @param questions the list of {@link QuestionDTO} objects from which to generate a variant
   * @return a new {@link QuestionDTO} representing the generated variant
   * @throws HttpClientErrorException if {@code questions} is null or empty, or if not all entries
   *     share the same variant index and type
   */
  public QuestionDTO<?> generateQuestionVariant(List<QuestionDTO<?>> questions) {

    if (questions == null || questions.isEmpty()) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "Input question list must not be null or empty");
    }

    final QuestionDTO<?> first = questions.getFirst();
    final String questionType = first.type();
    final int variantIdx = first.variantIndex();

    final boolean uniform =
        questions.stream()
            .allMatch(q -> q.variantIndex().equals(variantIdx) && q.type().equals(questionType));

    if (!uniform) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "The list of questions must have the same exercise did and type");
    }

    final List<String> questionTitles = questions.stream().map(QuestionDTO::questionTitle).toList();

    return first.generateQuestionVariant(chatClient, questionTitles);
  }

  /**
   * Evaluates whether a user’s answer to a question is correct by consulting the AI.
   *
   * <p>Sends the question, instructor’s reference answer, and the student’s answer to the AI model,
   * which returns a JSON payload indicating correctness and reasoning. If the answer is incorrect,
   * a follow‑up explanation is generated. Non‑instructor users’ evaluations are persisted.
   *
   * @param questionTitle the text of the question being evaluated
   * @param answer the {@link Answer} entity containing the student’s answer
   * @param instructorAnswer the instructor’s reference answer text
   * @param principal the currently authenticated user (used to check an instructor role)
   * @return {@code true} if the student’s answer is correct, {@code false} otherwise
   * @throws HttpClientErrorException if the AI response is null, malformed, or if the user lacks
   *     permission
   */
  public boolean isCorrect(
      String questionTitle, Answer answer, String instructorAnswer, Object principal) {
    final Boolean isCorrect =
            Boolean.valueOf(chatClient
                .prompt()
                .system(
                    s ->
                        s.text(
                            "You are an AI assistant tasked with evaluating whether a student's answer is correct.\n"
                                + "You will be provided with:\n"
                                + "- A question.\n"
                                + "- An example answer from the instructor (this is not the only valid answer).\n"
                                + "- A student's answer.\n\n"
                                + "Your job is to compare the student's answer with the instructor's answer and decide whether the student demonstrates correct understanding.\n"
                                + "The instructor’s answer is a reference, but reasonable variation or equivalent explanation from the student may still be correct.\n\n"
                                + "Respond with a boolean. "
                                + "Think step-by-step before deciding. Prioritize correctness of concepts over exact wording."))
                .user(
                    u ->
                        u.text(
                                "QUESTION: {question} "
                                    + "INSTRUCTOR ANSWER: {instructorAnswer} "
                                    + "STUDENT ANSWER: {studentAnswer} ")
                            .param("question", questionTitle)
                            .param("instructorAnswer", instructorAnswer)
                            .param("studentAnswer", answer.getAnswerContent()))
                .call()
                    .content());

//    if (content.getEntity() == null) {
//      throw new HttpClientErrorException(
//          HttpStatus.BAD_REQUEST, "AI response for feedback cannot be null");
//    }
//
//    if (content.getEntity().isCorrect() == null) {
//      throw new HttpClientErrorException(
//          HttpStatus.BAD_REQUEST, "AI both response for feedback cannot be null or empty");
//    }
//
//    final boolean isCorrect = content.getEntity().isCorrect();


    if (isCorrect == null) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "AI response for feedback cannot be null");
    }
    final String aiReasoning;

    if (!isCorrect) {
      aiReasoning =
          generateAIExplanation(questionTitle, instructorAnswer, answer.getAnswerContent());
    } else {
      aiReasoning =
          chatClient
              .prompt()
              .user(
                  u ->
                      u.text(
                              "Given the question: \"{title}\", "
                                  + "the correct answer is: \"{correct}\"."
                                  + " My answer was: \"{wrongAnswer}\". "
                                  + "Explain why the answer I provided is correct")
                          .param("title", questionTitle)
                          .param("correct", instructorAnswer)
                          .param("wrongAnswer", answer.getAnswerContent()))
              .call()
              .content();
    }

    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      final AIEvaluation eval = new AIEvaluation(answer, isCorrect, aiReasoning);
      aiEvaluationRepository.save(eval);
    }

    return isCorrect;
  }

  /**
   * Retrieves all short‑answer questions and associated user answers for the specified exercise.
   *
   * <p>Queries the repository for short‑answer questions (with their answers and AI responses) by
   * exercise DID, and maps each to a {@link QuestionAndUserAnswerDTO} containing the question
   * title, correct answer, and a list of {@link SHAResponseDTO} entries for each student answer.
   *
   * @param exerciseDid the UUID of the exercise to fetch questions and answers for
   * @return a list of {@link QuestionAndUserAnswerDTO} objects
   * @throws HttpClientErrorException if no exercise or no short‑answer questions are found
   */
  @Transactional
  public List<QuestionAndUserAnswerDTO> getQuestionsAndAnswers(UUID exerciseDid) {
    final Exercise ex =
        exerciseRepository
            .findByExerciseDid(exerciseDid)
            .orElseThrow(
                () ->
                    new HttpClientErrorException(
                        HttpStatus.NOT_FOUND,
                        "The exercise with did " + exerciseDid + " does not exist"));

    final List<ShortAnswerQuestion> questions =
        shortAnswerQuestionRepository.findAllWithAnswersAndResponsesByExerciseId(ex);

    if (questions == null || questions.isEmpty()) {
      throw new HttpClientErrorException(
          HttpStatus.NOT_FOUND, "No short-answer questions found with exercise did " + exerciseDid);
    }

    return questions.stream()
        .map(
            (ShortAnswerQuestion question) -> {
              List<SHAResponseDTO> shaResponseDTOs =
                  question.getAnswers().stream()
                      .flatMap(
                          answer ->
                              answer.getExerciseAIResponse().stream()
                                  .map(
                                      aiResponse ->
                                          new SHAResponseDTO(
                                              aiResponse.getOwner().getName(),
                                              answer.getAnswerContent(),
                                              aiResponse.getAiAnswer())))
                      .toList();

              return new QuestionAndUserAnswerDTO(
                  question.getQuestionTitle(), question.getCorrectAnswer(), shaResponseDTOs);
            })
        .toList();
  }

  /**
   * Requests an AI explanation for a specific answer.
   *
   * <p>If an existing public or private {@link AIExerciseResponse} is found for the answer, it is
   * returned. Otherwise, if the answer is correct or has been evaluated, appropriate errors or
   * existing evaluations are handled. Finally, a new AI response is created and persisted.
   *
   * @param answerDid the UUID of the {@link Answer} for which to ask an explanation
   * @param user the {@link AbstractUser} requesting the explanation
   * @return the {@link AIExerciseResponse} containing the AI’s explanation
   * @throws HttpClientErrorException if the answer is correct or related entities are missing
   */
  @Transactional
  public AIExerciseResponse askAboutAnswer(UUID answerDid, AbstractUser user) {
    final Answer answer = getAnswerOrThrow(answerDid);
    final AbstractQuestion question = getQuestionOrThrow(answer);

    final AIExerciseResponse response =
        aiExerciseResponseRepository.findByAnswer(answer).orElse(null);
    if (response != null) {
      return response;
    }

    if (answer.getAnswerContent().equals(question.getStringAnswer())) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Answer is correct");
    }

    // If SHA and already evaluated, return that
    if (question.isSha()) {
      final AIEvaluation eval = aiEvaluationRepository.findByAnswer(answer).orElse(null);
      if (eval != null) {
        return new AIExerciseResponse(
            false,
            eval.getAiReasoning(),
            user.getStudent(),
            answer.getAnswerContent(),
            answer,
            question,
            eval.getAiEvaluationDid());
      }
    }

    return createAndSaveAIResponse(answer, question, user);
  }

  /**
   * Refreshes the AI explanation for a given answer, generating a new rationale each time.
   *
   * <p>If the answer is correct, an exception is thrown. Otherwise, the AI is queried for a fresh
   * explanation. Existing responses are updated; new ones are created and persisted as needed.
   *
   * @param answerDid the UUID of the {@link Answer} to refresh
   * @param user the {@link AbstractUser} requesting the refresh
   * @return the updated or newly created {@link AIExerciseResponse}
   * @throws HttpClientErrorException if the answer is correct or supporting data is missing
   */
  @Transactional
  public AIExerciseResponse refreshAnswer(UUID answerDid, AbstractUser user) {
    final Answer answer = getAnswerOrThrow(answerDid);
    final AbstractQuestion question = getQuestionOrThrow(answer);

    if (answer.getAnswerContent().equals(question.getStringAnswer())) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Answer is correct");
    }

    final String aiAnswer =
        generateAIExplanation(
            question.getQuestionTitle(),
            question.getStringAnswer(),
            question.isMch()
                ? question.getMch().getChoices().get(Integer.parseInt(answer.getAnswerContent()))
                : answer.getAnswerContent());

    AIExerciseResponse response = aiExerciseResponseRepository.findByAnswer(answer).orElse(null);

    if (response == null) {
      response =
          new AIExerciseResponse(
              false, aiAnswer, user.getStudent(), answer.getAnswerContent(), answer, question);
    } else {
      response.setAiAnswer(aiAnswer);
      response.setIsPublic(false);
    }

    aiResponseRepository.save(response);
    aiExerciseResponseRepository.save(response);
    return response;
  }

  private Answer getAnswerOrThrow(UUID answerDid) {
    return answerRepository
        .findByAnswerDid(answerDid)
        .orElseThrow(
            () -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Answer doesn't exist"));
  }

  private AbstractQuestion getQuestionOrThrow(Answer answer) {
    return questionRepository
        .findByQuestionDid(answer.getQuestionDid())
        .orElseThrow(
            () ->
                new HttpClientErrorException(
                    HttpStatus.BAD_REQUEST, "Question referenced by answer doesn't exist"));
  }

  private String generateAIExplanation(
      String questionTitle, String correctAnswer, String wrongAnswer) {
    return chatClient
        .prompt()
        .user(
            u ->
                u.text(
                        "Given the question: \"{title}\","
                            + " the correct answer is: \"{correct}\". "
                            + "My answer was: \"{wrongAnswer}\". "
                            + "Explain why my answer is incorrect")
                    .param("title", questionTitle)
                    .param("correct", correctAnswer)
                    .param("wrongAnswer", wrongAnswer))
        .call()
        .content();
  }

  private AIExerciseResponse createAndSaveAIResponse(
      Answer answer, AbstractQuestion question, AbstractUser user) {
    final String aiAnswer =
        generateAIExplanation(
            question.getQuestionTitle(),
            question.getStringAnswer(),
            question.isMch()
                ? question.getMch().getChoices().get(Integer.parseInt(answer.getAnswerContent()))
                : answer.getAnswerContent());
    final AIExerciseResponse response =
        new AIExerciseResponse(
            false, aiAnswer, user.getStudent(), answer.getAnswerContent(), answer, question);
    aiResponseRepository.save(response);
    aiExerciseResponseRepository.save(response);
    return response;
  }

  /**
   * Retrieves all publicly visible AI-generated exercise responses for the specified answer.
   *
   * <p>Looks up the {@link Answer} by its DID and its associated {@link AbstractQuestion}, throwing
   * a {@code HttpClientErrorException} with status 400 if either is missing. Returns all {@link
   * AIExerciseResponse} entities that are marked as public and match the question and user answer.
   *
   * @param answerDid the UUID (DID) of the {@link Answer} whose public AI responses are to be
   *     fetched
   * @return a {@link List} of publicly visible {@link AIExerciseResponse} objects
   * @throws HttpClientErrorException if no {@code Answer} or no {@code AbstractQuestion} is found
   */
  @Transactional
  public List<AiAbstractResponseDTO> getPublicAiExerciseResponses(UUID answerDid, AbstractUser user) {
    final Optional<Answer> answer = answerRepository.findByAnswerDid(answerDid);
    if (answer.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Answer doesn't exist");
    }
    final Optional<AbstractQuestion> question =
        questionRepository.findByQuestionDid(answer.get().getQuestionDid());
    if (question.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Question doesn't exist");
    }
    return aiExerciseResponseRepository.findByIsPublicTrueAndQuestionAndUserAnswer(
        question.get(), answer.get().getAnswerContent()).stream().map(
                aer -> aer.toDTO(getVoteUser(aer, user))
    ).toList();
  }
}
