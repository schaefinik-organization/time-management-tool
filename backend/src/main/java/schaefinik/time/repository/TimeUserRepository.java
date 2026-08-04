package schaefinik.time.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import schaefinik.time.model.TimeUserModel;

import java.util.Optional;

public interface TimeUserRepository extends JpaRepository<TimeUserModel, Long> {
	Optional<TimeUserModel> findByUsername(String username);

	Optional<TimeUserModel> findByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}
