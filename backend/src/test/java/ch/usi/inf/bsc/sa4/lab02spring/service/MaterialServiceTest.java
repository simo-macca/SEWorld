package ch.usi.inf.bsc.sa4.lab02spring.service;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.MaterialDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.Material;
import ch.usi.inf.bsc.sa4.lab02spring.model.Topic;
import ch.usi.inf.bsc.sa4.lab02spring.repository.MaterialRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.TopicRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@Transactional
@DisplayName("Material Service Tests")
class MaterialServiceTest {
  private final MaterialService materialService;
  private final MaterialRepository materialRepository;
  private final TopicRepository topicRepository;
  private byte[] mockFile;

  private Topic topic;
  private Material newLinkMaterial;
  private Material newMdMaterial;
  private Material newFileMaterial;

  private static final UUID wrongDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf0");

  @Autowired
  public MaterialServiceTest(
      MaterialService materialService,
      TopicRepository topicRepository,
      MaterialRepository materialRepository) {
    this.materialService = materialService;
    this.topicRepository = topicRepository;
    this.materialRepository = materialRepository;
  }

  @BeforeEach
  void setUp() {
    topic = new Topic("New Testing Topic", "New Testing topic description");
    topic = topicRepository.save(topic);

    UUID linkMaterialDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf1");
    newLinkMaterial = new Material(linkMaterialDid, "New link", "https://link.to.org");
    newLinkMaterial.setMaterialType("link");
    newLinkMaterial.setTopic(topic);

    UUID mdMaterialDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf2");
    newMdMaterial = new Material(mdMaterialDid, "New md", "# This is some md content");
    newMdMaterial.setMaterialType("md");
    newMdMaterial.setTopic(topic);

    UUID fileMaterialDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf3");
    newFileMaterial = new Material(fileMaterialDid, "New md", "# This is some md content");
    mockFile = new byte[] {1, 2, 3, 4, 5, 6, 95, 23, 72, -1, -2, -5, -122, 125, 44};
    newFileMaterial.setMaterialType("file");
    newFileMaterial.setMaterialContent(mockFile);
    newFileMaterial.setTopic(topic);

    newLinkMaterial = materialRepository.save(newLinkMaterial);
    newMdMaterial = materialRepository.save(newMdMaterial);
    newFileMaterial = materialRepository.save(newFileMaterial);
  }

  @DisplayName("On material upload")
  @Nested
  class MaterialCreation {

    private MaterialDTO linkDto;
    private MaterialDTO mdDto;
    private MaterialDTO fileDto;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
      linkDto = new MaterialDTO(newLinkMaterial);
      mdDto = new MaterialDTO(newMdMaterial);
      fileDto = new MaterialDTO(newFileMaterial);
      file = new MockMultipartFile("file", "testFile.bin", "application/octet-stream", mockFile);
    }

    @DisplayName("Error scenarios on upload")
    @Nested
    class ErrorCasesOnUpload {
      @DisplayName(
          "Should throw 404 not found when trying to create a material with a non existing topic")
      @Test
      void createMaterialWithNullTopic() {
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () -> materialService.uploadMaterial(linkDto, wrongDid, null));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 Topic not found", exception.getMessage());
      }

      @DisplayName(
          "Should throw 400 bad request when trying to create a material with null material type")
      @Test
      void createMaterialWithEmptyType() {
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () ->
                    materialService.uploadMaterial(
                        new MaterialDTO(
                            newMdMaterial.getMaterialDid(),
                            newMdMaterial.getMaterialTitle(),
                            "",
                            null,
                            null),
                        topic.getDid(),
                        null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("400 Material type cannot be null", exception.getMessage());
      }

      @DisplayName(
          "Should throw 400 bad request when trying to create a material with invalid material type")
      @Test
      void createMaterialWithInvalidType() {
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () ->
                    materialService.uploadMaterial(
                        new MaterialDTO(
                            newMdMaterial.getMaterialDid(),
                            "Title",
                            "Description",
                            "not valid type",
                            null),
                        topic.getDid(),
                        null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("400 Invalid material type", exception.getMessage());
      }

      @DisplayName(
          "Should throw 400 bad request when trying to create a material with null material title")
      @Test
      void createMaterialWithNullTitle() {
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () ->
                    materialService.uploadMaterial(
                        new MaterialDTO(newMdMaterial.getMaterialDid(), null, "", "md", null),
                        topic.getDid(),
                        null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("400 Material title cannot be null", exception.getMessage());
      }

      @DisplayName(
          "Should throw 400 bad request when trying to create a material with blank material title")
      @Test
      void createMaterialWithBlankTitle() {
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () ->
                    materialService.uploadMaterial(
                        new MaterialDTO(newMdMaterial.getMaterialDid(), "", "", "md", null),
                        topic.getDid(),
                        null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("400 Material title cannot be null", exception.getMessage());
      }

      @DisplayName(
          "Should throw 400 bad request when trying to create a link with null description")
      @Test
      void createLinkMaterialWithNullDescription() {
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () ->
                    materialService.uploadMaterial(
                        new MaterialDTO(
                            newMdMaterial.getMaterialDid(),
                            newMdMaterial.getMaterialTitle(),
                            null,
                            "link",
                            null),
                        topic.getDid(),
                        null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("400 Link is required for type 'link'", exception.getMessage());
      }

      @DisplayName(
          "Should throw 400 bad request when trying to create a link with empty description")
      @Test
      void createLinkMaterialWithEmptyDescription() {
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () ->
                    materialService.uploadMaterial(
                        new MaterialDTO(
                            newMdMaterial.getMaterialDid(),
                            newMdMaterial.getMaterialTitle(),
                            "",
                            "link",
                            null),
                        topic.getDid(),
                        null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("400 Link is required for type 'link'", exception.getMessage());
      }

      @DisplayName("Should throw 400 bad request when trying to create a md with empty description")
      @Test
      void createMDMaterialWithEmptyDescription() {
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () ->
                    materialService.uploadMaterial(
                        new MaterialDTO(
                            newMdMaterial.getMaterialDid(),
                            newMdMaterial.getMaterialTitle(),
                            "",
                            "md",
                            null),
                        topic.getDid(),
                        null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("400 Markdown text is required for type 'md'", exception.getMessage());
      }

      @DisplayName("Should throw 400 bad request when trying to create a md with null description")
      @Test
      void createMDMaterialWithNullDescription() {
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class,
                () ->
                    materialService.uploadMaterial(
                        new MaterialDTO(
                            newMdMaterial.getMaterialDid(),
                            newMdMaterial.getMaterialTitle(),
                            null,
                            "md",
                            null),
                        topic.getDid(),
                        null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("400 Markdown text is required for type 'md'", exception.getMessage());
      }

      @DisplayName(
          "Should throw 400 bad request an error when trying to create a file with null file")
      @Test
      void createFileMaterialWithNullFile() {
        UUID topicDid = topic.getDid();
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> materialService.uploadMaterial(fileDto, topicDid, null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("400 File content is required for type 'file'", exception.getMessage());
      }

      @DisplayName(
          "Should throw 400 bad request an error when trying to create a file with empty file")
      @Test
      void createFileMaterialWithEmptyFile() {
        MockMultipartFile emptyFile =
            new MockMultipartFile(
                "file", "testFile.bin", "application/octet-stream", new byte[] {});
        UUID topicDid = topic.getDid();
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> materialService.uploadMaterial(fileDto, topicDid, emptyFile));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("400 File content is required for type 'file'", exception.getMessage());
      }

      @DisplayName("Should throw 500 internal server error when file read fails")
      @Test
      void uploadFileThrowsWhenReadingFails() throws IOException {
        MultipartFile failingFile = mock(MultipartFile.class);
        when(failingFile.isEmpty()).thenReturn(false);
        when(failingFile.getBytes()).thenThrow(new IOException("Cannot read file"));
        UUID did = topic.getDid();
        HttpClientErrorException exception =
            assertThrows(
                HttpClientErrorException.class,
                () -> materialService.uploadMaterial(fileDto, did, failingFile));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("500 Cannot read file", exception.getMessage());
      }
    }

    @DisplayName("Positive creation scenarios")
    @Nested
    class CreationSuccessScenarios {

      @DisplayName("Should create a new link material")
      @Nested
      class CreateNewLinkMaterial {
        private Material linkMaterial;

        @BeforeEach
        void setUp() {
          Optional<Material> optionalMaterial =
              materialService.uploadMaterial(linkDto, topic.getDid(), null);
          assertTrue(optionalMaterial.isPresent(), "Material should be present");
          linkMaterial = optionalMaterial.get();
        }

        @Test
        @DisplayName("Link material should have expected fields")
        void validateLinkMaterial() {
          assertEquals(newLinkMaterial.getTopic().getId(), linkMaterial.getTopic().getId());
          assertEquals(newLinkMaterial.getMaterialTitle(), linkMaterial.getMaterialTitle());
          assertEquals(
              newLinkMaterial.getMaterialDescription(), linkMaterial.getMaterialDescription());
          assertEquals("link", linkMaterial.getMaterialType());
          assertArrayEquals(new byte[0], linkMaterial.getMaterialContent());
        }
      }

      @DisplayName("Should create a new md material")
      @Nested
      class CreateNewMdMaterial {
        private Material mdMaterial;

        @BeforeEach
        void setUp() {
          Optional<Material> optionalMaterial =
              materialService.uploadMaterial(mdDto, topic.getDid(), null);
          assertTrue(optionalMaterial.isPresent(), "Material should be present");
          mdMaterial = optionalMaterial.get();
        }

        @Test
        @DisplayName("MD material should have expected fields")
        void validateMdMaterial() {
          assertEquals(newMdMaterial.getTopic().getId(), mdMaterial.getTopic().getId());
          assertEquals(newMdMaterial.getMaterialTitle(), mdMaterial.getMaterialTitle());
          assertEquals(newMdMaterial.getMaterialDescription(), mdMaterial.getMaterialDescription());
          assertEquals("md", mdMaterial.getMaterialType());
          assertArrayEquals(new byte[0], mdMaterial.getMaterialContent());
        }
      }

      @DisplayName("Should create a new file material")
      @Nested
      class CreateNewFileMaterial {
        private Material fileMaterial;

        @BeforeEach
        void setUp() {
          Optional<Material> optionalMaterial =
              materialService.uploadMaterial(fileDto, topic.getDid(), file);
          assertTrue(optionalMaterial.isPresent(), "Material should be present");
          fileMaterial = optionalMaterial.get();
        }

        @Test
        @DisplayName("File material should have expected fields")
        void validateFileMaterial() {
          assertEquals(newFileMaterial.getTopic().getId(), fileMaterial.getTopic().getId());
          assertEquals(newFileMaterial.getMaterialTitle(), fileMaterial.getMaterialTitle());
          assertEquals(
              newFileMaterial.getMaterialDescription(), fileMaterial.getMaterialDescription());
          assertEquals("file", fileMaterial.getMaterialType());
          assertNotNull(fileMaterial.getMaterialContent());
          assertArrayEquals(
              newFileMaterial.getMaterialContent(), fileMaterial.getMaterialContent());
        }
      }
    }

    @DisplayName("When getting materials")
    @Nested
    class getMaterials {
      @DisplayName("Should return a list of materials")
      @Test
      void getAllMaterialsByTopicTest() {
        List<Material> list = materialService.getMaterialsByTopicDid(topic.getDid());
        assertTrue(list.contains(newLinkMaterial));
        assertTrue(list.contains(newMdMaterial));
        assertTrue(list.contains(newFileMaterial));
      }

      @DisplayName("Should return a single material")
      @Test
      void getSingleMaterialByMaterialDid() {
        Optional<Material> mat = materialService.getMaterialByDid(newFileMaterial.getMaterialDid());
        assertTrue(mat.isPresent(), "Material should be present");
        assertEquals(newFileMaterial, mat.get());
      }
    }

    @DisplayName("When updating materials")
    @Nested
    class UpdatingMaterials {
      private MaterialDTO updateLinkDto;
      private MaterialDTO updateMdDto;
      private MaterialDTO updateFileDto;
      private MultipartFile newFile;

      @BeforeEach
      void setUp() {
        updateLinkDto =
            new MaterialDTO(
                newLinkMaterial.getMaterialDid(),
                "Updated Link Title",
                "https://updated-link.com",
                "link",
                null);
        updateMdDto =
            new MaterialDTO(
                newMdMaterial.getMaterialDid(),
                "Updated MD Title",
                "# Updated markdown content",
                "md",
                null);
        updateFileDto =
            new MaterialDTO(
                newFileMaterial.getMaterialDid(),
                "Updated File Title",
                "Updated file description",
                "file",
                null);
        newFile =
            new MockMultipartFile(
                "file", "updated.bin", "application/octet-stream", new byte[] {7, 8, 9});
      }

      @DisplayName("When updating a link material")
      @Nested
      class UpdateLinkMaterial {
        @DisplayName("Should update link material title and description")
        @Test
        void updateLinkMaterial() {
          Optional<Material> updatedOpt =
              materialService.updateMaterial(newLinkMaterial.getMaterialDid(), updateLinkDto, null);
          assertTrue(updatedOpt.isPresent());
          Material updated = updatedOpt.get();

          assertEquals("Updated Link Title", updated.getMaterialTitle());
          assertEquals("https://updated-link.com", updated.getMaterialDescription());
          assertEquals("link", updated.getMaterialType());
          assertArrayEquals(new byte[0], updated.getMaterialContent());
        }
      }

      @DisplayName("When updating an MD material")
      @Nested
      class UpdateMdMaterial {
        @DisplayName("Should update md material title and description")
        @Test
        void updateMdMaterial() {
          Optional<Material> updatedOpt =
              materialService.updateMaterial(newMdMaterial.getMaterialDid(), updateMdDto, null);
          assertTrue(updatedOpt.isPresent());
          Material updated = updatedOpt.get();

          assertEquals("Updated MD Title", updated.getMaterialTitle());
          assertEquals("# Updated markdown content", updated.getMaterialDescription());
          assertEquals("md", updated.getMaterialType());
          assertArrayEquals(new byte[0], updated.getMaterialContent());
        }
      }

      @DisplayName("When updating a file material")
      @Nested
      class UpdateFileMaterial {
        @DisplayName("Should update file material with new file content")
        @Test
        void updateFileMaterial() {
          Optional<Material> updatedOpt =
              materialService.updateMaterial(updateFileDto.materialDid(), updateFileDto, newFile);
          assertTrue(updatedOpt.isPresent());

          Material updated = updatedOpt.get();
          assertEquals("Updated File Title", updated.getMaterialTitle());
          assertEquals("Updated file description", updated.getMaterialDescription());
          assertEquals("file", updated.getMaterialType());
          assertArrayEquals(new byte[] {7, 8, 9}, updated.getMaterialContent());
        }
      }

      @DisplayName("Edge cases in updating materials")
      @Nested
      class UpdateEdgeCases {
        @DisplayName("Should retain original title if null is passed")
        @Test
        void updateWithNullTitle() {
          MaterialDTO dto =
              new MaterialDTO(
                  newLinkMaterial.getMaterialDid(), null, "New Link Description", "link", null);

          Optional<Material> updatedOpt =
              materialService.updateMaterial(newLinkMaterial.getMaterialDid(), dto, null);
          assertTrue(updatedOpt.isPresent());
          Material updated = updatedOpt.get();

          assertEquals(newLinkMaterial.getMaterialTitle(), updated.getMaterialTitle());
          assertEquals("New Link Description", updated.getMaterialDescription());
        }

        @DisplayName("Should retain original description if null is passed")
        @Test
        void updateWithNullDescription() {
          MaterialDTO dto =
              new MaterialDTO(
                  newLinkMaterial.getMaterialDid(), "New Link Title", null, "link", null);

          Optional<Material> updatedOpt =
              materialService.updateMaterial(newLinkMaterial.getMaterialDid(), dto, null);
          assertTrue(updatedOpt.isPresent());
          Material updated = updatedOpt.get();

          assertEquals("New Link Title", updated.getMaterialTitle());
          assertEquals(newLinkMaterial.getMaterialDescription(), updated.getMaterialDescription());
        }

        @DisplayName("Should retain original file if null file is passed for file type")
        @Test
        void updateFileWithNullFileContent() {
          MaterialDTO dto =
              new MaterialDTO(
                  newFileMaterial.getMaterialDid(), "Title Still", "Still desc", "file", null);

          Optional<Material> updatedOpt =
              materialService.updateMaterial(newFileMaterial.getMaterialDid(), dto, null);
          assertTrue(updatedOpt.isPresent());
          Material updated = updatedOpt.get();

          assertEquals("Title Still", updated.getMaterialTitle());
          assertEquals("Still desc", updated.getMaterialDescription());
          assertArrayEquals(newFileMaterial.getMaterialContent(), updated.getMaterialContent());
        }

        @DisplayName("Should retain original type if blank is passed")
        @Test
        void updateWithBlankType() {
          String originalType = newMdMaterial.getMaterialType();

          MaterialDTO dto =
              new MaterialDTO(
                  newMdMaterial.getMaterialDid(),
                  "MD Updated",
                  "New markdown content",
                  "   ",
                  null);

          Optional<Material> updatedOpt =
              materialService.updateMaterial(newMdMaterial.getMaterialDid(), dto, null);
          assertTrue(updatedOpt.isPresent());

          Material updated = updatedOpt.get();
          assertEquals(originalType, updated.getMaterialType());
          assertEquals("MD Updated", updated.getMaterialTitle());
          assertEquals("New markdown content", updated.getMaterialDescription());
        }

        @DisplayName("Should retain original type if null is passed")
        @Test
        void updateWithNullType() {
          String originalType = newLinkMaterial.getMaterialType();

          MaterialDTO dto =
              new MaterialDTO(
                  newLinkMaterial.getMaterialDid(), "New Title", "New Description", null, null);

          Optional<Material> updatedOpt =
              materialService.updateMaterial(newLinkMaterial.getMaterialDid(), dto, null);
          assertTrue(updatedOpt.isPresent());

          Material updated = updatedOpt.get();
          assertEquals(originalType, updated.getMaterialType());
          assertEquals("New Title", updated.getMaterialTitle());
          assertEquals("New Description", updated.getMaterialDescription());
        }
      }

      @DisplayName("Error scenarios when updating materials")
      @Nested
      class UpdateErrorScenarios {
        @DisplayName("Should throw 404 not found when updating a non-existent material")
        @Test
        void updateNonExistentMaterial() {
          UUID did = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf5");
          MaterialDTO updateDto = new MaterialDTO(did, "Title", "Description", "link", null);

          HttpClientErrorException exception =
              assertThrowsExactly(
                  HttpClientErrorException.class,
                  () -> materialService.updateMaterial(wrongDid, updateDto, null));

          assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
          assertEquals("404 Material not found", exception.getMessage());
        }

        @DisplayName("Should throw 400 bad request when creating material with invalid type")
        @Test
        void updateWithInvalidMaterialType() {
          MaterialDTO dto =
              new MaterialDTO(
                  newLinkMaterial.getMaterialDid(), "Whatever", "Whatever desc", "video", null);

          UUID did = dto.materialDid();

          HttpClientErrorException exception =
              assertThrows(
                  HttpClientErrorException.class,
                  () -> materialService.updateMaterial(did, dto, null));

          assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
          assertEquals("400 Invalid material type", exception.getMessage());
        }

        @DisplayName("Should throw 500 internal server error when file read fails")
        @Test
        void updateFileThrowsWhenReadingFails() throws IOException {
          MultipartFile failingFile = mock(MultipartFile.class);
          when(failingFile.isEmpty()).thenReturn(false);
          when(failingFile.getBytes()).thenThrow(new IOException("Cannot read file"));

          MaterialDTO dto =
              new MaterialDTO(
                  newFileMaterial.getMaterialDid(), "Title", "Description", "file", null);

          UUID did = newFileMaterial.getMaterialDid();

          HttpClientErrorException exception =
              assertThrows(
                  HttpClientErrorException.class,
                  () -> materialService.updateMaterial(did, dto, failingFile));

          assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
          assertEquals("500 Cannot read file", exception.getMessage());
        }
      }
    }

    @DisplayName("When deleting materials")
    @Nested
    class DeletingMaterials {

      @DisplayName("Should delete an existing material")
      @Test
      void deleteMaterialSuccessfully() {
        UUID materialDid = newLinkMaterial.getMaterialDid();

        materialService.deleteMaterial(materialDid);
        Optional<Material> deleted = materialRepository.findByMaterialDid(materialDid);
        assertFalse(deleted.isPresent(), "Material should be deleted");
      }

      @DisplayName("Should throw 404 when trying to delete a non-existent material")
      @Test
      void deleteNonExistentMaterial() {
        HttpClientErrorException exception =
            assertThrowsExactly(
                HttpClientErrorException.class, () -> materialService.deleteMaterial(wrongDid));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 Material not found", exception.getMessage());
      }
    }
  }
}
