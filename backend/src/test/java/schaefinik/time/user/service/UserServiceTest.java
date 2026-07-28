package schaefinik.time.user.service;

import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import schaefinik.time.auth.responseData.CurrentUserResponse;
import schaefinik.time.auth.service.AuthService;
import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.user.enums.Role;
import schaefinik.time.user.model.TimeUser;
import schaefinik.time.user.repository.TimeUserRepository;
import schaefinik.time.user.requestData.AccountRequest;
import schaefinik.time.user.requestData.UserRequest;
import schaefinik.time.user.responseData.UserResponse;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @InjectMocks
  private UserService sut;

  @Mock
  private TimeUserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  public UserResponse createUser(UserRequest request) {
    if (userRepository.existsByUsername(request.username())) {
      throw new IllegalArgumentException("Username already exists");
    }

    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("Email already exists");
    }

    Role role = request.role() != null ? request.role() : Role.ROLE_USER;

    TimeUser user = TimeUser.builder()
        .username(request.username())
        .email(request.email())
        .passwordHash(passwordEncoder.encode(request.password()))
        .role(role)
        .enabled(true)
        .build();

    TimeUser savedUser = userRepository.save(user);

    return new UserResponse(
        savedUser.getId(),
        savedUser.getUsername(),
        savedUser.getEmail(),
        savedUser.getRole().name(),
        savedUser.isEnabled());
  }

  public void changePassword(TimeUserPrincipal principal, AccountRequest request) {
    TimeUser user = userRepository.findById(principal.getId())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
      throw new IllegalArgumentException("Current password is incorrect");
    }

    user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    userRepository.save(user);
  }

  public CurrentUserResponse getCurrentUser(TimeUserPrincipal principal) {
    String role = principal.getAuthorities().stream()
        .findFirst()
        .map(a -> a.getAuthority())
        .orElse("ROLE_USER");

    return new CurrentUserResponse(principal.getId(), principal.getUsername(), principal.getEmail(), role);
  }

  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  public List<UserResponse> findAllUsers() {
    return userRepository.findAll().stream().map(user -> new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getRole().name(),
        user.isEnabled())).toList();
  }

  public UserResponse updateUser(Long id, UserRequest request) {
    TimeUser user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (request.username() != null && !request.username().equals(user.getUsername())) {
      if (userRepository.existsByUsername(request.username())) {
        throw new IllegalArgumentException("Username already exists");
      }
      user.setUsername(request.username());
    }

    if (request.email() != null && !request.email().equals(user.getEmail())) {
      if (userRepository.existsByEmail(request.email())) {
        throw new IllegalArgumentException("Email already exists");
      }
      user.setEmail(request.email());
    }

    if (request.password() != null) {
      user.setPasswordHash(passwordEncoder.encode(request.password()));
    }

    if (request.role() != null) {
      user.setRole(request.role());
    }

    if (request.enabled() != false) {
      user.setEnabled(request.enabled());
    }

    TimeUser updatedUser = userRepository.save(user);

    return new UserResponse(
        updatedUser.getId(),
        updatedUser.getUsername(),
        updatedUser.getEmail(),
        updatedUser.getRole().name(),
        updatedUser.isEnabled());
  }

  public void updateUser(TimeUserPrincipal principal, UserRequest request) {
    TimeUser user = userRepository.findById(principal.getId())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (request.username() != null) {
      user.setUsername(request.username());
    }

    if (request.email() != null && !request.email().equals(user.getEmail())) {
      if (userRepository.existsByEmail(request.email())) {
        throw new IllegalArgumentException("Email already exists");
      }
      user.setEmail(request.email());
    }

    userRepository.save(user);
  }

}
