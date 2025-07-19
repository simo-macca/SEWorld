package backend.controller;

import backend.controller.dto.ApiResponse;
import backend.controller.dto.CreateTopicDTO;
import backend.controller.dto.TopicDTO;
import backend.model.Instructor;
import backend.model.Topic;
import backend.service.AbstractUserService;
import backend.service.TopicService;
import java.security.SecureRandom;
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

  private final SecureRandom secureRandom = new SecureRandom();
  private final TopicService topicService;

  public TopicController(AbstractUserService abstractUserService, TopicService topicService) {
    this.abstractUserService = abstractUserService;
    this.topicService = topicService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<TopicDTO>>> getAllTopics() {
    List<TopicDTO> topics = topicService.getAllTopics();
    ApiResponse<List<TopicDTO>> body =
        new ApiResponse<>(topics,
            "Topics found");
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

  @GetMapping("/{did}")
  public ResponseEntity<ApiResponse<TopicDTO>> getTopic(@PathVariable UUID did) {
    TopicDTO topic = topicService.getTopicByDid(did);
    ApiResponse<TopicDTO> body = new ApiResponse<>(topic, "Topic found");
    return ResponseEntity.status(HttpStatus.OK).body(body);
  }

  @PostMapping
  public ResponseEntity<?> createTopic(
      @AuthenticationPrincipal Object principal, @RequestBody CreateTopicDTO createTopicDTO) {
    Instructor Instructor = (Instructor) abstractUserService.createOrFindUser(principal);
    topicService.createTopic(Instructor, createTopicDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(null, "Topic created"));
  }
}
