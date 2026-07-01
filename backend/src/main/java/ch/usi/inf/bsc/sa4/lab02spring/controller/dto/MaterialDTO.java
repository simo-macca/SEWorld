package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import ch.usi.inf.bsc.sa4.lab02spring.model.Material;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a material.
 *
 * <p>This record encapsulates essential details for a material, including its unique identifier,
 * title, description, type, and the filename of any associated file.
 *
 * @param materialDid the unique identifier (UUID) of the material
 * @param title the title of the material
 * @param description a brief description of the material
 * @param materialType the type or category of the material (e.g., video, article)
 * @param materialFileName the filename of the material
 */
public record MaterialDTO(
    UUID materialDid,
    String title,
    String description,
    String materialType,
    String materialFileName) {

  /**
   * Constructs a new {@code MaterialDTO} from a {@code Material} object.
   *
   * <p>This constructor converts the {@code Material} model instance into its corresponding DTO,
   * mapping the model fields to the DTO components.
   *
   * @param material the {@code Material} object from which to create the DTO
   */
  public MaterialDTO(Material material) {
    this(
        material.getMaterialDid(),
        material.getMaterialTitle(),
        material.getMaterialDescription(),
        material.getMaterialType(),
        material.getMaterialFileName());
  }
}
