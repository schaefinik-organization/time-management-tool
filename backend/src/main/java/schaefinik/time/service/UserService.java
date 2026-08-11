package schaefinik.time.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schaefinik.time.enums.Role;
import schaefinik.time.exception.ResourceNotFoundException;
import schaefinik.time.model.TimeUserModel;
import schaefinik.time.repository.TimeUserRepository;
import schaefinik.time.request.AccountRequest;
import schaefinik.time.request.user.UserChangeRequest;
import schaefinik.time.request.user.UserCreateRequest;
import schaefinik.time.response.DataMapper;
import schaefinik.time.response.user.TimeUserDTO;
import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.security.util.SecurityUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

	private final TimeUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final DataMapper dataMapper;

	public List<TimeUserDTO> findAllUsers() {
		return userRepository.findAll()
				.stream()
				.map(this::mapToDTO)
				.toList();
	}

	@Transactional
	public List<TimeUserDTO> findAllSubordinatesByCurrentUser() {
		TimeUserModel currentUser = getCurrentUser();
		return findAllByIds(
				currentUser.getSubordinates()
						.stream()
						.map(TimeUserModel::getId)
						.collect(Collectors.toSet())
		)
				.stream()
				.map(this::mapToDTO)
				.toList();
	}

	public TimeUserModel getUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(
						() ->
								new ResourceNotFoundException(
										"User not found!"
								)
				);
	}

	public TimeUserModel getCurrentUser() {
		TimeUserPrincipal principal = SecurityUtil.currentUser();
		return getUser(principal.getId());
	}

	public TimeUserDTO getCurrentUserData() {
		TimeUserModel user = getCurrentUser();
		return mapToDTO(user);
	}

	public Set<TimeUserModel> findAllByIds(Set<Long> userIds) {
		return userRepository.findAllByIdIn(userIds);
	}

	@Transactional
	public TimeUserDTO createEmployee(UserCreateRequest request) {
		TimeUserModel currentUser = getCurrentUser();
		if (currentUser.getRole() == Role.ROLE_USER) {
			throw new AccessDeniedException("Normale User dürfen keine Accounts anlegen.");
		}
		checkUsername(request.getUsername());
		checkEmail(request.getEmail());
		TimeUserModel newUser = TimeUserModel.builder()
				.username(request.getUsername())
				.email(request.getEmail())
				.passwordHash(passwordEncoder.encode(request.getPassword()))
				.role(Role.ROLE_USER)
				.build();

		if (currentUser.getRole() == Role.ROLE_MANAGER) {
			newUser.setManager(currentUser);
		}
		//send email
		return mapToDTO(userRepository.save(newUser));
	}

	//TODO: implement Feature in future releases
	@Transactional
	public boolean assignUserToManager(Long userId, Long managerId) {
		TimeUserModel currentUser = getCurrentUser();

		if (currentUser.getRole() != Role.ROLE_ADMIN) {
			throw new AccessDeniedException("Nur Administratoren dürfen Zuweisungen ändern.");
		}

		TimeUserModel employee = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User nicht gefunden"));

		TimeUserModel newManager = userRepository.findById(managerId)
				.orElseThrow(() -> new ResourceNotFoundException("Manager nicht gefunden"));

		if (newManager.getRole() == Role.ROLE_USER) {
			throw new IllegalArgumentException("Ein User kann nicht Manager eines anderen Users sein.");
		}

		employee.setManager(newManager);
		userRepository.save(employee);
		return true;
	}

	public boolean createUser(UserCreateRequest request) {
		TimeUserModel currentUser = getCurrentUser();
		if (currentUser.getRole() != Role.ROLE_ADMIN) {
			throw new AccessDeniedException("Nur Admins dürfen neue User anlegen.");
		}
		checkUsername(request.getUsername());
		checkEmail(request.getEmail());
		TimeUserModel user = TimeUserModel.builder()
				.username(request.getUsername())
				.email(request.getEmail())
				.passwordHash(passwordEncoder.encode(request.getPassword()))
				.role(Role.ROLE_USER)
				.enabled(true)
				.build();

		userRepository.save(user);

		//send email
		return true;
	}

	//TODO implement and test feature in frontend
	public void changePassword(TimeUserPrincipal principal, AccountRequest request) {
		TimeUserModel user = getUser(principal.getId());

		if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
			throw new IllegalArgumentException("Current password is incorrect!");
		}

		user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
		userRepository.save(user);
	}

	public void deleteUser(Long id) {
		TimeUserModel currentUser = getCurrentUser();
		if (currentUser.getRole() != Role.ROLE_ADMIN) {
			throw new AccessDeniedException("Nur Admins dürfen User löschen.");
		}
		userRepository.deleteById(id);
	}

	public boolean updateUser(Long id, UserChangeRequest request) {
		TimeUserModel currentUser = getCurrentUser();
		TimeUserModel user = getUser(id);
		if (currentUser.getRole() != Role.ROLE_ADMIN && currentUser != user) {
			throw new AccessDeniedException("Nur Admins dürfen andere User bearbeiten.");
		}

		if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
			checkUsername(request.getUsername());
			user.setUsername(request.getUsername());
		}

		if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
			checkEmail(request.getEmail());
			user.setEmail(request.getEmail());
		}

		if (request.getPassword() != null) {
			user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		}

		if (request.getRole() != null) {
			user.setRole(request.getRole());
		}

		if (request.getEnabled() != null && request.getEnabled() != user.isEnabled()) {
			user.setEnabled(request.getEnabled());
		}

		userRepository.save(user);
		return true;
	}

	public boolean updateCurrentUser(UserChangeRequest request) {
		TimeUserModel user = getCurrentUser();
		return updateUser(user.getId(), request);

	}

	private void checkEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new IllegalArgumentException("Email already exists!");
		}
	}

	private void checkUsername(String username) {
		if (userRepository.existsByUsername(username)) {
			throw new IllegalArgumentException("Username already exists!");
		}
	}

	private TimeUserDTO mapToDTO(TimeUserModel user) {
		return dataMapper.toUserDto(user);
	}
}
