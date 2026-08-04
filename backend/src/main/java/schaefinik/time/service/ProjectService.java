package schaefinik.time.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schaefinik.time.exception.ProjectNotFoundException;
import schaefinik.time.model.ProjectModel;
import schaefinik.time.repository.ProjectRepository;
import schaefinik.time.requestData.ProjectRequest;
import schaefinik.time.responseData.ProjectResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

	private final ProjectRepository projectRepository;

	public List<ProjectResponse> findAll() {
		return projectRepository.findAll()
				.stream()
				.map(this::mapToResponse)
				.toList();
	}

	public ProjectModel getProject(Long id) {
		return projectRepository.findById(id)
				.orElseThrow(() -> new ProjectNotFoundException("Project not found: " + id));
	}

	public ProjectResponse getProjectData(Long id) {
		ProjectModel project = getProject(id);
		return mapToResponse(project);
	}

	public ProjectResponse create(ProjectRequest request) {
		if (projectRepository.existsByName(request.name())) {
			throw new IllegalArgumentException("Project name already exists");
		}

		ProjectModel project = ProjectModel.builder()
				.name(request.name())
				.description(request.description())
				.active(true)
				.build();

		ProjectModel saved = projectRepository.save(project);
		return mapToResponse(saved);
	}

	public ProjectResponse update(Long id, ProjectRequest request) {
		ProjectModel project = getProject(id);

		project.setName(request.name());
		project.setDescription(request.description());

		ProjectModel updated = projectRepository.save(project);
		return mapToResponse(updated);
	}

	public void delete(Long id) {
		ProjectModel project = getProject(id);
		projectRepository.delete(project);
	}

	private ProjectResponse mapToResponse(ProjectModel project) {
		return new ProjectResponse(
				project.getId(),
				project.getName(),
				project.getDescription());
	}

}
