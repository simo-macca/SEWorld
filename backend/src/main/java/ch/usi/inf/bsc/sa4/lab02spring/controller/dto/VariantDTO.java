package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import ch.usi.inf.bsc.sa4.lab02spring.model.Variant;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a specific variant of a question.
 *
 * <p>This record encapsulates the unique identifier and index of a question variant, facilitating
 * the transfer of variant data between different layers of the application.
 *
 * @param variantDid the unique identifier of the variant
 * @param idx the index position of the variant within its collection
 */
public record VariantDTO(UUID variantDid, int idx) {

  /**
   * Constructs a {@code VariantDTO} from a {@link Variant} entity.
   *
   * <p>This constructor extracts the necessary information from the provided {@code Variant} object
   * to initialize the DTO.
   *
   * @param variant the {@code Variant} entity from which to create the DTO
   */
  public VariantDTO(Variant variant) {
    this(variant.getVariantDid(), variant.getIdx());
  }
}
