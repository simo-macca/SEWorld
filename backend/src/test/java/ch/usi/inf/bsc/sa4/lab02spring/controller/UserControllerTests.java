package ch.usi.inf.bsc.sa4.lab02spring.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Student;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.JwtTestUtil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
@DisplayName("AbstractUser Controller Test")
class AbstractUserControllerTest {

  @Mock private UserService userService;

  @InjectMocks private UserController userController;

  private static final UUID studentDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf1");
  private static final UUID instructorDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf3");
  private static final UUID userDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf0");

  @Test
  void testOwnProfileWithStudentJwt() {
    Jwt jwt = JwtTestUtil.createStudentJwt();
    Student mockStudent = new Student("student-sub", "student name", "student@example.com");
    mockStudent.setDid(studentDid);

    when(userService.findOrCreateUser(any(Jwt.class))).thenReturn(mockStudent);

    ResponseEntity<Object> response = userController.ownProfile(jwt);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.hasBody());

    verify(userService).findOrCreateUser(jwt);
  }

  @Test
  void testGetUserFound() {
    Student mockStudent = new Student("sub-id", "Test Name", "test@example.com");
    mockStudent.setDid(userDid);

    when(userService.getByDid(userDid)).thenReturn(Optional.of(mockStudent));

    ResponseEntity<Object> response = userController.getUser(userDid);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(userService).getByDid(userDid);
  }

  @Test
  void testGetUserNotFound() {

    when(userService.getByDid(userDid)).thenReturn(Optional.empty());

    ResponseEntity<Object> response = userController.getUser(userDid);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(userService).getByDid(userDid);
  }

  @Test
  void testGetAllUsers() {
    Student student = new Student("sub-id", "Test Name", "test@example.com");
    student.setDid(studentDid);

    when(userService.getAllUsers()).thenReturn(List.of(student));

    ResponseEntity<Object> response = userController.getUsers();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(userService).getAllUsers();
  }

  @Test
  void testInstructorProfileWithInstructorJwt() {
    Jwt jwt = JwtTestUtil.createInstructorJwt();
    AbstractUser mockInstructor = mock(AbstractUser.class);
    when(mockInstructor.getRole()).thenReturn("INSTRUCTOR");
    when(mockInstructor.getDid()).thenReturn(instructorDid);

    when(userService.findOrCreateUser(jwt)).thenReturn(mockInstructor);

    ResponseEntity<Object> response = userController.instructorProfile(jwt);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(userService).findOrCreateUser(jwt);
  }

  @Test
  void testInstructorProfileWithStudentJwt() {
    Jwt jwt = JwtTestUtil.createStudentJwt();
    Student mockStudent = new Student("sub-id", "Test Name", "test@example.com");
    mockStudent.setDid(studentDid);

    when(userService.findOrCreateUser(jwt)).thenReturn(mockStudent);

    ResponseEntity<Object> response = userController.instructorProfile(jwt);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    verify(userService).findOrCreateUser(jwt);
  }

  @Test
  void testSearchUsers() {
    String query = "Test";
    Student student = new Student("sub-id", "Test Name", "test@example.com");
    student.setDid(studentDid);

    when(userService.searchUsers(query)).thenReturn(List.of(student));

    ResponseEntity<Object> response = userController.searchUsers(query);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(userService).searchUsers(query);
  }
}
