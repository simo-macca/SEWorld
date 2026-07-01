package ch.usi.inf.bsc.sa4.lab02spring.model;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Material Model Test")
class MaterialTests {

  private Topic testTopic;
  private byte[] testContent;

  @BeforeEach
  void setUp() {
    testTopic = new Topic();
    testContent = "Test Content".getBytes(StandardCharsets.UTF_8);
  }

  @Test
  @DisplayName("should have a DID once created with no-args constructor")
  void testEmptyConstructor() {
    Material material = new Material();
    assertNotNull(material.getMaterialDid());
    assertInstanceOf(UUID.class, material.getMaterialDid());
  }

  @Test
  @DisplayName("should correctly initialize all fields with parameterized constructor")
  void testParameterizedConstructor() {
    LocalDateTime testTime = LocalDateTime.now();
    String title = "Test Title";
    String description = "Test Description";
    String type = "pdf";

    Material material = new Material(title, description, testContent, testTime, testTopic, type);

    assertNotNull(material.getMaterialDid());
    assertEquals(title, material.getMaterialTitle());
    assertEquals(description, material.getMaterialDescription());
    assertArrayEquals(testContent, material.getMaterialContent());
    assertEquals(testTime, material.getUploadAt());
    assertEquals(testTopic, material.getTopic());
    assertEquals(type, material.getMaterialType());
  }

  @Test
  @DisplayName("should correctly initialize fields with additional constructor")
  void testAdditionalConstructor() {
    UUID testDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf1");
    String title = "Test Title";
    String description = "Test Description";

    Material material = new Material(testDid, title, description);

    assertEquals(testDid, material.getMaterialDid());
    assertEquals(title, material.getMaterialTitle());
    assertEquals(description, material.getMaterialDescription());
    assertNotNull(material.getMaterialContent());
    assertEquals(0, material.getMaterialContent().length);
    assertNotNull(material.getUploadAt());
    assertNotNull(material.getTopic());
    assertEquals("default", material.getMaterialType());
  }

  @Test
  @DisplayName("should correctly get and set materialTitle")
  void testMaterialTitleGetterAndSetter() {
    Material material = new Material();
    String title = "New Title";

    material.setMaterialTitle(title);

    assertEquals(title, material.getMaterialTitle());
  }

  @Test
  @DisplayName("should correctly get and set materialDescription")
  void testMaterialDescriptionGetterAndSetter() {
    Material material = new Material();
    String description = "New Description";

    material.setMaterialDescription(description);

    assertEquals(description, material.getMaterialDescription());
  }

  @Test
  @DisplayName("should correctly get and set materialContent")
  void testMaterialContentGetterAndSetter() {
    Material material = new Material();
    byte[] content = "New Content".getBytes(StandardCharsets.UTF_8);

    material.setMaterialContent(content);

    assertArrayEquals(content, material.getMaterialContent());
  }

  @Test
  @DisplayName("should correctly get and set materialType")
  void testMaterialTypeGetterAndSetter() {
    Material material = new Material();
    String type = "pdf";

    material.setMaterialType(type);

    assertEquals(type, material.getMaterialType());
  }

  @Test
  @DisplayName("should correctly get and set topic")
  void testTopicGetterAndSetter() {
    Material material = new Material();
    Topic topic = new Topic();

    material.setTopic(topic);

    assertEquals(topic, material.getTopic());
  }

  @Test
  @DisplayName("should set uploadAt to current time on creation")
  void testPrePersistMethod() throws Exception {
    Material material = new Material();

    java.lang.reflect.Method onCreate = Material.class.getDeclaredMethod("onCreate");
    onCreate.setAccessible(true);
    onCreate.invoke(material);

    assertNotNull(material.getUploadAt());
    LocalDateTime now = LocalDateTime.now();
    assertTrue(material.getUploadAt().isAfter(now.minusSeconds(1)));
    assertTrue(material.getUploadAt().isBefore(now.plusSeconds(1)));
  }

  @Test
  @DisplayName("should return correct materialId")
  void testGetMaterialId() {
    Material material = new Material();
    assertNull(material.getMaterialId());
  }

  @Test
  @DisplayName("should fix constructor parameter issues")
  void testConstructorParameterFix() {
    LocalDateTime testTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0, 0));
    Material material =
        new Material("Title", "Description", testContent, testTime, testTopic, "default");

    assertNotNull(material.getMaterialDid());
    assertEquals("Title", material.getMaterialTitle());
    assertEquals("Description", material.getMaterialDescription());
    assertArrayEquals(testContent, material.getMaterialContent());
  }
}
