package schaefinik.time.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import schaefinik.time.admin.responseData.UserResponse;
import schaefinik.time.user.requestData.UserRequest;
import schaefinik.time.user.service.UserService;

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
 * Test-Klasse für AdminUserController
 * <p>
 * Abgedeckte Endpoints (Alle mit @PreAuthorize("hasRole('ADMIN')")):
 * - GET /api/admin/users (getAllUsers)
 * - POST /api/admin/users (createUser)
 * - PUT /api/admin/users/{id} (updateUser)
 * - DELETE /api/admin/users/{id} (deleteUser)
 */
@WebMvcTest(controllers = AdminUserController.class)
@EnableMethodSecurity
@AutoConfigureDataJpa
@ActiveProfiles("test")
public class AdminUserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	private UserResponse user1;
	private UserResponse user2;

	@BeforeEach
	void setUp() {
		user1 = new UserResponse(1L, "john_doe", "john@example.com", "ADMIN", true);
		user2 = new UserResponse(2L, "jane_smith", "jane@example.com", "USER", true);
	}

	// ========== GET /api/admin/users - getAllUsers ==========

	@Nested
	class GetAllUsersTests {

		@Test
		@WithMockUser(roles = "ADMIN")
		void getAllUsers_AsAdmin_ShouldReturnOkWithUsers() throws Exception {
			given(userService.findAllUsers())
					.willReturn(List.of(user1, user2));

			mockMvc.perform(get("/api/admin/users"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(2)))
					.andExpect(jsonPath("$[0].id", is(1)))
					.andExpect(jsonPath("$[0].username", is("john_doe")))
					.andExpect(jsonPath("$[1].id", is(2)))
					.andExpect(jsonPath("$[1].username", is("jane_smith")));

			verify(userService).findAllUsers();
		}

		@Test
		@WithMockUser(roles = "ADMIN")
		void getAllUsers_WithNoUsers_ShouldReturnOkWithEmptyList() throws Exception {
			given(userService.findAllUsers())
					.willReturn(Collections.emptyList());

			mockMvc.perform(get("/api/admin/users"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(0)));

			verify(userService).findAllUsers();
		}

		@Test
		@WithMockUser(roles = "USER")
		void getAllUsers_AsUser_ShouldReturnForbidden() throws Exception {
			mockMvc.perform(get("/api/admin/users"))
					.andExpect(status().isForbidden());

			verify(userService, never()).findAllUsers();
		}

		@Test
		void getAllUsers_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(get("/api/admin/users"))
					.andExpect(status().isUnauthorized());

			verify(userService, never()).findAllUsers();
		}
	}

	// ========== POST /api/admin/users - createUser ==========

	@Nested
	class CreateUserTests {

		@Test
		@WithMockUser(roles = "ADMIN")
		void createUser_AsAdmin_WithValidRequest_ShouldReturnCreatedWithData() throws Exception {
			given(userService.createUser(any()))
					.willReturn(user1);

			mockMvc.perform(post("/api/admin/users")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"username\":\"john_doe\",\"email\":\"john@example.com\",\"password\":\"SecurePassword123!\"}"))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.id", is(1)))
					.andExpect(jsonPath("$.username", is("john_doe")));

			verify(userService).createUser(any());
		}

		@Test
		@WithMockUser(roles = "ADMIN")
		void createUser_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
			mockMvc.perform(post("/api/admin/users")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{}"))
					.andExpect(status().isBadRequest());

			verify(userService, never()).createUser(any());
		}

		@Test
		@WithMockUser(roles = "ADMIN")
		void createUser_WithDuplicateUsername_ShouldReturnConflict() throws Exception {
			given(userService.createUser(any()))
					.willThrow(new IllegalArgumentException("Username already exists"));

			mockMvc.perform(post("/api/admin/users")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"username\":\"john_doe\",\"email\":\"john@example.com\",\"password\":\"SecurePassword123!\"}"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@WithMockUser(roles = "USER")
		void createUser_AsUser_ShouldReturnForbidden() throws Exception {
			mockMvc.perform(post("/api/admin/users")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"username\":\"john_doe\",\"email\":\"john@example.com\",\"password\":\"SecurePassword123!\"}"))
					.andExpect(status().isForbidden());

			verify(userService, never()).createUser(any());
		}

		@Test
		void createUser_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(post("/api/admin/users")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"username\":\"john_doe\",\"email\":\"john@example.com\",\"password\":\"SecurePassword123!\"}"))
					.andExpect(status().isUnauthorized());

			verify(userService, never()).createUser(any());
		}
	}

	// ========== PUT /api/admin/users/{id} - updateUser ==========

	@Nested
	class UpdateUserTests {

		@Test
		@WithMockUser(roles = "ADMIN")
		void updateUser_AsAdmin_WithValidIdAndRequest_ShouldReturnOkWithUpdatedData() throws Exception {
			UserResponse updated = new UserResponse(1L, "john_updated", "john.new@example.com", "ADMIN", true);
			given(userService.updateUser(eq(1L), any(UserRequest.class)))
					.willReturn(updated);

			mockMvc.perform(put("/api/admin/users/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"username\":\"john_updated\",\"email\":\"john.new@example.com\"}"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id", is(1)))
					.andExpect(jsonPath("$.username", is("john_updated")));

			verify(userService).updateUser(eq(1L), any(UserRequest.class));
		}

		@Test
		@WithMockUser(roles = "ADMIN")
		void updateUser_WithNonExistentId_ShouldReturnBadRequest() throws Exception {
			given(userService.updateUser(eq(999L), any(UserRequest.class)))
					.willThrow(new IllegalArgumentException("User not found with id 999"));

			mockMvc.perform(put("/api/admin/users/999")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"username\":\"test\",\"email\":\"test@example.com\"}"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@WithMockUser(roles = "ADMIN")
		void updateUser_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
			mockMvc.perform(put("/api/admin/users/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{}"))
					.andExpect(status().isBadRequest());

			verify(userService, never()).updateUser(any(Long.class), any(UserRequest.class));
		}

		@Test
		@WithMockUser(roles = "USER")
		void updateUser_AsUser_ShouldReturnForbidden() throws Exception {
			mockMvc.perform(put("/api/admin/users/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"username\":\"test\",\"email\":\"test@example.com\"}"))
					.andExpect(status().isForbidden());

			verify(userService, never()).updateUser(any(Long.class), any(UserRequest.class));
		}

		@Test
		void updateUser_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(put("/api/admin/users/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"username\":\"test\",\"email\":\"test@example.com\"}"))
					.andExpect(status().isUnauthorized());

			verify(userService, never()).updateUser(any(Long.class), any(UserRequest.class));
		}
	}

	// ========== DELETE /api/admin/users/{id} - deleteUser ==========

	@Nested
	class DeleteUserTests {

		@Test
		@WithMockUser(roles = "ADMIN")
		void deleteUser_AsAdmin_WithValidId_ShouldReturnNoContent() throws Exception {
			doNothing().when(userService).deleteUser(1L);

			mockMvc.perform(delete("/api/admin/users/1"))
					.andExpect(status().isNoContent());

			verify(userService).deleteUser(eq(1L));
		}

		@Test
		@WithMockUser(roles = "ADMIN")
		void deleteUser_WithNonExistentId_ShouldReturnBadRequest() throws Exception {
			doThrow(new IllegalArgumentException("User not found with id 999"))
					.when(userService).deleteUser(999L);

			mockMvc.perform(delete("/api/admin/users/999"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@WithMockUser(roles = "USER")
		void deleteUser_AsUser_ShouldReturnForbidden() throws Exception {
			mockMvc.perform(delete("/api/admin/users/1"))
					.andExpect(status().isForbidden());

			verify(userService, never()).deleteUser(any());
		}

		@Test
		void deleteUser_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(delete("/api/admin/users/1"))
					.andExpect(status().isUnauthorized());

			verify(userService, never()).deleteUser(any());
		}
	}

	// ========== Edge Cases ==========

	@Nested
	class EdgeCaseTests {

		@Test
		@WithMockUser(roles = "ADMIN")
		void getAllUsers_WithLargeDataSet_ShouldReturnAllUsers() throws Exception {
			List<UserResponse> manyUsers = List.of(
					new UserResponse(1L, "user1", "user1@example.com", "USER", true),
					new UserResponse(2L, "user2", "user2@example.com", "USER", true),
					new UserResponse(3L, "user3", "user3@example.com", "USER", true),
					new UserResponse(4L, "user4", "user4@example.com", "USER", true),
					new UserResponse(5L, "user5", "user5@example.com", "USER", true)
			);

			given(userService.findAllUsers())
					.willReturn(manyUsers);

			mockMvc.perform(get("/api/admin/users"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(5)));
		}
	}
}