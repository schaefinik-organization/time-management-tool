package schaefinik.time.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import schaefinik.time.model.ProjectModel;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectModel, Long> {

	List<ProjectModel> findAllByOrderByNameAsc();

	List<ProjectModel> findByActiveTrueOrderByNameAsc();

	boolean existsByName(String name);

	Optional<ProjectModel> findByName(String name);
}
