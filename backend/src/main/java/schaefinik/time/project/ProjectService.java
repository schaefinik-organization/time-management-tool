package schaefinik.time.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

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

    public ProjectResponse create(CreateProjectRequest request) {
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

    public ProjectResponse update(Long id, CreateProjectRequest request) {
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
}
