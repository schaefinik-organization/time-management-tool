package schaefinik.time.timeentry.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import schaefinik.time.timeentry.requestData.TimeEntryRequest;
import schaefinik.time.timeentry.responseData.TimeEntryResponse;
import schaefinik.time.timeentry.service.TimeEntryService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test-Klasse für TimeEntryController
 * <p>
 * Abgedeckte Szenarien:
 * - GET /api/time-entries (findByDate)
 * - POST /api/time-entries (create)
 * - PUT /api/time-entries/{id} (update)
 * - DELETE /api/time-entries/{id} (delete)
 * <p>
 * Test-Kategorien:
 * - Success Cases (happy path)
 * - Authorization & Authentication
 * - Validation
 * - Exception Handling
 * - Edge Cases
 */
@WebMvcTest(controllers = TimeEntryController.class)
@AutoConfigureDataJpa
@ActiveProfiles("test")
public class TimeEntryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TimeEntryService timeEntryService;

	private TimeEntryResponse timeEntry1;
	private TimeEntryResponse timeEntry2;

	@BeforeEach
	void setUp() {
		timeEntry1 = new TimeEntryResponse(
				1L,
				1L,
				"Project A",
				LocalDate.of(2024, 1, 15),
				LocalTime.of(9, 0),
				LocalTime.of(10, 30),
				"Morning meeting"
		);

		timeEntry2 = new TimeEntryResponse(
				2L,
				2L,
				"Project B",
				LocalDate.of(2024, 1, 15),
				LocalTime.of(14, 0),
				LocalTime.of(16, 0),
				"Afternoon work"
		);

	}

	// ========== GET /api/time-entries - findByDate ==========

	@Nested
	class FindByDateTests {

		@Test
		@WithMockUser
		void findByDate_WithValidDate_ShouldReturnOkWithEntries() throws Exception {
			LocalDate testDate = LocalDate.of(2024, 1, 15);
			given(timeEntryService.findByDate(testDate))
					.willReturn(List.of(timeEntry1, timeEntry2));

			mockMvc.perform(get("/api/time-entries")
							.param("date", "2024-01-15"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(2)))
					.andExpect(jsonPath("$[0].id", is(1)))
					.andExpect(jsonPath("$[0].projectName", is("Project A")))
					.andExpect(jsonPath("$[0].startTime", is("09:00:00")))
					.andExpect(jsonPath("$[1].id", is(2)))
					.andExpect(jsonPath("$[1].projectName", is("Project B")));

			verify(timeEntryService).findByDate(eq(testDate));
		}

		@Test
		@WithMockUser
		void findByDate_WithNoEntries_ShouldReturnOkWithEmptyList() throws Exception {
			LocalDate testDate = LocalDate.of(2024, 1, 20);
			given(timeEntryService.findByDate(testDate))
					.willReturn(Collections.emptyList());

			mockMvc.perform(get("/api/time-entries")
							.param("date", "2024-01-20"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(0)));

			verify(timeEntryService).findByDate(eq(testDate));
		}

		@Test
		@WithMockUser
		void findByDate_WithSingleEntry_ShouldReturnOkWithOneItem() throws Exception {
			LocalDate testDate = LocalDate.of(2024, 1, 15);
			given(timeEntryService.findByDate(testDate))
					.willReturn(List.of(timeEntry1));

			mockMvc.perform(get("/api/time-entries")
							.param("date", "2024-01-15"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(1)))
					.andExpect(jsonPath("$[0].id", is(1)));

			verify(timeEntryService).findByDate(eq(testDate));
		}

		@Test
		void findByDate_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(get("/api/time-entries")
							.param("date", "2024-01-15"))
					.andExpect(status().isUnauthorized());

			verify(timeEntryService, never()).findByDate(any());
		}
	}

	// ========== POST /api/time-entries - create ==========

	@Nested
	class CreateTests {

		@Test
		@WithMockUser
		void create_WithValidRequest_ShouldReturnCreatedWithData() throws Exception {
			given(timeEntryService.create(any(TimeEntryRequest.class)))
					.willReturn(timeEntry1);

			mockMvc.perform(post("/api/time-entries")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"projectId\":1,\"entryDate\":\"2024-01-15\",\"startTime\":\"09:00:00\",\"endTime\":\"10:30:00\",\"note\":\"Morning meeting\"}"))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.id", is(1)))
					.andExpect(jsonPath("$.projectName", is("Project A")))
					.andExpect(jsonPath("$.note", is("Morning meeting")));

			verify(timeEntryService).create(any(TimeEntryRequest.class));
		}

		@Test
		@WithMockUser
		void create_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
			mockMvc.perform(post("/api/time-entries")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{}"))
					.andExpect(status().isBadRequest());

			verify(timeEntryService, never()).create(any());
		}

		@Test
		void create_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(post("/api/time-entries")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"projectId\":1,\"entryDate\":\"2024-01-15\",\"startTime\":\"09:00:00\",\"endTime\":\"10:30:00\",\"note\":\"Test\"}"))
					.andExpect(status().isUnauthorized());

			verify(timeEntryService, never()).create(any());
		}

		@Test
		@WithMockUser
		void create_WithOverlappingTimes_ShouldReturnBadRequest() throws Exception {
			given(timeEntryService.create(any()))
					.willThrow(new IllegalArgumentException("Times overlap"));

			mockMvc.perform(post("/api/time-entries")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"projectId\":1,\"entryDate\":\"2024-01-15\",\"startTime\":\"09:00:00\",\"endTime\":\"10:30:00\",\"note\":\"Test\"}"))
					.andExpect(status().isBadRequest());
		}
	}

	// ========== PUT /api/time-entries/{id} - update ==========

	@Nested
	class UpdateTests {

		@Test
		@WithMockUser
		void update_WithValidIdAndRequest_ShouldReturnOkWithUpdatedData() throws Exception {
			Long entryId = 1L;
			TimeEntryResponse updated = new TimeEntryResponse(
					1L, 1L, "Project A", LocalDate.of(2024, 1, 15),
					LocalTime.of(10, 0), LocalTime.of(11, 30), "Updated note"
			);
			given(timeEntryService.update(eq(entryId), any()))
					.willReturn(updated);

			mockMvc.perform(put("/api/time-entries/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"projectId\":1,\"entryDate\":\"2024-01-15\",\"startTime\":\"10:00:00\",\"endTime\":\"11:30:00\",\"note\":\"Updated note\"}"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id", is(1)))
					.andExpect(jsonPath("$.note", is("Updated note")));

			verify(timeEntryService).update(eq(entryId), any());
		}

		@Test
		@WithMockUser
		void update_WithNonExistentId_ShouldReturnBadRequest() throws Exception {
			given(timeEntryService.update(eq(999L), any()))
					.willThrow(new IllegalArgumentException("TimeEntry not found with id 999"));

			mockMvc.perform(put("/api/time-entries/999")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"projectId\":1,\"entryDate\":\"2024-01-15\",\"startTime\":\"09:00:00\",\"endTime\":\"10:30:00\",\"note\":\"Test\"}"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@WithMockUser
		void update_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
			mockMvc.perform(put("/api/time-entries/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{}"))
					.andExpect(status().isBadRequest());

			verify(timeEntryService, never()).update(any(), any());
		}

		@Test
		void update_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(put("/api/time-entries/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"projectId\":1,\"entryDate\":\"2024-01-15\",\"startTime\":\"09:00:00\",\"endTime\":\"10:30:00\",\"note\":\"Test\"}"))
					.andExpect(status().isUnauthorized());

			verify(timeEntryService, never()).update(any(), any());
		}
	}

	// ========== DELETE /api/time-entries/{id} - delete ==========

	@Nested
	class DeleteTests {

		@Test
		@WithMockUser
		void delete_WithValidId_ShouldReturnNoContent() throws Exception {
			doNothing().when(timeEntryService).delete(1L);

			mockMvc.perform(delete("/api/time-entries/1"))
					.andExpect(status().isNoContent());

			verify(timeEntryService).delete(eq(1L));
		}

		@Test
		@WithMockUser
		void delete_WithNonExistentId_ShouldReturnBadRequest() throws Exception {
			doThrow(new IllegalArgumentException("TimeEntry not found with id 999"))
					.when(timeEntryService).delete(999L);

			mockMvc.perform(delete("/api/time-entries/999"))
					.andExpect(status().isBadRequest());
		}

		@Test
		void delete_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(delete("/api/time-entries/1"))
					.andExpect(status().isUnauthorized());

			verify(timeEntryService, never()).delete(any());
		}
	}

	// ========== Edge Cases ==========

	@Nested
	class EdgeCaseTests {

		@Test
		@WithMockUser
		void findByDate_WithManyEntries_ShouldReturnAllEntries() throws Exception {
			List<TimeEntryResponse> manyEntries = List.of(
					timeEntry1, timeEntry2,
					new TimeEntryResponse(3L, 1L, "Project A", LocalDate.of(2024, 1, 15), LocalTime.of(11, 0), LocalTime.of(12, 0), ""),
					new TimeEntryResponse(4L, 2L, "Project B", LocalDate.of(2024, 1, 15), LocalTime.of(13, 0), LocalTime.of(14, 0), ""),
					new TimeEntryResponse(5L, 1L, "Project A", LocalDate.of(2024, 1, 15), LocalTime.of(15, 0), LocalTime.of(16, 0), "")
			);

			given(timeEntryService.findByDate(any()))
					.willReturn(manyEntries);

			mockMvc.perform(get("/api/time-entries")
							.param("date", "2024-01-15"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(5)));
		}

		@Test
		@WithMockUser
		void create_WithEmptyNote_ShouldReturnCreated() throws Exception {
			TimeEntryResponse response = new TimeEntryResponse(
					1L, 1L, "Project A", LocalDate.of(2024, 1, 15),
					LocalTime.of(9, 0), LocalTime.of(10, 30), ""
			);
			given(timeEntryService.create(any()))
					.willReturn(response);

			mockMvc.perform(post("/api/time-entries")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"projectId\":1,\"entryDate\":\"2024-01-15\",\"startTime\":\"09:00:00\",\"endTime\":\"10:30:00\",\"note\":\"\"}"))
					.andExpect(status().isCreated());
		}

		@Test
		@WithMockUser
		void create_WithMinimumDuration_ShouldReturnCreated() throws Exception {
			TimeEntryResponse response = new TimeEntryResponse(
					1L, 1L, "Project A", LocalDate.of(2024, 1, 15),
					LocalTime.of(9, 0), LocalTime.of(9, 1), "1 minute entry"
			);
			given(timeEntryService.create(any()))
					.willReturn(response);

			mockMvc.perform(post("/api/time-entries")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"projectId\":1,\"entryDate\":\"2024-01-15\",\"startTime\":\"09:00:00\",\"endTime\":\"09:01:00\",\"note\":\"1 minute entry\"}"))
					.andExpect(status().isCreated());
		}

		@Test
		@WithMockUser
		void findByDate_WithLeapYearDate_ShouldReturnOk() throws Exception {
			LocalDate leapYearDate = LocalDate.of(2024, 2, 29);
			given(timeEntryService.findByDate(leapYearDate))
					.willReturn(Collections.emptyList());

			mockMvc.perform(get("/api/time-entries")
							.param("date", "2024-02-29"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(0)));
		}
	}
}
