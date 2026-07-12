package schaefinik.time.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import schaefinik.time.security.TimeUserPrincipal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final AuthService authService;

  @PostMapping("/change-password")
  public void changePassword(
      @AuthenticationPrincipal TimeUserPrincipal principal,
      @Valid @RequestBody ChangePasswordRequest request) {
    authService.changePassword(principal, request);
  }
}
