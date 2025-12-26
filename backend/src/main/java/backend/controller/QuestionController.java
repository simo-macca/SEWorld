package backend.controller;

import static backend.Utils.createBody;
import static backend.Utils.withInstructor;

import backend.controller.dto.QuestionDTO;
import backend.controller.dto.UpdateQuestionDTO;
import backend.controller.dto.create.CreateQuestionDTO;
import backend.model.AbstractUser;
import backend.service.AbstractUserService;
import backend.service.QuestionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/question")
public class QuestionController {

  private final AbstractUserService abstractUserService;
  private final QuestionService questionService;

  public QuestionController(
      AbstractUserService abstractUserService, QuestionService questionService) {
    this.abstractUserService = abstractUserService;
    this.questionService = questionService;
  }

  @GetMapping
  public ResponseEntity<?> getAllQuestions() {
    List<QuestionDTO> questions = questionService.getAllQuestions();
    return ResponseEntity.ok().body(createBody(questions, "Questions found"));
  }

  @GetMapping("/{slug}")
  public ResponseEntity<?> getAllQuestionByExercise(@PathVariable("slug") String exerciseSlug) {
    List<QuestionDTO> questions = questionService.getAllQuestionsByExercise(exerciseSlug);
    return ResponseEntity.ok().body(createBody(questions, "Questions found"));
  }

  @PostMapping("/{slug}")
  public ResponseEntity<?> createQuestion(
      @AuthenticationPrincipal Object principal,
      @PathVariable("slug") String exerciseSlug,
      @RequestBody List<CreateQuestionDTO> questionDTO) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    return withInstructor(
        user,
        "create questions",
        instructor -> {
          List<QuestionDTO> createQuestion =
              questionService.createQuestion(exerciseSlug, questionDTO);
          return ResponseEntity.status(HttpStatus.CREATED)
              .body(createBody(createQuestion, "Question added"));
        });
  }

  @PatchMapping("")
  public ResponseEntity<?> updateQuestion(
      @AuthenticationPrincipal Object principal, @RequestBody List<UpdateQuestionDTO> questionDTO) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    return withInstructor(
        user,
        "update questions",
        instructor -> {
          List<QuestionDTO> updateQuestion = questionService.updateQuestions(questionDTO);
          return ResponseEntity.ok().body(createBody(updateQuestion, "Question updated"));
        });
  }

  @DeleteMapping("")
  public ResponseEntity<?> deleteQuestions(
      @AuthenticationPrincipal Object principal, @RequestBody List<String> questionsSlug) {

    AbstractUser user = abstractUserService.createOrFindUser(principal);

    return withInstructor(
        user,
        "delete questions",
        instructor -> {
          questionService.deleteQuestions(questionsSlug);
          return ResponseEntity.ok().body(createBody("Questions deleted"));
        });
  }
}
