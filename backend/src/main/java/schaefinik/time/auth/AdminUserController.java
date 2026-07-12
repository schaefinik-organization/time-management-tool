package schaefinik.time.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

  private final AuthService authService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
    return authService.createUser(request);
  }
}
