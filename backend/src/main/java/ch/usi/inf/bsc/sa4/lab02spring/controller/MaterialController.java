package ch.usi.inf.bsc.sa4.lab02spring.controller;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.MaterialDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.Material;
import ch.usi.inf.bsc.sa4.lab02spring.response.ResponseHandler;
import ch.usi.inf.bsc.sa4.lab02spring.service.MaterialService;
import ch.usi.inf.bsc.sa4.lab02spring.service.UserService;
import ch.usi.inf.bsc.sa4.lab02spring.utils.UserRoleCheckerUtils;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for managing educational materials.
 *
 * <p>Provides endpoints for uploading, downloading, retrieving, updating, and deleting materials.
 * Access to modifying endpoints is restricted to instructor users.
 */
@RestController
@RequestMapping("/api/auth/material")
public class MaterialController {

  /** Service layer for managing material operations. */
  private final MaterialService materialService;

  /** Service layer for retrieving user information and roles. */
  private final UserService userService;

  /**
   * Constructs a new MaterialController with the required dependencies.
   *
   * @param materialService service for material operations
   * @param userService service for user and role checks
   */
  @Autowired
  public MaterialController(MaterialService materialService, UserService userService) {
    this.materialService = materialService;
    this.userService = userService;
  }

  /**
   * Retrieves all materials associated with a given topic.
   *
   * @param topicDid the UUID of the topic
   * @return a {@link ResponseEntity} containing a list of {@link MaterialDTO} objects with HTTP
   *     status 200 if found
   * @throws HttpClientErrorException if no materials are found (HTTP 404)
   */
  @GetMapping("/{topic_did}/all_materials")
  public ResponseEntity<Object> getAllMaterialsByTopicDid(
      @PathVariable("topic_did") UUID topicDid) {
    List<MaterialDTO> materials =
        materialService.getMaterialsByTopicDid(topicDid).stream().map(MaterialDTO::new).toList();

    if (materials.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "No materials found for this topic");
    }

    return ResponseHandler.generateResponse("Materials found", HttpStatus.OK, true, materials);
  }

  /**
   * Retrieves a single material by its DID.
   *
   * @param materialDid the UUID of the material
   * @return a {@link ResponseEntity} containing an {@link Optional} of {@link MaterialDTO} with
   *     HTTP status 200 if found
   * @throws HttpClientErrorException if the material is not found (HTTP 404)
   */
  @GetMapping("/{did}")
  public ResponseEntity<Object> getMaterialByDid(@PathVariable("did") UUID materialDid) {
    Optional<Material> material = materialService.getMaterialByDid(materialDid);
    if (material.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "No material found with such did");
    }
    return ResponseHandler.generateResponse(
        "Material found", HttpStatus.OK, true, material.map(MaterialDTO::new));
  }

  /**
   * Downloads the binary content of a file-type material.
   *
   * @param materialDid the UUID of the material
   * @return a {@link ResponseEntity} containing the file bytes with content-disposition header set
   *     for attachment download
   * @throws HttpClientErrorException if the material is not found (HTTP 404) or if the material is
   *     not of type "file" (HTTP 400)
   */
  @GetMapping("/{did}/download")
  public ResponseEntity<byte[]> downloadMaterial(@PathVariable("did") UUID materialDid) {
    Optional<Material> materialOpt = materialService.getMaterialByDid(materialDid);
    if (materialOpt.isEmpty()) {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Material not found");
    }
    Material material = materialOpt.get();
    if (!material.getMaterialType().equals("file")) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "Cannot download a non file material");
    }
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + material.getMaterialTitle() + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(material.getMaterialContent());
  }

  /**
   * Uploads a new file-based material under the specified topic.
   *
   * <p>Only instructors are authorized to upload materials.
   *
   * @param principal the authenticated user principal
   * @param topicDid the UUID of the topic
   * @param file the file to upload
   * @param materialDTO DTO containing material metadata
   * @return a {@link ResponseEntity} with the saved {@link MaterialDTO} and HTTP status 201 if
   *     successful
   * @throws HttpClientErrorException if the user is not authorized (HTTP 401)
   */
  @PostMapping("{topic_did}/upload_file")
  public ResponseEntity<Object> uploadFileMaterial(
      @AuthenticationPrincipal Object principal,
      @PathVariable("topic_did") UUID topicDid,
      @RequestParam("file") MultipartFile file,
      @RequestPart MaterialDTO materialDTO) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "Student unauthorized to upload material");
    }

    Optional<Material> savedMaterial = materialService.uploadMaterial(materialDTO, topicDid, file);
    return ResponseHandler.generateResponse(
        "Material uploaded", HttpStatus.CREATED, true, savedMaterial.map(MaterialDTO::new));
  }

  /**
   * Uploads a new non-file (e.g., URL or text) material under the specified topic.
   *
   * <p>Only instructors are authorized to upload materials.
   *
   * @param principal the authenticated user principal
   * @param topicDid the UUID of the topic
   * @param materialDTO DTO containing material metadata
   * @return a {@link ResponseEntity} with the saved {@link MaterialDTO} and HTTP status 201 if
   *     successful
   * @throws HttpClientErrorException if the user is not authorized (HTTP 401)
   */
  @PostMapping("{topic_did}/upload")
  public ResponseEntity<Object> uploadNonFileMaterial(
      @AuthenticationPrincipal Object principal,
      @PathVariable("topic_did") UUID topicDid,
      @RequestBody MaterialDTO materialDTO) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "Student unauthorized to upload material");
    }

    Optional<Material> savedMaterial = materialService.uploadMaterial(materialDTO, topicDid, null);
    return ResponseHandler.generateResponse(
        "Material uploaded", HttpStatus.CREATED, true, savedMaterial.map(MaterialDTO::new));
  }

  /**
   * Updates an existing material, optionally replacing its binary file.
   *
   * <p>Only instructors are authorized to update materials.
   *
   * @param principal the authenticated user principal
   * @param materialDid the UUID of the material to update
   * @param materialDTO DTO containing updated metadata
   * @param file new file to replace existing content (may be omitted)
   * @return a {@link ResponseEntity} with the updated {@link MaterialDTO} and HTTP status 200 if
   *     successful
   * @throws HttpClientErrorException if the user is not authorized (HTTP 401)
   */
  @PatchMapping("/update/{did}")
  public ResponseEntity<Object> updateMaterial(
      @AuthenticationPrincipal Object principal,
      @PathVariable("did") UUID materialDid,
      @RequestPart MaterialDTO materialDTO,
      @RequestParam("file") MultipartFile file) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "Student unauthorized to update material");
    }

    Optional<Material> updatedMaterial =
        materialService.updateMaterial(materialDid, materialDTO, file);
    return ResponseHandler.generateResponse(
        "Material updated", HttpStatus.OK, true, updatedMaterial.map(MaterialDTO::new));
  }

  /**
   * Deletes a material by its DID.
   *
   * <p>Only instructors are authorized to delete materials.
   *
   * @param principal the authenticated user principal
   * @param materialDid the UUID of the material to delete
   * @return a {@link ResponseEntity} with HTTP status 200 if deletion is successful
   * @throws HttpClientErrorException if the user is not authorized (HTTP 401)
   */
  @DeleteMapping("/delete/{did}")
  public ResponseEntity<Object> deleteMaterial(
      @AuthenticationPrincipal Object principal, @PathVariable("did") UUID materialDid) {
    if (!UserRoleCheckerUtils.isAuthenticatedUserInstructor(principal, userService)) {
      throw new HttpClientErrorException(
          HttpStatus.UNAUTHORIZED, "Student unauthorized to delete material");
    }
    materialService.deleteMaterial(materialDid);
    return ResponseHandler.generateResponse("Material deleted", HttpStatus.OK, true, null);
  }
}
