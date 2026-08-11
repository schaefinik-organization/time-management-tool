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
import schaefinik.time.model.TimeUserModel;
import schaefinik.time.repository.TimeUserRepository;
import schaefinik.time.request.user.UserChangeRequest;
import schaefinik.time.request.user.UserCreateRequest;
import schaefinik.time.response.DataMapper;
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
	private static final String USER_CREATE_REQUEST_USERNAME = "createUser";
	private static final String USER_CREATE_REQUEST_EMAIL = "createUser@email.adress";
	private static final String USER_CREATE_REQUEST_PASSWORD = "createUserPassword";
	private final TimeUserDTO userDTO = new TimeUserDTO();
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
	private TimeUserModel user = new TimeUserModel();
	private List<TimeUserModel> allUsers;

	@BeforeEach
	void setUp() {
		user.setId(USER_ID);
		user.setRole(Role.ROLE_USER);
		allUsers = List.of(user);
	}

	@Test
	void findAllUsers_shouldReturnAllUsers() {
		when(userRepository.findAll()).thenReturn(allUsers);
		when(dataMapper.toUserDto(user)).thenReturn(userDTO);

		var result = sut.findAllUsers();

		assertThat(result).isEqualTo(List.of(userDTO));
		verify(userRepository).findAll();
	}

	@Test
	void findAllSubordinatesByCurrentUser_shouldReturnAllSubordinates() {
		try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
			mockedSecurityUtil.when(SecurityUtil::currentUser).thenReturn(principal);
			when(principal.getId()).thenReturn(USER_ID);
			when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
			TimeUserModel subordinate = new TimeUserModel();
			subordinate.setId(SUBORDINATE_USER_ID);
			List<TimeUserModel> allSubordinates = List.of(subordinate);
			user.setSubordinates(allSubordinates);
			when(userRepository.findAllByIdIn(Set.of(SUBORDINATE_USER_ID))).thenReturn(Set.of(subordinate));
			when(dataMapper.toUserDto(subordinate)).thenReturn(userDTO);

			var result = sut.findAllSubordinatesByCurrentUser();

			assertThat(result).isEqualTo(List.of(userDTO));
		}
	}

	@Test
	void getUser_shouldReturnUserID() {
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

		var result = sut.getUser(USER_ID);

		assertThat(result).isEqualTo(user);
		verify(userRepository).findById(USER_ID);
	}

	@Test
	void getCurrentUser_shouldReturnCurrentUser() {
		try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
			mockedSecurityUtil.when(SecurityUtil::currentUser).thenReturn(principal);
			when(principal.getId()).thenReturn(USER_ID);
			when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

			var result = sut.getCurrentUser();

			assertThat(result).isEqualTo(user);
		}
	}

	@Test
	void getCurrentUserData_shouldReturnCurrentUserData() {
		try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
			mockedSecurityUtil.when(SecurityUtil::currentUser).thenReturn(principal);
			when(principal.getId()).thenReturn(USER_ID);
			when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
			when(dataMapper.toUserDto(user)).thenReturn(userDTO);

			var result = sut.getCurrentUserData();

			assertThat(result).isEqualTo(userDTO);
		}
	}

	@Test
	void createEmployee_asAdmin_shouldCreateEmployee() {
		try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
			mockedSecurityUtil.when(SecurityUtil::currentUser).thenReturn(principal);
			when(principal.getId()).thenReturn(ADMIN_ID);
			when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(user));
			user.setId(ADMIN_ID);
			user.setRole(Role.ROLE_ADMIN);
			UserCreateRequest request = UserCreateRequest.builder()
					.username(USER_CREATE_REQUEST_USERNAME)
					.email(USER_CREATE_REQUEST_EMAIL)
					.password(USER_CREATE_REQUEST_PASSWORD)
					.role(null)
					.enabled(true)
					.build();

			sut.createEmployee(request);

			verify(userRepository).save(userCaptor.capture());
			verify(passwordEncoder).encode(USER_CREATE_REQUEST_PASSWORD);
			TimeUserModel savedUser = userCaptor.getValue();
			assertThat(savedUser.getUsername()).isEqualTo(USER_CREATE_REQUEST_USERNAME);
			assertThat(savedUser.getEmail()).isEqualTo(USER_CREATE_REQUEST_EMAIL);
			assertThat(savedUser.getRole()).isEqualTo(Role.ROLE_USER);
			assertThat(savedUser.getManager()).isNull();
		}
	}

	@Test
	void createEmployee_asManager_shouldCreateEmployee() {
		try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
			mockedSecurityUtil.when(SecurityUtil::currentUser).thenReturn(principal);
			when(principal.getId()).thenReturn(MANAGER_ID);
			when(userRepository.findById(MANAGER_ID)).thenReturn(Optional.of(user));
			user.setId(MANAGER_ID);
			user.setRole(Role.ROLE_MANAGER);
			UserCreateRequest request = UserCreateRequest.builder()
					.username(USER_CREATE_REQUEST_USERNAME)
					.email(USER_CREATE_REQUEST_EMAIL)
					.password(USER_CREATE_REQUEST_PASSWORD)
					.role(null)
					.enabled(true)
					.build();

			sut.createEmployee(request);

			verify(userRepository).save(userCaptor.capture());
			verify(passwordEncoder).encode(USER_CREATE_REQUEST_PASSWORD);
			TimeUserModel savedUser = userCaptor.getValue();
			assertThat(savedUser.getUsername()).isEqualTo(USER_CREATE_REQUEST_USERNAME);
			assertThat(savedUser.getEmail()).isEqualTo(USER_CREATE_REQUEST_EMAIL);
			assertThat(savedUser.getRole()).isEqualTo(Role.ROLE_USER);
			assertThat(savedUser.getManager()).isEqualTo(user);
		}
	}

	@Test
	void createEmployee_asUser_shouldThrowAccessDeniedException() {
		try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
			mockedSecurityUtil.when(SecurityUtil::currentUser).thenReturn(principal);
			when(principal.getId()).thenReturn(USER_ID);
			when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
			user.setId(USER_ID);
			user.setRole(Role.ROLE_USER);
			UserCreateRequest request = UserCreateRequest.builder()
					.username(USER_CREATE_REQUEST_USERNAME)
					.email(USER_CREATE_REQUEST_EMAIL)
					.password(USER_CREATE_REQUEST_PASSWORD)
					.role(null)
					.enabled(true)
					.build();

			assertThatThrownBy(() -> sut.createEmployee(request))
					.isInstanceOf(AccessDeniedException.class)
					.hasMessage("Normale User dürfen keine Accounts anlegen.");
		}
	}


	@Test
	void createUser_asAdmin_shouldCreateUser() {
		try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
			mockedSecurityUtil.when(SecurityUtil::currentUser).thenReturn(principal);
			when(principal.getId()).thenReturn(ADMIN_ID);
			when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(user));
			user.setId(ADMIN_ID);
			user.setRole(Role.ROLE_ADMIN);
			UserCreateRequest request = UserCreateRequest.builder()
					.username(USER_CREATE_REQUEST_USERNAME)
					.email(USER_CREATE_REQUEST_EMAIL)
					.password(USER_CREATE_REQUEST_PASSWORD)
					.role(null)
					.enabled(true)
					.build();

			sut.createUser(request);

			verify(userRepository).save(userCaptor.capture());
			verify(passwordEncoder).encode(USER_CREATE_REQUEST_PASSWORD);
			TimeUserModel savedUser = userCaptor.getValue();
			assertThat(savedUser.getUsername()).isEqualTo(USER_CREATE_REQUEST_USERNAME);
			assertThat(savedUser.getEmail()).isEqualTo(USER_CREATE_REQUEST_EMAIL);
			assertThat(savedUser.getRole()).isEqualTo(Role.ROLE_USER);
			assertThat(savedUser.getManager()).isNull();
		}
	}

	@Test
	void createUser_asManager_shouldThrowAccessDeniedException() {
		try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
			mockedSecurityUtil.when(SecurityUtil::currentUser).thenReturn(principal);
			when(principal.getId()).thenReturn(MANAGER_ID);
			when(userRepository.findById(MANAGER_ID)).thenReturn(Optional.of(user));
			user.setId(MANAGER_ID);
			user.setRole(Role.ROLE_MANAGER);
			UserCreateRequest request = UserCreateRequest.builder()
					.username(USER_CREATE_REQUEST_USERNAME)
					.email(USER_CREATE_REQUEST_EMAIL)
					.password(USER_CREATE_REQUEST_PASSWORD)
					.role(null)
					.enabled(true)
					.build();

			assertThatThrownBy(() -> sut.createUser(request))
					.isInstanceOf(AccessDeniedException.class)
					.hasMessage("Nur Admins dürfen neue User anlegen.");
		}
	}

	@Test
	void createUser_asUser_shouldThrowAccessDeniedException() {
		try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
			mockedSecurityUtil.when(SecurityUtil::currentUser).thenReturn(principal);
			when(principal.getId()).thenReturn(USER_ID);
			when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
			user.setId(USER_ID);
			user.setRole(Role.ROLE_USER);
			UserCreateRequest request = UserCreateRequest.builder()
					.username(USER_CREATE_REQUEST_USERNAME)
					.email(USER_CREATE_REQUEST_EMAIL)
					.password(USER_CREATE_REQUEST_PASSWORD)
					.role(null)
					.enabled(true)
					.build();

			assertThatThrownBy(() -> sut.createUser(request))
					.isInstanceOf(AccessDeniedException.class)
					.hasMessage("Nur Admins dürfen neue User anlegen.");
		}
	}

	@Test
	void deleteUser_asAdmin_shouldDeleteUser() {
		try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
			mockedSecurityUtil.when(SecurityUtil::currentUser).thenReturn(principal);
			when(principal.getId()).thenReturn(ADMIN_ID);
			when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(user));
			user.setId(ADMIN_ID);
			user.setRole(Role.ROLE_ADMIN);

			sut.deleteUser(USER_ID);

			verify(userRepository).deleteById(USER_ID);
		}
	}

	@Test
	void deleteUser_asManager_shouldThrowAccessDeniedException() {
		try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
			mockedSecurityUtil.when(SecurityUtil::currentUser).thenReturn(principal);
			when(principal.getId()).thenReturn(MANAGER_ID);
			when(userRepository.findById(MANAGER_ID)).thenReturn(Optional.of(user));
			user.setId(MANAGER_ID);
			user.setRole(Role.ROLE_MANAGER);

			assertThatThrownBy(() -> sut.deleteUser(USER_ID))
					.isInstanceOf(AccessDeniedException.class)
					.hasMessage("Nur Admins dürfen User löschen.");
		}
	}

	@Test
	void deleteUser_asUser_shouldThrowAccessDeniedException() {
		try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
			mockedSecurityUtil.when(SecurityUtil::currentUser).thenReturn(principal);
			when(principal.getId()).thenReturn(USER_ID);
			when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
			user.setId(USER_ID);
			user.setRole(Role.ROLE_USER);

			assertThatThrownBy(() -> sut.deleteUser(USER_ID))
					.isInstanceOf(AccessDeniedException.class)
					.hasMessage("Nur Admins dürfen User löschen.");
		}
	}


	@Test
	void updateUser_asAdmin_emailExists_shouldThrowIllegalArgumentException() {
		try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
			mockedSecurityUtil.when(SecurityUtil::currentUser).thenReturn(principal);
			when(principal.getId()).thenReturn(ADMIN_ID);
			when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(user));
			user.setId(ADMIN_ID);
			user.setRole(Role.ROLE_ADMIN);
			when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
			when(userRepository.existsByEmail(USER_CREATE_REQUEST_EMAIL)).thenReturn(true);

			UserChangeRequest request = UserChangeRequest.builder()
					.username(USER_CREATE_REQUEST_USERNAME)
					.email(USER_CREATE_REQUEST_EMAIL)
					.password(USER_CREATE_REQUEST_PASSWORD)
					.role(Role.ROLE_USER)
					.enabled(true)
					.build();

			assertThatThrownBy(() -> sut.updateUser(USER_ID, request))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("Nur Admins dürfen neue User anlegen.");
		}
	}

	@Test
	void updateUser_asAdmin_usernameExists_shouldThrowIllegalArgumentException() {

	}

	@Test
	void updateUser_asAdmin_shouldUpdateUser() {

	}

	@Test
	void updateUser_asManager_shouldThrowAccessDeniedException() {

	}

	@Test
	void updateUser_asUser_shouldThrowAccessDeniedException() {

	}

	@Test
	void updateCurrentUser_shouldUpdateCurrentUser() {

	}

}
