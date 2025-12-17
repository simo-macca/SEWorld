package backend.repository;

import backend.model.AbstractUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbstractUserRepository extends CrudRepository<AbstractUser, UUID> {
  Optional<AbstractUser> getByUserDid(UUID userDid);
}
