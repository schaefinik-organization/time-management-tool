package schaefinik.time.auth.controller;

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
import schaefinik.time.auth.requestData.LoginRequest;
import schaefinik.time.auth.responseData.AuthResponse;
import schaefinik.time.auth.responseData.CurrentUserResponse;
import schaefinik.time.auth.service.AuthService;
import schaefinik.time.user.service.UserService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test-Klasse für AuthController
 *
 * Abgedeckte Endpoints:
 * - POST /api/auth/login (login)
 * - GET /api/auth/me (getCurrentUser)
 */
@WebMvcTest(controllers = AuthController.class)
@AutoConfigureDataJpa
@ActiveProfiles("test")
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthService authService;

	@MockitoBean
	private UserService userService;

	private AuthResponse validAuthResponse;
	private CurrentUserResponse currentUserResponse;

	@BeforeEach
	void setUp() {
		validAuthResponse = new AuthResponse(
			"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U",
			"Bearer",
			3600L,
			"john_doe",
			"ADMIN"
		);

		currentUserResponse = new CurrentUserResponse(
			1L,
			"john_doe",
			"john@example.com",
			"ADMIN"
		);
	}

	// ========== POST /api/auth/login ==========

	@Nested
	class LoginTests {

		@Test
		void login_WithValidCredentials_ShouldReturnOkWithToken() throws Exception {
			given(authService.login(any()))
					.willReturn(validAuthResponse);

			mockMvc.perform(post("/api/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"username\":\"john_doe\",\"password\":\"SecurePassword123!\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists())
				.andExpect(jsonPath("$.tokenType", is("Bearer")))
				.andExpect(jsonPath("$.expiresIn", is(3600)))
				.andExpect(jsonPath("$.username", is("john_doe")))
				.andExpect(jsonPath("$.role", is("ADMIN")));

			verify(authService).login(any());
		}

		@Test
		void login_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
			mockMvc.perform(post("/api/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{}"))
				.andExpect(status().isBadRequest());

			verify(authService, never()).login(any());
		}

		@Test
		void login_WithMissingUsername_ShouldReturnBadRequest() throws Exception {
			mockMvc.perform(post("/api/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"password\":\"SecurePassword123!\"}"))
				.andExpect(status().isBadRequest());

			verify(authService, never()).login(any());
		}

		@Test
		void login_WithMissingPassword_ShouldReturnBadRequest() throws Exception {
			mockMvc.perform(post("/api/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"username\":\"john_doe\"}"))
				.andExpect(status().isBadRequest());

			verify(authService, never()).login(any());
		}

		@Test
		void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
			given(authService.login(any()))
					.willThrow(new IllegalArgumentException("Invalid credentials"));

			mockMvc.perform(post("/api/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"username\":\"john_doe\",\"password\":\"WrongPassword\"}"))
				.andExpect(status().isUnauthorized());
		}

		@Test
		void login_WithNonExistentUser_ShouldReturnUnauthorized() throws Exception {
			given(authService.login(any()))
					.willThrow(new IllegalArgumentException("User not found"));

			mockMvc.perform(post("/api/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"username\":\"nonexistent\",\"password\":\"SecurePassword123!\"}"))
				.andExpect(status().isUnauthorized());
		}

		@Test
		void login_WithEmptyUsername_ShouldReturnBadRequest() throws Exception {
			mockMvc.perform(post("/api/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"username\":\"\",\"password\":\"SecurePassword123!\"}"))
				.andExpect(status().isBadRequest());

			verify(authService, never()).login(any());
		}

		@Test
		void login_WithEmptyPassword_ShouldReturnBadRequest() throws Exception {
			mockMvc.perform(post("/api/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"username\":\"john_doe\",\"password\":\"\"}"))
				.andExpect(status().isBadRequest());

			verify(authService, never()).login(any());
		}
	}

	// ========== GET /api/auth/me ==========

	@Nested
	class GetCurrentUserTests {

		@Test
		@WithMockUser(username = "john_doe", roles = "ADMIN")
		void me_WithAuthenticatedUser_ShouldReturnOkWithUserData() throws Exception {
			given(userService.getCurrentUser(any()))
					.willReturn(currentUserResponse);

			mockMvc.perform(get("/api/auth/me"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.username", is("john_doe")))
				.andExpect(jsonPath("$.email", is("john@example.com")))
				.andExpect(jsonPath("$.role", is("ADMIN")));

			verify(userService).getCurrentUser(any());
		}

		@Test
		void me_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
			mockMvc.perform(get("/api/auth/me"))
				.andExpect(status().isUnauthorized());

			verify(userService, never()).getCurrentUser(any());
		}

		@Test
		@WithMockUser(username = "jane_smith", roles = "USER")
		void me_WithDifferentUser_ShouldReturnCorrectUserData() throws Exception {
			CurrentUserResponse janeResponse = new CurrentUserResponse(
				2L,
				"jane_smith",
				"jane@example.com",
				"USER"
			);

			given(userService.getCurrentUser(any()))
					.willReturn(janeResponse);

			mockMvc.perform(get("/api/auth/me"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(2)))
				.andExpect(jsonPath("$.username", is("jane_smith")))
				.andExpect(jsonPath("$.role", is("USER")));

			verify(userService).getCurrentUser(any());
		}
	}

	// ========== Edge Cases ==========

	@Nested
	class EdgeCaseTests {

		@Test
		void login_WithVeryLongUsername_ShouldReturnBadRequest() throws Exception {
			String veryLongUsername = "a".repeat(500);

			mockMvc.perform(post("/api/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"username\":\"" + veryLongUsername + "\",\"password\":\"SecurePassword123!\"}"))
				.andExpect(status().isBadRequest());

			verify(authService, never()).login(any());
		}

		@Test
		void login_WithSpecialCharactersInPassword_ShouldReturnOk() throws Exception {
			given(authService.login(any()))
					.willReturn(validAuthResponse);

			mockMvc.perform(post("/api/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"username\":\"john_doe\",\"password\":\"P@ssw0rd!#$%^&*()\"}"))
				.andExpect(status().isOk());

			verify(authService).login(any());
		}

		@Test
		void login_WithMalformedJson_ShouldReturnBadRequest() throws Exception {
			mockMvc.perform(post("/api/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{invalid json}"))
				.andExpect(status().isBadRequest());

			verify(authService, never()).login(any());
		}

		@Test
		@WithMockUser(username = "admin", roles = "ADMIN")
		void me_WithAdminRole_ShouldReturnOkWithAdminData() throws Exception {
			CurrentUserResponse adminResponse = new CurrentUserResponse(
				1L,
				"admin",
				"admin@example.com",
				"ADMIN"
			);

			given(userService.getCurrentUser(any()))
					.willReturn(adminResponse);

			mockMvc.perform(get("/api/auth/me"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.role", is("ADMIN")));
		}
	}
}