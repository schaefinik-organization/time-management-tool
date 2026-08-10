package schaefinik.time.controller.api.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import schaefinik.time.request.project.ProjectChangeRequest;
import schaefinik.time.request.project.ProjectCreateRequest;
import schaefinik.time.response.entry.TimeEntryDTO;
import schaefinik.time.response.project.ProjectDTO;
import schaefinik.time.response.project.UserProjectHoursDto;
import schaefinik.time.service.ExportService;
import schaefinik.time.service.ProjectService;
import schaefinik.time.service.TimeEntryService;
import schaefinik.time.service.UserService;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;
	private final TimeEntryService timeEntryService;
	private final UserService userService;
	private final ExportService exportService;

	@GetMapping("/assigned")
	public ResponseEntity<List<ProjectDTO>> getAssignedProjects() {
		List<ProjectDTO> assigned = projectService.findAllAssignedToCurrentUser();
		return ResponseEntity.ok(assigned);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
		return ResponseEntity.ok(projectService.getProjectData(id));
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

	@GetMapping("/{projectId}/time-entries")
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
	public ResponseEntity<List<TimeEntryDTO>> getProjectTimeEntries(
			@PathVariable Long projectId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {

		List<TimeEntryDTO> report = timeEntryService.getTimeEntryForProject(projectId, month);
		return ResponseEntity.ok(report);
	}

	@GetMapping("/{projectId}/reports/hours-per-user")
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
	public ResponseEntity<List<UserProjectHoursDto>> getProjectHoursReport(
			@PathVariable Long projectId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {

		List<UserProjectHoursDto> report = timeEntryService.getAggregatedReportForProject(projectId, month);
		return ResponseEntity.ok(report);
	}

	@GetMapping("/{projectId}/export/excel")
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
	public ResponseEntity<byte[]> exportProjectToExcel(
			@PathVariable Long projectId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {

		// 1. Hole die Daten (nutzt unsere bestehende Methode aus Epic 3)
		List<TimeEntryDTO> reportData = timeEntryService.getTimeEntryForProject(projectId, month);

		// 2. Erzeuge die Datei
		byte[] file = exportService.exportToExcel(reportData);

		// 3. Setze Dateinamen und HTTP-Header
		String filename = "projekt_" + projectId + "_report.xlsx";
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(file);
	}

	@GetMapping("/{projectId}/export/pdf")
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
	public ResponseEntity<byte[]> exportProjectToPdf(
			@PathVariable Long projectId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
		byte[] file = exportService.exportProjectIdForMonthToPdf(projectId, month);

		String filename = "projekt_" + projectId + "_report.pdf";
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.contentType(MediaType.APPLICATION_PDF)
				.body(file);
	}
}