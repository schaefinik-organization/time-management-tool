package schaefinik.time.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import schaefinik.time.project.requestData.ProjectRequest;
import schaefinik.time.project.responseData.ProjectResponse;
import schaefinik.time.project.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
	private final ProjectService projectService;

	@GetMapping(produces = "application/json")
	public ResponseEntity<List<ProjectResponse>> findAll() {
		return ResponseEntity.ok(projectService.findAll());
	}

	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Object> findById(@PathVariable Long id) {
		return ResponseEntity.ok(projectService.findById(id));
	}

	@PostMapping(produces = "application/json")
	public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(request));
	}

	@PutMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<ProjectResponse> update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
		return ResponseEntity.ok(projectService.update(id, request));
	}

	@DeleteMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		projectService.delete(id);
		return ResponseEntity.noContent().build();
	}
}