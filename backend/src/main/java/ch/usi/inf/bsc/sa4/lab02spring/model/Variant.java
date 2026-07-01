package ch.usi.inf.bsc.sa4.lab02spring.model;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a variant of an exercise, containing a unique identifier, an index, associated
 * exercise, and a list of questions. A Variant serves as a logical grouping of questions belonging
 * to a specific exercise.
 */
@Entity
@Table(name = "Variant")
public class Variant {

  /** The primary key identifier of the variant. */
  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "variant_seq")
  @SequenceGenerator(name = "variant_seq", sequenceName = "variant_seq", allocationSize = 1)
  @Column(name = "variant_ID")
  private long variantId;

  /**
   * The unique decentralized identifier (DID) for the variant.
   *
   * <p>This value is generated automatically and is used to uniquely identify the variant
   * externally.
   */
  @Column(name = "variant_DID", unique = true, nullable = false, updatable = false)
  private UUID variantDid;

  /** The index of the variant within the exercise. */
  @Column(name = "idx", nullable = false)
  private int idx;

  /** The exercise to which the variant belongs. */
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "exercise_ID")
  private Exercise exercise;

  /** The list of questions associated with this variant. */
  @OneToMany(mappedBy = "variant", fetch = LAZY, orphanRemoval = true, cascade = REMOVE)
  private List<AbstractQuestion> questions = new ArrayList<>();

  /**
   * Constructs a new Variant with the specified index and exercise.
   *
   * <p>A random UUID is generated for {@code variantDid} and the variant is set to draft mode by
   * default.
   *
   * @param idx the index of the variant within the exercise
   * @param exercise the exercise to which the variant belongs
   */
  public Variant(int idx, Exercise exercise) {
    this.variantDid = UUID.randomUUID();
    this.idx = idx;
    this.exercise = exercise;
  }

  /** Default constructor for JPA. */
  public Variant() {}

  /**
   * Gets the list of questions associated with this variant.
   *
   * @return the questions
   */
  public List<AbstractQuestion> getQuestions() {
    return new ArrayList<>(questions);
  }

  /**
   * Sets the list of questions associated with this variant.
   *
   * @param questions the questions to set
   */
  public void setQuestions(List<AbstractQuestion> questions) {
    this.questions = new ArrayList<>(questions);
  }

  /**
   * Adds a question to the list of questions associated with this variant.
   *
   * @param question the question to add
   */
  public void addQuestion(AbstractQuestion question) {
    this.questions.add(question);
  }

  /**
   * Removes a question from the list of questions associated with this variant.
   *
   * @param question the question to remove
   */
  public void removeQuestion(AbstractQuestion question) {
    this.questions.remove(question);
  }

  /**
   * Gets the unique identifier of the variant.
   *
   * @return the variant identifier
   */
  public long getVariantId() {
    return variantId;
  }

  /**
   * Sets the unique identifier of the variant.
   *
   * @param variantId the variant identifier to set
   */
  public void setVariantId(long variantId) {
    this.variantId = variantId;
  }

  /**
   * Gets the unique decentralized identifier (DID) for the variant.
   *
   * @return the variant DID
   */
  public UUID getVariantDid() {
    return variantDid;
  }

  /**
   * Sets the unique decentralized identifier (DID) for the variant.
   *
   * @param variantDid the variant DID to set
   */
  public void setVariantDid(UUID variantDid) {
    this.variantDid = variantDid;
  }

  /**
   * Gets the index of the variant within the exercise.
   *
   * @return the variant index
   */
  public int getIdx() {
    return idx;
  }

  /**
   * Sets the index of the variant within the exercise.
   *
   * @param idx the variant index to set
   */
  public void setIdx(int idx) {
    if (idx < 0) {
      throw new AssertionError();
    }
    this.idx = idx;
  }

  /**
   * Gets the exercise to which the variant belongs.
   *
   * @return the exercise
   */
  public Exercise getExercise() {
    return exercise;
  }

  /**
   * Sets the exercise to which the variant belongs.
   *
   * @param exercise the exercise to set
   */
  public void setExercise(Exercise exercise) {
    this.exercise = exercise;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Variant variant)) {
      return false;
    }
    return Objects.equals(variantId, variant.variantId)
        && Objects.equals(variantDid, variant.variantDid)
        && Objects.equals(idx, variant.idx)
        && Objects.equals(exercise, variant.exercise);
  }

  @Override
  public int hashCode() {
    return Objects.hash(variantId, variantDid, idx);
  }
}
