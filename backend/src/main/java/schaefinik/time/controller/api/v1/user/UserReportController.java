package schaefinik.time.controller.api.v1.user;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import schaefinik.time.response.entry.TimeEntryDTO;
import schaefinik.time.service.TimeEntryService;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user/report")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole( 'ROLE_USER','ROLE_MANAGER', 'ROLE_ADMIN')")
public class UserReportController {

	private final TimeEntryService timeEntryService;

	@GetMapping()
	public ResponseEntity<List<TimeEntryDTO>> getCurrentUserTimeEntriesForMonth(
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
		List<TimeEntryDTO> report = timeEntryService.getCurrentUserTimeEntriesForMonth(month);
		return ResponseEntity.ok(report);
	}

}