package backend.controller;

import backend.controller.dto.*;
import backend.model.AbstractUser;
import backend.model.Instructor;
import backend.model.Student;
import backend.service.AbstractUserService;
import backend.service.TopicService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/topic")
public class TopicController {

  private static final String ONLY_INSTRUCTOR = "Only instructors can ";

  private final AbstractUserService abstractUserService;
  private final TopicService topicService;

  public TopicController(AbstractUserService abstractUserService, TopicService topicService) {
    this.abstractUserService = abstractUserService;
    this.topicService = topicService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<TopicDTO>>> getAllTopics() {
    List<TopicDTO> topics = topicService.getAllTopics();
    ApiResponse<List<TopicDTO>> body = new ApiResponse<>(topics, "Topics found");
    return ResponseEntity.ok().body(body);
  }

  @GetMapping("/{slug}")
  public ResponseEntity<ApiResponse<TopicDTO>> getTopicBySlug(
      @PathVariable("slug") String topicSlug) {
    TopicDTO topic = topicService.getTopicBySlug(topicSlug);
    ApiResponse<TopicDTO> body = createBody(topic, "Topic found");
    return ResponseEntity.ok(body);
  }

  @PostMapping
  public ResponseEntity<?> createTopic(
      @AuthenticationPrincipal Object principal, @RequestBody CreateTopicDTO createTopicDTO) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    if (isStudent(user)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(createBody(null, ONLY_INSTRUCTOR + "create topics"));
    }
    Instructor instructor = (Instructor) user;
    topicService.createTopic(instructor, createTopicDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(createBody(null, "Topic created"));
  }

  @PatchMapping("/{slug}")
  public ResponseEntity<?> updateTopic(
      @AuthenticationPrincipal Object principal,
      @PathVariable("slug") String topicSlug,
      @RequestBody CreateTopicDTO topicDTO) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    if (isStudent(user)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(createBody(null, ONLY_INSTRUCTOR + "update topics"));
    }
    topicService.updateTopic(topicSlug, topicDTO);
    return ResponseEntity.ok().body(createBody(null, "Exercise modified"));
  }

  @DeleteMapping
  public ResponseEntity<?> deleteAllTopics(@AuthenticationPrincipal Object principal) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    if (isStudent(user)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(createBody(null, ONLY_INSTRUCTOR + "delete topics"));
    }
    topicService.deleteAllTopics();
    return ResponseEntity.ok().body(createBody(null, "All topics deleted"));
  }

  @DeleteMapping("/{slug}")
  public ResponseEntity<?> deleteTopicBySlug(
      @AuthenticationPrincipal Object principal, @PathVariable("slug") String topicSlug) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    if (isStudent(user)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(createBody(null, ONLY_INSTRUCTOR + "delete topics"));
    }
    topicService.deleteTopicBySlug(topicSlug);
    return ResponseEntity.ok().body(createBody(null, "Topic deleted"));
  }

  private boolean isStudent(AbstractUser user) {
    return user instanceof Student;
  }

  private ApiResponse<TopicDTO> createBody(TopicDTO topicDTO, String message) {
    return new ApiResponse<>(topicDTO, message);
  }
}
