package ch.usi.inf.bsc.sa4.lab02spring.utils;

import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Utility class for user-related operations.
 *
 * <p>This class provides helper methods to handle user authentication and retrieval logic,
 * primarily used in security and user service integrations.
 */
public final class UserUtils {
  /** Private {@link UserUtils} constructor to hide the implicit public one. */
  private UserUtils() {}

  /**
   * Retrieves an {@link AbstractUser} instance based on the provided principal object.
   *
   * <p>If the principal is {@code null} or does not resolve to a valid user, this method throws a
   * {@link HttpClientErrorException} with an appropriate error message and {@link
   * HttpStatus#BAD_REQUEST}.
   *
   * @param principal the authentication principal object, usually from Spring Security
   * @param userService the service used to find or create a user
   * @return the corresponding {@link AbstractUser} instance
   * @throws HttpClientErrorException if the principal is {@code null} or the user is not valid
   */
  public static AbstractUser getUser(Object principal, UserService userService) {
    if (principal == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "no principal provided");
    }
    final AbstractUser user = userService.findOrCreateUser(principal);

    if (user == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "not a valid user");
    }

    return user;
  }
}
