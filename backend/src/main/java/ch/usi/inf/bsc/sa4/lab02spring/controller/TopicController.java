package ch.usi.inf.bsc.sa4.lab02spring.controller;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.CompletionTopicDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.SearchTopicDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.TopicDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.Topic;
import ch.usi.inf.bsc.sa4.lab02spring.response.ResponseHandler;
import ch.usi.inf.bsc.sa4.lab02spring.service.TopicService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.UserRoleCheckerUtils;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

/**
 * The Topic Controller
 *
 * <p>The Topic controller manages Topic requests and re-directs those requests to the appropriate
 * {@link TopicService} call
 */
@RestController
@RequestMapping("/api/auth/topic")
class TopicController {
  /** Service for retrieving {@link Topic} details using decentralized identifiers. */
  private final TopicService topicService;

  /** Service for retrieving {@code User} details using decentralized identifiers. */
  private final UserService userService;

  /**
   * Constructs a new {@code TopicController} with the required services.
   *
   * @param userService the service for managing user-related operations
   * @param topicService the service for retrieving topics
   */
  @Autowired
  public TopicController(UserService userService, TopicService topicService) {
    this.userService = userService;
    this.topicService = topicService;
  }

  /**
   * The function returns all {@link Topic}s present in the database
   *
   * @return a {@link ResponseEntity<Object>} with {@link List<Topic>} in the response body, if none
   *     are present it returns an empty {@link List<Topic>}
   * @spec.requires no requirements
   * @spec.effects no effects
   * @spec.role any authenticated user
   */
  @GetMapping
  public ResponseEntity<Object> getAllTopics() {
    return ResponseHandler.generateResponse(
        "Topics found",
        HttpStatus.OK,
        true,
        topicService.wrapperSupplierList(
            topicService::getAllTopics, topicService::wrapTopicsInDto));
  }

  /**
   * The function returns the {@link Topic} matching the did.
   *
   * @param did the {@link Topic} did to match
   * @return A {@link Topic} that matches the Did, otherwise a response error
   * @spec.requires The request only needs the path variable {@code did}.
   *     <p>A missing or null title field will return a bad request, i.e., 400 Bad Request error.
   *     <p>A {@code did} that does not belong to any topic will return a 404 Not Found response
   *     error.
   *     <p>Any other information will be ignored.
   * @spec.effects No effects
   * @spec.role any authenticated user
   */
  @GetMapping("/{did}")
  public ResponseEntity<Object> getTopicByDid(@PathVariable UUID did) {
    return ResponseHandler.generateResponse(
        "Topic found",
        HttpStatus.OK,
        true,
        topicService.wrapperSupplierSingle(
            () -> topicService.getTopicByDid(did), topicService::wrapTopicInDto));
  }

  /**
   * The function returns a {@link List<CompletionTopicDTO>} containing a {@link CompletionTopicDTO}
   * for each existing {@link Topic}
   *
   * @return a {@link List<CompletionTopicDTO>} containing a {@link CompletionTopicDTO}. If there
   *     are no {@link Topic} it returns an empty {@link List<CompletionTopicDTO>}
   * @spec.requires an authenticated user
   * @spec.effects no effects
   * @spec.role any authenticated user
   */
  @GetMapping("/completion")
  public ResponseEntity<Object> getAllCompletionStageTopics(
      @AuthenticationPrincipal Object principal) {
    final List<CompletionTopicDTO> completionTopics =
        topicService.getAllCompletionStageTopics(principal);
    return ResponseHandler.generateResponse(
        "Topics completion found",
        HttpStatus.OK,
        true,
        completionTopics.stream().map(CompletionTopicDTO::new).toList());
  }

  /**
   * The function returns the {@link CompletionTopicDTO} of the {@link Topic} specified by the
   * {@code did}
   *
   * @return a {@link CompletionTopicDTO} if the {@link Topic} doesn't have any {@link Exercise} it
   *     returns a completion of 100 and the flag {@code hasExercises} as false. If the {@link
   *     Topic} doesn't exist it returns a 404 Not Found error response
   * @spec.requires an authenticated user
   * @spec.effects no effects
   * @spec.role any authenticated user
   */
  @GetMapping("/completion/{did}")
  public ResponseEntity<Object> getCompletionStageTopic(
      @AuthenticationPrincipal Object principal, @PathVariable UUID did) {
    final CompletionTopicDTO completionTopic = topicService.getCompletionStageTopic(principal, did);
    return ResponseHandler.generateResponse(
        "Topic completion found", HttpStatus.OK, true, completionTopic);
  }

  /**
   * Route to create a new {@link Topic} in the database
   *
   * <p>Only instructors can call this route
   *
   * @param topicDTO the {@link TopicDTO} used to create a topic
   * @return the newly created {@link Topic} with valid DID.
   * @spec.requires a missing title or description in the request body will result in a 400 Bad
   *     Request, any other information is ignored. An authenticated user.
   * @spec.effects Creates a new Topic in the database
   * @spec.role available only to Instructors
   */
  @PostMapping("/create")
  public ResponseEntity<Object> createTopic(
      @AuthenticationPrincipal Object principal, @RequestBody TopicDTO topicDTO) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "Only Instructors can create a topic");
    }

    return ResponseHandler.generateResponse(
        "Topic created",
        HttpStatus.OK,
        true,
        topicService.wrapperSupplierSingle(
            () -> topicService.createTopic(topicDTO), topicService::wrapTopicInDto));
  }

  /**
   * Route to update an existing {@link Topic}
   *
   * <p>Only instructors can call this route
   *
   * @param topicDTO the {@link Topic} used to create a topic
   * @return the newly created {@link Topic} with valid DID, or an error request.
   * @spec.requires a missing or wrong Did or both a missing title and missing description fields in
   *     the request body will result in a 400 Bad Request; any information outside Did, title and
   *     description is ignored. A did that is not related to an existing {@link Topic} will return
   *     a 404 Not Found response error. An authenticated user
   * @spec.effects Updates a {@link Topic} in the database
   * @spec.role available only to Instructors
   */
  @PatchMapping("/update/{did}")
  public ResponseEntity<Object> updateTopic(
      @AuthenticationPrincipal Object principal,
      @PathVariable UUID did,
      @RequestBody TopicDTO topicDTO) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "Only Instructors can update a topic");
    }

    return ResponseHandler.generateResponse(
        "Topic updated",
        HttpStatus.OK,
        true,
        topicService.wrapperSupplierSingle(
            () -> topicService.updateTopic(did, topicDTO), topicService::wrapTopicInDto));
  }

  /**
   * Route to delete an existing {@link Topic}
   *
   * <p>Only instructors can call this route
   *
   * @param did the {@link Topic} did that is used to find the {@link Topic} to delete
   * @return 200 OK or error response
   * @spec.requires a missing or wrong did will result in a 400 Bad Request, any information outside
   *     did. An authenticated user
   * @spec.effects Deletes a {@link Topic} from the database
   * @spec.role available only to Instructors
   */
  @DeleteMapping("/delete/{did}")
  public ResponseEntity<Object> deleteTopic(
      @AuthenticationPrincipal Object principal, @PathVariable UUID did) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "Only Instructors can delete a topic");
    }

    topicService.deleteTopic(did);
    return ResponseHandler.generateResponse("Topic deleted", HttpStatus.OK, true, null);
  }

  /**
   * The function returns all {@link Topic} that contain at least one keyword in the title and the
   * description, both keyword lists are not case-sensitive.
   *
   * <p>See <a
   * href="https://gitlab.com/usi-si-teaching/bachelor-inf/2025/software-atelier-4/team-1-power-rangers/backend
   * /-/wikis/Documentation/%7Bapi-docs%7D">official API documentation</a> for more details
   *
   * @param searchTopicDTO the {@link SearchTopicDTO} parsed from the request body
   * @return A {@link List<Topic>} that contain the title and the description or an empty list if
   *     none is found
   * @spec.requires The request body requires a {@code titleKeywords} and a {@code
   *     descriptionKeywords} or one of the two or none of the two. If they are provided, they can
   *     be either null or any list of strings.
   *     <p>A missing, null or empty {@code titleKeywords} or {@code descriptionKeywords} field will
   *     default to the empty list `[]`, thus matching any title or description respectively.
   *     <p>Any other field will be ignored. An authenticated user.
   * @spec.effects No effects
   * @spec.role available to any authenticated user
   */
  @PostMapping("/search")
  public ResponseEntity<Object> searchTopicByTitleAndDescription(
      @RequestBody SearchTopicDTO searchTopicDTO) {
    return ResponseHandler.generateResponse(
        "Topics found",
        HttpStatus.OK,
        true,
        topicService.wrapperSupplierList(
            () -> topicService.searchTopicsByTitleAndDescription(searchTopicDTO),
            topicService::wrapTopicsInDto));
  }

  /**
   * Returns the {@link java.util.UUID} of the {@link Topic} that contains the specified exercise.
   *
   * <p>Given the unique identifier (DID) of an exercise, this endpoint finds the corresponding
   * topic that includes this exercise.
   *
   * @param did the {@link java.util.UUID} of the exercise
   * @return a {@link ResponseEntity} containing the DID of the parent topic
   * @spec.requires a valid exercise DID must be provided in the path
   * @spec.effects No side effects; read-only query
   * @spec.role available to any authenticated user
   */
  @GetMapping("/exercises/get_topic_did_by_exercises/{did}")
  public ResponseEntity<Object> getTopicDidWithExercise(@PathVariable UUID did) {
    final UUID didResponse = topicService.getTopicDidByExercise(did);
    return ResponseHandler.generateResponse("Topic found", HttpStatus.OK, true, didResponse);
  }
}
