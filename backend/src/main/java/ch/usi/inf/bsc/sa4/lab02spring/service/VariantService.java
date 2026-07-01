package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.CreateVariantDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.Exercise;
import ch.usi.inf.bsc.sa4.lab02spring.model.Variant;
import ch.usi.inf.bsc.sa4.lab02spring.repository.ExerciseRepository;
import ch.usi.inf.bsc.sa4.lab02spring.repository.VariantRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

/** Service class responsible for managing operations related to variants on exercises. */
@Service
public class VariantService {

  /** Repository for storing and retrieving {@link Variant} entities. */
  private final VariantRepository variantRepository;

  /** Repository for retrieving {@link Exercise} entities. */
  private final ExerciseRepository exerciseRepository;

  /**
   * Constructs a new {@code VariantService} with injected dependencies.
   *
   * @param variantRepository the repository for Variant entities.
   * @param exerciseRepository the repository for Exercise entities.
   */
  @Autowired
  public VariantService(
      VariantRepository variantRepository, ExerciseRepository exerciseRepository) {
    this.variantRepository = variantRepository;
    this.exerciseRepository = exerciseRepository;
  }

  /**
   * Creates a new variant and persists it in the DB.
   *
   * @param exerciseDid the DID of the exercise to which the variant belongs.
   * @param variantDTO the data to create a new variant.
   * @return the newly created variant.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Optional<Variant> createNewVariant(UUID exerciseDid, CreateVariantDTO variantDTO) {
    Exercise ex =
        exerciseRepository
            .findByExerciseDid(exerciseDid)
            .orElseThrow(
                () ->
                    new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, "Exercise DID doesn't exist"));
    Optional<Variant> existingVariant =
        variantRepository.findByExerciseAndIdx(ex, variantDTO.idx());
    if (existingVariant.isPresent()) {
      throw new HttpClientErrorException(HttpStatus.CONFLICT, "Variant already exists");
    }
    Optional<Variant> optVariant = variantRepository.findTopByExerciseOrderByIdxDesc(ex);
    Variant newVariant;
    newVariant =
        optVariant
            .map(variant -> new Variant(variant.getIdx() + 1, ex))
            .orElseGet(() -> new Variant(0, ex));
    return Optional.of(variantRepository.save(newVariant));
  }

  /**
   * Retrieves all variants associated with the specified exercise.
   *
   * @param exerciseDid the DID of the exercise to retrieve variants for.
   * @return a list of variants associated with the specified exercise.
   */
  public List<Variant> getAllVariantsOfExercise(UUID exerciseDid) {
    Exercise ex =
        exerciseRepository
            .findByExerciseDid(exerciseDid)
            .orElseThrow(
                () ->
                    new HttpClientErrorException(
                        HttpStatus.NOT_FOUND, "Exercise DID doesn't exist"));
    List<Variant> variants = variantRepository.findByExerciseOrderByIdxAsc(ex);
    if (variants == null || variants.isEmpty()) {
      throw new HttpClientErrorException(
          HttpStatus.NOT_FOUND,
          "There are no variant associated with the exercise did " + exerciseDid);
    }
    variants.sort(Comparator.comparing(Variant::getIdx));
    return variants;
  }
}
