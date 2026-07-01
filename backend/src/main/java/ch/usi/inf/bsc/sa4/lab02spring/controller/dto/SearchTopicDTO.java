package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) for encapsulating search criteria related to topics.
 *
 * <p>This record holds lists of keywords that can be used to search for topics based on their
 * titles and descriptions.
 *
 * @param titleKeywords a list of keywords to match against topic titles
 * @param descriptionKeywords a list of keywords to match against topic descriptions
 */
public record SearchTopicDTO(List<String> titleKeywords, List<String> descriptionKeywords) {
  /**
   * Constructs a new {@code SearchTopicDTO} by performing defensive (deep) copies of the provided
   * lists.
   *
   * <p>This ensures that the internal state cannot be modified externally after construction.
   *
   * @param titleKeywords the list of keywords to match against topic titles
   * @param descriptionKeywords the list of keywords to match against topic descriptions
   */
  public SearchTopicDTO(List<String> titleKeywords, List<String> descriptionKeywords) {
    if (titleKeywords == null) {
      this.titleKeywords = null;
    } else {
      this.titleKeywords = List.copyOf(titleKeywords);
    }

    if (descriptionKeywords == null) {
      this.descriptionKeywords = null;
    } else {
      this.descriptionKeywords = List.copyOf(descriptionKeywords);
    }
  }
}
