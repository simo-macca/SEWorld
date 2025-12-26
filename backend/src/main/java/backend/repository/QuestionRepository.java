package backend.repository;

import backend.model.AbstractQuestion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<AbstractQuestion, UUID> {
  List<AbstractQuestion> getAllByQuestionExercise_ExerciseSlug(String questionExerciseExerciseSlug);

  Optional<AbstractQuestion> getAbstractQuestionsByQuestionSlug(String questionSlug);

  void deleteAllByQuestionSlugIn(List<String> questionSlugs);
}
