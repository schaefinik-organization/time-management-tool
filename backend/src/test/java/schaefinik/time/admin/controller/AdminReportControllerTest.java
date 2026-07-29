package schaefinik.time.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import schaefinik.time.report.responseData.ReportDTO;
import schaefinik.time.timeentry.service.TimeEntryService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminReportController.class)
@EnableMethodSecurity
@AutoConfigureDataJpa
@ActiveProfiles("test")
public class AdminReportControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TimeEntryService timeEntryService;

	private ReportDTO report1;
	private ReportDTO report2;

	@BeforeEach
	void setUp() {
		report1 = new ReportDTO("John Doe", "Project A", 480L);
		report2 = new ReportDTO("Jane Smith", "Project B", 360L);
	}

	// ========== SUCCESS CASES ==========

	@Test
	@WithMockUser(roles = "ADMIN")
	void getSummary_AsAdmin_WithValidDates_ShouldReturnOkWithData() throws Exception {
		LocalDate startDate = LocalDate.of(2024, 1, 1);
		LocalDate endDate = LocalDate.of(2024, 1, 31);
		given(timeEntryService.getAggregatedReport(startDate, endDate))
				.willReturn(List.of(report1, report2));

		mockMvc.perform(get("/api/admin/reports/summary")
						.param("start", "2024-01-01")
						.param("end", "2024-01-31")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].username", is("John Doe")))
				.andExpect(jsonPath("$[0].projectName", is("Project A")))
				.andExpect(jsonPath("$[0].totalMinutes", is(480)))
				.andExpect(jsonPath("$[1].username", is("Jane Smith")))
				.andExpect(jsonPath("$[1].projectName", is("Project B")))
				.andExpect(jsonPath("$[1].totalMinutes", is(360)));

		verify(timeEntryService).getAggregatedReport(eq(startDate), eq(endDate));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void getSummary_AsAdmin_WithEmptyResult_ShouldReturnOkWithEmptyList() throws Exception {
		LocalDate startDate = LocalDate.of(2024, 1, 1);
		LocalDate endDate = LocalDate.of(2024, 1, 31);
		given(timeEntryService.getAggregatedReport(startDate, endDate))
				.willReturn(Collections.emptyList());

		mockMvc.perform(get("/api/admin/reports/summary")
						.param("start", "2024-01-01")
						.param("end", "2024-01-31")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));

		verify(timeEntryService).getAggregatedReport(eq(startDate), eq(endDate));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void getSummary_AsAdmin_WithSingleReport_ShouldReturnOkWithOneItem() throws Exception {
		LocalDate startDate = LocalDate.of(2024, 2, 1);
		LocalDate endDate = LocalDate.of(2024, 2, 29);
		given(timeEntryService.getAggregatedReport(startDate, endDate))
				.willReturn(List.of(report1));

		mockMvc.perform(get("/api/admin/reports/summary")
						.param("start", "2024-02-01")
						.param("end", "2024-02-29")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].username", is("John Doe")))
				.andExpect(jsonPath("$[0].totalMinutes", is(480)));

		verify(timeEntryService).getAggregatedReport(eq(startDate), eq(endDate));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void getSummary_AsAdmin_WithSameDateRange_ShouldReturnOk() throws Exception {
		LocalDate sameDate = LocalDate.of(2024, 3, 15);
		given(timeEntryService.getAggregatedReport(sameDate, sameDate))
				.willReturn(List.of(report1));

		mockMvc.perform(get("/api/admin/reports/summary")
						.param("start", "2024-03-15")
						.param("end", "2024-03-15")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));

		verify(timeEntryService).getAggregatedReport(eq(sameDate), eq(sameDate));
	}

	// ========== AUTHORIZATION & AUTHENTICATION TESTS ==========

	@Test
	@WithMockUser(roles = "USER")
	void getSummary_AsUserRole_ShouldReturnForbidden() throws Exception {
		mockMvc.perform(get("/api/admin/reports/summary")
						.param("start", "2024-01-01")
						.param("end", "2024-01-31"))
				.andExpect(status().isForbidden());

		verify(timeEntryService, never()).getAggregatedReport(any(), any());
	}

	@Test
	void getSummary_AsUnauthenticated_ShouldReturnUnauthorized() throws Exception {
		mockMvc.perform(get("/api/admin/reports/summary")
						.param("start", "2024-01-01")
						.param("end", "2024-01-31"))
				.andExpect(status().isUnauthorized());

		verify(timeEntryService, never()).getAggregatedReport(any(), any());
	}

	// ========== BOUNDARY/EDGE CASE TESTS ==========

	@Test
	@WithMockUser(roles = "ADMIN")
	void getSummary_WithFirstAndLastDayOfMonth_ShouldReturnOk() throws Exception {
		LocalDate startDate = LocalDate.of(2024, 3, 1);
		LocalDate endDate = LocalDate.of(2024, 3, 31);
		given(timeEntryService.getAggregatedReport(startDate, endDate))
				.willReturn(List.of(report1));

		mockMvc.perform(get("/api/admin/reports/summary")
						.param("start", "2024-03-01")
						.param("end", "2024-03-31"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));

		verify(timeEntryService).getAggregatedReport(eq(startDate), eq(endDate));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void getSummary_WithLeapYearDate_ShouldReturnOk() throws Exception {
		LocalDate startDate = LocalDate.of(2024, 2, 29);
		LocalDate endDate = LocalDate.of(2024, 2, 29);
		given(timeEntryService.getAggregatedReport(startDate, endDate))
				.willReturn(Collections.emptyList());

		mockMvc.perform(get("/api/admin/reports/summary")
						.param("start", "2024-02-29")
						.param("end", "2024-02-29"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));

		verify(timeEntryService).getAggregatedReport(eq(startDate), eq(endDate));
	}

	// ========== LARGE DATA SET TEST ==========

	@Test
	@WithMockUser(roles = "ADMIN")
	void getSummary_WithLargeDataSet_ShouldReturnOkWithAllData() throws Exception {
		List<ReportDTO> largeDataSet = List.of(
				new ReportDTO("User1", "ProjectA", 480L),
				new ReportDTO("User2", "ProjectB", 360L),
				new ReportDTO("User3", "ProjectC", 540L),
				new ReportDTO("User4", "ProjectD", 420L),
				new ReportDTO("User5", "ProjectE", 600L)
		);
		LocalDate startDate = LocalDate.of(2024, 1, 1);
		LocalDate endDate = LocalDate.of(2024, 12, 31);
		given(timeEntryService.getAggregatedReport(startDate, endDate))
				.willReturn(largeDataSet);

		mockMvc.perform(get("/api/admin/reports/summary")
						.param("start", "2024-01-01")
						.param("end", "2024-12-31")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(5)))
				.andExpect(jsonPath("$[4].username", is("User5")))
				.andExpect(jsonPath("$[4].totalMinutes", is(600)));

		verify(timeEntryService).getAggregatedReport(eq(startDate), eq(endDate));
	}
}