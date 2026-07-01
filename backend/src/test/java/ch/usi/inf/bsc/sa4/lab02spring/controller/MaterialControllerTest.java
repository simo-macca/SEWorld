package ch.usi.inf.bsc.sa4.lab02spring.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.MaterialDTO;
import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.TopicDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.AbstractUser;
import ch.usi.inf.bsc.sa4.lab02spring.model.Material;
import ch.usi.inf.bsc.sa4.lab02spring.service.MaterialService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.JwtTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("Material Controller Test")
class MaterialControllerTest {

  @MockitoBean private MaterialService materialService;

  @MockitoBean private UserService userService;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  final String baseUrl = "/api/auth/material";
  final UUID randomUUID = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf0");

  // Test fixtures for the controller
  static MaterialDTO materialDTO;
  static Material material;
  static TopicDTO topicDTO;
  static TopicDTO noMaterialsTopicDTO;
  static MockMultipartFile mockMultipartFile;

  @BeforeAll
  static void dataSetUp() {
    UUID materialDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf1");
    materialDTO = new MaterialDTO(materialDid, "Test Title", "Test Description", "PDF", null);
    mockMultipartFile =
        new MockMultipartFile(
            "file",
            "test-file.pdf",
            "application/pdf",
            "test content".getBytes(StandardCharsets.UTF_8));
    UUID topicDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf2");
    topicDTO = new TopicDTO(topicDid, "Title", "Description");
    material =
        new Material(materialDTO.materialDid(), materialDTO.title(), materialDTO.description());
    UUID noMaterialTopicd = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf3");
    noMaterialsTopicDTO = new TopicDTO(noMaterialTopicd, "Title", "Description");
  }

  @BeforeEach
  void mockSetUp() {
    materialService.deleteMaterial(materialDTO.materialDid());
    given(materialService.updateMaterial(materialDTO.materialDid(), materialDTO, mockMultipartFile))
        .willReturn(Optional.of(material));
    doThrow(
            new HttpClientErrorException(
                HttpStatus.BAD_REQUEST, "File content is required for type 'file'"))
        .when(materialService)
        .updateMaterial(materialDTO.materialDid(), materialDTO, null);
    doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Material not found"))
        .when(materialService)
        .updateMaterial(eq(randomUUID), any(), any(MultipartFile.class));

    doNothing().when(materialService).deleteMaterial(materialDTO.materialDid());
    doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Material not found"))
        .when(materialService)
        .deleteMaterial(randomUUID);
    given(materialService.getMaterialsByTopicDid(noMaterialsTopicDTO.did()))
        .willReturn(Collections.emptyList());
    given(materialService.getMaterialsByTopicDid(topicDTO.did())).willReturn(List.of(material));
    given(materialService.getMaterialByDid(materialDTO.materialDid()))
        .willReturn(Optional.of(material));
    given(materialService.getMaterialByDid(randomUUID)).willReturn(Optional.empty());
  }

  @DisplayName("All users endpoints")
  @Nested
  class AllUserEndpoints {

    Jwt jwt;
    AbstractUser mockUser;
    FakeUser fakeUser;

    @BeforeEach
    void setUp() {
      jwt = JwtTestUtil.createStudentJwt();
      mockUser = mock(AbstractUser.class);
      UUID mockUserDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf1");
      when(mockUser.getRole()).thenReturn("STUDENT");
      when(mockUser.getDid()).thenReturn(mockUserDid);
      when(userService.findOrCreateUser(any())).thenReturn(mockUser);
      fakeUser =
          new FakeUser(
              mockUser.getDid(), mockUser.getSubId(), mockUser.getName(), mockUser.getEmail());
    }

    @DisplayName("Should return a list of materials for a topic")
    @Test
    void getAllMaterialsByTopicTest() throws Exception {
      mockMvc
          .perform(
              get(baseUrl.concat("/" + topicDTO.did() + "/all_materials"))
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Materials found"))
          .andExpect(jsonPath("$.status").value("success"))
          .andExpect(jsonPath("$.data").isArray());
    }

    @DisplayName("Should return a single materials for correct material did")
    @Test
    void getSingleMaterialsByDidTest() throws Exception {
      mockMvc
          .perform(
              get(baseUrl.concat("/" + materialDTO.materialDid()))
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value("Material found"))
          .andExpect(jsonPath("$.status").value("success"))
          .andExpect(jsonPath("$.data").exists());
    }

    @DisplayName("Should throw 404 not found for a topic which doesn't have materials")
    @Test
    void getAllMaterialsByTopicWithNoMaterialsTest() throws Exception {
      mockMvc
          .perform(
              get(baseUrl.concat("/" + noMaterialsTopicDTO.did() + "/all_materials"))
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("No materials found for this topic"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should throw 404 not found if there is no material with such DID")
    @Test
    void getSingleMaterialByMaterialDid() throws Exception {
      mockMvc
          .perform(
              get(baseUrl.concat("/" + randomUUID))
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("No material found with such did"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should allow downloading a file material")
    @Test
    void downloadFileMaterial() throws Exception {
      UUID fileDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf4");
      Material fileMaterial = new Material(fileDid, "FileMaterial", "File description");
      fileMaterial.setMaterialType("file");
      fileMaterial.setMaterialContent("downloadable content".getBytes(StandardCharsets.UTF_8));

      given(materialService.getMaterialByDid(any(UUID.class)))
          .willReturn(Optional.of(fileMaterial));

      mockMvc
          .perform(
              get(baseUrl.concat("/" + fileMaterial.getMaterialDid() + "/download"))
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
          .andExpect(status().isOk())
          .andExpect(
              header()
                  .string(
                      HttpHeaders.CONTENT_DISPOSITION,
                      "attachment; filename=\"" + fileMaterial.getMaterialTitle() + "\""))
          .andExpect(content().bytes("downloadable content".getBytes(StandardCharsets.UTF_8)));
    }

    @DisplayName("Should throw 404 when trying to download a material with no such DID")
    @Test
    void downloadMaterialByMaterialDid() throws Exception {
      mockMvc
          .perform(
              get(baseUrl.concat("/" + randomUUID + "/download"))
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Material not found"))
          .andExpect(jsonPath("$.status").value("error"));
    }

    @DisplayName("Should throw 400 bad request when downloading a non-file material")
    @Test
    void downloadNonFileMaterialShouldFail() throws Exception {
      UUID materialDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf5");
      Material nonFileMaterial = new Material(materialDid, "NonFile", "Not a file");
      nonFileMaterial.setMaterialType("link");
      given(materialService.getMaterialByDid(nonFileMaterial.getMaterialDid()))
          .willReturn(Optional.of(nonFileMaterial));

      mockMvc
          .perform(
              get(baseUrl.concat("/" + nonFileMaterial.getMaterialDid() + "/download"))
                  .with(SecurityMockMvcRequestPostProcessors.authentication(fakeUser.getToken())))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value("Cannot download a non file material"))
          .andExpect(jsonPath("$.status").value("error"));
    }
  }

  @DisplayName("When the call is made by an instructor")
  @Nested
  class InstructorEndpoints {
    Jwt jwt;
    AbstractUser mockInstructor;
    FakeUser fakeInstructor;

    @BeforeEach
    void setUp() {
      jwt = JwtTestUtil.createStudentJwt();
      mockInstructor = mock(AbstractUser.class);
      UUID instructorDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf6");
      when(mockInstructor.getRole()).thenReturn("INSTRUCTOR");
      when(mockInstructor.getDid()).thenReturn(instructorDid);
      when(userService.findOrCreateUser(any())).thenReturn(mockInstructor);
      fakeInstructor =
          new FakeUser(
              mockInstructor.getDid(),
              mockInstructor.getSubId(),
              mockInstructor.getName(),
              mockInstructor.getEmail());
    }

    @DisplayName("Upload Endpoints")
    @Nested
    class UploadEndpoints {

      @DisplayName("Should upload a non-file material")
      @Test
      void uploadNonFileMaterial() throws Exception {
        given(materialService.uploadMaterial(any(), eq(topicDTO.did()), isNull()))
            .willReturn(Optional.of(material));

        mockMvc
            .perform(
                post(baseUrl.concat("/" + topicDTO.did() + "/upload"))
                    .content(objectMapper.writeValueAsString(materialDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(
                        SecurityMockMvcRequestPostProcessors.authentication(
                            fakeInstructor.getToken())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("Material uploaded"))
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data").exists());
      }

      @DisplayName("Should upload a file material")
      @Test
      void uploadFileMaterial() throws Exception {
        given(materialService.uploadMaterial(any(), eq(topicDTO.did()), any(MultipartFile.class)))
            .willReturn(Optional.of(material));

        mockMvc
            .perform(
                multipart(HttpMethod.POST, baseUrl.concat("/" + topicDTO.did() + "/upload_file"))
                    .file(mockMultipartFile)
                    .file(
                        new MockMultipartFile(
                            "materialDTO",
                            null,
                            "application/json",
                            objectMapper.writeValueAsBytes(materialDTO)))
                    .with(
                        SecurityMockMvcRequestPostProcessors.authentication(
                            fakeInstructor.getToken())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("Material uploaded"))
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data").exists());
      }
    }

    @DisplayName("Update Endpoint")
    @Nested
    class UpdateEndpoints {
      @DisplayName("Should update a material")
      @Test
      void updateMaterial() throws Exception {
        mockMvc
            .perform(
                multipart(HttpMethod.PATCH, baseUrl.concat("/update/" + materialDTO.materialDid()))
                    .file(mockMultipartFile)
                    .file(
                        new MockMultipartFile(
                            "materialDTO",
                            null,
                            "application/json",
                            objectMapper.writeValueAsBytes(materialDTO)))
                    .with(
                        SecurityMockMvcRequestPostProcessors.authentication(
                            fakeInstructor.getToken())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Material updated"))
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data").exists());
      }
    }

    @DisplayName("Delete Endpoint")
    @Nested
    class DeleteEndpoints {
      @DisplayName("Should delete a material")
      @Test
      void deleteMaterial() throws Exception {
        mockMvc
            .perform(
                delete(baseUrl.concat("/delete/" + materialDTO.materialDid()))
                    .with(
                        SecurityMockMvcRequestPostProcessors.authentication(
                            fakeInstructor.getToken())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Material deleted"))
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data").doesNotExist());
      }
    }
  }

  @DisplayName("When the call is made by a student")
  @Nested
  class StudentEndpoints {

    Jwt jwt;
    AbstractUser mockStudent;
    FakeUser fakeStudent;

    @BeforeEach
    void setUp() {
      jwt = JwtTestUtil.createStudentJwt();
      mockStudent = mock(AbstractUser.class);
      when(mockStudent.getRole()).thenReturn("STUDENT");
      UUID studentDid = UUID.fromString("c0daf844-82ca-441f-99f6-2e5099f60bf7");
      when(mockStudent.getDid()).thenReturn(studentDid);
      when(userService.findOrCreateUser(any())).thenReturn(mockStudent);
      fakeStudent =
          new FakeUser(
              mockStudent.getDid(),
              mockStudent.getSubId(),
              mockStudent.getName(),
              mockStudent.getEmail());
    }

    @DisplayName("Unauthorized actions")
    @Nested
    class UnauthorizedActions {

      @DisplayName("Should not allow material deletion")
      @Test
      void deleteMaterial() throws Exception {
        mockMvc
            .perform(
                delete(baseUrl.concat("/delete/" + randomUUID))
                    .with(
                        SecurityMockMvcRequestPostProcessors.authentication(
                            fakeStudent.getToken())))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Student unauthorized to delete material"))
            .andExpect(jsonPath("$.status").value("error"));
      }

      @DisplayName("Should not allow material update")
      @Test
      void updateMaterial() throws Exception {
        mockMvc
            .perform(
                multipart(HttpMethod.PATCH, baseUrl.concat("/update/" + randomUUID))
                    .file(mockMultipartFile)
                    .file(
                        new MockMultipartFile(
                            "materialDTO",
                            null,
                            "application/json",
                            objectMapper.writeValueAsBytes(materialDTO)))
                    .with(
                        SecurityMockMvcRequestPostProcessors.authentication(
                            fakeStudent.getToken())))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Student unauthorized to update material"))
            .andExpect(jsonPath("$.status").value("error"));
      }

      @DisplayName("Should not allow material upload (non file)")
      @Test
      void uploadMaterial() throws Exception {
        mockMvc
            .perform(
                post(baseUrl.concat("/" + randomUUID + "/upload"))
                    .content(objectMapper.writeValueAsString(materialDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(
                        SecurityMockMvcRequestPostProcessors.authentication(
                            fakeStudent.getToken())))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Student unauthorized to upload material"))
            .andExpect(jsonPath("$.status").value("error"));
      }

      @DisplayName("Should not allow file material upload")
      @Test
      void uploadFileMaterial() throws Exception {
        mockMvc
            .perform(
                multipart(HttpMethod.POST, baseUrl.concat("/" + randomUUID + "/upload_file"))
                    .file(mockMultipartFile)
                    .file(
                        new MockMultipartFile(
                            "materialDTO",
                            null,
                            "application/json",
                            objectMapper.writeValueAsBytes(materialDTO)))
                    .with(
                        SecurityMockMvcRequestPostProcessors.authentication(
                            fakeStudent.getToken())))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Student unauthorized to upload material"))
            .andExpect(jsonPath("$.status").value("error"));
      }
    }
  }
}
