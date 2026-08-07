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
import schaefinik.time.data.TimeEntryData;
import schaefinik.time.request.AssignUsersRequest;
import schaefinik.time.request.ProjectRequest;
import schaefinik.time.response.ProjectResponse;
import schaefinik.time.response.UserProjectHoursDto;
import schaefinik.time.service.ExportService;
import schaefinik.time.service.ProjectService;
import schaefinik.time.service.TimeEntryService;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;
	private final TimeEntryService timeEntryService;
	private final ExportService exportService;

	@GetMapping("/assigned")
	public ResponseEntity<List<ProjectResponse>> getAssignedProjects() {
		List<ProjectResponse> projects = projectService.findAllAssignedToCurrentUser();
		return ResponseEntity.ok(projects);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
		return ResponseEntity.ok(projectService.getProjectData(id));
	}

	@GetMapping("/managed")
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
	public ResponseEntity<List<ProjectResponse>> getManagedProjects() {
		List<ProjectResponse> projects = projectService.findAllManagedByCurrentUser();
		return ResponseEntity.ok(projects);
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
	public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
		ProjectResponse createdProject = projectService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
	public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
		ProjectResponse updatedProject = projectService.update(id, request);
		return ResponseEntity.ok(updatedProject);
	}

	@PatchMapping("/{id}/users")
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
	public ResponseEntity<ProjectResponse> assignUsersToProject(
			@PathVariable Long id,
			@Valid @RequestBody AssignUsersRequest request) {

		ProjectResponse updatedProject = projectService.assignUsers(id, request.getUserIds());
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
	public ResponseEntity<List<TimeEntryData>> getProjectTimeEntries(
			@PathVariable Long projectId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {

		List<TimeEntryData> report = timeEntryService.getReportForProject(projectId, month);
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
		List<TimeEntryData> reportData = timeEntryService.getReportForProject(projectId, month);

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

		List<TimeEntryData> reportData = timeEntryService.getReportForProject(projectId, month);
		byte[] file = exportService.exportToPdf(reportData);

		String filename = "projekt_" + projectId + "_report.pdf";
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.contentType(MediaType.APPLICATION_PDF)
				.body(file);
	}
}