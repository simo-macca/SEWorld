package backend;

import backend.controller.dto.ApiResponse;
import backend.model.AbstractUser;
import backend.model.Instructor;
import backend.model.Student;
import java.util.function.Function;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class Utils {

  private Utils() {
    throw new AssertionError("Utility class cannot be instantiated");
  }

  public static ResponseEntity<?> withInstructor(
      AbstractUser user, String action, Function<Instructor, ResponseEntity<?>> onSuccess) {

    if (user instanceof Student) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(createBody("Only instructors can " + action));
    }
    return onSuccess.apply((Instructor) user);
  }

  public static <T> ApiResponse<T> createBody(T data, String message) {
    return new ApiResponse<>(data, message);
  }

  public static <T> ApiResponse<T> createBody(T data) {
    return new ApiResponse<>(data, null);
  }

  public static <T> ApiResponse<T> createBody(String message) {
    return new ApiResponse<>(null, message);
  }
}
