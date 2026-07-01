package ch.usi.inf.bsc.sa4.lab02spring.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Instructor Model Test")
class InstructorTests {

  @Test
  void constructor_shouldInitializeFields() {
    Instructor instructor = new Instructor("test-sub", "Bruce", "bruce@example.com");

    assertEquals("test-sub", instructor.getSubId());
    assertEquals("Bruce", instructor.getName());
    assertEquals("bruce@example.com", instructor.getEmail());
    assertNotNull(instructor.getDid());
  }

  @Test
  void getRole_shouldReturnInstructor() {
    Instructor instructor = new Instructor();
    assertEquals("INSTRUCTOR", instructor.getRole());
  }
}
