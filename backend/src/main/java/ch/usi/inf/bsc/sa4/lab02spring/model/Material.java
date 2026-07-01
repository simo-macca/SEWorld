package ch.usi.inf.bsc.sa4.lab02spring.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a material resource associated with a topic.
 *
 * <p>This entity is mapped to the "material" table in the database and stores various details about
 * a material resource such as its title, content, description, type, upload time, and the
 * associated topic.
 */
@Entity
@Table(name = "material")
public class Material {

  /** The database-generated primary key. */
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @SequenceGenerator(name = "materials_seq", sequenceName = "materials_seq", allocationSize = 1)
  @Column(name = "material_id", unique = true, nullable = false)
  private Long materialId;

  /**
   * The universally unique identifier (UUID) of the material. This value is generated on object
   * creation and is immutable.
   */
  @Column(name = "material_did", unique = true, nullable = false, updatable = false)
  private final UUID materialDid;

  /** The title of the material. */
  @Column(name = "material_title", nullable = false)
  private String materialTitle;

  /** The file name associated with the material (if applicable). */
  @Column(name = "file_name")
  private String materialFileName;

  /**
   * The binary content of the material.
   *
   * <p>Annotated as a large object (LOB) to enable storage of large files. File materials can be up
   * to 20MB large
   */
  @Lob
  @Column(name = "material_content")
  private byte[] materialContent;

  /** The description of the material. */
  @Column(name = "material_description", length = 10000)
  private String materialDescription;

  /** The type of the material (for example, "file", "link", or "PDF"). */
  @Column(name = "material_type", nullable = false)
  private String materialType;

  /**
   * The date and time when the material was uploaded. Automatically set when the entity is
   * persisted.
   */
  @Column(name = "upload_at", nullable = false)
  private LocalDateTime uploadAt;

  /** The topic to which this material belongs. */
  @ManyToOne
  @JoinColumn(name = "topic_id", nullable = false)
  private Topic topic;

  /**
   * Default no-argument constructor.
   *
   * <p>Required by JPA for entity instantiation during data retrieval. This constructor generates a
   * new random UUID for {@link #materialDid}.
   */
  public Material() {
    this.materialDid = UUID.randomUUID();
  }

  /**
   * Additional constructor provided for test cases.
   *
   * <p>This constructor sets an initial empty content for the material and a default topic
   * instance. Modify if needed.
   *
   * @param materialDid the UUID of the material.
   * @param materialTitle the title of the material.
   * @param materialDescription the description of the material.
   */
  public Material(UUID materialDid, String materialTitle, String materialDescription) {
    this.materialDid = materialDid;
    this.materialTitle = materialTitle;
    this.materialDescription = materialDescription;
    this.materialContent = new byte[0];
    this.uploadAt = LocalDateTime.now();
    this.topic = new Topic();
    this.materialType = "default";
  }

  /**
   * Constructs a new Material with the specified title, description, content, upload time, topic,
   * and material type.
   *
   * @param materialTitle the title of the material.
   * @param materialDescription the description of the material.
   * @param materialContent the binary content of the material.
   * @param uploadAt the timestamp when the material was uploaded.
   * @param topic the topic associated with the material.
   * @param materialType the type of the material.
   */
  public Material(
      String materialTitle,
      String materialDescription,
      byte[] materialContent,
      LocalDateTime uploadAt,
      Topic topic,
      String materialType) {
    this.materialDid = UUID.randomUUID();
    this.materialTitle = materialTitle;
    this.materialContent = Arrays.copyOf(materialContent, materialContent.length);
    this.uploadAt = uploadAt;
    this.topic = topic;
    this.materialType = materialType;
    this.materialDescription = materialDescription;
  }

  /** Automatically sets the upload time before persisting to the database. */
  @PrePersist
  protected void onCreate() {
    this.uploadAt = LocalDateTime.now();
  }

  /**
   * Returns the database-generated ID of this material.
   *
   * @return the material ID.
   */
  public Long getMaterialId() {
    return materialId;
  }

  /**
   * Returns the universally unique identifier (UUID) of this material.
   *
   * @return the material UUID.
   */
  public UUID getMaterialDid() {
    return materialDid;
  }

  /**
   * Returns the title of the material.
   *
   * @return the material title.
   */
  public String getMaterialTitle() {
    return materialTitle;
  }

  /**
   * Returns the binary content of the material.
   *
   * @return the material content as a byte array.
   */
  public byte[] getMaterialContent() {
    if (this.materialContent == null) {
      return new byte[0];
    }
    return Arrays.copyOf(this.materialContent, this.materialContent.length);
  }

  /**
   * Returns the upload date and time of the material.
   *
   * @return the upload timestamp.
   */
  public LocalDateTime getUploadAt() {
    return uploadAt;
  }

  /**
   * Returns the topic associated with this material.
   *
   * @return the related Topic.
   */
  public Topic getTopic() {
    return topic;
  }

  /**
   * Returns the type of the material.
   *
   * @return the material type.
   */
  public String getMaterialType() {
    return materialType;
  }

  /**
   * Returns the description of the material.
   *
   * @return the material description.
   */
  public String getMaterialDescription() {
    return materialDescription;
  }

  /**
   * Sets the title of the material.
   *
   * @param materialTitle the new title of the material.
   */
  public void setMaterialTitle(String materialTitle) {
    this.materialTitle = materialTitle;
  }

  /**
   * Sets the description of the material.
   *
   * @param materialDescription the new description of the material.
   */
  public void setMaterialDescription(String materialDescription) {
    this.materialDescription = materialDescription;
  }

  /**
   * Sets the binary content of the material.
   *
   * @param materialContent the new content as a byte array.
   */
  public void setMaterialContent(byte[] materialContent) {
    if (materialContent == null) {
      this.materialContent = null;
      return;
    }
    this.materialContent = Arrays.copyOf(materialContent, materialContent.length);
  }

  /**
   * Sets the type of the material.
   *
   * @param materialType the new material type.
   */
  public void setMaterialType(String materialType) {
    this.materialType = materialType;
  }

  /**
   * Sets the associated topic for the material.
   *
   * @param topic the new Topic instance.
   */
  public void setTopic(Topic topic) {
    this.topic = topic;
  }

  /**
   * Returns the file name associated with the material.
   *
   * @return the material file name, or {@code null} if not set.
   */
  public String getMaterialFileName() {
    return materialFileName;
  }

  /**
   * Sets the file name associated with the material.
   *
   * @param materialFileName the new file name.
   */
  public void setMaterialFileName(String materialFileName) {
    this.materialFileName = materialFileName;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Material material)) {
      return false;
    }
    return Objects.equals(materialId, material.materialId)
        && Objects.equals(materialDid, material.materialDid)
        && Objects.equals(materialTitle, material.materialTitle)
        && Objects.equals(materialFileName, material.materialFileName)
        && Objects.deepEquals(materialContent, material.materialContent)
        && Objects.equals(materialDescription, material.materialDescription)
        && Objects.equals(materialType, material.materialType)
        && Objects.equals(uploadAt, material.uploadAt)
        && Objects.equals(topic, material.topic);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        materialId,
        materialDid,
        materialTitle,
        materialFileName,
        Arrays.hashCode(materialContent),
        materialDescription,
        materialType,
        uploadAt,
        topic);
  }

  @Override
  public String toString() {
    return "Material{"
        + "materialId="
        + materialId
        + ", materialDid="
        + materialDid
        + ", materialTitle='"
        + materialTitle
        + '\''
        + ", materialFileName='"
        + materialFileName
        + '\''
        + ", materialContent="
        + Arrays.toString(materialContent)
        + ", materialDescription='"
        + materialDescription
        + '\''
        + ", materialType='"
        + materialType
        + '\''
        + ", uploadAt="
        + uploadAt
        + ", topic="
        + topic
        + '}';
  }
}
