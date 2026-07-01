package ch.usi.inf.bsc.sa4.lab02spring.service;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.*;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.CreateVariantDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.Topic;
import ch.usi.inf.bsc.sa4.lab02spring.model.Variant;
import ch.usi.inf.bsc.sa4.lab02spring.repository.ExerciseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.TopicRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.VariantRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@Transactional
@DisplayName("Variant Service Test")
class VariantServiceTest {

  @Autowired private VariantService variantService;

  @Autowired private VariantRepository variantRepository;

  @Autowired private TopicRepository topicRepository;

  @Autowired private ExerciseRepository exerciseRepository;

  Exercise exercise1;
  Exercise exercise2;
  UUID wrongDid;
  Topic topic;
  CreateVariantDTO createVariantDTO1;
  CreateVariantDTO createVariantDTO2;
  CreateVariantDTO createVariantDTO3;
  CreateVariantDTO createVariantDTOWrongIndex;
  Variant variant0;
  Variant variant1;
  Variant variant2;
  Variant variant3;
  Variant variant4;
  int idx0;
  int idx1;
  int idx2;
  int idx3;
  int idx4;
  int wrongIdx;

  @BeforeEach
  void setUp() {
    topic = topicRepository.save(new Topic("Test Topic title", "Test Topic description"));
    exercise1 =
        exerciseRepository.save(
            new Exercise("Test 1 Exercise Title", "Test 1 Exercise description", topic));
    exercise2 =
        exerciseRepository.save(
            new Exercise("Test 2 Exercise Title", "Test 2 Exercise description", topic));
    assert topic.getDid().equals(exercise1.getTopic().getDid());
    idx0 = 0;
    idx1 = 1;
    idx2 = 2;
    idx3 = 3;
    idx4 = 4;
    wrongIdx = 1000;
    createVariantDTO1 = new CreateVariantDTO(idx0);
    createVariantDTO2 = new CreateVariantDTO(idx1);
    createVariantDTO3 = new CreateVariantDTO(idx2);
    createVariantDTOWrongIndex = new CreateVariantDTO(wrongIdx);
    wrongDid = UUID.fromString("3a4b2e7d-c939-4b7d-b3d2-74c5872db9f9");
  }

  @DisplayName("When creating the first variant of an exercise")
  @Nested
  class WhenCreatingNewVariant {

    @DisplayName("Create a new variant")
    @Test
    void createNewVariant() {
      Optional<Variant> optVariant =
          variantService.createNewVariant(exercise1.getExerciseDid(), createVariantDTO1);
      assertTrue(optVariant.isPresent());
      var variant = optVariant.get();
      assertEquals(exercise1.getExerciseDid(), variant.getExercise().getExerciseDid());
      assertEquals(idx0, variant.getIdx());
    }

    @DisplayName("Create variant with wrong exercise DID")
    @Test
    void createNewVariantWrongDid() {
      HttpClientErrorException exception =
          assertThrowsExactly(
              HttpClientErrorException.class,
              () -> variantService.createNewVariant(wrongDid, createVariantDTOWrongIndex));
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      assertEquals("404 Exercise DID doesn't exist", exception.getMessage());
    }

    @DisplayName("Get all variants")
    @Test
    void getAllVariants() {
      HttpClientErrorException exception =
          assertThrowsExactly(
              HttpClientErrorException.class,
              () -> variantService.getAllVariantsOfExercise(exercise1.getExerciseDid()));
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      assertEquals(
          "404 There are no variant associated with the exercise did " + exercise1.getExerciseDid(),
          exception.getMessage());
    }
  }

  @DisplayName("After creation of 2 variants")
  @Nested
  class AfterVariantCreationOfTwoVariants {

    @BeforeEach
    void setUp() {
      variant0 = new Variant(idx0, exercise1);
      variant1 = new Variant(idx1, exercise1);
      variantRepository.save(variant0);
      variantRepository.save(variant1);
    }

    @DisplayName("Create a new variant")
    @Test
    void createNewVariantNotExist() {
      Optional<Variant> optVariant =
          variantService.createNewVariant(exercise1.getExerciseDid(), createVariantDTO3);
      assertTrue(optVariant.isPresent());
      var variant = optVariant.get();
      assertEquals(exercise1.getExerciseDid(), variant.getExercise().getExerciseDid());
      assertEquals(idx2, variant.getIdx());
    }

    @DisplayName("Create variant but it already exists")
    @Test
    void createNewVariantAlreadyExists() {
      HttpClientErrorException exception =
          assertThrowsExactly(
              HttpClientErrorException.class,
              () -> variantService.createNewVariant(exercise1.getExerciseDid(), createVariantDTO1));
      assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
      assertEquals("409 Variant already exists", exception.getMessage());
    }

    @DisplayName("Create variant with a wrong but not already existing index")
    @Test
    void createNewVariantWrongIndex() {
      Optional<Variant> optVariant =
          variantService.createNewVariant(exercise1.getExerciseDid(), createVariantDTOWrongIndex);
      assertTrue(optVariant.isPresent());
      var variant = optVariant.get();
      assertEquals(exercise1.getExerciseDid(), variant.getExercise().getExerciseDid());
      assertEquals(idx1 + 1, variant.getIdx());
    }

    @DisplayName("Get all variants of an exercise")
    @Test
    void getAllVariants() {
      List<Variant> variants = variantService.getAllVariantsOfExercise(exercise1.getExerciseDid());
      assertEquals(2, variants.size());
      assertTrue(variants.contains(variant0));
      assertTrue(variants.contains(variant1));
    }

    @DisplayName("Get all variants of an exercise but the exercise DID is wrong")
    @Test
    void getAllVariantsWithWrongExerciseDid() {
      HttpClientErrorException exception =
          assertThrowsExactly(
              HttpClientErrorException.class,
              () -> variantService.getAllVariantsOfExercise(wrongDid));
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      assertEquals("404 Exercise DID doesn't exist", exception.getMessage());
    }

    @DisplayName("Get all variants in an existing exercise but it has no variants")
    @Test
    void getAllVariantsInExistingExercise() {
      HttpClientErrorException exception =
          assertThrowsExactly(
              HttpClientErrorException.class,
              () -> variantService.getAllVariantsOfExercise(exercise2.getExerciseDid()));
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      assertEquals(
          "404 There are no variant associated with the exercise did " + exercise2.getExerciseDid(),
          exception.getMessage());
    }
  }

  @DisplayName("After creation of many variants")
  @Nested
  class AfterVariantCreationOfManyVariants {

    @BeforeEach
    void setUp() {
      variant0 = new Variant(idx0, exercise1);
      variant1 = new Variant(idx1, exercise1);
      variant2 = new Variant(idx2, exercise2);
      variant3 = new Variant(idx3, exercise2);
      variant4 = new Variant(idx4, exercise2);
      variantRepository.save(variant0);
      variantRepository.save(variant1);
      variantRepository.save(variant2);
      variantRepository.save(variant3);
      variantRepository.save(variant4);
    }

    @DisplayName("It should return a list of variants sorted in increasing order by index")
    @Test
    void sortVariants() {
      List<Variant> variants = variantService.getAllVariantsOfExercise(exercise1.getExerciseDid());
      for (int i = 0; i < variants.size(); i++) {
        assertEquals(variants.get(i).getIdx(), i);
      }
    }
  }
}
