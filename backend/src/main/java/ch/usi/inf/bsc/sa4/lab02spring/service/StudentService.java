package ch.usi.inf.bsc.sa4.lab02spring.service;

import ch.usi.inf.bsc.sa4.lab02spring.controller.dto.CreateUserDTO;
import ch.usi.inf.bsc.sa4.lab02spring.model.Student;
import ch.usi.inf.bsc.sa4.lab02spring.repository.StudentRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Service class for managing Student entities. Provides methods to create, find, save, and update
 * students.
 */
@Service
public class StudentService {

  /** Repository for accessing and storing Student entities. */
  private final StudentRepository studentRepository;

  /**
   * Constructs a StudentService with the given StudentRepository.
   *
   * @param studentRepository repository for Student persistence operations
   */
  @Autowired
  public StudentService(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }

  /**
   * Creates a new user and persists it in the DB.
   *
   * @param createUserDTO the data to create a new user.
   * @return the newly created user.
   */
  @Transactional(rollbackFor = HttpClientErrorException.class)
  public Student createUser(CreateUserDTO createUserDTO) {
    Student newUser =
        new Student(createUserDTO.subId(), createUserDTO.name(), createUserDTO.email());
    return this.studentRepository.save(newUser);
  }

  /**
   * Finds a Student by their unique ID.
   *
   * @param id the student's ID
   * @return an Optional containing the Student if found, or empty if not
   */
  public Optional<Student> findById(Long id) {
    return studentRepository.findById(id);
  }

  /**
   * Saves or updates a Student entity.
   *
   * @param student the Student to save
   * @return the saved Student entity
   */
  public Student saveStudent(Student student) {
    return studentRepository.save(student);
  }

  /**
   * Increments the completion stage for the specified Student and saves the change.
   *
   * @param student the Student whose completion stage to increment
   */
  public void increaseCompletionStage(Student student) {
    student.increaseCompletionStage();
    studentRepository.save(student);
  }

  /**
   * Increments the completion stage for the specified Student and saves the change.
   *
   * @param student the Student whose completion stage to increment
   */
  public void increaseNumberAttempts(Student student) {
    student.increaseNumberAttempts();
    studentRepository.save(student);
  }
}
