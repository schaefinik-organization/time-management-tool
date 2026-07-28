package schaefinik.time.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import schaefinik.time.report.responseData.ReportDTO;
import schaefinik.time.timeentry.service.TimeEntryService;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

  private final TimeEntryService timeEntryService;

  @GetMapping("/summary")
  public List<ReportDTO> getSummary(
    @RequestParam LocalDate start,
    @RequestParam LocalDate end) {
      return timeEntryService.getAggregatedReport(start, end);
    }

}
