package ch.usi.inf.bsc.sa4.lab02spring.response;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for generating standardized HTTP responses in a Spring Boot application.
 *
 * <p>The {@code ResponseHandler} class provides static methods to create consistent response
 * structures It encapsulates response details such as status, status code, message, and data
 * payload into a {@link ResponseEntity} object.
 *
 * <p>This class is designed as a non-instantiable utility class; hence, it has a private
 * constructor that throws an {@link AssertionError} if instantiation is attempted.
 */
public final class ResponseHandler {

  /**
   * Private constructor to prevent instantiation of this utility class.
   *
   * <p>Throws an {@link AssertionError} if called, ensuring that the class cannot be instantiated.
   */
  private ResponseHandler() {
    throw new AssertionError("Cannot instantiate utility class");
  }

  /**
   * Constructs a response map containing standardized response fields.
   *
   * @param message the response message to be included
   * @param statusCode the HTTP status code to be set in the response
   * @param status a boolean indicating success ({@code true}) or failure ({@code false})
   * @param responseObj the data payload to be included in the response
   * @return a {@link Map} containing the structured response fields
   */
  private static Map<String, Object> response(
      String message, HttpStatus statusCode, boolean status, Object responseObj) {
    final Map<String, Object> map = new HashMap<>();
    if (status) {
      map.put("status", "success");
    } else {
      map.put("status", "error");
    }
    map.put("status_code", statusCode.value());
    map.put("message", message);
    map.put("data", responseObj);
    return map;
  }

  /**
   * Generates a standardized HTTP response without a system error message.
   *
   * @param message the response message to be included
   * @param statusCode the HTTP status code to be set in the response
   * @param status a boolean indicating success ({@code true}) or failure ({@code false})
   * @param responseObj the data payload to be included in the response
   * @return a {@link ResponseEntity} containing the structured response
   */
  public static ResponseEntity<Object> generateResponse(
      String message, HttpStatus statusCode, boolean status, Object responseObj) {
    final Map<String, Object> map = response(message, statusCode, status, responseObj);

    return new ResponseEntity<>(map, statusCode);
  }

  /**
   * Generates a standardized HTTP response with an additional system error message.
   *
   * @param message the response message to be included
   * @param statusCode the HTTP status code to be set in the response
   * @param status a boolean indicating success ({@code true}) or failure ({@code false})
   * @param responseObj the data payload to be included in the response
   * @param systemError a detailed system error message to be included
   * @return a {@link ResponseEntity} containing the structured response with system error details
   */
  public static ResponseEntity<Object> generateResponse(
      String message,
      HttpStatus statusCode,
      boolean status,
      Object responseObj,
      String systemError) {
    final Map<String, Object> map = response(message, statusCode, status, responseObj);
    map.put("system_error", systemError);

    return new ResponseEntity<>(map, statusCode);
  }
}
