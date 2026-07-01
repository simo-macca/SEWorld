package ch.usi.inf.bsc.sa4.lab02spring.service;

import static ch.usi.inf.bsc.sa4.lab02spring.utils.UserRoleCheckerUtils.isInstructor;

import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Instructor;
import ch.usi.inf.bsc.sa4.lab02spring.model.Student;
import ch.usi.inf.bsc.sa4.lab02spring.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/** A service class to support user management. */
@Service
public class UserService {

  /** Repository for accessing and storing {@link AbstractUser} entities. */
  private final UserRepository userRepository;

  /**
   * Constructs a new {@code UserService} with the required repository.
   *
   * @param userRepository the repository for user entities.
   */
  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Returns all existing users.
   *
   * @return a list of all existing users.
   */
  public List<AbstractUser> getAllUsers() {
    return this.userRepository.findAll();
  }

  /**
   * Looks for a user by its database ID.
   *
   * @param userDid the user ID.
   * @return an optional containing the user if found, otherwise empty.
   */
  public Optional<AbstractUser> getByDid(UUID userDid) {
    return userRepository.findByDid(userDid);
  }

  /**
   * Looks for a user by its subject ID from SWITCH EDU ID.
   *
   * @param userSubId the user ID.
   * @return an optional containing the user if found, otherwise empty.
   */
  public Optional<AbstractUser> getBySubId(String userSubId) {
    return userRepository.findBySubId(userSubId);
  }

  /**
   * Searches for users whose name contains a given string.
   *
   * @param partialName a partial name to search for.
   * @return a list of users whose names contain <code>partialName</code>.
   */
  public List<AbstractUser> searchUsers(String partialName) {
    return userRepository.findByNameContainingIgnoreCase(partialName);
  }

  /**
   * Find or create a AbstractUser from the JWT token
   *
   * @param principal the user from Switch EDU ID (JWT/OAuth2User...)
   * @return a AbstractUser which has the information in the JWT
   */
  public AbstractUser findOrCreateUser(Object principal) {
    // If principal is null, retrieve it from SecurityContextHolder
    if (principal == null) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null) {
        throw new IllegalStateException("The current request is not authenticated");
      }
      principal = authentication.getPrincipal();
    }

    Map<String, Object> attributes;
    if (principal instanceof Jwt jwt) {
      attributes = jwt.getClaims();
    } else if (principal instanceof OAuth2User oauth2User) {
      attributes = oauth2User.getAttributes();
    } else {
      throw new IllegalArgumentException(
          "Unsupported principal type: " + principal.getClass().getName());
    }

    // Extract required information. Make sure the attribute names match the actual data
    String subId = (String) attributes.get("sub");
    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");

    if (subId == null || email == null || name == null) {
      throw new IllegalStateException(
          "Failed to extract necessary fields from authentication info");
    }

    AbstractUser user =
        getBySubId(subId)
            .orElse(
                isInstructor(name, email)
                    ? new Instructor(subId, name, email)
                    : new Student(subId, name, email));
    userRepository.save(user);
    return user;
  }
}
