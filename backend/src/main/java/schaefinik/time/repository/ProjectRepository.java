package schaefinik.time.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import schaefinik.time.model.ProjectModel;
import schaefinik.time.model.TimeUserModel;

import java.util.List;

public interface ProjectRepository extends JpaRepository<ProjectModel, Long> {

	List<ProjectModel> findByManager(TimeUserModel manager);

	List<ProjectModel> findByAssignedUsersContainingAndActiveIsTrue(TimeUserModel user);
}
