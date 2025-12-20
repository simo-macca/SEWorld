package backend.repository;

import backend.model.Exercise;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {

  Optional<Exercise> getByExerciseSlug(String exerciseSlug);

  List<Exercise> findAllByExerciseTopic_TopicSlug(String topicSlug);

  void deleteByExerciseSlug(String exerciseSlug);
}
