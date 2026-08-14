package schaefinik.time.controller.api.v1.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import schaefinik.time.service.ExportService;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/manager/export")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
public class ManagerExportController {

	private final ExportService exportService;

	@GetMapping("/{projectId}/export/excel")
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
	public ResponseEntity<byte[]> exportProjectToExcel(
			@PathVariable Long projectId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {

		byte[] file = exportService.exportToExcelForMonth(month);

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
		byte[] file = exportService.exportProjectIdForMonthToPdf(month);

		String filename = "projekt_" + projectId + "_report.pdf";
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.contentType(MediaType.APPLICATION_PDF)
				.body(file);
	}
}
