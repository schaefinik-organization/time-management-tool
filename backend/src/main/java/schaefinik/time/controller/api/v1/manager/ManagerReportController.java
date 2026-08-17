package schaefinik.time.controller.api.v1.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import schaefinik.time.response.manager.TeamMemberReportDTO;
import schaefinik.time.service.TimeEntryService;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/manager/report")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
public class ManagerReportController {

	private final TimeEntryService timeEntryService;

	@GetMapping()
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
	public ResponseEntity<List<TeamMemberReportDTO>> getManagerTimeEntriesForMonth(
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
		List<TeamMemberReportDTO> report = timeEntryService.getCurrentManagerTeamReport(month);
		return ResponseEntity.ok(report);
	}
}
