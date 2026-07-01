 package ch.usi.inf.bsc.sa4.lab02spring.controller.dto;

/**
 * Immutable pair of question variant indices.
 *
 * <p>Represents two distinct indices (first and second) within a set of question variants. The
 * canonical constructor enforces that the two indices are different.
 *
 * @param first the index of the first variant (must differ from {@code second})
 * @param second the index of the second variant (must differ from {@code first})
 */
 public record QuestionIndexPair(int first, int second) {

  /**
   * Canonical constructor that validates the indices.
   *
   * @throws AssertionError if {@code first} and {@code second} are equal
   */
  public QuestionIndexPair {
    if (first == second) {
      throw new AssertionError("first and second should not be the same");
    }
  }

  /**
   * Checks whether both indices fall within the valid range [0, variantsLength).
   *
   * @param variantsLength the exclusive upper bound for valid indices; must be non-negative
   * @return {@code true} if {@code first} and {@code second} are each greater or equals to 0 and
   *     less than {@code variantsLength}; {@code false} otherwise
   */
  public boolean checkPair(int variantsLength) {
    return ((this.first >= 0 && this.first < variantsLength)
        && (this.second >= 0 && this.second < variantsLength));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof QuestionIndexPair(int first1, int second1))) {
      return false;
    }
    return (first == first1 && second == second1) || (first == second1 && second == first1);
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(first) + Integer.hashCode(second);
  }
 }
