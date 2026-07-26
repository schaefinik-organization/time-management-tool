package schaefinik.time.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import schaefinik.time.user.model.TimeUser;

import java.util.Optional;

public interface TimeUserRepository extends JpaRepository<TimeUser, Long> {
    Optional<TimeUser> findByUsername(String username);

    Optional<TimeUser> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
