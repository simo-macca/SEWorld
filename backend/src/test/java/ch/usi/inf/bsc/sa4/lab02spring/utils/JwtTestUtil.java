package ch.usi.inf.bsc.sa4.lab02spring.utils;

import java.time.Instant;
import org.springframework.security.oauth2.jwt.Jwt;

/** Utility class for generating test JWT tokens and related information. */
public class JwtTestUtil {

  /**
   * Creates a JWT token with the given subject, email, and name.
   *
   * @param sub the subject claim
   * @param email the email claim
   * @param name the name claim
   * @return a Jwt token instance
   */
  public static Jwt createJwt(String sub, String email, String name) {
    return Jwt.withTokenValue("dummyToken")
        .header("alg", "none")
        .claim("sub", sub)
        .claim("email", email)
        .claim("name", name)
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(3600))
        .build();
  }

  /**
   * Creates a JWT token without the 'sub' claim.
   *
   * @param email the email claim
   * @param name the name claim
   * @return a Jwt token instance missing 'sub'
   */
  public static Jwt createJwtWithoutSub(String email, String name) {
    return Jwt.withTokenValue("dummyToken")
        .header("alg", "none")
        .claim("email", email)
        .claim("name", name)
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(3600))
        .build();
  }

  /**
   * Creates a JWT token without the 'email' claim.
   *
   * @param sub the subject claim
   * @param name the name claim
   * @return a Jwt token instance missing 'email'
   */
  public static Jwt createJwtWithoutEmail(String sub, String name) {
    return Jwt.withTokenValue("dummyToken")
        .header("alg", "none")
        .claim("sub", sub)
        .claim("name", name)
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(3600))
        .build();
  }

  /**
   * Creates a JWT token without the 'name' claim.
   *
   * @param sub the subject claim
   * @param email the email claim
   * @return a Jwt token instance missing 'name'
   */
  public static Jwt createJwtWithoutName(String sub, String email) {
    return Jwt.withTokenValue("dummyToken")
        .header("alg", "none")
        .claim("sub", sub)
        .claim("email", email)
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(3600))
        .build();
  }

  /**
   * Creates a JWT token representing a student.
   *
   * @return a Jwt token with student information
   */
  public static Jwt createStudentJwt() {
    return createJwt("student-sub", "student@example.com", "student name");
  }

  /**
   * Creates a JWT token representing an instructor.
   *
   * @return a Jwt token with instructor information
   */
  public static Jwt createInstructorJwt() {
    return createJwt("instructor-sub", "instructor@example.com", "instructor name");
  }

  /**
   * Crea un JWT token rappresentante uno studente, con subject personalizzato.
   *
   * @param sub the subject claim
   * @param email the email claim
   * @param name the name claim
   * @return un Jwt token instance
   */
  public static Jwt createStudentJwt(String sub, String email, String name) {
    return createJwt(sub, email, name);
  }

  /**
   * Creates an InstructorInfo object for testing instructor information. 例如：new
   * InstructorInfo("Instructor Name", "instructor@example.com")
   *
   * @return an InstructorInfo object with predefined instructor details
   */
  public static InstructorInfo createInstructorInfo() {
    return new InstructorInfo("Instructor Name", "instructor@example.com");
  }

  /** Simple helper class to represent instructor information. */
  public static class InstructorInfo {
    private final String name;
    private final String email;

    public InstructorInfo(String name, String email) {
      this.name = name;
      this.email = email;
    }

    public String getName() {
      return name;
    }

    public String getEmail() {
      return email;
    }

    @Override
    public String toString() {
      return "InstructorInfo{name='" + name + "', email='" + email + "'}";
    }
  }
}
