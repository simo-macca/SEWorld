package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.CompletionTopicDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.SearchTopicDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.TopicDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Attempt;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.Student;
import ch.usi.inf.bsc.sa4.lab02spring.model.Topic;
import ch.usi.inf.bsc.sa4.lab02spring.repository.TopicRepository;
import ch.usi.inf.bsc.sa4.lab02spring.utils.UserUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Service class responsible for handling business logic related to {@link Topic} entities.
 *
 * <p>This service provides methods for retrieving, creating, updating, and managing topics, as well
 * as interacting with related entities such as exercises. It acts as an intermediary between the
 * controller layer and the data access layer (repositories).
 *
 * <p>All transactional and data-related operations involving topics should be encapsulated within
 * this service.
 */
@Service
public class TopicService {
  /**
   * The {@link TopicRepository} class
   *
   * <p>Used to make queries for the model
   */
  private final TopicRepository topicRepository;

  /** Service for retrieving {@link Student} details using decentralized identifiers. */
  private final UserService userService;

  /** Service for retrieving {@link Exercise} details using decentralized identifiers. */
  private final ExerciseService exerciseService;

  /** Service for retrieving {@link Attempt} details using decentralized identifiers. */
  private final AttemptService attemptService;

  /**
   * Constructs a new {@code TopicService} with the specified dependencies.
   *
   * <p>This constructor initializes the service with the provided {@code TopicRepository}, {@code
   * UserService}, {@code ExerciseService}, and {@code AttemptService} to handle business logic
   * related to topics, users, exercises, and attempts.
   *
   * @param topicRepository the repository for managing topics
   * @param userService the service for retrieving user data and feedback
   * @param exerciseService the service for managing exercises
   * @param attemptService the service for managing attempt data related to topics and exercises
   */
  @Autowired
  public TopicService(
      TopicRepository topicRepository,
      UserService userService,
      ExerciseService exerciseService,
      AttemptService attemptService) {
    this.topicRepository = topicRepository;
    this.userService = userService;
    this.exerciseService = exerciseService;
    this.attemptService = attemptService;
  }

  /**
   * Executes a supplier function within a transactional context, then applies a wrapper function to
   * the resulting list.
   *
   * <p>This is useful for chaining a data retrieval operation (e.g., from a repository) with a
   * transformation (e.g., mapping to DTOs), ensuring that any lazy-loaded fields are initialized
   * within a single transaction.
   *
   * @param supplier a {@link Supplier} that provides a {@link List} of elements of type {@code R}
   * @param wrapper a {@link Function} that transforms the list of {@code R} into a list of {@code
   *     F}
   * @return a list of transformed elements of type {@code F}
   * @param <R> the type of elements retrieved by the supplier
   * @param <F> the type of elements returned by the wrapper function
   * @spec.requires {@code supplier} and {@code wrapper} are not null
   * @spec.effects executes the supplier and wrapper within a transaction
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public <R, F> List<F> wrapperSupplierList(
      Supplier<List<R>> supplier, Function<List<R>, List<F>> wrapper) {
    return wrapper.apply(supplier.get());
  }

  /**
   * Executes a supplier function within a transactional context, then applies a wrapper function to
   * the resulting single value.
   *
   * <p>This is useful for chaining a data retrieval operation (e.g., from a repository) with a
   * transformation (e.g., mapping to a DTO), ensuring that any lazy-loaded fields are initialized
   * within a single transaction.
   *
   * @param supplier a {@link Supplier} that provides an object of type {@code R}
   * @param wrapper a {@link Function} that transforms the object of type {@code R} into type {@code
   *     F}
   * @return the transformed object of type {@code F}
   * @param <R> the type of the value retrieved by the supplier
   * @param <F> the type of the value returned by the wrapper function
   * @spec.requires {@code supplier} and {@code wrapper} are not null
   * @spec.effects executes the supplier and wrapper within a transaction
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public <R, F> F wrapperSupplierSingle(Supplier<R> supplier, Function<R, F> wrapper) {
    return wrapper.apply(supplier.get());
  }

  /**
   * Converts a list of {@link Topic} entities into a list of {@link TopicDTO}s within a
   * transactional context.
   *
   * <p>This ensures that any lazily loaded fields (such as materials) are initialized properly
   * during the mapping process.
   *
   * @param topics the list of {@link Topic} entities to be converted
   * @return a list of {@link TopicDTO}s mapped from the input topics
   * @spec.requires the provided list must not be null
   * @spec.effects returns a new list of DTOs with initialized data if required
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public List<TopicDTO> wrapTopicsInDto(List<Topic> topics) {
    return topics.stream().map(TopicDTO::new).toList();
  }

  /**
   * Wraps a single {@link Topic} entity into a {@link TopicDTO} within a transactional context.
   *
   * <p>This ensures that any lazily loaded fields (such as materials) are properly initialized
   * before constructing the DTO.
   *
   * @param topic the {@link Topic} entity to wrap
   * @return the corresponding {@link TopicDTO}
   * @spec.requires the topic must not be null
   * @spec.effects returns a DTO representation of the topic with all necessary fields loaded
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public TopicDTO wrapTopicInDto(Topic topic) {
    return new TopicDTO(topic);
  }

  /**
   * Retrieves all exercises for a given topic or returns an empty list if no exercises are found.
   *
   * <p>This method calls {@link ExerciseService#getAllExercises(UUID, boolean)} and handles the
   * case where no exercises exist for the topic by returning an empty list instead of throwing an
   * exception.
   *
   * @param topicDid the unique identifier of the topic
   * @return a {@link List} of {@link Exercise}s for the given topic, or an empty list if no
   *     exercises are found
   * @throws HttpClientErrorException if an unexpected error occurs while fetching exercises
   * @spec.requires a valid {@link Topic} {@code topicDid} or throws
   * @spec.effects no effects
   */
  private List<Exercise> getExercisesOrEmptyForTopic(UUID topicDid) {
    List<Exercise> exercises = new ArrayList<>();

    try {
      exercises = exerciseService.getAllExercises(topicDid, true);
    } catch (HttpClientErrorException e) {
      // skip empty exercises error
      if (e.getStatusCode() != HttpStatus.NOT_FOUND
          && !e.getStatusText().contains("Exercises not found")) {
        throw e;
      }
    }

    return exercises;
  }

  /**
   * Retrieves all attempts for a specific user and exercise or returns an empty list if no attempts
   * are found.
   *
   * <p>This method calls {@link AttemptService#getAllAttemptsByUserAndExerciseDid(UUID, UUID)} and
   * handles the case where no attempts exist for the user and exercise by returning an empty list
   * instead of throwing an exception.
   *
   * @param userDid the unique identifier of the user
   * @param userName the name of the user
   * @param exerciseDid the unique identifier of the exercise
   * @param exerciseTitle the title of the exercise
   * @return a {@link List} of {@link Attempt}s for the given user and exercise, or an empty list if
   *     no attempts are found
   * @throws HttpClientErrorException if an unexpected error occurs while fetching attempts
   * @spec.requires all parameters to be valid parameters
   * @spec.effects no effects
   */
  private List<Attempt> getAttemptsOrEmptyForUserAndExercise(
      UUID userDid, String userName, UUID exerciseDid, String exerciseTitle) {
    List<Attempt> attempts = new ArrayList<>();
    try {
      attempts = attemptService.getAllAttemptsByUserAndExerciseDid(userDid, exerciseDid);
    } catch (HttpClientErrorException e) {
      // skip empty attempts error
      if (e.getStatusCode() != HttpStatus.NOT_FOUND
          && !e.getStatusText()
              .contains(
                  "There are no attempts related to this user: "
                      + userName
                      + " and this exercise: "
                      + exerciseTitle)) {
        throw e;
      }
    }

    return attempts;
  }

  /**
   * Calculates the number of completed exercises for a given list of exercises by a specific user.
   *
   * <p>This method iterates through the provided exercises and checks if the user has at least one
   * completed attempt for each exercise. The count represents the number of exercises where the
   * user has a completed attempt.
   *
   * @param userDid the unique identifier of the user
   * @param userName the name of the user
   * @param exercises the list of {@link Exercise}s to check for completion
   * @return the number of exercises completed by the user
   * @spec.requires all parameters to be valid parameters
   * @spec.effects no effects
   */
  private int getCompleted(UUID userDid, String userName, Iterable<Exercise> exercises) {
    int completed = 0;

    for (final Exercise exercise : exercises) {
      final List<Attempt> attempts =
          this.getAttemptsOrEmptyForUserAndExercise(
              userDid, userName, exercise.getExerciseDid(), exercise.getExerciseTitle());

      for (final Attempt attempt : attempts) {
        if (attempt.isAttemptIsCompleted()) {
          completed++;
          // next exercise
          break;
        }
      }
    }

    return completed;
  }

  /**
   * It returns a {@link List<CompletionTopicDTO>} containing all {@link Topic}s completion by a
   * {@link AbstractUser}
   *
   * <p>It gets all exercises of a topic by calling {@link ExerciseService#getAllExercises(UUID,
   * boolean)} then it uses the {@link AttemptService#getAllAttemptsByUserAndExerciseDid(UUID,
   * UUID)} to get all attempts of a user on a specific exercise.
   *
   * <p>Finally, if a user has at least one completed attempt, it marks the topic's exercise as
   * completed and calculates the percentage of {@link Exercise} completed of the {@link Topic}
   *
   * @param principal the user principal, which in spring is represented by the {@link Object} class
   * @return a {@link List<CompletionTopicDTO>} where each {@link CompletionTopicDTO} contains:
   *     <ul>
   *       <li>The {@link Topic} identifier: {@code topicId}
   *       <li>The {@link Topic} completion percentage from 0 to 100 included
   *       <li>If an {@link Topic} has no exercises it returns a completion of 100 with a false
   *           {@code hasExercises} flag
   *     </ul>
   *
   * @throws HttpClientErrorException is thrown if one of the following is true:
   *     <ul>
   *       <li>The {@code principal} is null
   *       <li>The {@code principal} is invalid
   *     </ul>
   *
   * @spec.requires a valid principal user
   * @spec.effects no effects
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public List<CompletionTopicDTO> getAllCompletionStageTopics(Object principal)
      throws HttpClientErrorException {
    final AbstractUser user = UserUtils.getUser(principal, userService);

    // Calculate completion for each topic
    final List<CompletionTopicDTO> completionTopics = new ArrayList<>();
    for (final Topic topic : this.getAllTopics()) {
      final List<Exercise> exercises =
          getExercisesOrEmptyForTopic(topic.getDid()).stream()
              .filter(e -> !e.isExerciseIsDraft())
              .collect(Collectors.toList());

      final int exercisesNumber = exercises.size();
      int completed = 0;

      // skip the call to db if there are no exercises
      if (exercisesNumber > 0) {
        completed = getCompleted(user.getDid(), user.getName(), exercises);
      }

      final double MAX_COMPLETION = 100.0;
      if (exercisesNumber == 0) {
        completionTopics.add(
            new CompletionTopicDTO(
                topic.getDid(),
                // An empty exercises topic is always completed
                MAX_COMPLETION,
                false));
      } else {
        completionTopics.add(
            new CompletionTopicDTO(
                topic.getDid(),
                ((double) completed / (double) exercisesNumber) * MAX_COMPLETION,
                true));
      }
    }

    // Return completions
    return completionTopics;
  }

  /**
   * It returns the {@link CompletionTopicDTO} of a {@link Topic} by a {@link AbstractUser}
   *
   * <p>It gets all exercises of a topic by calling {@link ExerciseService#getAllExercises(UUID,
   * boolean)} then it uses the {@link AttemptService#getAllAttemptsByUserAndExerciseDid(UUID,
   * UUID)} to get all attempts of a user on a specific exercise.
   *
   * <p>Finally, if a user has at least one completed attempt, it marks the topic's exercise as
   * completed and calculates the percentage of {@link Exercise} completed of the {@link Topic}
   *
   * @param principal the user principal, which in spring is represented by the {@link Object} class
   * @param topicDid the {@link Topic} {@code DID} of an existing topic
   * @return a {@link CompletionTopicDTO} containing:
   *     <ul>
   *       <li>The {@link Topic} identifier: {@code topicId}
   *       <li>The {@link Topic} completion percentage from 0 to 100 included
   *       <li>If the {@link Topic} has no exercises it returns a completion of 100 with a false
   *           {@code hasExercises} flag
   *     </ul>
   *
   * @throws HttpClientErrorException is thrown if one of the following is true:
   *     <ul>
   *       <li>The {@code principal} is null
   *       <li>The {@code principal} is invalid
   *       <li>The {@code topicDid} is null
   *       <li>The {@code topicDid} is invalid
   *     </ul>
   *
   * @spec.requires a valid principal user, a valid topic DID.
   * @spec.effects no effects
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public CompletionTopicDTO getCompletionStageTopic(Object principal, UUID topicDid)
      throws HttpClientErrorException {
    final AbstractUser user = UserUtils.getUser(principal, userService);

    final List<Exercise> exercises =
        getExercisesOrEmptyForTopic(topicDid).stream()
            .filter(e -> !e.isExerciseIsDraft())
            .collect(Collectors.toList());

    final int exercisesNumber = exercises.size();
    int completed = 0;

    // skip the call to db if there are no exercises
    if (exercisesNumber > 0) {
      completed = getCompleted(user.getDid(), user.getName(), exercises);
    }

    final double MAX_COMPLETION = 100.0;
    if (exercisesNumber == 0) {
      return new CompletionTopicDTO(topicDid, MAX_COMPLETION, false);
    } else {
      return new CompletionTopicDTO(
          topicDid, ((double) completed / (double) exercisesNumber) * MAX_COMPLETION, true);
    }
  }

  /**
   * It creates a {@link Topic} in the database.
   *
   * <p>The function creates the entity in the database by calling the {@link
   * TopicRepository#save(Object)}} method.
   *
   * @param topicDto the {@link TopicDTO} with the title and description of the new topic
   * @return a {@link Topic} containing:
   *     <ul>
   *       <li>The Topic identifier: {@code topicId}
   *       <li>The Topic decentralized identifier {@code topicDid}
   *       <li>The Topic title {@code topicTitle}.
   *       <li>The Topic description {@code topicDescription}.
   *     </ul>
   *
   * @throws HttpClientErrorException is thrown if one of the following is true:
   *     <ul>
   *       <li>The {@code topicDto} is null
   *       <li>The {@code topicDto.title()} is null
   *       <li>The {@code topicDto.description()} is null
   *     </ul>
   *
   * @spec.requires a Topic DTO with both a valid title and description, any other field may be
   *     invalid and null
   * @spec.effects Creates a new Topic in the database
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Topic createTopic(TopicDTO topicDto) throws HttpClientErrorException {
    // Better feedback for the client
    if (topicDto == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Request body is missing");
    }

    if (topicDto.title() == null && topicDto.description() == null) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "Both title and description are null");
    }

    if (topicDto.title() == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "title is null");
    }

    if (topicDto.description() == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "description is null");
    }

    // We don't care about did to create a new topic
    final Topic topic = new Topic(topicDto.title(), topicDto.description());
    // Save it in the db
    return topicRepository.save(topic);
  }

  /**
   * It updates a {@link Topic} title and/or description based on its DID.
   *
   * <p>If the DID is null or not valid or both the new title and description are null, it throws an
   * error, see below.
   *
   * <p>It requires a valid DTO or it throws.
   *
   * @param topicDto the {@link TopicDTO} with the title and/or description to update
   * @param did the DID ({@link UUID}) of the topic to update
   * @return a {@link Topic} containing:
   *     <ul>
   *       <li>The Topic identifier: {@code topicId}
   *       <li>The Topic decentralized identifier {@code topicDid}
   *       <li>The possibly new Topic title {@code topicTitle}.
   *       <li>The possibly new Topic description {@code topicDescription}.
   *     </ul>
   *
   * @throws HttpClientErrorException is thrown if one of the following is true:
   *     <ul>
   *       <li>The {@code topicDto} is null
   *       <li>The {@code topicDto.title()} and {@code topicDto.description()} are null
   *       <li>The {@code topicDto.did()} is null
   *       <li>No Topic matches the {@code topicDto.did()}
   *     </ul>
   *
   * @spec.requires a Topic DTO with the Did of the topic to update, the new title, and the new
   *     description. If one between title and description is null, we update only the other value.
   *     If both title and description are null, we do not change the Topic, and we throw an error.
   *     <p>It requires a valid DTO, DID matching a {@link Topic} or it throws.
   * @spec.effects Updates the Topic matching the Did in the database
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Topic updateTopic(UUID did, TopicDTO topicDto) throws HttpClientErrorException {
    // we need the dto to exist and did to exists to update
    if (topicDto == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Request body is missing");
    }

    if (topicDto.title() == null && topicDto.description() == null) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "Both title and description are null");
    }

    final Topic topic = this.getTopicByDid(did);

    // We update either the title or the description or both
    final Topic newTopic;
    if (topicDto.title() == null) {
      newTopic = new Topic(topic.getId(), topic.getDid(), topic.getTitle(), topicDto.description());
    } else if (topicDto.description() == null) {
      newTopic = new Topic(topic.getId(), topic.getDid(), topicDto.title(), topic.getDescription());
    } else {
      newTopic = new Topic(topic.getId(), topic.getDid(), topicDto.title(), topicDto.description());
    }

    // Save it in the db and return it
    return topicRepository.save(newTopic);
  }

  /**
   * It deletes a Topic in the database
   *
   * <p>If did is null, or it is not referring to a valid topic, it throws
   *
   * @param did the topic DID ({@link UUID}) to be deleted
   * @throws HttpClientErrorException is thrown if one of the following is true:
   *     <ul>
   *       <li>The {@code did} is null
   *       <li>No Topic matches the {@code did}
   *       <li>The {@link TopicRepository#delete(Object)} call fails
   *     </ul>
   *
   * @spec.requires a valid Did referring to the topic to be deleted
   * @spec.effects Deletes the {@code did} matching {@link Topic} from the database
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public void deleteTopic(UUID did) throws HttpClientErrorException {
    // Get the topic
    final Topic topic = this.getTopicByDid(did);

    // Delete it from the db
    topicRepository.delete(topic);
  }

  /**
   * The function returns a {@link Topic} matching the id if found. Otherwise, it throws
   *
   * @param id the id of the topic to be returned
   * @return A {@link Topic} with all its fields
   * @throws HttpClientErrorException is thrown if one of the following is true:
   *     <ul>
   *       <li>The {@code id} is null
   *       <li>No Topic matches the {@code id}
   *     </ul>
   *
   * @spec.requires The function requires a valid id, for an invalid id, or an id that is not
   *     related to an existing topic it throws
   * @spec.effects No effects
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Topic getTopicById(Long id) throws HttpClientErrorException {
    // Invalid id
    if (id == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "id is null");
    }

    final Optional<Topic> optTopic = this.topicRepository.findByTopicId(id);

    if (optTopic.isEmpty()) {
      throw new HttpClientErrorException(
          HttpStatus.NOT_FOUND, "No Topic found by the id: ".concat(id.toString()));
    }

    return optTopic.get();
  }

  /**
   * The function returns a topic matching the Did if found. Otherwise, it throws.
   *
   * @param did the topic DID to be returned.
   * @return A {@link Topic} with all its fields
   * @throws HttpClientErrorException is thrown if one of the following is true:
   *     <ul>
   *       <li>The {@code did} is null
   *       <li>No Topic matches the {@code did}
   *     </ul>
   *
   * @spec.requires The function requires a valid Did, for an invalid Did, or a Did that is not
   *     related to an existing topic it throws
   * @spec.effects No effects
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Topic getTopicByDid(UUID did) throws HttpClientErrorException {
    // Invalid did
    if (did == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "did is null");
    }

    final Optional<Topic> optTopic = this.topicRepository.findByTopicDid(did);

    if (optTopic.isEmpty()) {
      throw new HttpClientErrorException(
          HttpStatus.NOT_FOUND, "No Topic found by the did: ".concat(did.toString()));
    }

    return optTopic.get();
  }

  /**
   * The function returns all {@link Topic}s that are present in the database
   *
   * @return all {@link Topic}
   * @spec.requires No requirements
   * @spec.effects No effects
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public List<Topic> getAllTopics() {
    return this.topicRepository.findAll();
  }

  /**
   * The function returns all {@link Topic}s that contain at least one keyword in the title and the
   * description, both keyword lists are not case-sensitive.
   *
   * <p>It requires a valid {@code searchTopicDTO} or it throws.
   *
   * @param searchTopicDTO the dto for the search
   * @return A list of {@link Topic}s that contain at least one keyword of title keywords in the
   *     title and at least one keyword of description keywords in the description
   * @throws HttpClientErrorException is thrown if one of the following is true:
   *     <ul>
   *       <li>The {@code searchTopicDTO} is null
   *     </ul>
   *
   * @spec.requires requirements are:
   *     <ul>
   *       <li>Two String lists of keywords to perform the match with titles and descriptions
   *       <li>A null or empty title keywords list or description keyword list will match any title
   *           or description respectively
   *       <li>It requires a valid {@code searchTopicDTO} or it throws.
   *     </ul>
   *
   * @spec.effects No effects
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public List<Topic> searchTopicsByTitleAndDescription(SearchTopicDTO searchTopicDTO)
      throws HttpClientErrorException {
    if (searchTopicDTO == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Body is missing");
    }

    return this.topicRepository.findByTitleAndDescriptionKeywords(
        searchTopicDTO.titleKeywords(), searchTopicDTO.descriptionKeywords());
  }

  /**
   * Retrieves the unique identifier (DID) of the topic associated with the given exercise DID.
   *
   * <p>This method first attempts to find the {@code Exercise} with the specified DID using the
   * {@code exerciseService}. If the exercise is found, it delegates to {@link #getDid(Exercise)} to
   * retrieve the associated topic's DID.
   *
   * @param did the DID of the exercise
   * @return the DID of the associated topic
   * @throws HttpClientErrorException if no exercise with the specified DID is found
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public UUID getTopicDidByExercise(UUID did) throws HttpClientErrorException {
    final Exercise ex =
        exerciseService
            .getExerciseByDid(did, true)
            .orElseThrow(
                () ->
                    new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, "No exercise with this did"));

    return getDid(ex);
  }

  /**
   * Retrieves the unique identifier (DID) of the topic associated with the given {@code Exercise}.
   *
   * <p>This method looks up the {@code Topic} by the internal topic ID present in the {@code
   * Exercise}. If found, it returns the topic's DID.
   *
   * @param ex the exercise whose associated topic DID is to be retrieved
   * @return the DID of the associated topic
   * @throws HttpClientErrorException if no topic is found for the exercise
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public UUID getDid(Exercise ex) {
    final Topic t =
        topicRepository
            .findByTopicId(ex.getTopic().getId())
            .orElseThrow(
                () ->
                    new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, "No topic found with such exercise"));

    return t.getDid();
  }
}
