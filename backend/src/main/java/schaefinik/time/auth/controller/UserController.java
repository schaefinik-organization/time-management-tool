package schaefinik.time.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import schaefinik.time.auth.requestData.AccountRequest;
import schaefinik.time.auth.service.AuthService;
import schaefinik.time.security.principal.TimeUserPrincipal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final AuthService authService;

  @PostMapping("/change-password")
  public void changePassword(
      @AuthenticationPrincipal TimeUserPrincipal principal,
      @Valid @RequestBody AccountRequest request) {
    authService.changePassword(principal, request);
  }
}
