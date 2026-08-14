package schaefinik.time.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import schaefinik.time.enums.Role;
import schaefinik.time.mapper.DataMapper;
import schaefinik.time.model.TimeUserModel;
import schaefinik.time.repository.TimeUserRepository;
import schaefinik.time.request.user.UserChangeRequest;
import schaefinik.time.request.user.UserCreateRequest;
import schaefinik.time.response.user.TimeUserDTO;
import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.security.util.SecurityUtil;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest implements WithAssertions {

	private static final Long ADMIN_ID = 1L;
	private static final Long MANAGER_ID = 2L;
	private static final Long USER_ID = 3L;
	private static final Long SUBORDINATE_USER_ID = 4L;
	private static final String USERNAME = "createUser";
	private static final String EMAIL = "createUser@email.adress";
	private static final String PASSWORD = "createUserPassword";
	private final TimeUserDTO userDTO = new TimeUserDTO();
	private final TimeUserModel
			currentUser = new TimeUserModel();
	private final TimeUserModel otherUser = new TimeUserModel();

	@InjectMocks
	private UserService sut;
	@Mock
	private TimeUserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private DataMapper dataMapper;
	@Mock
	private TimeUserPrincipal principal;
	@Captor
	private ArgumentCaptor<TimeUserModel> userCaptor;

	private List<TimeUserModel> allUsers;

	@BeforeEach
	void setUp() {
		currentUser.setId(USER_ID);
		currentUser.setRole(Role.ROLE_USER);
		allUsers = List.of(currentUser);
	}

	@Test
	void findAllUsers_shouldReturnAllUsers() {
		when(userRepository.findAll()).thenReturn(allUsers);
		when(dataMapper.toUserDto(currentUser)).thenReturn(userDTO);

		var result = sut.findAllUsers();

		assertThat(result).isEqualTo(List.of(userDTO));
		verify(userRepository).findAll();
	}

	@Test
	void findAllSubordinatesByCurrentUser_shouldReturnAllSubordinates() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(USER_ID, Role.ROLE_USER)) {
			TimeUserModel subordinate = new TimeUserModel();
			subordinate.setId(SUBORDINATE_USER_ID);
			List<TimeUserModel> allSubordinates = List.of(subordinate);
			currentUser.setSubordinates(allSubordinates);
			when(userRepository.findAllByIdIn(Set.of(SUBORDINATE_USER_ID))).thenReturn(Set.of(subordinate));
			when(dataMapper.toUserDto(subordinate)).thenReturn(userDTO);

			var result = sut.findAllSubordinatesByCurrentUser();

			assertThat(result).isEqualTo(List.of(userDTO));
		}
	}

	@Test
	void getUser_shouldReturnUserID() {
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(currentUser));

		var result = sut.getUser(USER_ID);

		assertThat(result).isEqualTo(currentUser);
		verify(userRepository).findById(USER_ID);
	}

	@Test
	void getCurrentUser_shouldReturnCurrentUser() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(USER_ID, Role.ROLE_USER)) {

			var result = sut.getCurrentUser();

			assertThat(result).isEqualTo(currentUser);
		}
	}

	@Test
	void getCurrentUserData_shouldReturnCurrentUserData() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(USER_ID, Role.ROLE_USER)) {
			when(dataMapper.toUserDto(currentUser)).thenReturn(userDTO);

			var result = sut.getCurrentUserData();

			assertThat(result).isEqualTo(userDTO);
		}
	}

	@Test
	void createEmployee_asAdmin_shouldCreateEmployee() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(ADMIN_ID, Role.ROLE_ADMIN)) {
			UserCreateRequest request = buildUserCreateRequest();

			sut.createEmployee(request);

			TimeUserModel savedUser = verifyCreateSavedUser(request);
			assertThat(savedUser.getManager()).isNull();
		}
	}

	@Test
	void createEmployee_asManager_shouldCreateEmployee() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(MANAGER_ID, Role.ROLE_MANAGER)) {
			UserCreateRequest request = buildUserCreateRequest();

			sut.createEmployee(request);

			TimeUserModel savedUser = verifyCreateSavedUser(request);
			assertThat(savedUser.getManager()).isEqualTo(currentUser);
		}
	}

	@Test
	void createEmployee_asUser_shouldThrowAccessDeniedException() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(USER_ID, Role.ROLE_USER)) {
			UserCreateRequest request = buildUserCreateRequest();

			assertThatThrownBy(() -> sut.createEmployee(request))
					.isInstanceOf(AccessDeniedException.class)
					.hasMessage("Normale User dürfen keine Accounts anlegen.");
		}
	}


	@Test
	void createUser_asAdmin_shouldCreateUser() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(ADMIN_ID, Role.ROLE_ADMIN)) {
			UserCreateRequest request = buildUserCreateRequest();

			sut.createUser(request);

			TimeUserModel savedUser = verifyCreateSavedUser(request);
			assertThat(savedUser.getManager()).isNull();
		}
	}

	@Test
	void createUser_asManager_shouldThrowAccessDeniedException() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(MANAGER_ID, Role.ROLE_MANAGER)) {
			UserCreateRequest request = buildUserCreateRequest();

			assertThatThrownBy(() -> sut.createUser(request))
					.isInstanceOf(AccessDeniedException.class)
					.hasMessage("Nur Admins dürfen neue User anlegen.");
		}
	}

	@Test
	void createUser_asUser_shouldThrowAccessDeniedException() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(USER_ID, Role.ROLE_USER)) {
			UserCreateRequest request = buildUserCreateRequest();

			assertThatThrownBy(() -> sut.createUser(request))
					.isInstanceOf(AccessDeniedException.class)
					.hasMessage("Nur Admins dürfen neue User anlegen.");
		}
	}

	@Test
	void deleteUser_asAdmin_shouldDeleteUser() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(ADMIN_ID, Role.ROLE_ADMIN)) {
			sut.deleteUser(USER_ID);

			verify(userRepository).deleteById(USER_ID);
		}
	}

	@Test
	void deleteUser_asManager_shouldThrowAccessDeniedException() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(MANAGER_ID, Role.ROLE_MANAGER)) {

			assertThatThrownBy(() -> sut.deleteUser(USER_ID))
					.isInstanceOf(AccessDeniedException.class)
					.hasMessage("Nur Admins dürfen User löschen.");
		}
	}

	@Test
	void deleteUser_asUser_shouldThrowAccessDeniedException() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(USER_ID, Role.ROLE_USER)) {

			assertThatThrownBy(() -> sut.deleteUser(USER_ID))
					.isInstanceOf(AccessDeniedException.class)
					.hasMessage("Nur Admins dürfen User löschen.");
		}
	}


	@Test
	void updateUser_asAdmin_emailExists_shouldThrowIllegalArgumentException() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(ADMIN_ID, Role.ROLE_ADMIN)) {
			when(userRepository.existsByEmail(EMAIL)).thenReturn(true);
			otherUser.setId(SUBORDINATE_USER_ID);
			otherUser.setRole(Role.ROLE_USER);
			when(userRepository.findById(SUBORDINATE_USER_ID)).thenReturn(Optional.of(otherUser));

			UserChangeRequest request = buildUserChangeRequest();

			assertThatThrownBy(() -> sut.updateUser(SUBORDINATE_USER_ID, request))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("Email already exists!");
		}
	}

	@Test
	void updateUser_asAdmin_usernameExists_shouldThrowIllegalArgumentException() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(ADMIN_ID, Role.ROLE_ADMIN)) {
			when(userRepository.existsByUsername(USERNAME)).thenReturn(true);
			otherUser.setId(SUBORDINATE_USER_ID);
			otherUser.setRole(Role.ROLE_USER);
			when(userRepository.findById(SUBORDINATE_USER_ID)).thenReturn(Optional.of(otherUser));

			UserChangeRequest request = buildUserChangeRequest();

			assertThatThrownBy(() -> sut.updateUser(SUBORDINATE_USER_ID, request))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("Username already exists!");
		}
	}

	@Test
	void updateUser_asAdmin_shouldUpdateUser() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(ADMIN_ID, Role.ROLE_ADMIN)) {
			UserChangeRequest request = buildUserChangeRequest();
			otherUser.setId(SUBORDINATE_USER_ID);
			otherUser.setRole(Role.ROLE_USER);
			when(userRepository.findById(SUBORDINATE_USER_ID)).thenReturn(Optional.of(otherUser));

			sut.updateUser(SUBORDINATE_USER_ID, request);

			verify(userRepository).save(otherUser);

		}
	}

	@Test
	void updateUser_asManager_shouldThrowAccessDeniedException() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(MANAGER_ID, Role.ROLE_MANAGER)) {
			otherUser.setId(SUBORDINATE_USER_ID);
			otherUser.setRole(Role.ROLE_USER);
			when(userRepository.findById(SUBORDINATE_USER_ID)).thenReturn(Optional.of(otherUser));

			UserChangeRequest request = buildUserChangeRequest();

			assertThatThrownBy(() -> sut.updateUser(SUBORDINATE_USER_ID, request))
					.isInstanceOf(AccessDeniedException.class)
					.hasMessage("Nur Admins dürfen andere User bearbeiten.");
		}
	}

	@Test
	void updateUser_asUser_shouldThrowAccessDeniedException() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(USER_ID, Role.ROLE_USER)) {
			otherUser.setId(SUBORDINATE_USER_ID);
			otherUser.setRole(Role.ROLE_USER);
			when(userRepository.findById(SUBORDINATE_USER_ID)).thenReturn(Optional.of(otherUser));

			UserChangeRequest request = buildUserChangeRequest();

			assertThatThrownBy(() -> sut.updateUser(SUBORDINATE_USER_ID, request))
					.isInstanceOf(AccessDeniedException.class)
					.hasMessage("Nur Admins dürfen andere User bearbeiten.");
		}
	}

	@Test
	void updateCurrentUser_shouldUpdateCurrentUser() {
		try (MockedStatic<SecurityUtil> ignored = setupSecurityAndUserContext(USER_ID, Role.ROLE_USER)) {
			UserChangeRequest request = buildUserChangeRequest();

			sut.updateUser(USER_ID, request);

			verify(userRepository).save(userCaptor.capture());
			verify(passwordEncoder).encode(PASSWORD);
			TimeUserModel savedUser = userCaptor.getValue();
			assertThat(savedUser.getUsername()).isEqualTo(request.getUsername());
			assertThat(savedUser.getEmail()).isEqualTo(request.getEmail());
			assertThat(savedUser.getRole()).isEqualTo(request.getRole());
		}
	}

	// --- PRIVATE HELPER METHODS ---

	private TimeUserModel verifyCreateSavedUser(UserCreateRequest request) {
		verify(userRepository).save(userCaptor.capture());
		verify(passwordEncoder).encode(PASSWORD);
		TimeUserModel savedUser = userCaptor.getValue();
		assertThat(savedUser.getUsername()).isEqualTo(request.getUsername());
		assertThat(savedUser.getEmail()).isEqualTo(request.getEmail());
		assertThat(savedUser.getRole()).isEqualTo(Role.ROLE_USER);
		return savedUser;
	}

	private UserCreateRequest buildUserCreateRequest() {
		return UserCreateRequest.builder()
				.username(USERNAME)
				.email(EMAIL)
				.password(PASSWORD)
				.role(null)
				.enabled(true)
				.build();
	}

	private UserChangeRequest buildUserChangeRequest() {
		return UserChangeRequest.builder()
				.username(USERNAME)
				.email(EMAIL)
				.password(PASSWORD)
				.role(Role.ROLE_USER)
				.enabled(true)
				.build();
	}

	private MockedStatic<SecurityUtil> setupSecurityAndUserContext(Long userId, Role role) {
		MockedStatic<SecurityUtil> ignored = mockStatic(SecurityUtil.class);
		ignored.when(SecurityUtil::currentUser).thenReturn(principal);

		when(principal.getId()).thenReturn(userId);

		currentUser.setId(userId);
		currentUser.setRole(role);

		when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));

		return ignored;
	}
}