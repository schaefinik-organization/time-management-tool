package schaefinik.time.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import schaefinik.time.project.requestData.ProjectRequest;
import schaefinik.time.project.responseData.ProjectResponse;
import schaefinik.time.project.service.ProjectService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// --- ProjectController.java ---
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> findAll() {
        return ResponseEntity.ok(projectService.findAll());
    }

    @GetMapping("/{id}") // NEU: Detailansicht
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(request));
    }
    // ... update/delete bleiben wie gehabt, aber in ResponseEntity gewrappt
}