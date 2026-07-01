package ch.usi.inf.bsc.sa4.lab02spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Lab02Spring Spring Boot application.
 *
 * <p>This class is annotated with {@code @SpringBootApplication}, which enables component scanning,
 * autoconfiguration, and property support. The application is started by invoking the {@link
 * SpringApplication#run(Class, String...)} method within the {@code main} method.
 */
@SpringBootApplication
public class Lab02SpringApplication {

  /** Default constructor for {@code Lab02SpringApplication}. */
  public Lab02SpringApplication() {
    // default constructor
  }

  /**
   * Main method that starts the Spring Boot application.
   *
   * @param args command-line arguments passed during application startup
   */
  public static void main(String[] args) {
    SpringApplication.run(Lab02SpringApplication.class, args);
  }
}
