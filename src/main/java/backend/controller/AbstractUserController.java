package backend.controller;

import backend.controller.dto.ApiResponse;
import backend.controller.dto.UserDTO;
import backend.model.AbstractUser;
import backend.service.AbstractUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AbstractUserController {

  private final AbstractUserService abstractUserService;

  public AbstractUserController(AbstractUserService abstractUserService) {
    this.abstractUserService = abstractUserService;
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(
      @AuthenticationPrincipal Object principal) {
    AbstractUser user = abstractUserService.createOrFindUser(principal);
    ApiResponse<UserDTO> body =
        new ApiResponse<>(
            new UserDTO(user.getName(), user.getEmail(), user.getRole()), "User found");
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }
}
