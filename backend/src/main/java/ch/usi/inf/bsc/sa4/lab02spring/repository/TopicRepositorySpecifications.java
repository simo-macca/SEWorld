package ch.usi.inf.bsc.sa4.lab02spring.repository;

import ch.usi.inf.bsc.sa4.lab02spring.model.Topic;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

/** Holds specification functions for dynamic filtering for the {@link TopicRepository} */
final class TopicRepositorySpecifications {
  /** Private {@link TopicRepositorySpecifications} constructor to add the public constructor. */
  private TopicRepositorySpecifications() {}

  /**
   * Creates a {@link Specification} to filter {@link Topic} entities based on keywords in their
   * titles and descriptions. The search is case-insensitive.
   *
   * @param titleKeywords a list of keywords to match against topic titles; if null or empty,
   *     matches any title
   * @param descriptionKeywords a list of keywords to match against topic descriptions; if null or
   *     empty, matches any description
   * @return a {@link Specification} for filtering topics based on the provided keywords
   * @spec.requires {@code titleKeywords} and {@code descriptionKeywords} can be null or any valid
   *     list
   * @spec.effects No effects
   */
  public static Specification<Topic> withKeywordsInTitleOrDescription(
      List<String> titleKeywords, List<String> descriptionKeywords) {
    return new TopicKeywordSpecification(titleKeywords, descriptionKeywords);
  }

  /**
   * A {@link Specification} implementation for filtering {@link Topic} entities based on keyword
   * matches in their titles and descriptions.
   *
   * <p>This record takes two optional lists of keywords: one for matching against topic titles and
   * one for topic descriptions. The match is case-insensitive and uses SQL-like wildcard searching.
   *
   * <p>Usage example:
   *
   * <pre>{@code
   * Specification<Topic> spec = new TopicKeywordSpecification(
   *     List.of("spring", "java"), List.of("boot", "tutorial")
   * );
   * }</pre>
   *
   * @param titleKeywords the list of keywords to match in the topic titles; may be null or empty
   * @param descriptionKeywords the list of keywords to match in the topic descriptions; may be null
   *     or empty
   */
  private record TopicKeywordSpecification(
      List<String> titleKeywords, List<String> descriptionKeywords)
      implements Specification<Topic> {

    /**
     * Builds a {@link Predicate} for querying {@link Topic} entities based on the provided title
     * and description keywords.
     *
     * <p>If either keyword list is null or empty, that part of the predicate will always evaluate
     * to true (i.e., it won't filter by that field). Matching is done with case-insensitive SQL
     * LIKE patterns.
     *
     * @param root the root type in the from clause
     * @param query the criteria query
     * @param cb the criteria builder
     * @return a combined {@link Predicate} that matches topics with titles and/or descriptions
     *     containing any of the specified keywords
     */
    @Override
    public Predicate toPredicate(Root<Topic> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
      final List<Predicate> predicatesTitle = new ArrayList<>();
      final List<Predicate> predicatesDescription = new ArrayList<>();

      if (titleKeywords == null || titleKeywords.isEmpty()) {
        predicatesTitle.add(cb.isTrue(cb.literal(true)));
      } else {
        for (final String tKeyword : titleKeywords) {
          predicatesTitle.add(
              cb.like(
                  cb.lower(root.get("topicTitle")), "%" + tKeyword.toLowerCase(Locale.ROOT) + "%"));
        }
      }

      if (descriptionKeywords == null || descriptionKeywords.isEmpty()) {
        predicatesDescription.add(cb.isTrue(cb.literal(true)));
      } else {
        for (final String pKeyword : descriptionKeywords) {
          predicatesDescription.add(
              cb.like(
                  cb.lower(root.get("topicDescription")),
                  "%" + pKeyword.toLowerCase(Locale.ROOT) + "%"));
        }
      }

      return cb.and(
          cb.or(predicatesTitle.toArray(new Predicate[0])),
          cb.or(predicatesDescription.toArray(new Predicate[0])));
    }
  }
}
