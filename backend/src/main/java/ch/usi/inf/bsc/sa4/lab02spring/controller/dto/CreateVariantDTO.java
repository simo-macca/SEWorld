package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

/**
 * Data Transfer Object (DTO) for creating a new variant. Encapsulates the index position of the
 * variant that is being created, facilitating the transfer of index data across application layers.
 *
 * @param idx the index position of the variant within its collection
 */
public record CreateVariantDTO(int idx) {}
