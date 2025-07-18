package backend.controller;

import backend.controller.dto.UserDTO;
import backend.model.AbstractUser;
import backend.service.AbstractUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class AbstractUserController {

  private final AbstractUserService abstractUserService;

  public AbstractUserController(AbstractUserService abstractUserService) {
    this.abstractUserService = abstractUserService;
  }

  @GetMapping("/me")
  public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal Object principal) {
    AbstractUser user = abstractUserService.createUser(principal);
    return new ResponseEntity<>(
        new UserDTO(user.getName(), user.getEmail(), user.getRole()), HttpStatus.CREATED);
  }
}
