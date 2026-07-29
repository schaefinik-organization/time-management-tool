package schaefinik.time.user.controller;

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
import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.user.requestData.UserRequest;
import schaefinik.time.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test-Klasse für UserController
 * <p>
 * Abgedeckte Endpoints:
 * - PUT /api/user/update-profile (updateProfile)
 * - POST /api/user/change-password (changePassword)
 * <p>
 * Anforderungen: Authentifizierung erforderlich
 */
@WebMvcTest(controllers = UserController.class)
@AutoConfigureDataJpa
@ActiveProfiles("test")
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	// ========== PUT /api/user/update-profile ==========

	@Nested
	class UpdateProfileTests {

		@Test
		@WithMockUser
		void updateProfile_WithValidRequest_ShouldReturnOk() throws Exception {
			doNothing().when(userService).updateUser(any(TimeUserPrincipal.class), any(UserRequest.class));

			mockMvc.perform(put("/api/user/update-profile")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"username\":\"newname\",\"email\":\"new@example.com\"}"))
					.andExpect(status().isOk());

			verify(userService).updateUser(any(TimeUserPrincipal.class), any(UserRequest.class));
		}

		@Test
		@WithMockUser
		void updateProfile_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
			mockMvc.perform(put("/api/user/update-profile")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{}"))
					.andExpect(status().isBadRequest());

			verify(userService, never()).updateUser(any(TimeUserPrincipal.class), any(UserRequest.class));
		}

		@Test
		void updateProfile_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(put("/api/user/update-profile")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"username\":\"newname\",\"email\":\"new@example.com\"}"))
					.andExpect(status().isUnauthorized());

			verify(userService, never()).updateUser(any(TimeUserPrincipal.class), any(UserRequest.class));
		}

		@Test
		@WithMockUser
		void updateProfile_WithExistingUsername_ShouldReturnConflict() throws Exception {
			doThrow(new IllegalArgumentException("Username already exists"))
					.when(userService).updateUser(any(TimeUserPrincipal.class), any(UserRequest.class));

			mockMvc.perform(put("/api/user/update-profile")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"username\":\"existingname\",\"email\":\"new@example.com\"}"))
					.andExpect(status().isBadRequest());
		}
	}

	// ========== POST /api/user/change-password ==========

	@Nested
	class ChangePasswordTests {

		@Test
		@WithMockUser
		void changePassword_WithValidRequest_ShouldReturnOk() throws Exception {
			doNothing().when(userService).changePassword(any(), any());

			mockMvc.perform(post("/api/user/change-password")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"currentPassword\":\"oldpass123\",\"newPassword\":\"newpass123\"}"))
					.andExpect(status().isOk());

			verify(userService).changePassword(any(), any());
		}

		@Test
		@WithMockUser
		void changePassword_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
			mockMvc.perform(post("/api/user/change-password")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{}"))
					.andExpect(status().isBadRequest());

			verify(userService, never()).changePassword(any(), any());
		}

		@Test
		void changePassword_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(post("/api/user/change-password")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"currentPassword\":\"oldpass123\",\"newPassword\":\"newpass123\"}"))
					.andExpect(status().isUnauthorized());

			verify(userService, never()).changePassword(any(), any());
		}

		@Test
		@WithMockUser
		void changePassword_WithWrongCurrentPassword_ShouldReturnUnauthorized() throws Exception {
			doThrow(new IllegalArgumentException("Current password is incorrect"))
					.when(userService).changePassword(any(), any());

			mockMvc.perform(post("/api/user/change-password")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"currentPassword\":\"wrongpass\",\"newPassword\":\"newpass123\"}"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@WithMockUser
		void changePassword_WithWeakNewPassword_ShouldReturnBadRequest() throws Exception {
			mockMvc.perform(post("/api/user/change-password")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"currentPassword\":\"oldpass123\",\"newPassword\":\"123\"}"))
					.andExpect(status().isBadRequest());
		}
	}

	// ========== Edge Cases ==========

	@Nested
	class EdgeCaseTests {

		@Test
		@WithMockUser
		void updateProfile_WithSpecialCharactersInUsername_ShouldReturnOk() throws Exception {
			doNothing().when(userService).updateUser(any(TimeUserPrincipal.class), any(UserRequest.class));

			mockMvc.perform(put("/api/user/update-profile")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"username\":\"user-name_123\",\"email\":\"user+test@example.com\"}"))
					.andExpect(status().isOk());
		}

		@Test
		@WithMockUser
		void changePassword_WithVeryLongPassword_ShouldReturnOk() throws Exception {
			doNothing().when(userService).changePassword(any(), any());

			String longPassword = "P".repeat(255) + "1";
			mockMvc.perform(post("/api/user/change-password")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"currentPassword\":\"oldpass123\",\"newPassword\":\"" + longPassword + "\"}"))
					.andExpect(status().isOk());
		}
	}
}
