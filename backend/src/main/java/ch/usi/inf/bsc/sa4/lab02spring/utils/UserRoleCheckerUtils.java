package ch.usi.inf.bsc.sa4.lab02spring.utils;

import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import java.util.List;

/**
 * Utility class providing methods to check user roles, specifically to determine if a user is an
 * instructor.
 *
 * <p>This class maintains a static list of instructor information and offers methods to verify
 * instructor status based on name and email, as well as to check if an authenticated user has the
 * instructor role.
 */
public final class UserRoleCheckerUtils {

  /**
   * A static, immutable list containing predefined instructor information.
   *
   * <p>This list consists of default instructors, each represented by an immutable record {@code
   * InstructorInfo}, which contains the instructor's name and email.
   *
   * <p>The {@code INSTRUCTOR_LIST} is used primarily within {@code UserRoleCheckerUtils} to
   * validate if a given name and email pair correspond to a known instructor.
   *
   * <p>Note: This list is immutable and cannot be modified at runtime.
   */
  private static final List<InstructorInfo> INSTRUCTOR_LIST =
      List.of(
          new InstructorInfo("John Doe", "johndoe@university.edu"),
          new InstructorInfo("Jane Smith", "janesmith@university.edu"),
          new InstructorInfo("Instructor Name", "instructor@example.com"));

  /** Default constructor for {@code UserRoleCheckerUtils}. */
  public UserRoleCheckerUtils() {
    // default constructor
  }

  /**
   * Immutable record representing basic instructor information.
   *
   * @param name the full name of the instructor
   * @param email the email address of the instructor
   */
  public record InstructorInfo(String name, String email) {}

  /**
   * Checks if the provided name and email correspond to an instructor.
   *
   * @param name the name of the user
   * @param email the email of the user
   * @return {@code true} if the user is an instructor; {@code false} otherwise
   */
  public static boolean isInstructor(String name, String email) {
    if (name == null || email == null) {
      return false;
    }
    return INSTRUCTOR_LIST.contains(new InstructorInfo(name, email));
  }

  /**
   * Determines if the authenticated user has the instructor role.
   *
   * @param principal the authenticated principal object, typically obtained from the controller
   *     parameter
   * @param userService the user service used to retrieve or create user information
   * @return {@code true} if the authenticated user has the "INSTRUCTOR" role; {@code false}
   *     otherwise
   */
  public static boolean isAuthenticatedUserInstructor(Object principal, UserService userService) {
    final AbstractUser user = userService.findOrCreateUser(principal);
    return "INSTRUCTOR".equals(user.getRole());
  }
}
