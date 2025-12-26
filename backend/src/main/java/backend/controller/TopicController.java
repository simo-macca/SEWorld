package backend.controller;

import static backend.Utils.createBody;
import static backend.Utils.withInstructor;

import backend.controller.dto.*;
import backend.controller.dto.create.CreateTopicDTO;
import backend.model.AbstractUser;
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

  private final AbstractUserService abstractUserService;
  private final TopicService topicService;

  public TopicController(AbstractUserService abstractUserService, TopicService topicService) {
    this.abstractUserService = abstractUserService;
    this.topicService = topicService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<TopicDTO>>> getAllTopics() {
    List<TopicDTO> topics = topicService.getAllTopics();
    return ResponseEntity.ok().body(createBody(topics, "Topics found"));
  }

  @GetMapping("/{slug}")
  public ResponseEntity<ApiResponse<TopicDTO>> getTopicBySlug(
      @PathVariable("slug") String topicSlug) {
    TopicDTO topic = topicService.getTopicBySlug(topicSlug);
    return ResponseEntity.ok(createBody(topic, "Topic found"));
  }

  @PostMapping
  public ResponseEntity<?> createTopic(
      @AuthenticationPrincipal Object principal, @RequestBody CreateTopicDTO createTopicDTO) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    return withInstructor(
        user,
        "create topics",
        instructor -> {
          TopicDTO newTopic = topicService.createTopic(instructor, createTopicDTO);
          return ResponseEntity.status(HttpStatus.CREATED)
              .body(createBody(newTopic, "Topic created"));
        });
  }

  @PatchMapping("/{slug}")
  public ResponseEntity<?> updateTopic(
      @AuthenticationPrincipal Object principal,
      @PathVariable("slug") String topicSlug,
      @RequestBody CreateTopicDTO topicDTO) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    return withInstructor(
        user,
        "update topic",
        instructor -> {
          TopicDTO updateTopic = topicService.updateTopic(topicSlug, topicDTO);
          return ResponseEntity.ok().body(createBody(updateTopic, "Topic modified"));
        });
  }

  @DeleteMapping
  public ResponseEntity<?> deleteAllTopics(@AuthenticationPrincipal Object principal) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    return withInstructor(
        user,
        "delete topics",
        instructor -> {
          topicService.deleteAllTopics();
          return ResponseEntity.ok().body(createBody("All topics deleted"));
        });
  }

  @DeleteMapping("/{slug}")
  public ResponseEntity<?> deleteTopicBySlug(
      @AuthenticationPrincipal Object principal, @PathVariable("slug") String topicSlug) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    return withInstructor(
        user,
        "delete topics",
        instructor -> {
          topicService.deleteTopicBySlug(topicSlug);
          return ResponseEntity.ok().body(createBody("Topic deleted"));
        });
  }
}
