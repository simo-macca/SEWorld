package ch.usi.inf.bsc.sa4.lab02spring.exception;

import ch.usi.inf.bsc.sa4.lab02spring.response.ResponseHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler for the application.
 *
 * <p>This class handles various exceptions thrown by the application and returns appropriate HTTP
 * responses. It is annotated with {@code @RestControllerAdvice} so that it applies to all
 * controllers. The handler methods log the errors and use the {@link ResponseHandler} to generate
 * standardized responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** Regular expression pattern to match and remove HTTP status codes from strings. */
  private static final String REMOVE_STATUS_CODE = "^\\d+\\s+";

  /** Precompiled regular expression pattern to match numeric sequences. */
  private static final Pattern CODE_PATTERN = Pattern.compile("\\d+");

  /** Logger instance for logging messages related to global exception handling. */
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /** Pre-compile the Pattern with the UNICODE_CHARACTER_CLASS flag */
  private static final Pattern REMOVE_STATUS_CODE_PATTERN =
      Pattern.compile(REMOVE_STATUS_CODE, Pattern.UNICODE_CHARACTER_CLASS);

  /**
   * Default constructor for {@link GlobalExceptionHandler}.
   *
   * <p>This constructor is provided to adhere to Java conventions.
   */
  public GlobalExceptionHandler() {
    // Default constructor
  }

  /**
   * Handles {@link DataIntegrityViolationException} exceptions.
   *
   * <p>This method is triggered when a data integrity violation occurs in the database. It logs the
   * exception and returns a {@code 400 Bad Request} response with a message indicating that the
   * provided request body is invalid.
   *
   * @param ex the thrown {@link DataIntegrityViolationException}
   * @return a {@link ResponseEntity} with a standardized error response
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Object> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex) {
    LOGGER.error("Data integrity violation occurred", ex);
    return ResponseHandler.generateResponse(
        "Body passed is invalid", HttpStatus.BAD_REQUEST, false, null, ex.getMessage());
  }

  /**
   * Handles {@link HttpMessageNotReadableException} exceptions.
   *
   * <p>This method is triggered when the HTTP message is not readable, typically due to a missing
   * or malformed request body. It logs the exception and returns a {@code 400 Bad Request} response
   * with a message indicating that the request body is missing.
   *
   * @param ex the thrown {@link HttpMessageNotReadableException}
   * @return a {@link ResponseEntity} with a standardized error response
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Object> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex) {
    LOGGER.error("HttpMessageNotReadableException occurred", ex);
    return ResponseHandler.generateResponse(
        "Body is missing", HttpStatus.BAD_REQUEST, false, null, ex.getMessage());
  }

  /**
   * Handles {@link MethodArgumentTypeMismatchException} exceptions.
   *
   * <p>This method is triggered when a method argument does not match the expected type. It logs
   * the error and returns a {@code 400 Bad Request} response with a message indicating that an
   * invalid parameter was passed.
   *
   * @param ex the thrown {@link MethodArgumentTypeMismatchException}
   * @return a {@link ResponseEntity} with a standardized error response
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    LOGGER.error("Method argument type mismatch occurred", ex);
    return ResponseHandler.generateResponse(
        "Parameter passed is invalid", HttpStatus.BAD_REQUEST, false, null, ex.getMessage());
  }

  /**
   * Handles generic {@link Exception} types.
   *
   * <p>This method catches any unhandled exceptions, logs the error, and attempts to extract an
   * HTTP status code from the exception message. If a valid status code is not found, it defaults
   * to {@code 500 Internal Server Error}. The error message is refined by removing any leading
   * status code and whitespace before generating the response.
   *
   * @param ex the thrown {@link Exception}
   * @return a {@link ResponseEntity} with a standardized error response based on the extracted
   *     status
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGenericException(Exception ex) {
    LOGGER.error("An unexpected error occurred", ex);

    String exMessage = "";
    if (ex.getMessage() != null) {
      exMessage = ex.getMessage();
    }

    final int extractedStatusCode = extractCode(exMessage);
    HttpStatus status = HttpStatus.resolve(extractedStatusCode);
    if (status == null) {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    final String refinedMessage =
        REMOVE_STATUS_CODE_PATTERN.matcher(exMessage).replaceFirst("").trim();
    String defaultMessage = refinedMessage;
    if (refinedMessage.isEmpty()) {
      defaultMessage = getDefaultMessage(status);
    }

    return ResponseHandler.generateResponse(defaultMessage, status, false, null);
  }

  /**
   * Extracts an HTTP status code from the given message.
   *
   * <p>This helper method searches the provided message for a numeric pattern that represents an
   * HTTP status code. If found, it parses and returns the code; otherwise, it returns {@code 500}
   * (Internal Server Error) by default.
   *
   * @param message the message from which to extract the status code
   * @return the extracted HTTP status code, or {@code 500} if extraction fails
   */
  private int extractCode(String message) {
    if (message == null || message.isEmpty()) {
      return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
    final Matcher matcher = CODE_PATTERN.matcher(message);
    if (matcher.find()) {
      try {
        return Integer.parseInt(matcher.group());
      } catch (NumberFormatException e) {
        LOGGER.warn("Failed to parse HTTP status code from message: {}", message, e);
      }
    }
    return HttpStatus.INTERNAL_SERVER_ERROR.value();
  }

  /**
   * Returns a default error message based on the provided HTTP status.
   *
   * <p>This helper method maps certain HTTP status codes to predefined error messages. If the
   * status code is not recognized, it defaults to "Internal Server Error".
   *
   * @param status the {@link HttpStatus} for which a default message is needed
   * @return the default error message corresponding to the given status
   */
  private String getDefaultMessage(HttpStatus status) {
    return switch (status) {
      case BAD_REQUEST -> "Bad Request";
      case UNAUTHORIZED -> "Unauthorized";
      case FORBIDDEN -> "Forbidden";
      case NOT_FOUND -> "Not Found";
      default -> "Internal Server Error";
    };
  }
}
