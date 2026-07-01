package ch.usi.inf.bsc.sa4.lab02spring.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Student Model Test")
class StudentTests {

  private Student student;

  @BeforeEach
  void setUp() {
    student = new Student("test-sub", "Bruce", "mintbruce@example.com");
  }

  @Test
  void constructor_shouldInitializeFields() {
    assertEquals("test-sub", student.getSubId());
    assertEquals("Bruce", student.getName());
    assertEquals("mintbruce@example.com", student.getEmail());
    assertEquals(0, student.getCompletionStage());
    assertEquals(0, student.getNumberAttempts());
    assertNotNull(student.getDid());
  }

  @Test
  void getRole_shouldReturnStudent() {
    assertEquals("STUDENT", student.getRole());
  }

  @Test
  void increaseCompletionStage_shouldIncreaseByOne() {
    student.increaseCompletionStage();
    assertEquals(1, student.getCompletionStage());
  }

  @Test
  void decreaseCompletionStage_shouldNotGoBelowZero() {
    student.decreaseCompletionStage(); // already 0
    assertEquals(0, student.getCompletionStage());

    student.increaseCompletionStage(); // 1
    assertEquals(1, student.getCompletionStage());

    student.decreaseCompletionStage(); // back to 0
    assertEquals(0, student.getCompletionStage());
  }

  @Test
  void increaseNumberAttempts_shouldIncreaseByOne() {
    student.increaseNumberAttempts();
    assertEquals(1, student.getNumberAttempts());
  }

  @Test
  void decreaseNumberAttempts_shouldNotGoBelowZero() {
    student.decreaseNumberAttempts(); // already 0
    assertEquals(0, student.getNumberAttempts());

    student.increaseNumberAttempts(); // 1
    student.decreaseNumberAttempts(); // back to 0
    assertEquals(0, student.getNumberAttempts());
  }
}
