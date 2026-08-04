package schaefinik.time.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schaefinik.time.enums.Role;
import schaefinik.time.exception.UserNotFoundException;
import schaefinik.time.model.TimeUserModel;
import schaefinik.time.repository.TimeUserRepository;
import schaefinik.time.requestData.AccountRequest;
import schaefinik.time.requestData.UserRequest;
import schaefinik.time.responseData.CurrentUserResponse;
import schaefinik.time.responseData.UserResponse;
import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.security.util.SecurityUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

	private final TimeUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public TimeUserModel getUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found!"));
	}

	public TimeUserModel getCurrentUser() {
		TimeUserPrincipal principal = SecurityUtil.currentUser();
		return getUser(principal.getId());
	}

	public CurrentUserResponse getCurrentUserData() {
		TimeUserModel user = getCurrentUser();
		return new CurrentUserResponse(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getRole().name());
	}

	public UserResponse createUser(UserRequest request) {
		checkUsername(request.username());
		checkEmail(request.email());

		Role role = request.role() != null ? request.role() : Role.ROLE_USER;

		TimeUserModel user = TimeUserModel.builder()
				.username(request.username())
				.email(request.email())
				.passwordHash(passwordEncoder.encode(request.password()))
				.role(role)
				.enabled(true)
				.build();

		TimeUserModel savedUser = userRepository.save(user);

		return mapToResponse(savedUser);
	}

	public void changePassword(TimeUserPrincipal principal, AccountRequest request) {
		TimeUserModel user = getUser(principal.getId());

		if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
			throw new IllegalArgumentException("Current password is incorrect!");
		}

		user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
		userRepository.save(user);
	}


	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	public List<UserResponse> findAllUsers() {
		return userRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	public UserResponse updateUser(Long id, UserRequest request) {
		TimeUserModel user = getUser(id);

		if (request.username() != null && !request.username().equals(user.getUsername())) {
			checkUsername(request.username());
			user.setUsername(request.username());
		}

		if (request.email() != null && !request.email().equals(user.getEmail())) {
			checkEmail(request.email());
			user.setEmail(request.email());
		}

		if (request.password() != null) {
			user.setPasswordHash(passwordEncoder.encode(request.password()));
		}

		if (request.role() != null) {
			user.setRole(request.role());
		}

		if (request.enabled() != null && request.enabled() != user.isEnabled()) {
			user.setEnabled(request.enabled());
		}

		TimeUserModel updatedUser = userRepository.save(user);

		return mapToResponse(updatedUser);
	}

	public UserResponse updateCurrentUser(UserRequest request) {
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

	private UserResponse mapToResponse(TimeUserModel user) {
		return new UserResponse(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getRole().name(),
				user.isEnabled());
	}

}
