package schaefinik.time.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import schaefinik.time.project.model.Project;
import schaefinik.time.project.repository.ProjectRepository;
import schaefinik.time.project.requestData.ProjectRequest;
import schaefinik.time.project.responseData.ProjectResponse;

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

    public ProjectResponse create(ProjectRequest request) {
        if (projectRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Project name already exists");
        }

        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .active(true)
                .build();

        Project saved = projectRepository.save(project);
        return mapToResponse(saved);
    }

    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projekt nicht gefunden: " + id));

        project.setName(request.name());
        project.setDescription(request.description());

        Project updated = projectRepository.save(project);
        return mapToResponse(updated);
    }

    public void delete(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projekt nicht gefunden: " + id));

        projectRepository.delete(project);
    }

    private ProjectResponse mapToResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription());
    }

    public Object findById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projekt nicht gefunden: " + id));

        return mapToResponse(project);
    }
}
