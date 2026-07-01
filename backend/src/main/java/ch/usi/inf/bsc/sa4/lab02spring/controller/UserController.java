package ch.usi.inf.bsc.sa4.lab02spring.controller;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.UserDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.response.ResponseHandler;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** The controller for users. */
@RestController
@RequestMapping("/api/auth")
public class UserController {

  /** The service layer for user operations. */
  private final UserService userService;

  /**
   * Constructs a new {@code UserController} with the required services.
   *
   * @param userService the service responsible for user operations
   */
  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Returns the list of existing users.
   *
   * @return a list of existing users.
   */
  @GetMapping("/all")
  public ResponseEntity<Object> getUsers() {
    var users = this.userService.getAllUsers();
    return ResponseHandler.generateResponse(
        "", HttpStatus.OK, true, users.stream().map(UserDTO::new).toList());
  }

  /**
   * Returns the user dto with the given did.
   *
   * @param did a path variable containing the user's did.
   * @return a 200 OK if the user exists, a 404 NOT FOUND otherwise.
   */
  @GetMapping("/{did}")
  public ResponseEntity<Object> getUser(@PathVariable UUID did) {
    Optional<AbstractUser> u = this.userService.getByDid(did);
    boolean stat = u.isEmpty();
    return ResponseHandler.generateResponse(
        "", stat ? HttpStatus.NOT_FOUND : HttpStatus.OK, !stat, u.map(UserDTO::new));
  }

  /**
   * Get the user's own profile
   *
   * @param principal the JWT of the request sender
   * @return a 200 OK with the AbstractUser if the user exists
   */
  @GetMapping("/me")
  @ResponseBody
  public ResponseEntity<Object> ownProfile(@AuthenticationPrincipal Object principal) {
    AbstractUser user = userService.findOrCreateUser(principal);
    return ResponseHandler.generateResponse("", HttpStatus.OK, true, new UserDTO(user));
  }

  /**
   * Get the Instructor's own profile This is an API for testing instructor privilege.
   *
   * @param principal the JWT of the request sender
   * @return a 200 OK with the AbstractUser if the user exists and the user is Instructor
   */
  @GetMapping("/instructor/me")
  @ResponseBody
  public ResponseEntity<Object> instructorProfile(@AuthenticationPrincipal Object principal) {
    AbstractUser user = userService.findOrCreateUser(principal);
    if ("INSTRUCTOR".equals(user.getRole())) {
      return ResponseHandler.generateResponse("", HttpStatus.OK, true, new UserDTO(user));
    } else {
      return ResponseHandler.generateResponse(
          "Instructor privileges required.", HttpStatus.FORBIDDEN, true, null);
    }
  }

  /**
   * Searches for a user's name in the system
   *
   * @param partialName a request param with the string to search in the user's name.
   * @return a 200 OK with the list of user dtos matching the query.
   */
  @GetMapping("/search")
  public ResponseEntity<Object> searchUsers(@RequestParam("query") String partialName) {
    return ResponseHandler.generateResponse(
        "",
        HttpStatus.OK,
        true,
        userService.searchUsers(partialName).stream().map(UserDTO::new).toList());
  }
}
