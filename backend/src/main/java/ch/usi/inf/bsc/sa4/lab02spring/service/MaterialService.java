package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.MaterialDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.Material;
import ch.usi.inf.bsc.sa4.lab02spring.model.Topic;
import ch.usi.inf.bsc.sa4.lab02spring.repository.MaterialRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.TopicRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

/** Service class responsible for managing operations related to materials on exercises. */
@Service
public class MaterialService {

  /** Repository for storing and retrieving {@link Material} entities. */
  private final MaterialRepository materialRepository;

  /** Repository for retrieving {@link Topic} entities. */
  private final TopicRepository topicRepository;

  /**
   * Constructs a new {@code MaterialService} with injected dependencies.
   *
   * @param materialRepository the repository for Material entities.
   * @param topicRepository the repository for Topic entities.
   */
  @Autowired
  public MaterialService(MaterialRepository materialRepository, TopicRepository topicRepository) {
    this.materialRepository = materialRepository;
    this.topicRepository = topicRepository;
  }

  /**
   * Retrieves all materials associated with a given topic.
   *
   * @param topicDid the DID of the topic to retrieve materials for.
   * @return a list of materials associated with the given topic.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public List<Material> getMaterialsByTopicDid(UUID topicDid) {
    return materialRepository.findByTopic_TopicDid(topicDid);
  }

  /**
   * Retrieves a material by its DID.
   *
   * @param materialDid the DID of the material to retrieve.
   * @return the material with the specified DID, or {@code null} if no material with the specified
   *     DID exists.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Optional<Material> getMaterialByDid(UUID materialDid) {
    return materialRepository.findByMaterialDid(materialDid);
  }

  /**
   * Creates a new material for the specified topic.
   *
   * @param materialDTO the DTO containing the material data.
   * @param topicDid the DID of the topic to associate the material with.
   * @param file the file to upload as material content.
   * @return the created material.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Optional<Material> uploadMaterial(
      MaterialDTO materialDTO, UUID topicDid, MultipartFile file) {
    Topic topic = getTopicOrThrow(topicDid);
    validateMaterialDTO(materialDTO);

    String materialType = materialDTO.materialType().toLowerCase();
    String description = materialDTO.description();

    Material material = new Material();
    material.setMaterialTitle(materialDTO.title());
    material.setMaterialType(materialType);
    material.setTopic(topic);
    material.setMaterialDescription(description);

    handleMaterialContentForUpload(material, file, description);

    return Optional.of(materialRepository.save(material));
  }

  private Topic getTopicOrThrow(UUID topicDid) {
    return topicRepository
        .findByTopicDid(topicDid)
        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Topic not found"));
  }

  private void validateMaterialDTO(MaterialDTO dto) {
    if (dto.materialType() == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Material type cannot be null");
    }
    if (dto.title() == null || dto.title().isBlank()) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Material title cannot be null");
    }
  }

  private void handleMaterialContentForUpload(
      Material material, MultipartFile file, String description) {
    switch (material.getMaterialType()) {
      case "file" -> handleFileContent(material, file);
      case "link" -> validateNonEmpty(description, "Link is required for type 'link'");
      case "md" -> validateNonEmpty(description, "Markdown text is required for type 'md'");
      default -> throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "Invalid material type");
    }
  }

  private void handleFileContent(Material material, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "File content is required for type 'file'");
    }
    try {
      material.setMaterialContent(file.getBytes());
      material.setMaterialFileName(file.getOriginalFilename());
    } catch (Exception e) {
      throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private void validateNonEmpty(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, message);
    }
  }

  /**
   * Updates an existing material.
   *
   * @param materialDid the DID of the material to update.
   * @param materialDTO the DTO containing the updated material data.
   * @param file the file to upload as material content.
   * @return the updated material.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Optional<Material> updateMaterial(
      UUID materialDid, MaterialDTO materialDTO, MultipartFile file) {
    Material material =
        materialRepository
            .findByMaterialDid(materialDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Material not found"));
    String lastMaterialType = material.getMaterialType().toLowerCase();
    System.out.println(lastMaterialType);

    updateTitleIfPresent(material, materialDTO);
    String materialType = determineMaterialType(material, materialDTO);

    updateMaterialByType(material, materialType, materialDTO.description(), file, lastMaterialType);

    return Optional.of(materialRepository.save(material));
  }

  private void updateTitleIfPresent(Material material, MaterialDTO dto) {
    String title = dto.title();
    if (title != null && !title.isBlank()) {
      material.setMaterialTitle(title);
    }
  }

  private String determineMaterialType(Material material, MaterialDTO dto) {
    String type = dto.materialType();
    if (type == null || type.isBlank()) {
      return material.getMaterialType().toLowerCase();
    }
    material.setMaterialType(type);
    return type.toLowerCase();
  }

  private void updateMaterialByType(
      Material material,
      String type,
      String description,
      MultipartFile file,
      String lastMaterialType) {
    switch (type) {
      case "file" -> {
        updateDescriptionIfPresent(material, description);
        updateFileIfPresent(material, file, lastMaterialType);
      }
      case "link", "md" -> {
        updateDescriptionIfPresent(material, description);
        material.setMaterialContent(null);
      }
      default -> throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "Invalid material type");
    }
  }

  private void updateDescriptionIfPresent(Material material, String description) {
    if (description != null && !description.isBlank()) {
      material.setMaterialDescription(description);
    }
  }

  private void updateFileIfPresent(Material material, MultipartFile file, String lastMaterialType) {
    if (file != null && !file.isEmpty()) {
      if (lastMaterialType.equals("file")) {
        try {
          material.setMaterialContent(file.getBytes());
          material.setMaterialFileName(file.getOriginalFilename());
        } catch (Exception e) {
          throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
      } else {
        handleFileContent(material, file);
      }
    }
  }

  /**
   * Deletes an existing material.
   *
   * @param materialDid the DID of the material to delete.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public void deleteMaterial(UUID materialDid) {
    Material existingMaterial =
        materialRepository
            .findByMaterialDid(materialDid)
            .orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Material not found"));

    materialRepository.delete(existingMaterial);
  }
}
