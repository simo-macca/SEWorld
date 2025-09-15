package backend.controller;

import backend.controller.dto.ApiResponse;
import backend.controller.dto.CreateTopicDTO;
import backend.controller.dto.TopicDTO;
import backend.model.Instructor;
import backend.service.AbstractUserService;
import backend.service.TopicService;
import java.util.List;
import java.util.UUID;
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
    ApiResponse<List<TopicDTO>> body = new ApiResponse<>(topics, "Topics found");
    return ResponseEntity.ok().body(body);
  }

  @GetMapping("/get_by_did/{did}")
  public ResponseEntity<ApiResponse<TopicDTO>> getTopic(@PathVariable("did") UUID topicDid) {
    TopicDTO topic = topicService.getTopicByDid(topicDid);
    ApiResponse<TopicDTO> body = new ApiResponse<>(topic, "Topic found");
    return ResponseEntity.ok().body(body);
  }

  @GetMapping("get_by_slug/{slug}")
  public ResponseEntity<ApiResponse<TopicDTO>> getTopicBySlug(
      @PathVariable("slug") String topicSlug) {
    TopicDTO topic = topicService.getTopicBySlug(topicSlug);
    ApiResponse<TopicDTO> body = new ApiResponse<>(topic, "Topic found");
    return ResponseEntity.ok(body);
  }

  @PostMapping
  public ResponseEntity<?> createTopic(
      @AuthenticationPrincipal Object principal, @RequestBody CreateTopicDTO createTopicDTO) {
    Instructor Instructor = (Instructor) abstractUserService.createOrFindUser(principal);
    topicService.createTopic(Instructor, createTopicDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(null, "Topic created"));
  }
}
