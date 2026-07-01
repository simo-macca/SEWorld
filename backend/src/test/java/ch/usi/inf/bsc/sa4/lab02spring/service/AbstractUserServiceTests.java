package ch.usi.inf.bsc.sa4.lab02spring.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Instructor;
import ch.usi.inf.bsc.sa4.lab02spring.model.Student;
import ch.usi.inf.bsc.sa4.lab02spring.repository.UserRepository;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;

@DisplayName("AbstractUser Service Test")
class AbstractUserServiceTests {

  private UserRepository userRepository;
  private UserService userService;

  private final Student mockUser = new Student("test-sub", "Bruce", "bruce@example.com");

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    userService = new UserService(userRepository);
  }

  @Test
  void getAllUsers_shouldReturnUserList() {
    List<AbstractUser> mockList = List.of(mockUser);
    when(userRepository.findAll()).thenReturn(mockList);

    List<AbstractUser> result = userService.getAllUsers();
    assertEquals(1, result.size());
    assertEquals("Bruce", result.getFirst().getName());

    verify(userRepository, times(1)).findAll();
  }

  @Test
  void getByDid_shouldReturnUserIfFound() {
    UUID did = mockUser.getDid();
    when(userRepository.findByDid(did)).thenReturn(Optional.of(mockUser));

    Optional<AbstractUser> result = userService.getByDid(did);
    assertTrue(result.isPresent());
    assertEquals("test-sub", result.get().getSubId());

    verify(userRepository).findByDid(did);
  }

  @Test
  void getByDid_shouldReturnEmptyIfNotFound() {
    UUID did = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bfc");
    when(userRepository.findByDid(did)).thenReturn(Optional.empty());

    Optional<AbstractUser> result = userService.getByDid(did);
    assertTrue(result.isEmpty());

    verify(userRepository).findByDid(did);
  }

  @Test
  void getBySubId_shouldReturnUserIfFound() {
    when(userRepository.findBySubId("test-sub")).thenReturn(Optional.of(mockUser));

    Optional<AbstractUser> result = userService.getBySubId("test-sub");
    assertTrue(result.isPresent());
    assertEquals("Bruce", result.get().getName());

    verify(userRepository).findBySubId("test-sub");
  }

  @Test
  void getBySubId_shouldReturnEmptyIfNotFound() {
    when(userRepository.findBySubId("unknown-sub")).thenReturn(Optional.empty());

    Optional<AbstractUser> result = userService.getBySubId("unknown-sub");
    assertTrue(result.isEmpty());

    verify(userRepository).findBySubId("unknown-sub");
  }

  @Test
  void searchUsers_shouldReturnMatchingUsers() {
    when(userRepository.findByNameContainingIgnoreCase("Bru")).thenReturn(List.of(mockUser));

    List<AbstractUser> result = userService.searchUsers("Bru");
    assertEquals(1, result.size());
    assertEquals("Bruce", result.getFirst().getName());

    verify(userRepository).findByNameContainingIgnoreCase("Bru");
  }

  @Test
  void searchUsers_shouldReturnEmptyListIfNoMatch() {
    when(userRepository.findByNameContainingIgnoreCase("Nonexistent"))
        .thenReturn(Collections.emptyList());

    List<AbstractUser> result = userService.searchUsers("Nonexistent");
    assertTrue(result.isEmpty());

    verify(userRepository).findByNameContainingIgnoreCase("Nonexistent");
  }

  // ---------------- Tests for findOrCreateUser() ----------------

  /**
   * Test that findOrCreateUser() throws an IllegalStateException when principal is null and there
   * is no authentication available in SecurityContextHolder.
   */
  @Test
  void findOrCreateUser_shouldThrowException_whenNoAuthenticationContext() {
    // Clear SecurityContextHolder to simulate missing authentication
    SecurityContextHolder.clearContext();

    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> userService.findOrCreateUser(null));
    assertEquals("The current request is not authenticated", exception.getMessage());
  }

  /**
   * Test that findOrCreateUser() extracts the principal from SecurityContextHolder when a null
   * principal is provided.
   */
  @Test
  void findOrCreateUser_shouldExtractPrincipalFromSecurityContext_whenPrincipalIsNull() {
    // Create a Jwt token with valid claims representing a student1
    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", "jwt-sub-context")
            .claim("email", "student2@example.com")
            .claim("name", "Student2 Name")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();

    // Create a mock Authentication that returns the Jwt as principal
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(jwt);
    // Create a mock SecurityContext that returns the mock Authentication
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    // Simulate repository returning empty for this sub id
    when(userRepository.findBySubId("jwt-sub-context")).thenReturn(Optional.empty());

    AbstractUser user = userService.findOrCreateUser(null);
    assertNotNull(user);
    // Assuming that isInstructor returns false for student2@example.com
    assertInstanceOf(Student.class, user);
    assertEquals("jwt-sub-context", user.getSubId());
    assertEquals("student2@example.com", user.getEmail());
    assertEquals("Student2 Name", user.getName());

    verify(userRepository).save(user);
  }

  /**
   * Test that findOrCreateUser() creates a Student when provided with a Jwt principal that contains
   * valid claims and the user does not already exist.
   */
  @Test
  void findOrCreateUser_shouldCreateStudent_whenJwtPrincipalAndNotInstructor() {
    // Create a Jwt token with valid claims for a student1
    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", "jwt-sub-student1")
            .claim("email", "student1@example.com")
            .claim("name", "Student Name")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();

    // Simulate repository returning empty for this sub id
    when(userRepository.findBySubId("jwt-sub-student1")).thenReturn(Optional.empty());

    AbstractUser user = userService.findOrCreateUser(jwt);

    // Assert that the returned user is not null and is an instance of Student
    assertNotNull(user);
    assertInstanceOf(Student.class, user);
    assertEquals("jwt-sub-student1", user.getSubId());
    assertEquals("student1@example.com", user.getEmail());
    assertEquals("Student Name", user.getName());

    verify(userRepository).save(user);
  }

  /**
   * Test that findOrCreateUser() creates an Instructor when provided with an OAuth2User principal
   * that contains valid attributes and the role checker determines the user is an instructor.
   */
  @Test
  void findOrCreateUser_shouldCreateInstructor_whenOAuth2UserPrincipalAndIsInstructor() {
    // Create an OAuth2User with valid attributes for an instructor
    OAuth2User oauth2User =
        new OAuth2User() {
          @Override
          public Map<String, Object> getAttributes() {
            return Map.of(
                "sub", "oauth-sub-instructor",
                "email", "instructor@example.com",
                "name", "Instructor Name");
          }

          @Override
          public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.emptyList();
          }

          @Override
          public String getName() {
            return "Instructor Name";
          }
        };

    // Simulate repository returning empty for this sub id
    when(userRepository.findBySubId("oauth-sub-instructor")).thenReturn(Optional.empty());

    AbstractUser user = userService.findOrCreateUser(oauth2User);

    // Assert that the returned user is not null and is an instance of Instructor
    assertNotNull(user);
    assertInstanceOf(Instructor.class, user);
    assertEquals("oauth-sub-instructor", user.getSubId());
    assertEquals("instructor@example.com", user.getEmail());
    assertEquals("Instructor Name", user.getName());

    verify(userRepository).save(user);
  }

  /**
   * Test that findOrCreateUser() throws an IllegalStateException when a Jwt principal is missing
   * required attributes.
   */
  @Test
  void findOrCreateUser_shouldThrowException_whenJwtMissingAttributes() {
    // Create a Jwt token missing the "email" attribute
    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", "jwt-sub-missing")
            // "email" is missing
            .claim("name", "Missing Email")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();

    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> userService.findOrCreateUser(jwt));
    assertEquals(
        "Failed to extract necessary fields from authentication info", exception.getMessage());
  }

  /**
   * Test that findOrCreateUser() throws an IllegalArgumentException when an unsupported principal
   * type is provided.
   */
  @Test
  void findOrCreateUser_shouldThrowException_whenUnsupportedPrincipal() {
    // Use a principal type that is not Jwt or OAuth2User (e.g., String)
    String unsupportedPrincipal = "unsupported";

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.findOrCreateUser(unsupportedPrincipal));
    assertTrue(exception.getMessage().startsWith("Unsupported principal type:"));
  }

  /**
   * Test that findOrCreateUser() returns the existing user from the repository if found, even
   * though the save() method is still invoked.
   */
  @Test
  void findOrCreateUser_shouldReturnExistingUser_whenUserAlreadyExists() {
    // Create a Jwt token with valid claims
    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", "jwt-sub-existing")
            .claim("email", "existing@example.com")
            .claim("name", "Existing AbstractUser")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();

    AbstractUser existingUser =
        new Student("jwt-sub-existing", "Existing AbstractUser", "existing@example.com");

    // Simulate repository returning an existing user
    when(userRepository.findBySubId("jwt-sub-existing")).thenReturn(Optional.of(existingUser));

    AbstractUser user = userService.findOrCreateUser(jwt);

    // Assert that the returned user is the existing user
    assertNotNull(user);
    assertEquals(existingUser, user);

    // Verify that the repository's save method was still called
    verify(userRepository).save(existingUser);
  }
}
