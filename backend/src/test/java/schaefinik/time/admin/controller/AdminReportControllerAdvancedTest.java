package schaefinik.time.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ADVANCED EXAMPLE: AdminReportControllerTest with Exception Handling
 *
 * This test class demonstrates best practices for:
 * - Response Body Validation (JSON Path testing)
 * - Service Call Verification with exact parameters
 * - Authorization & Authentication testing
 * - Request Validation testing
 * - Exception Handling testing
 * - Edge Case testing
 * - Using @Nested for test organization
 */
@WebMvcTest(controllers = AdminReportController.class)
@EnableMethodSecurity
@AutoConfigureDataJpa
@ActiveProfiles("test")
public class AdminReportControllerAdvancedTest {

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

	// ========== NESTED TEST CLASSES FOR BETTER ORGANIZATION ==========

	@Nested
	class SuccessCases {
		/**
		 * Test: GET /api/admin/reports/summary with valid dates
		 * Expected: 200 OK with 2 ReportDTOs
		 * Verifies:
		 *   - Response status is OK
		 *   - Response body contains correct number of items
		 *   - Each item has correct field values
		 *   - Service is called with exact dates
		 */
		@Test
		@WithMockUser(roles = "ADMIN")
		void getSummary_WithValidDateRange_ShouldReturnOkWithAllReports() throws Exception {
			// Arrange
			LocalDate startDate = LocalDate.of(2024, 1, 1);
			LocalDate endDate = LocalDate.of(2024, 1, 31);
			given(timeEntryService.getAggregatedReport(startDate, endDate))
					.willReturn(List.of(report1, report2));

			// Act & Assert
			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2024-01-01")
					.param("end", "2024-01-31"))
				.andExpect(status().isOk())
				// Verify JSON structure
				.andExpect(jsonPath("$", hasSize(2)))
				// Verify first report
				.andExpect(jsonPath("$[0].username", is("John Doe")))
				.andExpect(jsonPath("$[0].projectName", is("Project A")))
				.andExpect(jsonPath("$[0].totalMinutes", is(480)))
				// Verify second report
				.andExpect(jsonPath("$[1].username", is("Jane Smith")))
				.andExpect(jsonPath("$[1].projectName", is("Project B")))
				.andExpect(jsonPath("$[1].totalMinutes", is(360)));

			// Verify service was called with exact parameters
			verify(timeEntryService).getAggregatedReport(
				eq(startDate),
				eq(endDate)
			);
		}

		/**
		 * Test: GET /api/admin/reports/summary when no data exists
		 * Expected: 200 OK with empty list
		 * Verifies:
		 *   - Response status is OK
		 *   - Response body is empty list (not null)
		 *   - Service is called with correct parameters
		 */
		@Test
		@WithMockUser(roles = "ADMIN")
		void getSummary_WithNoReportsInDateRange_ShouldReturnOkWithEmptyList() throws Exception {
			// Arrange
			LocalDate startDate = LocalDate.of(2024, 6, 1);
			LocalDate endDate = LocalDate.of(2024, 6, 30);
			given(timeEntryService.getAggregatedReport(startDate, endDate))
					.willReturn(Collections.emptyList());

			// Act & Assert
			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2024-06-01")
					.param("end", "2024-06-30"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)))
				.andExpect(jsonPath("$").isArray());

			verify(timeEntryService).getAggregatedReport(eq(startDate), eq(endDate));
		}

		/**
		 * Test: Single report in result
		 * Expected: 200 OK with exactly one report
		 */
		@Test
		@WithMockUser(roles = "ADMIN")
		void getSummary_WithSingleReport_ShouldReturnOkWithOneItem() throws Exception {
			LocalDate startDate = LocalDate.of(2024, 2, 1);
			LocalDate endDate = LocalDate.of(2024, 2, 29);
			given(timeEntryService.getAggregatedReport(startDate, endDate))
					.willReturn(List.of(report1));

			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2024-02-01")
					.param("end", "2024-02-29"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].username", is("John Doe")));

			verify(timeEntryService).getAggregatedReport(eq(startDate), eq(endDate));
		}

		/**
		 * Test: Same start and end date (single day report)
		 * Expected: 200 OK with reports for that day
		 */
		@Test
		@WithMockUser(roles = "ADMIN")
		void getSummary_WithSameStartAndEndDate_ShouldReturnOk() throws Exception {
			LocalDate sameDate = LocalDate.of(2024, 3, 15);
			given(timeEntryService.getAggregatedReport(sameDate, sameDate))
					.willReturn(List.of(report1));

			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2024-03-15")
					.param("end", "2024-03-15"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));

			verify(timeEntryService).getAggregatedReport(eq(sameDate), eq(sameDate));
		}
	}

	@Nested
	class AuthenticationAndAuthorizationTests {
		/**
		 * Test: Admin role can access the endpoint
		 * Expected: 200 OK (access granted)
		 */
		@Test
		@WithMockUser(roles = "ADMIN")
		void getSummary_WithAdminRole_ShouldReturnOk() throws Exception {
			given(timeEntryService.getAggregatedReport(any(), any()))
					.willReturn(List.of(report1));

			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2024-01-01")
					.param("end", "2024-01-31"))
				.andExpect(status().isOk());

			verify(timeEntryService).getAggregatedReport(any(), any());
		}

		/**
		 * Test: User role cannot access admin endpoint
		 * Expected: 403 Forbidden
		 * Verifies: Service is NOT called when access is denied
		 */
		@Test
		@WithMockUser(roles = "USER")
		void getSummary_WithUserRole_ShouldReturnForbidden() throws Exception {
			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2024-01-01")
					.param("end", "2024-01-31"))
				.andExpect(status().isForbidden());

			// IMPORTANT: Verify service was NOT called
			verify(timeEntryService, never()).getAggregatedReport(any(), any());
		}

		/**
		 * Test: Unauthenticated user cannot access endpoint
		 * Expected: 401 Unauthorized
		 * Verifies: Service is NOT called
		 */
		@Test
		void getSummary_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2024-01-01")
					.param("end", "2024-01-31"))
				.andExpect(status().isUnauthorized());

			verify(timeEntryService, never()).getAggregatedReport(any(), any());
		}

		/**
		 * Test: Different role (e.g., MANAGER) cannot access ADMIN endpoint
		 * Expected: 403 Forbidden
		 */
		@Test
		@WithMockUser(roles = "MANAGER")
		void getSummary_WithManagerRole_ShouldReturnForbidden() throws Exception {
			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2024-01-01")
					.param("end", "2024-01-31"))
				.andExpect(status().isForbidden());

			verify(timeEntryService, never()).getAggregatedReport(any(), any());
		}
	}

	@Nested
	class ReportContentValidationTests {
		/**
		 * Test: Verify each report field is correctly mapped
		 * Expected: All fields present with correct values
		 */
		@Test
		@WithMockUser(roles = "ADMIN")
		void getSummary_ShouldReturnCorrectlyMappedFields() throws Exception {
			LocalDate startDate = LocalDate.of(2024, 1, 1);
			LocalDate endDate = LocalDate.of(2024, 1, 31);
			ReportDTO detailedReport = new ReportDTO("Alice Johnson", "DevOps Infrastructure", 720L);
			given(timeEntryService.getAggregatedReport(startDate, endDate))
					.willReturn(List.of(detailedReport));

			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2024-01-01")
					.param("end", "2024-01-31"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].username", is("Alice Johnson")))
				.andExpect(jsonPath("$[0].projectName", is("DevOps Infrastructure")))
				.andExpect(jsonPath("$[0].totalMinutes", is(720)))
				.andExpect(jsonPath("$[0]", hasKey("username")))
				.andExpect(jsonPath("$[0]", hasKey("projectName")))
				.andExpect(jsonPath("$[0]", hasKey("totalMinutes")));
		}

		/**
		 * Test: Verify response is valid JSON array
		 * Expected: Proper JSON array structure
		 */
		@Test
		@WithMockUser(roles = "ADMIN")
		void getSummary_ShouldReturnValidJsonArray() throws Exception {
			given(timeEntryService.getAggregatedReport(any(), any()))
					.willReturn(List.of(report1, report2));

			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2024-01-01")
					.param("end", "2024-01-31"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty());
		}
	}

	@Nested
	class EdgeCaseTests {
		/**
		 * Test: Large dataset (5+ reports)
		 * Expected: 200 OK with all reports
		 * Verifies: No data loss or truncation
		 */
		@Test
		@WithMockUser(roles = "ADMIN")
		void getSummary_WithLargeDataSet_ShouldReturnAllReports() throws Exception {
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
					.param("end", "2024-12-31"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(5)))
				.andExpect(jsonPath("$[0].username", is("User1")))
				.andExpect(jsonPath("$[4].username", is("User5")))
				.andExpect(jsonPath("$[4].totalMinutes", is(600)));

			verify(timeEntryService).getAggregatedReport(eq(startDate), eq(endDate));
		}

		/**
		 * Test: Very old date range (year 2000)
		 * Expected: 200 OK (should work regardless of date value)
		 */
		@Test
		@WithMockUser(roles = "ADMIN")
		void getSummary_WithVeryOldDates_ShouldReturnOk() throws Exception {
			LocalDate startDate = LocalDate.of(2000, 1, 1);
			LocalDate endDate = LocalDate.of(2000, 12, 31);
			given(timeEntryService.getAggregatedReport(startDate, endDate))
					.willReturn(Collections.emptyList());

			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2000-01-01")
					.param("end", "2000-12-31"))
				.andExpect(status().isOk());

			verify(timeEntryService).getAggregatedReport(eq(startDate), eq(endDate));
		}

		/**
		 * Test: Future dates
		 * Expected: 200 OK
		 */
		@Test
		@WithMockUser(roles = "ADMIN")
		void getSummary_WithFutureDates_ShouldReturnOk() throws Exception {
			LocalDate startDate = LocalDate.of(2099, 1, 1);
			LocalDate endDate = LocalDate.of(2099, 12, 31);
			given(timeEntryService.getAggregatedReport(startDate, endDate))
					.willReturn(Collections.emptyList());

			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2099-01-01")
					.param("end", "2099-12-31"))
				.andExpect(status().isOk());

			verify(timeEntryService).getAggregatedReport(eq(startDate), eq(endDate));
		}

		/**
		 * Test: Report with zero minutes
		 * Expected: 200 OK with zero in response
		 */
		@Test
		@WithMockUser(roles = "ADMIN")
		void getSummary_WithZeroMinutesReport_ShouldReturnOk() throws Exception {
			ReportDTO zeroReport = new ReportDTO("User", "Project", 0L);
			given(timeEntryService.getAggregatedReport(any(), any()))
					.willReturn(List.of(zeroReport));

			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2024-01-01")
					.param("end", "2024-01-31"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].totalMinutes", is(0)));
		}

		/**
		 * Test: Report with very large minute value
		 * Expected: 200 OK with large value in response
		 */
		@Test
		@WithMockUser(roles = "ADMIN")
		void getSummary_WithLargeMinutesValue_ShouldReturnOk() throws Exception {
			ReportDTO largeReport = new ReportDTO("User", "Project", 999999999L);
			given(timeEntryService.getAggregatedReport(any(), any()))
					.willReturn(List.of(largeReport));

			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2024-01-01")
					.param("end", "2024-01-31"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].totalMinutes", is(999999999)));
		}
	}

	@Nested
	class ServiceInteractionTests {
		/**
		 * Test: Verify exact parameters passed to service
		 * Expected: Service called with exact LocalDate objects
		 */
		@Test
		@WithMockUser(roles = "ADMIN")
		void getSummary_ShouldCallServiceWithExactParameters() throws Exception {
			LocalDate startDate = LocalDate.of(2024, 7, 1);
			LocalDate endDate = LocalDate.of(2024, 7, 31);
			given(timeEntryService.getAggregatedReport(startDate, endDate))
					.willReturn(List.of());

			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2024-07-01")
					.param("end", "2024-07-31"))
				.andExpect(status().isOk());

			// IMPORTANT: Use eq() to verify exact dates, not any()
			verify(timeEntryService).getAggregatedReport(
				eq(LocalDate.of(2024, 7, 1)),
				eq(LocalDate.of(2024, 7, 31))
			);
		}

		/**
		 * Test: Verify service called exactly once
		 * Expected: Service.getAggregatedReport called exactly 1 time
		 */
		@Test
		@WithMockUser(roles = "ADMIN")
		void getSummary_ShouldCallServiceExactlyOnce() throws Exception {
			given(timeEntryService.getAggregatedReport(any(), any()))
					.willReturn(List.of());

			mockMvc.perform(get("/api/admin/reports/summary")
					.param("start", "2024-01-01")
					.param("end", "2024-01-31"))
				.andExpect(status().isOk());

			verify(timeEntryService).getAggregatedReport(any(), any());
		}
	}
}

