package schaefinik.time.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import schaefinik.time.security.principal.TimeUserPrincipal;
import schaefinik.time.security.properties.JwtProperties;
import schaefinik.time.security.service.JwtService;
import schaefinik.time.auth.requestData.AccountRequest;
import schaefinik.time.auth.requestData.LoginRequest;
import schaefinik.time.auth.requestData.UserRequest;
import schaefinik.time.auth.responseData.AuthResponse;
import schaefinik.time.auth.responseData.CurrentUserResponse;
import schaefinik.time.auth.responseData.UserResponse;
import schaefinik.time.user.enums.Role;
import schaefinik.time.user.model.TimeUser;
import schaefinik.time.user.repository.TimeUserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final JwtProperties jwtProperties;
  private final TimeUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password()));

    TimeUser user = userRepository.findByUsername(request.username())
        .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

    TimeUserPrincipal principal = new TimeUserPrincipal(user);
    String token = jwtService.generateToken(principal);

    return new AuthResponse(
        token,
        "Bearer",
        jwtProperties.getExpirationMs(),
        user.getUsername(),
        user.getRole().name());
  }

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

    return new CurrentUserResponse(principal.getId(), principal.getUsername(), role);
  }

  public void deleteUser(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
  }

  public Object findAllUsers() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findAllUsers'");
  }
}
