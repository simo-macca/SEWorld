package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.CreateUserDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.Instructor;
import ch.usi.inf.bsc.sa4.lab02spring.repository.InstructorRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Service layer responsible for handling business logic related to instructors, including creation,
 * validation, and persistence. This service collaborates with multiple repositories and other
 * services to manage user attempts and question-related data.
 */
@Service
public class InstructorService {

  /** The repository for instructor entities. */
  private final InstructorRepository instructorRepository;

  /**
   * Constructs a new {@code InstructorService} with the required repository.
   *
   * @param instructorRepository the repository for instructor entities.
   */
  @Autowired
  public InstructorService(InstructorRepository instructorRepository) {
    this.instructorRepository = instructorRepository;
  }

  /**
   * Creates a new instructor and persists it in the DB.
   *
   * @param createUserDTO the data to create a new instructor.
   * @return the newly created instructor.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Instructor createInstructor(CreateUserDTO createUserDTO) {
    Instructor newInstructor =
        new Instructor(createUserDTO.subId(), createUserDTO.name(), createUserDTO.email());
    return this.instructorRepository.save(newInstructor);
  }

  /**
   * Finds an instructor by its ID.
   *
   * @param id the ID of the instructor to find.
   * @return the instructor with the specified ID, or {@code null} if not found.
   */
  public Optional<Instructor> findById(Long id) {
    return instructorRepository.findById(id);
  }

  /**
   * Saves an instructor in the DB.
   *
   * @param instructor the instructor to save.
   * @return the saved instructor.
   */
  public Instructor saveInstructor(Instructor instructor) {
    return instructorRepository.save(instructor);
  }
}
