package backend.repository;

import backend.model.Topic;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {
  Optional<Topic> getByTopicDid(UUID topicDid);

  Optional<Topic> getBySlug(String topicSlug);
}
