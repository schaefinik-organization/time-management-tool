package schaefinik.time.project.controller;

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
import schaefinik.time.project.responseData.ProjectResponse;
import schaefinik.time.project.service.ProjectService;

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
 * Test-Klasse für ProjectController
 * <p>
 * Abgedeckte Endpoints:
 * - GET /api/projects (findAll)
 * - GET /api/projects/{id} (findById)
 * - POST /api/projects (create)
 * - PUT /api/projects/{id} (update)
 * - DELETE /api/projects/{id} (delete)
 */
@WebMvcTest(controllers = ProjectController.class)
@AutoConfigureDataJpa
@ActiveProfiles("test")
public class ProjectControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ProjectService projectService;

	private ProjectResponse project1;
	private ProjectResponse project2;

	@BeforeEach
	void setUp() {
		project1 = new ProjectResponse(1L, "Project Alpha", "Development");
		project2 = new ProjectResponse(2L, "Project Beta", "Testing");
	}

	// ========== GET /api/projects - findAll ==========

	@Nested
	class FindAllTests {

		@Test
		@WithMockUser
		void findAll_ShouldReturnOkWithProjects() throws Exception {
			given(projectService.findAll())
					.willReturn(List.of(project1, project2));

			mockMvc.perform(get("/api/projects"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(2)))
					.andExpect(jsonPath("$[0].id", is(1)))
					.andExpect(jsonPath("$[0].name", is("Project Alpha")))
					.andExpect(jsonPath("$[1].id", is(2)))
					.andExpect(jsonPath("$[1].name", is("Project Beta")));

			verify(projectService).findAll();
		}

		@Test
		@WithMockUser
		void findAll_WithNoProjects_ShouldReturnOkWithEmptyList() throws Exception {
			given(projectService.findAll())
					.willReturn(Collections.emptyList());

			mockMvc.perform(get("/api/projects"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(0)));

			verify(projectService).findAll();
		}

		@Test
		void findAll_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(get("/api/projects"))
					.andExpect(status().isUnauthorized());

			verify(projectService, never()).findAll();
		}
	}

	// ========== GET /api/projects/{id} - findById ==========

	@Nested
	class FindByIdTests {

		@Test
		@WithMockUser
		void findById_WithValidId_ShouldReturnOkWithProject() throws Exception {
			given(projectService.findById(1L))
					.willReturn(project1);

			mockMvc.perform(get("/api/projects/1"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id", is(1)))
					.andExpect(jsonPath("$.name", is("Project Alpha")));

			verify(projectService).findById(eq(1L));
		}

		@Test
		@WithMockUser
		void findById_WithNonExistentId_ShouldReturnBadRequest() throws Exception {
			given(projectService.findById(999L))
					.willThrow(new IllegalArgumentException("Project not found with id 999"));

			mockMvc.perform(get("/api/projects/999"))
					.andExpect(status().isBadRequest());
		}

		@Test
		void findById_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(get("/api/projects/1"))
					.andExpect(status().isUnauthorized());

			verify(projectService, never()).findById(any());
		}
	}

	// ========== POST /api/projects - create ==========

	@Nested
	class CreateTests {

		@Test
		@WithMockUser
		void create_WithValidRequest_ShouldReturnCreatedWithData() throws Exception {
			given(projectService.create(any()))
					.willReturn(project1);

			mockMvc.perform(post("/api/projects")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"name\":\"Project Alpha\",\"description\":\"Development\"}"))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.id", is(1)))
					.andExpect(jsonPath("$.name", is("Project Alpha")));

			verify(projectService).create(any());
		}

		@Test
		@WithMockUser
		void create_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
			mockMvc.perform(post("/api/projects")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{}"))
					.andExpect(status().isBadRequest());

			verify(projectService, never()).create(any());
		}

		@Test
		void create_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(post("/api/projects")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"name\":\"Project\",\"description\":\"Test\"}"))
					.andExpect(status().isUnauthorized());

			verify(projectService, never()).create(any());
		}
	}

	// ========== PUT /api/projects/{id} - update ==========

	@Nested
	class UpdateTests {

		@Test
		@WithMockUser
		void update_WithValidIdAndRequest_ShouldReturnOkWithUpdatedData() throws Exception {
			ProjectResponse updated = new ProjectResponse(1L, "Project Alpha Updated", "Development");
			given(projectService.update(eq(1L), any()))
					.willReturn(updated);

			mockMvc.perform(put("/api/projects/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"name\":\"Project Alpha Updated\",\"description\":\"Development\"}"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id", is(1)))
					.andExpect(jsonPath("$.name", is("Project Alpha Updated")));

			verify(projectService).update(eq(1L), any());
		}

		@Test
		@WithMockUser
		void update_WithNonExistentId_ShouldReturnBadRequest() throws Exception {
			given(projectService.update(eq(999L), any()))
					.willThrow(new IllegalArgumentException("Project not found with id 999"));

			mockMvc.perform(put("/api/projects/999")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"name\":\"Project\",\"description\":\"Test\"}"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@WithMockUser
		void update_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
			mockMvc.perform(put("/api/projects/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{}"))
					.andExpect(status().isBadRequest());

			verify(projectService, never()).update(any(), any());
		}

		@Test
		void update_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(put("/api/projects/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"name\":\"Project\",\"description\":\"Test\"}"))
					.andExpect(status().isUnauthorized());

			verify(projectService, never()).update(any(), any());
		}
	}

	// ========== DELETE /api/projects/{id} - delete ==========

	@Nested
	class DeleteTests {

		@Test
		@WithMockUser
		void delete_WithValidId_ShouldReturnNoContent() throws Exception {
			doNothing().when(projectService).delete(1L);

			mockMvc.perform(delete("/api/projects/1"))
					.andExpect(status().isNoContent());

			verify(projectService).delete(eq(1L));
		}

		@Test
		@WithMockUser
		void delete_WithNonExistentId_ShouldReturnBadRequest() throws Exception {
			doThrow(new IllegalArgumentException("Project not found with id 999"))
					.when(projectService).delete(999L);

			mockMvc.perform(delete("/api/projects/999"))
					.andExpect(status().isBadRequest());
		}

		@Test
		void delete_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(delete("/api/projects/1"))
					.andExpect(status().isUnauthorized());

			verify(projectService, never()).delete(any());
		}
	}

	// ========== Edge Cases ==========

	@Nested
	class EdgeCaseTests {

		@Test
		@WithMockUser
		void findAll_WithLargeDataSet_ShouldReturnAllProjects() throws Exception {
			List<ProjectResponse> manyProjects = List.of(
					new ProjectResponse(1L, "Project 1", "Desc 1"),
					new ProjectResponse(2L, "Project 2", "Desc 2"),
					new ProjectResponse(3L, "Project 3", "Desc 3"),
					new ProjectResponse(4L, "Project 4", "Desc 4"),
					new ProjectResponse(5L, "Project 5", "Desc 5")
			);

			given(projectService.findAll())
					.willReturn(manyProjects);

			mockMvc.perform(get("/api/projects"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(5)));
		}

		@Test
		@WithMockUser
		void create_WithVeryLongProjectName_ShouldReturnBadRequest() throws Exception {
			ProjectResponse response = new ProjectResponse(1L,
					"A".repeat(255), // Very long name
					"Description");

			given(projectService.create(any()))
					.willReturn(response);

			mockMvc.perform(post("/api/projects")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"name\":\"" + "A".repeat(255) + "\",\"description\":\"Description\"}"))
					.andExpect(status().isBadRequest());
		}
	}
}