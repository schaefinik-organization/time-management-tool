package schaefinik.time.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import schaefinik.time.project.model.Project;
import java.util.Optional;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByOrderByNameAsc();

    List<Project> findByActiveTrueOrderByNameAsc();

    boolean existsByName(String name);

    Optional<Project> findByName(String name);
}
