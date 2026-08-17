package schaefinik.time.controller.api.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import schaefinik.time.request.project.ProjectChangeRequest;
import schaefinik.time.request.project.ProjectCreateRequest;
import schaefinik.time.response.project.ProjectDTO;
import schaefinik.time.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;

	@GetMapping("/assigned")
	public ResponseEntity<List<ProjectDTO>> getAssignedProjects() {
		List<ProjectDTO> assigned = projectService.findAllAssignedToCurrentUser();
		return ResponseEntity.ok(assigned);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
		return ResponseEntity.ok(projectService.getProjectData(id));
	}

	@GetMapping("/all")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public ResponseEntity<List<ProjectDTO>> getAllProjects() {
		List<ProjectDTO> projects = projectService.findAllProjects();
		return ResponseEntity.ok(projects);
	}


	@GetMapping("/managed")
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
	public ResponseEntity<List<ProjectDTO>> getManagedProjects() {
		List<ProjectDTO> projects = projectService.findAllManagedByCurrentUser();
		return ResponseEntity.ok(projects);
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
	public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectCreateRequest request) {
		ProjectDTO createdProject = projectService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
	public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectChangeRequest request) {
		ProjectDTO updatedProject = projectService.update(id, request);
		return ResponseEntity.ok(updatedProject);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
	public ResponseEntity<Void> archiveProject(@PathVariable Long id) {
		projectService.archive(id);
		return ResponseEntity.noContent().build();
	}

}